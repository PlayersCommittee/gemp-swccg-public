package com.gempukku.swccgo.game;

import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class SwccgGameMediatorTests {

    static final String LS_PLAYER = "light";
    static final String DS_PLAYER = "dark";

    @Test
    @DisplayName("Game timer is able to be extended multiple times")
    void extendGameTimerMore() throws Exception {
        final int minutes = 30;

        SwccgGameParticipant lightParp = new SwccgGameParticipant(LS_PLAYER, new SwccgDeck("fish"));
        SwccgGameParticipant darkParp = new SwccgGameParticipant(DS_PLAYER, new SwccgDeck("fish"));
        SwccgGameMediator subject = new SwccgGameMediator("1",
                mock(SwccgFormat.class),
                new SwccgGameParticipant[]{lightParp, darkParp},
                mock(SwccgCardBlueprintLibrary.class),
                50,
                false,
                false,
                true,
                true,
                30000,
                true
        );

        Player lsPlayer = new Player(1, DS_PLAYER, "3",
                "", 0, null, "8.8.8.8", "8.8.8.8"
        );
        Player dsPlayer = new Player(1, LS_PLAYER, "3",
                "", 0, null, "8.8.8.8", "8.8.8.8"
        );


        int privateSecondsExtended = (int) ReflectionUtils.tryToReadFieldValue(SwccgGameMediator.class, "_secondsGameTimerExtended", subject).get();

        assertEquals(0, privateSecondsExtended);

        subject.extendGameTimer(lsPlayer, minutes);
        subject.extendGameTimer(dsPlayer, minutes);

        privateSecondsExtended = (int) ReflectionUtils.tryToReadFieldValue(SwccgGameMediator.class, "_secondsGameTimerExtended", subject).get();
        assertEquals(minutes * 60, privateSecondsExtended);

        subject.extendGameTimer(lsPlayer, minutes);
        subject.extendGameTimer(dsPlayer, minutes);

        privateSecondsExtended = (int) ReflectionUtils.tryToReadFieldValue(SwccgGameMediator.class, "_secondsGameTimerExtended", subject).get();
        assertEquals(minutes * 60 * 2, privateSecondsExtended);
    }

}