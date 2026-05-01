package com.gempukku.swccgo.cards.set223.light;

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
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_223_006_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(

				new HashMap<>() {{
					put("ttv", "223_006"); //Transmission Terminated (V)
					put("tantive","2_073"); //Tantive IV
				}},
				new HashMap<>() {{
					put("devastator","1_302");
					put("beam","2_115"); //Tractor Beam
					put("lenox","3_90"); //Captain Lennox
				}},
				10,
				10,
				StartingSetup.DefaultLSSpaceSystem,
				StartingSetup.DefaultDSSpaceSystem,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void TransmissionTerminatedVStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Transmission Terminated
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Interrupt
		 * Subtype: Used or Lost
		 * Destiny: 5
		 * Icons: Dagobah, Set 23
		 * Game Text: USED: Target a starship. For remainder of turn, target may not use tractor beams, fire weapons, or 'cloak.'
		 * 		LOST: Cancel a hologram. OR Cancel the game text of Emperor's Power or an Admiral's Order until start of your turn.
		 * Lore: After the mission, the Death Squadron HoloNet communications system reported fifteen system errors:
		 * 		ten computer malfunctions, four power failures and one asteroid.
		 * Set: Set 23
		 * Rarity: V
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("ttv").getBlueprint();

		assertEquals("Transmission Terminated", card.getTitle());
		assertTrue(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
		assertEquals(5, card.getDestiny(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.INTERRUPT);
			add(Icon.DAGOBAH);
			add(Icon.VIRTUAL_SET_23);
		}});
		assertEquals(ExpansionSet.SET_23,card.getExpansionSet());
		assertEquals(Rarity.V, card.getRarity());
	}

	//associated issue: https://github.com/PlayersCommittee/gemp-swccg-public/issues/846
	@Test @Ignore
	public void TransmissionTerminatedVUsedPreventsUsingTractorBeam() {
		//test coverage:
		//test1: can target opponent starship
		//test2: can target self starship
		//test3: (FAILS) opponent targeted starship cannot use tractor beam this turn
		//test4: opponent targeted starship can use tractor beam next turn

		var scn = GetScenario();

		var ttv = scn.GetLSCard("ttv");
		var tantive = scn.GetLSCard("tantive");

		var devastator = scn.GetDSCard("devastator");
		var beam = scn.GetDSCard("beam");

		var system = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(ttv);
		scn.MoveCardsToDSHand(beam);

		scn.MoveCardsToLocation(system, devastator, tantive);
		scn.AttachCardsTo(devastator, beam);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSPass();

		scn.LSPlayUsedInterrupt(ttv);
		assertTrue(scn.LSHasCardChoiceAvailable(devastator)); //test1
		assertTrue(scn.LSHasCardChoiceAvailable(tantive)); //test2
		scn.LSChooseCard(devastator);
		scn.PassAllResponses();

		scn.DSInitiateBattle(system);

		scn.SkipToDamageSegment();

		assertTrue(scn.GetDSForcePileCount() >= 2); //enough to pay tractor beam cost
		assertEquals(3,scn.GetUnpaidLSBattleDamage());
		scn.LSPayRemainingBattleDamageFromReserveDeck();

			///FAILS HERE (should not be able to use tractor beam)
		assertFalse(scn.DSDecisionAvailable("about to end - Optional responses"));
		assertFalse(scn.DSCardActionAvailable(beam, "tractor beam")); //test3

		//add more to skip to next turn, battle again, verify able to use tractor beam
	}

	//add test for showing TT(V) does not allow In Range to use a Tractor Beam
	//add test for showing TT(V) does not allow Lennox to use a Tractor Beam

}


