package com.gempukku.swccgo.draft;

import com.gempukku.swccgo.game.CardCollection;

public interface DraftChannelVisitor {
    public void channelNumber(int channelNumber);
    public void timeLeft(long timeLeft);
    public void cardChoice(CardCollection cardCollection);
    public void noCardChoice();
    public void chosenCards(CardCollection cardCollection);
}
