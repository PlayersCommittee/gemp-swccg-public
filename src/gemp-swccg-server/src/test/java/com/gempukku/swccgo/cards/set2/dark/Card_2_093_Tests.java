package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_2_093_Tests
{
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("chewie", "2_3");
					put("threepio", "1_5");
					put("slave_leia", "6_32");
				}},
				new HashMap<>()
				{{
					put("it-o", "2_093");
					put("boba", "5_91");

				}},
				10,
				10,
				StartingSetup.DefaultLSGroundLocation,
				new StartingSetup.StartingLocation("1_284"), //Detention Block Corridor
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void ITOStatsAndKeywordsAreCorrect() {
		/**
		 * Title: IT-O (Eyetee-Oh)
		 * Uniqueness: UNIQUE
		 * Side: Dark
		 * Type: Character
		 * Subtype: Droid
		 * Destiny: 2
		 * Deploy: 3
		 * Power: 4
		 * Forfeit: 1
		 * Icons: Warrior
		 * Game Text: When at Detention Block Corridor, adds X to your Force drains here, where X = number of captives here.
		 * 		Immune to Restraining Bolt.
		 * Lore: Floating prisoner interrogation droid. Uses probes and needles to dispense truth drugs and perform 'surgery.'
		 * 		Sensors determine subject's pain threshold and truthfulness.
		 * Set: A New Hope
		 * Rarity: R1
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("it-o").getBlueprint();

		assertEquals(Title.IT0, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertTrue(card.isCardType(CardType.DROID));
		//assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
		assertEquals(2, card.getDestiny(), scn.epsilon);
		assertEquals(3, card.getDeployCost(), scn.epsilon);
		assertEquals(4, card.getPower(), scn.epsilon);
		assertEquals(0, card.getAbility(), scn.epsilon);
		assertEquals(1, card.getForfeit(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.A_NEW_HOPE));
		assertTrue(card.getModelTypes().contains(ModelType.INTERROGATOR));
	}

	@Test
	public void ITOAdds3ToForceDrainsAtDetentionBlockCorridorWhenPresentWith3Captives() {
		var scn = GetScenario();

		var chewie = scn.GetLSCard("chewie");
		var leia = scn.GetLSCard("slave_leia");
		var threepio = scn.GetLSCard("threepio");

		var ito = scn.GetDSCard("it-o");
		var boba = scn.GetDSCard("boba");
		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, ito, boba);
		scn.CaptureCardWith(boba, chewie);
		scn.CaptureCardWith(boba, leia);
		scn.CaptureCardWith(boba, threepio);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());
		assertTrue(scn.DSForceDrainAvailable(site));

		assertEquals(0, scn.GetLSIconsOnLocation(site));
		assertAtLocation(site, boba);
		assertEquals(boba, chewie.getEscort());
		assertEquals(boba, leia.getEscort());
		assertEquals(boba, threepio.getEscort());

		scn.DSForceDrainAt(site);
		scn.PassForceDrainStartResponses();
		//0 from the site, +1 for each of the captive Chewie, Leia, Threepio
		assertEquals(3, scn.GetForceDrainTotal());
	}

}

