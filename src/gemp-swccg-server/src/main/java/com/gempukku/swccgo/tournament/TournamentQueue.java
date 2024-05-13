package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

public interface TournamentQueue {
    public int getCost();

    public String getFormat();

    public CollectionType getCollectionType();

    public String getTournamentQueueName();

    public String getPrizesDescription();

    public String getPairingDescription();

    public String getStartCondition();

    public boolean isRequiresDeck();

    public boolean process(TournamentQueueCallback tournamentQueueCallback, CollectionsManager collectionsManager);

    public void joinPlayer(CollectionsManager collectionsManager, Player player, SwccgDeck deck);

    public void leavePlayer(CollectionsManager collectionsManager, Player player);

    public void leaveAllPlayers(CollectionsManager collectionsManager);

    public int getPlayerCount();

    public boolean isPlayerSignedUp(String player);

    public boolean isJoinable();
}
