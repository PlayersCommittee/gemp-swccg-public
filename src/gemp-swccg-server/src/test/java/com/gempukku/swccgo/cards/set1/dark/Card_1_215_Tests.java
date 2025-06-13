package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.*;

public class Card_1_215_Tests
{
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
				}},
				new HashMap<>()
				{{
					put("expand", "1_215");
					put("barge", "6_172"); // Jabba's Sail Barge
					put("deck", "6_167"); // Jabba's Sail Barge: Passenger Deck

					put("sandcrawler", "1_309");
					put("junkheap", "2_149"); // Sandcrawler: Droid Junkheap

					put("desert1", "6_169");
					put("desert2", "6_169");
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
	public void ExpandTheEmpireStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Expand the Empire
		 * Uniqueness: UNIQUE
		 * Side: Dark
		 * Type: Effect
		 * Destiny: 3
		 * Keywords: Deploys on Site
		 * Game Text: Deploy on any site. 'Expands' your 'game text' for that site to add to your 'game text' at
		 * 			the adjacent sites.
		 * Lore: The Emperor disbanded the Imperial Senate 'for the duration of the emergency,' seizing absolute power.
		 * 			He planned to extend rule by terrorizing planets into submission.
		 * Set: Premiere
		 * Rarity: R1
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("expand").getBlueprint();

		assertEquals(Title.Expand_The_Empire, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertTrue(card.isCardType(CardType.EFFECT));
		assertEquals(3, card.getDestiny(), scn.epsilon);
		assertTrue(card.hasKeyword(Keyword.DEPLOYS_ON_SITE));
	}

	@Test
	public void ExpandTheEmpireDuplicatesTextToBothLeftAndRight() {
		var scn = GetScenario();

		var troopers = scn.GetDSFillerRange(3);

		var marketplace = scn.GetDSStartingLocation();
		var desert1 = scn.GetDSCard("desert1");
		var desert2 = scn.GetDSCard("desert2");
		var expand = scn.GetDSCard("expand");
		scn.MoveCardsToHand(desert1, desert2, expand);

		scn.StartGame();

		scn.SkipToPhase(Phase.DEPLOY);

		scn.DSDeployCard(desert1);
		scn.DSChoose("Left");
		scn.PassAllResponses();
		scn.LSPass();

		scn.DSDeployCard(desert2);
		scn.DSChooseCard(marketplace);
		scn.DSChoose("Right");
		scn.PassAllResponses();

		scn.SkipToLSTurn();
		scn.MoveCardsToLocation(marketplace, troopers.get(0));
		scn.MoveCardsToLocation(desert1, troopers.get(1));
		scn.MoveCardsToLocation(desert2, troopers.get(2));

		scn.SkipToDSTurn(Phase.CONTROL);

		//The original Marketplace has an ability that lets you retrieve 1 Force
		// if you occupy it.  The two deserts normally have no such ability
		assertTrue(scn.DSCardActionAvailable(marketplace, "Retrieve 1 Force"));
		assertFalse(scn.DSCardActionAvailable(desert1, "Retrieve 1 Force"));
		assertFalse(scn.DSCardActionAvailable(desert2, "Retrieve 1 Force"));

		scn.SkipToDSTurn(Phase.DEPLOY);
		assertTrue(scn.DSDeployAvailable(expand));
		scn.DSDeployCardAndPassResponses(expand, marketplace);
		scn.LSPass();

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);

		assertTrue(scn.IsAdjacentTo(marketplace, desert1));
		assertTrue(scn.IsAdjacentTo(desert1, marketplace));
		assertTrue(scn.IsAdjacentTo(marketplace, desert2));
		assertTrue(scn.IsAdjacentTo(desert2, marketplace));
		assertFalse(scn.IsAdjacentTo(desert1, desert2));

		assertTrue(scn.DSCardActionAvailable(marketplace, "Retrieve 1 Force"));
		assertTrue(scn.DSCardActionAvailable(desert1, "Retrieve 1 Force"));
		assertTrue(scn.DSCardActionAvailable(desert2, "Retrieve 1 Force"));
	}

	@Test
	public void ExpandTheEmpireDuplicatesTextToVehicleSiteWhenVehiclePresent() {
		var scn = GetScenario();

		var troopers = scn.GetDSFillerRange(3);

		var marketplace = scn.GetDSStartingLocation();
		var barge = scn.GetDSCard("barge");
		var deck = scn.GetDSCard("deck");
		var expand = scn.GetDSCard("expand");
		scn.MoveCardsToHand(barge, deck, expand);

		scn.StartGame();

		scn.DSActivateForceCheat(2);

		scn.SkipToPhase(Phase.DEPLOY);

		scn.DSDeployCardAndPassResponses(barge, marketplace);
		scn.LSPass();

		scn.DSDeployCard(deck);
		scn.PassAllResponses();
		scn.LSPass();

		scn.MoveCardsToLocation(marketplace, troopers.get(0));
		scn.MoveCardsToLocation(deck, troopers.get(1));

		scn.SkipToDSTurn(Phase.CONTROL);

		//The original Marketplace has an ability that lets you retrieve 1 Force
		// if you occupy it.  The Passenger Deck normally have no such ability
		assertTrue(scn.DSCardActionAvailable(marketplace, "Retrieve 1 Force"));
		assertFalse(scn.DSCardActionAvailable(deck, "Retrieve 1 Force"));

		scn.SkipToDSTurn(Phase.DEPLOY);
		assertTrue(scn.DSDeployAvailable(expand));
		scn.DSDeployCardAndPassResponses(expand, marketplace);
		scn.LSPass();

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);

		assertTrue(scn.IsAdjacentTo(marketplace, deck));
		assertTrue(scn.IsAdjacentTo(deck, marketplace));

		assertTrue(scn.DSCardActionAvailable(marketplace, "Retrieve 1 Force"));
		assertTrue(scn.DSCardActionAvailable(deck, "Retrieve 1 Force"));
	}

	@Test
	public void ExpandTheEmpireDoesNotDuplicateTextToVehicleSitesWhenVehicleNotPresent() {
		var scn = GetScenario();

		var troopers = scn.GetDSFillerRange(3);

		var marketplace = scn.GetDSStartingLocation();
		var deck = scn.GetDSCard("deck");
		var expand = scn.GetDSCard("expand");
		scn.MoveCardsToHand(deck, expand);

		scn.StartGame();

		scn.DSActivateForceCheat(2);

		scn.SkipToPhase(Phase.DEPLOY);

		scn.LSPass();

		scn.DSDeployCard(deck);
		scn.PassAllResponses();
		scn.LSPass();

		scn.MoveCardsToLocation(marketplace, troopers.get(0));
		scn.MoveCardsToLocation(deck, troopers.get(1));

		scn.SkipToDSTurn(Phase.CONTROL);

		//The original Marketplace has an ability that lets you retrieve 1 Force
		// if you occupy it.  The Passenger Deck normally have no such ability
		assertTrue(scn.DSCardActionAvailable(marketplace, "Retrieve 1 Force"));
		assertFalse(scn.DSCardActionAvailable(deck, "Retrieve 1 Force"));

		scn.SkipToDSTurn(Phase.DEPLOY);
		assertTrue(scn.DSDeployAvailable(expand));
		scn.DSDeployCardAndPassResponses(expand, marketplace);
		scn.LSPass();

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);

		assertFalse(scn.IsAdjacentTo(marketplace, deck));
		assertFalse(scn.IsAdjacentTo(deck, marketplace));

		assertTrue(scn.DSCardActionAvailable(marketplace, "Retrieve 1 Force"));
		assertFalse(scn.DSCardActionAvailable(deck, "Retrieve 1 Force"));
	}

	@Test
	public void ExpandTheEmpireDuplicatesVehicleSiteTextToAdjacentSiteWhenVehiclePresent() {
		var scn = GetScenario();

		var troopers = scn.GetDSFillerRange(3);

		var marketplace = scn.GetDSStartingLocation();
		var sandcrawler = scn.GetDSCard("sandcrawler");
		var junkheap = scn.GetDSCard("junkheap");
		var expand = scn.GetDSCard("expand");
		scn.MoveCardsToHand(sandcrawler, junkheap, expand);

		scn.StartGame();

		scn.MoveCardsToLocation(marketplace, sandcrawler);

		scn.SkipToPhase(Phase.DEPLOY);

		scn.DSDeployCard(junkheap);
		scn.PassAllResponses();
		scn.LSPass();

		assertTrue(scn.IsNighttimeAt(junkheap));
		assertFalse(scn.IsNighttimeAt(marketplace));

		assertTrue(scn.DSDeployAvailable(expand));
		scn.DSDeployCardAndPassResponses(expand, junkheap);

		assertTrue(scn.IsAdjacentTo(marketplace, junkheap));
		assertTrue(scn.IsAdjacentTo(junkheap, marketplace));

		assertTrue(scn.IsNighttimeAt(junkheap));
		assertTrue(scn.IsNighttimeAt(marketplace));
	}

}

