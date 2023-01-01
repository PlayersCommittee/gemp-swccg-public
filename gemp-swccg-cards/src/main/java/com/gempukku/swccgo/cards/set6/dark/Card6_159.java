package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: Trap Door
 */
public class Card6_159 extends AbstractUsedInterrupt {
    public Card6_159() {
        super(Side.DARK, 5, Title.Trap_Door, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("'Boscka!'");
        setGameText("If you have no characters at Rancor Pit at the end of your deploy phase, target a character (even a captive) at Audience Chamber. Draw destiny. If destiny +2 > ability, target relocated to Rancor Pit, and, if captive, released to the Light Side of Rancor Pit (not Used Pile).");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.character, Filters.at(Filters.Audience_Chamber), Filters.canBeRelocatedToLocation(Filters.Rancor_Pit, false, false, true, 0, false));

        // Check condition(s)
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.DEPLOY)
                && GameConditions.canSpotLocation(game, Filters.Rancor_Pit)
                && !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.Rancor_Pit)))
                && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target character");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", SpotOverride.INCLUDE_CAPTIVE, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Relocate " + GameUtils.getCardLink(targetedCard) + " to Rancor Pit",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            if (Filters.captive.accepts(game, finalTarget)
                                                    && GameConditions.hasGameTextModification(game, self, ModifyGameTextType.TRAP_DOOR__DO_NOT_DRAW_DESTINY)) {
                                                PhysicalCard rancorPit = Filters.findFirstFromTopLocationsOnTable(game, Filters.Rancor_Pit);
                                                action.appendEffect(
                                                        new RelocateBetweenLocationsEffect(action, finalTarget, rancorPit));
                                            }
                                            else {
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId) {
                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                GameState gameState = game.getGameState();

                                                                float ability = game.getModifiersQuerying().getAbility(game.getGameState(), finalTarget);
                                                                gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                                                                gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                                if (((totalDestiny != null ? totalDestiny : 0) + 2) > ability) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    PhysicalCard rancorPit = Filters.findFirstFromTopLocationsOnTable(game, Filters.Rancor_Pit);
                                                                        action.appendEffect(
                                                                                new RelocateBetweenLocationsEffect(action, finalTarget, rancorPit));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                        });
                                            }
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
}