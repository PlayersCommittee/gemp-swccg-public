package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Ewok Rescue
 */
public class Card8_049 extends AbstractUsedInterrupt {
    public Card8_049() {
        super(Side.LIGHT, 4, Title.Ewok_Rescue, Uniqueness.UNRESTRICTED, ExpansionSet.ENDOR, Rarity.C);
        setLore("Ewoks used superior knowledge of Endor's forested terrain to free commandos captured by Imperial stormtroopers.");
        setGameText("If your Ewok is present with an escorted captive, draw destiny. Add 2 if Ewok is a scout. If total destiny > escort's defense value, captive is released. OR If your Ewok is defending a battle, up to three of your Ewoks at one exterior site may move as a 'react' (for free).");
        addIcons(Icon.ENDOR);
        addKeyword(Keyword.CAN_RELEASE_CAPTIVES);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final Filter ewokFilter = Filters.and(Filters.your(self), Filters.Ewok,
                Filters.presentWith(self, SpotOverride.INCLUDE_CAPTIVE, Filters.escortedCaptive));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, ewokFilter)
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.escortedCaptive)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Attempt to release captive");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Ewok", ewokFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard ewok) {
                            Filter captiveFilter = Filters.and(Filters.escortedCaptive, Filters.presentWith(ewok));
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose captive", SpotOverride.INCLUDE_CAPTIVE, captiveFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, final PhysicalCard captive) {
                                            action.addAnimationGroup(ewok, captive);
                                            // Allow response(s)
                                            action.allowResponses("Have " + GameUtils.getCardLink(ewok) + " attempt to release " + GameUtils.getCardLink(captive),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            final PhysicalCard finalEwok = action.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard finalCaptive = action.getPrimaryTargetCard(targetGroupId2);
                                                            final PhysicalCard escortAtPlay = finalCaptive != null ? finalCaptive.getEscort() : null;
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            PhysicalCard escort = escortAtPlay != null ? escortAtPlay : (finalCaptive != null ? finalCaptive.getEscort() : null);
                                                                            if (escort != null) {
                                                                                return Arrays.asList(escort);
                                                                            }
                                                                            return Collections.emptyList();
                                                                        }
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            GameState gameState = game.getGameState();
                                                                            PhysicalCard escort = escortAtPlay != null ? escortAtPlay : finalCaptive.getEscort();
                                                                            if (totalDestiny == null) {
                                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                                return;
                                                                            }
                                                                            if (escort == null) {
                                                                                gameState.sendMessage("Result: Failed due to missing escort");
                                                                                return;
                                                                            }

                                                                            float scoutBonus = (Filters.scout.accepts(game, finalEwok) || finalEwok.getBlueprint().hasKeyword(com.gempukku.swccgo.common.Keyword.SCOUT)) ? 2 : 0;
                                                                            float total = totalDestiny + scoutBonus;
                                                                            float defenseValue = game.getModifiersQuerying().getDefenseValue(gameState, escort);

                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            if (scoutBonus > 0) {
                                                                                gameState.sendMessage("Scout bonus: +" + GuiUtils.formatAsString(scoutBonus));
                                                                            }
                                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(total));
                                                                            gameState.sendMessage("Escort's defense value: " + GuiUtils.formatAsString(defenseValue));

                                                                            if (total > defenseValue) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new ReleaseCaptiveEffect(action, finalCaptive));
                                                                            }
                                                                            else {
                                                                                gameState.sendMessage("Result: Failed");
                                                                            }
                                                                        }
                                                                    }
                                                            );
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
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s) — react window when battle is initiated and your Ewok is defending (not if you initiated).
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.and(Filters.site, Filters.canBeTargetedBy(self)))
                && GameConditions.isDuringBattleInitiatedBy(game, opponent)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Ewok))) {
            Filter ewokFilter = getReactEwokFilter(self, null);
            if (GameConditions.canTarget(game, self, ewokFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move Ewoks as 'react'");
                // First react is required targeting so Sense can cancel the whole play.
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Ewok to move as a 'react'", ewokFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedEwok) {
                                action.addAnimationGroup(targetedEwok);
                                action.addSecondaryTargetFilter(Filters.battleLocation);
                                // Allow response(s)
                                action.allowResponses("Move " + GameUtils.getCardLink(targetedEwok) + " as a 'react'",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                PhysicalCard finalEwok = action.getPrimaryTargetCard(targetGroupId1);
                                                final PhysicalCard exteriorSite = getExteriorSiteOf(finalEwok);
                                                action.appendEffect(
                                                        new MoveAsReactEffect(action, finalEwok, true));
                                                // Remaining Ewoks at the same exterior site may optionally react for free
                                                // as sub-actions of this same interrupt (AR Appendix C). Total <= 3.
                                                appendOptionalAdditionalReacts(action, playerId, self, exteriorSite, 2);
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

    private PhysicalCard getExteriorSiteOf(PhysicalCard ewok) {
        PhysicalCard site = ewok.getAtLocation();
        if (site == null && ewok.getAttachedTo() != null) {
            site = ewok.getAttachedTo().getAtLocation();
        }
        return site;
    }

    private Filter getReactEwokFilter(PhysicalCard self, PhysicalCard lockedExteriorSite) {
        Filter siteFilter = lockedExteriorSite != null
                ? Filters.sameCardId(lockedExteriorSite)
                : Filters.exterior_site;
        return Filters.and(Filters.your(self), Filters.Ewok, Filters.at(siteFilter),
                Filters.canMoveAsReactAsActionFromOtherCard(self, true, 0, false));
    }

    private void appendOptionalAdditionalReacts(final PlayInterruptAction action, final String playerId, final PhysicalCard self,
                                                final PhysicalCard exteriorSite, final int remaining) {
        if (remaining <= 0 || exteriorSite == null) {
            return;
        }
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Filter remainingFilter = getReactEwokFilter(self, exteriorSite);
                        if (GameConditions.canTarget(game, self, remainingFilter)) {
                            action.appendEffect(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose another Ewok to move as a 'react'", remainingFilter, 0) {
                                        @Override
                                        protected void cardSelected(PhysicalCard selectedCard) {
                                            action.appendEffect(
                                                    new MoveAsReactEffect(action, selectedCard, true));
                                            appendOptionalAdditionalReacts(action, playerId, self, exteriorSite, remaining - 1);
                                        }
                                    }
                            );
                        }
                    }
                }
        );
    }
}
