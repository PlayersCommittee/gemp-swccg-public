package com.gempukku.swccgo.ai;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class AiRegistry {

    private static final Map<String, SwccgAiController> AI_PLAYERS = new ConcurrentHashMap<>();

    public static void register(String playerId, SwccgAiController ai) {
        AI_PLAYERS.put(playerId, ai);
    }

    public static SwccgAiController get(String playerId) {
        return AI_PLAYERS.get(playerId);
    }

    public static boolean isAi(String playerId) {
        return AI_PLAYERS.containsKey(playerId);
    }
}
