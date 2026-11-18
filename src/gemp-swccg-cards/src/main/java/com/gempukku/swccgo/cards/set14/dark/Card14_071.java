package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckAndLoseTheRestEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Droid
 * Title: 3B3-21
 */
public class Card14_071 extends AbstractDroid {
    public Card14_071() {
        super(Side.DARK, 2, 3, 3, 3, "3B3-21", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setArmor(4);
        setLore("Infantry battle droid equipped with command programs to call reinforcements into his patrol zone when needed. Assigned to escort Amidala soon after her capture.");
        setGameText("If opponent just initiated a battle at same site, may use X Force to reveal the top X cards of your Reserve Deck. (maximum 4). Any battle droids revealed this way may deploy for free; all other cards are lost.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PRESENCE);
        addKeywords(Keyword.INFANTRY_BATTLE_DROID);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, game.getOpponent(playerId), Filters.sameSite(self))
                && GameConditions.hasReserveDeck(game, playerId)) {
            final int maxX = Math.min(4, Math.min(
                    GameConditions.forceAvailableToUse(game, playerId),
                    game.getGameState().getReserveDeckSize(playerId)));
            if (maxX > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Reveal cards from Reserve Deck");
                // Pay cost(s) — choose X (1..max), use X Force, then reveal X / deploy battle droids / lose rest
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to use ", 1, maxX, maxX) {
                                    @Override
                                    public void decisionMade(final int x) throws DecisionResultInvalidException {
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, x));
                                        action.setActionMsg("Reveal top " + x + " card" + (x == 1 ? "" : "s") + " of Reserve Deck");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new RevealTopCardsOfReserveDeckEffect(action, playerId, x) {
                                                    @Override
                                                    protected void cardsRevealed(List<PhysicalCard> cards) {
                                                        action.appendEffect(
                                                                new DeployCardsFromReserveDeckAndLoseTheRestEffect(action, cards,
                                                                        Filters.battle_droid, true));
                                                    }
                                                }
                                        );
                                    }
                                }
                        )
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
