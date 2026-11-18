package com.gempukku.swccgo.chat;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChatRoomMediatorCleanupTests {
    @Test
    public void cleanupKeepsInGamePlayersInTheHall() throws Exception {
        ChatRoomMediator room = new ChatRoomMediator("Game Hall", true, 1, false, null, true, false);
        room.joinUser("alice", false, false);
        room.joinUser("bob", false, false);

        Thread.sleep(1100);
        room.cleanup(playerId -> "alice".equals(playerId));

        assertTrue(room.getUsersInRoom().contains("alice"));
        assertFalse(room.getUsersInRoom().contains("bob"));

        room.cleanup(playerId -> false);
        assertFalse("Idle players drop once they no longer have a live game connection",
                room.getUsersInRoom().contains("alice"));
    }
}
