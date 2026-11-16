package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
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

        Filter characterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.at(Filters.battleLocation));
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.and(Filters.site, Filters.canBeTargetedBy(self),
                Filters.sameSiteAs(self, Filters.and(Filters.opponents(self), Filters.weapon_or_character_with_permanent_weapon, Filters.canBeTargetedBy(self)))))
                && GameConditions.canSpot(game, self, 2, characterFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setActionMsg("Cause an accident");
            // Count-set is specified at initiation but is not targeted to be lost.
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
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
                                            Collection<PhysicalCard> characters = Filters.filterActive(game, self, characterFilter);
                                            int numberOfCharacters = characters.size();
                                            gameState.sendMessage("Number of characters: " + numberOfCharacters);

                                            if (totalDestiny < numberOfCharacters) {
                                                gameState.sendMessage("Result: Succeeded");
                                                String playerToChoose = modifiersQuerying.getPlayerToChooseCardTargetAtLocation(gameState, self, gameState.getBattleLocation(), opponent);
                                                action.appendEffect(
                                                        new ChooseCardToLoseFromTableEffect(action, playerToChoose, Filters.in(characters)));
                                            } else {
                                                gameState.sendMessage("Result: Failed");
                                            }
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
