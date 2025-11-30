package com.gempukku.swccgo.draft2;

import com.gempukku.swccgo.draft2.builder.CardCollectionProducer;
import com.gempukku.swccgo.draft2.builder.DraftChoiceBuilder;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.packagedProduct.EnhancedPremierePack_DarthVader;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultSoloDraft implements SoloDraft {
    private String _format;
    private int _fixedCardCount;
    private int _objChoiceCountPerSide;
    private int _choiceCountPerSide;
    private List<String> _lightObjCards;
    private List<String> _darkObjCards;
    private CardCollectionProducer _newCollection;
    private List<DraftChoiceDefinition> _draftChoiceDefinitions;

    public DefaultSoloDraft(String format, int fixedCardCount, int choiceCount, int objChoiceCount, List<String> lightObjCards, List<String> darkObjCards, CardCollectionProducer newCollection, List<DraftChoiceDefinition> draftChoiceDefinitions) {
        _format = format;
        _fixedCardCount = fixedCardCount;
        _lightObjCards = lightObjCards;
        _darkObjCards = darkObjCards;
        _objChoiceCountPerSide = objChoiceCount;
        _choiceCountPerSide = choiceCount;
        _newCollection = newCollection;
        _draftChoiceDefinitions = draftChoiceDefinitions;
    }

    @Override
    public CardCollection initializeNewCollection(long seed) {
        return (_newCollection != null) ? _newCollection.getCardCollection(seed) : null;
    }

    private int calculateStageOffset(CardCollection currentCards, int stage) {
        if (_lightObjCards == null || _darkObjCards == null) {
            // non-obj based drafts don't need an offset
            return stage;
        }
        int howManyObjPicked = 0;
        ArrayList<String> lightObjCards = new ArrayList<>(_lightObjCards);
        ArrayList<String> darkObjCards = new ArrayList<>(_darkObjCards);
        ArrayList<String> lightObjCardsFound = new ArrayList<>();
        ArrayList<String> darkObjCardsFound = new ArrayList<>();
        for (int i = 0; i < _objChoiceCountPerSide; i++) {
            for (String objCardId : lightObjCards) {
                if (currentCards.getItemCount(objCardId) > 0 && lightObjCardsFound.contains(objCardId) == false) {
                    howManyObjPicked++;
                    lightObjCardsFound.add(objCardId);
                }
            }
            for (String objCardId : darkObjCards) {
                if (currentCards.getItemCount(objCardId) > 0 && darkObjCardsFound.contains(objCardId) == false) {
                    howManyObjPicked++;
                    darkObjCardsFound.add(objCardId);
                }
            }
        }
        // For obj-based drafts, some stages award many cards per pick.  Therefore we have to offset the stage
        // by that many cards (differs per objective) so that the pack sizes can still oscillate properly.
        int offset = howManyObjPicked;
        if (offset % _objChoiceCountPerSide == 0) {
            return stage - offset;
        }
        return stage;
    }

    @Override
    public Iterable<DraftChoice> getAvailableChoices(long seed, int stage, CardCollection currentCards, String currentChoice) {
        int fixedStage = calculateStageOffset(currentCards, stage);
        return _draftChoiceDefinitions.get(stage).getDraftChoice(seed, fixedStage, currentCards, currentChoice);
    }

    @Override
    public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage, CardCollection currentCards) {
        int fixedStage = calculateStageOffset(currentCards, stage);
        return _draftChoiceDefinitions.get(stage).getCardsForChoiceId(choiceId, seed, fixedStage, currentCards);
    }

    @Override
    public boolean hasNextStage(long seed, int stage) {
        return stage + 1 < _draftChoiceDefinitions.size();
    }

    @Override
    public int stageCount() {
        return _draftChoiceDefinitions.size();
    }

    @Override
    public int fixedCardCount() {
        return _fixedCardCount;
    }

    @Override
    public int currentStage(CardCollection currentCards) {
        // We can compute the stage from the collection using the following logic:
        // 1) Some drafts have a fixed pool size to start from, so ignore those cards.
        // 2) Other drafts  have dynamic starting fixed pools, for these:
        //    2a) First determine how far into the draft by the number of fixed choices made so far
        //    2b) Then, for each choice made, get the fixed count from that choice from the choice class
        //    2c) Ignore the dynamic fixed size along with the static fixed size
        // 3) Now, iterate the choices, for each one determine the expected card count for that choice
        // 4) If the expected count ever exceeds the current count -> you've found the correct stage for this player
        // This logic seems complex, but we're doing it to avoid having to store extra data in each player's collection db.
        int currentCardCount = 0;
        Map<String, CardCollection.Item> cards = currentCards.getAll();
        for (CardCollection.Item item : cards.values())
        {
            currentCardCount += currentCards.getItemCount(item.getBlueprintId());
        }
        if (_lightObjCards == null || _darkObjCards == null) {
            // for non-obj drafts the stage is the collection size
            return currentCardCount;
        }
        int fixedCountToIgnore = _fixedCardCount;
        // this part is especially weird.  The problem is that in obj+cube draft players get obj draft packs.
        // each pack has a different size.  So we can't just divide the collection count to get stage.
        // instead we have to collapse the obj packs back into one card just for this calculation.
        // we do that by finding the expected cards (not finding them just means we aren't at that stage yet).
        // for each card we then add to the fixed card count the objective pack count - 1.
        // the -1 leaves 1 card left for the stage division math.
        ArrayList<String> lightObjCards = new ArrayList<>(_lightObjCards);
        ArrayList<String> darkObjCards = new ArrayList<>(_darkObjCards);
        ArrayList<String> lightObjCardsFound = new ArrayList<>();
        ArrayList<String> darkObjCardsFound = new ArrayList<>();
        int indexOfLightObjChoice = 0;
        int indexOfDarkObjChoice = _objChoiceCountPerSide + _choiceCountPerSide;
        DraftChoiceDefinition lightObjChoice = _draftChoiceDefinitions.get(indexOfLightObjChoice);
        DraftChoiceDefinition darkObjChoice = _draftChoiceDefinitions.get(indexOfDarkObjChoice);
        for (int i = 0; i < _objChoiceCountPerSide; i++) {
            for (String objCardId : lightObjCards) {
                if (currentCards.getItemCount(objCardId) > 0 && lightObjCardsFound.contains(objCardId) == false) {
                    int objCardCount = lightObjChoice.cardCountForChoiceId(objCardId)-1;
                    fixedCountToIgnore += objCardCount;
                    lightObjCardsFound.add(objCardId);
                }
            }
            for (String objCardId : darkObjCards) {
                if (currentCards.getItemCount(objCardId) > 0 && darkObjCardsFound.contains(objCardId) == false) {
                    fixedCountToIgnore += (darkObjChoice.cardCountForChoiceId(objCardId)-1);
                    darkObjCardsFound.add(objCardId);
                }
            }
        }
        return currentCardCount - fixedCountToIgnore;
    }

    @Override
    public String getFormat() {
        return _format;
    }
}
