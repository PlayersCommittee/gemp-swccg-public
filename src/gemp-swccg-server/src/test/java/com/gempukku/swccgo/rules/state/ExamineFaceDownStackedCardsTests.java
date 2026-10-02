package com.gempukku.swccgo.rules.state;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * AR Looking At A Deck, Pile, Or Stack: owner may examine face-down stacked cards
 * on their own cards or any location (ownership gates AR examples; Hatred exception).
 */
public class ExamineFaceDownStackedCardsTests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("conflict", "9_34"); // I Feel The Conflict
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("trooper", "1_194"); // Imperial Trooper
                    put("rebellion", "9_127"); // Insignificant Rebellion
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void OwnerMayExamineOwnCardStackedFaceDownOnOwnCard() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        scn.StackCardsFaceDownOn(trooper, vader);

        assertEquals(Zone.STACKED_FACE_DOWN, vader.getZone());
        assertTrue(GameUtils.canExamineFaceDownStackedCard(scn.gameState(), scn.DS, vader));
        assertFalse(GameUtils.canExamineFaceDownStackedCard(scn.gameState(), scn.LS, vader));
    }

    @Test
    public void OwnerMayExamineOwnCardStackedFaceDownOnLocation() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.StackCardsFaceDownOn(site, vader);

        assertEquals(Zone.STACKED_FACE_DOWN, vader.getZone());
        assertTrue(GameUtils.canExamineFaceDownStackedCard(scn.gameState(), scn.DS, vader));
        assertFalse(GameUtils.canExamineFaceDownStackedCard(scn.gameState(), scn.LS, vader));
    }

    @Test
    public void OwnerMayNotExamineOpponentCardsUnderInsignificantRebellion() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var rebellion = scn.GetDSCard("rebellion");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(rebellion);
        scn.StackCardsFaceDownOn(rebellion, luke);

        assertEquals(Zone.STACKED_FACE_DOWN, luke.getZone());
        // LS owns luke but IR is not LS's Effect / not a location — AR prohibits
        assertFalse(GameUtils.canExamineFaceDownStackedCard(scn.gameState(), scn.LS, luke));
        // DS does not own luke
        assertFalse(GameUtils.canExamineFaceDownStackedCard(scn.gameState(), scn.DS, luke));
    }


    @Test
    public void OwnerMayNotExamineCardsUnderIFeelTheConflict() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var conflict = scn.GetLSCard("conflict");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(conflict);
        scn.StackCardsFaceDownOn(conflict, vader);

        assertEquals(Zone.STACKED_FACE_DOWN, vader.getZone());
        assertFalse(GameUtils.canExamineFaceDownStackedCard(scn.gameState(), scn.DS, vader));
        assertFalse(GameUtils.canExamineFaceDownStackedCard(scn.gameState(), scn.LS, vader));
    }

    @Test
    public void HatredExceptionAllowsOwnerToExamineEvenIfNotOnOwnCardOrLocation() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        // Stack DS hatred card on LS character (not own card, not location) — Hatred exception still allows DS
        scn.MoveCardsToLocation(site, luke);
        scn.StackCardsFaceDownOn(luke, vader);
        vader.setHatredCard(true);

        assertEquals(Zone.STACKED_FACE_DOWN, vader.getZone());
        assertTrue(vader.isHatredCard());
        assertTrue(GameUtils.canExamineFaceDownStackedCard(scn.gameState(), scn.DS, vader));
        assertFalse(GameUtils.canExamineFaceDownStackedCard(scn.gameState(), scn.LS, vader));
    }

    @Test
    public void FaceUpStackedIsNotExaminableViaHelper() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        scn.StackCardsOn(trooper, vader);

        assertEquals(Zone.STACKED, vader.getZone());
        assertFalse(GameUtils.canExamineFaceDownStackedCard(scn.gameState(), scn.DS, vader));
    }

    @Test
    public void BlueprintIdShownToOwnerWhenExaminable() {
        var scn = GetScenario();
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.StackCardsFaceDownOn(site, vader);

        String front = vader.getBlueprintId(true);
        String hiddenFromOpponent = vader.getBlueprintId(scn.gameState(), false);
        assertNotEquals(front, hiddenFromOpponent);
        // Owner examine path uses alwaysShowCardFront=true
        assertEquals(front, vader.getBlueprintId(scn.gameState(), true));
    }
}
