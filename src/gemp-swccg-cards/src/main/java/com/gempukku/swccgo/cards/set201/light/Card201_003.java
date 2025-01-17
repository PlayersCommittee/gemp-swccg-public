package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Character
 * Subtype: Alien
 * Title: Melas (V)
 */
public class Card201_003 extends AbstractAlien {
    public Card201_003() {
        super(Side.LIGHT, 2, 3, 2, 4, 4, "Melas", Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Sarkan smuggler. Smokes an Essoomian gruu pipe to heighten awareness. Exiled from his home planet of Sarka for displaying curiosity in other aliens. Misses his homeworld.");
        setGameText("[Pilot] 2. Your StarSpeeders are destiny +2 and move for free. Once per turn, may place a card from hand on Used Pile to 'smoke pipe' (for remainder of turn, Melas is power and defense value +2 and may not move).");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.VIRTUAL_SET_1);
        setSpecies(Species.SARKAN);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));

        Filter yourStarSpeederFilter = Filters.and(Filters.your(self), Filters.titleContains("StarSpeeder"));
        modifiers.add(new DestinyModifier(self, yourStarSpeederFilter, 2));
        modifiers.add(new MovesForFreeModifier(self, yourStarSpeederFilter));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasHand(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place card from hand on Used Pile");
            action.setActionMsg("Smoke pipe (power and defense value +2 and may not move)");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PutCardFromHandOnUsedPileEffect(action, playerId));
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action, new PowerModifier(self, Filters.title("Melas"), 2), "make Melas power +2 until end of turn"));
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action, new DefenseValueModifier(self, Filters.title("Melas"), 2), "make Melas defense value +2 until end of turn"));
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action, new MayNotMoveModifier(self, Filters.title("Melas")), "make Melas unable to move until end of turn"));
            
            return Collections.singletonList(action);
        }

        return null;
    }
}
