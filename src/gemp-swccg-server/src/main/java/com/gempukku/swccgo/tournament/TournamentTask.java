package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.collection.CollectionsManager;

public interface TournamentTask {
    public void executeTask(TournamentCallback tournamentCallback, CollectionsManager collectionsManager);

    public long getExecuteAfter();
}
