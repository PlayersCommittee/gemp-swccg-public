package com.gempukku.swccgo.game;

import com.gempukku.swccgo.game.state.GameEvent;

import java.util.Map;

public interface ParticipantCommunicationVisitor {
    public void visitChannelNumber(int channelNumber);

    public void visitClock(Map<String, Integer> secondsLeft);

    public void visitGameEvent(GameEvent gameEvent);
}
