package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_2_109_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("luke", "108_3");
				}},
				new HashMap<>()
				{{
					put("septoid", "2_109");
					put("servitude", "106_014");
					put("vader", "7_175");
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
	public void SeptoidStatsAndKeywordsAreCorrect() {
		/**
		 * Title: WED15-l7 'Septoid' Droid
		 * Uniqueness: UNIQUE
		 * Side: Dark
		 * Type: Character
		 * Subtype: Droid
		 * Destiny: 2
		 * Deploy: 3
		 * Power: 1
		 * Forfeit: *
		 * Model: Maintenance
		 * Game Text: * Forfeit value begins at 7. When 'forfeited,' droid remains in play, but forfeit value is
		 * 		reduced by the amount of attrition or battle damage absorbed. Droid lost when forfeit value reaches zero.
		 * Lore: Multi-armed maintenance droid fiercely loyal to the Empire. Specializes in extending effective
		 * 		operational life of Imperial resources. Nicknamed for an insect from Eriadu.
		 * Set: A New Hope
		 * Rarity: U2
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("septoid").getBlueprint();

		assertEquals("WED15-l7 'Septoid' Droid", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertTrue(card.isCardType(CardType.DROID));
		assertEquals(2, card.getDestiny(), scn.epsilon);
		assertEquals(3, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		//The forfeit value is specially calculated due to the game text
		assertNull(card.getForfeit());
		assertEquals(1, card.getIconCount(Icon.DROID));
		assertEquals(1, card.getIconCount(Icon.A_NEW_HOPE));
		assertTrue(card.getModelTypes().contains(ModelType.MAINTENANCE));
	}

	@Test
	public void SeptoidDefaultsTo7Forfeit() {
		var scn = GetScenario();

		var luke = scn.GetLSCard("luke");
		var site = scn.GetLSStartingLocation();

		var septoid = scn.GetDSCard("septoid");
		var stormtrooper1 = scn.GetDSFiller(1);
		var stormtrooper2 = scn.GetDSFiller(2);
		scn.MoveCardsToHand(septoid);

		scn.StartGame();

		scn.MoveCardsToLocation(site, luke, septoid, stormtrooper1, stormtrooper2);

		scn.SkipToPhase(Phase.BATTLE);

		scn.DSInitiateBattle(site);
		scn.DSPass();
		scn.PrepareLSDestiny(3);
		scn.PrepareLSDestiny(2);
		scn.LSUseCardAction(luke);
		scn.LSChooseCard(stormtrooper2);
		scn.PassAllResponses();
		scn.DSPass();
		scn.LSPass();

		scn.PrepareDSDestiny(1);
		scn.PrepareLSDestiny(7);
		scn.SkipToDamageSegment(true);
		assertEquals(9, scn.GetUnpaidDSBattleDamage());

		scn.DSPayBattleDamageFromCardInPlay(septoid);
		assertEquals(2, scn.GetUnpaidDSBattleDamage());
	}

	@Test
	public void SeptoidIsWorth7ForfeitWhenForcedServitudeIsInPlay() {
		var scn = GetScenario();

		var luke = scn.GetLSCard("luke");
		var site = scn.GetLSStartingLocation();

		var septoid = scn.GetDSCard("septoid");
		var servitude = scn.GetDSCard("servitude");
		var stormtrooper1 = scn.GetDSFiller(1);
		var stormtrooper2 = scn.GetDSFiller(2);

		scn.StartGame();

		scn.MoveCardsToHand(septoid);
		scn.MoveCardsToLocation(site, luke, stormtrooper1, stormtrooper2);
		scn.AttachCardsTo(site, servitude);

		scn.SkipToPhase(Phase.BATTLE);

		scn.DSInitiateBattle(site);
		scn.DSPass();
		scn.PrepareLSDestiny(3);
		scn.PrepareLSDestiny(2);
		scn.LSUseCardAction(luke);
		scn.LSChooseCard(stormtrooper2);
		scn.PassAllResponses();
		scn.DSPass();
		scn.LSPass();

		scn.PrepareDSDestiny(1);
		scn.PrepareLSDestiny(7);
		scn.SkipToDamageSegment(true);
		assertEquals(10, scn.GetUnpaidDSBattleDamage());

		scn.DSPayBattleDamageFromCardInHand(septoid);
		assertEquals(3, scn.GetUnpaidDSBattleDamage());
	}
}
