package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SingleEliminationOnDemandPrizes implements TournamentPrizes{
    private List<String> _promos = new ArrayList<String>();
    private String _registryRepresentation;

    public SingleEliminationOnDemandPrizes(String registryRepresentation) {
        _registryRepresentation = registryRepresentation;
        RarityReader rarityReader = new RarityReader();

        for (int i = 1; i <= 19; i++) {
            SetRarity setRarity = rarityReader.getSetRarity(String.valueOf(i));
            _promos.addAll(setRarity.getCardsOfRarity(Rarity.PM));
        }

    }

    @Override
    public CardCollection getPrizeForTournament(PlayerStanding playerStanding, int playersCount) {
        DefaultCardCollection tournamentPrize = new DefaultCardCollection();
        if (playerStanding.getPoints() == 3) {
            tournamentPrize.addItem("(S)Booster Choice -- Premiere - Cloud City", 2);
        } else if (playerStanding.getPoints() == 2) {
            tournamentPrize.addItem("(S)Booster Choice -- Premiere - Cloud City", 1);
            tournamentPrize.addItem(getRandom(_promos), 1);
        } else if (playerStanding.getPoints() == 1) {
            tournamentPrize.addItem("(S)Booster Choice -- Premiere - Cloud City", 1);
        }

        if (tournamentPrize.getAll().isEmpty())
            return null;
        return tournamentPrize;
    }

    private String getRandom(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    @Override
    public String getRegistryRepresentation() {
        return _registryRepresentation;
    }

    @Override
    public String getPrizeDescription() {
        return "<div class='prizeHint' value='3 wins - 2 boosters, 2 wins - 1 booster and a random promo, 1 win - 1 booster'>2-(1+promo)-1-1</div>";
    }
}
