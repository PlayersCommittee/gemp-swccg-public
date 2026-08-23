package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.communication.UserFeedback;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultSwccgGameTests {

    DefaultSwccgGame game;
    static final String lsPlayer = "light";
    static final String dsPlayer = "dark";


    @BeforeEach
    void setup() {
        SwccgDeck deck = new SwccgDeck("fish");
        SwccgFormat format = mock(SwccgFormat.class);
        when(format.hasJpSealedRule()).thenReturn(false);
        Map<String, SwccgDeck> playerDecks = new HashMap<>();
        playerDecks.put(lsPlayer, deck);
        playerDecks.put(dsPlayer, deck);

        game = new DefaultSwccgGame(format, playerDecks, mock(UserFeedback.class), mock(SwccgCardBlueprintLibrary.class));
    }

    @Test
    @DisplayName("requestExtendGameTimer sets players requested time to minutes when uninitialized")
    void testRequestExtendGameTimerSetsPlayersRequestedTimeToMinutesWhenUninitialized() throws Exception {
        final int minutes = 30;
        game.requestExtendGameTimer(lsPlayer, minutes);
        Map<String, Integer> field = (Map<String, Integer>) ReflectionUtils.tryToReadFieldValue(DefaultSwccgGame.class, "_requestedExtendGameTimer", game).get();
        assertEquals(field.get(lsPlayer), minutes);
    }

    @Test
    @DisplayName("requestExtendGameTimer increases time for each player when all players request extension")
    void requestExtendGameIncreasesTimeForBoth() throws Exception {
        final int minutes = 30;

        game.requestExtendGameTimer(lsPlayer, minutes);
        game.requestExtendGameTimer(lsPlayer, minutes);

        Map<String, Integer> field = (Map<String, Integer>) ReflectionUtils.tryToReadFieldValue(DefaultSwccgGame.class, "_requestedExtendGameTimer", game).get();
        assertEquals(field.get(lsPlayer), minutes * 2);

    }

    @Test
    @DisplayName("getGameTimerExtendedInMinutes returns lowest amount requested")
    void getGameTimerExtendedInMinutesReturnsTheLowestRequestedTime() {
        final int lowMinutes = 20;
        final int highMinutes = 60;

        game.requestExtendGameTimer(dsPlayer, highMinutes);
        game.requestExtendGameTimer(lsPlayer, lowMinutes);

        assertEquals(game.getGameTimerExtendedInMinutes(), lowMinutes);
    }

    @Test
    @DisplayName("getGameTimerExtendedInMinutes no time extension when not enough players request extension")
    void getGameTimerExtendedInMinutesNotEnoughRequests() {
        final int minutes = 30;
        game.requestExtendGameTimer(lsPlayer, minutes);
        assertEquals(game.getGameTimerExtendedInMinutes(), 0);
    }
}
