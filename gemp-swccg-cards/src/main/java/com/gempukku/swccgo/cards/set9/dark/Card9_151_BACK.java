package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.AbilityEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
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
 * Set: Death Star II
 * Type: Objective
 * Title: Take Your Father's Place
 */
public class Card9_151_BACK extends AbstractObjective {
    public Card9_151_BACK() {
        super(Side.DARK, 7, Title.Take_Your_Fathers_Place);
        setGameText("While this side up, lose 1 Force at end of each of your turns. Once during each of your turns, when Vader, Luke (even as a non-frozen captive) and Emperor are all present at your Throne Room, you may initiate a Luke/Vader duel: Each player draws two destiny. Add ability. Highest total wins. If Vader wins, opponent loses 3 Force. If Luke wins, shuffle Reserve Deck and draw destiny; if destiny > 12, Luke crosses to Dark Side, totally depleting opponent's Life Force. Flip if Luke neither present with Vader nor a captive.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();
        boolean targetsLeiaInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE);
        boolean targetsKananInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_KANAN_INSTEAD_OF_LUKE);

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

        if (targetsLeiaInsteadOfLuke) {
            // Check condition(s)
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.canBeFlipped(game, self)
                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE,
                    Filters.and(Filters.Leia, Filters.or(Filters.captive, Filters.presentWith(self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Vader))))) {

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
        else if (targetsKananInsteadOfLuke) {
            // Check condition(s)
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.canBeFlipped(game, self)
                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE,
                    Filters.and(Filters.Kanan, Filters.or(Filters.captive, Filters.presentWith(self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Vader))))) {

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
                    Filters.and(Filters.Luke, Filters.or(Filters.captive, Filters.presentWith(self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Vader))))) {

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
        boolean targetsLeiaInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE);
        boolean targetsKananInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_KANAN_INSTEAD_OF_LUKE);

        GameTextActionId gameTextActionId = GameTextActionId.BRING_HIM_BEFORE_ME__DOWNLOAD_EMPEROR;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.SIDIOUS)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Emperor from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Emperor, -2, true));
            actions.add(action);
        }

        final String opponent = game.getOpponent(playerId);
        Filter presentAtThroneRoom = Filters.presentAt(Filters.Throne_Room);

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (targetsLeiaInsteadOfLuke) {
            // Check condition(s)
            if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Emperor, presentAtThroneRoom))) {
                PhysicalCard vader = Filters.findFirstActive(game, self, Filters.and(Filters.Vader, presentAtThroneRoom));
                if (vader != null) {
                    final PhysicalCard leia = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE,
                            Filters.and(Filters.Leia, Filters.not(Filters.frozenCaptive), presentAtThroneRoom, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_DUELED)));
                    if (leia != null) {

                        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                        action.setText("Initiate a Leia/Vader duel");
                        action.addAnimationGroup(leia, vader);
                        // Update usage limit(s)
                        action.appendUsage(
                                new OncePerTurnEffect(action));
                        // Perform result(s)
                        action.appendEffect(
                                new DuelEffect(action, vader, leia, new DuelDirections() {
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

                                        // If Vader wins, opponent loses 3 Force.
                                        if (Filters.Vader.accepts(game, winningCharacter)) {
                                            action.appendEffect(
                                                    new LoseForceEffect(action, opponent, 3));
                                        }
                                        // If Leia wins, shuffle Reserve Deck and draw destiny.
                                        else if (Filters.Leia.accepts(game, winningCharacter)) {
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
                                                            float crossoverAttemptTotal = modifiersQuerying.getCrossoverAttemptTotal(gameState, leia, totalDestiny);
                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(crossoverAttemptTotal));
                                                            if (crossoverAttemptTotal > 12) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new CrossOverCharacterEffect(action, leia));
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
        else if (targetsKananInsteadOfLuke) {
            // Check condition(s)
            if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Emperor, presentAtThroneRoom))) {
                PhysicalCard vader = Filters.findFirstActive(game, self, Filters.and(Filters.Vader, presentAtThroneRoom));
                if (vader != null) {
                    final PhysicalCard kanan = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE,
                            Filters.and(Filters.Kanan, Filters.not(Filters.frozenCaptive), presentAtThroneRoom, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_DUELED)));
                    if (kanan != null) {

                        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                        action.setText("Initiate a Kanan/Vader duel");
                        action.addAnimationGroup(kanan, vader);
                        // Update usage limit(s)
                        action.appendUsage(
                                new OncePerTurnEffect(action));
                        // Perform result(s)
                        action.appendEffect(
                                new DuelEffect(action, vader, kanan, new DuelDirections() {
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

                                        // If Vader wins, opponent loses 3 Force.
                                        if (Filters.Vader.accepts(game, winningCharacter)) {
                                            action.appendEffect(
                                                    new LoseForceEffect(action, opponent, 3));
                                        }
                                        // If Kanan wins, shuffle Reserve Deck and draw destiny.
                                        else if (Filters.Kanan.accepts(game, winningCharacter)) {
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
                                                            float crossoverAttemptTotal = modifiersQuerying.getCrossoverAttemptTotal(gameState, kanan, totalDestiny);
                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(crossoverAttemptTotal));
                                                            if (crossoverAttemptTotal > 12) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new CrossOverCharacterEffect(action, kanan));
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
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Emperor, presentAtThroneRoom))) {
                PhysicalCard vader = Filters.findFirstActive(game, self, Filters.and(Filters.Vader, presentAtThroneRoom));
                if (vader != null) {
                    final PhysicalCard luke = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE,
                            Filters.and(Filters.Luke, Filters.not(Filters.frozenCaptive), presentAtThroneRoom, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_DUELED)));
                    if (luke != null) {

                        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                        action.setText("Initiate a Luke/Vader duel");
                        action.addAnimationGroup(luke, vader);
                        // Update usage limit(s)
                        action.appendUsage(
                                new OncePerTurnEffect(action));
                        // Perform result(s)
                        action.appendEffect(
                                new DuelEffect(action, vader, luke, new DuelDirections() {
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

                                        // If Vader wins, opponent loses 3 Force.
                                        if (Filters.Vader.accepts(game, winningCharacter)) {
                                            action.appendEffect(
                                                    new LoseForceEffect(action, opponent, 3));
                                        }
                                        // If Luke wins, shuffle Reserve Deck and draw destiny.
                                        else if (Filters.Luke.accepts(game, winningCharacter)) {
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
                                                            float crossoverAttemptTotal = modifiersQuerying.getCrossoverAttemptTotal(gameState, luke, totalDestiny);
                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(crossoverAttemptTotal));
                                                            if (crossoverAttemptTotal > 12) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new CrossOverCharacterEffect(action, luke));
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
        boolean targetsLeiaInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE);
        boolean targetsKananInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_KANAN_INSTEAD_OF_LUKE);

        GameTextActionId gameTextActionId = GameTextActionId.BRING_HIM_BEFORE_ME__DOWNLOAD_LUKE;

        if (targetsLeiaInsteadOfLuke) {
            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.LEIA)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Leia from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.Leia, -2, true));
                actions.add(action);
            }

            gameTextActionId = GameTextActionId.BRING_HIM_BEFORE_ME__DOWNLOAD_LUKE_FROM_LOST_PILE;

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.LEIA)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Leia from Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.Leia, true));
                actions.add(action);
            }

            return actions;
        } else if (targetsKananInsteadOfLuke) {
            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.KANAN)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Kanan from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.Kanan, -2, true));
                actions.add(action);
            }

            gameTextActionId = GameTextActionId.BRING_HIM_BEFORE_ME__DOWNLOAD_LUKE_FROM_LOST_PILE;

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.LEIA)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Kanan from Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.Kanan, true));
                actions.add(action);
            }

            return actions;
        }
        else {
            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.LUKE)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Luke from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.Luke, -2, true));
                actions.add(action);
            }

            gameTextActionId = GameTextActionId.BRING_HIM_BEFORE_ME__DOWNLOAD_LUKE_FROM_LOST_PILE;

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.LUKE)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Luke from Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.Luke, true));
                actions.add(action);
            }

            return actions;
        }
    }
}