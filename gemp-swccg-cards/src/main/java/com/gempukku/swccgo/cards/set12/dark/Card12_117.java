package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleActionProxyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ResetDestinyEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Rune Haako
 */
public class Card12_117 extends AbstractRepublic {
    public Card12_117() {
        super(Side.DARK, 2, 3, 2, 3, 6, "Rune Haako", Uniqueness.UNIQUE);
        setLore("Trade Federation settlement officer serving as legal council to Viceroy Gunray. Reputed to have one of the sharpest legal minds in all of the Republic. Neimoidian.");
        setGameText("While in battle, may choose one number. If next card opponent draws for destiny this battle has a printed destiny number matching this choice, that destiny is reduced to zero. If you just initiated a battle at same or adjacent site, retrieve 1 Force.");
        addPersona(Persona.HAAKO);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        setSpecies(Species.NEIMOIDIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Choose a number");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new MultipleChoiceAwaitingDecision("Choose number", new String[]{"0", "1", "2", "3", "π", "4", "5", "6", "2π", "7"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    final float chosenDestiny;
                                    if(result.equalsIgnoreCase("π")){
                                        chosenDestiny = (float) Math.PI;
                                    }
                                    else if(result.equalsIgnoreCase("2π")){
                                        chosenDestiny = 2 * (float) Math.PI;
                                    }else{
                                        chosenDestiny = Float.parseFloat(result);
                                    }
                                    self.clearForRemainderOfGameData();
                                    game.getGameState().sendMessage(playerId + " chooses " + GuiUtils.formatAsString(chosenDestiny) + " as number");
                                    final int permCardId = self.getPermanentCardId();
                                    action.appendEffect(
                                            new AddUntilEndOfBattleActionProxyEffect(action,
                                                    new AbstractActionProxy() {
                                                        @Override
                                                        public List<TriggerAction> getRequiredAfterTriggers(SwccgGame swccgGame, EffectResult effectResult) {
                                                            List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                            final PhysicalCard self = game.findCardByPermanentId(permCardId);
                                                            GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_2;

                                                            // Check condition(s)
                                                            if (TriggerConditions.isDestinyJustDrawnBy(swccgGame, effectResult, game.getOpponent(playerId), true)
                                                                    && !GameConditions.cardHasAnyForRemainderOfGameDataSet(self)) {

                                                                // Check more condition(s)
                                                                DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
                                                                PhysicalCard drawnCard = destinyDrawnResult.getCard();
                                                                if (drawnCard != null
                                                                        && drawnCard.getDestinyValueToUse().equals(chosenDestiny)) {
                                                                    final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId2);
                                                                     action2.setText("Reset destiny to 0");
                                                                     // Perform result(s)
                                                                     action2.appendEffect(
                                                                             new ResetDestinyEffect(action2, 0));
                                                                    actions.add(action2);
                                                                }
                                                                self.setForRemainderOfGameData(self.getCardId(), new ForRemainderOfGameData());
                                                            }
                                                            return actions;
                                                        }
                                                    }
                                            ));
                                }
                            }
                    )
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerId, Filters.sameOrAdjacentSite(self))) {
            final PhysicalCard battleSite = game.getGameState().getBattleLocation();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1) {
                        @Override
                        public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                            return Collections.singletonList(battleSite);
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
