package com.gempukku.swccgo.draft2;

import com.gempukku.swccgo.game.CardCollection;

public interface DraftChoiceDefinition {
    int cardCountForChoiceId(String choiceId);

    Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, CardCollection currentCards, String currentChoice);

    CardCollection getCardsForChoiceId(String choiceId, long seed, int stage, CardCollection currentCards);
}
