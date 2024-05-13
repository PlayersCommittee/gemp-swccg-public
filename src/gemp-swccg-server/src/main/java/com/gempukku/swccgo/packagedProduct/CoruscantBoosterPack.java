package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Coruscant booster pack.
 */
public class CoruscantBoosterPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private SetRarity _setRarity;
    private boolean _includeNonEpisodeI;

    /**
     * Creates a Coruscant booster pack.
     * @param library the blueprint library
     */
    public CoruscantBoosterPack(SwccgCardBlueprintLibrary library) {
        this(library, true);
    }

    /**
     * Creates a Coruscant booster pack.
     * @param library the blueprint library
     * @param includeNonEpisodeI false if non-Episode I cards should be excluded
     */
    public CoruscantBoosterPack(SwccgCardBlueprintLibrary library, boolean includeNonEpisodeI) {
        super(library);
        RarityReader rarityReader = new RarityReader();
        _setRarity = rarityReader.getSetRarity(String.valueOf(ExpansionSet.CORUSCANT.getSetNumber()));
        _includeNonEpisodeI = includeNonEpisodeI;
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return _includeNonEpisodeI?ProductName.CORUSCANT_BOOSTER_PACK:ProductName.CORUSCANT_BOOSTER_PACK_EPISODE_I_ONLY;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.CORUSCANT_BOOSTER_PACK;
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
            possibleCards.remove("12_101"); // Darth Maul, Young Apprentice
            possibleCards.remove("12_182"); // Maul's Sith Infiltrator
            possibleCards.remove("12_139"); // The Phantom Menace
            possibleCards.remove("12_13");  // Mace Windu
            possibleCards.remove("12_16");  // Master Qui-Gon
            possibleCards.remove("12_22");  // Queen Amidala, Ruler of Naboo
            possibleCards.remove("12_28");  // Senator Palpatine
            possibleCards.remove("12_30");  // Supreme Chancellor Valorum
            possibleCards.remove("12_35");  // Yoda, Senior Council Member
        }
        else {
            possibleCards.remove("12_102"); // Darth Maul, Young Apprentice (AI)
            possibleCards.remove("12_183"); // Maul's Sith Infiltrator (AI)
            possibleCards.remove("12_140"); // The Phantom Menace (AI)
            possibleCards.remove("12_14");  // Mace Windu (AI)
            possibleCards.remove("12_17");  // Master Qui-Gon (AI)
            possibleCards.remove("12_23");  // Queen Amidala, Ruler of Naboo (AI)
            possibleCards.remove("12_29");  // Senator Palpatine (AI)
            possibleCards.remove("12_31");  // Supreme Chancellor Valorum (AI)
            possibleCards.remove("12_36");  // Yoda, Senior Council Member (AI)
        }
        filterNonExistingCards(possibleCards);
        if(!_includeNonEpisodeI)
            filterIcon(possibleCards, Icon.EPISODE_I,true);
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
        if(!_includeNonEpisodeI)
            filterIcon(possibleCards,Icon.EPISODE_I,true);
        Collections.shuffle(possibleCards, _random);
        addCards(result, possibleCards.subList(0, Math.min(possibleCards.size(), count)), false);
    }
}
