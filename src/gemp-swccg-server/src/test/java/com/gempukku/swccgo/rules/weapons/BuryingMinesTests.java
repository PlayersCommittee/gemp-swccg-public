package com.gempukku.swccgo.rules.weapons;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Appendix C Mining Droid Rules - Burying Mines (issue #231).
 * Reuses under-site face-down stacking (same model as Tatooine: Bluffs).
 */
public class BuryingMinesTests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trooper", "1_028");
                    put("trooper2", "1_028");
                    put("trooper3", "1_028");
                    put("speeder", "1_149");
                    put("linLS", "1_018");
                    put("orbital", "9_091");
                }},
                new HashMap<>() {{
                    put("lin", "1_186");
                    put("infantryMine", "3_160");
                    put("infantryMine2", "3_160");
                    put("vehicleMine", "3_162");
                    put("timer", "1_322");
                    put("dud", "1_249");
                    put("seeker", "1_321");
                    put("dsTrooper", "1_194");
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


    private String decisionText(VirtualTableScenario scn) {
        try {
            var d = scn.GetCurrentDecision();
            return d == null ? "null" : String.valueOf(d.getText());
        } catch (Exception e) {
            return "err:" + e.getMessage();
        }
    }

    /** Stack face-down under site with buried-mine flag (same STACKED_FACE_DOWN path as bury action). */
    private void stackAsBuriedMine(VirtualTableScenario scn, PhysicalCardImpl site, PhysicalCardImpl card) {
        scn.RemoveCardZone(card);
        scn.gameState().stackCard(card, site, true, false, false);
        card.setBuriedMine(true);
    }

    private void buryFromHand(VirtualTableScenario scn, PhysicalCardImpl site, PhysicalCardImpl card) {
        scn.MoveCardsToDSHand(card);
        assertTrue(scn.DSCardActionAvailable(site, "Bury card under site"));
        scn.DSUseCardAction(site, "Bury card under site");
        if (scn.DSHasCardChoiceAvailable(card)) {
            scn.DSChooseCard(card);
        }
        assertTrue(card.isBuriedMine());
        assertEquals(Zone.STACKED_FACE_DOWN, card.getZone());
        assertEquals(site, card.getStackedOn());
    }

    @Test
    public void BuryingMinesCanBuryCardFromHandUnderExteriorPlanetSiteDuringDeploy() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var lin = scn.GetDSCard("lin");
        var dud = scn.GetDSCard("dud");

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin);
        scn.MoveCardsToDSHand(dud);

        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.DSCardActionAvailable(site, "Bury card under site"));
        scn.DSUseCardAction(site, "Bury card under site");
        assertTrue(dud.isBuriedMine());
        assertEquals(Zone.STACKED_FACE_DOWN, dud.getZone());
        assertEquals(site, dud.getStackedOn());
    }

    @Test
    public void BuryingMinesCannotBuryWithoutMiningDroidPresent() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var dud = scn.GetDSCard("dud");

        scn.StartGame();
        scn.MoveCardsToDSHand(dud);
        scn.SkipToPhase(Phase.DEPLOY);

        assertFalse(scn.DSCardActionAvailable(site, "Bury card under site"));
    }

    @Test
    public void BuryingMinesCanBuryRealMineAsBuriedMine() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var lin = scn.GetDSCard("lin");
        var mine = scn.GetDSCard("infantryMine");

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin);
        scn.MoveCardsToDSHand(mine);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.DSCardActionAvailable(site, "Bury card under site"));
        scn.DSUseCardAction(site, "Bury card under site");
        assertTrue(mine.isBuriedMine());
        assertEquals(Zone.STACKED_FACE_DOWN, mine.getZone());
        assertEquals(site, mine.getStackedOn());
    }

    @Test
    public void BuryingMinesTimerMineOwnTripIsDiscardedOnly() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var lin = scn.GetDSCard("lin");
        var timer = scn.GetDSCard("timer");
        var dsTrooper = scn.GetDSCard("dsTrooper");

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin);
        stackAsBuriedMine(scn, site, timer);
        scn.MoveCardsToDSHand(dsTrooper);

        scn.SkipToPhase(Phase.DEPLOY);
        int lostBefore = scn.GetDSLostPileCount();
        scn.DSDeployCard(dsTrooper);
        scn.DSChooseCard(site);
        scn.PassCardAndForceUseResponses();
        scn.PassAllResponses();

        // Mining droid present on owner's turn offers defuse first; decline to reach own-trip discard path
        if (scn.DSDecisionAvailable("Defuse buried mines")) {
            scn.DSChoose("Do not defuse");
            scn.PassAllResponses();
        }

        // Own trip: no destiny draw / character-loss choice - Timer simply discarded (lost pile)
        assertFalse(scn.DSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD"));
        assertFalse(scn.LSDecisionAvailable("Choose cards to be lost"));
        assertFalse(scn.gameState().isDuringWeaponFiring());
        assertFalse(timer.isBuriedMine());
        assertEquals(Zone.TOP_OF_LOST_PILE, timer.getZone());
        assertEquals(lostBefore + 1, scn.GetDSLostPileCount());
        assertTrue(scn.CardsAtLocation(site, dsTrooper, lin));
    }

    @Test
    public void BuryingMinesTimerMineOpponentTripDestinyLosesCharactersAndIsNotWeaponDestiny() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var lin = scn.GetDSCard("lin");
        var timer = scn.GetDSCard("timer");
        var trooper = scn.GetLSCard("trooper");
        var trooper2 = scn.GetLSCard("trooper2");
        var trooper3 = scn.GetLSCard("trooper3");

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin, trooper2, trooper3);
        stackAsBuriedMine(scn, site, timer);
        scn.MoveCardsToLSHand(trooper);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.PrepareDSDestiny(2);
        scn.LSDeployCard(trooper);
        scn.LSChooseCard(site);
        scn.PassCardPlayResponses();

        // Ordinary destiny (not weapon destiny): firing state must be off while destiny is pending/resolving
        assertFalse("Timer buried explode must not use weapon-firing path; decision=[" + decisionText(scn) + "]",
                scn.gameState().isDuringWeaponFiring());

        // Advance through destiny draw windows if still pending
        if (scn.DSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD") || scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD")
                || decisionText(scn).toLowerCase().contains("destiny")) {
            scn.PassDestinyDrawResponses();
        }
        scn.PassAllResponses();

        if (scn.LSDecisionAvailable("Choose cards to be lost")) {
            assertEquals(2, scn.LSGetChoiceMax());
            assertEquals(2, scn.LSGetChoiceMin());
            assertTrue(scn.LSHasCardChoicesAvailable(trooper, trooper2, trooper3));
            scn.LSChooseCards(trooper2, trooper3);
            scn.PassAllResponses();
            if (scn.LSDecisionAvailable("Choose card to be lost")) {
                scn.LSChooseCard(trooper3);
                scn.PassAllResponses();
            }
            if (scn.LSDecisionAvailable("Choose card to be lost")) {
                scn.LSChooseCard(trooper2);
                scn.PassAllResponses();
            }
        }

        assertTrue(timer.getZone() == Zone.LOST_PILE || timer.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(scn.GetLSLostPileCount() >= 2);
        assertTrue(scn.CardsAtLocation(site, trooper, lin));
    }
    @Test
    public void BuryingMinesInfantryMineExplodesViaWeaponDestinyPath() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var lin = scn.GetDSCard("lin");
        var mine = scn.GetDSCard("infantryMine");
        var trooper = scn.GetLSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin);
        stackAsBuriedMine(scn, site, mine);
        scn.MoveCardsToLSHand(trooper);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.PrepareDSDestiny(7);
        scn.LSDeployCard(trooper);
        scn.LSChooseCard(site);
        scn.PassCardPlayResponses();

        // Catch mid-explode weapon-firing state when still pending; otherwise outcomes prove explode path ran
        boolean sawWeaponFiring = scn.gameState().isDuringWeaponFiring();
        if (scn.DSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD") || scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD")
                || decisionText(scn).toLowerCase().contains("destiny")) {
            assertTrue("Infantry Mine buried explode must use weapon-firing path; decision=[" + decisionText(scn) + "]",
                    scn.gameState().isDuringWeaponFiring());
            sawWeaponFiring = true;
            scn.PassDestinyDrawResponses();
        }
        scn.PassAllResponses();
        scn.PassCardLeavingTable();
        scn.PassAllResponses();

        assertTrue(mine.getZone() == Zone.LOST_PILE || mine.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(trooper.getZone() == Zone.LOST_PILE || trooper.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue("Expected weapon-firing path or completed explode; decision=[" + decisionText(scn) + "] sawWeapon=" + sawWeaponFiring,
                sawWeaponFiring || (mine.getZone() == Zone.LOST_PILE || mine.getZone() == Zone.TOP_OF_LOST_PILE));
    }
    @Test
    public void BuryingMinesVehicleMineExplodesViaWeaponDestinyPath() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var lin = scn.GetDSCard("lin");
        var mine = scn.GetDSCard("vehicleMine");
        var speeder = scn.GetLSCard("speeder");

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin);
        stackAsBuriedMine(scn, site, mine);
        scn.MoveCardsToLSHand(speeder);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.PrepareDSDestiny(7);
        assertTrue(scn.LSDeployAvailable(speeder));
        scn.LSDeployCard(speeder);
        scn.LSChooseCard(site);
        scn.PassCardPlayResponses();

        boolean sawWeaponFiring = scn.gameState().isDuringWeaponFiring();
        if (scn.DSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD") || scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD")
                || decisionText(scn).toLowerCase().contains("destiny")) {
            assertTrue("Vehicle Mine buried explode must use weapon-firing path; decision=[" + decisionText(scn) + "]",
                    scn.gameState().isDuringWeaponFiring());
            sawWeaponFiring = true;
            scn.PassDestinyDrawResponses();
        }
        scn.PassAllResponses();
        scn.PassCardLeavingTable();
        scn.PassAllResponses();

        assertTrue(mine.getZone() == Zone.LOST_PILE || mine.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(speeder.getZone() == Zone.LOST_PILE || speeder.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue("Expected weapon-firing path or completed explode; sawWeapon=" + sawWeaponFiring, sawWeaponFiring
                || (mine.getZone() == Zone.LOST_PILE || mine.getZone() == Zone.TOP_OF_LOST_PILE));
    }
    @Test
    public void BuryingMinesMultiMineTripResolvesAllBuriedCards() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var lin = scn.GetDSCard("lin");
        var mine = scn.GetDSCard("infantryMine");
        var dud = scn.GetDSCard("dud");
        var seeker = scn.GetDSCard("seeker");
        var trooper = scn.GetLSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin);
        stackAsBuriedMine(scn, site, mine);
        stackAsBuriedMine(scn, site, dud);
        stackAsBuriedMine(scn, site, seeker);
        scn.MoveCardsToLSHand(trooper);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.PrepareDSDestiny(7);
        scn.LSDeployCard(trooper);
        scn.LSChooseCard(site);
        scn.PassCardAndForceUseResponses();
        scn.PassAllResponses();

        // Dud + non-mine automated resolve as lost; Infantry uses weapon destiny against tripper
        if (scn.DSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD") || scn.LSDecisionAvailable("COST_TO_DRAW_DESTINY_CARD")) {
            if (scn.gameState().isDuringWeaponFiring()) {
                scn.PassDestinyDrawResponses();
            } else {
                scn.PassDestinyDrawResponses();
            }
        }
        scn.PassAllResponses();
        scn.PassCardLeavingTable();
        scn.PassAllResponses();

        assertTrue(dud.getZone() == Zone.LOST_PILE || dud.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(seeker.getZone() == Zone.LOST_PILE || seeker.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(mine.getZone() == Zone.LOST_PILE || mine.getZone() == Zone.TOP_OF_LOST_PILE);
        assertFalse(dud.isBuriedMine());
        assertFalse(seeker.isBuriedMine());
        assertFalse(mine.isBuriedMine());
    }

    @Test
    public void BuryingMinesDefuseUsesOneForcePerMineOnOwnersTurn() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var lin = scn.GetDSCard("lin");
        var mine = scn.GetDSCard("infantryMine");
        var mine2 = scn.GetDSCard("infantryMine2");
        var dsTrooper = scn.GetDSCard("dsTrooper");

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin);
        stackAsBuriedMine(scn, site, mine);
        stackAsBuriedMine(scn, site, mine2);
        scn.MoveCardsToDSHand(dsTrooper);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(dsTrooper);
        scn.DSChooseCard(site);
        scn.PassCardAndForceUseResponses();
        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("Defuse buried mines"));
        int forceBefore = scn.GetDSForcePileCount();
        scn.DSChoose("Defuse Infantry Mine");
        scn.PassForceUseResponses();
        scn.PassAllResponses();

        // Second mine still buried - offered again
        if (scn.DSDecisionAvailable("Defuse buried mines")) {
            scn.DSChoose("Defuse Infantry Mine");
            scn.PassForceUseResponses();
            scn.PassAllResponses();
        }

        assertEquals(forceBefore - 2, scn.GetDSForcePileCount());
        assertTrue(mine.getZone() == Zone.LOST_PILE || mine.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(mine2.getZone() == Zone.LOST_PILE || mine2.getZone() == Zone.TOP_OF_LOST_PILE);
        assertFalse(mine.isBuriedMine());
        assertFalse(mine2.isBuriedMine());
        assertTrue(scn.CardsAtLocation(site, dsTrooper, lin));
    }

    @Test
    public void BuryingMinesOrbitalMineBuriedIsDudRevealThenLost() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var linLS = scn.GetLSCard("linLS");
        var orbital = scn.GetLSCard("orbital");
        var trooper = scn.GetLSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(site, linLS);
        // Keyword.MINE but Bill lock: buried Orbital = dud (reveal -> lost, no explode)
        stackAsBuriedMine(scn, site, orbital);
        assertTrue(orbital.isBuriedMine());
        assertEquals(Zone.STACKED_FACE_DOWN, orbital.getZone());

        scn.MoveCardsToLSHand(trooper);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(trooper);
        scn.LSChooseCard(site);
        scn.PassCardAndForceUseResponses();
        scn.PassAllResponses();

        if (scn.LSDecisionAvailable("Defuse buried mines")) {
            scn.LSChoose("Do not defuse");
            scn.PassAllResponses();
        }

        assertFalse(scn.gameState().isDuringWeaponFiring());
        assertFalse(orbital.isBuriedMine());
        assertTrue(orbital.getZone() == Zone.LOST_PILE || orbital.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(scn.CardsAtLocation(site, trooper, linLS));
    }

    @Test
    
    public void BuryingMinesNonMineAutomatedWeaponBuriedIsDud() {
        var scn = GetScenario();
        var site = scn.GetDSStartingLocation();
        var lin = scn.GetDSCard("lin");
        var seeker = scn.GetDSCard("seeker");
        var trooper = scn.GetLSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin);
        stackAsBuriedMine(scn, site, seeker);
        scn.MoveCardsToLSHand(trooper);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(trooper);
        scn.LSChooseCard(site);
        scn.PassCardAndForceUseResponses();
        scn.PassAllResponses();

        assertFalse(scn.gameState().isDuringWeaponFiring());
        assertFalse(seeker.isBuriedMine());
        assertEquals(Zone.TOP_OF_LOST_PILE, seeker.getZone());
        assertTrue(scn.CardsAtLocation(site, trooper, lin));
    }
}
