package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 6
 * Type: Interrupt
 * Subtype: Used
 * Title: Lightsaber Deficiency (V)
 */
public class Card601_018 extends AbstractUsedInterrupt {
    public Card601_018() {
        super(Side.DARK, 5, "Lightsaber Deficiency", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'Ah... Uh...'");
        setGameText("Take Ghhhk or Molator into hand from Reserve Deck; reshuffle. OR Cancel Corellian Retort, Gift Of The Mentor, or Jedi Presence. OR Retrieve Protocol Failure into hand. OR If a lightsaber was just 'swung' during battle, it may not target again this battle.");
        addIcons(Icon.HOTH, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && TriggerConditions.weaponJustFired(game, effectResult, Filters.lightsaber)) {
            FiredWeaponResult weaponFiredResult = (FiredWeaponResult) effectResult;
            final PhysicalCard lightsaber = weaponFiredResult.getWeaponCardFired() != null ? weaponFiredResult.getWeaponCardFired() : weaponFiredResult.getPermanentWeaponFired().getPhysicalCard(game);
            if (GameConditions.canTarget(game, self, lightsaber)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Prevent " + GameUtils.getCardLink(lightsaber) + " from targeting again");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose lightsaber", lightsaber) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Prevent " + GameUtils.getCardLink(targetedCard) + " from targeting again this battle",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddUntilEndOfBattleModifierEffect(action,
                                                                new MayNotBeFiredModifier(self, Filters.or(targetedCard, Filters.permanentWeaponOf(targetedCard))),
                                                                "Prevents " + GameUtils.getCardLink(targetedCard) + " from targeting again this battle"));
                                            }
                                        }
                                );
                            }
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LIGHTSABER_DEFICIENCY__UPLOAD_GHHHK_OR_MOLATOR;
        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Ghhhk or Molator into hand");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Ghhhk, Filters.Molator), true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.hasLostPile(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Retrieve Protocol Failure into hand");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardIntoHandEffect(action, playerId, Filters.title("Protocol Failure")));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.title("Corellian Retort"))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title("Corellian Retort"), "Corellian Retort");
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Gift_Of_The_Mentor)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Gift_Of_The_Mentor, Title.Gift_Of_The_Mentor);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Jedi_Presence)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Jedi_Presence, Title.Jedi_Presence);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.title("Corellian Retort"), Filters.Gift_Of_The_Mentor, Filters.Jedi_Presence))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }
}