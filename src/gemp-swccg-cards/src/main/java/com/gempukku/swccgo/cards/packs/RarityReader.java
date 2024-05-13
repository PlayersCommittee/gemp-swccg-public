package com.gempukku.swccgo.cards.packs;

import com.gempukku.swccgo.common.Rarity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Reads in the card rarity information from a file and creates the mapping between blueprint card ids and rarity.
 */
public class RarityReader {
    /**
     * Reads in the card rarity information from a file and creates the mapping between blueprint card ids and rarity.
     * @param setNo the set number
     * @return a SetRarity object containing card rarity information for the specified set
     */
    public SetRarity getSetRarity(String setNo) {
        try {
            String fileName = "/set" + setNo + "-rarity.txt";

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(RarityReader.class.getResourceAsStream(fileName), "UTF-8"));
            try {
                String line;
                Map<Rarity, List<String>> cardsByRarity = new HashMap<Rarity, List<String>>();
                Map<String, Rarity> cardRarity = new HashMap<String, Rarity>();

                while ((line = bufferedReader.readLine()) != null) {
                        if (!line.substring(0, setNo.length()).equals(setNo))
                            throw new IllegalStateException("Seems the rarity is for some other set");
                        String[] lineSegments = line.split("_");
                        if (lineSegments.length!=3)
                            throw new IllegalStateException("Seems the rarity info is malformed");
                        Rarity rarity = Rarity.getRarityFromString(lineSegments[1]);
                        if (rarity == null)
                            throw new IllegalStateException("Seems the rarity info is malformed");

                        List<String> cards = cardsByRarity.get(rarity);
                        if (cards == null) {
                            cards = new LinkedList<String>();
                            cardsByRarity.put(rarity, cards);
                        }
                        String blueprintId = lineSegments[0] + "_" + lineSegments[2];
                        cards.add(blueprintId);
                        cardRarity.put(blueprintId, rarity);
                }

                return new DefaultSetRarity(cardsByRarity, cardRarity);
            } finally {
                bufferedReader.close();
            }
        } catch (IOException exp) {
            throw new RuntimeException("Problem loading rarity of set " + setNo, exp);
        }
    }
}
