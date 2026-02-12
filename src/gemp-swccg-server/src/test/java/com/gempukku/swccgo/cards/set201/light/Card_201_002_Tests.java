package com.gempukku.swccgo.cards.set201.light;

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

public class Card_201_002_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("mace", "201_002"); //Mace Windu (V)

					put("lightsaber", "211_033"); //Jedi Lightsaber (V)
					put("mace_saber", "201_021"); //Mace Windu's Lightsaber
				}},
				new HashMap<>()
				{{
					put("vader", "1_168"); //Dark Jedi
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
	public void MaceWinduVStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Mace Windu (V)
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Character
		 * Subtype: Jedi Master
		 * Destiny: 1
		 * Deploy: 7
		 * Power: 6
		 * Ability: 7
		 * Forfeit: 8
		 * Icons: Warrior, Coruscant, Episode 1, Virtual Set 1
		 * Persona: Mace
		 * Game Text: During battle, Mace may 'swing' one lightsaber twice.
		 * 		Fetts may not add battle destiny draws here. Immune to attrition <6 (<8 if with a Dark Jedi).
		 * Lore: Senior Jedi Council member who maintains rigorous adherence to the Code.
		 * 		Sent Qui-Gon to Naboo to accompany the Queen and learn more about the mysterious 'dark warrior'.
		 * Set: Set 1
		 * Rarity: V
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("mace").getBlueprint();

		assertEquals("Mace Windu", card.getTitle());
		assertTrue(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(7, card.getDeployCost(), scn.epsilon);
		assertEquals(6, card.getPower(), scn.epsilon);
		assertEquals(7, card.getAbility(), scn.epsilon);
		assertEquals(8, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.JEDI_MASTER);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
			add(Keyword.JEDI_COUNCIL_MEMBER);
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
			add(Persona.MACE);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.WARRIOR);
			add(Icon.CORUSCANT);
			add(Icon.EPISODE_I);
			add(Icon.VIRTUAL_SET_1);
			add(Icon.JEDI_MASTER);
		}});
		assertEquals(ExpansionSet.SET_1,card.getExpansionSet());
		assertEquals(Rarity.V,card.getRarity());
	}

	@Test
	public void MaceWinduVCanSwingLightsaberTwice() {
		//test1: Mace can 'swing' (fire) lightsaber a second time during battle
		//test2: Mace cannot 'swing' (fire) lightsaber three times during battle
		var scn = GetScenario();

		var mace = scn.GetLSCard("mace");
		var mace_saber = scn.GetLSCard("mace_saber");

		var site = scn.GetLSStartingLocation();

		var trooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, mace, trooper);
		scn.AttachCardsTo(mace,mace_saber);

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSInitiateBattle(site);

		assertEquals(1,scn.GetLSUsedPileCount()); //1 to initiate

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
		assertTrue(scn.LSCardActionAvailable(mace_saber));
		scn.LSUseCardAction(mace_saber);
		scn.LSChooseCard(trooper);
		scn.PassAllResponses();

		assertEquals(3,scn.GetLSUsedPileCount()); //1 to initiate + 2 first swing

		scn.DSPass();

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
		assertTrue(scn.LSCardActionAvailable(mace_saber)); //test1
		scn.LSUseCardAction(mace_saber);
		scn.LSChooseCard(trooper);
		scn.PassAllResponses();

		assertEquals(5,scn.GetLSUsedPileCount()); //1 to initiate + 2 first swing + 2 second swing

		scn.DSPass();

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
		assertFalse(scn.LSCardActionAvailable(mace_saber)); //test2
	}

	@Test
	public void MaceWinduVCanSwingOnlyOneLightsaberTwice() {
		//test1: Mace cannot 'swing' (fire) a different lightsaber from the first swing
		var scn = GetScenario();

		var mace = scn.GetLSCard("mace");
		var mace_saber = scn.GetLSCard("mace_saber");
		var lightsaber = scn.GetLSCard("lightsaber");

		var site = scn.GetLSStartingLocation();

		var trooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.MoveCardsToLocation(site, mace, trooper);
		scn.AttachCardsTo(mace,mace_saber,lightsaber);

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSInitiateBattle(site);

		assertEquals(1,scn.GetLSUsedPileCount()); //1 to initiate

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
		assertTrue(scn.LSCardActionAvailable(lightsaber));
		assertTrue(scn.LSCardActionAvailable(mace_saber));
		scn.LSUseCardAction(mace_saber);
		scn.LSChooseCard(trooper);
		scn.PassAllResponses();

		assertEquals(3,scn.GetLSUsedPileCount()); //1 to initiate + 2 first swing

		scn.DSPass();

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
		assertFalse(scn.LSCardActionAvailable(lightsaber)); //test1
		assertTrue(scn.LSCardActionAvailable(mace_saber));
		scn.LSUseCardAction(mace_saber);
		scn.LSChooseCard(trooper);
		scn.PassAllResponses();

		assertEquals(5,scn.GetLSUsedPileCount()); //1 to initiate + 2 first swing + 2 second swing

		scn.DSPass();

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
		assertFalse(scn.LSCardActionAvailable(mace_saber));
		assertFalse(scn.LSCardActionAvailable(lightsaber));
	}

	@Test
	public void MaceWinduVCanSwingLightsaberTwiceResetsEachBattle() {
		//demonstrates fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/248
		//and https://github.com/PlayersCommittee/gemp-swccg-public/issues/602

		//test1: Mace can 'swing' (fire) lightsaber twice during a battle
		//after firing a different weapon in a previous battle
		var scn = GetScenario();

		var mace = scn.GetLSCard("mace");
		var lightsaber = scn.GetLSCard("lightsaber");
		var mace_saber = scn.GetLSCard("mace_saber");

		var site = scn.GetLSStartingLocation();

		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(site, mace, vader);
		scn.AttachCardsTo(mace,lightsaber);

		scn.MoveCardsToLSHand(mace_saber);

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSInitiateBattle(site);

		scn.LSUseCardAction(lightsaber);
		scn.LSChooseCard(vader);
		scn.PassAllResponses();

		scn.DSPass();

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());

		scn.SkipToDamageSegment(false);
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSBattlePhaseActions());
		scn.SkipToDSTurn();

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(mace_saber);
		scn.LSChooseCard(mace);
		scn.PassAllResponses();

		scn.SkipToPhase(Phase.BATTLE);
		scn.LSInitiateBattle(site);

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
		assertTrue(scn.LSCardActionAvailable(mace_saber));
		scn.LSUseCardAction(mace_saber);
		scn.LSChooseCard(vader);
		scn.PassAllResponses();

		scn.DSPass();

		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
		assertTrue(scn.GetLSReserveDeckCount() >= 1); //enough to draw for swing
		assertTrue(scn.LSCardActionAvailable(mace_saber)); //test1
		scn.LSUseCardAction(mace_saber);
		scn.LSChooseCard(vader);
		scn.PassAllResponses();
	}

}
