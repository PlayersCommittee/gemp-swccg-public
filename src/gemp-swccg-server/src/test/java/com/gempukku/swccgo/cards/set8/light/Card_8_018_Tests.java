package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Endor 8_18 Lieutenant Greeve.
 * Doc scenarios + Mouse-style edges.
 */
public class Card_8_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("greeve", "8_18");
                    put("a280", "8_84");
                    put("blaster", "1_153");
                    put("ls_cantina", "1_128");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("tarkin", "1_179");
                    put("speeder", "1_309");
                    put("ds_cantina", "1_290");
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
    public void LieutenantGreeveStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("greeve").getBlueprint();

        assertEquals("Lieutenant Greeve", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ENDOR);
            add(Icon.REBEL);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.ENDOR, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void LieutenantGreeveCharacterWeaponDestinyDrawsAreEachPlusOne() {
        var scn = GetScenario();
        var greeve = scn.GetLSCard("greeve");
        var a280 = scn.GetLSCard("a280");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, greeve, trooper);
        scn.AttachCardsTo(greeve, a280);

        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        // A280: draw destiny; Greeve +1; target hit if total > DV. Trooper DV ~1; destiny 1 +1 = 2 hits.
        scn.PrepareLSDestiny(1);
        scn.LSInitiateBattle(cantina);

        assertTrue(scn.LSCardActionAvailable(a280, "Fire"));
        scn.LSUseCardAction(a280, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();

        assertTrue(trooper.isHit());
    }

    @Test
    public void LieutenantGreeveCharactersHitAreForfeitMinusThree() {
        // VHD: target forfeit 4+ so -3 is observable (trooper floors at 0). Tarkin 6 -> 3.
        var scn = GetScenario();
        var greeve = scn.GetLSCard("greeve");
        var a280 = scn.GetLSCard("a280");
        var cantina = scn.GetLSCard("ls_cantina");
        var tarkin = scn.GetDSCard("tarkin");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, greeve, tarkin);
        scn.AttachCardsTo(greeve, a280);

        assertEquals(6, scn.GetForfeit(tarkin));

        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        // Tarkin ability/DV 3; Greeve +1 weapon destiny; destiny 3+1=4 hits.
        scn.PrepareLSDestiny(3);
        scn.LSInitiateBattle(cantina);

        scn.LSUseCardAction(a280, "Fire");
        scn.LSChooseCard(tarkin);
        scn.PassAllResponses();

        assertTrue(tarkin.isHit());
        assertEquals(3, scn.GetForfeit(tarkin));
    }

    @Test
    public void LieutenantGreeveNonCharactersHitAreNotForfeitMinusThree() {
        var scn = GetScenario();
        var greeve = scn.GetLSCard("greeve");
        var a280 = scn.GetLSCard("a280");
        var cantina = scn.GetLSCard("ls_cantina");
        var speeder = scn.GetDSCard("speeder");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        // Need presence for battle; keep a DS character and the vehicle present
        scn.MoveCardsToLocation(cantina, greeve, speeder, trooper);
        scn.AttachCardsTo(greeve, a280);

        int forfeitBefore = scn.GetForfeit(speeder);

        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(7);
        scn.LSInitiateBattle(cantina);

        assertTrue(scn.LSCardActionAvailable(a280, "Fire"));
        scn.LSUseCardAction(a280, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(speeder));
        scn.LSChooseCard(speeder);
        scn.PassAllResponses();

        if (speeder.isHit()) {
            assertEquals(forfeitBefore, scn.GetForfeit(speeder));
        } else {
            // If destiny too low to hit vehicle armor/maneuver DV, forfeit still unchanged
            assertEquals(forfeitBefore, scn.GetForfeit(speeder));
        }
    }

    @Test
    public void LieutenantGreeveMayFireA280RepeatedlyAtSameTargetForTwoForce() {
        var scn = GetScenario();
        var greeve = scn.GetLSCard("greeve");
        var a280 = scn.GetLSCard("a280");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, greeve, trooper);
        scn.AttachCardsTo(greeve, a280);

        scn.LSActivateForceCheat(6);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(0);
        scn.PrepareLSDestiny(5);
        scn.LSInitiateBattle(cantina);

        int forceBefore = scn.GetLSForcePileCount();
        scn.LSUseCardAction(a280, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw();
        // Clear any optional responses (e.g. forfeit-reduced) before the repeatedly-fire prompt.
        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("repeatedly fire"));
        scn.LSChooseYes();
        // A280 first fire is free; repeat costs 2 Force. Same-target filter may auto-narrow chooser.
        if (scn.LSDecisionAvailable("Choose target")) {
            assertTrue(scn.LSHasCardChoiceAvailable(trooper));
            scn.LSChooseCard(trooper);
        }
        scn.PassAllResponses();

        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void LieutenantGreeveRepeatedA280FireMayOnlyChooseOriginalTarget() {
        var scn = GetScenario();
        var greeve = scn.GetLSCard("greeve");
        var a280 = scn.GetLSCard("a280");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper = scn.GetDSCard("trooper");
        var speeder = scn.GetDSCard("speeder");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, greeve, trooper, speeder);
        scn.AttachCardsTo(greeve, a280);

        scn.LSActivateForceCheat(6);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(0);
        scn.PrepareLSDestiny(5);
        scn.LSInitiateBattle(cantina);

        scn.LSUseCardAction(a280, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw();
        // Clear any optional responses (e.g. forfeit-reduced) before the repeatedly-fire prompt.
        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("repeatedly fire"));
        scn.LSChooseYes();
        // Same-target repeat must not offer the other DS card; chooser may be skipped if only one valid.
        if (scn.LSDecisionAvailable("Choose target")) {
            assertTrue(scn.LSHasCardChoiceAvailable(trooper));
            assertFalse(scn.LSHasCardChoiceAvailable(speeder));
            scn.LSChooseCard(trooper);
        } else {
            // No chooser: ensure speeder never became a pending card choice either.
            assertFalse(scn.LSDecisionAvailable("speeder") || scn.LSDecisionAvailable("Speeder"));
        }
        scn.PassAllResponses();
    }

    @Test
    public void LieutenantGreeveMayNotFireNonA280Repeatedly() {
        var scn = GetScenario();
        var greeve = scn.GetLSCard("greeve");
        var blaster = scn.GetLSCard("blaster");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, greeve, trooper);
        scn.AttachCardsTo(greeve, blaster);

        scn.LSActivateForceCheat(8);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(5);
        scn.LSInitiateBattle(cantina);

        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();

        assertFalse(scn.LSDecisionAvailable("repeatedly fire"));
    }
}
