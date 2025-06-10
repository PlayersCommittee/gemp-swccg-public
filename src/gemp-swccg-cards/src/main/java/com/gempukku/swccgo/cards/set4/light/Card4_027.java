package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceFromInsertCardEffect;
import com.gempukku.swccgo.logic.effects.LoseInsertCardEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Effect
 * Title: Never Tell Me The Odds
 */
public class Card4_027 extends AbstractNormalEffect {
    public Card4_027() {
        super(Side.LIGHT, 3, PlayCardZoneOption.OPPONENTS_RESERVE_DECK, Title.Never_Tell_Me_The_Odds, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("'You said you wanted to be around when I made a mistake, well, this could be it, sweetheart.'");
        setGameText("'Insert' in opponent's Reserve Deck. When Effect reaches top it is lost and up to the three lowest destiny numbers of each player's characters on table are totaled. Player with lower total loses Force equal to the difference. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextInsertCardRevealed(SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Reveal 'insert' card");
        action.setActionMsg(null);
        // Perform result(s)
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        final GameState gameState = game.getGameState();
                        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                        final Collection<PhysicalCard> playersCharacters = Filters.filterActive(game, self, Filters.and(Filters.owner(playerId), Filters.character));
                        final Collection<PhysicalCard> opponentsCharacters = Filters.filterActive(game, self, Filters.and(Filters.owner(opponent), Filters.character));

                        // Because this card looks at destiny values, we need to refresh any cards that have multiple destinies (for example: R2D2)
                        action.appendEffect(
                                new RefreshPrintedDestinyValuesEffect(action, playersCharacters) {
                                    @Override
                                    protected void refreshedPrintedDestinyValues() {
                                        action.appendEffect(
                                                new RefreshPrintedDestinyValuesEffect(action, opponentsCharacters) {
                                                    @Override
                                                    protected void refreshedPrintedDestinyValues() {
                                                        // Get all the destiny numbers
                                                        List<Float> playersDestinies = new LinkedList<Float>();
                                                        for (PhysicalCard character : playersCharacters) {
                                                            playersDestinies.add(modifiersQuerying.getDestiny(game.getGameState(), character));
                                                        }
                                                        Collections.sort(playersDestinies);
                                                        float playersTotal = 0;
                                                        for (int i=0; i<3 && i<playersDestinies.size(); ++i) {
                                                            playersTotal += playersDestinies.get(i);
                                                        }
                                                        List<Float> opponentsDestinies = new LinkedList<Float>();
                                                        for (PhysicalCard character : opponentsCharacters) {
                                                            opponentsDestinies.add(modifiersQuerying.getDestiny(game.getGameState(), character));
                                                        }
                                                        Collections.sort(opponentsDestinies);
                                                        float opponentsTotal = 0;
                                                        for (int i=0; i<3 && i<opponentsDestinies.size(); ++i) {
                                                            opponentsTotal += opponentsDestinies.get(i);
                                                        }

                                                        gameState.sendMessage(playerId + "'s total is " + GuiUtils.formatAsString(playersTotal));
                                                        gameState.sendMessage(opponent + "'s total is " + GuiUtils.formatAsString(opponentsTotal));

                                                        action.appendEffect(
                                                                new LoseInsertCardEffect(action, self));

                                                        if (playersTotal != opponentsTotal) {
                                                            String lowerPlayer = (playersTotal > opponentsTotal) ? opponent : playerId;
                                                            float difference = Math.abs(playersTotal - opponentsTotal);
                                                            action.appendEffect(
                                                                    new LoseForceFromInsertCardEffect(action, lowerPlayer, difference));
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
        return action;
    }
}