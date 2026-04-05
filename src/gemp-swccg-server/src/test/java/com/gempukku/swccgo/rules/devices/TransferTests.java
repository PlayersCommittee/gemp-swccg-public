package com.gempukku.swccgo.rules.devices;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransferTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("electrobinoculars", "1_035"); //electrobinoculars
                    put("jawa", "1_012");
                    put("jawaIonGun", "2_078");
                    put("landspeeder","1_151"); //SoroSuub V-35 Landspeeder, enclosed vehicle
                    put("boushh", "110_001"); //undercover spy
                    put("kessel","1_126");
                    put("toolkit","4_010"); //han's toolkit
                    put("xwing","1_146");
                    put("ywing","1_147");
                    put("skiff","6_088"); //(open non-creature vehicle)
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
    public void CharacterMayTransferDeviceToOtherCharacterPresent() {
        //test1: top level action available on device to transfer
        //test2: can target to transfer to an eligible target present at site
        //test3: successfully transfers device to the target
        //test4: force cost equal to deploy cost is paid
        var scn = GetScenario();

        var electrobinoculars = scn.GetLSCard("electrobinoculars");
        var rebeltrooper1 = scn.GetLSFiller(1);
        var rebeltrooper2 = scn.GetLSFiller(2);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, rebeltrooper1, rebeltrooper2);
        scn.AttachCardsTo(rebeltrooper1, electrobinoculars);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(3,scn.GetLSForcePileCount()); //enough to pay 1 to transfer the electrobinoculars

        assertTrue(scn.LSCardActionAvailable(electrobinoculars,"Transfer")); //test1
        scn.LSUseCardAction(electrobinoculars,"Transfer");
        assertTrue(scn.LSHasCardChoiceAvailable(rebeltrooper2)); //test2
        assertFalse(scn.LSHasCardChoiceAvailable(rebeltrooper1));
        scn.LSChooseCard(rebeltrooper2);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(rebeltrooper2, electrobinoculars)); //test3
        assertEquals(2,scn.GetLSForcePileCount()); //test4
    }

    @Test
    public void TransferRequiresAnyDeployRestrictionsAreMet() {
        //must obey relevant deployment restrictions
        //test1: electrobinoculars cannot be deployed on a (non-warrior) jawa, so it cannot be transferred to a jawa
        var scn = GetScenario();

        var electrobinoculars = scn.GetLSCard("electrobinoculars");
        var rebeltrooper1 = scn.GetLSFiller(1);
        var rebeltrooper2 = scn.GetLSFiller(2);
        var jawa = scn.GetLSCard("jawa");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, rebeltrooper1, rebeltrooper2, jawa);
        scn.AttachCardsTo(rebeltrooper1, electrobinoculars);

        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.LSUseCardAction(electrobinoculars,"Transfer");
        assertTrue(scn.LSHasCardChoiceAvailable(rebeltrooper2));
        assertFalse(scn.LSHasCardChoiceAvailable(rebeltrooper1));
        assertFalse(scn.LSHasCardChoiceAvailable(jawa)); //test1
    }

    @Test
    public void CannotTransferToUndercoverSpy() {
        //test1: electrobinoculars transfer cannot target an undercover spy (to transfer to)
        var scn = GetScenario();

        var electrobinoculars = scn.GetLSCard("electrobinoculars");
        var rebeltrooper1 = scn.GetLSFiller(1);
        var boushh = scn.GetLSCard("boushh");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(boushh);

        scn.MoveCardsToLocation(site, rebeltrooper1);
        scn.AttachCardsTo(rebeltrooper1, electrobinoculars);

        scn.LSActivateForceCheat(4);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(7,scn.GetLSForcePileCount());
        scn.LSDeployCard(boushh);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        scn.DSPass();

        assertTrue(boushh.isUndercover());
        assertTrue(scn.CardsAtLocation(site,boushh));

        assertFalse(scn.LSCardActionAvailable(electrobinoculars,"Transfer")); //test1
    }

    @Test
    public void CanTransferFromUndercoverSpy() {
        //test1: electrobinoculars transfer can target an active character to transfer to (from an undercover spy)
        var scn = GetScenario();

        var electrobinoculars = scn.GetLSCard("electrobinoculars");
        var rebeltrooper1 = scn.GetLSFiller(1);
        var boushh = scn.GetLSCard("boushh");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(boushh, electrobinoculars);

        scn.MoveCardsToLocation(site, rebeltrooper1);

        scn.LSActivateForceCheat(4);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(7,scn.GetLSForcePileCount());
        scn.LSDeployCard(boushh);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        scn.AttachCardsTo(boushh, electrobinoculars);
        scn.DSPass();

        assertTrue(boushh.isUndercover());
        assertTrue(scn.CardsAtLocation(site,boushh));

        assertTrue(scn.LSCardActionAvailable(electrobinoculars,"Transfer"));
        scn.LSUseCardAction(electrobinoculars,"Transfer");
        assertTrue(scn.LSHasCardChoiceAvailable(rebeltrooper1)); //test1
        assertFalse(scn.LSHasCardChoiceAvailable(boushh));

        scn.LSChooseCard(rebeltrooper1);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(rebeltrooper1, electrobinoculars));
    }


    @Test
    public void CannotTransferFromCharacterAtSiteToCharacterInEnclosedVehicle() {
        //test1: character in an enclosed vehicle is not 'present with' device holder, so no transfer allowed
        var scn = GetScenario();

        var electrobinoculars = scn.GetLSCard("electrobinoculars");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var landspeeder = scn.GetLSCard("landspeeder");

        scn.StartGame();

        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, landspeeder);
        scn.AttachCardsTo(rebelTrooper1, electrobinoculars);

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(rebelTrooper2,"Embark");
        scn.LSChooseOption("Passenger");

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.IsAboardAsPassenger(landspeeder,rebelTrooper2));

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardActionAvailable(electrobinoculars,"Transfer")); //test1
    }

    @Test
    public void CannotTransferFromCharacterInEnclosedVehicleToCharacterAtSite() {
        //test1: character at site is not 'present with' device holder in an enclosed vehicle, so no transfer allowed
        var scn = GetScenario();

        var electrobinoculars = scn.GetLSCard("electrobinoculars");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var landspeeder = scn.GetLSCard("landspeeder");

        scn.StartGame();

        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, landspeeder);
        scn.AttachCardsTo(rebelTrooper2, electrobinoculars);

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(rebelTrooper2,"Embark");
        scn.LSChooseOption("Passenger");

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.IsAboardAsPassenger(landspeeder,rebelTrooper2));

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardActionAvailable(electrobinoculars,"Transfer")); //test1
    }

    @Test
    public void CanTransferBetweenCharactersInEnclosedVehicle() {
        //test1: characters in same enclosed vehicle together are 'present with' and may transfer device
        var scn = GetScenario();

        var electrobinoculars = scn.GetLSCard("electrobinoculars");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var landspeeder = scn.GetLSCard("landspeeder");

        scn.StartGame();

        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, landspeeder);
        scn.AttachCardsTo(rebelTrooper2, electrobinoculars);

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(rebelTrooper2,"Embark");
        scn.LSChooseOption("Passenger");
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        scn.DSPass();

        scn.LSUseCardAction(rebelTrooper1,"Embark");
        scn.LSChooseOption("Passenger");
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSMovePhaseActions());

        assertTrue(scn.IsAboardAsPassenger(landspeeder,rebelTrooper2));
        assertTrue(scn.IsAboardAsPassenger(landspeeder,rebelTrooper1));

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardActionAvailable(electrobinoculars,"Transfer")); //test1
    }


    @Test
    public void CanTransferFromCharacterAtSiteToCharacterInOpenVehicle() {
        //test1: character in an open vehicle is 'present with' device holder, so transfer allowed
        var scn = GetScenario();

        var electrobinoculars = scn.GetLSCard("electrobinoculars");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var skiff = scn.GetLSCard("skiff");

        scn.StartGame();

        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, skiff);
        scn.AttachCardsTo(rebelTrooper1, electrobinoculars);

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(rebelTrooper2,"Embark");
        scn.LSChooseOption("Passenger");

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.IsAboardAsPassenger(skiff,rebelTrooper2));

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardActionAvailable(electrobinoculars,"Transfer")); //test1
    }

    @Test
    public void CanTransferFromCharacterInOpenVehicleToCharacterAtSite() {
        //test1: character at site is 'present with' device holder in an open vehicle, so transfer allowed
        var scn = GetScenario();

        var electrobinoculars = scn.GetLSCard("electrobinoculars");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var skiff = scn.GetLSCard("skiff");

        scn.StartGame();

        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, skiff);
        scn.AttachCardsTo(rebelTrooper2, electrobinoculars);

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(rebelTrooper2,"Embark");
        scn.LSChooseOption("Passenger");

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.IsAboardAsPassenger(skiff,rebelTrooper2));

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardActionAvailable(electrobinoculars,"Transfer")); //test1
    }

    @Test
    public void CanTransferBetweenStarships() {
        //test1: device can be transferred from one starship to another starship present at same location
        //test2: eligible starship can be chosen as recipient
        //test3: transfer succeeds
        //tets4: deploy cost paid
        var scn = GetScenario();

        var toolkit = scn.GetLSCard("toolkit");
        var xwing = scn.GetLSCard("xwing");
        var ywing = scn.GetLSCard("ywing");
        var kessel = scn.GetLSCard("kessel");

        scn.StartGame();

        scn.MoveLocationToTable(kessel);

        scn.MoveCardsToLocation(kessel, xwing, ywing);
        scn.AttachCardsTo(xwing, toolkit);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to pay toolkit deploy cost to transfer
        assertTrue(scn.LSCardActionAvailable(toolkit,"Transfer")); //test1
        scn.LSUseCardAction(toolkit,"Transfer");
        assertTrue(scn.LSHasCardChoiceAvailable(ywing)); //test2
        scn.LSChooseCard(ywing);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(ywing, toolkit)); //test3
        assertEquals(1,scn.GetLSUsedPileCount()); //test4
    }

    @Test
    public void CanTransferFromEnclosedToOpenVehicle() {
        //test1: device can be transferred from one enclosed vehicle to another open vehicle present at same location
        //test2: eligible vehicle can be chosen as recipient
        //test3: transfer succeeds
        //tets4: deploy cost paid
        var scn = GetScenario();

        var toolkit = scn.GetLSCard("toolkit");
        var landspeeder = scn.GetLSCard("landspeeder");
        var skiff = scn.GetLSCard("skiff");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, landspeeder, skiff);
        scn.AttachCardsTo(landspeeder, toolkit);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to pay toolkit deploy cost to transfer
        assertTrue(scn.LSCardActionAvailable(toolkit,"Transfer")); //test1
        scn.LSUseCardAction(toolkit,"Transfer");
        assertTrue(scn.LSHasCardChoiceAvailable(skiff)); //test2
        scn.LSChooseCard(skiff);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(skiff, toolkit)); //test3
        assertEquals(1,scn.GetLSUsedPileCount()); //test4
    }

    @Test
    public void CanTransferFromOpenToEnclosedVehicle() {
        //test1: device can be transferred from one enclosed vehicle to another open vehicle present at same location
        //test2: eligible vehicle can be chosen as recipient
        //test3: transfer succeeds
        //tets4: deploy cost paid
        var scn = GetScenario();

        var toolkit = scn.GetLSCard("toolkit");
        var landspeeder = scn.GetLSCard("landspeeder");
        var skiff = scn.GetLSCard("skiff");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, landspeeder, skiff);
        scn.AttachCardsTo(skiff, toolkit);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to pay toolkit deploy cost to transfer
        assertTrue(scn.LSCardActionAvailable(toolkit,"Transfer")); //test1
        scn.LSUseCardAction(toolkit,"Transfer");
        assertTrue(scn.LSHasCardChoiceAvailable(landspeeder)); //test2
        scn.LSChooseCard(landspeeder);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(landspeeder, toolkit)); //test3
        assertEquals(1,scn.GetLSUsedPileCount()); //test4
    }

    //add additional tests for coverage of:
    //presence:
    //  cannot transfer to characters at adjacent sites
    //  cannot transfer to opponent's character
    //  cannot transfer to inactive character (captive, missing)
    //deploy requirements:
    //  requires force available to pay cost equal to deploy cost
    //timing
    //  only available during your deploy phase
    //restrictions
    //  can transfer multiple devices
    //  can transfer same device multiple times

}
