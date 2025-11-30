package com.gempukku.swccgo.draft2;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.draft2.builder.CardCollectionProducer;
import com.gempukku.swccgo.draft2.builder.DraftChoiceBuilder;
import com.gempukku.swccgo.draft2.builder.StartingPoolBuilder;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.cards.packs.SetDefinition;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class SoloDraftDefinitions {
    private static Logger _logger = LogManager.getLogger(SoloDraftDefinitions.class);
    private Map<String, SoloDraft> draftTypes = new HashMap<String, SoloDraft>();
    private StartingPoolBuilder startingPoolBuilder = new StartingPoolBuilder();
    private DraftChoiceBuilder draftChoiceBuilder;

    public SoloDraftDefinitions(CollectionsManager collectionsManager, SwccgCardBlueprintLibrary cardLibrary,
                                SwccgoFormatLibrary formatLibrary) {
        Map<String, SetRarity> rarities = new HashMap<String, SetRarity>();
        RarityReader rreader = new RarityReader();

        for (int i = 1; i <= 14; i++) {
            rarities.put(String.valueOf(i), rreader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 101; i <= 112; i++) {
            rarities.put(String.valueOf(i), rreader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 200; i <= 207; i++) {
            rarities.put(String.valueOf(i), rreader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 301; i <= 301; i++) {
            rarities.put(String.valueOf(i), rreader.getSetRarity(String.valueOf(i)));
        }
        draftChoiceBuilder = new DraftChoiceBuilder(collectionsManager, cardLibrary, formatLibrary, rarities);
        try {
            final InputStreamReader reader = new InputStreamReader(SwccgoFormatLibrary.class.getResourceAsStream("/swccgDrafts.json"), "UTF-8");
            try {
                JSONParser parser = new JSONParser();
                JSONArray object = (JSONArray) parser.parse(reader);
                for (Object draftDefObj : object) {
                    String type = (String) ((JSONObject) draftDefObj).get("type");
                    String location = (String) ((JSONObject) draftDefObj).get("location");
                    draftTypes.put(type, loadDraft(location));
                }
            } catch (ParseException exp) {
                throw new RuntimeException("Problem loading solo drafts", exp);
            }
        } catch (IOException exp) {
            throw new RuntimeException("Problem loading solo drafts", exp);
        }
    }

    private SoloDraft loadDraft(String file) {
        try {
            final InputStreamReader reader = new InputStreamReader(SwccgoFormatLibrary.class.getResourceAsStream(file), "UTF-8");
            try {
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(reader);
                String format = (String) object.get("format");
                Long startingCardCount = (Long) object.get("fixedCount");
                Long objChoiceCountPerSide = (Long) object.get("objChoiceCountPerSide");
                Long choiceCountPerSide = (Long) object.get("choiceCountPerSide");
                List<String> lightObjCards = (List<String>) object.get("lightObjCards");
                List<String> darkObjCards = (List<String>) object.get("darkObjCards");

                CardCollectionProducer cardCollectionProducer = null;
                JSONObject startingPool = (JSONObject) object.get("startingPool");
                if (startingPool != null) {
                    cardCollectionProducer = startingPoolBuilder.buildCardCollectionProducer(startingPool);
                }

                List<DraftChoiceDefinition> draftChoiceDefinitions = new ArrayList<DraftChoiceDefinition>();
                JSONArray choices = (JSONArray) object.get("choices");
                Iterator<JSONObject> choicesIterator = choices.iterator();
                while (choicesIterator.hasNext()) {
                    JSONObject choice = choicesIterator.next();
                    DraftChoiceDefinition draftChoiceDefinition = draftChoiceBuilder.buildDraftChoiceDefinition(choice);
                    int repeatCount = ((Number) choice.get("repeat")).intValue();
                    for (int i = 0; i < repeatCount; i++)
                        draftChoiceDefinitions.add(draftChoiceDefinition);
                }

                _logger.debug("Loaded draft definition: "+file);
                return new DefaultSoloDraft(format, startingCardCount.intValue(), choiceCountPerSide.intValue(), objChoiceCountPerSide.intValue(), lightObjCards, darkObjCards, cardCollectionProducer, draftChoiceDefinitions);
            } catch (ParseException exp) {
                throw new RuntimeException("Problem loading solo draft " + file, exp);
            }
        } catch (IOException exp) {
            throw new RuntimeException("Problem loading solo draft " + file, exp);
        }
    }

    public SoloDraft getSoloDraft(String draftType) {
        return draftTypes.get(draftType);
    }
}
