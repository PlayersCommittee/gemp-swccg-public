package com.gempukku.swccgo.rules.weapons;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class TransferTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("blaster", "1_152");
                    put("jawa", "1_012");
                    put("jawaIonGun", "2_078");
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

    //AR: P95 Weapons - Transferring
    //During your deploy phase, you may transfer your
    //weapon (or device) from one of your characters,
    //vehicles, or starships to another such card present with
    //the targeted character, vehicle, or starship on which the
    //weapon is currently deployed by using Force equal to
    //the deploy cost of the weapon (or device) and obeying
    //all relevant deployment restrictions. (e.g. you cannot
    //transfer Vader's Lightsaber to Obi-Wan, or a Cloud City
    //Blaster to a character not on Cloud City).

    @Test
    public void CharacterMayTransferCharacterWeaponToOtherCharacterPresent() {
        //test1: top level action available on weapon to transfer
        //test2: can target to transfer to an eligible target present at site
        //test3: successfully transfers weapon to the target
        //test4: force cost equal to deploy cost is paid
        var scn = GetScenario();

        var blaster = scn.GetLSCard("blaster");
        var rebeltrooper1 = scn.GetLSFiller(1);
        var rebeltrooper2 = scn.GetLSFiller(2);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, rebeltrooper1, rebeltrooper2);
        scn.AttachCardsTo(rebeltrooper1,blaster);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(3,scn.GetLSForcePileCount()); //enough to pay 1 to transfer the blaster

        assertTrue(scn.LSCardActionAvailable(blaster,"Transfer")); //test1
        scn.LSUseCardAction(blaster,"Transfer");
        assertTrue(scn.LSHasCardChoiceAvailable(rebeltrooper2)); //test2
        assertFalse(scn.LSHasCardChoiceAvailable(rebeltrooper1));
        scn.LSChooseCard(rebeltrooper2);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(rebeltrooper2,blaster)); //test3
        assertEquals(2,scn.GetLSForcePileCount()); //test4
    }

    @Test
    public void CharacterMayOnlyTransferCharacterWeaponToEligibleTarget() {
        //must obey relevant deployment restrictions
        //blaster cannot be deployed on a (non-warrior) jawa, so it cannot be transferred to a jawa
        var scn = GetScenario();

        var blaster = scn.GetLSCard("blaster");
        var rebeltrooper1 = scn.GetLSFiller(1);
        var rebeltrooper2 = scn.GetLSFiller(2);
        var jawa = scn.GetLSCard("jawa");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, rebeltrooper1, rebeltrooper2, jawa);
        scn.AttachCardsTo(rebeltrooper1,blaster); //normally cannot be done and this weapon is inactive

        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.LSUseCardAction(blaster,"Transfer");
        assertTrue(scn.LSHasCardChoiceAvailable(rebeltrooper2));
        assertFalse(scn.LSHasCardChoiceAvailable(rebeltrooper1));
        assertFalse(scn.LSHasCardChoiceAvailable(jawa)); //test1
    }

    //add additional tests for coverage of:
    //presence:
    //  cannot transfer to characters at adjacent sites
    //  cannot transfer to character at same site but not present with (ex: inside enclosed vehicle)
    //  cannot transfer to opponent's character
    //  cannot transfer to inactive character (captive, missing)
    //deploy requirements:
    //  requires force available to pay cost equal to deploy cost
    //  uses force equal to deploy cost
    //timing
    //  only available during your deploy phase
    //restrictions
    //  can transfer multiple weapons
    //  can transfer same weapon multiple times

    //vehicle weapons

    //starship weapons
}
