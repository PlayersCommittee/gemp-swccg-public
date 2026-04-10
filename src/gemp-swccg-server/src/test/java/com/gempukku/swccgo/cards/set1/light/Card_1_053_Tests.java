package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_053_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("slug", "1_053"); //K'lor'slug
                    put("ywing","1_147");
                    put("kessel","1_126");
					put("snowspeeder","3_069"); //(enclosed vehicle with perm pilot)
					put("skiff","6_088"); //open vehicle
					put("pilot","1_027"); //rebel pilot
				}},
				new HashMap<>()
				{{
					put("tiescout","1_305"); //tie scout (starship)
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
	public void KlorslugStatsAndKeywordsAreCorrect() {
		/**
		 * Title: K'lor'slug
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Effect
		 * Destiny: 3
		 * Icons: Effect
		 * Game Text: Deploy on your side of table. For each unit of ability you have present during a battle,
         *      you may use 1 Force to raise your total power by 1. Ability used in this way cannot also
         *      be used to draw destiny.
		 * Lore: Dejarik of venomous swamp creature from Noe'ha'on. Keen senses of smell and vision. Dangerous hunter.
         *      Lays eggs - hundreds of ravenously hungry hatchlings.
		 * Set: Premiere
		 * Rarity: R1
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("slug").getBlueprint();

		assertEquals("K'lor'slug", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertFalse(card.hasVirtualSuffix());
		assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
		assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
		assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEJARIK);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.PREMIERE,card.getExpansionSet());
        assertEquals(Rarity.R1,card.getRarity());

	}

	@Test
	public void KlorslugCanSpendForceToIncreasePowerTable() {
		//test1: slug deploys free
		//test2: slug deploys on table
		//test3: can use slug during battle - weapons segment action
		//test4: can raise power by 1 (with force available)
		//test5: paid force cost of 1

		var scn = GetScenario();

		var slug = scn.GetLSCard("slug");
        var trooper = scn.GetLSFiller(1);

		var stormtrooper = scn.GetDSFiller(1);

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(slug);

        scn.MoveCardsToLocation(site, trooper, stormtrooper);

		scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(slug));
        scn.LSPlayCard(slug);
		scn.PassAllResponses();

		assertEquals(0,scn.GetLSUsedPileCount()); //test1
		assertEquals(Zone.SIDE_OF_TABLE, slug.getZone()); //test2

		scn.SkipToPhase(Phase.BATTLE);
		scn.LSInitiateBattle(site);
		scn.PassAllResponses();
		assertTrue(scn.AwaitingLSWeaponsSegmentActions());
		assertEquals(1,scn.GetLSTotalPower()); //before adding
		assertTrue(scn.LSCardActionAvailable(slug, "raise"));
		scn.LSUseCardAction(slug, "raise");
		assertTrue(scn.LSDecisionAvailable("amount of Force"));
		assertEquals(1,scn.LSGetChoiceMax());
		scn.LSDecided(1); //test3
		scn.PassAllResponses();

		assertEquals(2,scn.GetLSTotalPower()); //test4
		assertEquals(2, scn.GetLSUsedPileCount()); //test5 (1 to battle, 1 for slug)
	}

	@Test
	public void KlorslugLimitsPowerByForceAvailable() {
		//test1: with 2 force available and 3 ability, limited to increasing power by 2
		//test2: force cost of 2 paid
		var scn = GetScenario();

		var slug = scn.GetLSCard("slug");
		var trooper = scn.GetLSFiller(1);
		var trooper2 = scn.GetLSFiller(2);
		var trooper3 = scn.GetLSFiller(3);

		var stormtrooper = scn.GetDSFiller(1);

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSSideOfTable(slug);

		scn.MoveCardsToLocation(site, trooper, trooper2, trooper3, stormtrooper);

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSInitiateBattle(site);
		scn.PassAllResponses();
		assertEquals(2,scn.GetLSForcePileCount());
		assertEquals(3,scn.GetLSTotalPower()); //before adding
		scn.LSUseCardAction(slug, "raise");
		assertEquals(2,scn.LSGetChoiceMax()); //test1
		scn.LSDecided(2);
		scn.PassAllResponses();

		assertEquals(5,scn.GetLSTotalPower());
		assertEquals(3, scn.GetLSUsedPileCount()); //test2 (1 to battle, 2 for slug)
	}

	@Test
	public void KlorslugLimitsPowerByAbilityAvailable() {
		//test1: with 3 force available and 2 ability, limited to increasing power by 2
		//test2: force cost of 2 paid
		var scn = GetScenario();

		var slug = scn.GetLSCard("slug");
		var trooper = scn.GetLSFiller(1);
		var trooper2 = scn.GetLSFiller(2);

		var stormtrooper = scn.GetDSFiller(1);

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSSideOfTable(slug);

		scn.MoveCardsToLocation(site, trooper, trooper2, stormtrooper);

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSActivateForceCheat(1);
		scn.LSInitiateBattle(site);
		scn.PassAllResponses();
		assertEquals(3,scn.GetLSForcePileCount());
		assertEquals(2,scn.GetLSTotalPower()); //before adding
		scn.LSUseCardAction(slug, "raise");
		assertEquals(2,scn.LSGetChoiceMax()); //test1
		scn.LSDecided(2);
		scn.PassAllResponses();

		assertEquals(4,scn.GetLSTotalPower());
		assertEquals(3, scn.GetLSUsedPileCount()); //test2 (1 to battle, 2 for slug)
	}

	@Test
	public void KlorslugCanUseAbilityFromDriverOnOpenVehicle() {
		//test1: character on open vehicle is 'present'
		var scn = GetScenario();

		var slug = scn.GetLSCard("slug");
		var skiff = scn.GetLSCard("skiff");
		var trooper = scn.GetLSFiller(1);

		var stormtrooper = scn.GetDSFiller(1);

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSSideOfTable(slug);

		scn.MoveCardsToLSHand(trooper);

		scn.MoveCardsToLocation(site, skiff, stormtrooper);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(trooper);
		scn.LSChooseCard(skiff);
		scn.LSChoose("Driver");
		scn.PassAllResponses();
		assertTrue(scn.IsAttachedTo(skiff, trooper));

		scn.SkipToPhase(Phase.BATTLE);
		scn.LSInitiateBattle(site);
		scn.PassAllResponses();
		assertEquals(4,scn.GetLSTotalPower());
		assertTrue(scn.LSCardActionAvailable(slug, "raise"));
		scn.LSUseCardAction(slug, "raise");
		assertEquals(1,scn.LSGetChoiceMax()); //test1
		scn.LSDecided(1);
		scn.PassAllResponses();
		assertEquals(5,scn.GetLSTotalPower());
	}

	@Test
	public void KlorslugCannotUseAbilityFromVehiclePermPilot() {
		//test1: permanent pilot is not 'present' at location
		var scn = GetScenario();

		var slug = scn.GetLSCard("slug");
		var snowspeeder = scn.GetLSCard("snowspeeder");

		var stormtrooper = scn.GetDSFiller(1);

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSSideOfTable(slug);

		scn.MoveCardsToLocation(site, snowspeeder, stormtrooper);

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSInitiateBattle(site);
		scn.PassAllResponses();
		assertFalse(scn.LSCardActionAvailable(slug, "raise")); //test1
	}

	@Test
	public void KlorslugCannotUseAbilityFromStarshipPermPilot() {
		//test1: permanent pilot is not 'present' at location
		var scn = GetScenario();

		var slug = scn.GetLSCard("slug");
		var ywing = scn.GetLSCard("ywing");
		var kessel = scn.GetLSCard("kessel");

		var tiescout = scn.GetDSCard("tiescout");

		scn.StartGame();

		scn.MoveCardsToLSSideOfTable(slug);

		scn.MoveLocationToTable(kessel);

		scn.MoveCardsToLocation(kessel, ywing, tiescout);

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSInitiateBattle(kessel);
		scn.PassAllResponses();
		assertFalse(scn.LSCardActionAvailable(slug, "raise")); //test1
	}

	@Test
	public void KlorslugCannotUseAbilityFromStarshipPilot() {
		//test1: character pilot aboard is not 'present' at location
		var scn = GetScenario();

		var slug = scn.GetLSCard("slug");
		var ywing = scn.GetLSCard("ywing");
		var kessel = scn.GetLSCard("kessel");
		var pilot = scn.GetLSCard("pilot");

		var tiescout = scn.GetDSCard("tiescout");

		scn.StartGame();

		scn.MoveCardsToLSSideOfTable(slug);

		scn.MoveCardsToLSHand(pilot);

		scn.MoveLocationToTable(kessel);

		scn.MoveCardsToLocation(kessel, ywing, tiescout);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSDeployCard(pilot);
		scn.LSChooseCard(ywing);
		scn.LSChoose("Pilot");
		scn.PassAllResponses();
		assertTrue(scn.IsAttachedTo(ywing, pilot));

		scn.SkipToPhase(Phase.BATTLE);
		scn.LSInitiateBattle(kessel);
		scn.PassAllResponses();
		assertEquals(1,scn.GetLSForcePileCount()); //enough to use
		assertFalse(scn.LSCardActionAvailable(slug, "raise")); //test1
	}

	//other tests:
	//ability 'used' can't contribute to draw destiny
	//can't use more than once per battle
	//can use during battle on opponent's turn

}
