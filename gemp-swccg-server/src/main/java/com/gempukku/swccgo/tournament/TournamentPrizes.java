package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.game.CardCollection;

public interface TournamentPrizes {
    public CardCollection getPrizeForTournament(PlayerStanding playerStanding, int playersCount);
    public String getRegistryRepresentation();
    public String getPrizeDescription();
}
