package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.AbilityEvaluator;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CrossOverCharacterEffect;
import com.gempukku.swccgo.logic.effects.DepleteLifeForceEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Objective
 * Title: Hostile Takeover/Usurped
 */
public class Card304_122_BACK extends AbstractObjective {
    public Card304_122_BACK() {
        super(Side.DARK, 7, Title.Usurped, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setGameText("While this side up, lose 1 Force at end of each of your turns. Once during each of your turns, when Kamjin, Locita (even as a non-frozen captive) and Kamjin are all present at your Throne Room, you may initiate a Locita/Kamjin duel: Each player draws two destiny. Add ability. Highest total wins. If Kamjin wins, opponent loses 3 Force. If Locita wins, shuffle Reserve Deck and draw destiny; if destiny > 12, Locita crosses to Dark Side, totally depleting opponent's Life Force. Flip if Locita neither present with Kamjin nor a captive.");
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();
        boolean targetsKaiInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
        boolean targetsHikaruInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType. HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isEndOfYourTurn(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, playerId, 1));
            actions.add(action);
        }

        if (targetsKaiInsteadOfLocita) {
            // Check condition(s)
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.canBeFlipped(game, self)
                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE,
                    Filters.and(Filters.Kai, Filters.or(Filters.captive, Filters.presentWith(self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Kamjin))))) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                actions.add(action);
            }
            return actions;
        }
        else if (targetsHikaruInsteadOfLocita) {
            // Check condition(s)
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.canBeFlipped(game, self)
                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE,
                    Filters.and(Filters.Hikaru, Filters.or(Filters.captive, Filters.presentWith(self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Kamjin))))) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                actions.add(action);
            }
            return actions;
        }
        else {
            // Check condition(s)
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.canBeFlipped(game, self)
                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE,
                    Filters.and(Filters.Locita, Filters.or(Filters.captive, Filters.presentWith(self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Kamjin))))) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                actions.add(action);
            }
            return actions;
        }
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        boolean targetsKaiInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
        boolean targetsHikaruInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType. HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);

        GameTextActionId gameTextActionId = GameTextActionId.HOSTILE_TAKEOVER__DOWNLOAD_THRAN;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.THRAN)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Thran from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Thran, -2, true));
            actions.add(action);
        }

        final String opponent = game.getOpponent(playerId);
        Filter presentAtThroneRoom = Filters.presentAt(Filters.Throne_Room);

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (targetsKaiInsteadOfLocita) {
            // Check condition(s)
            if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Thran, presentAtThroneRoom))) {
                PhysicalCard kamjin = Filters.findFirstActive(game, self, Filters.and(Filters.Kamjin, presentAtThroneRoom));
                if (kamjin != null) {
                    final PhysicalCard kai = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE,
                            Filters.and(Filters.Kai, Filters.not(Filters.frozenCaptive), presentAtThroneRoom, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_DUELED)));
                    if (kai != null) {

                        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                        action.setText("Initiate a Kai/Kamjin duel");
                        action.addAnimationGroup(kai, kamjin);
                        // Update usage limit(s)
                        action.appendUsage(
                                new OncePerTurnEffect(action));
                        // Perform result(s)
                        action.appendEffect(
                                new DuelEffect(action, kamjin, kai, new DuelDirections() {
                                    @Override
                                    public boolean isEpicDuel() {
                                        return false;
                                    }

                                    @Override
                                    public boolean isCrossOverToDarkSideAttempt() {
                                        return false;
                                    }

                                    @Override
                                    public Evaluator getBaseDuelTotal(final String playerId, final DuelState duelState) {
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
                                                    protected void destinyDraws(SwccgGame game, final List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, final Float darkTotalDestiny) {
                                                        if (darkTotalDestiny != null) {
                                                            duelState.increaseTotalDuelDestinyFromDraws(game.getDarkPlayer(), darkTotalDestiny, darkDestinyCardDraws.size());
                                                        }
                                                        duelAction.appendEffect(
                                                                new DrawDestinyEffect(duelAction, game.getLightPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getLightPlayer()), DestinyType.DUEL_DESTINY) {
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
                                    public void performDuelResults(Action duelAction, SwccgGame game, DuelState duelState) {
                                        PhysicalCard winningCharacter = duelState.getWinningCharacter();
                                        if (winningCharacter == null) {
                                            return;
                                        }

                                        // If Kamjin wins, opponent loses 3 Force.
                                        if (Filters.Kamjin.accepts(game, winningCharacter)) {
                                            action.appendEffect(
                                                    new LoseForceEffect(action, opponent, 3));
                                        }
                                        // If Kai wins, shuffle Reserve Deck and draw destiny.
                                        else if (Filters.Kai.accepts(game, winningCharacter)) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ShuffleReserveDeckEffect(action));
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }
                                                            float crossoverAttemptTotal = modifiersQuerying.getCrossoverAttemptTotal(gameState, kai, totalDestiny);
                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(crossoverAttemptTotal));
                                                            if (crossoverAttemptTotal > 12) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new CrossOverCharacterEffect(action, kai));
                                                                action.appendEffect(
                                                                        new DepleteLifeForceEffect(action, opponent));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                                })
                        );
                        actions.add(action);
                    }
                }
            }

            return actions;
        }
        else if (targetsHikaruInsteadOfLocita) {
            // Check condition(s)
            if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Thran, presentAtThroneRoom))) {
                PhysicalCard kamjin = Filters.findFirstActive(game, self, Filters.and(Filters.Kamjin, presentAtThroneRoom));
                if (kamjin != null) {
                    final PhysicalCard hikaru = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE,
                            Filters.and(Filters.Hikaru, Filters.not(Filters.frozenCaptive), presentAtThroneRoom, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_DUELED)));
                    if (hikaru != null) {

                        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                        action.setText("Initiate a Hikaru/Kamjin duel");
                        action.addAnimationGroup(hikaru, kamjin);
                        // Update usage limit(s)
                        action.appendUsage(
                                new OncePerTurnEffect(action));
                        // Perform result(s)
                        action.appendEffect(
                                new DuelEffect(action, kamjin, hikaru, new DuelDirections() {
                                    @Override
                                    public boolean isEpicDuel() {
                                        return false;
                                    }

                                    @Override
                                    public boolean isCrossOverToDarkSideAttempt() {
                                        return false;
                                    }

                                    @Override
                                    public Evaluator getBaseDuelTotal(final String playerId, final DuelState duelState) {
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
                                                    protected void destinyDraws(SwccgGame game, final List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, final Float darkTotalDestiny) {
                                                        if (darkTotalDestiny != null) {
                                                            duelState.increaseTotalDuelDestinyFromDraws(game.getDarkPlayer(), darkTotalDestiny, darkDestinyCardDraws.size());
                                                        }
                                                        duelAction.appendEffect(
                                                                new DrawDestinyEffect(duelAction, game.getLightPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getLightPlayer()), DestinyType.DUEL_DESTINY) {
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
                                    public void performDuelResults(Action duelAction, SwccgGame game, DuelState duelState) {
                                        PhysicalCard winningCharacter = duelState.getWinningCharacter();
                                        if (winningCharacter == null) {
                                            return;
                                        }

                                        // If Kamjin wins, opponent loses 3 Force.
                                        if (Filters.Kamjin.accepts(game, winningCharacter)) {
                                            action.appendEffect(
                                                    new LoseForceEffect(action, opponent, 3));
                                        }
                                        // If Hikaru wins, shuffle Reserve Deck and draw destiny.
                                        else if (Filters.Hikaru.accepts(game, winningCharacter)) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ShuffleReserveDeckEffect(action));
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }
                                                            float crossoverAttemptTotal = modifiersQuerying.getCrossoverAttemptTotal(gameState, hikaru, totalDestiny);
                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(crossoverAttemptTotal));
                                                            if (crossoverAttemptTotal > 12) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new CrossOverCharacterEffect(action, hikaru));
                                                                action.appendEffect(
                                                                        new DepleteLifeForceEffect(action, opponent));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                                })
                        );
                        actions.add(action);
                    }
                }
            }

            return actions;
        }
        else {
            // Check condition(s)
            if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Thran, presentAtThroneRoom))) {
                PhysicalCard kamjin = Filters.findFirstActive(game, self, Filters.and(Filters.Kamjin, presentAtThroneRoom));
                if (kamjin != null) {
                    final PhysicalCard locita = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE,
                            Filters.and(Filters.Locita, Filters.not(Filters.frozenCaptive), presentAtThroneRoom, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_DUELED)));
                    if (locita != null) {

                        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                        action.setText("Initiate a Locita/Kamjin duel");
                        action.addAnimationGroup(locita, kamjin);
                        // Update usage limit(s)
                        action.appendUsage(
                                new OncePerTurnEffect(action));
                        // Perform result(s)
                        action.appendEffect(
                                new DuelEffect(action, kamjin, locita, new DuelDirections() {
                                    @Override
                                    public boolean isEpicDuel() {
                                        return false;
                                    }

                                    @Override
                                    public boolean isCrossOverToDarkSideAttempt() {
                                        return false;
                                    }

                                    @Override
                                    public Evaluator getBaseDuelTotal(final String playerId, final DuelState duelState) {
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
                                                    protected void destinyDraws(SwccgGame game, final List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, final Float darkTotalDestiny) {
                                                        if (darkTotalDestiny != null) {
                                                            duelState.increaseTotalDuelDestinyFromDraws(game.getDarkPlayer(), darkTotalDestiny, darkDestinyCardDraws.size());
                                                        }
                                                        duelAction.appendEffect(
                                                                new DrawDestinyEffect(duelAction, game.getLightPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getLightPlayer()), DestinyType.DUEL_DESTINY) {
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
                                    public void performDuelResults(Action duelAction, SwccgGame game, DuelState duelState) {
                                        PhysicalCard winningCharacter = duelState.getWinningCharacter();
                                        if (winningCharacter == null) {
                                            return;
                                        }

                                        // If Kamjin wins, opponent loses 3 Force.
                                        if (Filters.Kamjin.accepts(game, winningCharacter)) {
                                            action.appendEffect(
                                                    new LoseForceEffect(action, opponent, 3));
                                        }
                                        // If Locita wins, shuffle Reserve Deck and draw destiny.
                                        else if (Filters.Locita.accepts(game, winningCharacter)) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ShuffleReserveDeckEffect(action));
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }
                                                            float crossoverAttemptTotal = modifiersQuerying.getCrossoverAttemptTotal(gameState, locita, totalDestiny);
                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(crossoverAttemptTotal));
                                                            if (crossoverAttemptTotal > 12) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new CrossOverCharacterEffect(action, locita));
                                                                action.appendEffect(
                                                                        new DepleteLifeForceEffect(action, opponent));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                                })
                        );
                        actions.add(action);
                    }
                }
            }

            return actions;
        }
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        boolean targetsKaiInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
        boolean targetsHikaruInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);

        GameTextActionId gameTextActionId = GameTextActionId.HOSTILE_TAKEOVER__DOWNLOAD_LOCITA;

        if (targetsKaiInsteadOfLocita) {
            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.KAI)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Kai from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.Kai, -2, true));
                actions.add(action);
            }

            gameTextActionId = GameTextActionId.HOSTILE_TAKEOVER__DOWNLOAD_LOCITA_FROM_LOST_PILE;

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.KAI)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Kai from Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.Kai, true));
                actions.add(action);
            }

            return actions;
        } else if (targetsHikaruInsteadOfLocita) {
            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.HIKARU)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Hikaru from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.Hikaru, -2, true));
                actions.add(action);
            }

            gameTextActionId = GameTextActionId.HOSTILE_TAKEOVER__DOWNLOAD_LOCITA_FROM_LOST_PILE;

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.HIKARU)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Hikaru from Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.Hikaru, true));
                actions.add(action);
            }

            return actions;
        }
        else {
            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.LOCITA)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Locita from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.Locita, -2, true));
                actions.add(action);
            }

            gameTextActionId = GameTextActionId.HOSTILE_TAKEOVER__DOWNLOAD_LOCITA_FROM_LOST_PILE;

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.LOCITA)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Locita from Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.Locita, true));
                actions.add(action);
            }

            return actions;
        }
    }
}