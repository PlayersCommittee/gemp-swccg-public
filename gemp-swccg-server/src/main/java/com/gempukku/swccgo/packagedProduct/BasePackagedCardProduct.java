package com.gempukku.swccgo.packagedProduct;

import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Provides the base implementation for a booster pack of cards.
 */
public abstract class BasePackagedCardProduct implements PackagedCardProduct {
    protected SwccgCardBlueprintLibrary _library;

    /**
     * Creates a base packaged card product.
     * @param library the blueprint library
     */
    public BasePackagedCardProduct(SwccgCardBlueprintLibrary library) {
        _library = library;
    }

    /**
     * Removes any cards from the collection that do not have a blueprint id that matches an existing card in the blueprint library.
     * @param cards the cards (identified by blueprint id)
     */
    protected void filterNonExistingCards(Collection<String> cards) {
        Iterator<String> iterator = cards.iterator();
        while (iterator.hasNext()) {
            String blueprintId = iterator.next();
            if (blueprintId.contains("_")) {
                SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                if (blueprint == null) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Adds the specified cards to the list of card collection items.
     * @param result the card collection items
     * @param cards the cards (identified by blueprint id)
     * @param foil true if foil, otherwise false
     */
    protected void addCards(List<CardCollection.Item> result, Collection<String> cards, boolean foil) {
        for (String card : cards) {
            result.add(CardCollection.Item.createItem(card + (foil ? "*" : ""), 1));
        }
    }

    /**
     * Adds the specified product to the list of card collection items.
     * @param result the card collection items
     * @param product the product (identified by product name)
     * @param count the number of the specified product to add
     */
    protected void addProducts(List<CardCollection.Item> result, String product, int count) {
        result.add(CardCollection.Item.createItem(product, count));
    }
}
