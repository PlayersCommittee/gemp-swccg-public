package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Used
 * Title: Darth Maul's Demise
 */
public class Card13_012 extends AbstractUsedInterrupt {
    public Card13_012() {
        super(Side.LIGHT, 6, "Darth Maul's Demise", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Despite coming face to face with the first Sith to be seen in a millenia, the young Padawan was still a force to be reckoned with.");
        setGameText("If your [Episode I] Jedi just defeated Maul in lightsaber combat, draw destiny. Add 2 if Qui-Gon out of play. If total > 6, place Maul out of play and you may search your Lost Pile and take any one card into hand. (Immune to Sense.)");
        addIcons(Icon.EPISODE_I, Icon.REFLECTIONS_III);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.DARTH_MAULS_DEMISE__PLACE_MAUL_OUT_OF_PLAY_AND_SEARCH_LOST_PILE;

        if (TriggerConditions.wonLightsaberCombatAgainst(game, effectResult, Filters.and(Filters.your(self), Icon.EPISODE_I, Filters.Jedi), Filters.Maul)
                && GameConditions.canTarget(game, self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY, Filters.Maul)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Attempt to place Maul out of play");
            action.setImmuneTo(Title.Sense);

            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target Maul", TargetingReason.TO_BE_PLACED_OUT_OF_PLAY, Filters.Maul) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses("Draw destiny", new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new DrawDestinyEffect(action, playerId, 1) {
                                @Override
                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                    if (totalDestiny == null) {
                                        game.getGameState().sendMessage("Result: Failed due to failed destiny draw");
                                    } else {
                                        float destinyValue = totalDestiny;
                                        if (GameConditions.isOutOfPlay(game, Filters.QuiGon))
                                            destinyValue += 2;

                                        game.getGameState().sendMessage("Total destiny: "+destinyValue);
                                        if (destinyValue > 6) {
                                            PhysicalCard maul = action.getPrimaryTargetCard(targetGroupId);
                                            game.getGameState().sendMessage("Result: Success");
                                            action.appendEffect(new PlaceCardOutOfPlayFromTableEffect(action, maul));
                                            action.appendEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Take any card into hand from Lost Pile?") {
                                                        @Override
                                                        protected void yes() {
                                                            action.appendEffect(new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.any, false));
                                                        }

                                                        @Override
                                                        protected void no() {
                                                            action.appendEffect(new SendMessageEffect(action,playerId+" chooses not to search Lost Pile"));
                                                        }
                                                    })
                                            );

                                        } else {
                                            game.getGameState().sendMessage("Result: Failed");
                                        }
                                    }
                                }
                            });
                        }
                    });
                }

                @Override
                protected boolean getUseShortcut() {
                    return true;
                }
            });

            return Collections.singletonList(action);
        }
        return null;
    }
}