package com.gempukku.swccgo.ai;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class AiRegistry {

    private static final Map<String, Map<String, SwccgAiController>> AI_PLAYERS_BY_GAME = new ConcurrentHashMap<>();

    public static void register(String gameId, String playerId, SwccgAiController ai) {
        if (gameId == null || playerId == null || ai == null) {
            return;
        }
        AI_PLAYERS_BY_GAME.computeIfAbsent(gameId, key -> new ConcurrentHashMap<>()).put(playerId, ai);
    }

    public static SwccgAiController get(String gameId, String playerId) {
        if (gameId == null || playerId == null) {
            return null;
        }
        Map<String, SwccgAiController> gameAis = AI_PLAYERS_BY_GAME.get(gameId);
        if (gameAis == null) {
            return null;
        }
        return gameAis.get(playerId);
    }

    public static boolean isAi(String gameId, String playerId) {
        if (gameId == null || playerId == null) {
            return false;
        }
        Map<String, SwccgAiController> gameAis = AI_PLAYERS_BY_GAME.get(gameId);
        return gameAis != null && gameAis.containsKey(playerId);
    }

    public static void unregisterGame(String gameId) {
        if (gameId == null) {
            return;
        }
        AI_PLAYERS_BY_GAME.remove(gameId);
    }
}
