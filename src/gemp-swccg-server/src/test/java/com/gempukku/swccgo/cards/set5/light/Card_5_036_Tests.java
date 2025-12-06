package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.GoMissingEffect;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_5_036_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("fury", "5_36");
					put("chewie", "2_3");
					put("han", "108_1"); //Han with Heavy Blaster Pistol
					put("bowcaster", "8_86"); //weapon
					put("electrobinoculars", "1_35"); //device
					put("path", "5_62"); //relocates
					put("threepio", "1_5"); //droid
					put("slave_leia", "6_32"); //retrieves force once released
					put("wilderness", "1_047"); //for going missing
					put("blaster", "1_152");
					put("rook", "206_1"); //Bodhi Rook

					put("core_tunnel", "7_112"); //cloud city interior location
				}},
				new HashMap<>()
				{{
					put("boba", "5_91");
					put("bobas_blaster", "5_179");
					put("vader", "7_175");
					put("ig88", "109_11"); //does not provide presence to battle
					put("dengar", "205_23"); //adds battle destiny while escorting captive
					put("hidden_weapons", "6_154"); //captures mid-battle
					put("human_shield", "5_145");
					put("it-o", "2_93");
					put("detention_block", "1_284"); //Detention Block Corridor

					put("tube", "1_308"); //provides enclosure
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
	public void CaptiveFuryStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Captive Fury
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Interrupt
		 * Subtype: Used or Lost
		 * Destiny: 4
		 * Game Text: USED: Cancel Force drain bonus from IT-O this turn.
		 * 		LOST: During your battle phase, any of your escorted captives at same site may initiate and participate
		 * 		in one battle (they may not use weapons or devices and you may not voluntarily forfeit or relocate them).
		 * Lore: Chewie's life debt to Han forced him to act, retaliating unexpectedly against his captors.
		 * Set: Cloud City
		 * Rarity: U
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("fury").getBlueprint();

		assertEquals("Captive Fury", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertTrue(card.isCardType(CardType.INTERRUPT));
		assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
		assertEquals(4, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.CLOUD_CITY));
	}

	@Test
	public void CaptiveFuryInitializesBattleAndRelocatesCaptiveToLSSideOfSite() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);

		assertTrue(scn.LSDecisionAvailable("Choose escorted captives to battle"));
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();

		assertAtLocation(site, chewie);
		assertFalse(scn.IsAttachedTo(chewie, boba));
		assertTrue(scn.IsActiveBattle());
		assertTrue(scn.IsParticipatingInBattle(chewie));
		assertTrue(scn.IsParticipatingInBattle(boba));

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
	}

	@Test
	public void CaptiveFuryRequiresSufficientForceToPayForBattle() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.CONTROL);

		//Clear out the force pile so that there is nothing to pay for battle with
		for(var card : scn.GetLSForcePile().stream().toList()) {
			scn.MoveCardsToTopOfOwnReserveDeck((PhysicalCardImpl)card);
		}

		scn.SkipToPhase(Phase.BATTLE);
		assertFalse(scn.LSPlayLostInterruptAvailable(fury));
	}

	@Test
	public void CaptiveFuryCaptiveCannotUseWeapons() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		var bowcaster = scn.GetLSCard("bowcaster");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, chewie);
		scn.AttachCardsTo(chewie, bowcaster);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());

		assertFalse(scn.LSCardActionAvailable(bowcaster));
	}

	@Test
	public void CaptiveFuryCaptiveCannotUsePermanentWeapons() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var han = scn.GetLSCard("han");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, han);
		scn.CaptureCardWith(boba, han);

		scn.SkipToLSTurn(Phase.BATTLE);

		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(han);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());

		assertFalse(scn.LSCardActionAvailable(han));
	}

	@Test
	public void CaptiveFuryCaptiveCannotUseDevices() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		var eb = scn.GetLSCard("electrobinoculars");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, chewie);
		scn.AttachCardsTo(chewie, eb);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();

		assertFalse(scn.LSCardActionAvailable(eb));
	}

	@Test
	public void CaptiveFuryCaptiveCannotBeVoluntarilyForfeit() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(site, vader);
		scn.CaptureCardWith(vader, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PrepareLSDestiny(1);
		scn.PrepareDSDestiny(1);
		scn.SkipToDamageSegment(true);

		assertTrue(scn.DSWonBattle());
		//Vader drew destiny 1
		assertEquals(1, scn.GetUnpaidLSAttrition());
		// Vader 6 + destiny 1 > Chewbacca 6
		assertEquals(1, scn.GetUnpaidLSBattleDamage());
		assertTrue(scn.AwaitingLSAttritionPayment());
		assertTrue(scn.AwaitingLSBattleDamagePayment());
		assertFalse(chewie.isHit());
		assertFalse(scn.LSHasCardChoiceAvailable(chewie));

		var life = scn.GetLSLifeForceRemaining();
		scn.LSPayBattleDamageFromReserveDeck();
		assertEquals(life - 1, scn.GetLSLifeForceRemaining());

		assertFalse(scn.IsActiveBattle());
	}

	@Test
	public void CaptiveFuryCaptiveCanBeForfeitIfHitAndIsNotReturnedToEscort() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");
		var bobas_blaster = scn.GetDSCard("bobas_blaster");
		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, vader);
		scn.AttachCardsTo(boba, bobas_blaster);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();
		scn.LSPass();

		assertTrue(scn.DSCardActionAvailable(bobas_blaster));
		scn.DSUseCardAction(bobas_blaster);

		assertTrue(scn.DSDecisionAvailable("Choose target"));
		scn.DSChooseCard(chewie);
		scn.PassForceUseResponses();
		scn.PrepareDSDestiny(7);
		scn.PassWeaponFireWithDestinyDraw();

		assertTrue(chewie.isHit());
		//Boba's blaster can keep firing, we just want the once
		scn.DSChooseNo();

		scn.PrepareDSDestiny(1);
		scn.PrepareLSDestiny(1);
		scn.SkipToDamageSegment(true);

		assertTrue(scn.DSWonBattle());
		//DS drew destiny 1
		assertEquals(1, scn.GetUnpaidLSAttrition());
		// Vader 6 + Boba Fett 3 + destiny 1 + gun destiny 1 > Chewbacca 6
		assertEquals(5, scn.GetUnpaidLSBattleDamage());

		assertTrue(scn.AwaitingLSAttritionPayment());
		assertTrue(scn.AwaitingLSBattleDamagePayment());
		assertTrue(chewie.isHit());
		assertTrue(scn.LSHasCardChoiceAvailable(chewie));

		int life = scn.GetLSLifeForceRemaining();
		scn.LSPayBattleDamageFromCardInPlay(chewie);
		assertFalse(scn.IsActiveBattle()); // battle damage was satisfied
		assertEquals(life, scn.GetLSLifeForceRemaining()); //killing chewie meant nothing was paid from life

		//Dead Chewie was not auto-positioned back on captor
		assertInZone(Zone.LOST_PILE, chewie);
		assertNull(chewie.getEscort());
		assertEquals(0, boba.getCardsEscorting().size());

		//We are back in the regular action procedure and all ripples of Captive Fury have settled down
		assertTrue(scn.AwaitingDSBattlePhaseActions());
		assertInZone(Zone.LOST_PILE, fury);
	}

	@Test
	public void CaptiveFuryCaptiveCannotBeRelocated() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		var path = scn.GetLSCard("path");
		var core_tunnel = scn.GetLSCard("core_tunnel");
		scn.MoveCardsToHand(fury, path, core_tunnel);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployLocation(core_tunnel);
		scn.PassCardPlayResponses();
		scn.SkipToPhase(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();

		assertFalse(scn.LSCardPlayAvailable(path));
	}

	@Test
	public void CaptiveFuryRelocatesCaptiveEvenIfEnclosedInVehicle() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");
		var tube= scn.GetDSCard("tube");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, tube);
		scn.CaptureCardWith(boba, chewie);
		scn.BoardAsPassenger(tube, boba);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.IsAboardAsPassenger(tube, boba));
		//Normally it can take 4 passengers, but the captive on boba takes a slot as well.
		assertEquals(2, scn.GetPassengerCapacity(tube));

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);

		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();

		assertFalse(scn.IsAboardAsPassenger(tube, chewie));
		assertAtLocation(site, chewie);
		assertFalse(scn.IsAttachedTo(chewie, boba));
		assertEquals(3, scn.GetPassengerCapacity(tube));
	}

	@Test
	public void CaptiveFuryEjectsEscortWhenRelocatingCaptiveIfNoRoomInVehicle() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");
		var tube= scn.GetDSCard("tube");
		var troopers = scn.GetDSFillerRange(3);

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, tube);
		scn.MoveCardsToLocation(troopers.get(0), troopers.get(1), troopers.get(2));
		scn.CaptureCardWith(boba, chewie);
		scn.BoardAsPassenger(tube, boba);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.IsAboardAsPassenger(tube, boba));
		assertTrue(chewie.isCaptive());
		assertEquals(boba, chewie.getEscort());
		//Normally it can take 4 passengers, but the captive on boba takes a slot as well.
		assertEquals(2, scn.GetPassengerCapacity(tube));

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);

		scn.PassCardAndForceUseResponses();

		assertFalse(scn.IsAboardAsPassenger(tube, chewie));
		assertAtLocation(site, chewie);
		assertFalse(scn.IsAttachedTo(chewie, boba));
		assertTrue(scn.IsActiveBattle());

		//Only Boba in the tube
		assertEquals(3, scn.GetPassengerCapacity(tube));

		scn.BoardAsPassenger(tube, troopers.get(0), troopers.get(1), troopers.get(2));
		assertEquals(0, scn.GetPassengerCapacity(tube));

		scn.SkipToDamageSegment(false);
		scn.DSPayBattleDamageFromReserveDeck(6);
		assertFalse(scn.IsActiveBattle());

		assertFalse(scn.IsAttachedTo(boba, chewie));
		assertAtLocation(site, chewie);

		// The tube was too crammed with passengers, so Boba was forced out so he could escort Chewie again
		scn.PassResponses("DISEMBARKING");
		scn.PassResponses("DISEMBARKED");
		assertTrue(scn.IsAboardAsPassenger(tube, troopers.get(0), troopers.get(1), troopers.get(2)));
		assertFalse(scn.IsAboardAsPassenger(tube, boba, chewie));
		assertAtLocation(site, boba);

		assertTrue(scn.IsAttachedTo(boba, chewie));
		assertNotAtLocation(site, chewie);
	}

	@Test
	public void CaptiveFuryDoesNotTriggerRelease() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var leia = scn.GetLSCard("slave_leia");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba);
		scn.CaptureCardWith(boba, leia);
		scn.MoveCardsToTopOfLSLostPile(scn.GetLSFillerRange(6));

		scn.SkipToLSTurn(Phase.BATTLE);

		assertEquals(6, scn.GetLSLostPileCount());
		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(leia);
		scn.PassCardAndForceUseResponses();

		assertTrue(scn.DSDecisionAvailable("BATTLE_INITIATED"));
		assertEquals(6, scn.GetLSLostPileCount()); // Leia did not retrieve 5 Force
	}

	@Test
	public void CaptiveFuryIncludesNonCaptivesInBattleIfEligible() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		var trooper = scn.GetLSFiller(1);
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");
		var stormtrooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, trooper, stormtrooper);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.SkipToEndOfPowerSegment(false);

		assertTrue(scn.IsActiveBattle());
		assertTrue(scn.IsParticipatingInBattle(boba, stormtrooper));
		assertTrue(scn.IsParticipatingInBattle(chewie, trooper));

		// Chewie 6 + Rebel Trooper 1
		assertEquals(7, scn.GetLSTotalPower());
		// Boba 4 + Stormtrooper 1
		assertEquals(5, scn.GetDSTotalPower());
	}

	@Test
	public void CaptiveFuryCannotBeUsedOnCaptiveThatBattledEarlierThisTurn() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var city = scn.GetLSStartingLocation();
		var marketplace = scn.GetDSStartingLocation();

		var boba = scn.GetDSCard("boba");
		var stormtrooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(city, boba);
		scn.MoveCardsToLocation(marketplace, stormtrooper, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSInitiateBattle(marketplace);
		scn.SkipToDamageSegment(false);
		scn.DSPayRemainingBattleDamageFromReserveDeck();

		assertFalse(scn.IsActiveBattle());

		scn.MoveCardsToLocation(city, chewie);
		scn.CaptureCardWith(boba, chewie);
		assertTrue(chewie.isCaptive());
		assertSame(boba, chewie.getEscort());

		scn.DSPass();

		assertTrue(scn.AwaitingLSBattlePhaseActions());

		//As Chewie has already participated in battle, he is not an eligible target for Captive Fury.
		assertInZone(Zone.HAND,  fury);
		assertEquals(2, scn.GetLSForcePileCount());
		assertFalse(scn.LSPlayLostInterruptAvailable(fury));
	}

	@Test
	public void CaptiveFuryCancelsBattleIfCaptiveHasNoPresence() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var threepio = scn.GetLSCard("threepio");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba);
		scn.CaptureCardWith(boba, threepio);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(threepio);
		scn.PassCardAndForceUseResponses();

		assertTrue(scn.IsActiveBattle());
		scn.PassBattleStartResponses();

		assertFalse(scn.IsActiveBattle());
	}

	@Test
	public void CaptiveFuryCancelsBattleIfCaptorHasNoPresence() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var ig88 = scn.GetDSCard("ig88");

		scn.StartGame();

		scn.MoveCardsToLocation(site, ig88);
		scn.CaptureCardWith(ig88, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();

		assertTrue(scn.IsActiveBattle());
		scn.PassBattleStartResponses();

		assertFalse(scn.IsActiveBattle());
	}

	@Test
	public void CaptiveFuryReturnsCaptiveToCaptorAfterBattleIfBothAlive() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(site, vader);
		scn.CaptureCardWith(vader, chewie);

		assertTrue(chewie.isCaptive());

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);

		assertTrue(chewie.isCaptive());
		scn.LSChooseCard(chewie);
		assertTrue(chewie.isCaptive());

		scn.PassCardAndForceUseResponses();
		scn.PrepareLSDestiny(1);
		scn.PrepareDSDestiny(1);
		scn.SkipToDamageSegment(true);

		assertTrue(scn.DSWonBattle());
		scn.LSPayBattleDamageFromReserveDeck();
		assertFalse(scn.IsActiveBattle());

		assertTrue(chewie.isCaptive());
		assertTrue(scn.IsAttachedTo(vader, chewie));
	}

	@Test
	public void CaptiveFuryReleasesBattlingCaptiveIfCaptorIsForfeit() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PrepareDSDestiny(1);
		scn.PrepareLSDestiny(1);
		scn.SkipToDamageSegment(true);

		assertTrue(scn.LSWonBattle());
		scn.DSPayBattleDamageFromCardInPlay(boba);
		assertFalse(scn.IsActiveBattle());

		assertTrue(scn.LSReleaseDecisionAvailable());
		scn.LSChooseRally();

		assertFalse(chewie.isCaptive());
		assertFalse(scn.IsAttachedTo(boba, chewie));
		assertInZone(Zone.LOST_PILE,  boba);
	}

	@Test
	public void CaptiveFuryReleasesBattlingCaptiveIfCaptorIsMissing() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba);
		scn.CaptureCardWith(boba, chewie);

		assertEquals(boba, chewie.getEscort());
		assertTrue(boba.getCardsEscorting().contains(chewie));

		scn.SkipToLSTurn(Phase.BATTLE);
		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();

		assertTrue(chewie.isCaptive());
		assertEquals(boba, chewie.getEscort());
		assertTrue(boba.getCardsEscorting().contains(chewie));
		assertNull(chewie.getAttachedTo());
		assertTrue(scn.CardsAtLocation(site, chewie));
		assertTrue(scn.IsActiveBattle());

		// Since all the "go missing" cards suck to execute, we're gonna make our own.
		scn.LSExecuteAdHocEffect(site, new GoMissingEffect(new TopLevelGameTextAction(site, scn.LS, site.getCardId()), boba));
		assertTrue(scn.LSReleaseDecisionAvailable());
		scn.LSChooseRally();
		scn.PassAllResponses();

		assertFalse(chewie.isCaptive());
		assertNull(chewie.getEscort());
		assertFalse(boba.getCardsEscorting().contains(chewie));
		assertNull(chewie.getAttachedTo());
		assertTrue(scn.CardsAtLocation(site, chewie));
		assertFalse(scn.IsActiveBattle());
	}

	@Test
	public void CaptiveFuryDoesNotReturnCaptiveAfterBattleIfMissing() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);
		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();

		assertTrue(chewie.isCaptive());
		assertEquals(boba, chewie.getEscort());
		assertTrue(boba.getCardsEscorting().contains(chewie));
		assertNull(chewie.getAttachedTo());
		assertTrue(scn.CardsAtLocation(site, chewie));
		assertTrue(scn.IsActiveBattle());

		scn.LSPass();
		// Since all the "go missing" cards suck to execute, we're gonna make our own.
		scn.DSExecuteAdHocEffect(boba, new GoMissingEffect(new TopLevelGameTextAction(boba, boba.getCardId()), chewie));
		scn.PassAllResponses();

		assertFalse(chewie.isCaptive());
		assertTrue(chewie.isMissing());
		assertNull(chewie.getEscort());
		assertFalse(boba.getCardsEscorting().contains(chewie));
		assertNull(chewie.getAttachedTo());
		assertTrue(scn.CardsAtLocation(site, chewie));
		assertFalse(scn.IsActiveBattle());
	}

	@Test
	public void CaptiveFuryDoesNotReturnCaptiveIfCapturedByAnotherCaptor() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");
		var vader = scn.GetDSCard("vader");
		var hidden_weapons = scn.GetDSCard("hidden_weapons");
		scn.MoveCardsToDSHand(hidden_weapons);

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, vader);
		scn.CaptureCardWith(vader, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();
		scn.LSPass();

		assertTrue(scn.DSCardPlayAvailable(hidden_weapons));
		scn.PrepareDSDestiny(3); // 2-3 will result in capture
		scn.DSPlayCard(hidden_weapons);

		assertTrue(chewie.isCaptive());
		assertEquals(vader, chewie.getEscort());
		assertTrue(vader.getCardsEscorting().contains(chewie));
		assertNull(chewie.getAttachedTo());
		assertTrue(scn.CardsAtLocation(site, chewie));
		assertTrue(scn.IsActiveBattle());

		assertTrue(scn.DSDecisionAvailable("Choose character"));
		scn.DSChooseCard(chewie);
		scn.PassCardPlayResponses();
		scn.PassDestinyDrawResponses();

		scn.PassResponses("ABOUT_TO_BE_CAPTURED");
		assertTrue(scn.DSCaptureDecisionAvailable());
		scn.DSChooseSeizeCaptive();

		assertTrue(scn.DSDecisionAvailable("Choose escort for"));
		scn.DSChooseCard(boba);
		scn.PassResponses("CAPTURED");
		scn.PassCardLeavingTable(); //Hidden Weapons discard

		//Chewie was recaptured, meaning that the battle is both canceled and he does not revert to the
		// custody of his previous captor
		assertTrue(chewie.isCaptive());
		assertEquals(boba, chewie.getEscort());
		assertTrue(boba.getCardsEscorting().contains(chewie));
		assertEquals(boba, chewie.getAttachedTo());
		assertFalse(scn.CardsAtLocation(site, chewie));
		assertFalse(scn.IsActiveBattle());

		scn.PassCardLeavingTable(); //Captive Fury discard

		//We are back in the regular action procedure and all ripples of Captive Fury have settled down
		assertTrue(scn.AwaitingDSBattlePhaseActions());
		assertInZone(Zone.LOST_PILE, fury);
	}

	@Test
	public void CaptiveFuryCanChooseMultipleCaptivesAtSameSiteToBattle() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		var leia = scn.GetLSCard("slave_leia");
		var threepio = scn.GetLSCard("threepio");
		scn.MoveCardsToHand(fury);

		var city = scn.GetLSStartingLocation();
		var marketplace = scn.GetDSStartingLocation();

		var boba = scn.GetDSCard("boba");
		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(city, boba);
		scn.CaptureCardWith(boba, chewie);
		scn.CaptureCardWith(boba, threepio);

		scn.MoveCardsToLocation(marketplace, vader);
		scn.CaptureCardWith(vader, leia);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		assertTrue(scn.LSDecisionAvailable("Choose location"));
		scn.LSChooseCard(city);

		assertTrue(scn.LSDecisionAvailable("Choose escorted captives to battle,"));
		assertEquals(2, scn.LSGetChoiceMax());
		//On Bespin
		assertTrue(scn.LSHasCardChoiceAvailable(chewie));
		assertTrue(scn.LSHasCardChoiceAvailable(threepio));
		//On Tatooine
		assertFalse(scn.LSHasCardChoiceAvailable(leia));

		scn.LSChooseCards(chewie, threepio);

		scn.PassCardAndForceUseResponses();
		assertTrue(scn.IsActiveBattle());
		assertTrue(scn.IsParticipatingInBattle(chewie, threepio));
		assertFalse(scn.IsParticipatingInBattle(leia));
	}

	@Test
	public void CaptiveFuryDoesNotNeedToChooseAllCaptivesAtSameSite() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		var leia = scn.GetLSCard("slave_leia");
		var threepio = scn.GetLSCard("threepio");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();
		var emptysite = scn.GetDSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba);
		scn.CaptureCardWith(boba, chewie);
		scn.CaptureCardWith(boba, threepio);
		scn.CaptureCardWith(boba, leia);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		assertTrue(scn.LSDecisionAvailable("Choose location"));
		assertTrue(scn.LSHasCardChoiceAvailable(site));
		assertFalse(scn.LSHasCardChoiceAvailable(emptysite));
		scn.LSChooseCard(site);

		assertTrue(scn.LSDecisionAvailable("Choose escorted captives to battle,"));
		assertEquals(3, scn.LSGetChoiceMax());

		assertTrue(scn.LSHasCardChoiceAvailable(chewie));
		assertTrue(scn.LSHasCardChoiceAvailable(threepio));
		assertTrue(scn.LSHasCardChoiceAvailable(leia));

		scn.LSChooseCards(chewie, threepio);

		scn.PassCardAndForceUseResponses();
		assertTrue(scn.IsActiveBattle());
		assertTrue(scn.IsParticipatingInBattle(chewie, threepio));
		assertFalse(scn.IsParticipatingInBattle(leia));
	}

	@Test
	public void CaptiveFuryStillPermitsCaptiveSpottingEffectsToWorkDuringBattle() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var dengar = scn.GetDSCard("dengar");

		scn.StartGame();

		scn.MoveCardsToLocation(site, dengar);
		scn.CaptureCardWith(dengar, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.SkipToPowerSegment();

		//No battle destiny for LS
		assertTrue(scn.DSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER"));
		scn.PassResponses();

		// DS only has a battle destiny draw because Dengar is "escorting a captive"
		assertEquals(2, scn.GetAbility(dengar));
		scn.PrepareDSDestiny(3);
		assertEquals(0, scn.GetDSTotalDestiny());
		assertTrue(scn.DSDecisionAvailable("Do you want to draw 1 battle destiny?"));
		scn.DSChooseYes();
		scn.PassDestinyDrawResponses();
		assertEquals(3, scn.GetDSTotalDestiny());

	}

	@Test
	public void CaptiveFuryPermitsUsedHumanShieldToKillCaptive() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		var trooper = scn.GetLSFiller(1);
		var blaster = scn.GetLSCard("blaster");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var vader = scn.GetDSCard("vader");
		var human_shield = scn.GetDSCard("human_shield");
		scn.MoveCardsToDSHand(human_shield);

		scn.StartGame();

		scn.MoveCardsToLocation(site, vader, trooper);
		scn.AttachCardsTo(trooper, blaster);
		scn.CaptureCardWith(vader, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();

		assertTrue(scn.LSCardActionAvailable(blaster));
		scn.LSUseCardAction(blaster);

		assertTrue(scn.LSDecisionAvailable("Choose target"));
		scn.LSChooseCard(vader);
		scn.PassForceUseResponses();
		scn.PrepareLSDestiny(7);
		scn.PassResponses("Fire ");
		scn.PassDestinyDrawResponses();

		scn.PassResponses("ABOUT_TO_BE_HIT");
		assertTrue(scn.DSPlayUsedInterruptAvailable(human_shield));
		scn.DSPlayUsedInterrupt(human_shield);
		scn.DSChooseCard(chewie);
		scn.PassCardPlayResponses();
		scn.PassAllResponses();

		assertFalse(vader.isHit());
		assertTrue(chewie.isHit());
	}

	@Test
	public void CaptiveFuryPermitsLostHumanShieldToForfeitCaptive() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");
		var human_shield = scn.GetDSCard("human_shield");
		scn.MoveCardsToDSHand(human_shield);

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PrepareDSDestiny(1);
		scn.PrepareLSDestiny(1);
		scn.SkipToDamageSegment(true);

		assertTrue(scn.LSWonBattle());
		assertEquals(2, scn.GetUnpaidDSBattleDamage());
		scn.DSPlayLostInterrupt(human_shield);

        assertTrue(scn.DSDecisionAvailable("Target captives(s) to forfeit"));
		assertTrue(scn.DSHasCardChoiceAvailable(chewie));
        scn.DSChooseCard(chewie); //captive to forfeit
		scn.PassCardPlayResponses();
		scn.PassCardLeavingTable(); //chewie forfeited
		scn.PassCardLeavingTable(); //human shield
		scn.PassCardLeavingTable(); //captive fury
		assertFalse(scn.IsActiveBattle());

		assertFalse(chewie.isCaptive());

		assertFalse(scn.IsAttachedTo(boba, chewie));
		assertInZone(Zone.LOST_PILE,  chewie);
		assertInZone(Zone.AT_LOCATION,  boba);
	}

	@Test
	public void CaptiveFuryCannotTargetFrozenCaptive() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, chewie);
		scn.FreezeCard(chewie);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertFalse(scn.LSPlayLostInterruptAvailable(fury));
	}

	@Test
	public void CaptiveFuryCancelsThePlus3BonusOfITOAtDetentionBlockCorridorWith3CaptivesUntilEndOfTurn() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		var leia = scn.GetLSCard("slave_leia");
		var threepio = scn.GetLSCard("threepio");
		scn.MoveCardsToHand(fury);

		var ito = scn.GetDSCard("it-o");
		var boba = scn.GetDSCard("boba");
		var site = scn.GetDSCard("detention_block");

		scn.StartGame();

		scn.MoveLocationToTable(site);
		scn.MoveCardsToLocation(site, ito, boba);
		scn.CaptureCardWith(boba, chewie);
		scn.CaptureCardWith(boba, leia);
		scn.CaptureCardWith(boba, threepio);

		scn.DSActivateMaxForceAndPass();
		scn.DSPass();

		assertTrue(scn.LSPlayUsedInterruptAvailable(fury));
		scn.LSPlayUsedInterrupt(fury);
		scn.LSChooseCard(ito);
		scn.PassAllResponses();

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.DSForceDrainAvailable(site));

		assertEquals(0, scn.GetLSIconsOnLocation(site));
		assertAtLocation(site, boba);
		assertEquals(boba, chewie.getEscort());
		assertEquals(boba, leia.getEscort());
		assertEquals(boba, threepio.getEscort());

		scn.DSForceDrainAt(site);

		//Should be: 0 from the site, +1 for each of the captive Chewie, Leia, Threepio, for a total of +3
		//Instead, Captive Fury eliminates the bonus and reduces it to 0.
		assertEquals(0, scn.GetForceDrainTotal());
		scn.PassForceDrainStartResponses();
		scn.PassForceDrainEndResponses();

		//Skip to the next turn to ensure it only lasted until the end of this turn
		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);

		assertTrue(scn.AwaitingDSControlPhaseActions());
		scn.DSForceDrainAt(site);
		scn.PassForceDrainStartResponses();

		//Should be: 0 from the site, +1 for each of the captive Chewie, Leia, Threepio, for a total of +3
		assertEquals(3, scn.GetForceDrainTotal());
	}

	@Test
	public void CaptiveFuryReleasesBattlingCaptiveIfCaptorIsForfeitThroughRevert() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		var trooper = scn.GetLSFiller(1);
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, trooper);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();

		scn.IssueRevert("Start of Light Side Player's deploy phase #1");

		// After performing a revert, all of our card references are now stale and referring
		// to a game state which strictly speaking no longer exists.  We use those references
		// to look up the equivalent card in the new alternate universe.
		fury = scn.GetPostRevertCard(fury);
		chewie = scn.GetPostRevertCard(chewie);
		trooper = scn.GetPostRevertCard(trooper);
		site = scn.GetPostRevertCard(site);
		boba = scn.GetPostRevertCard(boba);

		scn.SkipToPhase(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();

		scn.PrepareDSDestiny(1);
		scn.PrepareLSDestiny(1);
		scn.SkipToDamageSegment(true);

		assertTrue(scn.LSWonBattle());
		scn.DSPayBattleDamageFromCardInPlay(boba);
		assertFalse(scn.IsActiveBattle());

		assertTrue(scn.LSReleaseDecisionAvailable());
		scn.LSChooseRally();

		assertFalse(chewie.isCaptive());
		assertFalse(scn.IsAttachedTo(boba, chewie));
		assertInZone(Zone.LOST_PILE,  boba);
	}

	@Test
	public void CaptiveFuryModifiersOnCaptiveAreActiveDuringBattle() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var chewie = scn.GetLSCard("chewie");
		var han = scn.GetLSCard("han");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba, han);
		scn.CaptureCardWith(boba, chewie);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(chewie);
		scn.PassCardAndForceUseResponses();
		scn.SkipToPowerSegment();

		//Base forfeit is 6, +5 from Boba Fett
		assertTrue(scn.CardsAtLocation(site, chewie, boba));
		assertEquals(6, chewie.getBlueprint().getForfeit(), scn.epsilon);
		assertEquals(11, scn.GetForfeit(chewie));
		//Base power is 6, +1 if Han can be spotted at the same location
		assertTrue(scn.CardsAtLocation(site, chewie, han));
		assertEquals(6, chewie.getBlueprint().getPower(), scn.epsilon);
		assertEquals(7, scn.GetPower(chewie));
	}

	@Test
	public void CaptiveFuryAbilitiesOnCaptiveAreActiveDuringBattle() {
		var scn = GetScenario();

		var fury = scn.GetLSCard("fury");
		var rook = scn.GetLSCard("rook");
		scn.MoveCardsToHand(fury);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");
		var filler = scn.GetDSFiller(1);
		scn.MoveCardsToHand(filler);

		scn.StartGame();

		scn.MoveCardsToLocation(site, boba);
		scn.CaptureCardWith(boba, rook);

		scn.SkipToLSTurn(Phase.BATTLE);

		assertFalse(scn.LSCardActionAvailable(rook));

		assertTrue(scn.LSPlayLostInterruptAvailable(fury));
		scn.LSPlayLostInterrupt(fury);
		scn.LSChooseCard(site);
		scn.LSChooseCard(rook);
		scn.PassCardAndForceUseResponses();
		scn.PassBattleStartResponses();
		scn.DSPass();

		assertTrue(scn.LSCardActionAvailable(rook));
		scn.LSUseCardAction(rook);
		scn.PassResponses();

		assertTrue(scn.LSHasCardChoiceAvailable(filler));

		scn.LSDismissRevealedCards();
	}

}
