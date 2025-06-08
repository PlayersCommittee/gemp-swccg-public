package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.GoMissingEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.*;

public class Card_5_106_Tests {
	protected VirtualTableScenario GetScenario() throws DecisionResultInvalidException {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("chewie", "2_3");
					put("han", "108_1");
					put("leia", "6_32");
					put("kabe", "1_14");
					put("arm", "4_15");
				}},
				new HashMap<>()
				{{
					put("binders", "5_106");
					put("boba", "5_91");
					put("bobas_blaster", "5_179");
					put("ig88", "109_11"); //with riot gun
					put("bib", "6_98"); //not a warrior or bounty hunter
					put("allyours", "5_144"); //He's All Yours, Bounty Hunter
					put("ungrateful", "5_161"); //Weapon of an Ungrateful Son
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
	public void BindersStatsAndKeywordsAreCorrect() throws DecisionResultInvalidException {
		/**
		 * Title: Binders
		 * Uniqueness: Unrestricted
		 * Side: Dark
		 * Type: Device
		 * Destiny: 6
		 * Game Text: Deploy on one of your warriors or bounty hunters. May now escort any number of captives.
		 * 			If device removed from your character, select one captive escorted by that character to remain and release all others.
		 * Lore: Because standard binders are durable but not easily adaptable, bounty hunters often carry special
		 * 			binders which automatically tighten around a captive's appendages.
		 * Set: Cloud City
		 * Rarity: C
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("binders").getBlueprint();

		assertEquals("Binders", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertTrue(card.isCardType(CardType.DEVICE));
		assertEquals(6, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.CLOUD_CITY));
	}

	@Test
	public void BindersDeploysOnAWarriorOrBountyHunter() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);
		var ig88 = scn.GetDSCard("ig88");
		var bib = scn.GetDSCard("bib");
		var binders = scn.GetDSCard("binders");
		scn.MoveCardsToHand(binders);

		scn.StartGame();

		scn.MoveCardsToLocation(site, stormtrooper, ig88, bib);

		scn.SkipToPhase(Phase.DEPLOY);

		assertTrue(scn.DSDeployAvailable(binders));
		scn.DSDeployCard(binders);

		//Warrior, valid target
		assertTrue(scn.DSHasCardChoiceAvailable(stormtrooper));
		//Bounty hunter, valid target
		assertTrue(scn.DSHasCardChoiceAvailable(ig88));
		//Neither, invalid target
		assertFalse(scn.DSHasCardChoiceAvailable(bib));
	}

	@Test
	public void BindersPermitsBearerToEscortMultipleCaptives() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var han = scn.GetLSCard("han");

		var site = scn.GetLSStartingLocation();

		var ig88 = scn.GetDSCard("ig88");
		var binders = scn.GetDSCard("binders");

		scn.StartGame();
		scn.MoveCardsToLocation(site, han, chewie, ig88);
		scn.AttachCardsTo(ig88, binders);
		scn.CaptureCardWith(ig88, chewie);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.PassBattleStartResponses();
		assertTrue(scn.AwaitingDSWeaponsSegmentActions());

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertFalse(han.isCaptive());
		assertNull(han.getEscort());
		assertNull(han.getAttachedTo());

		scn.PrepareDSDestiny(7);
		scn.DSUseCardAction(ig88);
		//Targeting
		scn.DSChooseCard(han);
		scn.PassWeaponFireWithDestinyDraw();
		scn.PassAllResponses();
		scn.DSChooseSeizeCaptive();

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertTrue(han.isCaptive());
		assertEquals(ig88, han.getEscort());
		assertEquals(ig88, han.getAttachedTo());
	}

	@Test
	public void BindersPermitsMultipleCaptivesToBeTransferredToBearer() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var han = scn.GetLSCard("han");

		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);
		var ig88 = scn.GetDSCard("ig88");
		var binders = scn.GetDSCard("binders");
		var allyours = scn.GetDSCard("allyours");

		scn.StartGame();

		scn.MoveCardsToHand(allyours);
		scn.MoveCardsToLocation(site, han, chewie, stormtrooper, ig88);
		scn.AttachCardsTo(ig88, binders);
		scn.CaptureCardWith(ig88, chewie);
		scn.CaptureCardWith(stormtrooper, han);

		scn.SkipToPhase(Phase.MOVE);

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());
		assertTrue(han.isCaptive());
		assertEquals(stormtrooper, han.getEscort());
		assertEquals(stormtrooper, han.getAttachedTo());

		assertTrue(scn.DSCardPlayAvailable(allyours));
		scn.DSPlayCard(allyours);

		assertTrue(scn.DSDecisionAvailable("Choose new escort"));
		//Has binders
		assertTrue(scn.DSHasCardChoiceAvailable(ig88));
		//Does not have binders
		assertFalse(scn.DSHasCardChoiceAvailable(stormtrooper));

		scn.DSChooseCard(ig88);
		scn.DSChooseCard(han);
		scn.PassAllResponses();

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());
		assertTrue(han.isCaptive());
		assertEquals(ig88, han.getAttachedTo());
		assertEquals(ig88, han.getEscort());
	}

	@Test
	public void BindersWhenRemovedCauseAllBut1CaptiveToBeReleasedFromBearer() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var han = scn.GetLSCard("han");
		var leia = scn.GetLSCard("leia");

		var site = scn.GetLSStartingLocation();

		var ig88 = scn.GetDSCard("ig88");
		var binders = scn.GetDSCard("binders");
		var ungrateful = scn.GetDSCard("ungrateful");

		scn.StartGame();

		scn.MoveCardsToHand(ungrateful);
		scn.MoveCardsToLocation(site, han, chewie, leia, ig88);
		scn.AttachCardsTo(ig88, binders);
		scn.CaptureCardWith(ig88, chewie);
		scn.CaptureCardWith(ig88, han);
		scn.CaptureCardWith(ig88, leia);

		scn.SkipToPhase(Phase.CONTROL);

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());
		assertTrue(han.isCaptive());
		assertEquals(ig88, han.getEscort());
		assertEquals(ig88, han.getAttachedTo());
		assertTrue(leia.isCaptive());
		assertEquals(ig88, leia.getEscort());
		assertEquals(ig88, leia.getAttachedTo());

		assertTrue(scn.DSPlayLostInterruptAvailable(ungrateful));
		scn.DSPlayLostInterrupt(ungrateful);
		scn.DSChooseCard(binders);
		scn.PassAllResponses();

		assertTrue(scn.DSDecisionAvailable("Select one captive to remain (all others on this escort will be released)"));
		assertTrue(scn.DSHasCardChoicesAvailable(han, chewie, leia));

		scn.DSChooseCard(chewie);
		assertTrue(scn.DSDecisionAvailable("Choose character to release"));
		assertFalse(scn.DSHasCardChoicesAvailable(chewie));
		scn.DSChooseCard(han);

		scn.LSChooseRally();
		scn.PassAllResponses();

		scn.LSChooseRally();
		scn.PassAllResponses();

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());

		assertFalse(han.isCaptive());
		assertNull(han.getAttachedTo());
		assertNull(han.getEscort());
		assertAtLocation(site, han);

		assertFalse(leia.isCaptive());
		assertNull(leia.getAttachedTo());
		assertNull(leia.getEscort());
		assertAtLocation(site, leia);
	}

	@Test
	public void BindersWhenLostCauseAllBut1CaptiveToBeReleasedFromBearer() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var han = scn.GetLSCard("han");
		var leia = scn.GetLSCard("leia");

		var site = scn.GetLSStartingLocation();

		var ig88 = scn.GetDSCard("ig88");
		var binders = scn.GetDSCard("binders");

		scn.StartGame();

		scn.MoveCardsToLocation(site, han, chewie, leia, ig88);
		scn.AttachCardsTo(ig88, binders);
		scn.CaptureCardWith(ig88, chewie);
		scn.CaptureCardWith(ig88, han);
		scn.CaptureCardWith(ig88, leia);

		scn.SkipToPhase(Phase.CONTROL);

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());
		assertTrue(han.isCaptive());
		assertEquals(ig88, han.getEscort());
		assertEquals(ig88, han.getAttachedTo());
		assertTrue(leia.isCaptive());
		assertEquals(ig88, leia.getEscort());
		assertEquals(ig88, leia.getAttachedTo());

		//Since Sabotage isn't coded, we will cheat an ability on IG-88 that causes Binders
		// to be immediately lost.
		scn.DSExecuteAdHocEffect(ig88, new LoseCardFromTableEffect(new TopLevelGameTextAction(ig88, ig88.getCardId()), binders));
		scn.PassAllResponses();

		assertTrue(scn.DSDecisionAvailable("Select one captive to remain (all others on this escort will be released)"));
		assertTrue(scn.DSHasCardChoicesAvailable(han, chewie, leia));

		scn.DSChooseCard(chewie);
		assertTrue(scn.DSDecisionAvailable("Choose character to release"));
		assertFalse(scn.DSHasCardChoicesAvailable(chewie));
		scn.DSChooseCard(han);

		scn.LSChooseRally();
		scn.PassAllResponses();

		scn.LSChooseRally();
		scn.PassAllResponses();

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());

		assertFalse(han.isCaptive());
		assertNull(han.getAttachedTo());
		assertNull(han.getEscort());
		assertAtLocation(site, han);

		assertFalse(leia.isCaptive());
		assertNull(leia.getAttachedTo());
		assertNull(leia.getEscort());
		assertAtLocation(site, leia);
	}

	@Test
	public void BindersWhenStolenCauseAllBut1CaptiveToBeReleasedFromBearer() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var han = scn.GetLSCard("han");
		var leia = scn.GetLSCard("leia");
		var kabe = scn.GetLSCard("kabe");

		var site = scn.GetLSStartingLocation();

		var ig88 = scn.GetDSCard("ig88");
		var binders = scn.GetDSCard("binders");

		scn.StartGame();

		scn.MoveCardsToLocation(site, han, chewie, leia, kabe, ig88);
		scn.AttachCardsTo(ig88, binders);
		scn.CaptureCardWith(ig88, chewie);
		scn.CaptureCardWith(ig88, han);
		scn.CaptureCardWith(ig88, leia);

		scn.SkipToLSTurn(Phase.CONTROL);

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());
		assertTrue(han.isCaptive());
		assertEquals(ig88, han.getEscort());
		assertEquals(ig88, han.getAttachedTo());
		assertTrue(leia.isCaptive());
		assertEquals(ig88, leia.getEscort());
		assertEquals(ig88, leia.getAttachedTo());

		assertTrue(scn.LSCardActionAvailable(kabe, "Steal weapon or device"));
		scn.PrepareLSDestiny(0);
		scn.LSUseCardAction(kabe);
		scn.LSChooseCard(binders);
		scn.PassAllResponses();

		assertTrue(scn.DSDecisionAvailable("Select one captive to remain (all others on this escort will be released)"));
		assertTrue(scn.DSHasCardChoicesAvailable(han, chewie, leia));

		scn.DSChooseCard(chewie);
		assertTrue(scn.LSDecisionAvailable("Choose character to release"));
		assertFalse(scn.LSHasCardChoicesAvailable(chewie));
		scn.LSChooseCard(han);

		scn.LSChooseRally();
		scn.PassAllResponses();

		scn.LSChooseRally();
		scn.PassAllResponses();

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());

		assertFalse(han.isCaptive());
		assertNull(han.getAttachedTo());
		assertNull(han.getEscort());
		assertAtLocation(site, han);

		assertFalse(leia.isCaptive());
		assertNull(leia.getAttachedTo());
		assertNull(leia.getEscort());
		assertAtLocation(site, leia);
	}

	@Test
	public void BindersWhenTransferredCauseAllBut1CaptiveToBeReleasedFromBearer() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var han = scn.GetLSCard("han");
		var leia = scn.GetLSCard("leia");

		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);
		var ig88 = scn.GetDSCard("ig88");
		var binders = scn.GetDSCard("binders");

		scn.StartGame();

		scn.MoveCardsToLocation(site, han, chewie, leia, stormtrooper, ig88);
		scn.AttachCardsTo(ig88, binders);
		scn.CaptureCardWith(ig88, chewie);
		scn.CaptureCardWith(ig88, han);
		scn.CaptureCardWith(ig88, leia);

		scn.SkipToPhase(Phase.DEPLOY);

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());
		assertTrue(han.isCaptive());
		assertEquals(ig88, han.getEscort());
		assertEquals(ig88, han.getAttachedTo());
		assertTrue(leia.isCaptive());
		assertEquals(ig88, leia.getEscort());
		assertEquals(ig88, leia.getAttachedTo());

		assertTrue(scn.DSTransferAvailable(binders));
		scn.DSTransferCard(binders);
		scn.DSChooseCard(stormtrooper); //Moving binders to the stormtrooper

		assertTrue(scn.DSDecisionAvailable("Select one captive to remain (all others on this escort will be released)"));
		assertTrue(scn.DSHasCardChoicesAvailable(han, chewie, leia));

		scn.DSChooseCard(chewie);
		assertTrue(scn.DSDecisionAvailable("Choose character to release"));
		assertFalse(scn.DSHasCardChoicesAvailable(chewie));
		scn.DSChooseCard(han);

		scn.LSChooseRally();
		scn.PassAllResponses();

		scn.LSChooseRally();
		scn.PassAllResponses();

		assertEquals(stormtrooper, binders.getAttachedTo());

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());

		assertFalse(han.isCaptive());
		assertNull(han.getAttachedTo());
		assertNull(han.getEscort());
		assertAtLocation(site, han);

		assertFalse(leia.isCaptive());
		assertNull(leia.getAttachedTo());
		assertNull(leia.getEscort());
		assertAtLocation(site, leia);
	}

	@Test
	public void BindersDoesNotOfferChoiceIfOnly1CaptiveOnEscortWhenTransferred() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");

		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);
		var ig88 = scn.GetDSCard("ig88");
		var binders = scn.GetDSCard("binders");

		scn.StartGame();

		scn.MoveCardsToLocation(site, chewie, stormtrooper, ig88);
		scn.AttachCardsTo(ig88, binders);
		scn.CaptureCardWith(ig88, chewie);

		scn.SkipToPhase(Phase.DEPLOY);

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());

		assertTrue(scn.DSTransferAvailable(binders));
		scn.DSTransferCard(binders);
		scn.DSChooseCard(stormtrooper); //Moving binders to the stormtrooper
		scn.PassAllResponses();

		assertFalse(scn.DSDecisionAvailable("Select one captive to remain (all others on this escort will be released)"));

		assertTrue(chewie.isCaptive());
		assertEquals(ig88, chewie.getEscort());
		assertEquals(ig88, chewie.getAttachedTo());
	}

	@Test
	public void BindersDoesNotOfferChoiceIf0CaptivesOnEscortWhenTransferred() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);
		var ig88 = scn.GetDSCard("ig88");
		var binders = scn.GetDSCard("binders");

		scn.StartGame();

		scn.MoveCardsToLocation(site, stormtrooper, ig88);
		scn.AttachCardsTo(ig88, binders);

		scn.SkipToPhase(Phase.DEPLOY);

		assertTrue(scn.DSTransferAvailable(binders));
		scn.DSTransferCard(binders);
		scn.DSChooseCard(stormtrooper); //Moving binders to the stormtrooper
		scn.PassAllResponses();

		assertFalse(scn.DSDecisionAvailable("Select one captive to remain (all others on this escort will be released)"));
	}

}
