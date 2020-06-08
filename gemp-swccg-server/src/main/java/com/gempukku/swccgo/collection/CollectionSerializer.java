package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.common.CardCounts;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.MutableCardCollection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionSerializer {
    private List<String> _doubleByteCountItems = new ArrayList<String>();
    private List<String> _singleByteCountItems = new ArrayList<String>();

    public CollectionSerializer() {
        try {
            fillDoubleByteItems();

            fillSingleByteItems();

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

    public void serializeCollection(CardCollection collection, OutputStream outputStream) throws IOException {
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

        int cardBytes = _singleByteCountItems.size();
        printInt(outputStream, cardBytes, 2);

        for (String itemId : _singleByteCountItems) {
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
        } else {
            throw new IllegalStateException("Unknown version of serialized collection: " + version);
        }
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
                final String blueprintId = _singleByteCountItems.get(i);
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
