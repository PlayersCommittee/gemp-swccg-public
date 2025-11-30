package com.gempukku.swccgo.draft2.builder;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.google.common.collect.Iterables;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class StartingPoolBuilder {
    public CardCollectionProducer buildCardCollectionProducer(JSONObject startingPool) {
        String cardCollectionProducerType = (String) startingPool.get("type");
        if (cardCollectionProducerType.equals("randomCardPool")) {
            return buildRandomCardPool((JSONObject) startingPool.get("data"));
        } else if (cardCollectionProducerType.equals("boosterDraftRun")) {
            return buildBoosterDraftRun((JSONObject) startingPool.get("data"));
        } else if (cardCollectionProducerType.equals("cubeDraftRun")) {
            return buildCubeDraftRun((JSONObject) startingPool.get("data"));
        }
        throw new RuntimeException("Unknown cardCollectionProducer type: " + cardCollectionProducerType);
    }

    private CardCollectionProducer buildRandomCardPool(JSONObject randomCardPool) {
        JSONArray cardPools = (JSONArray) randomCardPool.get("randomResult");

        final List<CardCollection> cardCollections = new ArrayList<CardCollection>();
        Iterator<JSONArray> iterator = cardPools.iterator();
        while (iterator.hasNext()) {
            JSONArray cards = iterator.next();

            DefaultCardCollection cardCollection = new DefaultCardCollection();
            Iterator<String> cardIterator = cards.iterator();
            while (cardIterator.hasNext()) {
                cardCollection.addItem(cardIterator.next(), 1);
            }
            cardCollections.add(cardCollection);
        }

        return new CardCollectionProducer() {
            @Override
            public CardCollection getCardCollection(long seed) {
                Random rnd = new Random(seed);
                float thisFixesARandomnessBug = rnd.nextFloat();
                return cardCollections.get(rnd.nextInt(cardCollections.size()));
            }
        };
    }

    private CardCollectionProducer buildBoosterDraftRun(JSONObject boosterDraftRun) {
        final int runLength = ((Number) boosterDraftRun.get("runLength")).intValue();
        final JSONArray coreCards = (JSONArray) boosterDraftRun.get("coreCards");
        final JSONArray lightRuns = (JSONArray) boosterDraftRun.get("lightRuns");
        final JSONArray darkRuns = (JSONArray) boosterDraftRun.get("darkRuns");

        return new CardCollectionProducer() {
            @Override
            public CardCollection getCardCollection(long seed) {
                Random rnd = new Random(seed);
                List<String> freePeoplesRun = (List<String>) lightRuns.get(rnd.nextInt(lightRuns.size()));
                List<String> shadowRun = (List<String>) darkRuns.get(rnd.nextInt(darkRuns.size()));

                int freePeopleLength = freePeoplesRun.size();
                int freePeopleStart = rnd.nextInt(freePeopleLength);

                int shadowLength = shadowRun.size();
                int shadowStart = rnd.nextInt(shadowLength);

                Iterable<String> freePeopleIterable = getCyclingIterable(freePeoplesRun, freePeopleStart, runLength);
                Iterable<String> shadowIterable = getCyclingIterable(shadowRun, shadowStart, runLength);

                final DefaultCardCollection startingCollection = new DefaultCardCollection();

                for (String card : Iterables.concat((List<String>) coreCards, freePeopleIterable, shadowIterable))
                    startingCollection.addItem(card, 1);

                return startingCollection;
            }
        };
    }

    private CardCollectionProducer buildCubeDraftRun(JSONObject cubeDraftRun) {
        final JSONArray coreCards = (JSONArray) cubeDraftRun.get("coreCards");

        return new CardCollectionProducer() {
            @Override
            public CardCollection getCardCollection(long seed) {
                final DefaultCardCollection startingCollection = new DefaultCardCollection();

                for (String card : (List<String>) coreCards)
                    startingCollection.addItem(card, 1);

                return startingCollection;
            }
        };
    }

    private static Iterable<String> getCyclingIterable(List<String> list, int start, int length) {
        return Iterables.limit(Iterables.skip(Iterables.cycle(list), start), length);
    }
}
