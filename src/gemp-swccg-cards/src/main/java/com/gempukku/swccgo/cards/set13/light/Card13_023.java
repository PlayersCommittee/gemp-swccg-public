package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealRandomCardInOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Character
 * Subtype: Republic
 * Title: Horace Vancil
 */
public class Card13_023 extends AbstractRepublic {
    public Card13_023() {
        super(Side.LIGHT, 3, 2, 2, 2, 5, "Horace Vancil", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Part of Amidala's advisory council, Horace monitors political crises, as well as providing the Queen with information regarding the current economic state of the city.");
        setGameText("Deploys -1 on Naboo. While at Theed Palace Throne Room, once during your control phase may reveal one card (random selection) from opponent's hand. Opponent must place card in Used Pile or lose 1 Force.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Deploys_on_Naboo));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.isAtLocation(game, self, Filters.Theed_Palace_Throne_Room)
                && GameConditions.hasHand(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Reveal card from opponent's hand");
            action.setActionMsg("Reveal a random card from opponent's hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RevealRandomCardInOpponentsHandEffect(action, playerId) {
                        @Override
                        protected void cardRevealed(final PhysicalCard revealedCard) {
                            if (Filters.inHand(opponent).accepts(game.getGameState(), game.getModifiersQuerying(), revealedCard)) {

                                if (game.getModifiersQuerying().mayNotRemoveCardsFromOpponentsHand(game.getGameState(), self, playerId)) {
                                    game.getGameState().sendMessage(opponent + " may not choose to place " + GameUtils.getCardLink(revealedCard) + " in Used Pile");
                                    action.appendEffect(
                                            new LoseForceEffect(action, opponent, 1, true));
                                } else {

                                    action.appendEffect(
                                            new PlayoutDecisionEffect(action, opponent,
                                                    new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Place " + GameUtils.getFullName(revealedCard) + " in Used Pile", "Lose 1 Force"}) {
                                                        @Override
                                                        protected void validDecisionMade(int index, String result) {
                                                            if (index == 0) {
                                                                game.getGameState().sendMessage(opponent + " chooses to place " + GameUtils.getCardLink(revealedCard) + " in Used Pile");
                                                                action.appendEffect(
                                                                        new PutCardFromHandOnUsedPileEffect(action, opponent, revealedCard, false));
                                                            } else {
                                                                game.getGameState().sendMessage(opponent + " chooses to lose 1 Force");
                                                                action.appendEffect(
                                                                        new LoseForceEffect(action, opponent, 1, true));
                                                            }
                                                        }
                                                    }
                                            )
                                    );
                                }
                            }
                            else {
                                game.getGameState().sendMessage("Losing 1 Force is the only available choice");
                                action.appendEffect(
                                        new LoseForceEffect(action, opponent, 1));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
