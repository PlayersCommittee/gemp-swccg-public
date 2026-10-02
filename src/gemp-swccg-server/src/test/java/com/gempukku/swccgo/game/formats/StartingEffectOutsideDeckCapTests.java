package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Original Starting Effects (An Unusual Amount Of Fear / Fear Is My Ally) may include
 * at most 10 Defensive Shields outside the deck. Virtual "any number" Starting Effects
 * are uncapped by this rule.
 */
public class StartingEffectOutsideDeckCapTests {
    private static SwccgCardBlueprintLibrary library;
    private static SwccgFormat openFormat;

    // Light: An Unusual Amount Of Fear; Dark: Fear Is My Ally
    private static final String LS_ORIGINAL_SE = "13_5";
    private static final String DS_ORIGINAL_SE = "13_69";
    // Light: Anger, Fear, Aggression (V); Dark: Knowledge And Defense (V)
    private static final String LS_VIRTUAL_SE = "200_35";
    private static final String DS_VIRTUAL_SE = "200_110";

    private static final String[] LS_SHIELDS = {
            "13_1", "13_3", "13_4", "13_6", "13_8",
            "13_15", "13_16", "13_22", "13_30", "13_35",
            "13_37", "13_38", "13_44", "13_47", "13_49"
    };
    private static final String[] DS_SHIELDS = {
            "13_51", "13_52", "13_54", "13_61", "13_63",
            "13_66", "13_68", "13_72", "13_78", "13_81",
            "13_84", "13_86", "13_90", "200_95", "200_99"
    };

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        openFormat = new SwccgoFormatLibrary(library).getFormat("open");
    }

    @Test
    public void AnUnusualAmountOfFearRejectsElevenOutsideDeckShields() {
        SwccgDeck deck = lightDeckWithStartingEffect(LS_ORIGINAL_SE, 11);
        try {
            openFormat.validateDeck(deck);
            fail("Expected DeckInvalidException for original SE with 11 shields");
        } catch (DeckInvalidException e) {
            assertTrue(e.getMessage().contains("up to 10 Defensive Shields"));
            assertTrue(e.getMessage().contains("11"));
        }
    }

    @Test
    public void FearIsMyAllyRejectsElevenOutsideDeckShields() {
        SwccgDeck deck = darkDeckWithStartingEffect(DS_ORIGINAL_SE, 11);
        try {
            openFormat.validateDeck(deck);
            fail("Expected DeckInvalidException for original SE with 11 shields");
        } catch (DeckInvalidException e) {
            assertTrue(e.getMessage().contains("up to 10 Defensive Shields"));
        }
    }

    @Test
    public void AnUnusualAmountOfFearAllowsTenOutsideDeckShields() throws DeckInvalidException {
        openFormat.validateDeck(lightDeckWithStartingEffect(LS_ORIGINAL_SE, 10));
    }

    @Test
    public void AngerFearAggressionVirtualAllowsElevenOutsideDeckShields() throws DeckInvalidException {
        openFormat.validateDeck(lightDeckWithStartingEffect(LS_VIRTUAL_SE, 11));
    }

    @Test
    public void KnowledgeAndDefenseVirtualAllowsElevenOutsideDeckShields() throws DeckInvalidException {
        openFormat.validateDeck(darkDeckWithStartingEffect(DS_VIRTUAL_SE, 11));
    }

    @Test
    public void OriginalStartingEffectAllowsNonShieldOutsideDeckBeyondTenTotal() throws DeckInvalidException {
        // 10 shields + Hidden Base (or equivalent non-shield OOD) must still be legal
        SwccgDeck deck = lightDeckWithStartingEffect(LS_ORIGINAL_SE, 10);
        deck.addCardOutsideDeck("200_16"); // The Mythrol (non-shield OOD)
        openFormat.validateDeck(deck);
    }

    private static SwccgDeck lightDeckWithStartingEffect(String startingEffectId, int shieldCount) {
        SwccgDeck deck = new SwccgDeck("test-ls");
        deck.addCard(startingEffectId);
        deck.addCard("5_079"); // starting location filler slot
        for (int i = 0; i < 58; i++) {
            deck.addCard("1_28");
        }
        for (int i = 0; i < shieldCount; i++) {
            deck.addCardOutsideDeck(LS_SHIELDS[i]);
        }
        return deck;
    }

    private static SwccgDeck darkDeckWithStartingEffect(String startingEffectId, int shieldCount) {
        SwccgDeck deck = new SwccgDeck("test-ds");
        deck.addCard(startingEffectId);
        deck.addCard("12_176");
        for (int i = 0; i < 58; i++) {
            deck.addCard("1_194");
        }
        for (int i = 0; i < shieldCount; i++) {
            deck.addCardOutsideDeck(DS_SHIELDS[i]);
        }
        return deck;
    }
}
