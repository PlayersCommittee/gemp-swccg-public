package com.gempukku.swccgo.cards.set7.dark;

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
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Card_7_252_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("site3","1_128"); //tatooine: cantina
					put("bad_feeling","4_052");
					put("sense_ls","1_109");
				}},
				new HashMap<>()
				{{
					put("hunting", "7_252"); //hunting party
					put("bh_djas","1_171"); //djas puhr (bounty hunter)
					put("bh_4lom","4_091"); //4-lom (bounty hunter, droid)
					put("bh_scout","4_107"); //zuckuss (bounty hunter, scout)
					put("site4","1_291"); //tatooine: docking bay 94
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
	public void HuntingPartyStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Hunting Party
		 * Uniqueness: Unique
		 * Side: Dark
		 * Type: Interrupt
		 * Subtype: Used
		 * Destiny: 7
		 * Icons: Interrupt, Special Edition
		 * Game Text: During your control phase, form a search party for an opponent's missing character at same site.
		 * 		Add 1 to search party destiny draw for each bounty hunter in search party. If successful,
		 * 		capture the character found.
		 * Lore: Sometimes a missing person is found by the wrong search party.
		 * Set: Special Edition
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("hunting").getBlueprint();

		assertEquals("Hunting Party", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(CardSubtype.USED, card.getCardSubtype());
		assertEquals(7, card.getDestiny(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.INTERRUPT);
			add(Icon.SPECIAL_EDITION);
		}});
		assertEquals(ExpansionSet.SPECIAL_EDITION,card.getExpansionSet());
		assertEquals(Rarity.R, card.getRarity());
	}

	@Test
	public void HuntingPartyCanOnlyBePlayedDuringYourControlPhase() {
		//spot checks that hunting party can only be played during the correct phases
		//test1: can play hunting party during your control phase
		//test2: cannot play hunting party during your non-control phase
		//test3: cannot play hunting party during opponent's control phase

		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper = scn.GetLSFiller(1);

		var trooper = scn.GetDSFiller(1);
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToLocation(site,trooper,rebelTrooper);
		scn.MoveCardsToDSHand(hunting);

		scn.MakeCardGoMissing(rebelTrooper);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(hunting)); //test1

		scn.SkipToPhase(Phase.DEPLOY);
		assertFalse(scn.DSCardPlayAvailable(hunting)); //test2

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.LSPass();
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertFalse(scn.DSCardPlayAvailable(hunting)); //test3
	}

	@Test
	public void HuntingPartyCanOnlyTargetASiteWithYourCharacter() {
		//test1: hunting party cannot target a site where you have no characters
		//test2: hunting party can target a site where you have a character (even one without ability/presence)
		//test3: hunting party cannot target a site where you have no characters and opponent does
		var scn = GetScenario();

		var site1 = scn.GetDSStartingLocation();
		var site2 = scn.GetLSStartingLocation();
		var site3 = scn.GetLSCard("site3");

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var rebelTrooper3 = scn.GetLSFiller(3);
		var rebelTrooper4 = scn.GetLSFiller(4);

		var hunting = scn.GetDSCard("hunting");
		var bh_4lom = scn.GetDSCard("bh_4lom");

		scn.StartGame();

		scn.MoveLocationToTable(site3);
		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLocation(site1,rebelTrooper1);
		scn.MakeCardGoMissing(rebelTrooper1);

		scn.MoveCardsToLocation(site2,bh_4lom,rebelTrooper2);
		scn.MakeCardGoMissing(rebelTrooper2);

		scn.MoveCardsToLocation(site3,rebelTrooper3,rebelTrooper4);
		scn.MakeCardGoMissing(rebelTrooper4);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(hunting));
		scn.DSPlayCard(hunting);
		assertFalse(scn.DSHasCardChoiceAvailable(site1)); //your character not at site
		assertTrue(scn.DSHasCardChoiceAvailable(site2)); //your character at site
		assertFalse(scn.DSHasCardChoiceAvailable(site3)); //your character not at site (but opponent's character is)
	}

	@Test
	public void HuntingPartyCanOnlyTargetASiteWithOpponentsMissingCharacter() {
		//test1: hunting party cannot target a site where only missing characters at site are yours
		//test2: hunting party cannot target a site where no missing characters at site
		var scn = GetScenario();

		var site1 = scn.GetDSStartingLocation();
		var site2 = scn.GetLSStartingLocation();

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var trooper3 = scn.GetDSFiller(3);
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLocation(site1,trooper1,trooper2); //test1 setup
		scn.MakeCardGoMissing(trooper1);

		scn.MoveCardsToLocation(site2,trooper3); //test2 setup

		scn.SkipToPhase(Phase.CONTROL);
		assertFalse(scn.DSCardPlayAvailable(hunting)); //test1, test2
	}

	@Test
	public void HuntingPartySearchPartyFailDoesNotFindOrCaptureOpponentsMissingCharacter() {
		//>5 total destiny is required to succeed, so test that a total destiny of 5 fails
		//test1: opponent's character not found after failing search party draw
		//test2: opponent's character not captured (not captive or escaped to used pile) after failing search party draw
		//test3: hunting party goes to used pile after playing
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper = scn.GetLSFiller(1);

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLocation(site,trooper1,trooper2,rebelTrooper);
		scn.MakeCardGoMissing(rebelTrooper);

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(hunting);
		scn.DSChooseCard(site);
		scn.PrepareDSDestiny(4);
		scn.LSPass(); //Playing Hunting Party - Optional responses
		scn.DSPass();

		assertTrue(scn.DSHasCardChoicesAvailable(trooper1,trooper2)); //select search party members
		scn.DSChooseCard(trooper1);
		//search party destiny total: 4 + 1 character = 5, failed
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(rebelTrooper.isMissing()); //test1

		assertTrue(scn.CardsAtLocation(site,rebelTrooper)); //test2 (not captured and escaped to used pile)
		assertFalse(rebelTrooper.isCaptive());

		assertSame(Zone.TOP_OF_USED_PILE,hunting.getZone()); //test3
	}

	@Test
	public void HuntingPartySearchPartySuccessFindsAndCapturesOpponentsMissingCharacter() {
		//>5 total destiny is required to succeed, so test that a total destiny of 6 passes
		//test1: opponent's character found after search party draw success
		//test2: opponent's character captured (escaped to used pile) after search party draw success
		//test3: hunting party goes to used pile after playing
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper = scn.GetLSFiller(1);

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLocation(site,trooper1,trooper2,rebelTrooper);
		scn.MakeCardGoMissing(rebelTrooper);

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(hunting);
		scn.DSChooseCard(site);
		scn.PrepareDSDestiny(5);
		scn.LSPass(); //Playing Hunting Party - Optional responses
		scn.DSPass();

		assertTrue(scn.DSHasCardChoicesAvailable(trooper1,trooper2)); //select search party members
		scn.DSChooseCard(trooper1);
		//search party destiny total: 5 + 1 character = 6, success
		scn.PassAllResponses();

		assertFalse(rebelTrooper.isMissing()); //test1
		assertTrue(scn.DSCaptureDecisionAvailable());
		scn.DSChooseEscape();

		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSControlPhaseActions());

		assertFalse(scn.CardsAtLocation(site,rebelTrooper)); //test2 (escaped to used pile)
		assertFalse(rebelTrooper.isCaptive());
		assertSame(Zone.TOP_OF_USED_PILE,rebelTrooper.getZone()); //(LS used pile)
		assertSame(Zone.TOP_OF_USED_PILE,hunting.getZone()); //test3
	}

	@Test
	public void HuntingPartySearchPartySuccessAllowsNonMemberToSeizeOpponentsMissingCharacter() {
		//test1: warrior at site in search party is an eligible escort for seized captive
		//test2: warrior at site outside search party is an eligible escort for seized captive
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper = scn.GetLSFiller(1);

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLocation(site,trooper1,trooper2,rebelTrooper);
		scn.MakeCardGoMissing(rebelTrooper);

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(hunting);
		scn.DSChooseCard(site);
		scn.PrepareDSDestiny(5);
		scn.LSPass(); //Playing Hunting Party - Optional responses
		scn.DSPass();

		assertTrue(scn.DSHasCardChoicesAvailable(trooper1,trooper2)); //select search party members
		scn.DSChooseCard(trooper1);
		//search party destiny total: 5 + 1 character = 6, success
		scn.PassAllResponses();

		assertTrue(scn.DSCaptureDecisionAvailable());
		scn.DSChooseSeizeCaptive();
		assertTrue(scn.DSHasCardChoiceAvailable(trooper1)); //test1
		assertTrue(scn.DSHasCardChoiceAvailable(trooper2)); //test2
	}

	@Test
	public void HuntingPartyBountyHuntersInSearchPartyEachAdd1ToSearchPartyDestiny() {
		//test1: each bounty hunter in search party adds 1 to search party destiny
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper = scn.GetLSFiller(1);

		var trooper = scn.GetDSFiller(1);
		var bh_djas = scn.GetDSCard("bh_djas");
		var bh_4lom = scn.GetDSCard("bh_4lom");
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLocation(site,trooper,bh_djas,bh_4lom,rebelTrooper);
		scn.MakeCardGoMissing(rebelTrooper);

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(hunting);
		scn.DSChooseCard(site);
		scn.PrepareDSDestiny(1);
		scn.LSPass(); //Playing Hunting Party - Optional responses
		scn.DSPass();

		assertTrue(scn.DSHasCardChoicesAvailable(trooper,bh_djas,bh_4lom)); //select search party members
		scn.DSChooseCards(trooper,bh_djas,bh_4lom);
		//search party destiny total: 1 + 3 character + 2 for bounty hunters = 6, success
		scn.PassAllResponses();

		assertFalse(rebelTrooper.isMissing()); //test1
		assertTrue(scn.DSCaptureDecisionAvailable());
	}

	@Test
	public void HuntingPartyBountyHunterScoutsInSearchPartyEachAdd1ToSearchPartyDestiny() {
		//test1: each scout bounty hunter in search party adds 1 to search party destiny (in addition to scout bonus)
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper = scn.GetLSFiller(1);

		var trooper = scn.GetDSFiller(1);
		var bh_djas = scn.GetDSCard("bh_djas");
		var bh_scout = scn.GetDSCard("bh_scout");
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLocation(site,trooper,bh_djas,bh_scout,rebelTrooper);
		scn.MakeCardGoMissing(rebelTrooper);

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(hunting);
		scn.DSChooseCard(site);
		scn.PrepareDSDestiny(0);
		scn.LSPass(); //Playing Hunting Party - Optional responses
		scn.DSPass();

		assertTrue(scn.DSHasCardChoicesAvailable(trooper,bh_djas,bh_scout)); //select search party members
		scn.DSChooseCards(trooper,bh_djas,bh_scout);
		//search party destiny total: 0 + 2 non-scout characters + 2 from one scout character + 2 for bounty hunters = 6, success
		scn.PassAllResponses();

		assertFalse(rebelTrooper.isMissing()); //test1
		assertTrue(scn.DSCaptureDecisionAvailable());
	}

	@Test
	public void HuntingPartyBountyHuntersOutsideSearchPartyDoNotAddToSearchPartyDestiny() {
		//test1: check that a bounty hunter at site but not selected for search party does not add
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper = scn.GetLSFiller(1);

		var trooper = scn.GetDSFiller(1);
		var bh_djas = scn.GetDSCard("bh_djas");
		var bh_4lom = scn.GetDSCard("bh_4lom");
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLocation(site,trooper,bh_djas,bh_4lom,rebelTrooper);
		scn.MakeCardGoMissing(rebelTrooper);

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(hunting);
		scn.DSChooseCard(site);
		scn.PrepareDSDestiny(2);
		scn.LSPass(); //Playing Hunting Party - Optional responses
		scn.DSPass();

		assertTrue(scn.DSHasCardChoicesAvailable(trooper,bh_djas,bh_4lom)); //select search party members
		scn.DSChooseCards(trooper,bh_djas);
		//search party destiny total: 2 + 2 character + 1 for bounty hunters = 5, fail
		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(rebelTrooper.isMissing()); //test1
		assertTrue(scn.CardsAtLocation(site,rebelTrooper));
		assertFalse(rebelTrooper.isCaptive());
	}

	@Test
	public void HuntingPartyPreventsMembersFromParticipatingInSearchPartiesThisTurn() {
		//test1: modifiers for participating in the search party apply
		//this also indirectly also confirms members are not able to battle or move this turn,
		// since all 3 modifiers are applied together in SearchPartyAction
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper = scn.GetLSFiller(1);

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var trooper3 = scn.GetDSFiller(3);
		var trooper4 = scn.GetDSFiller(4);
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLocation(site,trooper1,trooper2,trooper3,trooper4,rebelTrooper);
		scn.MakeCardGoMissing(rebelTrooper);
		scn.MakeCardGoMissing(trooper3);

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(hunting);
		scn.DSChooseCard(site);
		scn.PrepareDSDestiny(4);
		scn.LSPass(); //Playing Hunting Party - Optional responses
		scn.DSPass();

		assertTrue(scn.DSHasCardChoicesAvailable(trooper1,trooper2,trooper4)); //select search party members
		scn.DSChooseCard(trooper1);
		//search party destiny total: 4 + 1 character = 5, failed
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(rebelTrooper.isMissing());

		scn.LSPass();

		assertTrue(scn.DSCardActionAvailable(site,"Form search party")); //test1

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(3);

		assertFalse(scn.DSHasCardChoicesAvailable(trooper1)); //test1: already in a search party this turn
		assertTrue(scn.DSHasCardChoicesAvailable(trooper2));
		assertTrue(scn.DSHasCardChoicesAvailable(trooper4));
	}

	@Test
	public void HuntingPartyParticipatingInSearchPartyThisTurnPreventsJoiningHuntingParty() {
		//test1: modifiers for participating in the search party apply
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper = scn.GetLSFiller(1);

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var trooper3 = scn.GetDSFiller(3);
		var trooper4 = scn.GetDSFiller(4);
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLocation(site,trooper1,trooper2,trooper3,trooper4,rebelTrooper);
		scn.MakeCardGoMissing(rebelTrooper);
		scn.MakeCardGoMissing(trooper3);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardActionAvailable(site,"Form search party")); //test1

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(3);

		assertTrue(scn.DSHasCardChoicesAvailable(trooper1,trooper2,trooper4)); //select search party members
		scn.DSChooseCard(trooper1);
		//search party destiny total: 3 + 1 character = 4, failed
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());

		scn.LSPass();
		scn.DSPlayCard(hunting);
		scn.DSChooseCard(site);
		scn.PrepareDSDestiny(4);
		scn.LSPass(); //Playing Hunting Party - Optional responses
		scn.DSPass();

		assertFalse(scn.DSHasCardChoicesAvailable(trooper1)); //test1: already in a search party this turn
		assertTrue(scn.DSHasCardChoicesAvailable(trooper2));
		assertTrue(scn.DSHasCardChoicesAvailable(trooper4));
	}

	@Test
	public void HuntingPartyBountyHuntersNotInSearchPartyDoNotAdd1InOtherSearchPartiesThisTurn() {
		//test1: check that any search party destiny modifiers for bounty hunters that affected the search party during
		// Hunting Party do not carry over into other search party actions
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper = scn.GetLSFiller(1);

		var trooper1 = scn.GetDSFiller(1);
		var trooper2 = scn.GetDSFiller(2);
		var bh_djas = scn.GetDSCard("bh_djas");
		var bh_4lom = scn.GetDSCard("bh_4lom");
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLocation(site,trooper1,trooper2,bh_djas,bh_4lom,rebelTrooper);
		scn.MakeCardGoMissing(rebelTrooper);
		scn.MakeCardGoMissing(trooper2);

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(hunting);
		scn.DSChooseCard(site);
		scn.PrepareDSDestiny(0);
		scn.LSPass(); //Playing Hunting Party - Optional responses
		scn.DSPass();

		assertTrue(scn.DSHasCardChoicesAvailable(bh_djas,bh_4lom)); //select search party members
		scn.DSChooseCard(bh_djas);
		//search party destiny total: 0 + 1 character + 1 bounty hunter = 2, failed
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(rebelTrooper.isMissing());

		scn.LSPass();

		assertTrue(scn.DSCardActionAvailable(site,"Form search party")); //test1

		scn.DSUseCardAction(site,"Form search party");

		scn.PrepareDSDestiny(4);

		assertFalse(scn.DSHasCardChoicesAvailable(bh_djas)); //already was in Hunting Party search party this turn
		assertTrue(scn.DSHasCardChoicesAvailable(bh_4lom));

		scn.DSChooseCard(bh_4lom);
		//search party destiny total: 4 + 1 character = 5, failed
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertTrue(rebelTrooper.isMissing()); //test1
	}

	@Test
	public void HuntingPartyCanBeTargetedBySense() {
		//show optional Sense response available after site is selected (before search party members are selected)
		var scn = GetScenario();

		var site = scn.GetDSStartingLocation();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var sense_ls = scn.GetLSCard("sense_ls");

		var trooper = scn.GetDSFiller(1);
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);

		scn.MoveCardsToLSHand(sense_ls);

		scn.MoveCardsToLocation(site,trooper,rebelTrooper1,rebelTrooper2);
		scn.MakeCardGoMissing(rebelTrooper1);

		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(hunting);
		assertTrue(scn.DSHasCardChoicesAvailable(site));
		scn.DSChooseCard(site);

		//LS: Playing •Hunting Party - Optional responses
		assertTrue(scn.LSCardPlayAvailable(sense_ls));
	}

	@Test
	public void HuntingPartyCanHaveSiteRetargetedByIHaveABadFeelingAboutThis() {
		//test1: shows I Have A Bad Feeling About This can be played to correctly re-target
		//	the selected search party site to a different (valid site on same side of the force)
		var scn = GetScenario();

		var site1 = scn.GetDSStartingLocation();
		var site2 = scn.GetLSStartingLocation();
		var site3 = scn.GetLSCard("site3");
		var site4 = scn.GetDSCard("site4");

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var rebelTrooper3 = scn.GetLSFiller(3);
		var rebelTrooper4 = scn.GetLSFiller(4);
		var bad_feeling = scn.GetLSCard("bad_feeling");

		var trooper1A = scn.GetDSFiller(1);
		var trooper1B = scn.GetDSFiller(2);
		var trooper2A = scn.GetDSFiller(3);
		var trooper2B = scn.GetDSFiller(4);
		var trooper3A = scn.GetDSFiller(5);
		var trooper3B = scn.GetDSFiller(6);
		var trooper4A = scn.GetDSFiller(7);
		var trooper4B = scn.GetDSFiller(8);
		var hunting = scn.GetDSCard("hunting");

		scn.StartGame();

		scn.MoveCardsToDSHand(hunting);
		scn.MoveCardsToLSHand(bad_feeling);

		scn.MoveCardsToLocation(site1,trooper1A,trooper1B,rebelTrooper1);
		scn.MakeCardGoMissing(rebelTrooper1);

		scn.MoveCardsToLocation(site2,trooper2A,trooper2B,rebelTrooper2);
		scn.MakeCardGoMissing(rebelTrooper2);

		scn.MoveLocationToTable(site3);
		scn.MoveCardsToLocation(site3,trooper3A,trooper3B,rebelTrooper3);
		scn.MakeCardGoMissing(rebelTrooper3);

		scn.MoveLocationToTable(site4);
		scn.MoveCardsToLocation(site4,trooper4A,trooper4B,rebelTrooper4);
		scn.MakeCardGoMissing(rebelTrooper4);

		scn.LSActivateForceCheat(3); //enough to play bad_feeling

		scn.SkipToPhase(Phase.CONTROL);

		scn.PrepareDSDestiny(5);
		scn.DSPlayCard(hunting);
		assertTrue(scn.DSHasCardChoicesAvailable(site1,site2,site4));
		scn.DSChooseCard(site1);

		//Playing Hunting Party - Optional responses
		assertTrue(scn.LSCardPlayAvailable(bad_feeling));
		scn.LSPlayCard(bad_feeling);

		//Choose card (or card in group) to re-target from, or click 'Done' to cancel
		assertTrue(scn.LSHasCardChoiceAvailable(site1)); //original selection
		assertFalse(scn.LSHasCardChoicesAvailable(site2,site3,site4));
		scn.LSChooseCard(site1);

		//Choose card (or card in group) to re-target from, or click 'Done' to cancel
		assertFalse(scn.LSHasCardChoiceAvailable(site1)); //must retarget somewhere different
		assertFalse(scn.LSHasCardChoiceAvailable(site2)); //can't retarget to LS site, since original was DS site and must be "on the same side of the force"
		assertFalse(scn.LSHasCardChoiceAvailable(site3)); //can't retarget to LS site
		assertTrue(scn.LSHasCardChoiceAvailable(site4)); //DS site that meets criteria
		scn.LSChooseCard(site4);

		scn.PassAllResponses();

		assertTrue(scn.DSDecisionAvailable("Choose members of search party"));
		assertFalse(scn.DSHasCardChoicesAvailable(trooper1A,trooper1B)); //these are at site1 (the original target)
		assertFalse(scn.DSHasCardChoicesAvailable(trooper2A,trooper2B)); //these are at site2
		assertFalse(scn.DSHasCardChoicesAvailable(trooper3A,trooper3B)); //these are at site3
		assertTrue(scn.DSHasCardChoicesAvailable(trooper4A,trooper4B)); //these are at site4 (the new target)
		scn.DSChooseCard(trooper4A);

		scn.PassAllResponses();
		assertTrue(rebelTrooper1.isMissing());
		assertTrue(rebelTrooper2.isMissing());
		assertTrue(rebelTrooper3.isMissing());
		assertFalse(rebelTrooper4.isMissing()); //test1: found at the new targeted site
	}
}
