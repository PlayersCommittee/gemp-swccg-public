package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.timing.GameStats;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;

public class EventSerializer {
    public Node serializeEvent(Document doc, GameEvent gameEvent) {
        Element eventElem = doc.createElement("ge");
        eventElem.setAttribute("type", gameEvent.getType().name());
        if (gameEvent.getBlueprintId() != null)
            eventElem.setAttribute("blueprintId", gameEvent.getBlueprintId());
        if (gameEvent.getTestingText() != null)
            eventElem.setAttribute("testingText", gameEvent.getTestingText());
        if (gameEvent.getBackSideTestingText() != null)
            eventElem.setAttribute("backSideTestingText", gameEvent.getBackSideTestingText());
        if (gameEvent.getCardId() != null)
            eventElem.setAttribute("cardId", gameEvent.getCardId().toString());
        if (gameEvent.getIndex() != null)
            eventElem.setAttribute("index", gameEvent.getIndex().toString());
        if (gameEvent.getZoneOwnerId() != null)
            eventElem.setAttribute("zoneOwnerId", gameEvent.getZoneOwnerId());
        if (gameEvent.getSystemName() != null)
            eventElem.setAttribute("systemName", gameEvent.getSystemName());
        if (gameEvent.getLocationIndex() != null)
            eventElem.setAttribute("locationIndex", gameEvent.getLocationIndex().toString());
        if (gameEvent.getLocationIndexes() != null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Integer locationIndex : gameEvent.getLocationIndexes()) {
                if (!first) sb.append(",");
                sb.append(locationIndex);
                first = false;
            }
            eventElem.setAttribute("locationIndexes", sb.toString());
        }
        if (gameEvent.getParticipantId() != null)
            eventElem.setAttribute("participantId", gameEvent.getParticipantId());
        if (gameEvent.getAllParticipantIds() != null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String participantId : gameEvent.getAllParticipantIds()) {
                if (!first) sb.append(",");
                sb.append(participantId);
                first = false;
            }
            eventElem.setAttribute("allParticipantIds", sb.toString());
        }
        if (gameEvent.getPhase() != null)
            eventElem.setAttribute("phase", gameEvent.getPhase());
        if (gameEvent.getTargetCardId() != null)
            eventElem.setAttribute("targetCardId", gameEvent.getTargetCardId().toString());
        if (gameEvent.getZone() != null)
            eventElem.setAttribute("zone", gameEvent.getZone().name());
        if (gameEvent.isInverted() != null)
            eventElem.setAttribute("inverted", gameEvent.isInverted().toString());
        if (gameEvent.isSideways() != null)
            eventElem.setAttribute("sideways", gameEvent.isSideways().toString());
        if (gameEvent.isFrozen() != null)
            eventElem.setAttribute("frozen", gameEvent.isFrozen().toString());
        if (gameEvent.isSuspendedOrTurnedOff() != null)
            eventElem.setAttribute("suspended", gameEvent.isSuspendedOrTurnedOff().toString());
        if (gameEvent.isCollapsed() != null)
            eventElem.setAttribute("collapsed", gameEvent.isCollapsed().toString());
        if (gameEvent.getCount() != null)
            eventElem.setAttribute("count", gameEvent.getCount().toString());
        if (gameEvent.getDestinyText() != null)
            eventElem.setAttribute("destinyText", gameEvent.getDestinyText());
        if (gameEvent.getPlayerAttacking() != null)
            eventElem.setAttribute("playerAttacking", gameEvent.getPlayerAttacking());
        if (gameEvent.getPlayerDefending() != null)
            eventElem.setAttribute("playerDefending", gameEvent.getPlayerDefending());
        if (gameEvent.getOtherCardIds() != null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (int cardId : gameEvent.getOtherCardIds()) {
                if (!first) sb.append(",");
                sb.append(cardId);
                first = false;
            }
            eventElem.setAttribute("otherCardIds", sb.toString());
        }
        if (gameEvent.getOtherCardIds2() != null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (int cardId : gameEvent.getOtherCardIds2()) {
                if (!first) sb.append(",");
                sb.append(cardId);
                first = false;
            }
            eventElem.setAttribute("otherCardIds2", sb.toString());
        }
        if (gameEvent.getMessage() != null)
            eventElem.setAttribute("message", gameEvent.getMessage());
        if (gameEvent.getGameStats() != null) {
            GameStats gameStats = gameEvent.getGameStats();

            eventElem.setAttribute("darkForceGeneration", GuiUtils.formatAsString(gameStats.getDarkForceGeneration(), true));
            eventElem.setAttribute("lightForceGeneration", GuiUtils.formatAsString(gameStats.getLightForceGeneration(), true));
            eventElem.setAttribute("darkBattlePower", GuiUtils.formatAsString(gameStats.getDarkBattlePower(), true));
            eventElem.setAttribute("lightBattlePower", GuiUtils.formatAsString(gameStats.getLightBattlePower(), true));
            eventElem.setAttribute("darkBattleNumDestinyToPower", String.valueOf(gameStats.getDarkBattleNumDestinyToPower()));
            eventElem.setAttribute("lightBattleNumDestinyToPower", String.valueOf(gameStats.getLightBattleNumDestinyToPower()));
            eventElem.setAttribute("darkBattleNumBattleDestiny", String.valueOf(gameStats.getDarkBattleNumBattleDestiny()));
            eventElem.setAttribute("lightBattleNumBattleDestiny", String.valueOf(gameStats.getLightBattleNumBattleDestiny()));
            eventElem.setAttribute("darkBattleNumDestinyToAttrition", String.valueOf(gameStats.getDarkBattleNumDestinyToAttrition()));
            eventElem.setAttribute("lightBattleNumDestinyToAttrition", String.valueOf(gameStats.getLightBattleNumDestinyToAttrition()));
            eventElem.setAttribute("darkBattleDamageRemaining", GuiUtils.formatAsString(gameStats.getDarkBattleDamageRemaining(), true));
            eventElem.setAttribute("lightBattleDamageRemaining", GuiUtils.formatAsString(gameStats.getLightBattleDamageRemaining(), true));
            eventElem.setAttribute("darkBattleAttritionRemaining", GuiUtils.formatAsString(gameStats.getDarkBattleAttritionRemaining(), true));
            eventElem.setAttribute("lightBattleAttritionRemaining", GuiUtils.formatAsString(gameStats.getLightBattleAttritionRemaining(), true));
            eventElem.setAttribute("darkImmuneToRemainingAttrition", String.valueOf(gameStats.isDarkImmuneToRemainingAttrition()));
            eventElem.setAttribute("lightImmuneToRemainingAttrition", String.valueOf(gameStats.isLightImmuneToRemainingAttrition()));
            eventElem.setAttribute("darkSabaccTotal", GuiUtils.formatAsString(gameStats.getDarkSabaccTotal(), true));
            eventElem.setAttribute("lightSabaccTotal", GuiUtils.formatAsString(gameStats.getLightSabaccTotal(), true));
            eventElem.setAttribute("darkDuelOrLightsaberCombatTotal", GuiUtils.formatAsString(gameStats.getDarkDuelOrLightsaberCombatTotal(), true));
            eventElem.setAttribute("lightDuelOrLightsaberCombatTotal", GuiUtils.formatAsString(gameStats.getLightDuelOrLightsaberCombatTotal(), true));
            eventElem.setAttribute("darkDuelOrLightsaberCombatNumDestiny", String.valueOf(gameStats.getDarkDuelOrLightsaberCombatNumDestiny()));
            eventElem.setAttribute("lightDuelOrLightsaberCombatNumDestiny", String.valueOf(gameStats.getLightDuelOrLightsaberCombatNumDestiny()));
            eventElem.setAttribute("attackingPowerOrFerocityInAttack", GuiUtils.formatAsString(gameStats.getAttackingPowerOrFerocityInAttack(), true));
            eventElem.setAttribute("defendingPowerOrFerocityInAttack", GuiUtils.formatAsString(gameStats.getDefendingPowerOrFerocityInAttack(), true));
            eventElem.setAttribute("attackingNumDestinyInAttack", String.valueOf(gameStats.getAttackingNumDestinyInAttack()));
            eventElem.setAttribute("defendingNumDestinyInAttack", String.valueOf(gameStats.getDefendingNumDestinyInAttack()));
            eventElem.setAttribute("darkRaceTotal", GuiUtils.formatAsString(gameStats.getDarkRaceTotal(), true));
            eventElem.setAttribute("lightRaceTotal", GuiUtils.formatAsString(gameStats.getLightRaceTotal(), true));
            eventElem.setAttribute("darkPoliticsTotal", GuiUtils.formatAsString(gameStats.getDarkPoliticsTotal(), true));
            eventElem.setAttribute("lightPoliticsTotal", GuiUtils.formatAsString(gameStats.getLightPoliticsTotal(), true));


            for (Map.Entry<String, Map<Zone, Integer>> playerZoneSizes : gameStats.getZoneSizes().entrySet()) {
                final Element playerZonesElem = doc.createElement("playerZones");
                playerZonesElem.setAttribute("name", playerZoneSizes.getKey());

                for (Map.Entry<Zone, Integer> zoneSizes : playerZoneSizes.getValue().entrySet()) {
                    playerZonesElem.setAttribute(zoneSizes.getKey().name(), zoneSizes.getValue().toString());
                }
                eventElem.appendChild(playerZonesElem);
            }

            final Element darkPowerAtLocationsElem = doc.createElement("darkPowerAtLocations");
            for (Map.Entry<Integer, Float> darkPowerAtLocations : gameStats.getDarkPowerAtLocations().entrySet()) {
                darkPowerAtLocationsElem.setAttribute("locationIndex" + darkPowerAtLocations.getKey(), GuiUtils.formatAsString(darkPowerAtLocations.getValue(), true));
            }
            eventElem.appendChild(darkPowerAtLocationsElem);

            final Element lightPowerAtLocationsElem = doc.createElement("lightPowerAtLocations");
            for (Map.Entry<Integer, Float> lightPowerAtLocations : gameStats.getLightPowerAtLocations().entrySet()) {
                lightPowerAtLocationsElem.setAttribute("locationIndex" + lightPowerAtLocations.getKey(), GuiUtils.formatAsString(lightPowerAtLocations.getValue(), true));
            }
            eventElem.appendChild(lightPowerAtLocationsElem);

        }
        if (gameEvent.getAwaitingDecision() != null) {
            AwaitingDecision decision = gameEvent.getAwaitingDecision();
            eventElem.setAttribute("id", String.valueOf(decision.getAwaitingDecisionId()));
            eventElem.setAttribute("decisionType", decision.getDecisionType().name());
            if (decision.getText() != null)
                eventElem.setAttribute("text", decision.getText());
            for (Map.Entry<String, Object> paramEntry : decision.getDecisionParameters().entrySet()) {
                if (paramEntry.getValue() instanceof String) {
                    Element decisionParam = doc.createElement("parameter");
                    decisionParam.setAttribute("name", paramEntry.getKey());
                    decisionParam.setAttribute("value", (String) paramEntry.getValue());
                    eventElem.appendChild(decisionParam);
                } else if (paramEntry.getValue() instanceof String[]) {
                    for (String value : (String[]) paramEntry.getValue()) {
                        Element decisionParam = doc.createElement("parameter");
                        decisionParam.setAttribute("name", paramEntry.getKey());
                        decisionParam.setAttribute("value", value);
                        eventElem.appendChild(decisionParam);
                    }
                }
            }
        }

        return eventElem;
    }
}
