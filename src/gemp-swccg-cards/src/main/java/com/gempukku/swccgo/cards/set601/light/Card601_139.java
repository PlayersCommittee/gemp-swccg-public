package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
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
 * Title: Desperate Reach (V)
 */
public class Card601_139 extends AbstractUsedInterrupt {
    public Card601_139() {
        super(Side.LIGHT, 5, Title.Desperate_Reach, Uniqueness.UNRESTRICTED, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("If only someone had given Luke a hand.");
        setGameText("Take Houjix or [Reflections II] R2-D2 into hand from Reserve Deck; reshuffle. OR Cancel Dark Jedi Presence, I Have You Now, or Maul Strikes. OR Place a card stacked on Droid Racks in opponent's Lost Pile. OR If a lightsaber was just 'swung' during battle, it may not target again this battle.");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6);
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

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DESPERATE_REACH_V__UPLOAD_HOUJIX_OR_R2D2;
        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Houjix or R2-D2 into hand");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Houjix, Filters.and(Icon.REFLECTIONS_II, Filters.R2D2)), true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Dark_Jedi_Presence)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Dark_Jedi_Presence, Title.Dark_Jedi_Presence);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.I_Have_You_Now)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.I_Have_You_Now, Title.I_Have_You_Now);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Maul_Strikes)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Maul_Strikes, Title.Maul_Strikes);
            actions.add(action);
        }


        if (GameConditions.canTarget(game, self, Filters.and(Filters.Droid_Racks, Filters.hasStacked(Filters.any)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place card stacked on Droid Racks in Lost Pile");
            action.appendTargeting(new ChooseStackedCardEffect(action, playerId, Filters.Droid_Racks) {
                @Override
                protected void cardSelected(final PhysicalCard selectedCard) {
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new PutStackedCardInLostPileEffect(action, playerId, selectedCard, false));
                        }
                    });
                }
            });

            actions.add(action);

        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Dark_Jedi_Presence, Filters.I_Have_You_Now, Filters.Maul_Strikes))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }
}