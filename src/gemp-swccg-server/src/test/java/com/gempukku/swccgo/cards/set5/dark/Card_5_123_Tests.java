package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_5_123_Tests
{
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{

				}},
				new HashMap<>()
				{{
					put("delivery", "5_123");

					put("tower", "5_172"); // Security Tower
					put("vader", "1_168");
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
	public void SpecialDeliveryStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Special Delivery
		 * Uniqueness: UNIQUE
		 * Side: Dark
		 * Type: Effect
		 * Destiny: 4
		 * Icons: Warrior
		 * Game Text: Deploy on a prison. When one of your troopers 'delivers' (imprisons) a captive here, you may
		 * 			search your Lost Pile, take any one card into hand and then lose effect. (Each captive may be
		 * 			'delivered' only once until they are released or leave table)
		 * Lore: Because bounty hunters are untrustworthy, the Empire relies on its troopers for efficient prisoner delivery.
		 * Set: Cloud City
		 * Rarity: C
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("delivery").getBlueprint();

		assertEquals(Title.Special_Delivery, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertTrue(card.isCardType(CardType.EFFECT));
		assertEquals(4, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.CLOUD_CITY));
		assertTrue(card.hasKeyword(Keyword.DEPLOYS_ON_SITE));
	}

	@Test
	public void SpecialDeliveryRewardsCaptiveTransferWithACardFromLostPile() {
		var scn = GetScenario();

		var rebel = scn.GetLSFiller(1);

		var tower = scn.GetDSCard("tower");

		var delivery = scn.GetDSCard("delivery");
		var stormtrooper = scn.GetDSFiller(1);
		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveLocationToTable(tower);
		scn.AttachCardsTo(tower, delivery);
		scn.MoveCardsToLocation(tower, stormtrooper);
		scn.CaptureCardWith(stormtrooper, rebel);
		scn.MoveCardsToTopOfDSLostPile(vader);

		scn.SkipToPhase(Phase.MOVE);

		assertTrue(rebel.isCaptive());
		assertEquals(stormtrooper, rebel.getEscort());
		assertEquals(1, stormtrooper.getCardsEscorting().size());
		assertTrue(stormtrooper.getCardsEscorting().contains(rebel));
		assertEquals(stormtrooper, rebel.getAttachedTo());
		assertInZone(Zone.LOST_PILE, vader);
		assertAtLocation(tower, stormtrooper);

		assertTrue(scn.DSActionAvailable("Deliver captive to prison"));
		scn.DSChooseAction("Deliver captive to prison");

		scn.LSPass();
		assertTrue(scn.DSCardActionAvailable(delivery));

		scn.DSUseCardAction(delivery);
		scn.PassResponses("SPECIAL_DELIVERY_COMPLETED");

		assertTrue(rebel.isCaptive());
		assertTrue(rebel.isImprisoned());
		assertNull(rebel.getEscort());
		assertEquals(tower, rebel.getAttachedTo());
		assertEquals(0, stormtrooper.getCardsEscorting().size());
		assertAtLocation(tower, stormtrooper);

		//We've successfully delivered, so now we get to pick a card from our lost pile
		assertTrue(scn.DSHasCardChoiceAvailable(vader));
		scn.DSChooseCard(vader);
		scn.PassAllResponses();

		assertInZone(Zone.HAND, vader);
		assertInZone(Zone.LOST_PILE, delivery);
	}

}

