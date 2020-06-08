package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Tatooine booster pack.
 */
public class TatooineBoosterPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private SetRarity _setRarity;

    /**
     * Creates a Tatooine booster pack.
     * @param library the blueprint library
     */
    public TatooineBoosterPack(SwccgCardBlueprintLibrary library) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.TATOOINE.getSetNumber()));
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.TATOOINE_BOOSTER_PACK;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.TATOOINE_BOOSTER_PACK;
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
            possibleCards.remove("11_51"); // Aurra Sing
            possibleCards.remove("11_54"); // Darth Maul
            possibleCards.remove("11_62"); // Sebulba
            possibleCards.remove("11_65"); // Watto
            possibleCards.remove("11_6");  // Obi-Wan Kenobi, Padawan Learner
            possibleCards.remove("11_8");  // Padme Naberrie
            possibleCards.remove("11_10"); // Qui-Gon Jinn
            possibleCards.remove("11_49"); // Qui-Gon Jinn's Lightsaber
            possibleCards.remove("11_13"); // Threepio With His Parts Showing
        }
        else {
            possibleCards.remove("11_52"); // Aurra Sing (AI)
            possibleCards.remove("11_55"); // Darth Maul (AI)
            possibleCards.remove("11_63"); // Sebulba (AI)
            possibleCards.remove("11_66"); // Watto (AI)
            possibleCards.remove("11_7");  // Obi-Wan Kenobi, Padawan Learner (AI)
            possibleCards.remove("11_9");  // Padme Naberrie (AI)
            possibleCards.remove("11_11"); // Qui-Gon Jinn (AI)
            possibleCards.remove("11_50"); // Qui-Gon Jinn's Lightsaber (AI)
            possibleCards.remove("11_14"); // Threepio With His Parts Showing (AI)
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
