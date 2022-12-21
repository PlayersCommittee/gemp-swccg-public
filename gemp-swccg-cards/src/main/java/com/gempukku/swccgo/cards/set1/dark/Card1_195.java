package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsToStealAndAttachOrBeLostEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Tonnika Sisters
 */
public class Card1_195 extends AbstractAlien {
    public Card1_195() {
        super(Side.DARK, 2, 2, 2, 2, 2, Title.Tonnika_Sisters, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setComboCard(true);
        setLore("Twins. Thieves. Con artists. Spies. Swindlers. Double agents. Brea and Senni use their natural charm to sway the unwary on the fringe of society.");
        setGameText("Twice during each of your control phases, may use 2 Force to draw 2 destiny for 2 chances at a destiny = 2. If successful, may steal or destroy up to 2 weapons or 2 devices present.");
        addIcon(Icon.WARRIOR, 2);
        addKeywords(Keyword.SPY, Keyword.THIEF, Keyword.FEMALE);
        setComboCard(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isPhaseForPlayer(game, Phase.CONTROL, playerId)) {
            final int numberOnCard = game.getModifiersQuerying().isDoubled(game.getGameState(), self) ? 4 : 2;

            // Check if any devices or weapons can be stolen
            final Filter deviceToDestroyFilter = Filters.and(Filters.device, Filters.present(self), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST));
            final Filter deviceToStealFilter = Filters.and(Filters.device, Filters.present(self), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_STOLEN));
            final Filter weaponToDestroyFilter = Filters.and(Filters.weapon, Filters.present(self), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST));
            final Filter weaponToStealFilter = Filters.and(Filters.weapon, Filters.present(self), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_STOLEN));

            if (GameConditions.isNumTimesDuringYourPhase(game, self, playerId, numberOnCard, gameTextSourceCardId, Phase.CONTROL)
                    && GameConditions.canUseForce(game, playerId, numberOnCard)
                    && GameConditions.canTarget(game, self, Filters.or(deviceToDestroyFilter, deviceToStealFilter, weaponToDestroyFilter, weaponToStealFilter))) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Steal/destroy weapons or devices");
                action.setActionMsg("Draw " + numberOnCard + " destiny to steal or destroy up to " + numberOnCard + " weapons or devices present");
                // Update usage limit(s)
                action.appendUsage(
                        new NumTimesPerPhaseEffect(action, numberOnCard));
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, numberOnCard));
                // Perform result(s)
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId, numberOnCard) {
                            @Override
                            protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                GameState gameState = game.getGameState();
                                if (totalDestiny == null) {
                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                    return;
                                }
                                boolean matchedNumber = false;
                                for (Float value : destinyDrawValues) {
                                    if (value == numberOnCard) {
                                        matchedNumber = true;
                                        break;
                                    }
                                }
                                if (!matchedNumber) {
                                    gameState.sendMessage("Result: No destiny draws matched " + numberOnCard);
                                    return;
                                }

                                List<String> choicesText = new LinkedList<String>();
                                boolean devicesOptionValid = false;
                                if (GameConditions.canTarget(game, self, deviceToDestroyFilter) || GameConditions.canTarget(game, self, deviceToStealFilter)) {
                                    choicesText.add("Steal or destroy up to " + numberOnCard + " devices present");
                                    devicesOptionValid = true;
                                }
                                boolean weaponsOptionValid = false;
                                if (GameConditions.canTarget(game, self, weaponToDestroyFilter) || GameConditions.canTarget(game, self, weaponToStealFilter)) {
                                    choicesText.add("Steal or destroy up to " + numberOnCard + " weapons present");
                                    weaponsOptionValid = true;
                                }
                                final boolean isDevicesOptionValid = devicesOptionValid;
                                final boolean isWeaponsOptionValid = weaponsOptionValid;

                                if (!choicesText.isEmpty()) {
                                    choicesText.add("Do nothing");
                                    String[] choices = new String[choicesText.size()];
                                    for (int i = 0; i < choicesText.size(); ++i) {
                                        choices[i] = choicesText.get(i);
                                    }

                                    // Ask player what to do
                                    action.appendEffect(
                                            new PlayoutDecisionEffect(action, playerId,
                                                    new MultipleChoiceAwaitingDecision("Choose effect", choices) {
                                                        @Override
                                                        protected void validDecisionMade(int index, String result) {
                                                            if (isDevicesOptionValid && index == 0) {
                                                                game.getGameState().sendMessage(playerId + " chooses to destroy or steal up to " + numberOnCard + " devices present");
                                                                action.appendEffect(
                                                                        new ChooseCardsToStealAndAttachOrBeLostEffect(action, playerId, 1, numberOnCard, Filters.or(deviceToDestroyFilter, deviceToStealFilter), self));
                                                            }
                                                            else if (isWeaponsOptionValid && ((!isDevicesOptionValid && index == 0) || (isDevicesOptionValid && index == 1))) {
                                                                game.getGameState().sendMessage(playerId + " chooses to destroy or steal up to " + numberOnCard + " weapons present");
                                                                action.appendEffect(
                                                                        new ChooseCardsToStealAndAttachOrBeLostEffect(action, playerId, 1, numberOnCard, Filters.or(weaponToDestroyFilter, weaponToStealFilter), self));
                                                            }
                                                            else {
                                                                game.getGameState().sendMessage(playerId + " chooses to not destroy or steal any weapons or devices present");
                                                            }
                                                        }
                                                    }
                                            )
                                    );
                                }
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
