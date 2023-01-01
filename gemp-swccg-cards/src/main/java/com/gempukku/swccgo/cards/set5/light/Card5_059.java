package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Off The Edge
 */
public class Card5_059 extends AbstractLostInterrupt {
    public Card5_059() {
        super(Side.LIGHT, 2, Title.Off_The_Edge, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Despite the brutal attacks of the Dark Lord, dying on Cloud City was not Luke's destiny.");
        setGameText("Target one of your characters at a Cloud City site. Draw destiny. If destiny > character's destiny number, retrieve Force equal to the difference. If destiny < character's destiny number, lose difference. If destiny = character's destiny number, character is lost.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.Cloud_City_site));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target character");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                            action.addAnimationGroup(character);
                            // Allow response(s)
                            action.allowResponses("Draw destiny while targeting " + GameUtils.getCardLink(character),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                            action.appendEffect(
                                                                    new RefreshPrintedDestinyValuesEffect(action, Collections.singleton(finalCharacter)) {
                                                                        @Override
                                                                        protected void refreshedPrintedDestinyValues() {
                                                                            final GameState gameState = game.getGameState();
                                                                            if (totalDestiny == null) {
                                                                                gameState.sendMessage("Result: Character lost due to failed destiny draw");
                                                                                action.appendEffect(
                                                                                        new LoseCardFromTableEffect(action, finalCharacter));
                                                                                return;
                                                                            }

                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            float charactersDestiny = game.getModifiersQuerying().getDestiny(game.getGameState(), character);
                                                                            gameState.sendMessage("Character's destiny number: " + GuiUtils.formatAsString(charactersDestiny));

                                                                            if (totalDestiny > charactersDestiny) {
                                                                                float difference = totalDestiny - charactersDestiny;
                                                                                gameState.sendMessage("Result: Retrieve " + GuiUtils.formatAsString(difference) + " Force");
                                                                                action.appendEffect(
                                                                                        new RetrieveForceEffect(action, playerId, difference) {
                                                                                            @Override
                                                                                            public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                                                                                return Collections.singletonList(finalCharacter);
                                                                                            }
                                                                                        });
                                                                            }
                                                                            else if (totalDestiny < charactersDestiny) {
                                                                                float difference = charactersDestiny - totalDestiny;
                                                                                gameState.sendMessage("Result: Lose " + GuiUtils.formatAsString(difference) + " Force");
                                                                                action.appendEffect(
                                                                                        new LoseForceEffect(action, playerId, difference));
                                                                            }
                                                                            else {
                                                                                gameState.sendMessage("Result: Character lost");
                                                                                action.appendEffect(
                                                                                        new LoseCardFromTableEffect(action, finalCharacter));
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
}