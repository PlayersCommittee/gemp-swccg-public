package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.common.CardCounts;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.MutableCardCollection;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionSerializer {
    private List<String> _doubleByteCountItems = new ArrayList<String>();
    private List<String> _singleByteCountItems = new ArrayList<String>();
    private List<String> _singleByteCountItemsForVer0 = new ArrayList<String>();

    public CollectionSerializer() {
        try {
            fillDoubleByteItems();
            fillSingleByteItems();
            loadForDeserializeVer0();

        } catch (IOException exp) {
            throw new RuntimeException("Problem loading collection data", exp);
        }
    }

    private void fillSingleByteItems() throws IOException {
        for (int i = 1; i < (1 + CardCounts.FULL_SETS_CARD_COUNTS.length); i++) {
            loadSet(String.valueOf(i));
        }
        for (int i = 101; i < (101 + CardCounts.PREMIUM_SETS_CARD_COUNTS.length); i++) {
            loadSet(String.valueOf(i));
        }
        for (int i = 200; i < (200 + CardCounts.VIRTUAL_SETS_CARD_COUNTS.length); i++) {
            loadSet(String.valueOf(i));
        }
        for (int i = 301; i < (301 + CardCounts.VIRTUAL_PREMIUM_SETS_CARD_COUNTS.length); i++) {
            loadSet(String.valueOf(i));
        }
    }

    private void fillDoubleByteItems() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(CollectionSerializer.class.getResourceAsStream("/packs.txt"), "UTF-8"));
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                _doubleByteCountItems.add(line);
        } finally {
            bufferedReader.close();
        }
    }

    private void loadForDeserializeVer0() throws IOException {
        BufferedReader cardReader = new BufferedReader(new InputStreamReader(CollectionSerializer.class.getResourceAsStream("/forDeserializeVer0.txt"), "UTF-8"));
        try {
            String line;

            while ((line = cardReader.readLine()) != null) {
                // Normal
                _singleByteCountItemsForVer0.add(translateToBlueprintId(line));
                // Foil
                _singleByteCountItemsForVer0.add(translateToBlueprintId(line) + "*");
            }
        } finally {
            cardReader.close();
        }
    }

    private void loadSet(String setId) throws IOException {
        BufferedReader cardReader = new BufferedReader(new InputStreamReader(CollectionSerializer.class.getResourceAsStream("/set" + setId + "-rarity.txt"), "UTF-8"));
        try {
            String line;

            while ((line = cardReader.readLine()) != null) {
                if (!line.substring(0, setId.length()).equals(setId))
                    throw new IllegalStateException("Seems the rarity is for some other set");
                // Normal
                _singleByteCountItems.add(translateToBlueprintId(line));
                // Foil
                _singleByteCountItems.add(translateToBlueprintId(line) + "*");
            }
        } finally {
            cardReader.close();
        }
    }

    private String translateToBlueprintId(String rarityString) {
        final String setNo = rarityString.substring(0, rarityString.indexOf("_", 1));
        final String cardNo = rarityString.substring(rarityString.lastIndexOf("_") + 1);
        return setNo + "_" + cardNo;
    }

    private int getSetId(String itemId) {
        String setNo = itemId.substring(0, itemId.indexOf("_",1));
        return Integer.parseInt(setNo);
    }

    private int getCardId(String itemId) {
        String cardNo = itemId.substring(itemId.lastIndexOf("_")+1);
        return Integer.parseInt(cardNo);
    }

    public void serializeCollection(CardCollection collection, OutputStream outputStream) throws IOException {
        byte version = 2;
        outputStream.write(version);

        int currency = collection.getCurrency();
        printInt(outputStream, currency, 3);

        boolean excludePackDuplicates = collection.excludePackDuplicates();
        printInt(outputStream, (excludePackDuplicates?1:0), 1);

        int packTypes = _doubleByteCountItems.size();
        printInt(outputStream, packTypes, 1);

        final Map<String, CardCollection.Item> collectionCounts = collection.getAll();
        for (String itemId : _doubleByteCountItems) {
            final CardCollection.Item count = collectionCounts.get(itemId);
            if (count == null) {
                printInt(outputStream, 0, 2);
            } else {
                int itemCount = Math.min((int) Math.pow(255, 2), count.getCount());
                printInt(outputStream, itemCount, 2);
            }
        }

        Map<String,Integer> cardCountsNonFoil = new HashMap<String,Integer>();
        Map<String,Integer> cardCountsFoil = new HashMap<String,Integer>();
        Map<String,Integer> cardCountsAlternateImage = new HashMap<String,Integer>();

        for (String itemId : _singleByteCountItems) {
            if(!itemId.endsWith("*") && !itemId.endsWith("^")) {
                final CardCollection.Item count = collectionCounts.get(itemId);
                final CardCollection.Item countFoil = collectionCounts.get(itemId+"*");
                final CardCollection.Item countAlternateImage = collectionCounts.get(itemId+"^");
                if (count != null || countFoil != null || countAlternateImage != null) {
                    // Apply the maximum of 255
                    int nonFoilCount = Math.min(255, count == null? 0: count.getCount());
                    int foilCount = Math.min(255, countFoil == null? 0: countFoil.getCount());
                    int alternateImageCount = Math.min(255, countAlternateImage == null? 0: countAlternateImage.getCount());

                    cardCountsNonFoil.put(itemId, nonFoilCount);
                    cardCountsFoil.put(itemId, foilCount);
                    cardCountsAlternateImage.put(itemId, alternateImageCount);
                }
            }
        }

        //number of cards with non-zero counts since cards with all zero counts aren't inserted into the map
        printInt(outputStream, cardCountsNonFoil.size(), 2);

        for(String itemId : cardCountsNonFoil.keySet()) {
            try {
                int setId = getSetId(itemId);
                int cardId = getCardId(itemId);
                int nonFoilCount = cardCountsNonFoil.get(itemId);
                int foilCount = cardCountsFoil.get(itemId);
                int alternateImageCount = cardCountsAlternateImage.get(itemId);

                printInt(outputStream, setId, 2);
                printInt(outputStream, cardId, 2);
                printInt(outputStream, nonFoilCount, 1);
                printInt(outputStream, foilCount, 1);
                printInt(outputStream, alternateImageCount, 1);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
    }

    public void serializeCollectionVer1(CardCollection collection, OutputStream outputStream) throws IOException {
        byte version = 1;
        outputStream.write(version);

        int currency = collection.getCurrency();
        printInt(outputStream, currency, 3);

        int packTypes = _doubleByteCountItems.size();
        printInt(outputStream, packTypes, 1);

        final Map<String, CardCollection.Item> collectionCounts = collection.getAll();
        for (String itemId : _doubleByteCountItems) {
            final CardCollection.Item count = collectionCounts.get(itemId);
            if (count == null) {
                printInt(outputStream, 0, 2);
            } else {
                int itemCount = Math.min((int) Math.pow(255, 2), count.getCount());
                printInt(outputStream, itemCount, 2);
            }
        }

        Map<String,Integer> cardCountsNonFoil = new HashMap<String,Integer>();
        Map<String,Integer> cardCountsFoil = new HashMap<String,Integer>();

        for (String itemId : _singleByteCountItems) {
            if(!itemId.endsWith("*") && !itemId.endsWith("^")) {
                final CardCollection.Item count = collectionCounts.get(itemId);
                final CardCollection.Item countFoil = collectionCounts.get(itemId+"*");
                if (count != null || countFoil != null) {
                    // Apply the maximum of 255
                    int nonFoilCount = Math.min(255, count == null? 0: count.getCount());
                    int foilCount = Math.min(255, countFoil == null? 0: countFoil.getCount());

                    cardCountsNonFoil.put(itemId, nonFoilCount);
                    cardCountsFoil.put(itemId, foilCount);
                }
            }
        }

        //number of cards with non-zero counts since cards with zero in both aren't inserted into the map
        printInt(outputStream, cardCountsNonFoil.size(), 2);

        for(String itemId : cardCountsNonFoil.keySet()) {
            try {
                int setId = getSetId(itemId);
                int cardId = getCardId(itemId);
                int nonFoilCount = cardCountsNonFoil.get(itemId);
                int foilCount = cardCountsFoil.get(itemId);

                printInt(outputStream, setId, 2);
                printInt(outputStream, cardId, 2);
                printInt(outputStream, nonFoilCount, 1);
                printInt(outputStream, foilCount, 1);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
    }

    public void serializeCollectionVer0(CardCollection collection, OutputStream outputStream) throws IOException {
        byte version = 0;
        outputStream.write(version);

        int currency = collection.getCurrency();
        printInt(outputStream, currency, 3);

        byte packTypes = (byte) _doubleByteCountItems.size();
        outputStream.write(packTypes);

        final Map<String, CardCollection.Item> collectionCounts = collection.getAll();
        for (String itemId : _doubleByteCountItems) {
            final CardCollection.Item count = collectionCounts.get(itemId);
            if (count == null) {
                printInt(outputStream, 0, 2);
            } else {
                int itemCount = Math.min((int) Math.pow(255, 2), count.getCount());
                printInt(outputStream, itemCount, 2);
            }
        }

        int cardBytes = _singleByteCountItemsForVer0.size();
        printInt(outputStream, cardBytes, 2);

        for (String itemId : _singleByteCountItemsForVer0) {
            final CardCollection.Item count = collectionCounts.get(itemId);
            if (count == null)
                outputStream.write(0);
            else {
                // Apply the maximum of 255
                int cardCount = Math.min(255, count.getCount());
                printInt(outputStream, cardCount, 1);
            }
        }
    }

    public MutableCardCollection deserializeCollection(InputStream inputStream) throws IOException {
        int version = inputStream.read();
        if (version == 0) {
            return deserializeCollectionVer0(new BufferedInputStream(inputStream));
        } else if (version == 1) {
            return deserializeCollectionVer1(new BufferedInputStream(inputStream));
        } else if (version == 2) {
            return deserializeCollectionVer2(new BufferedInputStream(inputStream));
        } else {
            throw new IllegalStateException("Unknown version of serialized collection: " + version);
        }
    }

    private MutableCardCollection deserializeCollectionVer2(BufferedInputStream inputStream) throws IOException {

        int byte1 = inputStream.read();
        int byte2 = inputStream.read();
        int byte3 = inputStream.read();
        int currency = convertToInt(byte1, byte2, byte3);

        int excludePackDuplicatesInt = inputStream.read();
        boolean excludePackDuplicates = (excludePackDuplicatesInt==1);

        DefaultCardCollection collection = new DefaultCardCollection(excludePackDuplicates);
        collection.addCurrency(currency);


        int packTypes = convertToInt(inputStream.read());

        byte[] packs = new byte[packTypes * 2];

        int read = inputStream.read(packs);
        if (read != packTypes * 2)
            throw new IllegalStateException("Under-read the packagedProduct information");
        for (int i = 0; i < packTypes; i++) {
            int count = convertToInt(packs[i * 2], packs[i * 2 + 1]);
            if (count > 0)
                collection.addItem(_doubleByteCountItems.get(i), count);
        }

        int cardBytes = convertToInt(inputStream.read(), inputStream.read());

        for(int i=0; i<cardBytes; i++) {
            int setId = convertToInt(inputStream.read(), inputStream.read());
            int cardId = convertToInt(inputStream.read(), inputStream.read());
            int nonFoilCount = inputStream.read();
            int foilCount = inputStream.read();
            int alternateImageCount = inputStream.read();

            String blueprintId = setId + "_" + cardId;
            if (nonFoilCount > 0)
                collection.addItem(blueprintId, nonFoilCount);
            if (foilCount > 0)
                collection.addItem(blueprintId + "*", foilCount);
            if (alternateImageCount > 0)
                collection.addItem(blueprintId + "^", alternateImageCount);

        }

        return collection;
    }

    private MutableCardCollection deserializeCollectionVer1(BufferedInputStream inputStream) throws IOException {
        DefaultCardCollection collection = new DefaultCardCollection();

        int byte1 = inputStream.read();
        int byte2 = inputStream.read();
        int byte3 = inputStream.read();
        int currency = convertToInt(byte1, byte2, byte3);
        collection.addCurrency(currency);

        int packTypes = convertToInt(inputStream.read());

        byte[] packs = new byte[packTypes * 2];

        int read = inputStream.read(packs);
        if (read != packTypes * 2)
            throw new IllegalStateException("Under-read the packagedProduct information");
        for (int i = 0; i < packTypes; i++) {
            int count = convertToInt(packs[i * 2], packs[i * 2 + 1]);
            if (count > 0)
                collection.addItem(_doubleByteCountItems.get(i), count);
        }

        int cardBytes = convertToInt(inputStream.read(), inputStream.read());

        for(int i=0; i<cardBytes; i++) {
            int setId = convertToInt(inputStream.read(), inputStream.read());
            int cardId = convertToInt(inputStream.read(), inputStream.read());
            int nonFoilCount = inputStream.read();
            int foilCount = inputStream.read();

            String blueprintId = setId + "_" + cardId;
            if (nonFoilCount > 0)
                collection.addItem(blueprintId, nonFoilCount);
            if (foilCount > 0)
                collection.addItem(blueprintId + "*", foilCount);

        }
        
        return collection;
    }

    private MutableCardCollection deserializeCollectionVer0(BufferedInputStream inputStream) throws IOException {
        DefaultCardCollection collection = new DefaultCardCollection();

        int byte1 = inputStream.read();
        int byte2 = inputStream.read();
        int byte3 = inputStream.read();
        int currency = convertToInt(byte1, byte2, byte3);
        collection.addCurrency(currency);

        int packTypes = convertToInt(inputStream.read());

        byte[] packs = new byte[packTypes * 2];

        int read = inputStream.read(packs);
        if (read != packTypes * 2)
            throw new IllegalStateException("Under-read the packagedProduct information");
        for (int i = 0; i < packTypes; i++) {
            int count = convertToInt(packs[i * 2], packs[i * 2 + 1]);
            if (count > 0)
                collection.addItem(_doubleByteCountItems.get(i), count);
        }

        int cardBytes = convertToInt(inputStream.read(), inputStream.read());
        byte[] cards = new byte[cardBytes];
        read = inputStream.read(cards);
        if (read != cardBytes)
            throw new IllegalArgumentException("Under-read the cards information");
        for (int i = 0; i < cards.length; i++) {
            int count = convertToInt(cards[i]);
            if (count>0) {
                final String blueprintId = _singleByteCountItemsForVer0.get(i);
                collection.addItem(blueprintId, count);
            }
        }

        return collection;
    }

    private int convertToInt(int... bytes) {
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] << ((bytes.length - i - 1) * 8);
            if (value < 0)
                value +=256;
            result += value;
        }
        return result;
    }

    private void printInt(OutputStream outputStream, int value, int byteCount) throws IOException {
        for (int i = 0; i < byteCount; i++)
            outputStream.write((value >> (8 * (byteCount - i - 1))) & 0x000000ff);
    }
}
