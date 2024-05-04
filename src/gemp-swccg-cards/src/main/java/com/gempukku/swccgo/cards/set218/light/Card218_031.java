package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 18
 * Type: Starship
 * Subtype: Starfighter
 * Title: Tydirium (V)
 */
public class Card218_031 extends AbstractStarfighter {
    public Card218_031() {
        super(Side.LIGHT, 3, 3, 2, null, 5, 3, 4, Title.Tydirium, Uniqueness.UNIQUE, ExpansionSet.SET_18, Rarity.V);
        setVirtualSuffix(true);
        setLore("Stolen Imperial Lambda-class shuttle. Supposedly carried parts and technical crew. Delivered General Solo's crack team of Rebel scouts to the forest moon of Endor.");
        setGameText("May add 1 pilot and 3 passengers. Permanent pilot provides ability of 2. Once per game, may retrieve Fly Casual. While alone and piloted by [Endor] Han, immune to attrition and adds one battle destiny.");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_18);
        addModelType(ModelType.LAMBDA_CLASS_SHUTTLE);
        setPilotCapacity(1);
        setPassengerCapacity(3);
        setAlwaysStolen(true);
        setMatchingPilotFilter(Filters.Han);
    }


    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionModifier(self, new AndCondition(new AloneCondition(self), new HasPilotingCondition(self, Filters.and(Icon.ENDOR, Filters.Han)))));
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new AloneCondition(self), new HasPilotingCondition(self, Filters.and(Icon.ENDOR, Filters.Han))), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.TYDIRIUM_V__RETRIEVE_FLY_CASUAL;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve Fly Casual");

            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.title("Fly Casual")));

            return Collections.singletonList(action);
        }

        return null;
    }
}
