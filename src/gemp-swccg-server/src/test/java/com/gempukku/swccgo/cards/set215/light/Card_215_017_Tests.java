package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_215_017_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("blaster", "7_161");
				}},
				new HashMap<>()
				{{
					put("vader", "1_168");
					put("lightsaber", "1_324");
				}},
				10,
				10,
				StartingSetup.RescueThePrincessVObjective,
				StartingSetup.DefaultDSGroundLocation,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void RescueThePrincessStatsAndKeywordsAreCorrect() {
		/**
		 * Front Title: Rescue the Princess (V)
		 * Back Title : Sometimes I Amaze Even Myself (V)
		 * Side: Light
		 * Type: Objective
		 * Destiny: 0/7
		 * Front Game Text: Deploy Central Core, A Power Loss, Detention Block Corridor (with [A New Hope] Leia
		 * 			imprisoned there), and Trash Compactor.
		 * 			For remainder of game, your Death Star sites generate +1 Force for you. You may not deploy Luke of
		 * 			ability > 4 or [Episode I] (or [Episode VII]) Jedi. If Leia is about to leave table (for any reason,
		 * 			even if inactive), imprison her in Detention Block Corridor (cards on her are placed in owner's Used
		 * 			Pile). Once per turn, may ▼ a Death Star site.
		 * 			Flip this card if Leia occupies a Death Star site and A Power Loss is 'shut down.'
		 * Back Game Text : While this side up, for opponent to initiate a Force drain, opponent must use +1 Force. Your
		 * 			Death Star sites are immune to Set Your Course For Alderaan. Once per turn, if you just 'hit' a
		 * 			character with a blaster, opponent loses 1 Force. May place Obi-Wan out of play from a Death Star
		 * 			site to cancel a battle just initiated anywhere on Death Star. I Can't Believe He's Gone is canceled.
		 * 			During opponent's draw phase, if opponent did not initiate a battle this turn, may retrieve 1 Force.
		 * 			Flip this card if Leia is not at a Death Star site.
		 * Set: 15
		 * Rarity: V
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("rescue").getBlueprint();

		assertEquals(Title.Rescue_The_Princess, card.getTitle());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(0, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.VIRTUAL_SET_15));

		var back = scn.GetLSCard("rescue").getOtherSideBlueprint();

		assertEquals(Title.Sometimes_I_Amaze_Even_Myself, back.getTitle());
		assertEquals(Side.LIGHT, back.getSide());
		assertEquals(7, back.getDestiny(), scn.epsilon);
		assertEquals(1, back.getIconCount(Icon.VIRTUAL_SET_15));
	}

	@Test
	public void RescueThePrincessImprisonsLeiaAndStripsCardsWhenSheIsLost() {
		var scn = GetScenario();

		var prisoner = scn.GetLSCard("prisoner");
		var blaster = scn.GetLSCard("blaster");
		var core = scn.GetLSCard("core");
		var corridor = scn.GetLSCard("corridor");

		var vader = scn.GetDSCard("vader");
		var lightsaber = scn.GetDSCard("lightsaber");

		scn.StartGame();

		scn.MoveCardsToLocation(core, prisoner, vader);
		scn.AttachCardsTo(prisoner, blaster);
		scn.AttachCardsTo(vader, lightsaber);

		scn.SkipToPhase(Phase.BATTLE);

		assertFalse(prisoner.isImprisoned());
		assertFalse(prisoner.isCaptive());
		assertAtLocation(core, prisoner, vader);

		scn.DSInitiateBattle(core);
		scn.PassBattleStartResponses();

		assertTrue(scn.DSCardActionAvailable(lightsaber));
		scn.DSUseCardAction(lightsaber);

		assertTrue(scn.DSDecisionAvailable("Choose target"));
		scn.DSChooseCard(prisoner);
		scn.PassForceUseResponses();
		scn.PrepareDSDestiny(7);
		scn.PrepareDSDestiny(6);
		scn.PassResponses("Fire ");
		scn.PassDestinyDrawResponses();
		scn.PassAllResponses();

		scn.LSPass();
		scn.DSPass();
		scn.SkipToDamageSegment(false);
		//Leia was hit by saber and forfeit to check the auto-imprison
		scn.LSChooseCard(prisoner);
		scn.PassAllResponses();

		assertTrue(prisoner.isImprisoned());
		assertTrue(prisoner.isCaptive());
		assertNotAtLocation(core, prisoner);
		assertEquals(corridor, prisoner.getAttachedTo());
		assertInZone(Zone.USED_PILE, blaster);
	}
}
