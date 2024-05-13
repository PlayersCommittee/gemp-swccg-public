package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.GameUtils;

import java.util.HashMap;
import java.util.Map;

public class SwccgoCardBlueprintLibraryTest {
    public static void main(String[] args) {
        SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();

        Map<String, String> cardNames = new HashMap<String, String>();
        for (int i = 1; i <= 1; i++) {
            for (int j = 1; j <= 1; j++) {
                String blueprintId = i + "_" + j;
                try {
                    if (blueprintId.equals(library.getBaseBlueprintId(blueprintId))) {
                        SwccgCardBlueprint cardBlueprint = library.getSwccgoCardBlueprint(blueprintId);
                        String cardName = GameUtils.getFullName(cardBlueprint);
                        if (cardNames.containsKey(cardName) && cardBlueprint.getCardCategory() != CardCategory.LOCATION)
                            System.out.println("Multiple detected - " + cardName + ": " + cardNames.get(cardName) + " and " + blueprintId);
                        else
                            cardNames.put(cardName, blueprintId);
                    }
                } catch (IllegalArgumentException exp) {
                    //exp.printStackTrace();
                }
            }
        }

    }
}
