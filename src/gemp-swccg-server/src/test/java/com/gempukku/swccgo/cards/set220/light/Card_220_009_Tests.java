package com.gempukku.swccgo.cards.set220.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_220_009_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					// The card itself is included in the Rescue the Princes objective pack

				}},
				new HashMap<>()
				{{
					put("vader", "1_168");
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
	public void Prisoner2187StatsAndKeywordsAreCorrect() {
		/**
		 * Title: Prisoner 2187 (V)
		 * Uniqueness: UNIQUE
		 * Side: Light
		 * Persona: Leia
		 * Type: Character
		 * Subtype: Rebel
		 * Destiny: 1
		 * Deploy: 4
		 * Power: 4
		 * Ability: 3
		 * Forfeit: 6
		 * Icons: Premium, Warrior, A New Hope, V Set 20
		 * Keywords: Senator, Female
		 * Species: Alderaanian
		 * Game Text: Draws one battle destiny if unable to otherwise. While present at a Death Star site, Force drain
		 * 			+1 where you have a Rebel stormtrooper. While on Death Star (even if imprisoned), Leia may not be
		 * 			transferred and her gametext may not be canceled.
		 * Lore: Princess Leia Organa. Alderaanian senator. Targeted by Vader for capture and interrogation. The Dark
		 * 			Lord of the Sith wanted her alive.
		 * Set: 20
		 * Rarity: V
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("prisoner").getBlueprint();

		assertEquals(Title.Prisoner_2187, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertTrue(card.hasPersona(Persona.LEIA));
		assertTrue(card.isCardType(CardType.REBEL));
		//assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(4, card.getDeployCost(), scn.epsilon);
		assertEquals(4, card.getPower(), scn.epsilon);
		assertEquals(3, card.getAbility(), scn.epsilon);
		assertEquals(6, card.getForfeit(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.REBEL));
		assertEquals(1, card.getIconCount(Icon.WARRIOR));
		assertEquals(1, card.getIconCount(Icon.PREMIUM));
		assertEquals(1, card.getIconCount(Icon.A_NEW_HOPE));
		assertEquals(1, card.getIconCount(Icon.VIRTUAL_SET_20));
		assertTrue(card.hasKeyword(Keyword.SENATOR));
		assertTrue(card.hasKeyword(Keyword.FEMALE));
		assertEquals(Species.ALDERAANIAN, card.getSpecies());
	}

	@Test
	public void Prisoner2187CannotBeTransferred() {
		var scn = GetScenario();

		var prisoner = scn.GetLSCard("prisoner");
		var corridor = scn.GetLSCard("corridor");

		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(corridor, vader);

		scn.SkipToPhase(Phase.MOVE);

		assertAtLocation(vader);
		assertTrue(prisoner.isCaptive());
		assertTrue(prisoner.isImprisoned());
		assertEquals(corridor, prisoner.getAttachedTo());

		assertTrue(scn.DSCardActionAvailable(vader));
	}

}
