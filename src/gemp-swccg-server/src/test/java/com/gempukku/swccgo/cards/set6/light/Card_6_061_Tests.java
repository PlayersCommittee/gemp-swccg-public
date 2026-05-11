package com.gempukku.swccgo.cards.set6.light;

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

public class Card_6_061_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("deflection", "6_61");
					put("luke", "108_003"); //luke with saber
                    put("rebeltrooper","1_028");
				}},
				new HashMap<>()
				{{
					put("stormtrooper1", "1_194");
                    put("stormtrooper2", "1_194");
					put("blaster", "1_317");
					put("blasterRifleV", "200_141"); //Blaster Rifle (V)
					put("bobasBlaster", "5_179"); //Boba Fett's Blaster Rifle
					put("boba","5_091"); //Boba Fett
					put("eppBoba","108_005"); //Boba Fett with Blaster Rifle
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
	public void BlasterDeflectionStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Blaster Deflection
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Interrupt
		 * Subtype: Used or Lost
		 * Destiny: 4
		 * Game Text: USED: Cancel an attempt to use a character weapon to target your character of ability > 4.
         *      LOST: If your character of ability > 4 with a lightsaber was just targeted by a blaster,
         *      use 3 Force to re-target that blaster to an opponent's character present.
		 * Lore: A Jedi can anticipate the actions of his opponent and let the Force control his actions,
         *      causing him to effortlessly deflect an opponent's attacks.
		 * Set: Jabba's Palace
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("deflection").getBlueprint();

		assertEquals("Blaster Deflection", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
		assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
		assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.JABBAS_PALACE);
        }});
        assertEquals(ExpansionSet.JABBAS_PALACE,card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
	}

	@Test
	public void BlasterDeflectionUsedCancelsTargeting() {
		var scn = GetScenario();

		var deflection = scn.GetLSCard("deflection");
		var luke = scn.GetLSCard("luke");
		scn.MoveCardsToHand(deflection);

		var site = scn.GetLSStartingLocation();

		var stormtrooper1 = scn.GetDSCard("stormtrooper1");
        var blaster = scn.GetDSCard("blaster");

		scn.StartGame();

		scn.MoveCardsToLocation(site, luke, stormtrooper1);
		scn.AttachCardsTo(stormtrooper1, blaster);

		scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(blaster));
        scn.DSUseCardAction(blaster);
        scn.DSChooseCard(luke);
        scn.LSPass(); //Use 1 Force - Optional responses
        scn.DSPass();
		assertTrue(scn.LSPlayUsedInterruptAvailable(deflection));
        scn.LSPlayUsedInterrupt(deflection);
        scn.PassAllResponses();
	}

    @Test @Ignore
    public void BlasterDeflectionLostRetargetsNonFreeBlaster() {
        //demonstrates bug https://github.com/PlayersCommittee/gemp-swccg-public/issues/859

        var scn = GetScenario();

        var deflection = scn.GetLSCard("deflection");
        var luke = scn.GetLSCard("luke");
        var rebeltrooper = scn.GetLSCard("rebeltrooper");
        scn.MoveCardsToHand(deflection);

        var site = scn.GetLSStartingLocation();

        var stormtrooper1 = scn.GetDSCard("stormtrooper1");
        var stormtrooper2 = scn.GetDSCard("stormtrooper2");
        var blaster = scn.GetDSCard("blaster");

        scn.StartGame();

        scn.MoveCardsToLocation(site, luke, rebeltrooper, stormtrooper1,stormtrooper2);
        scn.AttachCardsTo(stormtrooper1, blaster);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(blaster));
        scn.DSUseCardAction(blaster);
        scn.DSChooseCard(luke);
        scn.LSPass(); //Use 1 Force - Optional responses
        scn.DSPass();
        assertTrue(scn.GetLSForcePileCount() >= 3);
			///FAILS HERE
        assertTrue(scn.LSPlayLostInterruptAvailable(deflection));
		scn.LSPlayLostInterrupt(deflection);
		assertTrue(scn.LSDecisionAvailable("Choose character to re-target from"));
		assertTrue(scn.LSHasCardChoiceAvailable(luke));
		assertFalse(scn.LSHasCardChoicesAvailable(rebeltrooper));
		assertFalse(scn.LSHasCardChoicesAvailable(stormtrooper1));
		assertFalse(scn.LSHasCardChoicesAvailable(stormtrooper2));
		scn.LSChooseCard(luke);
		assertTrue(scn.LSDecisionAvailable("Choose character to re-target to"));
		assertTrue(scn.LSHasCardChoicesAvailable(stormtrooper1, stormtrooper2));
		assertFalse(scn.LSHasCardChoiceAvailable(rebeltrooper));
		scn.LSChooseCard(stormtrooper2);

		scn.PassAllResponses();
    }

	@Test
	public void BlasterDeflectionLostRetargetsFreeBlaster() {

		var scn = GetScenario();

		var deflection = scn.GetLSCard("deflection");
		var luke = scn.GetLSCard("luke");
		var rebeltrooper = scn.GetLSCard("rebeltrooper");
		scn.MoveCardsToHand(deflection);

		var site = scn.GetLSStartingLocation();

		var stormtrooper1 = scn.GetDSCard("stormtrooper1");
		var stormtrooper2 = scn.GetDSCard("stormtrooper2");
		var blasterRifleV = scn.GetDSCard("blasterRifleV"); //fires for free

		scn.StartGame();

		scn.MoveCardsToLocation(site, luke, rebeltrooper, stormtrooper1,stormtrooper2);
		scn.AttachCardsTo(stormtrooper1, blasterRifleV);

		scn.SkipToDSTurn(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSWeaponsSegmentActions());
		assertTrue(scn.DSCardActionAvailable(blasterRifleV));
		scn.DSUseCardAction(blasterRifleV);
		scn.DSChooseCard(luke);
		assertTrue(scn.GetLSForcePileCount() >= 3);
		assertTrue(scn.LSPlayLostInterruptAvailable(deflection));
		scn.LSPlayLostInterrupt(deflection);
		assertTrue(scn.LSDecisionAvailable("Choose character to re-target from"));
		assertTrue(scn.LSHasCardChoiceAvailable(luke));
		assertFalse(scn.LSHasCardChoicesAvailable(rebeltrooper));
		assertFalse(scn.LSHasCardChoicesAvailable(stormtrooper1));
		assertFalse(scn.LSHasCardChoicesAvailable(stormtrooper2));
		scn.LSChooseCard(luke);
		assertTrue(scn.LSDecisionAvailable("Choose character to re-target to"));
		assertTrue(scn.LSHasCardChoicesAvailable(stormtrooper1, stormtrooper2));
		assertFalse(scn.LSHasCardChoiceAvailable(rebeltrooper));
		scn.LSChooseCard(stormtrooper2);

		scn.PassAllResponses();
	}

	@Test @Ignore
	public void BlasterDeflectionLostRetargetsRepeatedBlaster() {

		var scn = GetScenario();

		var deflection = scn.GetLSCard("deflection");
		var luke = scn.GetLSCard("luke");
		var rebeltrooper = scn.GetLSCard("rebeltrooper");
		scn.MoveCardsToHand(deflection);

		var site = scn.GetLSStartingLocation();

		var boba = scn.GetDSCard("boba");
		var stormtrooper2 = scn.GetDSCard("stormtrooper2");
		var bobasBlaster = scn.GetDSCard("bobasBlaster"); //can fire repeatedly

		scn.StartGame();

		scn.MoveCardsToLocation(site, luke, rebeltrooper, boba,stormtrooper2);
		scn.AttachCardsTo(boba, bobasBlaster);

		scn.SkipToDSTurn(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSWeaponsSegmentActions());
		assertTrue(scn.DSCardActionAvailable(bobasBlaster));
		scn.DSUseCardAction(bobasBlaster);
		scn.DSChooseCard(luke);
		//assertTrue(scn.GetLSForcePileCount() >= 3);
			///FAILS HERE on initial firing (because not free, but covered in other test)
		//assertTrue(scn.LSPlayLostInterruptAvailable(deflection));
		scn.PassAllResponses();

		assertTrue(scn.DSDecisionAvailable("repeatedly fire"));
		scn.DSChooseYes();
		scn.DSChooseCard(luke);

		assertTrue(scn.GetLSForcePileCount() >= 3);
			///FAILS HERE on repeated firing
		assertTrue(scn.LSPlayLostInterruptAvailable(deflection));
		scn.LSPlayLostInterrupt(deflection);
		assertTrue(scn.LSDecisionAvailable("Choose character to re-target from"));
		assertTrue(scn.LSHasCardChoiceAvailable(luke));
		assertFalse(scn.LSHasCardChoicesAvailable(rebeltrooper));
		assertFalse(scn.LSHasCardChoicesAvailable(boba));
		assertFalse(scn.LSHasCardChoicesAvailable(stormtrooper2));
		scn.LSChooseCard(luke);
		assertTrue(scn.LSDecisionAvailable("Choose character to re-target to"));
		assertTrue(scn.LSHasCardChoicesAvailable(boba, stormtrooper2));
		assertFalse(scn.LSHasCardChoiceAvailable(rebeltrooper));
		scn.LSChooseCard(stormtrooper2);

		scn.PassAllResponses();
	}

	@Test
	public void BlasterDeflectionLostRetargetsBuiltInBlaster() {

		var scn = GetScenario();

		var deflection = scn.GetLSCard("deflection");
		var luke = scn.GetLSCard("luke");
		var rebeltrooper = scn.GetLSCard("rebeltrooper");
		scn.MoveCardsToHand(deflection);

		var site = scn.GetLSStartingLocation();

		var eppBoba = scn.GetDSCard("eppBoba");
		var stormtrooper2 = scn.GetDSCard("stormtrooper2");

		scn.StartGame();

		scn.MoveCardsToLocation(site, luke, rebeltrooper, eppBoba,stormtrooper2);

		scn.SkipToDSTurn(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSWeaponsSegmentActions());
		assertTrue(scn.DSCardActionAvailable(eppBoba));
		scn.DSUseCardAction(eppBoba);
		scn.DSChooseCard(luke);
		assertTrue(scn.GetLSForcePileCount() >= 3);
		assertTrue(scn.LSPlayLostInterruptAvailable(deflection));
		scn.LSPlayLostInterrupt(deflection);
		assertTrue(scn.LSDecisionAvailable("Choose character to re-target from"));
		assertTrue(scn.LSHasCardChoiceAvailable(luke));
		assertFalse(scn.LSHasCardChoicesAvailable(rebeltrooper));
		assertFalse(scn.LSHasCardChoicesAvailable(eppBoba));
		assertFalse(scn.LSHasCardChoicesAvailable(stormtrooper2));
		scn.LSChooseCard(luke);
		assertTrue(scn.LSDecisionAvailable("Choose character to re-target to"));
		assertTrue(scn.LSHasCardChoicesAvailable(eppBoba, stormtrooper2));
		assertFalse(scn.LSHasCardChoiceAvailable(rebeltrooper));
		scn.LSChooseCard(stormtrooper2);

		scn.PassAllResponses();
	}

	//add more tests for both used and lost actions

	//used:
	// works with non-blaster
	// does not work for ability 4
	//lost:
	// does not work with non-blaster
	// does not work with < 3 force to pay cost
	// works with multi-target ?
	// does not work for ability 4
	// check initial targeting of ability <= 4 into a repeated shot on ability > 4 works
}
