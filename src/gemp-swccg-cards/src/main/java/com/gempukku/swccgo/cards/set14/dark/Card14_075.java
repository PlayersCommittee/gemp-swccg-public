package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DrawsBattleDestinyIfUnableToOtherwiseUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;

import java.util.Collections;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Alien
 * Title: Bok Askol
 */
public class Card14_075 extends AbstractAlien {
    public Card14_075() {
        super(Side.DARK, 3, 2, 1, 2, 4, "Bok Askol", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("At birth, a Pacithhips gene configuration usually indicates if they're to become a farmer, an intellectual, or a warrior. Bok Askol's destiny remained a mystery for years.");
        setGameText("Once during your deploy phase, may draw destiny: (0-2) no result; (3-4) Askol draws one battle destiny if unable to otherwise for remainder of turn; (5+) Askol is power +4 for remainder of turn.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        setSpecies(Species.PACITHHIP);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DEPLOY)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Draw destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            if (totalDestiny == null) {
                                gameState.sendMessage("Result: No result due to failed destiny draw");
                                return;
                            }

                            if (totalDestiny >= 3 && totalDestiny <= 4) {
                                gameState.sendMessage("Result: Destiny between 3 and 4");
                                action.appendEffect(
                                        new DrawsBattleDestinyIfUnableToOtherwiseUntilEndOfTurnEffect(action, 1,
                                                "Causes " +  GameUtils.getCardLink(self) + " to draw one battle destiny if unable to otherwise"));
                            }
                            else if (totalDestiny >= 5) {
                                gameState.sendMessage("Result: Destiny 5 or higher");
                                action.appendEffect(
                                        new ModifyPowerUntilEndOfTurnEffect(action, self, 4));
                            }
                            else {
                                gameState.sendMessage("Result: No result");
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
