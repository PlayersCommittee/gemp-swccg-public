package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.draft.Draft;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.List;

public interface Tournament {
    public enum Stage {
        DRAFT("Drafting"), DECK_BUILDING("Deck building"), PLAYING_GAMES("Playing games"), FINISHED("Finished");

        private String _humanReadable;

        Stage(String humanReadable) {
            _humanReadable = humanReadable;
        }

        public String getHumanReadable() {
            return _humanReadable;
        }
    }

    public String getTournamentId();
    public String getFormat();
    public CollectionType getCollectionType();
    public String getTournamentName();
    public String getPlayOffSystem();

    public Stage getTournamentStage();
    public int getCurrentRound();
    public int getPlayersInCompetitionCount();

    public boolean advanceTournament(TournamentCallback tournamentCallback, CollectionsManager collectionsManager);

    public void reportGameFinished(String winner, String loser, String winnerSide, String loserSide);

    public void playerChosenCard(String playerName, String cardId);
    public void playerSummittedDeck(String player, SwccgDeck deck);
    public SwccgDeck getPlayerDeck(String player);
    public boolean dropPlayer(String player);

    public Draft getDraft();

    public List<PlayerStanding> getCurrentStandings();

    public boolean isPlayerInCompetition(String player);
}
