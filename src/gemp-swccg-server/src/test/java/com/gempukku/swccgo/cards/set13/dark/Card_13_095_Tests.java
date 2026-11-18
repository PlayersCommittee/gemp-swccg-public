package com.gempukku.swccgo.cards.set13.dark;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * VHD for Weapon Of A Sith — issue #972.
 * Weapon Levitation steal of a character weapon must require WOAS destiny.
 */
public class Card_13_095_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("levitation", "6_79"); // Weapon Levitation (Jabba's Palace)
                    put("obiwan", "1_21"); // Obi-Wan Kenobi ability 6
                }},
                new HashMap<>() {{
                    put("woas", "13_95"); // Weapon Of A Sith
                    put("vader", "1_168"); // Darth Vader ability 6
                    put("saber", "1_324"); // Vader's Lightsaber
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

    /** Leave the BATTLE_INITIATED response window open for LS (Weapon Levitation). */
    private void openBattleInitiatedWindowForLS(VirtualTableScenario scn, boolean darkInitiates) {
        var site = scn.GetLSStartingLocation();
        if (darkInitiates) {
            scn.SkipToDSTurn(Phase.BATTLE);
            assertTrue(scn.DSCanInitiateBattle(site));
            scn.DSUseCardAction(site, "Initiate battle");
        } else {
            scn.SkipToLSTurn(Phase.BATTLE);
            assertTrue(scn.LSCanInitiateBattle(site));
            scn.LSUseCardAction(site, "Initiate battle");
        }
        scn.PassForceUseResponses();
        for (int i = 0; i < 10; i++) {
            if (scn.LSGetDecision() != null) {
                return;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                break;
            }
        }
    }

    /** Advance until a destiny-related decision appears (WOAS gate). */
    private boolean advanceUntilDestinyDecision(VirtualTableScenario scn) {
        for (int i = 0; i < 40; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return false;
            }
            String lower = decision.getText().toLowerCase();
            if (lower.contains("destiny") || lower.contains("weapon of a sith") || lower.contains("make light")) {
                return true;
            }
            if (lower.contains("optional") || lower.contains("playing") || lower.contains("force")) {
                scn.PassResponses();
            } else if (lower.contains("required")) {
                scn.PlayerDecided(scn.GetDecidingPlayer(), "0");
            } else {
                return false;
            }
        }
        return false;
    }

    private void finishPendingResponses(VirtualTableScenario scn) {
        for (int i = 0; i < 50; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                break;
            }
            String text = decision.getText().toLowerCase();
            if (text.contains("optional") || text.contains("destiny") || text.contains("just drew")
                    || text.contains("just completed") || text.contains("required")
                    || text.contains("about to") || text.contains("playing") || text.contains("force")) {
                scn.PassResponses();
            } else {
                break;
            }
        }
    }

    @Test
    public void WeaponOfASithStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("woas").getBlueprint();
        assertEquals("Weapon Of A Sith", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REFLECTIONS_III);
            add(Icon.DEFENSIVE_SHIELD);
            add(Icon.EPISODE_I);
        }});
        assertEquals(ExpansionSet.REFLECTIONS_III, card.getExpansionSet());
        assertEquals(Rarity.PM, card.getRarity());
    }

    @Test
    public void WeaponOfASithWeaponLevitationStealRequiresDestinyAndFailsWhenDestinyTooLow() {
        var scn = GetScenario();

        var levitation = scn.GetLSCard("levitation");
        var obiwan = scn.GetLSCard("obiwan");
        scn.MoveCardsToHand(levitation);

        var site = scn.GetLSStartingLocation();
        var woas = scn.GetDSCard("woas");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(woas);
        scn.MoveCardsToLocation(site, obiwan, vader);
        scn.AttachCardsTo(vader, saber);
        assertEquals(vader, saber.getAttachedTo());

        openBattleInitiatedWindowForLS(scn, false);
        assertTrue(scn.LSPlayLostInterruptAvailable(levitation));
        scn.LSPlayLostInterrupt(levitation);
        scn.LSChooseCard(saber);
        scn.LSChooseCard(obiwan);

        scn.PrepareLSDestiny(0); // 0 + 1 = 1 not > Vader ability 6
        assertTrue("WOAS must require destiny before Weapon Levitation steals",
                advanceUntilDestinyDecision(scn));
        finishPendingResponses(scn);

        assertEquals(vader, saber.getAttachedTo());
        assertEquals(Zone.OUT_OF_PLAY, levitation.getZone());
    }

    @Test
    public void WeaponOfASithWeaponLevitationStealSucceedsWhenDestinyHighEnough() {
        var scn = GetScenario();

        var levitation = scn.GetLSCard("levitation");
        var obiwan = scn.GetLSCard("obiwan");
        scn.MoveCardsToHand(levitation);

        var site = scn.GetLSStartingLocation();
        var woas = scn.GetDSCard("woas");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(woas);
        scn.MoveCardsToLocation(site, obiwan, vader);
        scn.AttachCardsTo(vader, saber);

        openBattleInitiatedWindowForLS(scn, false);
        assertTrue(scn.LSPlayLostInterruptAvailable(levitation));
        scn.LSPlayLostInterrupt(levitation);
        scn.LSChooseCard(saber);
        scn.LSChooseCard(obiwan);

        scn.PrepareLSDestiny(7); // 7 + 1 = 8 > ability 6
        assertTrue("WOAS must require destiny before Weapon Levitation steals",
                advanceUntilDestinyDecision(scn));
        finishPendingResponses(scn);

        assertEquals(obiwan, saber.getAttachedTo());
        assertEquals(scn.LS, saber.getOwner());
        assertNotEquals(Zone.OUT_OF_PLAY, levitation.getZone());
    }

    @Test
    public void WeaponOfASithWeaponLevitationOnDarkTurnBattleRequiresDestinyFailurePath() {
        var scn = GetScenario();

        var levitation = scn.GetLSCard("levitation");
        var obiwan = scn.GetLSCard("obiwan");
        scn.MoveCardsToHand(levitation);

        var site = scn.GetLSStartingLocation();
        var woas = scn.GetDSCard("woas");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(woas);
        scn.MoveCardsToLocation(site, obiwan, vader);
        scn.AttachCardsTo(vader, saber);

        // Replay-shaped: DS turn battle; LS responds with Weapon Levitation
        openBattleInitiatedWindowForLS(scn, true);
        assertTrue(scn.LSPlayLostInterruptAvailable(levitation));
        scn.LSPlayLostInterrupt(levitation);
        scn.LSChooseCard(saber);
        scn.LSChooseCard(obiwan);

        scn.PrepareLSDestiny(0);
        assertTrue("WOAS must require destiny on DS-turn Weapon Levitation path",
                advanceUntilDestinyDecision(scn));
        finishPendingResponses(scn);

        assertEquals(vader, saber.getAttachedTo());
        assertEquals(Zone.OUT_OF_PLAY, levitation.getZone());
    }

    @Test
    public void WeaponLevitationStealsWithoutWeaponOfASithWithoutDestinyGate() {
        var scn = GetScenario();

        var levitation = scn.GetLSCard("levitation");
        var obiwan = scn.GetLSCard("obiwan");
        scn.MoveCardsToHand(levitation);

        var site = scn.GetLSStartingLocation();
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");

        scn.StartGame();
        scn.MoveCardsToLocation(site, obiwan, vader);
        scn.AttachCardsTo(vader, saber);

        openBattleInitiatedWindowForLS(scn, false);
        assertTrue(scn.LSPlayLostInterruptAvailable(levitation));
        scn.LSPlayLostInterrupt(levitation);
        scn.LSChooseCard(saber);
        scn.LSChooseCard(obiwan);
        scn.PassAllResponses();
        finishPendingResponses(scn);

        assertEquals(obiwan, saber.getAttachedTo());
        assertEquals(scn.LS, saber.getOwner());
    }
}
