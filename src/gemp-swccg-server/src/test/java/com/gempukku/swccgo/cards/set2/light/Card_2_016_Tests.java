package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
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

public class Card_2_016_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("ra7", "2_016");
                    put("jawa", "1_012");
					put("jawaIonGun", "2_078");
                    put("blaster1", "1_152");
                    put("blaster2", "1_152");
                    put("blaster3", "1_152");
                    put("lightsaber", "1_155");
					put("landspeeder","1_151"); //SoroSuub V-35 Landspeeder, enclosed vehicle
				}},
				new HashMap<>()
				{{
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
	public void RA7StatsAndKeywordsAreCorrect() {
		/**
		 * Title: RA-7 (Aray-Seven)
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Character
		 * Subtype: Droid
		 * Destiny: 4
		 * Deploy: 2
		 * Power: 1
		 * Ability: 0
		 * Forfeit: 3
		 * Icons: A New Hope
		 * Game Text: May transfer character weapons (for free) to or from your other characters present. May carry up to four such weapons at one time.
		 * Lore: The RA line of servant droids has fifth-degree primary programming. Low intelligence with capabilities for mental labor only. Common among nobles and high-ranking officials.
		 * Set: A New Hope
		 * Rarity: C2
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("ra7").getBlueprint();

		assertEquals("RA-7 (Aray-Seven)", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(4, card.getDestiny(), scn.epsilon);
		assertEquals(2, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertEquals(0, card.getAbility(), scn.epsilon);
		assertEquals(3, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.DROID);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
		}});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.SERVANT);
        }});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.DROID);
			add(Icon.A_NEW_HOPE);
		}});
		assertEquals(ExpansionSet.A_NEW_HOPE,card.getExpansionSet());
		assertEquals(Rarity.C2,card.getRarity());
	}

	@Test
	public void RA7CanTransferWeaponToSelfForFree() {
        //test1: transfer weapon to RA7 is not available outside of Deploy Phase (normal window for transfer actions)
		//test2: transfer weapon to RA7 is successful
		//test3: transfer was free
		var scn = GetScenario();

		var ra7 = scn.GetLSCard("ra7");
		var blaster1 = scn.GetLSCard("blaster1");
		var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);

		scn.StartGame();

        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site,ra7,rebelTrooper1,rebelTrooper2);
        scn.AttachCardsTo(rebelTrooper1,blaster1);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(ra7)); //test1

        assertEquals(3,scn.GetLSForcePileCount());

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(blaster1,"Transfer")); //this is the 'normal' transfer action
        assertTrue(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) to"));
        assertFalse(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) from"));
        scn.LSUseCardAction(ra7,"Transfer weapon (for free) to");
        assertTrue(scn.LSDecisionAvailable("Choose weapon"));
        assertTrue(scn.LSHasCardChoiceAvailable(blaster1));
        scn.LSChooseCard(blaster1);

        scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.IsAttachedTo(rebelTrooper1,blaster1));
        assertTrue(scn.IsAttachedTo(ra7,blaster1)); //test2
		assertEquals(3,scn.GetLSForcePileCount()); //test3
	}

	@Test
	public void RA7CanTransferWeaponToSelfIsUnlimitedAction() {
		//test1: transfer weapon to RA7 actions can be used multiple times during same turn
		var scn = GetScenario();

		var ra7 = scn.GetLSCard("ra7");
		var blaster1 = scn.GetLSCard("blaster1");
		var blaster2 = scn.GetLSCard("blaster2");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);

		scn.StartGame();

		var site = scn.GetLSStartingLocation();

		scn.MoveCardsToLocation(site,ra7,rebelTrooper1,rebelTrooper2);
		scn.AttachCardsTo(rebelTrooper1,blaster1);
		scn.AttachCardsTo(rebelTrooper2,blaster2);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSUseCardAction(ra7,"Transfer weapon (for free) to");
		scn.LSChooseCard(blaster1);

		scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
		scn.LSPass();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertTrue(scn.IsAttachedTo(ra7,blaster1));

		scn.DSPass();

		assertTrue(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) to"));
		scn.LSUseCardAction(ra7,"Transfer weapon (for free) to");
		scn.LSChooseCard(blaster2);

		scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
		scn.LSPass();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertTrue(scn.IsAttachedTo(ra7,blaster2)); //test1
	}

	@Test
	public void RA7CannotTransferWeaponToSelfIfHolding4Weapons() {
		//test1: transfer weapon to RA7 action cannot be used if already holding 4 weapons
		var scn = GetScenario();

		var ra7 = scn.GetLSCard("ra7");
		var blaster1 = scn.GetLSCard("blaster1");
		var blaster2 = scn.GetLSCard("blaster2");
		var blaster3 = scn.GetLSCard("blaster3");
		var jawaIonGun = scn.GetLSCard("jawaIonGun");
		var lightsaber = scn.GetLSCard("lightsaber");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);

		scn.StartGame();

		var site = scn.GetLSStartingLocation();

		scn.MoveCardsToLocation(site,ra7,rebelTrooper1,rebelTrooper2);
		scn.AttachCardsTo(rebelTrooper1,blaster1);
		scn.AttachCardsTo(rebelTrooper2,blaster2);
		scn.AttachCardsTo(ra7,blaster3,jawaIonGun,lightsaber);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSUseCardAction(ra7,"Transfer weapon (for free) to");
		scn.LSChooseCard(blaster1);

		scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
		scn.LSPass();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertTrue(scn.IsAttachedTo(ra7,blaster1));

		scn.DSPass();

		assertFalse(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) to"));
	}

	//this test can't really be performed to validate because there are no weapons that can deploy on RA-7
	//(so there are also no weapons that can be transferred to RA-7 with the 'normal' top level action)
	//manually confirmed NOT working correctly by temporarily allowing Blaster deploy filter to include droids
	//transfer checking does not seem to account for MayNotDeployToTargetModifier
	@Test @Ignore
	public void RA7CannotBeTargetedForWeaponTransferIfHolding4Weapons() {
		//test1: (regular) transfer weapon action cannot target ra7 if already holding 4 weapons
		var scn = GetScenario();

		var ra7 = scn.GetLSCard("ra7");
		var blaster1 = scn.GetLSCard("blaster1"); ///replace with a hypothetical weapon that can be deployed on RA-7
		var blaster2 = scn.GetLSCard("blaster2");
		var blaster3 = scn.GetLSCard("blaster3");
		var jawaIonGun = scn.GetLSCard("jawaIonGun");
		var lightsaber = scn.GetLSCard("lightsaber");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);

		scn.StartGame();

		var site = scn.GetLSStartingLocation();

		scn.MoveCardsToLocation(site,ra7,rebelTrooper1,rebelTrooper2);
		scn.AttachCardsTo(rebelTrooper1,blaster1);
		scn.AttachCardsTo(ra7,blaster2,blaster3,jawaIonGun,lightsaber);

		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(scn.GetLSForcePileCount() >= 1); ///replace with hypothetical weapon's deploy cost
		assertTrue(scn.LSCardActionAvailable(blaster1,"Transfer")); //this is the 'normal' transfer action
		scn.LSUseCardAction(blaster1,"Transfer");
		assertFalse(scn.LSHasCardChoiceAvailable(rebelTrooper1));
		assertTrue(scn.LSHasCardChoiceAvailable(rebelTrooper2));
		assertFalse(scn.LSHasCardChoiceAvailable(ra7));
	}

	//this test can't really be performed to validate because there are no weapons that can deploy on RA-7
	//manually confirmed working by temporarily allowing Blaster deploy filter to include droids
	@Test @Ignore
	public void RA7CannotBeTargetedForWeaponDeployIfHolding4Weapons() {
		//test1: deploy weapon action cannot target ra7 if already holding 4 weapons
		var scn = GetScenario();

		var ra7 = scn.GetLSCard("ra7");
		var blaster1 = scn.GetLSCard("blaster1"); ///replace with a hypothetical weapon that can be deployed on RA-7
		var blaster2 = scn.GetLSCard("blaster2");
		var blaster3 = scn.GetLSCard("blaster3");
		var jawaIonGun = scn.GetLSCard("jawaIonGun");
		var lightsaber = scn.GetLSCard("lightsaber");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);

		scn.StartGame();

		var site = scn.GetLSStartingLocation();

		scn.MoveCardsToLocation(site,ra7,rebelTrooper1,rebelTrooper2);
		scn.AttachCardsTo(ra7,blaster2,blaster3,jawaIonGun,lightsaber);
		scn.MoveCardsToLSHand(blaster1);

		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(scn.GetLSForcePileCount() >= 1); ///replace with hypothetical weapon's deploy cost
		assertTrue(scn.LSDeployAvailable(blaster1));
		scn.LSDeployCard(blaster1);
		assertTrue(scn.LSHasCardChoiceAvailable(rebelTrooper1));
		assertTrue(scn.LSHasCardChoiceAvailable(rebelTrooper2));
		assertFalse(scn.LSHasCardChoiceAvailable(ra7));
	}

	@Test
	public void RA7CanTransferWeaponToOtherCharacterPresentForFree() {
		//test1: transfer weapon from RA7 is not available outside of Deploy Phase (normal window for transfer actions)
		//test2: transfer weapon from RA7 is successful
		//test3: transfer was free
		var scn = GetScenario();

		var ra7 = scn.GetLSCard("ra7");
		var blaster1 = scn.GetLSCard("blaster1");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);

		scn.StartGame();

		var site = scn.GetLSStartingLocation();

		scn.MoveCardsToLocation(site,ra7,rebelTrooper1,rebelTrooper2);
		scn.AttachCardsTo(ra7,blaster1);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertFalse(scn.LSCardActionAvailable(ra7)); //test1

		assertEquals(3,scn.GetLSForcePileCount());

		scn.SkipToPhase(Phase.DEPLOY);
		assertTrue(scn.LSCardActionAvailable(blaster1,"Transfer")); //this is the 'normal' transfer action
		assertFalse(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) to"));
		assertTrue(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) from"));
		scn.LSUseCardAction(ra7,"Transfer weapon (for free) from");
		assertTrue(scn.LSDecisionAvailable("Choose weapon"));
		assertTrue(scn.LSHasCardChoiceAvailable(blaster1));
		scn.LSChooseCard(blaster1);

		assertTrue(scn.LSHasCardChoiceAvailable(rebelTrooper1));
		assertTrue(scn.LSHasCardChoiceAvailable(rebelTrooper2));
		scn.LSChooseCard(rebelTrooper1);

		scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
		scn.LSPass();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertTrue(scn.IsAttachedTo(rebelTrooper1,blaster1)); //test2
		assertFalse(scn.IsAttachedTo(ra7,blaster1));
		assertEquals(3,scn.GetLSForcePileCount()); //test3
	}

	@Test
	public void RA7CanTransferWeaponFromSelfIsUnlimitedAction() {
		//test1: transfer weapon from RA7 actions can be used multiple times during same turn
		var scn = GetScenario();

		var ra7 = scn.GetLSCard("ra7");
		var blaster1 = scn.GetLSCard("blaster1");
		var blaster2 = scn.GetLSCard("blaster2");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);

		scn.StartGame();

		var site = scn.GetLSStartingLocation();

		scn.MoveCardsToLocation(site,ra7,rebelTrooper1,rebelTrooper2);
		scn.AttachCardsTo(ra7,blaster1,blaster2);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSUseCardAction(ra7,"Transfer weapon (for free) from");
		scn.LSChooseCard(blaster1);
		scn.LSChooseCard(rebelTrooper1);

		scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
		scn.LSPass();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertTrue(scn.IsAttachedTo(rebelTrooper1,blaster1));

		scn.DSPass();

		assertTrue(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) from"));
		scn.LSUseCardAction(ra7,"Transfer weapon (for free) from");
		scn.LSChooseCard(blaster2);
		scn.LSChooseCard(rebelTrooper2);

		scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
		scn.LSPass();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertTrue(scn.IsAttachedTo(rebelTrooper2,blaster2)); //test1
	}

	@Test
	public void RA7CanOnlyTransferWeaponsToEligibleTarget() {
		//test1: transfer weapon from RA7 must obey deployment restrictions on destination target
		var scn = GetScenario();

		var ra7 = scn.GetLSCard("ra7");
		var blaster1 = scn.GetLSCard("blaster1");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var jawa = scn.GetLSCard("jawa");

		scn.StartGame();

		var site = scn.GetLSStartingLocation();

		scn.MoveCardsToLocation(site,ra7,rebelTrooper1,jawa);
		scn.AttachCardsTo(ra7,blaster1);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertFalse(scn.LSCardActionAvailable(ra7)); //test1

		scn.SkipToPhase(Phase.DEPLOY);
		assertTrue(scn.LSCardActionAvailable(blaster1,"Transfer")); //this is the 'normal' transfer action
		assertFalse(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) to"));
		assertTrue(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) from"));
		scn.LSUseCardAction(ra7,"Transfer weapon (for free) from");
		assertTrue(scn.LSDecisionAvailable("Choose weapon"));
		assertTrue(scn.LSHasCardChoiceAvailable(blaster1));
		scn.LSChooseCard(blaster1);

		assertTrue(scn.LSHasCardChoiceAvailable(rebelTrooper1));
		assertFalse(scn.LSHasCardChoiceAvailable(jawa)); //test1
		scn.LSChooseCard(rebelTrooper1);

		scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
		scn.LSPass();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertTrue(scn.IsAttachedTo(rebelTrooper1,blaster1)); //test2
	}

	@Test
	public void RA7TransferToRequiresPresentWith() {
		//test1: transfer weapon to RA7 cannot target weapon carried by your character that is not present (in enclosed vehicle)
		//test2: transfer weapon to RA7 can target weapon carried by your character that is present (both in enclosed vehicle)
		var scn = GetScenario();

		var ra7 = scn.GetLSCard("ra7");
		var blaster1 = scn.GetLSCard("blaster1");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var landspeeder = scn.GetLSCard("landspeeder");

		scn.StartGame();

		var site = scn.GetLSStartingLocation();

		scn.MoveCardsToLocation(site,ra7,rebelTrooper1,landspeeder);
		scn.AttachCardsTo(rebelTrooper1,blaster1);

		scn.SkipToLSTurn(Phase.MOVE);
		scn.LSUseCardAction(rebelTrooper1,"Embark");
		scn.LSChooseOption("Passenger");

		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSMovePhaseActions());
		assertTrue(scn.IsAboardAsPassenger(landspeeder,rebelTrooper1));

		scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.DEPLOY);

		assertFalse(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) to")); //test1

		scn.SkipToPhase(Phase.MOVE);
		scn.LSUseCardAction(ra7,"Embark");
		//scn.LSChooseOption("Passenger"); //RA-7 auto-passenger

		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSMovePhaseActions());
		assertTrue(scn.IsAboardAsPassenger(landspeeder,ra7));

		scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) to")); //test2
		scn.LSUseCardAction(ra7,"Transfer weapon (for free) to");
		scn.LSChooseCard(blaster1);

		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertFalse(scn.IsAttachedTo(rebelTrooper1,blaster1));
		assertTrue(scn.IsAttachedTo(ra7,blaster1));
	}

	@Test
	public void RA7TransferFromRequiresPresentWith() {
		//test1: transfer weapon from RA7 cannot target a character that is not present (in enclosed vehicle)
		//test2: transfer weapon from RA7 can target a character that is present (both in enclosed vehicle)
		var scn = GetScenario();

		var ra7 = scn.GetLSCard("ra7");
		var blaster1 = scn.GetLSCard("blaster1");
		var rebelTrooper1 = scn.GetLSFiller(1);
		var landspeeder = scn.GetLSCard("landspeeder");

		scn.StartGame();

		var site = scn.GetLSStartingLocation();

		scn.MoveCardsToLocation(site,ra7,rebelTrooper1,landspeeder);
		scn.AttachCardsTo(ra7,blaster1);

		scn.SkipToLSTurn(Phase.MOVE);
		scn.LSUseCardAction(rebelTrooper1,"Embark");
		scn.LSChooseOption("Passenger");

		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSMovePhaseActions());
		assertTrue(scn.IsAboardAsPassenger(landspeeder,rebelTrooper1));

		scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.DEPLOY);

		assertFalse(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) from")); //test1

		scn.SkipToPhase(Phase.MOVE);
		scn.LSUseCardAction(ra7,"Embark");
		//scn.LSChooseOption("Passenger"); //RA-7 auto-passenger

		scn.PassAllResponses();
		assertTrue(scn.AwaitingDSMovePhaseActions());
		assertTrue(scn.IsAboardAsPassenger(landspeeder,ra7));

		scn.SkipToDSTurn();
		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(scn.LSCardActionAvailable(ra7,"Transfer weapon (for free) from")); //test2
		scn.LSUseCardAction(ra7,"Transfer weapon (for free) from");
		scn.LSChooseCard(blaster1);
		scn.LSChooseCard(rebelTrooper1);

		scn.PassAllResponses();

		assertTrue(scn.AwaitingDSDeployPhaseActions());
		assertTrue(scn.IsAttachedTo(rebelTrooper1,blaster1));
		assertFalse(scn.IsAttachedTo(ra7,blaster1));
	}

}
