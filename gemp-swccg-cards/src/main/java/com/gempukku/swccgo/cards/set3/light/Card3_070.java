package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractCreatureVehicle;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.ImmuneToUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextAbilityModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAwayAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Vehicle
 * Subtype: Creature
 * Title: Tauntaun
 */
public class Card3_070 extends AbstractCreatureVehicle {
    public Card3_070() {
        super(Side.LIGHT, 4, 1, 1, null, 2, 2, 3, "Tauntaun", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C);
        setLore("First native creature found on Hoth. Roam the icy plans in herds. Ill-tempered and easily spooked. Smell bad on the outside. Trained as steeds for Rebel patrols.");
        setGameText("May add 1 'rider' (passenger). Deploy only on Hoth. Ability = 1/4. May move as a 'react' from a battle. May be 'sacrificed' (lost) to make rider immune to Exposure this turn.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.TAUNTAUN);
        setPassengerCapacity(1);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Hoth;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextAbilityModifier(self, 0.25));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAwayAsReactModifier(self, new InBattleCondition(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        PhysicalCard rider = Filters.findFirstActive(game, self, Filters.and(Filters.character, Filters.aboard(self)));
        if (rider != null) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Sacrifice");
            action.setActionMsg("Sacrifice " + GameUtils.getCardLink(self) + " to make " + GameUtils.getCardLink(rider) + " immune to Exposure");
            // Pay cost(s)
            action.appendCost(
                    new LoseCardFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new ImmuneToUntilEndOfTurnEffect(action, rider, Title.Exposure));
            return Collections.singletonList(action);
        }
        return null;
    }
}
