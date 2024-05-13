package com.gempukku.swccgo.draft;

import com.gempukku.swccgo.SubscriptionConflictException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.tournament.TournamentCallback;

public interface Draft {
    public void advanceDraft(TournamentCallback draftCallback);

    public void playerChosenCard(String playerName, String cardId);

    public void signUpForDraft(String playerName, DraftChannelVisitor draftChannelVisitor);

    public DraftCommunicationChannel getCommunicationChannel(String playerName, int channelNumber)  throws SubscriptionExpiredException, SubscriptionConflictException;

    public DraftCardChoice getCardChoice(String playerName);
    public CardCollection getChosenCards(String player);

    public boolean isFinished();
}
