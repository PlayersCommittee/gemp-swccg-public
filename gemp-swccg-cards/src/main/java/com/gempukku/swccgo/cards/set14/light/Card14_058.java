package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Starfighter
 * Title: Bravo Fighter
 */
public class Card14_058 extends AbstractStarfighter {
    public Card14_058() {
        super(Side.LIGHT, 3, 2, 2, null, 4, 3, 3, Title.Bravo_Fighter, Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setLore("Flown by an unknown pilot who likes to spin a lot. Played the deciding role in the attack against the Droid Control Ship.");
        setGameText("Permanent pilot provides ability of 3. During your control phase, if landed aboard Blockade Flagship, may draw one destiny. If destiny > 6, place Bravo Fighter in your Used Pile; Flagship 'blown away.'");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER);
        addKeywords(Keyword.BRAVO_SQUADRON);
        addModelType(ModelType.N_1_STARFIGHTER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(3) {});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canDrawDestiny(game, playerId)
                && GameConditions.isLandedAboard(game, self, Persona.BLOCKADE_FLAGSHIP)) {
            final PhysicalCard flagship = Filters.findFirstActive(game, self, Filters.Blockade_Flagship);
            if (flagship != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Attempt to 'blow away' " + GameUtils.getFullName(flagship));
                action.setActionMsg("Attempt to 'blow away' " + GameUtils.getCardLink(flagship));
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId, 1) {
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                GameState gameState = game.getGameState();
                                if (totalDestiny == null) {
                                    gameState.sendMessage("Result: No result due to failed destiny draw");
                                    return;
                                }
                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                float attemptTotal = game.getModifiersQuerying().getBlowAwayBlockadeFlagshipAttemptTotal(gameState, totalDestiny);
                                gameState.sendMessage("Attempt total: " + GuiUtils.formatAsString(attemptTotal));

                                if (attemptTotal > 6) {
                                    gameState.sendMessage("Result: Succeeded");
                                    action.appendEffect(
                                            new PlaceCardInUsedPileFromTableEffect(action, self));
                                    action.appendEffect(
                                            new BlowAwayEffect(action, flagship));
                                }
                                else {
                                    gameState.sendMessage("Result: Failed");
                                }
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
