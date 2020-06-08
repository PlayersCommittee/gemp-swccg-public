package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines a Reflections II booster box.
 */
public class ReflectionsIIBoosterBox extends BasePackagedCardProduct {

    /**
     * Creates a Reflections II booster box.
     * @param library the blueprint library
     */
    public ReflectionsIIBoosterBox(SwccgCardBlueprintLibrary library) {
        super(library);
    }

    /**
     * Gets the name of the product.
     * @return the name of the product.
     */
    @Override
    public String getProductName() {
        return ProductName.REFLECTIONS_II_BOOSTER_BOX;
    }

    /**
     * Gets the price of the product.
     * @return the price of the product.
     */
    @Override
    public float getProductPrice() {
        return ProductPrice.REFLECTIONS_II_BOOSTER_BOX;
    }

    /**
     * Opens the packaged card product.
     * @return the card collection items contained in the packaged card product.
     */
    @Override
    public List<CardCollection.Item> openPackage() {
        List<CardCollection.Item> result = new LinkedList<CardCollection.Item>();
        addFoilCard(result);
        addProducts(result, ProductName.REFLECTIONS_II_BOOSTER_PACK, 30);
        return result;
    }

    /**
     * Adds "box topper" foil to the list.
     * @param result the list of cards in the pack
     */
    private void addFoilCard(List<CardCollection.Item> result) {
        List<String> boxToppers = new ArrayList<String>();
        double random = Math.random();
        if (random >= 0.75) {
            boxToppers.add("108_5"); // Boba Fett With Blaster Rifle
        }
        else if (random >= 0.50) {
            boxToppers.add("108_1"); // Han With Heavy Blaster Pistol
        }
        else if (random >= 0.25) {
            boxToppers.add("108_2"); // Leia With Blaster Rifle
        }
        else {
            boxToppers.add("108_4"); // Obi-Wan With Lightsaber
        }
        filterNonExistingCards(boxToppers);
        addCards(result, boxToppers, true);
    }
}
