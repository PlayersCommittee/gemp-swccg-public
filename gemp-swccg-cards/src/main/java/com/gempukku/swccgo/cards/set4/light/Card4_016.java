package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceFromInsertCardEffect;
import com.gempukku.swccgo.logic.effects.LoseInsertCardEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Effect
 * Title: Anger, Fear, Aggression
 */
public class Card4_016 extends AbstractNormalEffect {
    public Card4_016() {
        super(Side.LIGHT, 4, PlayCardZoneOption.OPPONENTS_RESERVE_DECK, Title.Anger_Fear_Aggression, Uniqueness.UNIQUE);
        setLore("'The dark side of the Force are they. Easily they flow, quick to join you in a fight.'");
        setGameText("'Insert' in opponent's Reserve Deck. When Effect reaches top it is lost, but opponent must initiate a battle by the end of opponent's next battle phase or lose 4 Force. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextInsertCardRevealed(SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);
        final int cardId = self.getCardId();

        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Reveal 'insert' card");
        action.setActionMsg(null);
        // Perform result(s)
        action.appendEffect(
                new LoseInsertCardEffect(action, self));
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        self.setForRemainderOfGameData(cardId, new ForRemainderOfGameData(false));
                    }
                }
        );
        final int permCardId = self.getPermanentCardId();
        action.appendEffect(
                new AddUntilEndOfPlayersNextTurnActionProxyEffect(action, new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        List<TriggerAction> actions = new LinkedList<TriggerAction>();
                        final PhysicalCard self = game.findCardByPermanentId(permCardId);

                        if (self.getForRemainderOfGameData().get(cardId) != null) {
                            // Check condition(s)
                            // Start watching for initiated battle by end of this battle phase
                            if (TriggerConditions.isStartOfOpponentsPhase(game, effectResult, Phase.BATTLE, playerId)) {
                                self.setForRemainderOfGameData(cardId, new ForRemainderOfGameData(true));
                            }
                            // Check condition(s)
                            // At end of battle phase being watched, if battle was not initiated, opponent must Lose 4 force
                            else if (TriggerConditions.isEndOfOpponentsPhase(game, effectResult, Phase.BATTLE, playerId)) {
                                if (self.getForRemainderOfGameData().get(cardId).getBooleanValue()) {

                                    RequiredGameTextTriggerAction action1 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                    action1.setText("Make " + opponent + " lose 4 Force");
                                    // Perform result(s)
                                    action1.appendEffect(
                                            new PassthruEffect(action1) {
                                                @Override
                                                protected void doPlayEffect(SwccgGame game) {
                                                    // Clear game data so action proxy doesn't trigger again
                                                    self.setForRemainderOfGameData(cardId, null);
                                                }
                                            }
                                    );
                                    action1.appendEffect(
                                            new LoseForceFromInsertCardEffect(action1, opponent, 4));
                                    actions.add(action1);
                                }
                            }
                            // Check condition(s)
                            // Check if battle was initiated, which satisfies requirements of the card
                            else if (TriggerConditions.battleInitiated(game, effectResult, opponent)) {
                                self.setForRemainderOfGameData(cardId, null);
                            }
                        }
                        return actions;
                    }
                }, opponent));
        return action;
    }
}