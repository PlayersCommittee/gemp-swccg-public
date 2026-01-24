package com.gempukku.swccgo.cards.set1.light;

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

public class Card_1_079_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("escape", "1_079"); //escape pod
					put("pilot", "1_027"); // rebel pilot
                    put("rebelguard", "1_026"); // rebel guard (may not move)
					put("transport", "3_065"); // medium transport (capital ship)
                    put("ywing", "1_147"); //
                    put("ronto", "7_155"); // ronto (non-enclosed creature vehicle)
                    put("ls_cantina","1_128"); //tatooine cantina (planet site)
                    put("ls_yavin_db","1_136"); //yavin docking bay (planet site)
                    put("ls_cc_db","5_083"); //cloud city docking bay (non-planet site)
                    put("ls_dag","4_085"); //dagobah: bog clearing (planet site)
                    put("ls_homeone_db","9_057"); //home one: docking bay
                    put("homeone", "9_074");
				}},
				new HashMap<>()
				{{
                    put("ds_tat_db","1_291"); //tatooine docking bay (planet site)
                    put("executor","4_167"); //(capital ship)
                    put("barrier","1_249"); //imperial barrier
                    put("surprise","5_156");
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
	public void EscapePodStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Escape Pod
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Interrupt
		 * Subtype: Used
		 * Destiny: 6
		 * Icons: Interrupt
		 * Game Text: If your capital starship is about to be lost, relocate your characters aboard to any one
         *      planet site.
		 * Lore: Capital starships have emergency escape pods. Equipped with food, water, flares, medpacs,
         *      hunting blaster and tracking beacon (R2-D2 deactivated this one's beacon).
		 * Set: Premiere
		 * Rarity: U2
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("escape").getBlueprint();

		assertEquals(Title.Escape_Pod, card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
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
	public void EscapePodMayBePlayedWhenYourCapitalShipAboutToBeLost() {
        //test1: escape pod can be played when your capital ship is about to be lost
        //  with basic conditions met: your character on board that can relocate
        //  planet site that can be relocated to
        //test2: character aboard relocates to target planet site
        //test3: escape pod goes to used pile
		var scn = GetScenario();

		var escape = scn.GetLSCard("escape");
        var ls_cantina = scn.GetLSCard("ls_cantina");
        var transport = scn.GetLSCard("transport");
        var pilot = scn.GetLSCard("pilot");

        var executor = scn.GetDSCard("executor");

		var system = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(escape);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,executor,transport);
        scn.BoardAsPilot(transport,pilot);

		scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.LSPayBattleDamageFromCardInPlay(transport);

        assertTrue(scn.LSDecisionAvailable("still want to forfeit"));
        scn.LSChooseYes();

        assertTrue(scn.LSDecisionAvailable("About to forfeit")); //About to forfeit Medium Transport - Optional responses
        assertTrue(scn.LSCardPlayAvailable(escape)); //test1
        scn.LSPlayCard(escape);
        assertTrue(scn.LSDecisionAvailable("Choose planet site"));
        assertTrue(scn.LSHasCardChoiceAvailable(ls_cantina));
        scn.LSChooseCard(ls_cantina);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_cantina,pilot)); //test2
        assertSame(Zone.TOP_OF_USED_PILE,escape.getZone()); //test3
	}

    @Test
    public void EscapePodMayNotBePlayedWhenYourStarfighterAboutToBeLost() {
        //test1: escape pod cannot be played when your non-capital ship is about to be lost
        //  with basic conditions met: your character on board that can relocate
        //  planet site that can be relocated to
        var scn = GetScenario();

        var escape = scn.GetLSCard("escape");
        var ls_cantina = scn.GetLSCard("ls_cantina");
        var ywing = scn.GetLSCard("ywing");
        var pilot = scn.GetLSCard("pilot");

        var executor = scn.GetDSCard("executor");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(escape);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,executor,ywing);
        scn.BoardAsPilot(ywing,pilot);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.LSPayBattleDamageFromCardInPlay(ywing);

        assertTrue(scn.LSDecisionAvailable("still want to forfeit"));
        scn.LSChooseYes();

        assertFalse(scn.LSDecisionAvailable("About to forfeit")); //About to forfeit Y-Wing - Optional responses
        assertTrue(scn.DSAnyDecisionsAvailable()); //test1 (past window to play Escape Pod)
    }

    @Test
    public void EscapePodRequiresAtLeast1Character() {
        //test1: escape pod cannot be played when your capital ship is about to be lost
        //  if no characters are aboard
        var scn = GetScenario();

        var escape = scn.GetLSCard("escape");
        var ls_cantina = scn.GetLSCard("ls_cantina");
        var transport = scn.GetLSCard("transport");

        var executor = scn.GetDSCard("executor");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(escape);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,executor,transport);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(transport);

        assertFalse(scn.LSAnyDecisionsAvailable()); //test1: DS decision means past window to play Escape Pod
        assertTrue(scn.DSDecisionAvailable("FORFEITED_TO_LOST_PILE_FROM_TABLE")); //optional response
    }

    @Test
    public void EscapePodMayBePlayedIfAllActiveCharactersAboardCannotRelocate() {
        //test1: escape pod can be played when your capital ship is about to be lost
        //  and not all characters aboard can relocate to the target planet site
        //test2: does not relocate character aboard that was unable to move
        var scn = GetScenario();

        var escape = scn.GetLSCard("escape");
        var ls_cantina = scn.GetLSCard("ls_cantina");
        var transport = scn.GetLSCard("transport");
        var pilot = scn.GetLSCard("pilot");
        var rebelguard = scn.GetLSCard("rebelguard"); //may not move

        var executor = scn.GetDSCard("executor");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(escape);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,executor,transport);
        scn.BoardAsPilot(transport,pilot);
        scn.BoardAsPassenger(transport,rebelguard);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());
        assertTrue(rebelguard.isPassengerOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.LSPayBattleDamageFromCardInPlay(transport);

        assertTrue(scn.LSDecisionAvailable("still want to forfeit"));
        scn.LSChooseYes();

        assertTrue(scn.LSDecisionAvailable("About to forfeit")); //About to forfeit Medium Transport - Optional responses
        assertTrue(scn.LSCardPlayAvailable(escape)); //test1
        scn.LSPlayCard(escape);
        assertTrue(scn.LSDecisionAvailable("Choose planet site"));
        assertTrue(scn.LSHasCardChoiceAvailable(ls_cantina));
        scn.LSChooseCard(ls_cantina);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_cantina,pilot));
        assertFalse(scn.CardsAtLocation(ls_cantina,rebelguard)); //test2: cannot move, so could not relocate and stays on ship
        assertSame(Zone.TOP_OF_USED_PILE,escape.getZone());
        assertTrue(scn.LSDecisionAvailable("Choose card to put on Lost Pile")); //rebelguard and transport simultaneous loss
    }

    @Test
    public void EscapePodCannotTargetNonPlanetSite() {
        //test1: escape pod cannot be played when your capital ship is about to be lost
        //  and no planet site to relocate to
        var scn = GetScenario();

        var escape = scn.GetLSCard("escape");
        var ls_cc_db = scn.GetLSCard("ls_cc_db");
        var transport = scn.GetLSCard("transport");
        var pilot = scn.GetLSCard("pilot");

        var executor = scn.GetDSCard("executor");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(escape);

        scn.MoveLocationToTable(ls_cc_db);

        scn.MoveCardsToLocation(system,executor,transport);
        scn.BoardAsPilot(transport,pilot);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.LSPayBattleDamageFromCardInPlay(transport);

        assertTrue(scn.LSDecisionAvailable("still want to forfeit"));
        scn.LSChooseYes();

        assertFalse(scn.LSDecisionAvailable("About to forfeit")); //About to forfeit Medium Transport - Optional responses
        assertTrue(scn.DSAnyDecisionsAvailable()); //test1 (past window to play Escape Pod)
    }

    @Test
    public void EscapePodCannotTargetDagobahPlanetSite() {
        //test1: escape pod cannot be played when your capital ship is about to be lost
        //  and no non-dagobah planet site is on table
        var scn = GetScenario();

        var escape = scn.GetLSCard("escape");
        var ls_dag = scn.GetLSCard("ls_dag");
        var transport = scn.GetLSCard("transport");
        var pilot = scn.GetLSCard("pilot");

        var executor = scn.GetDSCard("executor");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(escape);

        scn.MoveLocationToTable(ls_dag);

        scn.MoveCardsToLocation(system,executor,transport);
        scn.BoardAsPilot(transport,pilot);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.LSPayBattleDamageFromCardInPlay(transport);

        assertTrue(scn.LSDecisionAvailable("still want to forfeit"));
        scn.LSChooseYes();

        assertFalse(scn.LSDecisionAvailable("About to forfeit")); //About to forfeit Medium Transport - Optional responses
        assertTrue(scn.DSAnyDecisionsAvailable()); //test1 (past window to play Escape Pod)
    }

    @Test
    public void EscapePodCannotTargetFromDagobah() {
        //test1: escape pod cannot be played when your capital ship is about to be lost
        //  from Dagobah location
        var scn = GetScenario();

        var escape = scn.GetLSCard("escape");
        var ls_dag = scn.GetLSCard("ls_dag");
        var transport = scn.GetLSCard("transport");
        var pilot = scn.GetLSCard("pilot");
        var ls_cantina = scn.GetLSCard("ls_cantina");

        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveCardsToLSHand(escape);

        scn.MoveLocationToTable(ls_dag);
        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(ls_dag,trooper,transport);
        scn.BoardAsPilot(transport,pilot);

        scn.SkipToPhase(Phase.BATTLE);

        scn.DSInitiateBattle(ls_dag);
        scn.SkipToDamageSegment(false);

        scn.LSPayBattleDamageFromCardInPlay(transport);

        assertTrue(scn.LSDecisionAvailable("still want to forfeit"));
        scn.LSChooseYes();

        assertFalse(scn.LSDecisionAvailable("About to forfeit")); //About to forfeit Medium Transport - Optional responses
        assertTrue(scn.DSAnyDecisionsAvailable()); //test1 (past window to play Escape Pod)
    }

    @Test
    public void EscapePodCanTargetYourLandedShipSite() {
        //test1: escape pod is playable if lost while landed at a site
        //test2: escape pod can relocate to the same site that your ship is currently landed at
        //test3: relocate to same site succeeds
        var scn = GetScenario();

        var escape = scn.GetLSCard("escape");
        var ls_yavin_db = scn.GetLSCard("ls_yavin_db");
        var ls_cantina = scn.GetLSCard("ls_cantina");
        var transport = scn.GetLSCard("transport");
        var pilot = scn.GetLSCard("pilot");

        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveCardsToLSHand(escape);

        scn.MoveLocationToTable(ls_yavin_db);
        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(ls_yavin_db,transport,trooper);
        scn.BoardAsPilot(transport,pilot);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());

        scn.DSInitiateBattle(ls_yavin_db);
        scn.SkipToDamageSegment(false);

        scn.LSPayBattleDamageFromCardInPlay(transport);

        assertTrue(scn.LSDecisionAvailable("still want to forfeit"));
        scn.LSChooseYes();

        assertTrue(scn.LSDecisionAvailable("About to forfeit")); //About to forfeit Medium Transport - Optional responses
        assertTrue(scn.LSCardPlayAvailable(escape)); //test1
        scn.LSPlayCard(escape);
        assertTrue(scn.LSDecisionAvailable("Choose planet site"));
        assertTrue(scn.LSHasCardChoiceAvailable(ls_cantina));
        assertTrue(scn.LSHasCardChoiceAvailable(ls_yavin_db)); //test2
        scn.LSChooseCard(ls_yavin_db);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_yavin_db,pilot)); //test3
        assertFalse(scn.IsAttachedTo(transport,pilot));
    }

    @Test
    public void EscapePodDoesNotRelocateCharactersOnVehiclesAboard() {
        //test1: does not relocate vehicles aboard
        //test2: does not relocate characters on vehicles aboard
        var scn = GetScenario();

        var escape = scn.GetLSCard("escape");
        var ls_yavin_db = scn.GetLSCard("ls_yavin_db");
        var transport = scn.GetLSCard("transport"); //capacity to hold 1 vehicle
        var pilot = scn.GetLSCard("pilot");
        var ronto = scn.GetLSCard("ronto");
        var rebelTrooper = scn.GetLSFiller(1);

        var executor = scn.GetDSCard("executor");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(escape);

        scn.MoveLocationToTable(ls_yavin_db);

        scn.MoveCardsToLocation(system,executor,transport);
        scn.BoardAsPilot(transport,pilot);
        scn.BoardAsVehicle(transport,ronto);
        scn.BoardAsPassenger(ronto,rebelTrooper);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());
        assertTrue(rebelTrooper.isPassengerOf());
        assertTrue(ronto.isInCargoHoldAsVehicle());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.LSPayBattleDamageFromCardInPlay(transport);

        assertTrue(scn.LSDecisionAvailable("still want to forfeit"));
        scn.LSChooseYes();

        assertTrue(scn.LSDecisionAvailable("About to forfeit")); //About to forfeit Medium Transport - Optional responses
        assertTrue(scn.LSCardPlayAvailable(escape)); //test1
        scn.LSPlayCard(escape);
        assertTrue(scn.LSDecisionAvailable("Choose planet site"));
        assertTrue(scn.LSHasCardChoiceAvailable(ls_yavin_db));
        scn.LSChooseCard(ls_yavin_db);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_yavin_db,pilot));
        assertFalse(scn.CardsAtLocation(ls_yavin_db,ronto)); //test1
        assertFalse(scn.CardsAtLocation(ls_yavin_db,rebelTrooper)); //test2
    }

    @Test
    public void EscapePodDoesNotRelocateInactiveCharacters() {
        //test1: will not relocate characters that are inactive (in this, excluded from battle)
        var scn = GetScenario();

        var escape = scn.GetLSCard("escape");
        var ls_cantina = scn.GetLSCard("ls_cantina");
        var transport = scn.GetLSCard("transport");
        var pilot = scn.GetLSCard("pilot");
        var rebelTrooper = scn.GetLSFiller(1);

        var executor = scn.GetDSCard("executor");
        var barrier = scn.GetDSCard("barrier");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(escape, rebelTrooper);
        scn.MoveCardsToDSHand(barrier);

        scn.MoveLocationToTable(ls_cantina);

        scn.MoveCardsToLocation(system,executor,transport);
        scn.BoardAsPilot(transport,pilot);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(rebelTrooper);
        scn.LSChooseCard(transport);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        //Rebel Trooper just deployed - Optional responses
        scn.DSPlayCard(barrier);

        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());
        assertTrue(rebelTrooper.isPassengerOf());

        scn.LSInitiateBattle(system);
        assertTrue(scn.LSDecisionAvailable("ABOUT_TO_BE_EXCLUDED"));
        scn.LSPass();
        scn.DSPass();

        scn.LSPass(); //EXCLUDED_FROM_BATTLE - Optional responses
        scn.DSPass();

        scn.SkipToDamageSegment(false);

        scn.LSPayBattleDamageFromCardInPlay(transport);

        assertTrue(scn.LSDecisionAvailable("still want to forfeit"));
        scn.LSChooseYes();

        assertTrue(scn.LSDecisionAvailable("About to forfeit")); //About to forfeit Medium Transport - Optional responses
        assertTrue(scn.LSCardPlayAvailable(escape)); //test1
        scn.LSPlayCard(escape);
        assertTrue(scn.LSDecisionAvailable("Choose planet site"));
        assertTrue(scn.LSHasCardChoiceAvailable(ls_cantina));
        scn.LSChooseCard(ls_cantina);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_cantina,pilot));
        assertFalse(scn.CardsAtLocation(ls_cantina,rebelTrooper));//test1 (excluded and could not be seen to relocate)
        assertSame(Zone.TOP_OF_USED_PILE,escape.getZone());
    }

    @Test
    public void EscapePodDestinationMayBeRetargetedBySurprise() {
        //test1: escape pod planet site selected can be retargeted to a new, valid planet site
        var scn = GetScenario();

        var escape = scn.GetLSCard("escape");
        var ls_cantina = scn.GetLSCard("ls_cantina");
        var transport = scn.GetLSCard("transport");
        var pilot = scn.GetLSCard("pilot");
        var ls_yavin_db = scn.GetLSCard("ls_yavin_db");
        var ls_cc_db = scn.GetLSCard("ls_cc_db");
        var ls_dag = scn.GetLSCard("ls_dag");

        var executor = scn.GetDSCard("executor");
        var surprise = scn.GetDSCard("surprise");
        var ds_tat_db = scn.GetDSCard("ds_tat_db");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(escape);
        scn.MoveCardsToDSHand(surprise);

        scn.MoveLocationToTable(ls_cantina);
        scn.MoveLocationToTable(ls_yavin_db);
        scn.MoveLocationToTable(ls_cc_db);
        scn.MoveLocationToTable(ls_dag);
        scn.MoveLocationToTable(ds_tat_db);

        scn.MoveCardsToLocation(system,executor,transport);
        scn.BoardAsPilot(transport,pilot);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());

        scn.DSActivateForceCheat(4); //enough to battle and play surprise

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.LSPayBattleDamageFromCardInPlay(transport);

        assertTrue(scn.LSDecisionAvailable("still want to forfeit"));
        scn.LSChooseYes();

        assertTrue(scn.LSDecisionAvailable("About to forfeit")); //About to forfeit Medium Transport - Optional responses
        assertTrue(scn.LSCardPlayAvailable(escape)); //test1
        scn.LSPlayCard(escape);
        assertTrue(scn.LSDecisionAvailable("Choose planet site"));
        assertTrue(scn.LSHasCardChoiceAvailable(ls_cantina));
        scn.LSChooseCard(ls_cantina);

        assertTrue(scn.DSDecisionAvailable("Optional responses"));
        assertTrue(scn.DSCardPlayAvailable(surprise));
        scn.DSPlayCard(surprise);

        //Choose card (or card in group) to re-target from, or click 'Done' to cancel
        assertTrue(scn.DSHasCardChoiceAvailable(ls_cantina)); //original selection
        assertFalse(scn.DSHasCardChoicesAvailable(ls_yavin_db,ls_cc_db,ls_dag,ds_tat_db));
        scn.DSChooseCard(ls_cantina);

        //Choose card (or card in group) to re-target from, or click 'Done' to cancel
        assertFalse(scn.DSHasCardChoiceAvailable(ls_cantina)); //must retarget somewhere different
        assertFalse(scn.DSHasCardChoiceAvailable(ls_dag)); //not planet site
        assertTrue(scn.DSHasCardChoiceAvailable(ls_yavin_db)); //LS site that meets criteria
        assertFalse(scn.DSHasCardChoiceAvailable(ds_tat_db)); //can't retarget to DS site, since original was LS site and must be "on the same side of the force"
        assertFalse(scn.DSHasCardChoiceAvailable(ls_dag)); //dagobah rules
        scn.DSChooseCard(ls_yavin_db);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_yavin_db,pilot)); //test1
        assertSame(Zone.TOP_OF_USED_PILE,escape.getZone());
    }

    @Test
    public void EscapePodRelocatesCharactersAtRelatedStarshipSites() {
        //test1: escape pod relocates characters that are aboard due to being at a related starship site
        var scn = GetScenario();

        var escape = scn.GetLSCard("escape");
        var ls_cantina = scn.GetLSCard("ls_cantina");
        var homeone = scn.GetLSCard("homeone");
        var pilot = scn.GetLSCard("pilot");
        var rebelTrooper = scn.GetLSFiller(1);
        var ls_homeone_db = scn.GetLSCard("ls_homeone_db");

        var executor = scn.GetDSCard("executor");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(escape);

        scn.MoveLocationToTable(ls_cantina);
        scn.MoveLocationToTable(ls_homeone_db);

        scn.MoveCardsToLocation(system,executor, homeone);
        scn.BoardAsPilot(homeone,pilot);

        scn.MoveCardsToLocation(ls_homeone_db,rebelTrooper);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(pilot.isPilotOf());

        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        scn.LSPayBattleDamageFromCardInPlay(homeone);

        assertTrue(scn.LSDecisionAvailable("still want to forfeit"));
        scn.LSChooseYes();

        assertTrue(scn.LSDecisionAvailable("About to forfeit")); //About to forfeit Medium Transport - Optional responses
        assertTrue(scn.LSCardPlayAvailable(escape)); //test1
        scn.LSPlayCard(escape);
        assertTrue(scn.LSDecisionAvailable("Choose planet site"));
        assertTrue(scn.LSHasCardChoiceAvailable(ls_cantina));
        scn.LSChooseCard(ls_cantina);

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ls_cantina,pilot));
        assertTrue(scn.CardsAtLocation(ls_cantina,rebelTrooper)); //test1
        assertSame(Zone.TOP_OF_USED_PILE,escape.getZone());
    }

}
