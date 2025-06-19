package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.CardCollection;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Card_2_059_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("kid", "2_59");
					put("red2", "2_70");
					put("biggs", "1_3");
					put("torpedoes", "1_158");
					put("suit", "4_014");

					put("deathstar", "7_117");
					put("trench", "2_62");
					put("attackrun", "2_42");
				}},
				new HashMap<>()
				{{
					put("tie", "1_304");
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
	public void YoureAllClearKidStatsAndKeywordsAreCorrect() {
		/**
		 * Title: You're All Clear Kid
		 * Uniqueness: UNIQUE
		 * Side: Light
		 * Type: Used Interrupt
		 * Destiny: 3
		 * Game Text: Cancel I'm On The Leader. (Immune to Sense.)  OR  Use 1 Force during an Attack Run.  Move one TIE
		 * 			in Death Star: Trench (your choice) to Death Star system for free.  Add 1 to total of Attack Run if
		 * 			lead starfighter has matching pilot aboard.
		 * Lore: 'Now let's blow this thing and go home!'
		 * Set: A New Hope
		 * Rarity: R1
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("kid").getBlueprint();

		assertEquals(Title.Youre_All_Clear_Kid, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertTrue(card.getCardTypes().contains(CardType.INTERRUPT));
		assertEquals(CardSubtype.USED, card.getCardSubtype());
		assertEquals(3, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.A_NEW_HOPE));
	}

	@Test
	public void YoureAllClearKidAdds1ToAttackRunTotalIfLeaderWearingRebelFlightSuit() {
		var scn = GetScenario();

		var kid = scn.GetLSCard("kid");
		var attackrun = scn.GetLSCard("attackrun");
		var red2 = scn.GetLSCard("red2");
		var biggs = scn.GetLSCard("biggs");
		var torpedoes = scn.GetLSCard("torpedoes");
		var suit = scn.GetLSCard("suit");

		var deathstar = scn.GetLSCard("deathstar");
		var trench = scn.GetLSCard("trench");

		var tie = scn.GetDSCard("tie");

		scn.StartGame();

		scn.MoveCardsToHand(kid);
		scn.MoveLocationToTable(deathstar);
		scn.MoveLocationToTable(trench);
		scn.AttachCardsTo(trench, attackrun);
		scn.MoveCardsToLocation(deathstar, red2, tie);
		scn.BoardAsPilot(red2, biggs);
		scn.AttachCardsTo(red2, torpedoes);
		scn.AttachCardsTo(biggs, suit);

		scn.SkipToLSTurn(Phase.MOVE);

		assertTrue(scn.LSActionAvailable("Attempt to 'blow away' Death Star"));
		scn.PrepareLSDestiny(6);
		scn.PrepareLSDestiny(7);
		scn.LSChooseAction("Attempt to 'blow away' Death Star");
		scn.PassResponses("MOVING_AT_START_OF_ATTACK_RUN");
		scn.PassResponses("MOVED_AT_START_OF_ATTACK_RUN");

		//Moving TIE Fighter into trench
		scn.DSChooseCard(tie);
		scn.PassResponses("MOVING_AT_START_OF_ATTACK_RUN");
		scn.PassResponses("MOVED_AT_START_OF_ATTACK_RUN");

		assertTrue(scn.LSCardPlayAvailable(kid));
		scn.LSPlayCard(kid);
		scn.LSChooseCard(tie);
		scn.PassAllResponses();

		scn.DSPass();
		scn.LSPass();

		scn.PassAllResponses();

		// Destiny 6 + 7 + Biggs 2 ability = 15
		// +1 from YACK because Rebel Flight Suit made Biggs a matching pilot
		// == 16, enough to blow it up
		assertTrue(scn.DSDecisionAvailable("Choose Force to lose"));
	}


}
