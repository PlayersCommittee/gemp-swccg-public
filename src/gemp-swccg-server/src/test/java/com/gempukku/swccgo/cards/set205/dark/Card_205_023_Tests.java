package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_205_023_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("deflection", "6_61"); //blaster deflection
					put("luke", "108_003"); //luke with saber
				}},
				new HashMap<>()
				{{
					put("dengar", "205_023"); //Dengar V
					put("blaster", "1_317");
					put("crimson_blaster", "213_034"); //crimson dawn blaster
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
	public void DengarVStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Dengar (V)
		 * Uniqueness: Unique
		 * Side: Dark
		 * Type: Character
		 * Subtype: Alien
		 * Destiny: 1
		 * Deploy: 3
		 * Power: 3
		 * Ability: 2
		 * Forfeit: 5
		 * Icons: Pilot, Warrior, Dagobah, Virtual Set 5
		 * Persona: Dengar
		 * Game Text: While escorting a captive or piloting Punishing One, adds one battle destiny.
		 * 		If a character is about to be 'hit' by Dengar, he may capture that character instead.
		 * 		Once per game, may take Bounty or a blaster into hand from Reserve Deck; reshuffle.
		 * Lore: Corellian bounty hunter. Assassin trained by the Empire. Has reflex-enhancing cyber-implants.
		 * 		Gravely injured during a swoop race in the crystal swamp of Agrilat. Blames Han Solo.
		 * Keywords: bounty hunter
		 * Set: Set 5
		 * Rarity: V
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("dengar").getBlueprint();

		assertEquals("Dengar", card.getTitle());
		assertTrue(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(3, card.getDeployCost(), scn.epsilon);
		assertEquals(3, card.getPower(), scn.epsilon);
		assertEquals(2, card.getAbility(), scn.epsilon);
		assertEquals(5, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.ALIEN);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
			add(Keyword.BOUNTY_HUNTER);
			add(Keyword.ASSASSIN);
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
			add(Persona.DENGAR);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.ALIEN);
			add(Icon.PILOT);
			add(Icon.WARRIOR);
			add(Icon.DAGOBAH);
			add(Icon.VIRTUAL_SET_5);
		}});
		assertEquals(ExpansionSet.SET_5,card.getExpansionSet());
		assertEquals(Rarity.V,card.getRarity());
	}

	@Test
	public void DengarVMayCaptureCharacter() {
		var scn = GetScenario();

		var luke = scn.GetLSCard("luke");

		var dengar = scn.GetDSCard("dengar");
		var blaster = scn.GetDSCard("blaster");

        var site = scn.GetLSStartingLocation(); //interior

        scn.StartGame();

        scn.MoveCardsToLocation(site,luke,dengar);
        scn.AttachCardsTo(dengar,blaster);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.GetDSForcePileCount() >= 2); //enough to battle and fire
		assertTrue(scn.DSCanInitiateBattle(site));
		scn.PrepareDSDestiny(7); //guarantee hit

		scn.DSInitiateBattle(site);
		assertTrue(scn.AwaitingDSWeaponsSegmentActions());
		scn.DSUseCardAction(blaster,"Fire");
		scn.DSChooseCard(luke);

		scn.LSPass(); //Use 1 Force - Optional responses
		scn.DSPass();

		scn.LSPass(); //Fire Imperial Blaster - Optional responses
		scn.DSPass();

		scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		scn.LSPass(); //DESTINY_DRAWN - Optional responses
		scn.DSPass();

		scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
		scn.DSPass();

		scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
		assertTrue(scn.DSDecisionAvailable("About to 'hit'"));
		assertTrue(scn.DSCardActionAvailable(dengar,"Capture"));
		scn.DSUseCardAction(dengar,"Capture");
		scn.DSChooseCard(luke);

		scn.LSPass(); //Capture Luke With Lightsaber instead - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_BE_CAPTURED - Optional responses
		scn.DSPass();

		assertTrue(scn.DSChoiceAvailable("Seize"));
		assertTrue(scn.DSChoiceAvailable("Escape"));

		scn.DSChooseSeizeCaptive();

		scn.PassAllResponses();

		assertTrue(luke.isCaptive());
		assertEquals(dengar,luke.getEscort());
	}

	//demonstrates fixed https://github.com/PlayersCommittee/gemp-swccg-public/issues/926
	@Test
	public void DengarVCannotSelfCapture() {
		var scn = GetScenario();

		var luke = scn.GetLSCard("luke");
		var deflection = scn.GetLSCard("deflection");

		var dengar = scn.GetDSCard("dengar");
		var crimson_blaster = scn.GetDSCard("crimson_blaster");

		var site = scn.GetLSStartingLocation(); //interior

		scn.StartGame();

		scn.MoveCardsToLocation(site,luke,dengar);
		scn.AttachCardsTo(dengar,crimson_blaster);

		scn.MoveCardsToLSHand(deflection);

		scn.LSActivateForceCheat(3);

		scn.SkipToPhase(Phase.BATTLE);
		assertTrue(scn.GetDSForcePileCount() >= 2); //enough to battle and fire
		assertTrue(scn.DSCanInitiateBattle(site));
		scn.PrepareDSDestiny(7); //guarantee hit

		scn.DSInitiateBattle(site);
		assertTrue(scn.AwaitingDSWeaponsSegmentActions());
		scn.DSUseCardAction(crimson_blaster,"Fire");
		scn.DSChooseCard(luke);

		assertTrue(scn.LSPlayUsedInterruptAvailable(deflection));
		assertTrue(scn.LSPlayLostInterruptAvailable(deflection));

		scn.LSPlayLostInterrupt(deflection);
		scn.LSChooseCard(luke);
		scn.LSChooseCard(dengar);

		scn.DSPass(); //Use 3 Force - Optional responses
		scn.LSPass();

		scn.DSPass(); //Playing Blaster Deflection - Optional responses
		scn.LSPass();

		scn.DSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
		scn.LSPass();

		scn.DSPass(); //Fire Crimson Dawn Blaster - Optional responses
		scn.LSPass();

		scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
		scn.DSPass();

		scn.LSPass(); //DESTINY_DRAWN - Optional responses
		scn.DSPass();

		scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
		scn.DSPass();

		scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
		assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_HIT - Optional responses"));
		assertFalse(scn.DSDecisionAvailable("About to 'hit'"));
		assertFalse(scn.DSCardActionAvailable(dengar,"Capture"));
//		scn.DSUseCardAction(dengar,"Capture");
//		scn.DSChooseCard(dengar);
//
//		scn.LSPass(); //Capture Dengar (V) instead - Optional responses
//		scn.DSPass();
//
//		scn.LSPass(); //ABOUT_TO_BE_CAPTURED - Optional responses
//		scn.DSPass();
	}

}
