package com.gempukku.swccgo.cards.packs;

import com.gempukku.swccgo.common.Rarity;

import java.util.List;
import java.util.Set;

/**
 * An interface for representing cards (and rarity of those cards) in a set of SWCCG cards.
 */
public interface SetRarity {

    /**
     * Gets the cards of the specified rarity.
     * @param rarity the rarity
     * @return list of cards of the specified rarity
     */
    List<String> getCardsOfRarity(Rarity rarity);

    /**
     * Gets the rarity of the specified blueprint card id.
     * @param blueprintCardId the blueprint card id
     * @return the rarity
     */
    Rarity getCardRarity(String blueprintCardId);

    /**
     * Gets the blueprint card ids of all the cards in the set.
     * @return the blueprint card ids
     */
    Set<String> getAllCards();
}
