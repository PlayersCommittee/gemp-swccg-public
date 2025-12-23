package com.gempukku.swccgo.draft2.builder;

import com.gempukku.swccgo.AbstractServer;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.draft2.DraftChoiceDefinition;
import com.gempukku.swccgo.draft2.SoloDraft;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SortAndFilterCards;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.cards.packs.SetDefinition;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.smartcardio.Card;
import java.util.*;

public class DraftChoiceBuilder {
    public static final int HIGH_ENOUGH_PRIME_NUMBER = 9497;
    private CollectionsManager _collectionsManager;
    private SwccgCardBlueprintLibrary _cardLibrary;
    private SwccgoFormatLibrary _formatLibrary;
    private Map<String, SetRarity> _rarities;
    private SortAndFilterCards _sortAndFilterCards;

    public DraftChoiceBuilder(CollectionsManager collectionsManager, SwccgCardBlueprintLibrary cardLibrary,
                              SwccgoFormatLibrary formatLibrary, Map<String, SetRarity> rarities) {
        _collectionsManager = collectionsManager;
        _cardLibrary = cardLibrary;
        _formatLibrary = formatLibrary;
        _rarities = rarities;
        _sortAndFilterCards = new SortAndFilterCards();
    }

    public DraftChoiceDefinition buildDraftChoiceDefinition(JSONObject choiceDefinition) {
        return constructDraftChoiceDefinition(choiceDefinition);
    }

    private DraftChoiceDefinition constructDraftChoiceDefinition(JSONObject choiceDefinition) {
        String choiceDefinitionType = (String) choiceDefinition.get("type");
        JSONObject data = (JSONObject) choiceDefinition.get("data");
        if (choiceDefinitionType.equals("singleCollectionPick"))
            return buildSingleCollectionPickDraftChoiceDefinition(data);
        else if (choiceDefinitionType.equals("weightedSwitch"))
            return buildWeightedSwitchDraftChoiceDefinition(data);
        else if (choiceDefinitionType.equals("multipleCardPick"))
            return buildMultipleCardPickDraftChoiceDefinition(data);
        else if (choiceDefinitionType.equals("cubePackPick"))
            return buildCubePackPickDraftChoiceDefinition(data);
        else if (choiceDefinitionType.equals("cubePackObjPick"))
            return buildCubePackObjPickDraftChoiceDefinition(data);
        else if (choiceDefinitionType.equals("randomSwitch"))
            return buildRandomSwitchDraftChoiceDeifinition(data);
        else if (choiceDefinitionType.equals("filterPick"))
            return buildFilterPickDraftChoiceDefinition(data);
        else
            throw new RuntimeException("Unknown choiceDefinitionType: " + choiceDefinitionType);
    }

    private DraftChoiceDefinition buildFilterPickDraftChoiceDefinition(JSONObject data) {
        final int optionCount = ((Number) data.get("optionCount")).intValue();
        String filter = (String) data.get("filter");

        Collection<CardCollection.Item> items = _collectionsManager.getDefaultCollection(false).getAll().values();

        final List<CardCollection.Item> possibleCards = _sortAndFilterCards.process(filter, items, _cardLibrary, _formatLibrary, _rarities);

        return new DraftChoiceDefinition() {
            @Override
            public int cardCountForChoiceId(String choiceId) {
                return 1;
            }

            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, CardCollection currentCards, String currentChoice) {
                final List<CardCollection.Item> cards = getCards(seed, stage);

                List<SoloDraft.DraftChoice> draftChoices = new ArrayList<SoloDraft.DraftChoice>(optionCount);
                for (int i = 0; i < Math.min(optionCount, possibleCards.size()); i++) {
                    final int finalI = i;
                    draftChoices.add(
                            new SoloDraft.DraftChoice() {
                                @Override
                                public String getChoiceId() {
                                    return cards.get(finalI).getBlueprintId();
                                }

                                @Override
                                public String getBlueprintId() {
                                    return cards.get(finalI).getBlueprintId();
                                }

                                @Override
                                public String getChoiceUrl() {
                                    return null;
                                }

                                @Override
                                public String getObjPackDescription() { return null; }
                            });
                }
                return draftChoices;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage, CardCollection currentCards) {
                DefaultCardCollection cardCollection = new DefaultCardCollection();
                cardCollection.addItem(choiceId, 1);
                return cardCollection;
            }

            private List<CardCollection.Item> getCards(long seed, int stage) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                float thisFixesRandomnessForSomeReason = rnd.nextInt();
                final List<CardCollection.Item> cards = new ArrayList<CardCollection.Item>(possibleCards);
                Collections.shuffle(cards, rnd);
                return cards;
            }
        };
    }

    private DraftChoiceDefinition buildSingleCollectionPickDraftChoiceDefinition(JSONObject data) {
        JSONArray switchResult = (JSONArray) data.get("possiblePicks");

        final Map<String, List<String>> cardsMap = new HashMap<String, List<String>>();
        final List<SoloDraft.DraftChoice> draftChoices = new ArrayList<SoloDraft.DraftChoice>();

        for (JSONObject pickDefinition : (Iterable<JSONObject>) switchResult) {
            final String choiceId = (String) pickDefinition.get("choiceId");
            final String url = (String) pickDefinition.get("url");
            JSONArray cards = (JSONArray) pickDefinition.get("cards");

            List<String> cardIds = new ArrayList<String>();
            for (String card : (Iterable<String>) cards)
                cardIds.add(card);

            draftChoices.add(
                    new SoloDraft.DraftChoice() {
                        @Override
                        public String getChoiceId() {
                            return choiceId;
                        }

                        @Override
                        public String getBlueprintId() {
                            return null;
                        }

                        @Override
                        public String getChoiceUrl() {
                            return url;
                        }

                        @Override
                        public String getObjPackDescription() { return null; }
                    });
            cardsMap.put(choiceId, cardIds);
        }

        return new DraftChoiceDefinition() {
            @Override
            public int cardCountForChoiceId(String choiceId) {
                return 1;
            }

            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, CardCollection currentCards, String currentChoice) {
                return draftChoices;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage, CardCollection currentCards) {
                List<String> cardIds = cardsMap.get(choiceId);
                DefaultCardCollection cardCollection = new DefaultCardCollection();
                if (cardIds != null)
                    for (String cardId : cardIds)
                        cardCollection.addItem(cardId, 1);

                return cardCollection;
            }
        };
    }

    private DraftChoiceDefinition buildMultipleCardPickDraftChoiceDefinition(JSONObject data) {
        final int count = ((Number) data.get("count")).intValue();
        JSONArray availableCards = (JSONArray) data.get("availableCards");

        final List<String> cards = new ArrayList<String>();
        for (String availableCard : (Iterable<String>) availableCards)
            cards.add(availableCard);

        return new DraftChoiceDefinition() {
            @Override
            public int cardCountForChoiceId(String choiceId) {
                return 1;
            }

            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, CardCollection currentCards, String currentChoice) {
                final List<String> shuffledCards = getShuffledCards(seed, stage);

                List<SoloDraft.DraftChoice> draftableCards = new ArrayList<SoloDraft.DraftChoice>(count);
                for (int i = 0; i < count; i++) {
                    final int finalI = i;
                    draftableCards.add(
                            new SoloDraft.DraftChoice() {
                                @Override
                                public String getChoiceId() {
                                    return shuffledCards.get(finalI);
                                }

                                @Override
                                public String getBlueprintId() {
                                    return shuffledCards.get(finalI);
                                }

                                @Override
                                public String getChoiceUrl() {
                                    return null;
                                }

                                @Override
                                public String getObjPackDescription() { return null; }
                            });
                }
                return draftableCards;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage, CardCollection currentCards) {
                List<String> shuffledCards = getShuffledCards(seed, stage);

                for (int i = 0; i < count; i++) {
                    if (shuffledCards.get(i).equals(choiceId)) {
                        DefaultCardCollection result = new DefaultCardCollection();
                        result.addItem(choiceId, 1);
                        return result;
                    }
                }

                return new DefaultCardCollection();
            }

            private List<String> getShuffledCards(long seed, int stage) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                float thisFixesRandomnessForSomeReason = rnd.nextFloat();
                final List<String> shuffledCards = new ArrayList<String>(cards);
                Collections.shuffle(shuffledCards, rnd);
                return shuffledCards;
            }
        };
    }

    private DraftChoiceDefinition buildCubePackPickDraftChoiceDefinition(JSONObject data) {
        final int count = ((Number) data.get("count")).intValue();
        JSONArray availableCards = (JSONArray) data.get("availableCards");

        final List<String> cards = new ArrayList<String>();
        for (String availableCard : (Iterable<String>) availableCards)
            cards.add(availableCard);

        // Parse addOnCards as map of cardId -> list of add-on card IDs
        final Map<String, List<String>> addOnCards = new HashMap<>();
        JSONObject addOnsJson = (JSONObject) data.get("addOnCards");
        if (addOnsJson != null) {
            for (Object key : addOnsJson.keySet()) {
                String cardId = (String) key;
                JSONArray addOns = (JSONArray) addOnsJson.get(cardId);
                List<String> addOnList = new ArrayList<>();
                for (Object addOnId : addOns) {
                    addOnList.add((String) addOnId);
                }
                addOnCards.put(cardId, addOnList);
            }
        }

        return new DraftChoiceDefinition() {
            @Override
            public int cardCountForChoiceId(String choiceId) {
                // Return 1 for the main card + number of add-ons if present
                int result = 1;
                if (addOnCards.containsKey(choiceId)) {
                    result += addOnCards.get(choiceId).size();
                }
                return result;
            }

            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, CardCollection currentCards, String currentChoice) {
                List<String> tempCards = getShuffledCardsSpecial(seed, stage);
                Map<String, CardCollection.Item> existingPicks = currentCards.getAll();
                for (CardCollection.Item item: existingPicks.values()) {
                    if (tempCards.contains(item.getBlueprintId())) {
                        tempCards.remove((item.getBlueprintId()));
                    }
                }
                final List<String> shuffledCards = new ArrayList<>(tempCards);
                int countThisRound = count - (stage % count);
                List<SoloDraft.DraftChoice> draftableCards = new ArrayList<SoloDraft.DraftChoice>(countThisRound);
                for (int i = 0; i < countThisRound; i++) {
                    final int finalI = i;
                    draftableCards.add(
                            new SoloDraft.DraftChoice() {
                                @Override
                                public String getChoiceId() {
                                    return shuffledCards.get(finalI);
                                }

                                @Override
                                public String getBlueprintId() {
                                    return shuffledCards.get(finalI);
                                }

                                @Override
                                public String getChoiceUrl() {
                                    return null;
                                }

                                @Override
                                public String getObjPackDescription() {
                                    // Build comma-separated list of main card + add-ons
                                    if (addOnCards.containsKey(getChoiceId())) {
                                        StringBuilder result = new StringBuilder(getChoiceId());
                                        for (String addOn : addOnCards.get(getChoiceId())) {
                                            result.append(",").append(addOn);
                                        }
                                        return result.toString();
                                    }
                                    return null;
                                }
                            });
                }
                return draftableCards;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage, CardCollection currentCards) {
                DefaultCardCollection result = new DefaultCardCollection();

                // Add the main card
                result.addItem(choiceId, 1);

                // Add any add-on cards if present
                if (addOnCards.containsKey(choiceId)) {
                    for (String addOn : addOnCards.get(choiceId)) {
                        result.addItem(addOn, 1);
                    }
                }

                return result;
            }

            private List<String> getShuffledCardsSpecial(long seed, int stage) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                float thisFixesRandomnessForSomeReason = rnd.nextFloat();
                final List<String> shuffledCards = new ArrayList<String>(cards);
                Collections.shuffle(shuffledCards, rnd);
                return shuffledCards;
            }
        };
    }

    private DraftChoiceDefinition buildCubePackObjPickDraftChoiceDefinition(JSONObject data) {
        final class ObjPack {
            final public String firstCardId;
            final public List<String> cards;

            ObjPack(List<String> incomingCards) {
                firstCardId = incomingCards.get(0);
                cards = new ArrayList<>(incomingCards);
            }
        }
        final int count = ((Number) data.get("count")).intValue();
        JSONArray availablePacks = (JSONArray) data.get("packs");
        final List<ObjPack> objPacks = new ArrayList<>();
        List<String> topCards = new ArrayList<>();
        for (Object jsonPack : availablePacks) {
            List<String> _objCards = (List<String>)jsonPack;
            ObjPack objPack = new ObjPack(_objCards);
            objPacks.add(objPack);
            topCards.add(_objCards.get(0));
        }
        final List<String> objCards = new ArrayList<>(topCards);

        return new DraftChoiceDefinition() {
            @Override
            public int cardCountForChoiceId(String choiceId) {
                int result = 0;

                for (ObjPack objPack : objPacks) {
                    if (objPack.firstCardId.equals(choiceId)) {
                        result = objPack.cards.size();
                        break;
                    }
                }
                return result;
            }

            String cardListForChoiceId(String choiceId) {
                String result = "";

                for (ObjPack objPack : objPacks) {
                    if (objPack.firstCardId.equals(choiceId)) {
                        for (String cardId : objPack.cards) {
                            result += cardId + ",";
                        }
                        result = result.substring(0, result.length()-1);
                        break;
                    }
                }
                return result;
            }

            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, CardCollection currentCards, String currentChoice) {
                List<String> tempCards = getShuffledCardsSpecial(seed, stage);
                Map<String, CardCollection.Item> existingPicks = currentCards.getAll();
                for (CardCollection.Item item: existingPicks.values()) {
                    if (tempCards.contains(item.getBlueprintId())) {
                        tempCards.remove((item.getBlueprintId()));
                    }
                }
                final List<String> shuffledCards = new ArrayList<>(tempCards);
                int existingCount = 0;
                for (String objCardId : objCards) {
                    if (currentCards.getItemCount(objCardId) > 0 || (currentChoice != null && currentChoice.equals(objCardId))) {
                        existingCount ++;
                    }
                }
                int countThisRound = count - (existingCount % count);
                List<SoloDraft.DraftChoice> draftableCards = new ArrayList<SoloDraft.DraftChoice>(countThisRound);
                for (int i = 0; i < countThisRound; i++) {
                    final int finalI = i;
                    draftableCards.add(
                            new SoloDraft.DraftChoice() {
                                @Override
                                public String getChoiceId() {
                                    return shuffledCards.get(finalI);
                                }

                                @Override
                                public String getBlueprintId() {
                                    return shuffledCards.get(finalI);
                                }

                                @Override
                                public String getChoiceUrl() {
                                    return null;
                                }

                                @Override
                                public String getObjPackDescription() { return cardListForChoiceId(getChoiceId()); }
                            });
                }
                return draftableCards;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage, CardCollection currentCards) {
                DefaultCardCollection result = new DefaultCardCollection();
                for (ObjPack objPack : objPacks) {
                    if (objPack.firstCardId.equals(choiceId)) {
                        for (String cardId : objPack.cards) {
                            result.addItem(cardId, 1);
                        }
                        break;
                    }
                }
                return result;
            }

            private List<String> getShuffledCardsSpecial(long seed, int stage) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                float thisFixesRandomnessForSomeReason = rnd.nextFloat();
                final List<String> shuffledCards = new ArrayList<String>(objCards);
                Collections.shuffle(shuffledCards, rnd);
                return shuffledCards;
            }
        };
    }

    private DraftChoiceDefinition buildRandomSwitchDraftChoiceDeifinition(JSONObject data) {
        JSONArray switchResult = (JSONArray) data.get("switchResult");

        final List<DraftChoiceDefinition> draftChoiceDefinitionList = new ArrayList<DraftChoiceDefinition>();
        for (JSONObject switchResultObject : (Iterable<JSONObject>) switchResult)
            draftChoiceDefinitionList.add(constructDraftChoiceDefinition(switchResultObject));

        return new DraftChoiceDefinition() {
            @Override
            public int cardCountForChoiceId(String choiceId) {
                return 1;
            }

            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, CardCollection currentCards, String currentChoice) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                float thisFixesRandomnessForSomeReason = rnd.nextFloat();
                return draftChoiceDefinitionList.get(rnd.nextInt(draftChoiceDefinitionList.size())).getDraftChoice(seed, stage, currentCards, currentChoice);
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage, CardCollection currentCards) {
                Random rnd = getRandom(seed, stage);
                // Fixing some weird issue with Random
                float thisFixesRandomnessForSomeReason = rnd.nextFloat();
                return draftChoiceDefinitionList.get(rnd.nextInt(draftChoiceDefinitionList.size())).getCardsForChoiceId(choiceId, seed, stage, currentCards);
            }
        };
    }

    private DraftChoiceDefinition buildWeightedSwitchDraftChoiceDefinition(JSONObject data) {
        JSONArray switchResult = (JSONArray) data.get("switchResult");

        final Map<Float, DraftChoiceDefinition> draftChoiceDefinitionMap = new LinkedHashMap<Float, DraftChoiceDefinition>();
        float weightTotal = 0;
        for (JSONObject switchResultObject : (Iterable<JSONObject>) switchResult) {
            float weight = ((Number) switchResultObject.get("weight")).floatValue();
            weightTotal += weight;
            draftChoiceDefinitionMap.put(weightTotal, constructDraftChoiceDefinition(switchResultObject));
        }

        return new DraftChoiceDefinition() {
            @Override
            public int cardCountForChoiceId(String choiceId) {
                return 1;
            }

            @Override
            public Iterable<SoloDraft.DraftChoice> getDraftChoice(long seed, int stage, CardCollection currentCards, String currentChoice) {
                Random rnd = getRandom(seed, stage);
                float result = rnd.nextFloat();
                for (Map.Entry<Float, DraftChoiceDefinition> weightEntry : draftChoiceDefinitionMap.entrySet()) {
                    if (result < weightEntry.getKey())
                        return weightEntry.getValue().getDraftChoice(seed, stage, currentCards, currentChoice);
                }

                return null;
            }

            @Override
            public CardCollection getCardsForChoiceId(String choiceId, long seed, int stage, CardCollection currentCards) {
                Random rnd = getRandom(seed, stage);
                float result = rnd.nextFloat();
                for (Map.Entry<Float, DraftChoiceDefinition> weightEntry : draftChoiceDefinitionMap.entrySet()) {
                    if (result < weightEntry.getKey())
                        return weightEntry.getValue().getCardsForChoiceId(choiceId, seed, stage, currentCards);
                }

                return null;
            }
        };
    }

    private Random getRandom(long seed, int stage) {
        return new Random(seed + stage * HIGH_ENOUGH_PRIME_NUMBER);
    }
}
