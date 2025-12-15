package com.gempukku.swccgo.rules.missing;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SearchPartyTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("cantina","1_128"); //for a site adjacent to DS's starting location
					put("rebelScout", "8_010"); //endor scout trooper
				}},
				new HashMap<>() {{
					put("atat","3_157"); //blizzard walker
					put("droid","1_186"); //LIN-V8M droid
					put("scout","8_092"); //biker scout trooper
					put("scout2","8_092"); //biker scout trooper
					put("spy","1_177"); //garindan
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

	//Advanced Rulebook (2003) - Appendix C - Special Rules
	//Missing - Search Party
	//During your control phase, you may attempt to find missing characters (even if they went missing that same turn)
	// by forming and using a search party as follows:
	//1) Designate one or more of your characters at the same site as the missing character(s) to be members of the
	// search party.
	//2) Draw destiny.
	//3) Add 1 to the destiny draw for each member of the search party (2 if that search party character is a scout).
	//4) If total destiny > 5, one of your missing characters there (random selection) is found and joins the
	// search party.
	//You may only search where you have one or more characters missing (you may not search for your opponent's
	// characters). Members of a search party (including any characters they find) may not move, search again or
	// participate in a battle you initiate for the remainder of that turn.

	@Test
	public void SearchPartyCanOnlyBeFormedDuringYourControlPhase() {
		//spot checks that native search party functionality works during the correct phases
		//test1: can form a search party during your control phase
		//test2: cannot form a search party during your non-control phase
		//test3: cannot form a search party during opponent's control phase

		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,trooper2);

		scn.MakeCardGoMissing(trooper1);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party")); //test1

		scn.SkipToPhase(Phase.DEPLOY);
		assertFalse(scn.DSCardActionAvailable(site,"Form search party")); //test2

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.LSPass();
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertFalse(scn.DSCardActionAvailable(site,"Form search party")); //test3
	}

	@Test
	public void SearchPartyCanOnlyBeFormedAtSiteWithYourCharacter() {
		//test1: search party cannot be formed at a site where you have no (non-missing) characters
		//test2: search party can be formed at a site where you have a character (even one without ability/presence)
		var scn = GetScenario();

		var site1 = scn.GetDSStartingLocation();
		var site2 = scn.GetLSStartingLocation();

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var droid = scn.GetDSCard("droid");

		scn.StartGame();

		scn.MoveCardsToLocation(site1,trooper1);
		scn.MakeCardGoMissing(trooper1);

		scn.MoveCardsToLocation(site2,trooper2,droid);
		scn.MakeCardGoMissing(trooper2);

		scn.SkipToPhase(Phase.CONTROL);
		assertFalse(scn.DSCardActionAvailable(site1,"Form search party")); //test1
		assertTrue(scn.DSCardActionAvailable(site2,"Form search party")); //test2
	}

	@Test
	public void SearchPartyCannotBeFormedAtSiteWithYourAloneUndercoverCharacter() {
		//test1: search party cannot be formed at a site where your only non-missing character is undercover
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var trooper1 = scn.GetDSFiller(1);
		var spy = scn.GetDSCard("spy");

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,spy);
		scn.MakeCardGoMissing(trooper1);

		scn.MakeCardGoUndercover(spy);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(spy.isUndercover());
		assertFalse(scn.DSCardActionAvailable(site,"Form search party")); //test1
	}

	@Test
	public void SearchPartyCanOnlyBeFormedAtSiteWithYourMissingCharacter() {
		//test1: search party cannot be formed where only missing characters at site are opponent's
		//test2: search party cannot be formed where no missing characters at site
		var scn = GetScenario();

		var site1 = scn.GetDSStartingLocation();
		var site2 = scn.GetLSStartingLocation();

		var rebelTrooper1 = scn.GetLSFiller(1);

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);

		scn.StartGame();

		scn.MoveCardsToLocation(site1,rebelTrooper1,trooper1);
		scn.MakeCardGoMissing(trooper1);

		scn.MoveCardsToLocation(site2,trooper2);

		scn.SkipToPhase(Phase.CONTROL);
		assertFalse(scn.DSCardActionAvailable(site1,"Form search party")); //test1
		assertFalse(scn.DSCardActionAvailable(site2,"Form search party")); //test2
	}

	@Test
	public void SearchPartyCanBeFormedAtSiteWithOnlyMissingCharacterUndercover() {
		//test that search party can be formed at a site where your only missing character is undercover
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var spy = scn.GetDSCard("spy");
		var droid = scn.GetDSCard("droid");

		scn.StartGame();

		scn.MoveCardsToLocation(site,spy,droid);
		scn.MakeCardGoUndercover(spy);
		scn.MakeCardGoMissing(spy);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(spy.isMissing());
		assertTrue(spy.isUndercover());
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));
	}

	@Test
	public void SearchPartyMembersMustBeYourCharactersAtSameSite() {
		//test1: characters at same sites may be selected as search party members
		//test2: characters in enclosed vehicles may be selected as search party members
		//test3: characters at adjacent sites may not be selected as search party members
		//test4: non-characters may not be selected as search party members (spotcheck = vehicle)
		//test5: opponent's characters may not be selected as search party members
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var cantina = scn.GetLSCard("cantina");
		var rebelTrooper = scn.GetLSFiller(1);

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var trooper3 = scn.GetDSFiller(3);
		var trooper4 = scn.GetDSFiller(4);
		var atat = scn.GetDSCard("atat");

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,trooper2,trooper3,atat,rebelTrooper);
		scn.BoardAsPassenger(atat,trooper3);
		scn.MakeCardGoMissing(trooper1);

		scn.MoveLocationToTable(cantina);
		scn.MoveCardsToLocation(cantina,trooper4);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		assertTrue(scn.DSHasCardChoiceAvailable(trooper2)); //test1: at site
		assertTrue(scn.DSHasCardChoiceAvailable(trooper3)); //test2: at site (in enclosed vehicle)
		assertFalse(scn.DSHasCardChoiceAvailable(trooper4)); //test3: not at site
		assertFalse(scn.DSHasCardChoiceAvailable(atat)); //test4: at site but not character
		assertFalse(scn.DSHasCardChoiceAvailable(rebelTrooper)); //test5: at site but not your character
	}

	@Test
	public void SearchPartyFailsIfTotalDestiny5OrLess() {
		//>5 total destiny is required to succeed, so test that a total destiny of 5 fails
		//indirectly confirms fail/success threshold when combined with SearchPartySucceedsIfTotalDestinyMoreThan5()
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,trooper2);
		scn.MakeCardGoMissing(trooper1);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(4);

		assertTrue(scn.DSHasCardChoicesAvailable(trooper2));
		scn.DSChooseCard(trooper2);
		//search party destiny total: 4 + 1 character = 5, failed
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(trooper1.isMissing());
	}

	@Test
	public void SearchPartySucceedsIfTotalDestinyMoreThan5() {
		//>5 total destiny is required to succeed, so test that a total destiny of 6 passes
		//indirectly confirms fail/success threshold when combined with SearchPartyFailsIfTotalDestiny5OrLess()
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,trooper2);
		scn.MakeCardGoMissing(trooper1);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(5);

		assertTrue(scn.DSHasCardChoicesAvailable(trooper2));
		scn.DSChooseCard(trooper2);
		//search party destiny total: 5 + 1 character = 6, succeeds
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertFalse(trooper1.isMissing());
	}

	@Test
	public void SearchPartNonScoutMembersEachAdd1ToTotalDestiny() {
		//each non-scout search party member adds 1 to search party destiny total
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var trooper3 = scn.GetDSFiller(3);

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,trooper2,trooper3);
		scn.MakeCardGoMissing(trooper1);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(4);

		assertTrue(scn.DSHasCardChoicesAvailable(trooper2,trooper3));
		scn.DSChooseCards(trooper2,trooper3);
		//search party destiny total: 4 + 2 character = 6, success
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertFalse(trooper1.isMissing());
	}

	@Test
	public void SearchPartyScoutMembersEachAdd2ToTotalDestiny() {
		//each scout search party member adds 2 to search party destiny total
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var trooper1 = scn.GetDSFiller(1);
		var scout = scn.GetDSCard("scout");
		var scout2 = scn.GetDSCard("scout2");

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,scout,scout2);
		scn.MakeCardGoMissing(trooper1);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(2);

		assertTrue(scn.DSHasCardChoicesAvailable(scout,scout2));
		scn.DSChooseCards(scout,scout2);
		//search party destiny total: 2 + 4 scout character = 6, success
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertFalse(trooper1.isMissing());
	}

	@Test
	public void SearchPartyNonMembersDoNotAddToTotalDestiny() {
		//test1: your character (scout) at same site (but not in party) does not add
		//test2: opponent's character (scout) at same site does not add
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelScout = scn.GetLSCard("rebelScout");

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var scout = scn.GetDSCard("scout");

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,trooper2,scout,rebelScout);
		scn.MakeCardGoMissing(trooper1);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(4);

		assertTrue(scn.DSHasCardChoiceAvailable(trooper2));
		scn.DSChooseCard(trooper2);
		//search party destiny total: 4 + 1 character = 5, fails
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(trooper1.isMissing()); //test1, test2
	}

	@Test
	public void SearchPartySuccessOnlyFinds1MissingCharacter() {
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var trooper3 = scn.GetDSFiller(3);

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,trooper2,trooper3);
		scn.MakeCardGoMissing(trooper1);
		scn.MakeCardGoMissing(trooper3);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(5);
		assertTrue(trooper1.isMissing());
		assertTrue(trooper3.isMissing());

		assertTrue(scn.DSHasCardChoicesAvailable(trooper2));
		scn.DSChooseCard(trooper2);
		//search party destiny total: 5 + 1 character = 6, succeeds
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(trooper1.isMissing() ^ trooper3.isMissing()); //one (random choice) was found, one still missing
	}

	@Test
	public void SearchPartyMembersMayNotParticipateInSearchPartiesThisTurn() {
		//test1: can form more than 1 search party at a site in the same turn
		//test2: trooper2 in failed search party cannot participate in a second search party at same site this turn
		//test3: trooper2 can participate in search party in a future turn
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var scout = scn.GetDSCard("scout");

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,trooper2,scout);
		scn.MakeCardGoMissing(trooper1);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(4);

		assertTrue(scn.DSHasCardChoicesAvailable(trooper2,scout));
		scn.DSChooseCard(trooper2);
		//search party destiny total: 4 + 1 character = 5, fail
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(trooper1.isMissing());
		scn.LSPass();

		assertTrue(scn.DSCardActionAvailable(site,"Form search party")); //test1

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(3);

		assertTrue(scn.DSHasCardChoicesAvailable(scout));
		assertFalse(scn.DSHasCardChoicesAvailable(trooper2)); //test2
		scn.DSChooseCard(scout);
		//search party destiny total: 3 + 2 scout character = 5, fail
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(trooper1.isMissing());

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);

		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		assertTrue(scn.DSHasCardChoicesAvailable(trooper2,scout)); //test3
	}

	@Test
	public void SearchPartyMembersCannotBeUndercover() {
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var spy = scn.GetDSCard("spy");

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,trooper2,spy);
		scn.MakeCardGoMissing(trooper1);
		scn.MakeCardGoUndercover(spy);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(4);

		assertTrue(scn.DSHasCardChoiceAvailable(trooper2));
		assertFalse(scn.DSHasCardChoiceAvailable(spy)); //because undercover (considered inactive)
	}

	@Test
	public void SearchPartMembersCannotBeCaptives() {
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var rebelTrooper3 = scn.GetLSFiller(3);

		var trooper1 = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,rebelTrooper1,rebelTrooper2,rebelTrooper3);
		scn.MakeCardGoMissing(rebelTrooper1);

		scn.CaptureCardWith(trooper1,rebelTrooper2);
		assertTrue(rebelTrooper2.isCaptive());

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSCardActionAvailable(site,"Form search party"));

		scn.LSUseCardAction(site,"Form search party");

		assertTrue(scn.LSHasCardChoiceAvailable(rebelTrooper3));
		assertFalse(scn.LSHasCardChoiceAvailable(rebelTrooper1)); //because missing (inactive)
		assertFalse(scn.LSHasCardChoiceAvailable(rebelTrooper2)); //because captive (inactive)
	}


	@Test
	public void SearchPartyMembersMayNotParticipateInBattleThisTurn() {
		//test1: search party member cannot battle this turn
		//test2: search party member can battle in future turn
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();
		var rebelTrooper1 = scn.GetLSFiller(1);

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper1,trooper2,rebelTrooper1);
		scn.MakeCardGoMissing(trooper1);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(4);

		assertTrue(scn.DSHasCardChoicesAvailable(trooper2));
		scn.DSChooseCard(trooper2);
		//search party destiny total: 4 + 1 character = 5, fail
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(trooper1.isMissing());

		scn.SkipToPhase(Phase.BATTLE);
		assertTrue(scn.AwaitingDSBattlePhaseActions());
		assertFalse(scn.DSCardActionAvailable(site,"Initiate battle")); //test1

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.BATTLE);
		assertTrue(scn.AwaitingDSBattlePhaseActions());
		assertTrue(scn.DSCardActionAvailable(site,"Initiate battle")); //test2
	}

	@Test
	public void SearchPartyMembersMayNotMoveThisTurn() {
		//test1: search party member cannot move this turn
		//test2: search party member can move in future turn
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();
		var cantina = scn.GetLSCard("cantina");
		var rebelTrooper1 = scn.GetLSFiller(1);

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);

		scn.StartGame();

		scn.MoveLocationToTable(cantina);
		scn.MoveCardsToLocation(site,trooper1,trooper2,rebelTrooper1);
		scn.MakeCardGoMissing(trooper1);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(4);

		assertTrue(scn.DSHasCardChoicesAvailable(trooper2));
		scn.DSChooseCard(trooper2);
		//search party destiny total: 4 + 1 character = 5, fail
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(trooper1.isMissing());

		scn.SkipToPhase(Phase.MOVE);
		assertTrue(scn.AwaitingDSMovePhaseActions());
		assertFalse(scn.DSCardActionAvailable(trooper2,"Move")); //test1

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.MOVE);
		assertTrue(scn.AwaitingDSMovePhaseActions());
		assertTrue(scn.DSCardActionAvailable(trooper2,"Move")); //test2
	}

	@Test
	public void SearchPartySuccessKeepsSpyUndercover() {
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var spy = scn.GetDSCard("spy");
		var trooper2 = scn.GetDSFiller(2);

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper2,spy);

		scn.MakeCardGoUndercover(spy);
		scn.MakeCardGoMissing(spy);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(spy.isUndercover());
		assertTrue(scn.DSCardActionAvailable(site,"Form search party"));

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(5);

		assertTrue(scn.DSHasCardChoicesAvailable(trooper2));
		scn.DSChooseCard(trooper2);
		//search party destiny total: 5 + 1 character = 6, succeeds
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertFalse(spy.isMissing());
		assertTrue(spy.isUndercover());
	}

	//other tests to add:

}
