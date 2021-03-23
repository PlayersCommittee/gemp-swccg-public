package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.*;

/**
 * Defines a Watto's Cube fixed pack.
 */
public class WattosCubeFixedPack extends BasePackagedCardProduct {
    private Random _random = new Random();
    private String _side;

    /**
     * Creates a Watto's Cube fixed pack.
     * @param library the blueprint library
     * @param side Dark or Light
     */
    public WattosCubeFixedPack(SwccgCardBlueprintLibrary library, String side) {
        super(library);
        _side = side;
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return _side.equals("Dark") ? ProductName.CUBE_FIXED_PACK_DARK : ProductName.CUBE_FIXED_PACK_LIGHT;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return 0.0f;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        return openPackageWithExclusions(Collections.<String>emptySet());
    }

    public List<CardCollection.Item> openPackageWithExclusions(Set<String> exclusions) {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addFixedCards(result);
        return result;
    }

    private void addFixedCards(List<CardCollection.Item> result) {
        List<String> possibleCards = new ArrayList<String>();
        switch(_side) {
            case "Dark":
                possibleCards.addAll(darkFixedCards());
                break;
            case "Light":
                possibleCards.addAll(lightFixedCards());
                break;
        }
        filterNonExistingCards(possibleCards);
        addCards(result, possibleCards, false);
    }


    public List<String> darkFixedCards() {
        List<String> cards = new ArrayList<String>();
        cards.add("9_139");
        cards.add("7_270");
        cards.add("5_169");
        cards.add("7_282");
        cards.add("7_283");
        cards.add("6_162");
        cards.add("7_287");
        cards.add("1_289");
        cards.add("11_93");
        cards.add("12_177");

        return cards;
    }

    public List<String> lightFixedCards() {
        List<String> cards = new ArrayList<String>();
        cards.add("9_51");
        cards.add("5_76");
        cards.add("5_80");
        cards.add("5_83");
        cards.add("9_57");
        cards.add("12_77");
        cards.add("9_59");
        cards.add("1_128");
        cards.add("1_129");
        cards.add("205_6");

        return cards;
    }
}
