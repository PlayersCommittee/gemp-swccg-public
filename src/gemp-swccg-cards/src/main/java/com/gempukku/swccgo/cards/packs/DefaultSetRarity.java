package com.gempukku.swccgo.cards.packs;

import com.gempukku.swccgo.common.Rarity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The implementation of the SetRarity interface that is used to map between cards and their rarity.
 */
public class DefaultSetRarity implements SetRarity {
    private Map<Rarity, List<String>> _rarityList;
    private Map<String, Rarity> _cardsRarity;

    /**
     * Creates a set rarity from the specified rarity and card maps.
     * @param rarityList map of rarity to blueprint card ids for cards of that rarity
     * @param cardsRarity map of blueprint card ids to rarity of that card
     */
    public DefaultSetRarity(Map<Rarity, List<String>> rarityList, Map<String, Rarity> cardsRarity) {
        _rarityList = rarityList;
        _cardsRarity = cardsRarity;
    }

    @Override
    public List<String> getCardsOfRarity(Rarity rarity) {
        final List<String> list = _rarityList.get(rarity);
        if (list == null)
            return Collections.emptyList();
        return Collections.unmodifiableList(list);
    }

    @Override
    public Rarity getCardRarity(String cardId) {
        return _cardsRarity.get(cardId);
    }

    @Override
    public Set<String> getAllCards() {
        return Collections.unmodifiableSet(_cardsRarity.keySet());
    }
}
