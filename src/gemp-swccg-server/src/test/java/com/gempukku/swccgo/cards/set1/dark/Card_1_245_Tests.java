package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Card_1_245_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("ls_cantina","1_128"); //tatooine cantina (planet site)
                    put("ls_yavin_db","1_136"); //yavin docking bay (planet site)
                    put("ls_cc_db","5_083"); //cloud city docking bay (non-planet site)
                    put("ls_dag","4_085"); //dagobah: bog clearing (planet site)
                    put("homeone", "9_074");
                    put("liberty", "9_076");
                    put("barrier","1_105"); //rebel barrier
                    put("badFeeling","4_052"); //i have a bad feeling about this
				}},
				new HashMap<>()
				{{
                    put("evac", "1_245"); //evacuate?
                    put("pilot", "1_180"); // imperial pilot
                    put("guard", "1_181"); // imperial guard (may not move)
                    put("tarkin", "1_179"); // grand moff tarkin
                    put("ds_tat_db","1_291"); //tatooine docking bay (planet site)
                    put("executor","4_167"); //(capital ship)
                    put("ds_exec_db","7_282"); //executor: docking bay
                    put("vcsd","2_155"); //victory class star destroyer (capital ship)
                    put("tiescout","1_305"); //tie scout (starship)
                    put("ronto","7_316"); //(creature vehicle)
                    put("barrier","1_249"); //imperial barrier
                    put("cruiser","7_304"); //jabba's space cruiser (req. alien pilots)
                    put("bosskInBus","7_301"); //bossk in hound's tooth
                    put("binder","202_013");
                    put("pilot1", "1_180"); // imperial pilot
                    put("pilot2", "1_180"); // imperial pilot
                    put("pilot3", "1_180"); // imperial pilot
                    put("alienpilot1", "2_104"); // rodian
                    put("alienpilot2", "2_104"); // rodian
                    put("alienpilot3", "2_104"); // rodian
                    put("astromech", "1_192"); // r1-g4
                    put("lostartoo", "1_218"); // i've lost artoo
                }},
				10,
				10,
				StartingSetup.DefaultLSSpaceSystem, //parsec 2
				StartingSetup.DefaultDSSpaceSystem, //parsec 5
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void EvacuateStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Evacuate?
		 * Uniqueness: Unrestricted
		 * Side: Dark
		 * Type: Interrupt
		 * Subtype: Used
		 * Destiny: 6
		 * Icons: Interrupt
		 * Game Text: If your capital starship is about to be lost, unless Tarkin aboard, relocate your characters
         *      aboard to any one planet site or to one of your capital starships.
		 * Lore: Escape pods are on many starships allowing those in peril to flee, an act considered cowardly
         *      by Imperial officers. 'We've analyzed their attack, sir, and there is a danger.'
		 * Set: Premiere
		 * Rarity: U2
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("evac").getBlueprint();

		assertEquals("Evacuate?", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
		assertEquals(CardSubtype.USED, card.getCardSubtype());
		assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.PREMIERE,card.getExpansionSet());
        assertEquals(Rarity.U2,card.getRarity());

	}

	@Test
	public void EvacuateMayBePlayedWhenYourCapitalShipAboutToBeLost() {
        //test1: evacuate can be played when your capital ship is about to be lost
        //  with basic conditions met: your character on board that can relocate
        //  planet site that can be relocated to
        //test2: character aboard relocates to target planet site
        //test3: evacuate goes to used pile
		var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot = scn.GetDSCard("pilot");

		var system = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,vcsd,homeone);
        scn.BoardAsPilot(vcsd,pilot);

		scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        scn.DSChooseCard(ls_cantina);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_cantina,pilot)); //test2
        assertSame(Zone.TOP_OF_USED_PILE,evac.getZone()); //test3
	}

    @Test
    public void EvacuateMayNotBePlayedIfTarkinAboard() {
        //test1: evacuate can be played if tarkin is aboard the ship about to be lost
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot = scn.GetDSCard("pilot");
        var tarkin = scn.GetDSCard("tarkin");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,vcsd,homeone);
        scn.BoardAsPilot(vcsd,pilot);
        scn.BoardAsPassenger(vcsd,tarkin);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());
        assertTrue(tarkin.isPassengerOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertFalse(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.LSAnyDecisionsAvailable()); //test1 (past window to play Evacuate?)
    }

    @Test
    public void EvacuateMayNotBePlayedWhenYourStarfighterAboutToBeLost() {
        //test1: evacuate cannot be played when your non-capital ship is about to be lost
        //  with basic conditions met: your character on board that can relocate
        //  planet site that can be relocated to
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var pilot = scn.GetDSCard("pilot");
        var tiescout = scn.GetDSCard("tiescout");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,homeone,tiescout);
        scn.BoardAsPilot(tiescout,pilot);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(tiescout);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertFalse(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.LSAnyDecisionsAvailable()); //test1 (past window to play Evacuate?)
    }

    @Test
    public void EvacuateMayNotBePlayedIfNoCharacterAboard() {
        //test1: evacuate cannot be played when your capital ship is about to be lost
        //  if no characters are aboard, even if there is an eligible planet site and capital ship
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var cruiser = scn.GetDSCard("cruiser");
        var executor = scn.GetDSCard("executor");

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,homeone,vcsd,cruiser);

        scn.MoveCardsToLocation(system2,executor);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        assertTrue(scn.AwaitingDSBattleDamagePayment());
        scn.DSChooseCard(vcsd);

        assertFalse(scn.DSAnyDecisionsAvailable()); //test1: LS decision means past window to play Evacuate
        assertTrue(scn.LSDecisionAvailable("FORFEITED_TO_LOST_PILE_FROM_TABLE")); //optional response
    }

    @Test
    public void EvacuateMayNotBePlayedIfNoCharacterAboardCanRelocate() {
        //test1: evacuate cannot be played when your capital ship is about to be lost
        //  if characters are aboard but none can relocate (escort cannot relocate)
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");
        var rebelTrooper = scn.GetLSFiller(1);

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var trooper = scn.GetDSFiller(1);

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPassenger(vcsd,trooper);
        scn.CaptureCardWith(trooper,rebelTrooper);

        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(trooper.isPassengerOf());
        assertTrue(rebelTrooper.isCaptive());
        assertEquals(trooper,rebelTrooper.getEscort());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertFalse(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.LSAnyDecisionsAvailable()); //test1: (past window to play Evacuate?)
    }

    @Test
    public void EvacuateMayBePlayedIfAllActiveCharactersAboardCannotRelocateToSite() {
        //test1: evacuate can be played when your capital ship is about to be lost
        //  and not all characters aboard can relocate to the target planet site
        //test2: does not relocate character aboard that was unable to move
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot = scn.GetDSCard("pilot");
        var guard = scn.GetDSCard("guard"); //may not move

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPilot(vcsd,pilot);
        scn.BoardAsPassenger(vcsd,guard);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());
        assertTrue(guard.isPassengerOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        scn.DSChooseCard(ls_cantina);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_cantina,pilot));
        assertFalse(scn.CardsAtLocation(ls_cantina,guard)); //test2: cannot move, so could not relocate and stays on ship
        assertSame(Zone.TOP_OF_USED_PILE,evac.getZone());
        assertTrue(scn.DSDecisionAvailable("Choose card to put on Lost Pile")); //guard and vcsd simultaneous loss
    }

    @Test
    public void EvacuateCannotTargetNonPlanetSite() {
        //test1: evacuate cannot be played when your capital ship is about to be lost
        //  and no planet site to relocate to
        var scn = GetScenario();

        var homeone = scn.GetLSCard("homeone");
        var ls_cc_db = scn.GetLSCard("ls_cc_db");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot = scn.GetDSCard("pilot");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(evac);

        scn.MoveLocationToTable(ls_cc_db);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPilot(vcsd,pilot);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertFalse(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.LSAnyDecisionsAvailable()); //test1 (past window to play Evacuate?)
    }

    @Test
    public void EvacuateCannotTargetDagobahPlanetSite() {
        //test1: evacuate cannot be played when your capital ship is about to be lost
        //  and no non-dagobah planet site is on table
        var scn = GetScenario();

        var ls_dag = scn.GetLSCard("ls_dag");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot = scn.GetDSCard("pilot");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_dag);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPilot(vcsd,pilot);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertFalse(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.LSAnyDecisionsAvailable()); //test1 (past window to play Evacuate?)
    }

    @Test
    public void EvacuateCanTargetYourLandedShipSite() {
        //test1: evacuate is playable if lost while landed at a site
        //test2: evacuate can relocate to the same site that your ship is currently landed at
        //test3: relocate to same site succeeds
        var scn = GetScenario();

        var ls_yavin_db = scn.GetLSCard("ls_yavin_db");
        var ls_cantina = scn.GetLSCard("ls_cantina");
        var rebelTrooper = scn.GetLSFiller(1);

        var evac = scn.GetDSCard("evac");
        var cruiser = scn.GetDSCard("cruiser");
        var alienpilot1 = scn.GetDSCard("alienpilot1");

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_yavin_db);
        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(ls_yavin_db,cruiser,rebelTrooper);
        scn.BoardAsPilot(cruiser,alienpilot1);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(alienpilot1.isPilotOf());

        scn.DSInitiateBattle(ls_yavin_db);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(cruiser);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site or ship"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_yavin_db)); //test2
        scn.DSChooseCard(ls_yavin_db);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_yavin_db,alienpilot1)); //test3
        assertFalse(scn.IsAttachedTo(cruiser,alienpilot1));
    }

    @Test
    public void EvacuateDoesNotRelocateCharactersOnVehiclesAboard() {
        //test1: does not relocate vehicles aboard
        //test2: does not relocate characters on vehicles aboard
        var scn = GetScenario();

        var ls_yavin_db = scn.GetLSCard("ls_yavin_db");
        var homeone = scn.GetLSCard("homeone"); //capacity to hold 1 vehicle

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot = scn.GetDSCard("pilot");
        var ronto = scn.GetDSCard("ronto");
        var trooper = scn.GetDSFiller(1);

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_yavin_db);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPilot(vcsd,pilot);
        scn.BoardAsVehicle(vcsd,ronto);
        scn.BoardAsPassenger(ronto,trooper);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());
        assertTrue(trooper.isPassengerOf());
        assertTrue(ronto.isInCargoHoldAsVehicle());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_yavin_db));
        scn.DSChooseCard(ls_yavin_db);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_yavin_db,pilot));
        assertFalse(scn.CardsAtLocation(ls_yavin_db,ronto)); //test1
        assertFalse(scn.CardsAtLocation(ls_yavin_db,trooper)); //test2
    }

    @Test
    public void EvacuateDoesNotRelocateInactiveCharactersToSite() {
        //test1: will not relocate characters that are inactive (in this, excluded from battle)
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");
        var barrier = scn.GetLSCard("barrier");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot = scn.GetDSCard("pilot");
        var trooper = scn.GetDSFiller(1);

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac, trooper);
        scn.MoveCardsToLSHand(barrier);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPilot(vcsd,pilot);

        scn.LSActivateForceCheat(1);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(trooper);
        scn.DSChooseCard(vcsd);

        scn.LSPass(); //Use 1 Force - Optional responses
        scn.DSPass();

        //Stormtrooper just deployed - Optional responses
        scn.LSPlayCard(barrier);

        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());
        assertTrue(trooper.isPassengerOf());

        scn.DSInitiateBattle(system);
        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_EXCLUDED"));
        scn.DSPass();
        scn.LSPass();

        scn.DSPass(); //EXCLUDED_FROM_BATTLE - Optional responses
        scn.LSPass();

        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        scn.DSChooseCard(ls_cantina);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_cantina,pilot));
        assertFalse(scn.CardsAtLocation(ls_cantina,trooper));//test1 (excluded and could not be seen to relocate)
        assertSame(Zone.TOP_OF_USED_PILE,evac.getZone());
    }

    @Test
    public void EvacuateDoesNotRelocateEscortCharactersToSite() {
        //test1: will not relocate characters that are escorting a captive
        //test2: will not relocate captives that are being escorted
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");
        var rebelTrooper = scn.GetLSFiller(1);

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot = scn.GetDSCard("pilot");
        var trooper = scn.GetDSFiller(1);

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPilot(vcsd,pilot);
        scn.BoardAsPassenger(vcsd,trooper);
        scn.CaptureCardWith(trooper,rebelTrooper);

        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());
        assertTrue(trooper.isPassengerOf());
        assertTrue(rebelTrooper.isCaptive());
        assertEquals(trooper,rebelTrooper.getEscort());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        scn.DSChooseCard(ls_cantina);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_cantina,pilot));
        assertFalse(scn.CardsAtLocation(ls_cantina,trooper)); //test1
        assertFalse(scn.CardsAtLocation(ls_cantina,rebelTrooper)); //test2
    }

    @Test
    public void EvacuateRelocatesCharactersAtRelatedStarshipSites() {
        //test1: evacuate relocates characters that are aboard due to being at a related starship site
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");
        var liberty = scn.GetLSCard("liberty");

        var evac = scn.GetDSCard("evac");
        var executor = scn.GetDSCard("executor");
        var pilot = scn.GetDSCard("pilot");
        var ds_exec_db = scn.GetDSCard("ds_exec_db");
        var trooper = scn.GetDSFiller(1);

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cantina);
        scn.MoveLocationToTable(ds_exec_db);

        scn.MoveCardsToLocation(system,executor,homeone,liberty);
        scn.BoardAsPilot(executor,pilot);

        scn.MoveCardsToLocation(ds_exec_db,trooper);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(executor);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit Medium Transport - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        scn.DSChooseCard(ls_cantina);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_cantina,pilot));
        assertTrue(scn.CardsAtLocation(ls_cantina,trooper)); //test1
        assertSame(Zone.TOP_OF_USED_PILE,evac.getZone());
    }

    @Test
    public void EvacuateCanRelocateToAShip() {
        //test1: evacuate can relocate characters to a capital ship with sufficient capacity
        //test2: an eligible pilot that is currently a passenger can relocate to available pilot capacity
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot = scn.GetDSCard("pilot");
        var executor = scn.GetDSCard("executor");

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,vcsd,homeone);
        scn.BoardAsPassenger(vcsd,pilot);

        scn.MoveCardsToLocation(system2,executor);

        scn.SkipToPhase(Phase.BATTLE);
        assertFalse(pilot.isPilotOf());
        assertTrue(pilot.isPassengerOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site or ship"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        assertTrue(scn.DSHasCardChoiceAvailable(executor));
        scn.DSChooseCard(executor);

        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("Choose characters to relocate as pilots"));
        assertTrue(scn.DSHasCardChoicesAvailable(pilot));
        scn.DSChooseCards(pilot);

        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(executor,pilot)); //test1
        assertTrue(pilot.isPilotOf()); //test2
    }

    @Test
    public void EvacuateRelocatingToShipCanMakePilotsPilotOrPassenger() {
        //test1: evacuate can relocate pilot characters (piloting) as pilot
        //test2: evacuate can relocate pilot characters (piloting) as passenger
        //test3: evacuate can relocate pilot characters (passenger) as pilot
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot1 = scn.GetDSCard("pilot1");
        var pilot2 = scn.GetDSCard("pilot2");
        var pilot3 = scn.GetDSCard("pilot3");
        var executor = scn.GetDSCard("executor");

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,vcsd,homeone);
        scn.BoardAsPilot(vcsd,pilot1);
        scn.BoardAsPilot(vcsd,pilot2);
        scn.BoardAsPassenger(vcsd,pilot3);

        scn.MoveCardsToLocation(system2,executor);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot1.isPilotOf());
        assertTrue(pilot2.isPilotOf());
        assertFalse(pilot3.isPilotOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site or ship"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        assertTrue(scn.DSHasCardChoiceAvailable(executor));
        scn.DSChooseCard(executor);

        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("Choose characters to relocate as pilots"));
        assertTrue(scn.DSHasCardChoicesAvailable(pilot1,pilot2,pilot3));
        scn.DSChooseCards(pilot1,pilot3);

        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(executor,pilot1));
        assertTrue(scn.IsAttachedTo(executor,pilot2));
        assertTrue(scn.IsAttachedTo(executor,pilot3));
        assertTrue(pilot1.isPilotOf()); //test1
        assertFalse(pilot2.isPilotOf()); //test2
        assertTrue(pilot3.isPilotOf()); //test3
    }

    @Test
    public void EvacuateRelocatingToShipObeysPilotRequirements() {
        //test1: relocating pilots to jabba's space cruiser follows alien pilot requirement
        //test2: maximum pilot capacity is obeyed when relocating

        var scn = GetScenario();

        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot = scn.GetDSCard("pilot");
        var alienpilot1 = scn.GetDSCard("alienpilot1");
        var alienpilot2 = scn.GetDSCard("alienpilot2");
        var alienpilot3 = scn.GetDSCard("alienpilot3");
        var cruiser = scn.GetDSCard("cruiser");

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveCardsToLocation(system,vcsd,homeone);
        scn.BoardAsPassenger(vcsd,pilot,alienpilot1,alienpilot2);

        scn.MoveCardsToLocation(system2,cruiser);
        scn.BoardAsPilot(cruiser,alienpilot3);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site or ship"));
        assertTrue(scn.DSHasCardChoiceAvailable(cruiser));
        scn.DSChooseCard(cruiser);

        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("Choose characters to relocate as pilots"));
        assertTrue(scn.DSHasCardChoicesAvailable(alienpilot1,alienpilot2));
        assertFalse(scn.DSHasCardChoiceAvailable(pilot)); //test1: not alien, so unable to pilot cruiser
        assertEquals(0,scn.DSGetChoiceMin()); //enough room (6 passenger capacity) that no eligible pilots (2) have to be pilots
        assertEquals(1,scn.DSGetChoiceMax()); //test2: only 1 available pilot slot
        scn.DSChooseCards(alienpilot1);

        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(cruiser,pilot));
        assertTrue(scn.IsAttachedTo(cruiser,alienpilot1));
        assertTrue(scn.IsAttachedTo(cruiser,alienpilot2));
        assertTrue(scn.IsAttachedTo(cruiser,alienpilot3));
        assertFalse(pilot.isPilotOf());
        assertTrue(alienpilot1.isPilotOf());
        assertFalse(alienpilot2.isPilotOf());
        assertTrue(alienpilot3.isPilotOf());
    }

    @Test
    public void EvacuateRelocatingToShipForcesPilotSelectionToFitAll() {
        //test1: relocating forces user to select characters to pilot (if needed) to fit all characters

        var scn = GetScenario();

        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot = scn.GetDSCard("pilot");
        var alienpilot1 = scn.GetDSCard("alienpilot1");
        var alienpilot2 = scn.GetDSCard("alienpilot2");
        var passenger1 = scn.GetDSFiller(1);
        var passenger2 = scn.GetDSFiller(2);
        var passenger3 = scn.GetDSFiller(3);
        var passenger4 = scn.GetDSFiller(4);
        var cruiser = scn.GetDSCard("cruiser");

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveCardsToLocation(system,vcsd,homeone);
        scn.BoardAsPassenger(vcsd,pilot,alienpilot1,alienpilot2);

        scn.MoveCardsToLocation(system2,cruiser);
        scn.BoardAsPassenger(cruiser,passenger1,passenger2,passenger3,passenger4);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site or ship"));
        assertTrue(scn.DSHasCardChoiceAvailable(cruiser));
        scn.DSChooseCard(cruiser);

        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("Choose characters to relocate as pilots"));
        assertTrue(scn.DSHasCardChoicesAvailable(alienpilot1,alienpilot2));
        assertFalse(scn.DSHasCardChoiceAvailable(pilot)); //test1: not alien, so unable to pilot cruiser
/// should see min choice of 1 here and be unable to pass without choosing 1?
        //assertEquals(1,scn.DSGetChoiceMin()); //test1: 1 required to pilot since not enough room to hold all 3 relocated characters as passengers
        assertEquals(2,scn.DSGetChoiceMax()); //test2: 2 available pilot slots
        scn.DSChooseCards(alienpilot1);

        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(cruiser,pilot));
        assertTrue(scn.IsAttachedTo(cruiser,alienpilot1));
        assertTrue(scn.IsAttachedTo(cruiser,alienpilot2));
        assertFalse(pilot.isPilotOf());
        assertTrue(alienpilot1.isPilotOf());
        assertFalse(alienpilot2.isPilotOf());
    }

    @Test
    public void EvacuateCannotRelocateToShipIfCannotFitAll() {
        //test1: if all characters are unable to relocate to a ship, it cannot be targeted

        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var astromech = scn.GetDSCard("astromech");
        var passenger1 = scn.GetDSFiller(1);
        var passenger2 = scn.GetDSFiller(2);
        var passenger3 = scn.GetDSFiller(3);
        var passenger4 = scn.GetDSFiller(4);
        var passenger5 = scn.GetDSFiller(5);
        var passenger6 = scn.GetDSFiller(6);
        var cruiser = scn.GetDSCard("cruiser");

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToDSHand(evac);

        scn.MoveCardsToLocation(system,vcsd,homeone);
        scn.BoardAsPassenger(vcsd,astromech);

        scn.MoveCardsToLocation(system2,cruiser);
        scn.BoardAsPassenger(cruiser,passenger1,passenger2,passenger3,passenger4,passenger5,passenger6);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site or ship"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        assertFalse(scn.DSHasCardChoiceAvailable(cruiser)); //passenger capacity full and astromech cannot pilot
    }

    @Test
    public void EvacuateCannotRelocateToShipIfCannotFitAll2() {
        //test1: if all characters are unable to relocate to a ship, it cannot be targeted
        //  test with Binder which has shared "pilots or passengers" capacity

        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var pilot1 = scn.GetDSCard("pilot1");
        var pilot2 = scn.GetDSCard("pilot2");
        var pilot3 = scn.GetDSCard("pilot3");
        var passenger1 = scn.GetDSFiller(1);
        var passenger2 = scn.GetDSFiller(2);
        var passenger3 = scn.GetDSFiller(3);
        var passenger4 = scn.GetDSFiller(4);
        var binder = scn.GetDSCard("binder");

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToDSHand(evac);

        scn.MoveCardsToLocation(system,vcsd,homeone);
        scn.BoardAsPassenger(vcsd,passenger1,passenger2,passenger3,passenger4);
        scn.BoardAsPilot(vcsd,pilot1,pilot2,pilot3);
        scn.BoardAsPilot(vcsd);
        scn.BoardAsPilot(vcsd);

        scn.MoveCardsToLocation(system2,binder);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        assertEquals(8,scn.GetDSTotalPower()); //6+2 only one pilot adds to power due to cumulative rule
        assertEquals(9,scn.GetLSTotalPower());
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site or ship"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        assertFalse(scn.DSHasCardChoiceAvailable(binder)); //6 (shared) pilot or passenger slots, but 7 characters
    }

    @Test
    public void EvacuateSupportsAstromechCapacity() {
        //test1: dedicated astromech capacity is handled correctly when deciding if characters can relocate

        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var passenger1 = scn.GetDSFiller(1);
        var passenger2 = scn.GetDSFiller(2);
        var passenger3 = scn.GetDSFiller(3);
        var passenger4 = scn.GetDSFiller(4);
        var passenger5 = scn.GetDSFiller(5);
        var passenger6 = scn.GetDSFiller(6);
        var cruiser = scn.GetDSCard("cruiser");
        var lostartoo = scn.GetDSCard("lostartoo");
        var astromech = scn.GetDSCard("astromech");

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToDSHand(evac,lostartoo);

        scn.MoveCardsToLocation(system,vcsd,homeone);
        scn.BoardAsPassenger(vcsd,astromech);

        scn.MoveCardsToLocation(system2,cruiser);
        scn.BoardAsPassenger(cruiser,passenger1,passenger2,passenger3,passenger4,passenger5,passenger6);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.PrepareDSDestiny(7);
        scn.DSPlayCard(lostartoo);
        scn.DSChooseCard(cruiser);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(cruiser,lostartoo));

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site or ship"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        assertTrue(scn.DSHasCardChoiceAvailable(cruiser)); //passenger capacity full, but 1 capacity for astromech (via i've lost artoo)
        scn.DSChooseCard(cruiser);

        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(cruiser,astromech));
    }

    @Test
    public void EvacuateChecksCaptivesForPassengerCapacity() {
        //test1: evacuate cannot be played if insufficient capacity due to captives consuming passenger slots
        //  capacity 6 with 5 passengers, should fail when checking if enough room for escort + captive
        //  even though escort + captive would fail to relocate later!
        var scn = GetScenario();


        var homeone = scn.GetLSCard("homeone");
        var rebelTrooper = scn.GetLSFiller(1);

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var cruiser = scn.GetDSCard("cruiser");
        var alienpilot1 = scn.GetDSCard("alienpilot1");
        var trooper = scn.GetDSFiller(1);
        var passenger2 = scn.GetDSFiller(2);
        var passenger3 = scn.GetDSFiller(3);
        var passenger4 = scn.GetDSFiller(4);
        var passenger5 = scn.GetDSFiller(5);
        var passenger6 = scn.GetDSFiller(6);

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveCardsToLocation(system,vcsd,homeone);
        scn.BoardAsPassenger(vcsd,alienpilot1,trooper);
        scn.CaptureCardWith(trooper,rebelTrooper);

        scn.MoveCardsToLocation(system2,cruiser);
        scn.BoardAsPassenger(cruiser,passenger2,passenger3,passenger4,passenger5,passenger6);

        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(trooper.isPassengerOf());
        assertTrue(rebelTrooper.isCaptive());
        assertEquals(trooper,rebelTrooper.getEscort());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertFalse(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.LSAnyDecisionsAvailable()); //test1: past window of opportunity to play Evacuate?
    }

    @Test
    public void EvacuateCanRelocateLandedShipToLandedShip() {
        //test1: evacuate can relocate from a landed ship to another landed ship at the same site
        var scn = GetScenario();

        var ls_cc_db = scn.GetLSCard("ls_cc_db");
        var rebelTrooper = scn.GetLSFiller(1);

        var evac = scn.GetDSCard("evac");
        var cruiser = scn.GetDSCard("cruiser");
        var bosskInBus = scn.GetDSCard("bosskInBus");
        var alienpilot1 = scn.GetDSCard("alienpilot1");

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cc_db);

        scn.MoveCardsToLocation(ls_cc_db,cruiser,bosskInBus,rebelTrooper);
        scn.BoardAsPilot(cruiser,alienpilot1);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(alienpilot1.isPilotOf());

        scn.DSInitiateBattle(ls_cc_db);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(cruiser);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site or ship"));
        assertFalse(scn.DSHasCardChoiceAvailable(ls_cc_db)); //(not planet site)
        assertTrue(scn.DSHasCardChoiceAvailable(bosskInBus));
        scn.DSChooseCard(bosskInBus);

        scn.PassAllResponses();

        assertFalse(scn.CardsAtLocation(ls_cc_db,alienpilot1));
        assertFalse(scn.IsAttachedTo(cruiser,alienpilot1));
        assertTrue(scn.IsAttachedTo(bosskInBus,alienpilot1)); //test1
    }

    @Test
    public void EvacuateCanRelocateShipToShipAtSameSystem() {
        //test1: evacuate can relocate from a ship at a system to another ship the same system
        var scn = GetScenario();

        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var cruiser = scn.GetDSCard("cruiser");
        var vcsd = scn.GetDSCard("vcsd");
        var alienpilot1 = scn.GetDSCard("alienpilot1");

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        var system = scn.GetDSStartingLocation();

        scn.MoveCardsToLocation(system,cruiser,vcsd,homeone);
        scn.BoardAsPassenger(cruiser,alienpilot1);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(cruiser);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site or ship"));
        assertTrue(scn.DSHasCardChoiceAvailable(vcsd));
        scn.DSChooseCard(vcsd);

        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose characters")); //to relocate as pilots

        scn.DSPass();
        scn.PassAllResponses();

        assertFalse(scn.IsAttachedTo(cruiser,alienpilot1));
        assertTrue(scn.IsAttachedTo(vcsd,alienpilot1)); //test1
    }

    @Test
    public void EvacuateMayBePlayedIfAllActiveCharactersAboardCannotRelocateToShip() {
        //test1: evacuate can be played when your capital ship is about to be lost
        //  and not all characters aboard can relocate to the target ship
        //test2: does not relocate character aboard that was unable to move
        var scn = GetScenario();

        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var executor = scn.GetDSCard("executor");
        var trooper = scn.GetDSFiller(1);
        var guard = scn.GetDSCard("guard"); //may not move

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPassenger(vcsd,trooper);
        scn.BoardAsPassenger(vcsd,guard);

        scn.MoveCardsToLocation(system2,executor);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(trooper.isPassengerOf());
        assertTrue(guard.isPassengerOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site"));
        assertTrue(scn.DSHasCardChoiceAvailable(executor));
        scn.DSChooseCard(executor);

        scn.PassAllResponses();

        assertTrue(trooper.isPassengerOf());
        assertTrue(scn.IsAttachedTo(executor,trooper));

        assertFalse(guard.isPassengerOf()); //test2
        assertFalse(scn.IsAttachedTo(executor,guard));

        assertTrue(scn.DSDecisionAvailable("Choose card to put on Lost Pile")); //guard and vcsd simultaneous loss
    }

    @Test
    public void EvacuateDoesNotRelocateInactiveCharactersToShip() {
        //test1: will not relocate characters that are inactive (in this, excluded from battle)
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");
        var barrier = scn.GetLSCard("barrier");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var executor = scn.GetDSCard("executor");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac, trooper2);
        scn.MoveCardsToLSHand(barrier);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPassenger(vcsd,trooper1);

        scn.MoveCardsToLocation(system2,executor);

        scn.LSActivateForceCheat(1);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(trooper2);
        scn.DSChooseCard(vcsd);

        scn.LSPass(); //Use 1 Force - Optional responses
        scn.DSPass();

        //Stormtrooper just deployed - Optional responses
        scn.LSPlayCard(barrier);

        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(trooper1.isPassengerOf());
        assertTrue(trooper2.isPassengerOf());

        scn.DSInitiateBattle(system);
        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_EXCLUDED"));
        scn.DSPass();
        scn.LSPass();

        scn.DSPass(); //EXCLUDED_FROM_BATTLE - Optional responses
        scn.LSPass();

        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        assertTrue(scn.DSHasCardChoiceAvailable(executor));
        scn.DSChooseCard(executor);

        scn.PassAllResponses();

        assertTrue(trooper1.isPassengerOf());
        assertTrue(scn.IsAttachedTo(executor,trooper1));

        assertFalse(trooper2.isPassengerOf());
        assertFalse(scn.IsAttachedTo(executor,trooper2));//test1 (excluded and could not be seen to relocate)

        assertSame(Zone.TOP_OF_USED_PILE,evac.getZone());
    }

    @Test
    public void EvacuateDoesNotRelocateEscortCharactersToShip() {
        //test1: will not relocate characters that are escorting a captive
        //test2: will not relocate captives that are being escorted
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");
        var rebelTrooper = scn.GetLSFiller(1);

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var executor = scn.GetDSCard("executor");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPassenger(vcsd,trooper1);
        scn.BoardAsPassenger(vcsd,trooper2);
        scn.CaptureCardWith(trooper2,rebelTrooper);

        scn.MoveCardsToLocation(system2,executor);

        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(trooper1.isPassengerOf());
        assertTrue(trooper2.isPassengerOf());
        assertTrue(rebelTrooper.isCaptive());
        assertEquals(trooper2,rebelTrooper.getEscort());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        assertTrue(scn.DSHasCardChoiceAvailable(executor));
        scn.DSChooseCard(executor);

        scn.PassAllResponses();

        assertTrue(trooper1.isPassengerOf());
        assertTrue(scn.IsAttachedTo(executor,trooper1));

        assertFalse(trooper2.isPassengerOf());
        assertFalse(scn.IsAttachedTo(executor,trooper2));//test1

        assertFalse(scn.IsAttachedTo(executor,rebelTrooper)); //test2
        assertFalse(scn.IsAttachedTo(trooper2,rebelTrooper));
    }

    @Test
    public void EvacuateCannotTargetShipAtDagobah() {
        //test1: evacuate cannot be played when your capital ship is about to be lost
        //  and an eligible capital ship is at dagobah
        var scn = GetScenario();

        var ls_dag = scn.GetLSCard("ls_dag");
        var homeone = scn.GetLSCard("homeone");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var bosskInBus = scn.GetDSCard("bosskInBus");
        var pilot = scn.GetDSCard("pilot");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);

        scn.MoveLocationToTable(ls_dag);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPilot(vcsd,pilot);

        scn.MoveCardsToLocation(ls_dag,bosskInBus); //landed capital starship

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertFalse(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.LSAnyDecisionsAvailable()); //test1 (past window to play Evacuate?)
    }

    @Test
    public void EvacuateDestinationMayBeRetargetedByIHABFAT_1() {
        //test1: evacuate planet site selected can be retargeted to a new, valid planet site
        //test2: evacuate cannot retarget to DS ship with capacity if original site was LS
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");
        var ls_yavin_db = scn.GetLSCard("ls_yavin_db");
        var ls_cc_db = scn.GetLSCard("ls_cc_db");
        var ls_dag = scn.GetLSCard("ls_dag");
        var badFeeling = scn.GetLSCard("badFeeling");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var executor = scn.GetDSCard("executor");
        var pilot = scn.GetDSCard("pilot");
        var ds_tat_db = scn.GetDSCard("ds_tat_db");

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);
        scn.MoveCardsToLSHand(badFeeling);

        scn.MoveLocationToTable(ls_cantina);
        scn.MoveLocationToTable(ls_yavin_db);
        scn.MoveLocationToTable(ls_cc_db);
        scn.MoveLocationToTable(ls_dag);
        scn.MoveLocationToTable(ds_tat_db);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPilot(vcsd,pilot);

        scn.MoveCardsToLocation(system2,executor);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());

        scn.LSActivateForceCheat(4); //enough to battle and play IHABFAT

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site"));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina));
        scn.DSChooseCard(ls_cantina);

        assertTrue(scn.LSDecisionAvailable("Optional responses"));
        assertTrue(scn.LSCardPlayAvailable(badFeeling));
        scn.LSPlayCard(badFeeling);

        //Choose card (or card in group) to re-target from, or click 'Done' to cancel
        assertTrue(scn.LSHasCardChoiceAvailable(ls_cantina)); //original selection
        assertFalse(scn.LSHasCardChoicesAvailable(ls_yavin_db,ls_cc_db,ls_dag,ds_tat_db));
        scn.LSChooseCard(ls_cantina);

        //Choose card (or card in group) to re-target from, or click 'Done' to cancel
        assertFalse(scn.LSHasCardChoiceAvailable(ls_cantina)); //must retarget somewhere different
        assertFalse(scn.LSHasCardChoiceAvailable(ls_dag)); //not planet site
        assertTrue(scn.LSHasCardChoiceAvailable(ls_yavin_db)); //LS site that meets criteria
        assertFalse(scn.LSHasCardChoiceAvailable(ds_tat_db)); //can't retarget to DS site, since original was LS site and must be "on the same side of the force"
        assertFalse(scn.LSHasCardChoiceAvailable(ls_dag)); //dagobah rules
        assertFalse(scn.LSHasCardChoiceAvailable(executor)); //can't retarget to DS ship, since original was LS site and must be "on the same side of the force"
        scn.LSChooseCard(ls_yavin_db);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_yavin_db,pilot)); //test1
        assertSame(Zone.TOP_OF_USED_PILE,evac.getZone());
    }

    @Test
    public void EvacuateDestinationMayBeRetargetedByIHABFAT_2() {
        //test1: evacuate (DS) planet site selected can be retargeted to a (DS) eligible ship
        var scn = GetScenario();

        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");
        var badFeeling = scn.GetLSCard("badFeeling");

        var evac = scn.GetDSCard("evac");
        var vcsd = scn.GetDSCard("vcsd");
        var executor = scn.GetDSCard("executor");
        var pilot = scn.GetDSCard("pilot");
        var ds_tat_db = scn.GetDSCard("ds_tat_db");

        var system = scn.GetDSStartingLocation();
        var system2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(evac);
        scn.MoveCardsToLSHand(badFeeling);

        scn.MoveLocationToTable(ls_cantina);
        scn.MoveLocationToTable(ds_tat_db);

        scn.MoveCardsToLocation(system,homeone,vcsd);
        scn.BoardAsPilot(vcsd,pilot);

        scn.MoveCardsToLocation(system2,executor);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());

        scn.LSActivateForceCheat(4); //enough to battle and play IHABFAT

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.DSPayBattleDamageFromCardInPlay(vcsd);

        assertTrue(scn.DSDecisionAvailable("still want to forfeit"));
        scn.DSChooseYes();

        assertTrue(scn.DSDecisionAvailable("About to forfeit")); //About to forfeit - Optional responses
        assertTrue(scn.DSCardPlayAvailable(evac)); //test1
        scn.DSPlayCard(evac);
        assertTrue(scn.DSDecisionAvailable("Choose planet site"));
        assertTrue(scn.DSHasCardChoiceAvailable(ds_tat_db));
        scn.DSChooseCard(ds_tat_db);

        assertTrue(scn.LSDecisionAvailable("Optional responses"));
        assertTrue(scn.LSCardPlayAvailable(badFeeling));
        scn.LSPlayCard(badFeeling);

        //Choose card (or card in group) to re-target from, or click 'Done' to cancel
        assertTrue(scn.LSHasCardChoiceAvailable(ds_tat_db)); //original selection
        assertFalse(scn.LSHasCardChoicesAvailable(ls_cantina));
        scn.LSChooseCard(ds_tat_db);

        //Choose card (or card in group) to re-target from, or click 'Done' to cancel
        assertFalse(scn.LSHasCardChoiceAvailable(ls_cantina)); //can't retarget to LS site, since original was DS site and must be "on the same side of the force"
        assertTrue(scn.LSHasCardChoiceAvailable(executor)); //DS ship that meets criteria
        assertFalse(scn.LSHasCardChoiceAvailable(ds_tat_db)); //must be a different target than original target
        scn.LSChooseCard(executor);

        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose characters to relocate as pilots"));
        scn.DSPass();

        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(executor,pilot)); //test1
    }
}
