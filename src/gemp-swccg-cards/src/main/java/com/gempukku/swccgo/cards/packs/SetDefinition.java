package com.gempukku.swccgo.cards.packs;

import java.util.List;
import java.util.Set;

public interface SetDefinition {
    String getSetName();

    String getSetId();

    boolean hasFlag(String flag);

    List<String> getCardsOfRarity(String rarity);

    List<String> getTengwarCards();

    String getCardRarity(String cardId);

    Set<String> getAllCards();
}
