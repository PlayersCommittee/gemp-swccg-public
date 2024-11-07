package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Hired Gun
 */
public class Card304_116 extends AbstractUsedOrLostInterrupt {
    public Card304_116() {
        super(Side.LIGHT, 3, Title.Hired_Gun, Uniqueness.UNRESTRICTED, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Throughout the galaxy there's always someone willing to commit a crime, for a price.");
        setGameText("USED: [Upload] Selynn Valotra, non-[Maintenance] Boba Fett, Ixtal, or Dia Gida. LOST: During your move phase, your gangster (or [Independent] starship they are piloting) may make an additional move. OR Move your gangster as a 'react'.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.HIRED_GUN__UPLOAD_SELYNN_BOBA_FETT_IXTAL_DIA_GIDA;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Selynn Valtora, non-[Maintenance] Boba Fett, Ixtal, or Dia Gida into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Selynn_Valtora,
                                            Filters.and(Filters.Boba_Fett, Filters.not(Icon.MAINTENANCE)), Filters.Ixtal, Filters.DIA_GIDA), true));
                        }
                    }
            );
            actions.add(action);
        }

        Filter targetFilter = Filters.and(Filters.your(self), Filters.or(Filters.gangster,
                Filters.and(Filters.starship, Icon.INDEPENDENT, Filters.hasPiloting(self, Filters.and(Filters.your(self), Filters.or(Filters.gangster))))),
                Filters.movableAsAdditionalMove(playerId));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make an additional move");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose gangster or starship", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Have " + GameUtils.getCardLink(targetedCard) + " make an additional move",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MoveCardAsRegularMoveEffect(action, playerId, finalTarget, false, true, Filters.any));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        Filter gangsterFilter = Filters.and(Filters.your(self), Filters.gangster, Filters.canMoveAsReactAsActionFromOtherCard(self, false, 0, false));

        // Check condition(s)
        if ((TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent)
                || TriggerConditions.battleInitiated(game, effectResult, opponent))
                && GameConditions.canTarget(game, self, gangsterFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Move gangster as 'react'");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose gangster", gangsterFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " as a 'react'",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            final PhysicalCard finalBountyHunter = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MoveAsReactEffect(action, finalBountyHunter, false));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}