package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Theed Palace booster pack.
 */
public class TheedPalaceBoosterPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private SetRarity _setRarity;

    /**
     * Creates a Theed Palace booster pack.
     * @param library the blueprint library
     */
    public TheedPalaceBoosterPack(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.THEED_PALACE.getSetNumber()));
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.THEED_PALACE_BOOSTER_PACK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.THEED_PALACE_BOOSTER_PACK;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addRandomRareCard(result, 1);
        addRandomCommonOrUncommonCard(result, 10);
        return result;
    }

    /**
     * Adds random Rare cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomRareCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.R));

        // 1 of 4 times keep the AI card, instead of the non-AI card
        if ((Math.random() * 4) >= 3) {
            possibleCards.remove("14_78"); // Darth Sidious
            possibleCards.remove("14_81"); // Nute Gunray, Neimoidian Viceroy
            possibleCards.remove("14_86"); // Rune Haako, Legal Counsel
            possibleCards.remove("14_3");  // Artoo, Brave Little Droid
            possibleCards.remove("14_5");  // Boss Nass
            possibleCards.remove("14_10"); // General Jar Jar
            possibleCards.remove("14_18"); // Mace Windu, Jedi Master
            possibleCards.remove("14_23"); // Panaka, Protector Of The Queen
            possibleCards.remove("14_25"); // Queen Amidala
        }
        else {
            possibleCards.remove("14_79"); // Darth Sidious (AI)
            possibleCards.remove("14_82"); // Nute Gunray, Neimoidian Viceroy (AI)
            possibleCards.remove("14_87"); // Rune Haako, Legal Counsel (AI)
            possibleCards.remove("14_4");  // Artoo, Brave Little Droid (AI)
            possibleCards.remove("14_6");  // Boss Nass (AI)
            possibleCards.remove("14_11"); // General Jar Jar (AI)
            possibleCards.remove("14_19"); // Mace Windu, Jedi Master (AI)
            possibleCards.remove("14_24"); // Panaka, Protector Of The Queen (AI)
            possibleCards.remove("14_26"); // Queen Amidala (AI)
        }
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }

    /**
     * Adds random Common or Uncommon cards to the list.
     * @param result the list of cards in the pack
     * @param count the number cards to add
     */
    private void addRandomCommonOrUncommonCard(List<CardCollection.Item> result, int count) {
        List<String> possibleCards = new ArrayList<String>();
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.U));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.U));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.C));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.C));
        possibleCards.addAll(_setRarity.getCardsOfRarity(Rarity.C));
        filterNonExistingCards(possibleCards);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }
}
