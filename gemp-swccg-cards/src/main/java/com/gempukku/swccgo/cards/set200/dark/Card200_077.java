package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.InsteadOfForceDrainingEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Imperial
 * Title: DS-61-5
 */
public class Card200_077 extends AbstractImperial {
    public Card200_077() {
        super(Side.DARK, 2, 2, 2, 2, 5, Title.DS_61_5, Uniqueness.UNIQUE);
        setGameText("[Pilot] 2, 3: Black 5. While piloting Black 5, it is maneuver +1 and he draws one battle destiny if not able to otherwise. If at opponent's system, instead of Force draining here, may take any one card into hand from Force Pile; reshuffle.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.BLACK_SQUADRON);
        setMatchingStarshipFilter(Filters.Black_5);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whilePilotingBlack5 = new PilotingCondition(self, Filters.Black_5);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Black_5)));
        modifiers.add(new ManeuverModifier(self, Filters.Black_5, whilePilotingBlack5, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, whilePilotingBlack5, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DS_61_5__UPLOAD_CARD_FROM_FORCE_PILE;

        // Check condition(s)
        if (GameConditions.isAtLocation(game, self, Filters.and(Filters.opponents(self), Filters.system))) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, self);
            if (GameConditions.canInsteadOfForceDrainingAtLocation(game, playerId, location)
                    && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take card into hand from Force Pile");
                action.setActionMsg("Take a card into hand from Force Pile");
                // Perform result(s)
                action.appendEffect(
                        new InsteadOfForceDrainingEffect(action, location,
                                new TakeCardIntoHandFromForcePileEffect(action, playerId, true)));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
