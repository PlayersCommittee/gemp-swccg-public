package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.SabaccState;
import com.gempukku.swccgo.logic.PlayerOrder;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Game stats information that is sent to the User Interface for display.
 */
public class GameStats {
    private SwccgGame _game;
    private float _lightForceGeneration;
    private float _darkForceGeneration;
    private float _lightBattlePower;
    private float _darkBattlePower;
    private int _lightBattleNumDestinyToPower;
    private int _darkBattleNumDestinyToPower;
    private int _lightBattleNumBattleDestiny;
    private int _darkBattleNumBattleDestiny;
    private int _lightBattleNumDestinyToAttrition;
    private int _darkBattleNumDestinyToAttrition;
    private float _lightBattleDamageRemaining;
    private float _darkBattleDamageRemaining;
    private float _lightBattleAttritionRemaining;
    private float _darkBattleAttritionRemaining;
    private boolean _isLightImmuneToRemainingAttrition;
    private boolean _isDarkImmuneToRemainingAttrition;
    private float _lightSabaccTotal = -1;
    private float _darkSabaccTotal = -1;
    private float _lightDuelOrLightsaberCombatTotal = -1;
    private float _darkDuelOrLightsaberCombatTotal = -1;
    private int _lightDuelOrLightsaberCombatNumDestiny;
    private int _darkDuelOrLightsaberCombatNumDestiny;
    private float _attackingPowerOrFerocityInAttack = -1;
    private float _defendingPowerOrFerocityInAttack = -1;
    private int _attackingNumDestinyInAttack;
    private int _defendingNumDestinyInAttack;
    private float _lightRaceTotal = -1;
    private float _darkRaceTotal = -1;
    private float _lightPoliticsTotal = -1;
    private float _darkPoliticsTotal = -1;
    private Map<String, Map<Zone, Integer>> _zoneSizes = new HashMap<String, Map<Zone, Integer>>();
    private Map<Integer, Float> _darkPowerAtLocations = new HashMap<Integer, Float>();
    private Map<Integer, Float> _lightPowerAtLocations = new HashMap<Integer, Float>();

    /**
     * Updates game stats.
     *
     * @return true if any of the stats changed, otherwise false
     */
    public boolean updateGameStats(SwccgGame game) {
        _game = game;
        String darkPlayer = game.getDarkPlayer();
        String lightPlayer = game.getLightPlayer();
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        boolean changed = false;

        float newLightForceGenerationTotal = GuiUtils.calculateForceGeneration(game, lightPlayer);
        if (newLightForceGenerationTotal != _lightForceGeneration) {
            changed = true;
            _lightForceGeneration = newLightForceGenerationTotal;
        }

        float newDarkForceGenerationTotal = GuiUtils.calculateForceGeneration(game, darkPlayer);
        if (newDarkForceGenerationTotal != _darkForceGeneration) {
            changed = true;
            _darkForceGeneration = newDarkForceGenerationTotal;
        }

        float newLightBattlePower = GuiUtils.getBattleTotalPower(game, lightPlayer);
        if (newLightBattlePower != _lightBattlePower) {
            changed = true;
            _lightBattlePower = newLightBattlePower;
        }

        float newDarkBattlePower = GuiUtils.getBattleTotalPower(game, darkPlayer);
        if (newDarkBattlePower != _darkBattlePower) {
            changed = true;
            _darkBattlePower = newDarkBattlePower;
        }

        int newLightBattleNumDestinyToPower = GuiUtils.getNumDestinyToTotalPowerLeftToDraw(game, lightPlayer);
        if (newLightBattleNumDestinyToPower != _lightBattleNumDestinyToPower) {
            changed = true;
            _lightBattleNumDestinyToPower = newLightBattleNumDestinyToPower;
        }

        int newDarkBattleNumDestinyToPower = GuiUtils.getNumDestinyToTotalPowerLeftToDraw(game, darkPlayer);
        if (newDarkBattleNumDestinyToPower != _darkBattleNumDestinyToPower) {
            changed = true;
            _darkBattleNumDestinyToPower = newDarkBattleNumDestinyToPower;
        }

        int newLightBattleNumBattleDestiny = GuiUtils.getNumBattleDestinyLeftToDraw(game, lightPlayer);
        if (newLightBattleNumBattleDestiny != _lightBattleNumBattleDestiny) {
            changed = true;
            _lightBattleNumBattleDestiny = newLightBattleNumBattleDestiny;
        }

        int newDarkBattleNumBattleDestiny = GuiUtils.getNumBattleDestinyLeftToDraw(game, darkPlayer);
        if (newDarkBattleNumBattleDestiny != _darkBattleNumBattleDestiny) {
            changed = true;
            _darkBattleNumBattleDestiny = newDarkBattleNumBattleDestiny;
        }

        int newLightBattleNumDestinyToAttrition = GuiUtils.getNumDestinyToAttritionLeftToDraw(game, lightPlayer);
        if (newLightBattleNumDestinyToAttrition != _lightBattleNumDestinyToAttrition) {
            changed = true;
            _lightBattleNumDestinyToAttrition = newLightBattleNumDestinyToAttrition;
        }

        int newDarkBattleNumDestinyToAttrition = GuiUtils.getNumDestinyToAttritionLeftToDraw(game, darkPlayer);
        if (newDarkBattleNumDestinyToAttrition != _darkBattleNumDestinyToAttrition) {
            changed = true;
            _darkBattleNumDestinyToAttrition = newDarkBattleNumDestinyToAttrition;
        }

        float newLightBattleDamageRemaining = GuiUtils.getBattleDamageRemaining(game, lightPlayer);
        if (newLightBattleDamageRemaining != _lightBattleDamageRemaining) {
            changed = true;
            _lightBattleDamageRemaining = newLightBattleDamageRemaining;
        }

        float newDarkBattleDamageRemaining = GuiUtils.getBattleDamageRemaining(game, darkPlayer);
        if (newDarkBattleDamageRemaining != _darkBattleDamageRemaining) {
            changed = true;
            _darkBattleDamageRemaining = newDarkBattleDamageRemaining;
        }

        float newLightBattleAttritionRemaining = GuiUtils.getBattleAttritionRemaining(game, lightPlayer);
        if (newLightBattleAttritionRemaining != _lightBattleAttritionRemaining) {
            changed = true;
            _lightBattleAttritionRemaining = newLightBattleAttritionRemaining;
        }

        float newDarkBattleAttritionRemaining = GuiUtils.getBattleAttritionRemaining(game, darkPlayer);
        if (newDarkBattleAttritionRemaining != _darkBattleAttritionRemaining) {
            changed = true;
            _darkBattleAttritionRemaining = newDarkBattleAttritionRemaining;
        }

        boolean newLightIsImmuneToRemainingAttrition = GuiUtils.isImmuneToRemainingAttrition(game, lightPlayer);
        if (newLightIsImmuneToRemainingAttrition != _isLightImmuneToRemainingAttrition) {
            changed = true;
            _isLightImmuneToRemainingAttrition = newLightIsImmuneToRemainingAttrition;
        }

        boolean newDarkIsImmuneToRemainingAttrition = GuiUtils.isImmuneToRemainingAttrition(game, darkPlayer);
        if (newDarkIsImmuneToRemainingAttrition != _isDarkImmuneToRemainingAttrition) {
            changed = true;
            _isDarkImmuneToRemainingAttrition = newDarkIsImmuneToRemainingAttrition;
        }

        float newLightSabaccTotal = GuiUtils.getSabaccTotal(game, lightPlayer);
        if (newLightSabaccTotal != _lightSabaccTotal) {
            changed = true;
            _lightSabaccTotal = newLightSabaccTotal;
        }

        float newDarkSabaccTotal = GuiUtils.getSabaccTotal(game, darkPlayer);
        if (newDarkSabaccTotal != _darkSabaccTotal) {
            changed = true;
            _darkSabaccTotal = newDarkSabaccTotal;
        }

        float newLightDuelOrLightsaberCombatTotal = GuiUtils.getDuelOrLightsaberCombatTotal(game, lightPlayer);
        if (newLightDuelOrLightsaberCombatTotal != _lightDuelOrLightsaberCombatTotal) {
            changed = true;
            _lightDuelOrLightsaberCombatTotal = newLightDuelOrLightsaberCombatTotal;
        }

        float newDarkDuelOrLightsaberCombatTotal = GuiUtils.getDuelOrLightsaberCombatTotal(game, darkPlayer);
        if (newDarkDuelOrLightsaberCombatTotal != _darkDuelOrLightsaberCombatTotal) {
            changed = true;
            _darkDuelOrLightsaberCombatTotal = newDarkDuelOrLightsaberCombatTotal;
        }

        int newLightDuelOrLightsaberCombatNumDestiny = GuiUtils.getNumDuelOrLightsaberCombatDestinyLeftToDraw(game, lightPlayer);
        if (newLightDuelOrLightsaberCombatNumDestiny != _lightDuelOrLightsaberCombatNumDestiny) {
            changed = true;
            _lightDuelOrLightsaberCombatNumDestiny = newLightDuelOrLightsaberCombatNumDestiny;
        }

        int newDarkDuelOrLightsaberCombatNumDestiny = GuiUtils.getNumDuelOrLightsaberCombatDestinyLeftToDraw(game, darkPlayer);
        if (newDarkDuelOrLightsaberCombatNumDestiny != _darkDuelOrLightsaberCombatNumDestiny) {
            changed = true;
            _darkDuelOrLightsaberCombatNumDestiny = newDarkDuelOrLightsaberCombatNumDestiny;
        }

        float newAttackingPowerOrFerocityInAttack = GuiUtils.getAttackAttackerTotal(game);
        if (newAttackingPowerOrFerocityInAttack != _attackingPowerOrFerocityInAttack) {
            changed = true;
            _attackingPowerOrFerocityInAttack = newAttackingPowerOrFerocityInAttack;
        }

        float newDefendingPowerOrFerocityInAttack = GuiUtils.getAttackDefenderTotal(game);
        if (newDefendingPowerOrFerocityInAttack != _defendingPowerOrFerocityInAttack) {
            changed = true;
            _defendingPowerOrFerocityInAttack = newDefendingPowerOrFerocityInAttack;
        }

        int newAttackingNumDestinyInAttack = GuiUtils.getNumAttackAttackerDestinyLeftToDraw(game);
        if (newAttackingNumDestinyInAttack != _attackingNumDestinyInAttack) {
            changed = true;
            _attackingNumDestinyInAttack = newAttackingNumDestinyInAttack;
        }

        int newDefendingNumDestinyInAttack = GuiUtils.getNumAttackDefenderDestinyLeftToDraw(game);
        if (newDefendingNumDestinyInAttack != _defendingNumDestinyInAttack) {
            changed = true;
            _defendingNumDestinyInAttack = newDefendingNumDestinyInAttack;
        }

        float newLightRaceTotal = GuiUtils.getHighestRaceTotal(game, lightPlayer);
        if (newLightRaceTotal != _lightRaceTotal) {
            changed = true;
            _lightRaceTotal = newLightRaceTotal;
        }

        float newDarkRaceTotal = GuiUtils.getHighestRaceTotal(game, darkPlayer);
        if (newDarkRaceTotal != _darkRaceTotal) {
            changed = true;
            _darkRaceTotal = newDarkRaceTotal;
        }

        float newLightPoliticsTotal = GuiUtils.getPoliticsTotal(game, lightPlayer);
        if (newLightPoliticsTotal != _lightPoliticsTotal) {
            changed = true;
            _lightPoliticsTotal = newLightPoliticsTotal;
        }

        float newDarkPoliticsTotal = GuiUtils.getPoliticsTotal(game, darkPlayer);
        if (newDarkPoliticsTotal != _darkPoliticsTotal) {
            changed = true;
            _darkPoliticsTotal = newDarkPoliticsTotal;
        }

        Map<String, Map<Zone, Integer>> newZoneSizes = new HashMap<String, Map<Zone, Integer>>();
        PlayerOrder playerOrder = game.getGameState().getPlayerOrder();
        if (playerOrder != null) {
            for (String player : playerOrder.getAllPlayers()) {
                final HashMap<Zone, Integer> playerZoneSizes = new HashMap<Zone, Integer>();
                playerZoneSizes.put(Zone.HAND, game.getGameState().getHand(player).size());
                playerZoneSizes.put(Zone.SABACC_HAND, game.getGameState().getSabaccHand(player).size());
                playerZoneSizes.put(Zone.RESERVE_DECK, game.getGameState().getReserveDeckSize(player));
                playerZoneSizes.put(Zone.FORCE_PILE, game.getGameState().getForcePile(player).size());
                playerZoneSizes.put(Zone.USED_PILE, game.getGameState().getUsedPile(player).size());
                playerZoneSizes.put(Zone.LOST_PILE, game.getGameState().getLostPile(player).size());
                playerZoneSizes.put(Zone.OUT_OF_PLAY, game.getGameState().getOutOfPlayPile(player).size());
                newZoneSizes.put(player, playerZoneSizes);
            }
        }
        if (!newZoneSizes.equals(_zoneSizes) || game.getGameState().isTableChangedSinceStatsSent()) {
            changed = true;
            _zoneSizes = newZoneSizes;
        }

        List<PhysicalCard> locations = _game.getGameState().getTopLocations();
        Map<Integer, Float> newDarkPowerAtLocations = new HashMap<Integer, Float>();
        Map<Integer, Float> newLightPowerAtLocations = new HashMap<Integer, Float>();
        for (PhysicalCard location : locations) {
            int locationIndex = location.getLocationZoneIndex();
            // If no cards at location, then -1 so User Interface doesn't show any power indicator
            float darkPowerAtLocation = -1;
            if (Filters.canSpot(game, null, Filters.and(Filters.owner(darkPlayer), Filters.or(Filters.character, Filters.starship, Filters.vehicle), Filters.at(location)))) {
                darkPowerAtLocation = modifiersQuerying.getTotalPowerAtLocation(gameState, location, darkPlayer, false, false);
            }
            if (!_darkPowerAtLocations.containsKey(locationIndex) || _darkPowerAtLocations.get(locationIndex) != darkPowerAtLocation) {
                changed = true;
            }
            newDarkPowerAtLocations.put(locationIndex, darkPowerAtLocation);

            float lightPowerAtLocation = -1;
            // If no cards at location, then -1 so User Interface doesn't show any power indicator
            if (Filters.canSpot(game, null, Filters.and(Filters.owner(lightPlayer), Filters.or(Filters.character, Filters.starship, Filters.vehicle), Filters.at(location)))) {
                lightPowerAtLocation = modifiersQuerying.getTotalPowerAtLocation(gameState, location, lightPlayer, false, false);
            }
            if (!_lightPowerAtLocations.containsKey(locationIndex) || _lightPowerAtLocations.get(locationIndex) != lightPowerAtLocation) {
                changed = true;
            }
            newLightPowerAtLocations.put(locationIndex, lightPowerAtLocation);
        }
        if (_darkPowerAtLocations.size() != newDarkPowerAtLocations.size() || _lightPowerAtLocations.size() != newLightPowerAtLocations.size()) {
            changed = true;
        }
        _darkPowerAtLocations = newDarkPowerAtLocations;
        _lightPowerAtLocations = newLightPowerAtLocations;

        return changed;
    }

    public Float getLightForceGeneration() {
        return _lightForceGeneration;
    }

    public Float getDarkForceGeneration() {
        return _darkForceGeneration;
    }

    public Float getLightBattlePower() {
        return _lightBattlePower;
    }

    public Float getDarkBattlePower() {
        return _darkBattlePower;
    }

    public Integer getLightBattleNumDestinyToPower() {
        return _lightBattleNumDestinyToPower;
    }

    public Integer getDarkBattleNumDestinyToPower() {
        return _darkBattleNumDestinyToPower;
    }

    public Integer getLightBattleNumBattleDestiny() {
        return _lightBattleNumBattleDestiny;
    }

    public Integer getDarkBattleNumBattleDestiny() {
        return _darkBattleNumBattleDestiny;
    }

    public Integer getLightBattleNumDestinyToAttrition() {
        return _lightBattleNumDestinyToAttrition;
    }

    public Integer getDarkBattleNumDestinyToAttrition() {
        return _darkBattleNumDestinyToAttrition;
    }

    public Float getLightBattleDamageRemaining() {
        return _lightBattleDamageRemaining;
    }

    public Float getDarkBattleDamageRemaining() {
        return _darkBattleDamageRemaining;
    }

    public Float getLightBattleAttritionRemaining() {
        return _lightBattleAttritionRemaining;
    }

    public Float getDarkBattleAttritionRemaining() {
        return _darkBattleAttritionRemaining;
    }

    public Boolean isLightImmuneToRemainingAttrition() {
        return _isLightImmuneToRemainingAttrition;
    }

    public Boolean isDarkImmuneToRemainingAttrition() {
        return _isDarkImmuneToRemainingAttrition;
    }

    public Float getLightSabaccTotal() {
        return _lightSabaccTotal;
    }

    public Float getDarkSabaccTotal() {
        return _darkSabaccTotal;
    }

    public Float getLightDuelOrLightsaberCombatTotal() {
        return _lightDuelOrLightsaberCombatTotal;
    }

    public Float getDarkDuelOrLightsaberCombatTotal() {
        return _darkDuelOrLightsaberCombatTotal;
    }

    public Integer getLightDuelOrLightsaberCombatNumDestiny() {
        return _lightDuelOrLightsaberCombatNumDestiny;
    }

    public Integer getDarkDuelOrLightsaberCombatNumDestiny() {
        return _darkDuelOrLightsaberCombatNumDestiny;
    }

    public Float getAttackingPowerOrFerocityInAttack() {
        return _attackingPowerOrFerocityInAttack;
    }

    public Float getDefendingPowerOrFerocityInAttack() {
        return _defendingPowerOrFerocityInAttack;
    }

    public Integer getAttackingNumDestinyInAttack() {
        return _attackingNumDestinyInAttack;
    }

    public Integer getDefendingNumDestinyInAttack() {
        return _defendingNumDestinyInAttack;
    }

    public Float getLightRaceTotal() {
        return _lightRaceTotal;
    }

    public Float getDarkRaceTotal() {
        return _darkRaceTotal;
    }

    public Float getLightPoliticsTotal() {
        return _lightPoliticsTotal;
    }

    public Float getDarkPoliticsTotal() {
        return _darkPoliticsTotal;
    }

    public Map<String, Map<Zone, Integer>> getZoneSizes() {
        return Collections.unmodifiableMap(_zoneSizes);
    }

    public Map<Integer, Float> getDarkPowerAtLocations() {
        return Collections.unmodifiableMap(_darkPowerAtLocations);
    }

    public Map<Integer, Float> getLightPowerAtLocations() {
        return Collections.unmodifiableMap(_lightPowerAtLocations);
    }

    /**
     * Makes a copy of this game stats object
     * @return a copy of this game stats object
     */
    public GameStats makeACopy(String playerToSendTo) {
        GameStats copy = new GameStats();
        copy._lightForceGeneration = _lightForceGeneration;
        copy._darkForceGeneration = _darkForceGeneration;
        copy._lightBattlePower = _lightBattlePower;
        copy._darkBattlePower = _darkBattlePower;
        copy._lightBattleNumDestinyToPower = _lightBattleNumDestinyToPower;
        copy._darkBattleNumDestinyToPower = _darkBattleNumDestinyToPower;
        copy._lightBattleNumBattleDestiny = _lightBattleNumBattleDestiny;
        copy._darkBattleNumBattleDestiny = _darkBattleNumBattleDestiny;
        copy._lightBattleNumDestinyToAttrition = _lightBattleNumDestinyToAttrition;
        copy._darkBattleNumDestinyToAttrition = _darkBattleNumDestinyToAttrition;
        copy._lightBattleDamageRemaining = _lightBattleDamageRemaining;
        copy._darkBattleDamageRemaining = _darkBattleDamageRemaining;
        copy._lightBattleAttritionRemaining = _lightBattleAttritionRemaining;
        copy._darkBattleAttritionRemaining = _darkBattleAttritionRemaining;
        copy._isLightImmuneToRemainingAttrition = _isLightImmuneToRemainingAttrition;
        copy._isDarkImmuneToRemainingAttrition = _isDarkImmuneToRemainingAttrition;
        copy._lightDuelOrLightsaberCombatTotal = _lightDuelOrLightsaberCombatTotal;
        copy._darkDuelOrLightsaberCombatTotal = _darkDuelOrLightsaberCombatTotal;
        copy._lightDuelOrLightsaberCombatNumDestiny = _lightDuelOrLightsaberCombatNumDestiny;
        copy._darkDuelOrLightsaberCombatNumDestiny = _darkDuelOrLightsaberCombatNumDestiny;
        copy._attackingPowerOrFerocityInAttack = _attackingPowerOrFerocityInAttack;
        copy._defendingPowerOrFerocityInAttack = _defendingPowerOrFerocityInAttack;
        copy._attackingNumDestinyInAttack = _attackingNumDestinyInAttack;
        copy._defendingNumDestinyInAttack = _defendingNumDestinyInAttack;
        copy._lightRaceTotal = _lightRaceTotal;
        copy._darkRaceTotal = _darkRaceTotal;
        copy._lightPoliticsTotal = _lightPoliticsTotal;
        copy._darkPoliticsTotal = _darkPoliticsTotal;

        // Only copy sabacc total if sending to that player (or sabacc hands are revealed)
        if (_game != null && playerToSendTo != null) {

            SabaccState sabaccState = _game.getGameState().getSabaccState();
            if (sabaccState != null && sabaccState.isHandsRevealed()) {
                copy._lightSabaccTotal = _lightSabaccTotal;
                copy._darkSabaccTotal = _darkSabaccTotal;
            }
            else if (_game.getLightPlayer().equals(playerToSendTo)) {
                copy._lightSabaccTotal = _lightSabaccTotal;
            }
            else if (_game.getDarkPlayer().equals(playerToSendTo)) {
                copy._darkSabaccTotal = _darkSabaccTotal;
            }
        }

        copy._zoneSizes = _zoneSizes;
        copy._darkPowerAtLocations = _darkPowerAtLocations;
        copy._lightPowerAtLocations = _lightPowerAtLocations;
        return copy;
    }
}
