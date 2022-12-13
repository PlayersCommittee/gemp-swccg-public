package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalWeaponDestinyBeforeDrawingDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Sorry About The Mess & Blaster Proficiency
 */
public class Card10_023 extends AbstractUsedOrLostInterrupt {
    public Card10_023() {
        super(Side.LIGHT, 2, "Sorry About The Mess & Blaster Proficiency", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        addComboCardTitles(Title.Sorry_About_The_Mess, Title.Blaster_Proficiency);
        setGameText("USED: If you just targeted with a blaster, add 3 to your total weapon destiny. LOST: During your control phase, fire one of your weapons. Any 'hit' targets are immediately lost. OR Lose 1 Force to place opponent's just 'hit' character in Lost Pile.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isFiringWeapon(game, effect, playerId, Filters.blaster)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add 3 to total weapon destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyTotalWeaponDestinyBeforeDrawingDestinyEffect(action, 3));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)) {
            Filter weaponFilter = Filters.and(Filters.your(self), Filters.weapon_or_character_with_permanent_weapon, Filters.canBeFiredAt(self, Filters.canBeTargetedBy(self), 0));
            if (GameConditions.canSpot(game, self, weaponFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Fire a weapon");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose weapon to fire", weaponFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard weapon) {
                                action.addAnimationGroup(weapon);
                                // Allow response(s)
                                action.allowResponses("Fire " + GameUtils.getCardLink(weapon),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                final PhysicalCard finalWeapon = action.getPrimaryTargetCard(targetGroupId);
                                                Filter targetFilter = Filters.canBeTargetedBy(self);
                                                if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.SORRY_ABOUT_THE_MESS__WEAPONS_FIRED_MUST_TARGET_GREEDO_IF_POSSIBLE)
                                                        && Filters.canBeFiredAt(self, Filters.and(targetFilter, Filters.Greedo), 0).accepts(game, finalWeapon)) {
                                                    targetFilter = Filters.and(targetFilter, Filters.Greedo);
                                                }
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new FireWeaponEffect(action, finalWeapon, false, targetFilter));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (TriggerConditions.justHit(game, effectResult, Filters.and(Filters.opponents(self), Filters.character))) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            if (GameConditions.canTarget(game, self, targetingReason, cardHit)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Make " + GameUtils.getFullName(cardHit) + " lost");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target character", targetingReason, cardHit) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Pay cost(s)
                                action.appendCost(
                                        new LoseForceEffect(action, playerId, 1, true));
                                // Allow response(s)
                                action.allowResponses("Make " + GameUtils.getCardLink(cardTargeted) + " lost",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard cardToMakeLost = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new LoseCardFromTableEffect(action, cardToMakeLost));
                                            }
                                        });
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}