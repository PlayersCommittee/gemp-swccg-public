package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsAtSameLocationEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Friendly Fire
 */
public class Card1_080 extends AbstractLostInterrupt {
    public Card1_080() {
        super(Side.LIGHT, 4, Title.Friendly_Fire, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Overcrowding in heavy fire zones increases chances of accidentally shooting one's own comrades. Stormtroopers never let accuracy get in the way of victory.");
        setGameText("An accident occurs at the beginning of a battle at any site where opponent has at least two characters and one weapon. Draw destiny. If destiny < number of opponent's characters at that site, one is lost. (Opponent's choice.)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.and(Filters.site, Filters.canBeTargetedBy(self)))) {
            TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
            Filter characterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.at(Filters.and(Filters.battleLocation,
                    Filters.sameSiteAs(self, Filters.and(Filters.opponents(self), Filters.weapon_or_character_with_permanent_weapon, Filters.canBeTargetedBy(self))))));
            if (GameConditions.canTarget(game, self, 2, targetingReason, characterFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setActionMsg("Cause an accident");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardsAtSameLocationEffect(action, playerId, "Choose characters", 2, Integer.MAX_VALUE, targetingReason, Filters.and(Filters.opponents(self), Filters.character, Filters.at(Filters.battleLocation))) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected boolean isTargetAll() {
                                return true;
                            }
                            @Override
                            protected void cardsTargeted(final int targetGroupId1, Collection<PhysicalCard> targetedCharacters) {
                                action.addAnimationGroup(targetedCharacters);
                                // Set secondary target filter(s)
                                action.addSecondaryTargetFilter(Filters.battleLocation);
                                action.addSecondaryTargetFilter(Filters.and(Filters.opponents(self), Filters.weapon_or_character_with_permanent_weapon, Filters.at(Filters.battleLocation)));
                                // Allow response(s)
                                action.allowResponses("Cause an accident involving " + GameUtils.getAppendedNames(targetedCharacters),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the final targeted card(s)
                                                final Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId1);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId) {
                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, final List<Float> destinyDrawValues, Float totalDestiny) {
                                                                final GameState gameState = game.getGameState();
                                                                final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                                if (totalDestiny == null) {
                                                                    gameState.sendMessage("Result: No result due to failed destiny draw");
                                                                    return;
                                                                }

                                                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                int numberOfCharacters = finalCharacters.size();
                                                                gameState.sendMessage("Number of characters: " + numberOfCharacters);

                                                                if (totalDestiny < numberOfCharacters) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    String playerToChoose = modifiersQuerying.getPlayerToChooseCardTargetAtLocation(gameState, self, gameState.getBattleLocation(), opponent);
                                                                    action.appendEffect(
                                                                            new ChooseCardToLoseFromTableEffect(action, playerToChoose, Filters.in(finalCharacters)));
                                                                } else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                        });
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
}