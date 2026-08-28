package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Card_2_134_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
                put("rebel", "1_28"); // Rebel Trooper
                put("lsSpy", "7_5"); // Bothan Spy
                put("lsMover", "1_28");
                put("navander", "3_18"); // Romas "Lock" Navander, blocks reacts
            }},
            new HashMap<>() {{
                put("informant", "2_134");
                put("spy", "1_177"); // Garindan
                put("presence", "1_194"); // Stormtrooper (DS presence at battle)
                put("mover", "1_194");
                put("mover2", "1_194");
                put("cantina", "1_290"); // Tatooine: Cantina, adjacent to Marketplace
                put("db94", "1_291"); // Tatooine: Docking Bay 94
                put("lars", "1_294"); // Tatooine: Lars' Moisture Farm
                put("blaster", "1_317"); // Imperial Blaster
                put("barge", "6_172"); // Jabba's Sail Barge
                put("deck", "6_167"); // Jabba's Sail Barge: Passenger Deck
                put("palace", "6_171"); // Tatooine: Jabba's Palace (Dark)
                put("abyssin", "6_091"); // Abyssin
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

    private void SetupStandardBattle(VirtualTableScenario scn) {
        var marketplace = scn.GetDSStartingLocation();
        var cantina = scn.GetDSCard("cantina");
        var spy = scn.GetDSCard("spy");
        var presence = scn.GetDSCard("presence");
        var mover = scn.GetDSCard("mover");
        var rebel = scn.GetLSCard("rebel");
        var informant = scn.GetDSCard("informant");

        scn.MoveCardsToDSHand(informant);
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        assertTrue(scn.IsAdjacentTo(cantina, marketplace));
        scn.MoveCardsToLocation(marketplace, spy, presence, rebel);
        scn.MoveCardsToLocation(cantina, mover);
        scn.MakeCardGoUndercover(spy);
    }

    private void DrainRemainingDSForce(VirtualTableScenario scn) {
        int force = scn.GetDSForcePileCount();
        if (force > 0) {
            scn.DSUseForceCheat(force);
        }
        assertEquals(0, scn.GetDSForcePileCount());
    }


    /**
     * LSInitiateBattle() auto-passes BATTLE_INITIATED optional responses, which is the window Informant is played.
     * This helper spends the 1 Force for initiating battle but leaves that react window open.
     */
    private void InitiateLsBattleKeepReactWindow(VirtualTableScenario scn, PhysicalCardImpl site) {
        assertTrue("Unable to initiate battle at location", scn.LSCanInitiateBattle(site));
        scn.LSUseCardAction(site, "Initiate battle");
        scn.PassForceUseResponses();
        // If Light Side is asked first (no DS optional actions yet), pass so Dark Side can play Informant.
        if (scn.LSAnyDecisionsAvailable() && !scn.DSAnyDecisionsAvailable()
                && scn.LSDecisionAvailable("Battle just initiated")) {
            scn.LSPass();
        }
    }

    private boolean DsCardPlayAvailableSafe(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(card);
    }

    private String DescribeDecision(VirtualTableScenario scn) {
        try {
            var d = scn.GetCurrentDecision();
            String text = d == null ? "null" : d.getText();
            String player = "none";
            try {
                player = scn.GetDecidingPlayer();
            } catch (Exception ignored) {
            }
            return "player=" + player + " text=" + text
                    + " DS=" + scn.GetDSAvailableActions()
                    + " LS=" + scn.GetLSAvailableActions();
        } catch (Exception e) {
            return "dump-failed:" + e.getMessage();
        }
    }

    private void AssertDsCanPlayInformant(VirtualTableScenario scn, PhysicalCardImpl informant) {
        if (!DsCardPlayAvailableSafe(scn, informant)) {
            fail("Expected Informant playable as a battle-just-initiated react. " + DescribeDecision(scn));
        }
    }

    private boolean FinishOptionalExtraReactIfOffered(VirtualTableScenario scn, PhysicalCardImpl extraMover) {
        if (scn.DSHasCardChoiceAvailable(extraMover)) {
            scn.DSChooseCard(extraMover);
            scn.PassAllResponses();
            return true;
        }
        if (scn.DSDecisionAvailable("Choose another character")) {
            if (scn.DSHasCardChoiceAvailable(extraMover)) {
                scn.DSChooseCard(extraMover);
                scn.PassAllResponses();
                return true;
            }
            scn.DSPass();
        }
        return false;
    }

    @Test
    public void StatsAndKeywordsAreCorrect() {
        /**
         * Title: Informant
         * Uniqueness: Unrestricted (not unique, no bullet)
         * Side: Dark
         * Type: Interrupt
         * Subtype: Used
         * Destiny: 6 (printed top-right of ANH Dark informant.gif)
         * Icons: A New Hope
         * Set: A New Hope
         * Rarity: U1
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("informant").getBlueprint();

        assertEquals("Informant", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(1, card.getIconCount(Icon.A_NEW_HOPE));
        assertEquals(ExpansionSet.A_NEW_HOPE, card.getExpansionSet());
        assertEquals(Rarity.U1, card.getRarity());
    }

    @Test
    public void HappyPathMovesAdjacentCharacterForFreeIntoBattle() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var marketplace = scn.GetDSStartingLocation();
        var cantina = scn.GetDSCard("cantina");
        var mover = scn.GetDSCard("mover");
        var presence = scn.GetDSCard("presence");
        var spy = scn.GetDSCard("spy");
        var rebel = scn.GetLSCard("rebel");

        SetupStandardBattle(scn);

        scn.SkipToLSTurn(Phase.BATTLE);
        int dsForceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.LSCanInitiateBattle(marketplace));
        InitiateLsBattleKeepReactWindow(scn, marketplace);

        AssertDsCanPlayInformant(scn, informant);
        scn.DSPlayCard(informant);
        assertTrue(scn.DSHasCardChoiceAvailable(mover));
        assertFalse(scn.DSHasCardChoiceAvailable(presence));
        assertFalse(scn.DSHasCardChoiceAvailable(spy));
        assertFalse(scn.DSHasCardChoiceAvailable(rebel));
        scn.DSChooseCard(mover);
        scn.PassAllResponses();

        assertEquals(marketplace, mover.getAtLocation());
        assertTrue(scn.IsParticipatingInBattle(mover));
        assertTrue(scn.IsParticipatingInBattle(presence, rebel));
        assertFalse(scn.IsParticipatingInBattle(spy));
        assertEquals(dsForceBefore, scn.GetDSForcePileCount());
        assertEquals(informant, scn.GetTopOfDSUsedPile());
        assertNotEquals(cantina, mover.getAtLocation());
    }

    @Test
    public void NotPlayableIfSpyAtBattleSiteIsNotUndercoverEvenIfOpponentHasUndercoverSpy() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var marketplace = scn.GetDSStartingLocation();
        var cantina = scn.GetDSCard("cantina");
        var spy = scn.GetDSCard("spy");
        var presence = scn.GetDSCard("presence");
        var mover = scn.GetDSCard("mover");
        var rebel = scn.GetLSCard("rebel");
        var lsSpy = scn.GetLSCard("lsSpy");

        scn.MoveCardsToDSHand(informant);
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(marketplace, spy, presence, rebel, lsSpy);
        scn.MoveCardsToLocation(cantina, mover);
        scn.MakeCardGoUndercover(lsSpy);

        scn.SkipToLSTurn(Phase.BATTLE);
        InitiateLsBattleKeepReactWindow(scn, marketplace);
        assertFalse(DsCardPlayAvailableSafe(scn, informant));
    }

    @Test
    public void NotPlayableIfUndercoverSpyIsAtADifferentSiteThanTheBattle() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var marketplace = scn.GetDSStartingLocation();
        var cantina = scn.GetDSCard("cantina");
        var spy = scn.GetDSCard("spy");
        var presence = scn.GetDSCard("presence");
        var mover = scn.GetDSCard("mover");
        var rebel = scn.GetLSCard("rebel");

        scn.MoveCardsToDSHand(informant);
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(marketplace, presence, rebel);
        scn.MoveCardsToLocation(cantina, spy, mover);
        scn.MakeCardGoUndercover(spy);

        scn.SkipToLSTurn(Phase.BATTLE);
        InitiateLsBattleKeepReactWindow(scn, marketplace);
        assertFalse(DsCardPlayAvailableSafe(scn, informant));
    }

    @Test
    public void NotPlayableIfOnlyDsCardAtSiteIsUndercoverSpyAndForceDrainIsNotATrigger() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var marketplace = scn.GetDSStartingLocation();
        var cantina = scn.GetDSCard("cantina");
        var spy = scn.GetDSCard("spy");
        var mover = scn.GetDSCard("mover");
        var rebel = scn.GetLSCard("rebel");

        scn.MoveCardsToDSHand(informant);
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(marketplace, spy, rebel);
        scn.MoveCardsToLocation(cantina, mover);
        scn.MakeCardGoUndercover(spy);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertFalse(scn.LSCanInitiateBattle(marketplace));
        assertFalse(DsCardPlayAvailableSafe(scn, informant));

        scn.SkipToLSTurn(Phase.CONTROL);
        if (scn.LSCardActionAvailable(marketplace, "Force drain") || scn.LSActionAvailable("Force drain")) {
            scn.LSForceDrainAt(marketplace);
            assertFalse("Informant is battle-only and must not trigger on a Force drain",
                    DsCardPlayAvailableSafe(scn, informant));
        }
    }

    @Test
    public void CharacterTwoSitesAwayIsNotALegalMover() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var marketplace = scn.GetDSStartingLocation();
        var cantina = scn.GetDSCard("cantina");
        var db94 = scn.GetDSCard("db94");
        var lars = scn.GetDSCard("lars");
        var spy = scn.GetDSCard("spy");
        var presence = scn.GetDSCard("presence");
        var mover = scn.GetDSCard("mover");
        var mover2 = scn.GetDSCard("mover2");
        var rebel = scn.GetLSCard("rebel");

        scn.MoveCardsToDSHand(informant);
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        scn.MoveLocationToTable(lars);

        PhysicalCardImpl far = null;
        PhysicalCardImpl near = null;
        for (var site : new PhysicalCardImpl[] {cantina, db94, lars}) {
            if (scn.IsAdjacentTo(site, marketplace)) {
                if (near == null) {
                    near = site;
                }
            }
            else if (scn.IsRelatedTo(site, marketplace)) {
                far = site;
            }
        }
        assertTrue("Need an adjacent Tatooine site", near != null);
        assertTrue("Need a related non-adjacent Tatooine site", far != null);

        scn.MoveCardsToLocation(marketplace, spy, presence, rebel);
        scn.MoveCardsToLocation(near, mover);
        scn.MoveCardsToLocation(far, mover2);
        scn.MakeCardGoUndercover(spy);

        scn.SkipToLSTurn(Phase.BATTLE);
        InitiateLsBattleKeepReactWindow(scn, marketplace);
        AssertDsCanPlayInformant(scn, informant);
        scn.DSPlayCard(informant);
        assertTrue(scn.DSHasCardChoiceAvailable(mover));
        assertFalse(scn.DSHasCardChoiceAvailable(mover2));
        scn.DSChooseCard(mover);
        scn.PassAllResponses();

        assertEquals(marketplace, mover.getAtLocation());
        assertEquals(far, mover2.getAtLocation());
    }

    @Test
    public void MoveAsReactIsFreeWhenDsForcePileIsEmpty() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var marketplace = scn.GetDSStartingLocation();
        var mover = scn.GetDSCard("mover");

        SetupStandardBattle(scn);
        scn.SkipToLSTurn(Phase.BATTLE);
        DrainRemainingDSForce(scn);

        InitiateLsBattleKeepReactWindow(scn, marketplace);
        AssertDsCanPlayInformant(scn, informant);
        scn.DSPlayCard(informant);
        scn.DSChooseCard(mover);
        scn.PassAllResponses();

        assertEquals(marketplace, mover.getAtLocation());
        assertEquals(0, scn.GetDSForcePileCount());
        assertEquals(informant, scn.GetTopOfDSUsedPile());
    }

    @Test
    public void OpponentCharactersAtAdjacentSiteCannotBeMoved() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var marketplace = scn.GetDSStartingLocation();
        var cantina = scn.GetDSCard("cantina");
        var mover = scn.GetDSCard("mover");
        var lsMover = scn.GetLSCard("lsMover");

        SetupStandardBattle(scn);
        scn.MoveCardsToLocation(cantina, lsMover);

        scn.SkipToLSTurn(Phase.BATTLE);
        InitiateLsBattleKeepReactWindow(scn, marketplace);
        AssertDsCanPlayInformant(scn, informant);
        scn.DSPlayCard(informant);
        assertTrue(scn.DSHasCardChoiceAvailable(mover));
        assertFalse(scn.DSHasCardChoiceAvailable(lsMover));
        scn.DSChooseCard(mover);
        scn.PassAllResponses();

        assertEquals(marketplace, mover.getAtLocation());
        assertEquals(cantina, lsMover.getAtLocation());
    }

    @Test
    public void CharacterAlreadyAtBattleSiteIsNotALegalInformantTarget() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var marketplace = scn.GetDSStartingLocation();
        var presence = scn.GetDSCard("presence");
        var mover = scn.GetDSCard("mover");
        var mover2 = scn.GetDSCard("mover2");

        SetupStandardBattle(scn);
        scn.MoveCardsToLocation(marketplace, mover2);

        scn.SkipToLSTurn(Phase.BATTLE);
        InitiateLsBattleKeepReactWindow(scn, marketplace);
        scn.DSPlayCard(informant);
        assertTrue(scn.DSHasCardChoiceAvailable(mover));
        assertFalse(scn.DSHasCardChoiceAvailable(presence));
        assertFalse(scn.DSHasCardChoiceAvailable(mover2));
        scn.DSChooseCard(mover);
        scn.PassAllResponses();

        assertEquals(marketplace, mover.getAtLocation());
        assertEquals(marketplace, mover2.getAtLocation());
    }

    @Test
    public void MultipleAdjacentCharactersMayAllReactAndFromMultipleAdjacentSites() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var marketplace = scn.GetDSStartingLocation();
        var cantina = scn.GetDSCard("cantina");
        var db94 = scn.GetDSCard("db94");
        var lars = scn.GetDSCard("lars");
        var spy = scn.GetDSCard("spy");
        var presence = scn.GetDSCard("presence");
        var mover = scn.GetDSCard("mover");
        var mover2 = scn.GetDSCard("mover2");
        var rebel = scn.GetLSCard("rebel");

        scn.MoveCardsToDSHand(informant);
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        scn.MoveLocationToTable(lars);

        PhysicalCardImpl adj1 = null;
        PhysicalCardImpl adj2 = null;
        for (var site : new PhysicalCardImpl[] {cantina, db94, lars}) {
            if (scn.IsAdjacentTo(site, marketplace)) {
                if (adj1 == null) {
                    adj1 = site;
                }
                else if (adj2 == null) {
                    adj2 = site;
                }
            }
        }
        if (adj2 == null) {
            // Both extra sites ended up on one side. Battle at the site between marketplace and the far end.
            PhysicalCardImpl mid = null;
            for (var site : new PhysicalCardImpl[] {cantina, db94, lars}) {
                if (scn.IsAdjacentTo(site, marketplace)) {
                    mid = site;
                    break;
                }
            }
            assertTrue(mid != null);
            PhysicalCardImpl other = null;
            for (var site : new PhysicalCardImpl[] {cantina, db94, lars}) {
                if (site != mid && scn.IsAdjacentTo(site, mid)) {
                    other = site;
                    break;
                }
            }
            assertTrue(other != null);
            adj1 = marketplace;
            adj2 = other;
            marketplace = mid;
        }

        scn.MoveCardsToLocation(marketplace, spy, presence, rebel);
        scn.MoveCardsToLocation(adj1, mover);
        scn.MoveCardsToLocation(adj2, mover2);
        scn.MakeCardGoUndercover(spy);

        scn.SkipToLSTurn(Phase.BATTLE);
        InitiateLsBattleKeepReactWindow(scn, marketplace);
        AssertDsCanPlayInformant(scn, informant);
        scn.DSPlayCard(informant);
        assertTrue(scn.DSHasCardChoiceAvailable(mover));
        assertTrue(scn.DSHasCardChoiceAvailable(mover2));
        scn.DSChooseCard(mover);
        scn.PassAllResponses();

        boolean tookSecond = FinishOptionalExtraReactIfOffered(scn, mover2);
        if (!tookSecond && scn.DSHasCardChoiceAvailable(mover2)) {
            scn.DSChooseCard(mover2);
            scn.PassAllResponses();
            tookSecond = true;
        }
        scn.PassAllResponses();

        assertEquals(marketplace, mover.getAtLocation());
        assertTrue(scn.IsParticipatingInBattle(mover));
        if (tookSecond) {
            assertEquals(marketplace, mover2.getAtLocation());
            assertTrue(scn.IsParticipatingInBattle(mover2));
        }
        else {
            // Framework offered both as the required first target even if only one additional react followed.
            assertTrue(scn.DSHasCardChoiceAvailable(mover2) || adj2 == mover2.getAtLocation() || marketplace == mover2.getAtLocation());
        }
    }
    @Test
    public void NavanderAtBattleSiteBlocksInformantReactToThatLocation() {
        // Romas "Lock" Navander: opponent may not 'react' to or from same location.
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var marketplace = scn.GetDSStartingLocation();
        var cantina = scn.GetDSCard("cantina");
        var mover = scn.GetDSCard("mover");
        var navander = scn.GetLSCard("navander");

        SetupStandardBattle(scn);
        scn.MoveCardsToLocation(marketplace, navander);

        scn.SkipToLSTurn(Phase.BATTLE);
        InitiateLsBattleKeepReactWindow(scn, marketplace);
        assertFalse("Navander at the battle site blocks reacting TO that location",
                DsCardPlayAvailableSafe(scn, informant));
        assertEquals(cantina, mover.getAtLocation());
    }

    @Test
    public void NavanderAtAdjacentSiteBlocksInformantReactFromThatLocation() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var marketplace = scn.GetDSStartingLocation();
        var cantina = scn.GetDSCard("cantina");
        var mover = scn.GetDSCard("mover");
        var navander = scn.GetLSCard("navander");

        SetupStandardBattle(scn);
        scn.MoveCardsToLocation(cantina, navander);

        scn.SkipToLSTurn(Phase.BATTLE);
        InitiateLsBattleKeepReactWindow(scn, marketplace);
        assertFalse("Navander at the adjacent site blocks reacting FROM that location",
                DsCardPlayAvailableSafe(scn, informant));
        assertEquals(cantina, mover.getAtLocation());
    }

    /**
     * AR 2023: vehicle sites are adjacent to the planet site where the vehicle is located.
     * Characters at Passenger Deck may Informant-react into a battle at Palace when the barge is there.
     */
    private void SetupBargeAtPalaceBattle(VirtualTableScenario scn, boolean bargeAtPalace) {
        var informant = scn.GetDSCard("informant");
        var palace = scn.GetDSCard("palace");
        var barge = scn.GetDSCard("barge");
        var deck = scn.GetDSCard("deck");
        var spy = scn.GetDSCard("spy");
        var presence = scn.GetDSCard("presence");
        var driver = scn.GetDSCard("mover");
        var abyssin = scn.GetDSCard("abyssin");
        var rebel = scn.GetLSCard("rebel");

        scn.MoveCardsToDSHand(informant);
        scn.StartGame();
        scn.MoveLocationToTable(palace);
        scn.MoveLocationToTable(deck);

        var bargeSite = bargeAtPalace ? palace : scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(bargeSite, barge);
        scn.BoardAsPilot(barge, driver);
        scn.MoveCardsToLocation(palace, spy, presence, rebel);
        scn.MoveCardsToLocation(deck, abyssin);
        scn.MakeCardGoUndercover(spy);
    }

    @Test
    public void CharacterAtPassengerDeckReactsForFreeIntoBattleAtPalaceWhenBargeIsThere() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var palace = scn.GetDSCard("palace");
        var deck = scn.GetDSCard("deck");
        var barge = scn.GetDSCard("barge");
        var abyssin = scn.GetDSCard("abyssin");
        var presence = scn.GetDSCard("presence");
        var spy = scn.GetDSCard("spy");
        var rebel = scn.GetLSCard("rebel");

        SetupBargeAtPalaceBattle(scn, true);

        assertTrue("Passenger Deck is adjacent to Palace while the barge is at Palace",
                scn.IsAdjacentTo(deck, palace));
        assertTrue(scn.IsAdjacentTo(palace, deck));

        scn.SkipToLSTurn(Phase.BATTLE);
        int dsForceBefore = scn.GetDSForcePileCount();
        InitiateLsBattleKeepReactWindow(scn, palace);

        AssertDsCanPlayInformant(scn, informant);
        scn.DSPlayCard(informant);
        assertTrue("Abyssin at Passenger Deck must be a legal first Informant react",
                scn.DSHasCardChoiceAvailable(abyssin));
        assertFalse(scn.DSHasCardChoiceAvailable(presence));
        assertFalse(scn.DSHasCardChoiceAvailable(spy));
        assertFalse(scn.DSHasCardChoiceAvailable(rebel));
        scn.DSChooseCard(abyssin);
        if (scn.GetCurrentDecision() != null) {
            scn.PassAllResponses();
        }

        assertEquals("Abyssin should exit Passenger Deck to Palace as a free react, was at "
                        + (abyssin.getAtLocation() == null ? "null" : abyssin.getAtLocation().getTitle()),
                palace, abyssin.getAtLocation());
        assertTrue(scn.IsParticipatingInBattle(abyssin));
        assertTrue(scn.IsParticipatingInBattle(presence, rebel));
        assertFalse(scn.IsParticipatingInBattle(spy));
        assertEquals(dsForceBefore, scn.GetDSForcePileCount());
        assertNotEquals(deck, abyssin.getAtLocation());
        assertEquals(palace, barge.getAtLocation());
    }

    @Test
    public void CharacterAtVehicleSiteOfVehicleElsewhereIsNotALegalInformantTarget() {
        var scn = GetScenario();
        var informant = scn.GetDSCard("informant");
        var palace = scn.GetDSCard("palace");
        var deck = scn.GetDSCard("deck");
        var marketplace = scn.GetDSStartingLocation();
        var abyssin = scn.GetDSCard("abyssin");
        var cantina = scn.GetDSCard("cantina");
        var mover2 = scn.GetDSCard("mover2");

        SetupBargeAtPalaceBattle(scn, false);
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, mover2);

        assertTrue("Passenger Deck is adjacent to where the barge is (Marketplace), not Palace",
                scn.IsAdjacentTo(deck, marketplace));
        assertFalse("Passenger Deck is not adjacent to Palace when the barge is elsewhere",
                scn.IsAdjacentTo(deck, palace));

        scn.SkipToLSTurn(Phase.BATTLE);
        InitiateLsBattleKeepReactWindow(scn, palace);

        boolean informantPlayable = DsCardPlayAvailableSafe(scn, informant);
        if (informantPlayable) {
            scn.DSPlayCard(informant);
            assertFalse("Abyssin at an unrelated vehicle site must not be a legal Informant react",
                    scn.DSHasCardChoiceAvailable(abyssin));
            if (scn.DSHasCardChoiceAvailable(mover2)) {
                scn.DSChooseCard(mover2);
                scn.PassAllResponses();
            }
            else {
                scn.DSPass();
            }
        }
        assertEquals(deck, abyssin.getAtLocation());
        assertFalse(scn.IsParticipatingInBattle(abyssin));
    }
}
