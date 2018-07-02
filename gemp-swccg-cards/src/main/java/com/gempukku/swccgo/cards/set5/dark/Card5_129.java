package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.evaluators.AbilityEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.EachDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayPlayToInitiateEpicDuelModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.*;


/**
 * Set: Cloud City
 * Type: Epic Event
 * Title: Epic Duel
 */
public class Card5_129 extends AbstractEpicEventDeployable {
    public Card5_129() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Epic_Duel);
        setGameText("Deploy on your side of table. The Circle Is Now Complete or Vader's Obsession may be played to initiate an epic duel as follows: If Vader is present with specified target, each player draws two destiny (add 1 to each destiny draw if using a lightsaber). Add character's ability. If Vader loses, place him out of play. You lose 6 Force (9 if dueling Luke). If Obi-Wan loses, place him out of play. Opponent loses 9 Force. If Luke loses, opponent must choose: Cross Luke to the Dark Side and lose X Force, Where X = Luke's ability. OR Lose Luke and lose triple X Force.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayPlayToInitiateEpicDuelModifier(self, Filters.or(Filters.The_Circle_Is_Now_Complete, Filters.Vaders_Obsession)));
        return modifiers;
    }

    @Override
    public Map<PhysicalCard, Collection<PhysicalCard>> getInitiateEpicDuelMatchup(SwccgGame game, PhysicalCard self, Filter darkSideParticipantFilter, Filter lightSideParticipantFilter) {
        Map<PhysicalCard, Collection<PhysicalCard>> matchupsMap = new HashMap<PhysicalCard, Collection<PhysicalCard>>();
        Collection<PhysicalCard> darkSideParticipants = Filters.filterActive(game, self, darkSideParticipantFilter);
        for (PhysicalCard darkSideCharacter : darkSideParticipants) {
            Collection<PhysicalCard> lightSideParticipants = Filters.filterActive(game, self, null, TargetingReason.TO_BE_DUELED, Filters.and(lightSideParticipantFilter, Filters.presentWith(darkSideCharacter)));
            if (!lightSideParticipants.isEmpty()) {
                matchupsMap.put(darkSideCharacter, lightSideParticipants);
            }
        }
        return matchupsMap;
    }

    @Override
    public DuelDirections getDuelDirections(SwccgGame game) {
        return new DuelDirections() {
            @Override
            public boolean isEpicDuel() {
                return true;
            }

            @Override
            public boolean isCrossOverToDarkSideAttempt() {
                return true;
            }

            @Override
            public Evaluator getBaseDuelTotal(String playerId, DuelState duelState) {
                return new AbilityEvaluator(duelState.getCharacter(playerId));
            }

            @Override
            public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) {
                return 2;
            }

            @Override
            public void performDuelDirections(final Action duelAction, SwccgGame game, final DuelState duelState) {
                duelAction.appendEffect(
                        new DrawDestinyEffect(duelAction, game.getDarkPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getDarkPlayer()), DestinyType.DUEL_DESTINY) {
                            @Override
                            protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                if (Filters.armedWith(Filters.lightsaber).accepts(game, duelState.getCharacter(game.getDarkPlayer()))) {
                                    Modifier modifier = new EachDestinyModifier(duelAction.getActionSource(), drawDestinyState.getId(), 1);
                                    return Collections.singletonList(modifier);
                                }
                                return null;
                            }
                            @Override
                            protected void destinyDraws(SwccgGame game, final List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, final Float darkTotalDestiny) {
                                if (darkTotalDestiny != null) {
                                    duelState.increaseTotalDuelDestinyFromDraws(game.getDarkPlayer(), darkTotalDestiny, darkDestinyCardDraws.size());
                                }
                                duelAction.appendEffect(
                                        new DrawDestinyEffect(duelAction, game.getLightPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getLightPlayer()), DestinyType.DUEL_DESTINY) {
                                            @Override
                                            protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                if (Filters.armedWith(Filters.lightsaber).accepts(game, duelState.getCharacter(game.getLightPlayer()))) {
                                                    Modifier modifier = new EachDestinyModifier(duelAction.getActionSource(), drawDestinyState.getId(), 1);
                                                    return Collections.singletonList(modifier);
                                                }
                                                return null;
                                            }
                                            @Override
                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> lightDestinyCardDraws, List<Float> lightDestinyDrawValues, Float lightTotalDestiny) {
                                                if (lightTotalDestiny != null) {
                                                    duelState.increaseTotalDuelDestinyFromDraws(game.getLightPlayer(), lightTotalDestiny, lightDestinyCardDraws.size());
                                                }
                                            }
                                        }
                                );
                            }
                        }
                );
            }

            @Override
            public void performDuelResults(final Action action, SwccgGame game, DuelState duelState) {
                // If no loser, then nothing to do
                final String loser = duelState.getLoser();
                if (loser == null)
                    return;

                final PhysicalCard losingCharacter = duelState.getLosingCharacter();
                PhysicalCard winningCharacter = duelState.getWinningCharacter();

                // If Vader (or any dark side character) loses, place that character out of play. You lose 6 Force (9 if dueling Luke).
                if (loser.equals(game.getDarkPlayer())) {
                    action.appendEffect(
                            new PlaceCardOutOfPlayFromTableEffect(action, losingCharacter));
                    int forceToLose = Filters.Luke.accepts(game.getGameState(), game.getModifiersQuerying(), winningCharacter) ? 9 : 6;
                    action.appendEffect(
                            new LoseForceEffect(action, loser, forceToLose));
                }
                else {
                    //
                    // If Obi-Wan loses, place him out of play. Opponent loses 9 Force.
                    if (Filters.ObiWan.accepts(game.getGameState(), game.getModifiersQuerying(), losingCharacter)) {
                        action.appendEffect(
                                new PlaceCardOutOfPlayFromTableEffect(action, losingCharacter));
                        action.appendEffect(
                                new LoseForceEffect(action, loser, 9));
                    }
                    // If Luke loses, opponent must choose: Cross Luke to the Dark Side and lose X Force, Where X = Luke's ability. OR Lose Luke and lose triple X Force.
                    else if (Filters.Luke.accepts(game.getGameState(), game.getModifiersQuerying(), losingCharacter)) {
                        final float valueOfX = game.getModifiersQuerying().getAbility(game.getGameState(), losingCharacter);
                        final float tripleValueOfX = 3 * valueOfX;
                        action.appendEffect(
                                new PlayoutDecisionEffect(action, loser,
                                        new MultipleChoiceAwaitingDecision("Choose result", new String[]{"Cross Luke to the Dark Side and lose " + valueOfX + " Force", "Lose Luke and lose " + tripleValueOfX + " Force"}) {
                                            @Override
                                            protected void validDecisionMade(int index, String result) {
                                                if (index == 0) {
                                                    action.appendEffect(
                                                            new SendMessageEffect(action, loser + " chooses to cross Luke to the Dark Side and lose " + GuiUtils.formatAsString(valueOfX) + " Force"));
                                                    action.appendEffect(
                                                            new CrossOverCharacterEffect(action, losingCharacter));
                                                    action.appendEffect(
                                                            new LoseForceEffect(action, loser, valueOfX));
                                                }
                                                else {
                                                    action.appendEffect(
                                                            new SendMessageEffect(action, loser + " chooses to lose Luke and lose " + GuiUtils.formatAsString(tripleValueOfX) + " Force"));
                                                    action.appendEffect(
                                                            new LoseCardFromTableEffect(action, losingCharacter));
                                                    action.appendEffect(
                                                            new LoseForceEffect(action, loser, tripleValueOfX));
                                                }
                                            }
                                        }));
                    }
                }
            }
        };
    }
}