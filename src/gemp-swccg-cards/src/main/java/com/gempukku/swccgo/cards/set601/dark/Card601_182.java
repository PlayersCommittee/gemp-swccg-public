package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.InsteadOfForceDrainingEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Character
 * Subtype: Imperial
 * Title: DS-61-5
 */
public class Card601_182 extends AbstractImperial {
    public Card601_182() {
        super(Side.DARK, 2, 2, 2, 2, 5, Title.DS_61_5, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setGameText("[Pilot] 2, 3: Black 5. While piloting Black 5, it is maneuver +1 and he draws one battle destiny if not able to otherwise. If at opponent's system, instead of Force draining here, may take any one card into hand from Force Pile; reshuffle.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.LEGACY_BLOCK_7);
        addKeywords(Keyword.BLACK_SQUADRON);
        setMatchingStarshipFilter(Filters.Black_5);
        setAsLegacy(true);
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
