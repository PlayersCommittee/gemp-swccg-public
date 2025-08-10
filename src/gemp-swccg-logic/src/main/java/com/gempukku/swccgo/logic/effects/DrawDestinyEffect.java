package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToCompleteDrawingDestinyResult;
import com.gempukku.swccgo.logic.timing.results.AboutToDrawDestinyCardResult;
import com.gempukku.swccgo.logic.timing.results.CostToDrawDestinyCardResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawCompleteResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;
import com.gempukku.swccgo.logic.timing.results.RaceDestinyStackedResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An effect that causes the specified player to draw destiny.
 */
public abstract class DrawDestinyEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private int _normalNumToDraw;
    private int _extraDrawsFromDrawX;
    private DestinyType _destinyType;
    private List<PhysicalCard> _destinyCardDraws = new ArrayList<PhysicalCard>();
    private List<Float> _destinyDrawValues = new ArrayList<Float>();
    private int _numSkippedSoFar;
    private int _numSkippedSoFarWithinDrawX;
    private int _numDrawnSoFar;
    private int _numDrawnSoFarAgainstLimit;
    private boolean _noMoreDestinyToDraw;
    private DrawDestinyEffect _drawDestinyEffect;
    private Float _substitutedDestiny;
    private boolean _substitutedDestinyInitially;
    private PhysicalCard _drawnDestinyCard;
    private Float _drawnDestinyValue;
    private float _drawnDestinyValueModification;
    private boolean _reset;
    private boolean _canceled;
    private boolean _redrawn;
    private boolean _skipped;
    private boolean _choseY;
    private boolean _costToDrawFailed;
    private boolean _costToDrawFailedWasChosenByPlayer;
    private Set<String> _mayNotBeCanceledByPlayer = new HashSet<String>();
    private int _drawX;
    private int _numDrawnSoFarWithinDrawX;
    private List<PhysicalCard> _drawXCardsToChooseFrom = new ArrayList<PhysicalCard>();
    private List<Float> _drawXValuesToChooseFrom = new ArrayList<Float>();
    private int _chooseY;
    private boolean _takeOtherIntoHand;
    private Map<String, Float> _modifierSourceTitleMap = new HashMap<>();

    /**
     * Creates an effect that causes the player to draw a destiny.
     * @param action the action performing this effect
     * @param playerId the player to draw destiny
     */
    public DrawDestinyEffect(Action action, String playerId) {
        this(action, playerId, 1);
    }

    /**
     * Creates an effect that causes the player to draw the specified number of destiny.
     * @param action the action performing this effect
     * @param playerId the player to draw destiny
     * @param numberOfDraws the number of destiny to draw
     */
    public DrawDestinyEffect(Action action, String playerId, int numberOfDraws) {
        this(action, playerId, numberOfDraws, 0, 0, false, DestinyType.DESTINY);
    }

    /**
     * Creates an effect that causes the player to draw the specified number of destiny (as draw X and choose Y).
     * @param action the action performing this effect
     * @param playerId the player to draw destiny
     * @param drawX the number of destiny to draw (for draw X and choose Y)
     * @param chooseY the number of destiny to choose (for draw X and choose Y)
     */
    public DrawDestinyEffect(Action action, String playerId, int drawX, int chooseY) {
        this(action, playerId, chooseY, drawX, chooseY, false, DestinyType.DESTINY);
    }

    /**
     * Creates an effect that causes the player to draw the specified number of destiny (as draw X and choose Y).
     * @param action the action performing this effect
     * @param playerId the player to draw destiny
     * @param drawX the number of destiny to draw (for draw X and choose Y)
     * @param chooseY the number of destiny to choose (for draw X and choose Y)
     * @param takeOtherIntoHand true if the destinies not chosen (for draw X and choose Y) are taken into hand, otherwise false
     */
    public DrawDestinyEffect(Action action, String playerId, int drawX, int chooseY, boolean takeOtherIntoHand) {
        this(action, playerId, chooseY, drawX, chooseY, takeOtherIntoHand, DestinyType.DESTINY);
    }

    /**
     * Creates an effect that causes the player to draw the specified number of destiny of the specified type.
     * @param action the action performing this effect
     * @param playerId the player to draw destiny
     * @param numberOfDraws the number of destiny to draw
     * @param type the type of destiny
     */
    public DrawDestinyEffect(Action action, String playerId, int numberOfDraws, DestinyType type) {
        this(action, playerId, numberOfDraws, 0, 0, false, type);
    }

    /**
     * Creates an effect that causes the player to draw the specified number of destiny of the specified type.
     * @param action the action performing this effect
     * @param playerId the player to draw destiny
     * @param drawX the number of destiny to draw (for draw X and choose Y)
     * @param chooseY the number of destiny to choose (for draw X and choose Y)
     * @param type the type of destiny
     */
    public DrawDestinyEffect(Action action, String playerId, int drawX, int chooseY, DestinyType type) {
        this(action, playerId, chooseY, drawX, chooseY, false, type);
    }

    /**
     * Creates an effect that causes the player to draw the specified number of destiny of the specified type.
     * @param action the action performing this effect
     * @param playerId the player to draw destiny
     * @param numberOfDraws the number of destiny to draw (not part of a draw X and choose Y))
     * @param drawX the number of destiny to draw (for draw X and choose Y)
     * @param chooseY the number of destiny to choose (for draw X and choose Y)
     * @param takeOtherIntoHand true if the destinies not chosen (for draw X and choose Y) are taken into hand, otherwise false
     * @param type the type of destiny
     */
    public DrawDestinyEffect(Action action, String playerId, int numberOfDraws, int drawX, int chooseY, boolean takeOtherIntoHand, DestinyType type) {
        super(action);
        _performingPlayerId = playerId;
        _normalNumToDraw = numberOfDraws;
        _drawX = drawX;
        _extraDrawsFromDrawX = (_drawX > 0 ? _drawX - chooseY : 0);
        _chooseY = chooseY;
        _takeOtherIntoHand = takeOtherIntoHand;
        _destinyType = type;
        _drawDestinyEffect = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    /**
     * Gets the player drawing destiny.
     * @return the player
     */
    public String getPlayerDrawingDestiny() {
        return _performingPlayerId;
    }

    /**
     * Gets the value of the current substitute destiny.
     * @return the value, or null if no substitute destiny
     */
    public Float getSubstituteDestiny() {
        return _substitutedDestiny;
    }

    /**
     * Set the value of the current substitute destiny.
     * @param value the value
     */
    public void setSubstituteDestiny(Float value) {
        _substitutedDestiny = value;
        if (value != null) {
            _drawnDestinyCard = null;
        }
    }

    /**
     * Gets the current drawn destiny card.
     * @return the current drawn destiny card
     */
    public PhysicalCard getDrawnDestinyCard() {
        return _drawnDestinyCard;
    }

    /**
     * Sets the current drawn destiny card for when destiny card is replaced by another card.
     * @param card the new drawn destiny card
     */
    public void setDrawnDestinyCard(PhysicalCard card) {
        _drawnDestinyCard = card;
    }

    /**
     * Gets the current drawn destiny value.
     * @return the current drawn destiny value
     */
    public Float getDestinyDrawValue() {
        return _drawnDestinyValue != null ? (_drawnDestinyValue + _drawnDestinyValueModification) : null;
    }

    /**
     * Sets the current drawn destiny card value for when destiny card is replaced by another card.
     * @param value the new drawn destiny card value
     */
    public void setDrawnDestinyCardValue(float value) {
        _drawnDestinyValue = value;
    }

    /**
     * Sets the specified player as not being able to cancel the destiny draw.
     * @param playerId the player
     */
    public void setPlayerMayNotCancelDestiny(String playerId) {
        _mayNotBeCanceledByPlayer.add(playerId);
    }

    /**
     * Determines if the current destiny draw is may not be canceled by the specified player.
     * @return true if may not be canceled, otherwise false
     */
    public boolean mayNotBeCanceledByPlayer(String playerId) {
        return _mayNotBeCanceledByPlayer.contains(playerId);
    }

    /**
     * Determines if the destiny draw may not be canceled unless being redrawn
     */
    public boolean mayNotBeCanceledUnlessBeingRedrawn() {
        return false;
    }

    /**
     * Sets the current destiny draw as canceled, and specifies if redrawn.
     * @param redrawn true if canceled and redrawn, otherwise just canceled
     */
    public void cancelDestiny(boolean redrawn) {
        _canceled = true;
        _redrawn = redrawn;
    }

    /**
     * Determines if the current destiny draw is canceled.
     * @return true if canceled, otherwise false
     */
    public boolean isDestinyCanceled() {
        return _canceled;
    }

    /**
     * Determines if the current destiny draw is canceled and redrawn.
     * @return true if canceled, otherwise false
     */
    public boolean isDestinyToBeRedrawn() {
        return _redrawn;
    }

    /**
     * Modifies the current destiny draw value by the specified amount.
     * @param amount the amount to modify
     */
    public void modifyDestiny(float amount) {
        modifyDestiny(null, null, amount, true);
    }

    /**
     * Modifies the current destiny draw value by the specified amount.
     * @param sourceTitles list of titles attempting to modify the destiny (to account for combo cards)
     * @param amount the amount to modify
     * @param cumulative true if modifier is cumulative, false otherwise
     */
    public void modifyDestiny(List<String> sourceTitles, GameTextActionId gameTextActionId, float amount, boolean cumulative) {
        if (sourceTitles == null) {
            _drawnDestinyValueModification += amount;
            return;
        }

        float previousAmount = 0;

        String gameTextActionIdString = (gameTextActionId==null?"":gameTextActionId.name());
        for (String title: sourceTitles) {
            if (_modifierSourceTitleMap.containsKey(title+gameTextActionIdString)) {
                float temp = _modifierSourceTitleMap.get(title+gameTextActionIdString);
                if (previousAmount == 0)
                    previousAmount = temp;
                else if (temp < 0 && previousAmount < 0 && temp < previousAmount)
                    previousAmount = temp;
                else if (temp > 0 && previousAmount > 0 && temp > previousAmount)
                    previousAmount = temp;
            }
        }

        if (cumulative) {
            _drawnDestinyValueModification += amount;
            for (String title:sourceTitles)
                _modifierSourceTitleMap.put(title+gameTextActionIdString, previousAmount + amount);
        } else if (previousAmount == 0) {
            _drawnDestinyValueModification += amount;
            for (String title:sourceTitles)
                _modifierSourceTitleMap.put(title+gameTextActionIdString, amount);
        } else if (amount < 0 && previousAmount < 0 && amount < previousAmount) {
            //subtract a larger amount
            _drawnDestinyValueModification += amount - previousAmount;
            for (String title:sourceTitles)
                _modifierSourceTitleMap.put(title+gameTextActionIdString, amount);
        } else if (amount > 0 && previousAmount > 0 && amount > previousAmount) {
            //add a larger amount
            _drawnDestinyValueModification += amount - previousAmount;
            for (String title:sourceTitles)
                _modifierSourceTitleMap.put(title+gameTextActionIdString, amount);
        }
        //this doesn't handle a +X and -Y from the same title
    }

    /**
     * Determines if the current destiny draw is reset.
     * @return true if reset, otherwise false
     */
    public boolean isDestinyReset() {
        return _reset;
    }

    /**
     * Resets the current destiny draw value to the specified value.
     * @param resetValue the amount to modify
     */
    public void resetDestiny(float resetValue) {
        _reset = true;
        _drawnDestinyValue = resetValue;
        _drawnDestinyValueModification = 0;
        _modifierSourceTitleMap.clear();
    }

    /**
     * Sets the cost to draw the current destiny card as failed.
     * @param chosenByPlayer true if failing the cost was chosen by the player, otherwise it was required
     */
    public void costToDrawCardFailed(boolean chosenByPlayer) {
        _costToDrawFailed = true;
        _costToDrawFailedWasChosenByPlayer = chosenByPlayer;
    }

    /**
     * Determines if the cost to draw the current destiny card failed.
     * @return true if cost failed, otherwise false
     */
    public boolean isCostToDrawCardFailed() {
        return _costToDrawFailed;
    }

    /**
     * Determines if the failing the cost cost to draw was chosen by the player.
     * @return true if chosen by player, otherwise it was required
     */
    public boolean wasCostToDrawCardFailedChosenByPlayer() {
        return _costToDrawFailedWasChosenByPlayer;
    }

    /**
     * Determines if draw should be skipped due to the cost to draw failing.
     * @return true if draw should be skipped, otherwise false
     */
    private boolean isSkipDrawDueToCostToDrawCardFailed(SwccgGame game) {
        return _costToDrawFailedWasChosenByPlayer
                || (_costToDrawFailed
                && ((_numDrawnSoFar + _numSkippedSoFar) >= game.getModifiersQuerying().getNumBattleDestinyDrawsIfUnableToOtherwise(game.getGameState(), _performingPlayerId)));
    }

    /**
     * Gets the destiny type.
     * @return the destiny type
     */
    public DestinyType getDestinyType() {
        return _destinyType;
    }

    /**
     * Sets the values for "draw X and choose Y"
     * @param drawX the X value
     * @param chooseY the Y value
     */
    public void setDrawXAndChooseY(Integer drawX, Integer chooseY) {
        if (isDrawAndChoose()) {
            throw new UnsupportedOperationException("Draw X and choose Y is already being performed");
        }
        if (_normalNumToDraw < 1 || drawX < 2 || chooseY < 1 || chooseY > drawX) {
            throw new UnsupportedOperationException("Draw X and choose Y called with invalid values");
        }
        _drawX = drawX;
        _extraDrawsFromDrawX += (drawX - chooseY);
        _chooseY = chooseY;
        _numDrawnSoFarWithinDrawX = 0;
        _numSkippedSoFarWithinDrawX = 0;
        _drawXCardsToChooseFrom.clear();
        _drawXValuesToChooseFrom.clear();
    }

    /**
     * Determines if a "draw X and choose Y" can be performed
     * @param game the game
     * @param drawX the X value
     * @return true or false
     */
    public boolean canDrawAndChoose(SwccgGame game, int drawX) {
        if (isDrawAndChoose())
            return false;

        if (getSubstituteDestiny() != null)
            return false;

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Get the maximum limit for number of destinies that can be drawn
        int maxAllowedToDraw = Integer.MAX_VALUE;
        if (_destinyType == DestinyType.BATTLE_DESTINY) {
            maxAllowedToDraw = Math.min(maxAllowedToDraw, modifiersQuerying.getNumBattleDestinyDraws(gameState, _performingPlayerId, true, false));
        }
        else if (_destinyType == DestinyType.DESTINY_TO_TOTAL_POWER) {
            maxAllowedToDraw = Math.min(maxAllowedToDraw, modifiersQuerying.getNumDestinyDrawsToTotalPowerOnly(gameState, _performingPlayerId, true, false));
        }
        else if (_destinyType == DestinyType.DESTINY_TO_ATTRITION) {
            maxAllowedToDraw = Math.min(maxAllowedToDraw, modifiersQuerying.getNumDestinyDrawsToAttritionOnly(gameState, _performingPlayerId, true, false));
        }

        return (_numDrawnSoFarAgainstLimit + _numSkippedSoFar + drawX) <= maxAllowedToDraw;
    }

    /**
     * Determines if performing a "draw X and choose Y"
     * @return true or false
     */
    public boolean isDrawAndChoose() {
        return _drawX > 0 && _chooseY > 0;
    }

    /**
     * Sets variables when draw X and choose Y is complete.
     */
    private void drawXAndChooseYComplete() {
        _drawX = 0;
        _chooseY = 0;
        _numDrawnSoFarWithinDrawX = 0;
        _numSkippedSoFarWithinDrawX = 0;
        _drawXCardsToChooseFrom.clear();
        _drawXValuesToChooseFrom.clear();
        _choseY = true;
    }

    /**
     * Gets the number of destiny drawn so far.
     * @return the number of destiny drawn so far
     */
    public int getNumDestinyDrawnSoFar() {
        return _numDrawnSoFar + _numSkippedSoFar;
    }

    /**
     * Gets the number of draws remaining.
     * @return the number of draws remaining
     */
    public int getNumDestinyDrawsRemaining() {
        return Math.max(0, _normalNumToDraw + _extraDrawsFromDrawX - _numDrawnSoFar - _numSkippedSoFar);
    }

    /**
     * Gets the total destiny.
     * @return the total destiny
     */
    public Float getTotalDestiny(SwccgGame game) {
        if (_destinyDrawValues.isEmpty())
            return null;

        final GameState gameState = game.getGameState();

        Float totalDestiny = (float) 0;
        for (Float value : _destinyDrawValues)
            totalDestiny += value;

        // Add modifiers to total weapon destiny (unless this is combined firing)
        if (_destinyType==DestinyType.WEAPON_DESTINY || _destinyType==DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY) {
            totalDestiny = game.getModifiersQuerying().getTotalWeaponDestiny(gameState, _performingPlayerId, totalDestiny);
        }
        else if (_destinyType==DestinyType.BATTLE_DESTINY) {
            totalDestiny = game.getModifiersQuerying().getTotalBattleDestiny(gameState, _performingPlayerId, totalDestiny);
        }
        else if (_destinyType==DestinyType.CARBON_FREEZING_DESTINY) {
            totalDestiny = game.getModifiersQuerying().getTotalCarbonFreezingDestiny(gameState, _performingPlayerId, totalDestiny);
        }
        else if (_destinyType==DestinyType.ASTEROID_DESTINY) {
            totalDestiny = game.getModifiersQuerying().getTotalAsteroidDestiny(gameState, _performingPlayerId, totalDestiny);
        }
        else if (_destinyType==DestinyType.MOVEMENT_DESTINY) {
            totalDestiny = game.getModifiersQuerying().getTotalMovementDestiny(gameState, _performingPlayerId, totalDestiny);
        }
        else if (_destinyType==DestinyType.DESTINY_TO_ATTRITION) {
            totalDestiny = game.getModifiersQuerying().getTotalDestinyToAttrition(gameState, _performingPlayerId, totalDestiny);
        }
        else if (_destinyType==DestinyType.DESTINY_TO_TOTAL_POWER) {
            totalDestiny = game.getModifiersQuerying().getTotalDestinyToPower(gameState, _performingPlayerId, totalDestiny);
        }
        else if (_destinyType==DestinyType.SEARCH_PARTY_DESTINY) {
            totalDestiny = game.getModifiersQuerying().getTotalSearchPartyDestiny(gameState, _performingPlayerId, totalDestiny);
        }
        else if (_destinyType==DestinyType.TRAINING_DESTINY) {
            totalDestiny = game.getModifiersQuerying().getTotalTrainingDestiny(gameState, _action.getActionSource(), totalDestiny);
        }
        else if (_destinyType==DestinyType.TRACTOR_BEAM_DESTINY && gameState.getUsingTractorBeamState() != null) {
            totalDestiny = game.getModifiersQuerying().getTotalTractorBeamDestiny(gameState, gameState.getUsingTractorBeamState().getTractorBeam(), totalDestiny);
        }
        else {
            totalDestiny = game.getModifiersQuerying().getTotalDestiny(gameState, _performingPlayerId, totalDestiny);
        }

        return Math.max(0, totalDestiny);
    }

    /**
     * Determines if draw destiny is complete.
     * @param game the game
     * @return true if draw destiny is complete, otherwise false
     */
    private boolean checkIfComplete(SwccgGame game) {
        if (_noMoreDestinyToDraw)
            return true;

        if (getNumDestinyDrawsRemaining() == 0) {
            _noMoreDestinyToDraw = true;
            return true;
        }

        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check if Reserve Deck is empty
        if (gameState.getReserveDeck(_performingPlayerId).isEmpty()) {
            gameState.sendMessage("No cards in Reserve Deck. " + _performingPlayerId + " can't draw a card for " + _destinyType.getHumanReadable());
            _noMoreDestinyToDraw = true;
            return true;
        }

        // Get the maximum limit for number of destinies that can be drawn
        int maxAllowedToDraw = Integer.MAX_VALUE;
        if (_destinyType == DestinyType.BATTLE_DESTINY) {
            maxAllowedToDraw = Math.min(maxAllowedToDraw, modifiersQuerying.getNumBattleDestinyDraws(gameState, _performingPlayerId, true, false));
        }
        else if (_destinyType == DestinyType.DESTINY_TO_TOTAL_POWER) {
            maxAllowedToDraw = Math.min(maxAllowedToDraw, modifiersQuerying.getNumDestinyDrawsToTotalPowerOnly(gameState, _performingPlayerId, true, false));
        }
        else if (_destinyType == DestinyType.DESTINY_TO_ATTRITION) {
            maxAllowedToDraw = Math.min(maxAllowedToDraw, modifiersQuerying.getNumDestinyDrawsToAttritionOnly(gameState, _performingPlayerId, true, false));
        }

        // If the number of destiny drawn so far against the limit has reached the limit, then no more destiny can be drawn
        if ((_numDrawnSoFarAgainstLimit + _numSkippedSoFar) >= maxAllowedToDraw) {
            gameState.sendMessage("Limit of " + maxAllowedToDraw + " " + _destinyType.getHumanReadable() + " drawn for " + _performingPlayerId + " has been reached. No more " + _destinyType.getHumanReadable() + " can be drawn");
            _noMoreDestinyToDraw = true;
        }
        return _noMoreDestinyToDraw;
    }

    /**
     * Gets the trigger actions that may be performed when the total destiny is being calculated.
     * @param game the game
     * @param effectResult the effect result
     * @return the trigger actions
     */
    public final List<TriggerAction> getOptionalTotalDestinyTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard actionSource) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();
        List<OptionalGameTextTriggerAction> gameTextActions = getGameTextOptionalTotalDestinyTriggers(playerId, game, effectResult, actionSource, actionSource.getCardId());
        if (gameTextActions != null) {
            actions.addAll(gameTextActions);
        }
        return actions;
    }

    /**
     * This method can be overridden by a card to get the trigger actions that may be performed when the total destiny is
     * being calculated during the draw destiny action performed by that card.
     * @param game the game
     * @param effectResult the effect result
     * @return the trigger actions
     */
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalTotalDestinyTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * Gets the cards whose ability, maneuver, or defense value is being targeted for comparison with the result of this destiny draw.
     * @return the cards whose whose ability, maneuver, or defense value is being targeted
     */
    public final Collection<PhysicalCard> getAbilityManeuverOrDefenseValueTargeted() {
        return getGameTextAbilityManeuverOrDefenseValueTargeted();
    }

    /**
     * This method can be overridden to specify the cards whose ability, maneuver, or defense value is being targeted for comparison with the result of this destiny draw.
     * @return the cards whose whose ability, maneuver, or defense value is being targeted
     */
    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
        return Collections.emptyList();
    }

    /**
     * Determines the card to stack race destiny on.
     * @return the card to stack race destiny on, or null if not race destiny
     */
    public PhysicalCard getStackRaceDestinyOn() {
        return null;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();

        SubAction subAction = new SubAction(_action, _performingPlayerId);
        // 1) Begin draw destiny
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.beginDrawDestiny(_drawDestinyEffect);

                        // Check if training destinies are draw 2 and choose 1
                        if (_destinyType == DestinyType.TRAINING_DESTINY
                                && game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.DRAW_TWO_AND_CHOOSE_ONE_FOR_TRAINING_DESTINY, _performingPlayerId)) {
                            setDrawXAndChooseY(2, 1);
                        }
                    }
                }
        );

        // 2) Callback to allow modifiers/proxy actions
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
                        List<Modifier> modifiers = _drawDestinyEffect.getDrawDestinyModifiers(game, drawDestinyState);
                        if (modifiers != null) {
                            for (Modifier modifier : modifiers) {
                                game.getModifiersEnvironment().addUntilEndOfDrawDestinyModifier(modifier);
                            }
                        }

                        List<ActionProxy> actionProxies = _drawDestinyEffect.getDrawDestinyActionProxies(game, drawDestinyState);
                        if (actionProxies != null) {
                            for (ActionProxy actionProxy : actionProxies) {
                                game.getActionsEnvironment().addUntilEndOfDrawDestinyActionProxy(actionProxy);
                            }
                        }
                    }
                }
        );

        // 3) Perform each destiny draw
        subAction.appendEffect(
                new DrawEachDestinyEffect(subAction));

        // 4) Complete the draw destiny and calculate total
        subAction.appendEffect(
                new CompleteDrawDestinyEffect(subAction));

        // 5) End draw destiny
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Final total
                        Float totalDestiny = getTotalDestiny(game);

                        gameState.endDrawDestiny();

                        // Callback
                        destinyDraws(game, _destinyCardDraws, _destinyDrawValues, totalDestiny);
                    }
                }
        );
        return subAction;
    }

    /**
     * This method is called before any drawing destiny is performed in order to get any modifiers that will last until
     * the end of this drawing destiny process.
     * @param game the game
     * @param drawDestinyState the draw destiny state
     */
    protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
        return null;
    }

    /**
     * This method is called before any drawing destiny is performed in order to get any proxy actions that will last until
     * the end of this drawing destiny process.
     * @param game the game
     * @param drawDestinyState the draw destiny state
     */
    protected List<ActionProxy> getDrawDestinyActionProxies(SwccgGame game, DrawDestinyState drawDestinyState) {
        return null;
    }

    /**
     * This method called when the drawing destiny is complete to inform the action performing this draw destiny what the
     * drawn destiny and totals were.
     * @param game the game
     * @param destinyCardDraws the cards drawn (and chosen, if applicable) and not canceled for destiny. Substituted destinies
     *                         are represented as null in this list. The list will be empty if if all destiny draws failed
     *                         or were canceled.
     * @param destinyDrawValues the destiny values. These represent the value for the destiny card draw in the same position in
     *                          destinyCardDraws. The list will be empty if if all destiny draws failed or were canceled.
     * @param totalDestiny the total destiny value, or null if all destiny draws failed or were canceled
     */
    protected abstract void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny);

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect that causes the specified player to draw each destiny.
     */
    private class DrawEachDestinyEffect extends AbstractSubActionEffect {
        private SubAction _parentSubAction;

        /**
         * Creates an effect that causes the specified player to draw each destiny.
         * @param subAction the action performing this effect
         */
        private DrawEachDestinyEffect(SubAction subAction) {
            super(subAction);
            _parentSubAction = subAction;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        public boolean wasActionCarriedOut() {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final GameState gameState = game.getGameState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            final ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
            final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
            final SubAction subAction = new SubAction(_parentSubAction);

            // First initialize values for this draw
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            _substitutedDestiny = null;
                            _substitutedDestinyInitially = false;
                            _drawnDestinyCard = null;
                            _drawnDestinyValue = null;
                            _drawnDestinyValueModification = 0;
                            _modifierSourceTitleMap.clear();
                            _reset = false;
                            _canceled = false;
                            _redrawn = false;
                            _skipped = false;
                            _choseY = false;
                            _costToDrawFailed = false;
                            _costToDrawFailedWasChosenByPlayer = false;
                            _mayNotBeCanceledByPlayer.clear();

                            // 1) Trigger for costs to draw the destiny
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (checkIfComplete(game))
                                                return;

                                            actionsEnvironment.emitEffectResult(
                                                    new CostToDrawDestinyCardResult(subAction, _drawDestinyEffect));
                                        }
                                    }
                            );

                            // 2) Automatic and optional responses to "about to draw a destiny".
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (isSkipDrawDueToCostToDrawCardFailed(game)) {
                                                _skipped = true;
                                                _numSkippedSoFar++;
                                                if (isDrawAndChoose()) {
                                                    _numSkippedSoFarWithinDrawX++;
                                                }
                                                return;
                                            }

                                            // Begin state for modifiers with duration of single destiny draw
                                            gameState.beginEachDrawnDestiny(_drawDestinyEffect);

                                            if (checkIfComplete(game))
                                                return;

                                            actionsEnvironment.emitEffectResult(
                                                    new AboutToDrawDestinyCardResult(subAction, _drawDestinyEffect));
                                        }
                                    }
                            );

                            // 3) If the destiny was not substituted, then draw the destiny card.
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (_skipped)
                                                return;

                                            if (getSubstituteDestiny() != null) {
                                                _substitutedDestinyInitially = true;
                                                _drawnDestinyValue = _substitutedDestiny;
                                                _drawnDestinyValueModification = 0;
                                                _modifierSourceTitleMap.clear();
                                                // Increment number drawn (but not against limit)
                                                _numDrawnSoFar++;
                                                if (isDrawAndChoose()) {
                                                    _numDrawnSoFarWithinDrawX++;
                                                }
                                                return;
                                            }

                                            if (checkIfComplete(game))
                                                return;

                                            // Draw a destiny card
                                            SubAction drawDestinyCardSubAction = new SubAction(subAction);
                                            drawDestinyCardSubAction.appendEffect(
                                                    new DrawDestinyCardEffect(drawDestinyCardSubAction));
                                            subAction.stackSubAction(drawDestinyCardSubAction);
                                        }
                                    }
                            );

                            // 4) Check if the player's battle destiny modifiers are to be applied to total battle destiny instead
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (_skipped)
                                                return;

                                            if (_destinyType != DestinyType.BATTLE_DESTINY)
                                                return;

                                            // Create a total battle destiny modifier for each battle destiny modifier
                                            // that is to be applied to total battle destiny instead
                                            List<Map<PhysicalCard, Float>> sourceAmountMapList = modifiersQuerying.getPlayersBattleDestinyModifiersToApplyToTotalBattleDestiny(gameState, _drawnDestinyCard, _performingPlayerId);
                                            for (Map<PhysicalCard, Float> sourceAmountMap : sourceAmountMapList) {
                                                for (PhysicalCard source : sourceAmountMap.keySet()) {
                                                    Float amount = sourceAmountMap.get(source);
                                                    modifiersEnvironment.addUntilEndOfBattleModifier(
                                                            new TotalBattleDestinyModifier(source, amount, _performingPlayerId, true));
                                                }
                                            }
                                        }
                                    }
                            );

                            // 5) Automatic and optional responses to "just drawn destiny".
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (_skipped)
                                                return;

                                            if (_drawnDestinyCard == null && _substitutedDestiny == null)
                                                return;

                                            // Automatic actions and Just actions (for destiny drawn trigger)
                                            actionsEnvironment.emitEffectResult(
                                                    new DestinyDrawnResult(subAction, _drawDestinyEffect));
                                        }
                                    }
                            );

                            // 6) Complete the draw.
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (_skipped)
                                                return;

                                            if (getSubstituteDestiny() != null && !_substitutedDestinyInitially) {
                                                _drawnDestinyValue = _substitutedDestiny;
                                                _drawnDestinyValueModification = 0;
                                                _modifierSourceTitleMap.clear();
                                                // Decrement number drawn so far against limit if substituted after drawn
                                                _numDrawnSoFarAgainstLimit--;
                                            }
                                            else if (isDestinyToBeRedrawn()) {
                                                // Decrement number drawn so far (and against limit if canceled and redraw)
                                                _numDrawnSoFar--;
                                                _numDrawnSoFarAgainstLimit--;
                                                if (isDrawAndChoose()) {
                                                    _numDrawnSoFarWithinDrawX--;
                                                }
                                            }

                                            // End state for modifiers with duration of single destiny draw
                                            gameState.endEachDrawnDestiny();

                                            if (isDestinyCanceled()) {
                                                if (isDrawAndChoose()) {
                                                    // Place canceled card of a Draw X and Choose Y on Used Pile
                                                    if (_drawnDestinyCard != null && GameUtils.getZoneFromZoneTop(_drawnDestinyCard.getZone()) == Zone.UNRESOLVED_DESTINY_DRAW) {
                                                        gameState.removeCardsFromZone(Collections.singleton(_drawnDestinyCard));
                                                        gameState.addCardToTopOfZone(_drawnDestinyCard, Zone.USED_PILE, _performingPlayerId);
                                                    }
                                                }
                                                return;
                                            }

                                            if (_drawnDestinyCard == null && _substitutedDestiny == null)
                                                return;

                                            // Make sure not less than zero
                                            _drawnDestinyValue = Math.max(0, getDestinyDrawValue());
                                            _drawnDestinyValueModification = 0;

                                            // Add to choices for choose Y
                                            if (isDrawAndChoose()) {
                                                _drawXCardsToChooseFrom.add(_drawnDestinyCard);
                                                _drawXValuesToChooseFrom.add(_drawnDestinyValue);
                                            }

                                            // Automatic actions and Just actions (for destiny draw complete trigger)
                                            actionsEnvironment.emitEffectResult(
                                                    new DestinyDrawCompleteResult(subAction, _drawDestinyEffect));
                                        }
                                    }
                            );

                            // 7) Choose Y from draw X, if needed
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (!isDrawAndChoose())
                                                return;

                                            if ((_numDrawnSoFarWithinDrawX + _numSkippedSoFarWithinDrawX) < _drawX && !_noMoreDestinyToDraw)
                                                return;

                                            if (_drawXValuesToChooseFrom.isEmpty())
                                                return;

                                            // Choose Y
                                            SubAction chooseYSubAction = new SubAction(subAction);
                                            chooseYSubAction.appendEffect(
                                                    new ChooseYEffect(chooseYSubAction));
                                            chooseYSubAction.appendEffect(
                                                    new PassthruEffect(chooseYSubAction) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            // When we get here, the choose Y values has been performed
                                                            // Place drawn cards on Used Pile in order drawn (if not substituted and still in UNRESOLVED DESTINY DRAWN zone)
                                                            for (PhysicalCard destinyCard : _drawXCardsToChooseFrom) {
                                                                if (destinyCard != null && GameUtils.getZoneFromZoneTop(destinyCard.getZone()) == Zone.UNRESOLVED_DESTINY_DRAW) {
                                                                    gameState.removeCardsFromZone(Collections.singleton(destinyCard));
                                                                    gameState.addCardToTopOfZone(destinyCard, Zone.USED_PILE, _performingPlayerId);
                                                                }
                                                            }

                                                            // Draw X and choose Y is completed
                                                            drawXAndChooseYComplete();
                                                        }
                                                    }
                                            );
                                            subAction.stackSubAction(chooseYSubAction);
                                        }
                                    }
                            );

                            // 8) Determine if additional draws are required
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(final SwccgGame game) {
                                            if (!isDrawAndChoose() && !_choseY) {

                                                if (!isDestinyCanceled() && _drawnDestinyValue != null) {
                                                    // Save destiny card and value
                                                    _destinyCardDraws.add(_drawnDestinyCard);
                                                    _destinyDrawValues.add(_drawnDestinyValue);
                                                }

                                                final PhysicalCard stackRaceDestinyOn = getStackRaceDestinyOn();
                                                if (stackRaceDestinyOn != null
                                                        && _drawnDestinyCard != null
                                                        && GameUtils.getZoneFromZoneTop(_drawnDestinyCard.getZone()) == Zone.UNRESOLVED_DESTINY_DRAW) {
                                                    final boolean isDamagedPodracer = stackRaceDestinyOn.isDamaged();
                                                    subAction.appendEffect(
                                                            new PlayoutDecisionEffect(subAction, _performingPlayerId,
                                                                    new YesNoDecision("Do you want to " + (isDamagedPodracer ? " lose 1 Force to " : "") + "stack " + GameUtils.getCardLink(_drawnDestinyCard) + " on " + (isDamagedPodracer ? "'damaged' " : "") + GameUtils.getCardLink(stackRaceDestinyOn) + " as a " + _destinyType.getHumanReadable() + "?") {
                                                                        @Override
                                                                        protected void yes() {
                                                                            gameState.sendMessage(_performingPlayerId + " chooses to " + (isDamagedPodracer ? " lose 1 Force to " : "") + "stack " + GameUtils.getCardLink(_drawnDestinyCard) + " on " + (isDamagedPodracer ? "'damaged' " : "") + GameUtils.getCardLink(stackRaceDestinyOn) + " as a " + _destinyType.getHumanReadable());
                                                                            subAction.insertEffect(
                                                                                    new PassthruEffect(subAction) {
                                                                                        @Override
                                                                                        protected void doPlayEffect(SwccgGame game) {
                                                                                            if (GameUtils.getZoneFromZoneTop(_drawnDestinyCard.getZone()) == Zone.UNRESOLVED_DESTINY_DRAW) {
                                                                                                float destinyValueToUse = _drawnDestinyCard.getDestinyValueToUse();
                                                                                                gameState.removeCardFromZone(_drawnDestinyCard);
                                                                                                _drawnDestinyCard.setDestinyValueToUse(destinyValueToUse);
                                                                                                gameState.stackCard(_drawnDestinyCard, stackRaceDestinyOn, false, false, false);
                                                                                                game.getActionsEnvironment().emitEffectResult(
                                                                                                        new RaceDestinyStackedResult(subAction, _drawnDestinyCard, stackRaceDestinyOn));
                                                                                                _drawnDestinyCard.setRaceDestinyForPlayer(_performingPlayerId);
                                                                                            }
                                                                                        }
                                                                                    });
                                                                            // Insert the Lose Force Effect from 'damaged' Podracer, so it happens first
                                                                            if (isDamagedPodracer) {
                                                                                subAction.insertEffect(
                                                                                        new LoseForceEffect(subAction, _performingPlayerId, 1, true));
                                                                            }
                                                                        }
                                                                        @Override
                                                                        protected void no() {
                                                                            gameState.sendMessage(_performingPlayerId + " chooses to not " + (isDamagedPodracer ? " lose 1 Force to " : "") + "stack " + GameUtils.getCardLink(_drawnDestinyCard) + " on " + (isDamagedPodracer ? "'damaged' " : "") + GameUtils.getCardLink(stackRaceDestinyOn) + " as a " + _destinyType.getHumanReadable());
                                                                        }
                                                                    }
                                                            ));
                                                }

                                                // Place drawn card on Used Pile (if not substituted and still in UNRESOLVED DESTINY DRAWN zone)
                                                subAction.appendEffect(
                                                        new PassthruEffect(subAction) {
                                                            @Override
                                                            protected void doPlayEffect(SwccgGame game) {
                                                                if (_drawnDestinyCard != null && GameUtils.getZoneFromZoneTop(_drawnDestinyCard.getZone()) == Zone.UNRESOLVED_DESTINY_DRAW) {
                                                                    gameState.removeCardsFromZone(Collections.singleton(_drawnDestinyCard));
                                                                    gameState.addCardToTopOfZone(_drawnDestinyCard, Zone.USED_PILE, _performingPlayerId);
                                                                }
                                                            }
                                                        }
                                                );
                                            }

                                            // 9) Return to step one if more draws must be made
                                            subAction.appendEffect(
                                                    new PassthruEffect(subAction) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            if (!_noMoreDestinyToDraw && getNumDestinyDrawsRemaining() > 0) {
                                                                _parentSubAction.insertEffect(
                                                                        new DrawEachDestinyEffect(_parentSubAction));
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );

            return subAction;
        }
    }

    /**
     * A private effect that causes the player to draw a destiny card.
     */
    private class DrawDestinyCardEffect extends AbstractSubActionEffect {
        private SubAction _parentSubAction;

        /**
         * Creates an effect that causes the player to draw a destiny card.
         *
         * @param action the action performing this effect
         */
        public DrawDestinyCardEffect(SubAction action) {
            super(action);
            _parentSubAction = action;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        public boolean wasActionCarriedOut() {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final SubAction subAction = new SubAction(_parentSubAction);

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            final GameState gameState = game.getGameState();

                            // Step 1: Draw destiny card
                            // Optionally from bottom of deck if the modifier in effect, otherwise default to top card
                            if (gameState.getGame().getModifiersQuerying().shouldDrawDestinyFromBottomOfDeck(gameState, _performingPlayerId)) {

                                PhysicalCard modifierSource = gameState.getGame().getModifiersQuerying().getDrawsDestinyFromBottomOfDeckModiferSource(gameState, _performingPlayerId);
                                if (modifierSource != null) {
                                    gameState.sendMessage(_performingPlayerId + " drawing " + _destinyType.getHumanReadable() + " from bottom of pile due to " + GameUtils.getCardLink(modifierSource));
                                }

                                _drawnDestinyCard = gameState.getBottomOfCardPile(_performingPlayerId, Zone.RESERVE_DECK);

                            } else {
                                _drawnDestinyCard = gameState.getTopOfReserveDeck(_performingPlayerId);
                            }

                            String destinyText = _destinyType.getHumanReadable();
                            if (isDrawAndChoose()) {
                                destinyText += (" (draw " + _drawX + " and choose " + _chooseY + ")");
                                // Increment number drawn within draw X
                                _numDrawnSoFarWithinDrawX++;
                            }
                            gameState.destinyDrawn(_drawnDestinyCard, destinyText);
                            gameState.removeCardsFromZone(Collections.singleton(_drawnDestinyCard));
                            gameState.addCardToTopOfZone(_drawnDestinyCard, Zone.UNRESOLVED_DESTINY_DRAW, _performingPlayerId);
                            // Increment number drawn (and against limit)
                            _numDrawnSoFar++;
                            _numDrawnSoFarAgainstLimit++;

                            // Ask the player to choose the destiny value (if multiple exist)
                            if (_drawnDestinyCard.getBlueprint().getDestiny() != null && !_drawnDestinyCard.getBlueprint().getDestiny().equals(_drawnDestinyCard.getBlueprint().getAlternateDestiny())) {
                                subAction.appendEffect(
                                        new RefreshPrintedDestinyValuesEffect(subAction, Collections.singletonList(_drawnDestinyCard)) {
                                            @Override
                                            protected void refreshedPrintedDestinyValues() {
                                            }
                                        });
                            }
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            // Step 2: Apply automatic draw modifiers
                                            if (_destinyType == DestinyType.ASTEROID_DESTINY) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getAsteroidDestiny(game.getGameState(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else if (_destinyType == DestinyType.BATTLE_DESTINY) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getBattleDestiny(game.getGameState(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else if (_destinyType == DestinyType.CARBON_FREEZING_DESTINY) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getCarbonFreezingDestiny(game.getGameState(), _drawnDestinyCard);
                                            }
                                            else if (_destinyType == DestinyType.DESTINY_TO_ATTRITION) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getDestinyToAttrition(game.getGameState(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else if (_destinyType == DestinyType.DESTINY_TO_TOTAL_POWER) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getDestinyToPower(game.getGameState(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else if (_destinyType == DestinyType.DUEL_DESTINY) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getDuelDestiny(game.getGameState(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else if (_destinyType == DestinyType.EPIC_EVENT_DESTINY) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getEpicEventDestiny(game.getGameState(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else if (_destinyType == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getEpicEventAndWeaponDestiny(game.getGameState(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else if (_destinyType == DestinyType.LIGHTSABER_COMBAT_DESTINY) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getLightsaberCombatDestiny(game.getGameState(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else if (_destinyType == DestinyType.SEARCH_PARTY_DESTINY) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getSearchPartyDestiny(game.getGameState(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else if (_destinyType == DestinyType.TRACTOR_BEAM_DESTINY) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getTractorBeamDestiny(game.getGameState(), game.getGameState().getUsingTractorBeamState().getTractorBeam(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else if (_destinyType == DestinyType.TRAINING_DESTINY) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getTrainingDestiny(game.getGameState(), _drawDestinyEffect.getAction().getActionSource(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else if (_destinyType == DestinyType.WEAPON_DESTINY) {
                                                _drawnDestinyValue = game.getModifiersQuerying().getWeaponDestiny(game.getGameState(), _drawnDestinyCard, _performingPlayerId);
                                            }
                                            else {
                                                _drawnDestinyValue = game.getModifiersQuerying().getDestinyForDestinyDraw(game.getGameState(), _drawnDestinyCard, _drawDestinyEffect.getAction().getActionSource());
                                            }

                                            String msgText = _performingPlayerId + " draws " + GameUtils.getCardLink(_drawnDestinyCard) + " as a " + GuiUtils.formatAsString(_drawnDestinyValue) + " for " + _destinyType.getHumanReadable();
                                            if (isDrawAndChoose()) {
                                                msgText += (" (draw " + _drawX + " and choose " + _chooseY + ")");
                                            }
                                            gameState.sendMessage(msgText);
                                        }
                                    }
                            );
                        }
                    }
            );

            return subAction;
        }
    }

    /**
     * A private effect that causes the player to choose Y values from the X drawn values.
     */
    private class ChooseYEffect extends AbstractSubActionEffect {
        private SubAction _parentSubAction;

        /**
         * Creates an effect that causes the player to choose Y values from the X drawn values.
         *
         * @param action the action performing this effect
         */
        public ChooseYEffect(SubAction action) {
            super(action);
            _parentSubAction = action;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        public boolean wasActionCarriedOut() {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final GameState gameState = game.getGameState();

            final SubAction subAction = new SubAction(_parentSubAction);
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(final SwccgGame game) {
                            int numToChoose = Math.min(_drawXCardsToChooseFrom.size(), _chooseY);
                            if (numToChoose > 0) {
                                // Create map of destiny text
                                Map<PhysicalCard, String> cardTextMap = new HashMap<PhysicalCard, String>();
                                for (int i = 0; i < _drawXCardsToChooseFrom.size(); ++i) {
                                    cardTextMap.put(_drawXCardsToChooseFrom.get(i), "destiny = " + GuiUtils.formatAsString(_drawXValuesToChooseFrom.get(i)));
                                }
                                gameState.sendMessage(_performingPlayerId + " is choosing " + _chooseY + " destiny from those drawn");

                                subAction.appendEffect(
                                        new PlayoutDecisionEffect(subAction, _performingPlayerId,
                                                new ArbitraryCardsSelectionDecision("Choose " + numToChoose + " destiny", _drawXCardsToChooseFrom, _drawXCardsToChooseFrom, numToChoose, numToChoose, cardTextMap) {
                                                    @Override
                                                    public void decisionMade(String result) throws DecisionResultInvalidException {
                                                        final List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);

                                                        StringBuilder msgText = new StringBuilder(_performingPlayerId);
                                                        msgText.append(" chooses ");
                                                        for (PhysicalCard selectedCard : selectedCards) {
                                                            _destinyCardDraws.add(selectedCard);
                                                            Float value = _drawXValuesToChooseFrom.get(_drawXCardsToChooseFrom.indexOf(selectedCard));
                                                            _destinyDrawValues.add(value);
                                                            msgText.append(GameUtils.getCardLink(selectedCard)).append(" (destiny = ").append(GuiUtils.formatAsString(value)).append("), ");
                                                        }
                                                        msgText.setLength(msgText.length() - 2);
                                                        msgText.append(" for ").append(_destinyType.getHumanReadable());
                                                        gameState.sendMessage(msgText.toString());

                                                        final PhysicalCard stackRaceDestinyOn = getStackRaceDestinyOn();
                                                        if (stackRaceDestinyOn != null) {
                                                            for (final PhysicalCard selectedCard : selectedCards) {
                                                                if (selectedCard != null
                                                                        && GameUtils.getZoneFromZoneTop(selectedCard.getZone()) == Zone.UNRESOLVED_DESTINY_DRAW) {
                                                                    final boolean isDamagedPodracer = stackRaceDestinyOn.isDamaged();
                                                                    subAction.appendEffect(
                                                                            new PlayoutDecisionEffect(subAction, _performingPlayerId,
                                                                                    new YesNoDecision("Do you want to " + (isDamagedPodracer ? " lose 1 Force to " : "") + "stack " + GameUtils.getCardLink(selectedCard) + " on " + (isDamagedPodracer ? "'damaged' " : "") + GameUtils.getCardLink(stackRaceDestinyOn) + " as a " + _destinyType.getHumanReadable() + "?") {
                                                                                        @Override
                                                                                        protected void yes() {
                                                                                            gameState.sendMessage(_performingPlayerId + " chooses to " + (isDamagedPodracer ? " lose 1 Force to " : "") + "stack " + GameUtils.getCardLink(selectedCard) + " on " + (isDamagedPodracer ? "'damaged' " : "") + GameUtils.getCardLink(stackRaceDestinyOn) + " as a " + _destinyType.getHumanReadable());
                                                                                            subAction.insertEffect(
                                                                                                    new PassthruEffect(subAction) {
                                                                                                        @Override
                                                                                                        protected void doPlayEffect(SwccgGame game) {
                                                                                                            if (GameUtils.getZoneFromZoneTop(selectedCard.getZone()) == Zone.UNRESOLVED_DESTINY_DRAW) {
                                                                                                                float destinyValueToUse = selectedCard.getDestinyValueToUse();
                                                                                                                gameState.removeCardFromZone(selectedCard);
                                                                                                                selectedCard.setDestinyValueToUse(destinyValueToUse);
                                                                                                                selectedCard.setRaceDestinyForPlayer(_performingPlayerId);
                                                                                                                gameState.stackCard(selectedCard, stackRaceDestinyOn, false, false, false);
                                                                                                                game.getActionsEnvironment().emitEffectResult(
                                                                                                                        new RaceDestinyStackedResult(subAction, selectedCard, stackRaceDestinyOn));
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                            // Insert the Lose Force Effect from 'damaged' Podracer, so it happens first
                                                                                            if (isDamagedPodracer) {
                                                                                                subAction.insertEffect(
                                                                                                        new LoseForceEffect(subAction, _performingPlayerId, 1, true));
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        protected void no() {
                                                                                            gameState.sendMessage(_performingPlayerId + " chooses to not " + (isDamagedPodracer ? " lose 1 Force to " : "") + "stack " + GameUtils.getCardLink(selectedCard) + " on " + (isDamagedPodracer ? "'damaged' " : "") + GameUtils.getCardLink(stackRaceDestinyOn) + " as a " + _destinyType.getHumanReadable());
                                                                                        }
                                                                                    }
                                                                            )
                                                                    );
                                                                }
                                                            }
                                                        }

                                                        if (_takeOtherIntoHand) {
                                                            subAction.appendEffect(
                                                                    new PassthruEffect(subAction) {
                                                                        @Override
                                                                        protected void doPlayEffect(SwccgGame game) {
                                                                            // If destinies not chosen during choose Y are to be taken into hand,
                                                                            // put them in hand (if still in UNRESOLVED DESTINY DRAWN zone)
                                                                            for (PhysicalCard destinyCard : _drawXCardsToChooseFrom) {
                                                                                if (destinyCard != null && !selectedCards.contains(destinyCard)
                                                                                        && GameUtils.getZoneFromZoneTop(destinyCard.getZone()) == Zone.UNRESOLVED_DESTINY_DRAW) {
                                                                                    gameState.removeCardsFromZone(Collections.singleton(destinyCard));
                                                                                    gameState.addCardToZone(destinyCard, Zone.HAND, _performingPlayerId);
                                                                                    gameState.sendMessage(_performingPlayerId + " takes " + GameUtils.getCardLink(destinyCard) + " into hand");
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                        )
                                );
                            }
                        }
                    }
            );
            return subAction;
        }
    }

    /**
     * A private effect that causes the total destiny to be calculated.
     */
    private class CompleteDrawDestinyEffect extends AbstractSubActionEffect {
        private SubAction _parentSubAction;

        /**
         * Creates an effect that causes the total destiny to be calculated.
         *
         * @param action the action performing this effect
         */
        public CompleteDrawDestinyEffect(SubAction action) {
            super(action);
            _parentSubAction = action;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        public boolean wasActionCarriedOut() {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
            final SubAction subAction = new SubAction(_parentSubAction);

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            Float totalDestiny = getTotalDestiny(game);
                            if (totalDestiny == null)
                                return;

                            if (_destinyType != DestinyType.RACE_DESTINY) {
                                game.getGameState().sendMessage(_performingPlayerId + "'s total " + _destinyType.getHumanReadable() + " is " + GuiUtils.formatAsString(totalDestiny));
                            }

                            actionsEnvironment.emitEffectResult(
                                    new AboutToCompleteDrawingDestinyResult(subAction, _drawDestinyEffect));
                        }
                    });

            return subAction;
        }
    }
}
