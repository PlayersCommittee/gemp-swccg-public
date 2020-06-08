package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Interrupt
 * Subtype: Used
 * Title: I've Got A Bad Feeling About...
 */
public class Card208_019 extends AbstractUsedInterrupt {
    public Card208_019() {
        super(Side.LIGHT, 4, "I've Got A Bad Feeling About...", Uniqueness.UNIQUE);
        setLore("Unlike the Empire, the Alliance treats their droids with respect. Many droids volunteered to share the risk of battle and aid the Rebellion's assault on the Death Star.");
        setGameText("[Upload] one security droid. OR During your control phase, if K-2SO controls opponent's site, opponent loses 1 Force. OR If a battle was just initiated where K-2SO is with your spy, lose K-2SO to cancel that battle. OR Cancel Nevar Yalnal.");
        addIcons(Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.IVE_GOT_A_BAD_FEELING_ABOUT__UPLOAD_SECURITY_DROID;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take security droid into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a security droid into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.security_droid, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.canTarget(game, self, Filters.and(Filters.K2SO, Filters.at(Filters.and(Filters.opponents(self),
                Filters.site, Filters.controls(playerId), Filters.canBeTargetedBy(self)))))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent lose 1 Force");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose K-2SO", Filters.K2SO) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(int targetGroupId2, final PhysicalCard targetedK2SO) {
                            action.addAnimationGroup(targetedK2SO);
                            action.addSecondaryTargetFilter(Filters.sameSiteAs(self, Filters.K2SO));
                            // Allow response(s)
                            action.allowResponses("Make opponent lose 1 Force by targeting " + GameUtils.getCardLink(targetedK2SO),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseForceEffect(action, opponent, 1));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.canTarget(game, self, Filters.and(Filters.K2SO,
                Filters.inBattleWith(Filters.and(Filters.your(self), Filters.spy, Filters.canBeTargetedBy(self)))))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Lose K-2SO to cancel battle");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose K-2SO", Filters.K2SO) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }

                        @Override
                        protected void cardTargeted(int targetGroupId1, final PhysicalCard targetedK2SO) {
                            // Choose target(s)
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose spy", Filters.and(Filters.your(self), Filters.spy, Filters.not(Filters.K2SO), Filters.participatingInBattle)) {
                                        @Override
                                        protected boolean getUseShortcut() {
                                            return true;
                                        }

                                        @Override
                                        protected void cardTargeted(int targetGroupId2, final PhysicalCard targetedSpy) {
                                            action.addAnimationGroup(targetedSpy);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new LoseCardFromTableEffect(action, targetedK2SO, true));
                                            // Allow response(s)
                                            action.allowResponses("Cancel battle by targeting " + GameUtils.getCardLink(targetedSpy),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new CancelBattleEffect(action));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
           return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Nevar_Yalnal)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}