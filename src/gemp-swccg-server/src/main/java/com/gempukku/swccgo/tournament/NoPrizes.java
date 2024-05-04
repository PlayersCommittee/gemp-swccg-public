package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.game.CardCollection;

public class NoPrizes implements TournamentPrizes{
    @Override
    public CardCollection getPrizeForTournament(PlayerStanding playerStanding, int playersCount) {
        return null;
    }

    @Override
    public String getRegistryRepresentation() {
        return null;
    }

    @Override
    public String getPrizeDescription() {
        return "No prizes";
    }
}
