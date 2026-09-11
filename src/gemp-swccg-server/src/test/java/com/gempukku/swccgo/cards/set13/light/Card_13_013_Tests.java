package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.MayFireRepeatedlyAtSameTargetModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Reflections III 13_13 Desperate Times.
 * Scenarios from the development Google Doc.
 */
public class Card_13_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("desperate", "13_13");
                    put("han", "1_11");
                    put("blaster", "1_153");
                    put("e11b", "8_85");
                    put("greeve", "8_18");
                    put("savareen", "213_54");
                    put("ls_cantina", "1_128");
                }},
                new HashMap<>() {{
                    put("trooper1", "1_194");
                    put("trooper2", "1_194");
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
    public void DesperateTimesStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("desperate").getBlueprint();

        assertEquals("Desperate Times", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        // AR errata: no Episode I icon
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REFLECTIONS_III);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.REFLECTIONS_III, card.getExpansionSet());
        assertEquals(Rarity.PM, card.getRarity());
    }

    @Test
    public void DesperateTimesNotPlayableOutsideBattle() {
        var scn = GetScenario();
        var desperate = scn.GetLSCard("desperate");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, han, trooper1, trooper2);
        scn.AttachCardsTo(han, blaster);
        scn.MoveCardsToHand(desperate);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(desperate));
    }

    @Test
    public void DesperateTimesNotPlayableWhenOpponentHasEqualOrFewerCharacters() {
        var scn = GetScenario();
        var desperate = scn.GetLSCard("desperate");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper1 = scn.GetDSCard("trooper1");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        // 1 vs 1 participating
        scn.MoveCardsToLocation(cantina, han, trooper1);
        scn.AttachCardsTo(han, blaster);
        scn.MoveCardsToHand(desperate);

        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(cantina);

        assertFalse(scn.LSCardActionAvailable(desperate));
    }

    @Test
    public void DesperateTimesPlayableWhenOpponentHasMoreCharacters() {
        var scn = GetScenario();
        var desperate = scn.GetLSCard("desperate");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, han, trooper1, trooper2);
        scn.AttachCardsTo(han, blaster);
        scn.MoveCardsToHand(desperate);

        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(cantina);

        assertTrue(scn.LSCardActionAvailable(desperate));
    }

    @Test
    public void DesperateTimesNotPlayableWithOnlyRepeatingBlaster() {
        var scn = GetScenario();
        var desperate = scn.GetLSCard("desperate");
        var greeve = scn.GetLSCard("greeve");
        var e11b = scn.GetLSCard("e11b");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        // Greeve is scout warrior — valid deploy target for E-11B (fires repeatedly)
        scn.MoveCardsToLocation(cantina, greeve, trooper1, trooper2);
        scn.AttachCardsTo(greeve, e11b);
        scn.MoveCardsToHand(desperate);

        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(cantina);

        assertFalse(scn.LSCardActionAvailable(desperate));
    }

    @Test
    public void DesperateTimesNotPlayableWhenBlasterMayFireRepeatedlyAtSameTarget() {
        var scn = GetScenario();
        var desperate = scn.GetLSCard("desperate");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, han, trooper1, trooper2);
        scn.AttachCardsTo(han, blaster);
        scn.MoveCardsToHand(desperate);

        // Reuse PR 1052 API: same-target repeatedly grant makes the blaster ineligible
        scn.ApplyAdHocModifier(new MayFireRepeatedlyAtSameTargetModifier(han, blaster, 2));

        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(cantina);

        assertFalse(scn.LSCardActionAvailable(desperate));
    }

    @Test
    public void DesperateTimesFiresBlasterForFreeWithPlusOneTotalWeaponDestiny() {
        var scn = GetScenario();
        var desperate = scn.GetLSCard("desperate");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, han, trooper1, trooper2);
        scn.AttachCardsTo(han, blaster);
        scn.MoveCardsToHand(desperate);

        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        // Blaster Rifle: hit if destiny +1 > DV. Stormtrooper DV is typically 1.
        // destiny 0 => base total 1 (not > 1); with DT +1 => total 2 > 1 hits.
        scn.PrepareLSDestiny(0);
        scn.LSInitiateBattle(cantina);

        assertTrue(scn.LSCardActionAvailable(desperate));
        scn.LSPlayCard(desperate);
        scn.LSChooseCard(blaster);
        scn.PassAllResponses();
        // Choose weapon target
        if (scn.LSHasCardChoiceAvailable(trooper1)) {
            scn.LSChooseCard(trooper1);
        } else if (scn.LSHasCardChoiceAvailable(trooper2)) {
            scn.LSChooseCard(trooper2);
        }
        scn.PassAllResponses();

        assertTrue(trooper1.isHit() || trooper2.isHit());
        assertEquals(Zone.TOP_OF_LOST_PILE, desperate.getZone());
    }

    @Test
    public void DesperateTimesPlayableAfterBlasterAlreadyFiredAndDoesNotGrantOngoingSecondFire() {
        var scn = GetScenario();
        var desperate = scn.GetLSCard("desperate");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, han, trooper1, trooper2);
        scn.AttachCardsTo(han, blaster);
        scn.MoveCardsToHand(desperate);

        scn.LSActivateForceCheat(10);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(5);
        scn.LSInitiateBattle(cantina);

        // Fire normally first (uses Force)
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper1);
        scn.PassAllResponses();

        // Even if already fired, Desperate Times may fire it again
        assertTrue(scn.LSCardActionAvailable(desperate));
        scn.PrepareLSDestiny(5);
        scn.LSPlayCard(desperate);
        scn.LSChooseCard(blaster);
        scn.PassAllResponses();
        scn.LSChooseCard(trooper2);
        scn.PassAllResponses();

        assertTrue(trooper2.isHit() || trooper1.isHit());
        // Does not leave a lasting "may fire again" — normal Fire should not be available after two fires
        assertFalse(scn.LSCardActionAvailable(blaster, "Fire"));
    }

    @Test
    public void DesperateTimesAfterFiringUnfiredBlasterSetsFiredStatus() {
        var scn = GetScenario();
        var desperate = scn.GetLSCard("desperate");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, han, trooper1, trooper2);
        scn.AttachCardsTo(han, blaster);
        scn.MoveCardsToHand(desperate);

        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(5);
        scn.LSInitiateBattle(cantina);

        scn.LSPlayCard(desperate);
        scn.LSChooseCard(blaster);
        scn.PassAllResponses();
        scn.LSChooseCard(trooper1);
        scn.PassAllResponses();

        // After DT resolves the first fire, normal Fire should not be available again this battle
        assertFalse(scn.LSCardActionAvailable(blaster, "Fire"));
    }

    @Test
    public void DesperateTimesPlayableAfterSavareenStandoff() {
        var scn = GetScenario();
        var desperate = scn.GetLSCard("desperate");
        var savareen = scn.GetLSCard("savareen");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var cantina = scn.GetLSCard("ls_cantina");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, han, trooper1, trooper2);
        scn.AttachCardsTo(han, blaster);
        scn.MoveCardsToHand(desperate, savareen);

        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(5);
        scn.LSInitiateBattle(cantina);

        // Savareen: fire blaster at battle start response, then may fire again
        if (scn.LSCardActionAvailable(savareen)) {
            scn.LSPlayCard(savareen);
            scn.LSChooseCard(blaster);
            scn.PassAllResponses();
            if (scn.LSHasCardChoiceAvailable(trooper1)) {
                scn.LSChooseCard(trooper1);
                scn.PassAllResponses();
            }
        }

        assertTrue(scn.LSCardActionAvailable(desperate));
    }
}
