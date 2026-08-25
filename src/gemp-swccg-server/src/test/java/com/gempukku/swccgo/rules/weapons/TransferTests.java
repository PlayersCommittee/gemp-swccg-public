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
                    put("landspeeder","1_151"); //SoroSuub V-35 Landspeeder, enclosed vehicle
                    put("boushh", "110_001"); //undercover spy
                    put("xwing","1_146");
                    put("ywing","1_147");
                    put("proton","1_158"); //proton torpedoes
                    put("kessel","1_126");
                    put("skiff","6_088"); //open vehicle
                }},
                new HashMap<>()
                {{
                    put("laser","6_174"); //Antipersonnel Laser Cannon
                    put("sandcrawler","1_309"); //(enclosed transport vehicle)
                    put("skiff","6_173"); //(open transport vehicle)
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
    public void TransferRequiresAnyDeployRestrictionsAreMet() {
        //must obey relevant deployment restrictions
        //test1: blaster cannot be deployed on a (non-warrior) jawa, so it cannot be transferred to a jawa
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

    @Test
    public void CannotTransferToUndercoverSpy() {
        //test1: blaster cannot target an undercover spy to transfer to
        var scn = GetScenario();

        var blaster = scn.GetLSCard("blaster");
        var rebeltrooper1 = scn.GetLSFiller(1);
        var boushh = scn.GetLSCard("boushh");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(boushh);

        scn.MoveCardsToLocation(site, rebeltrooper1);
        scn.AttachCardsTo(rebeltrooper1,blaster); //normally cannot be done and this weapon is inactive

        scn.LSActivateForceCheat(4);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(7,scn.GetLSForcePileCount());
        scn.LSDeployCard(boushh);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        scn.DSPass();

        assertTrue(boushh.isUndercover());
        assertTrue(scn.CardsAtLocation(site,boushh));

        assertFalse(scn.LSCardActionAvailable(blaster,"Transfer")); //test1
    }

    @Test
    public void CanTransferFromUndercoverSpy() {
        //test1: blaster can target an active character to transfer to (from an undercover spy)
        var scn = GetScenario();

        var blaster = scn.GetLSCard("blaster");
        var rebeltrooper1 = scn.GetLSFiller(1);
        var boushh = scn.GetLSCard("boushh");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(boushh,blaster);

        scn.MoveCardsToLocation(site, rebeltrooper1);

        scn.LSActivateForceCheat(4);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(7,scn.GetLSForcePileCount());
        scn.LSDeployCard(boushh);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        scn.AttachCardsTo(boushh,blaster); //normally cannot be done and this weapon is inactive
        scn.DSPass();

        assertTrue(boushh.isUndercover());
        assertTrue(scn.CardsAtLocation(site,boushh));

        assertTrue(scn.LSCardActionAvailable(blaster,"Transfer")); //test1
        scn.LSUseCardAction(blaster,"Transfer");
        assertTrue(scn.LSHasCardChoiceAvailable(rebeltrooper1));
        assertFalse(scn.LSHasCardChoiceAvailable(boushh));

        scn.LSChooseCard(rebeltrooper1);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(rebeltrooper1,blaster)); //test3
    }


    @Test
    public void CannotTransferFromCharacterAtSiteToCharacterInEnclosedVehicle() {
        //test1: character in an enclosed vehicle is not 'present with' weapon holder, so no transfer allowed
        var scn = GetScenario();

        var blaster = scn.GetLSCard("blaster");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var landspeeder = scn.GetLSCard("landspeeder");

        scn.StartGame();

        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, landspeeder);
        scn.AttachCardsTo(rebelTrooper1, blaster);

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(rebelTrooper2,"Embark");
        scn.LSChooseOption("Passenger");

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.IsAboardAsPassenger(landspeeder,rebelTrooper2));

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardActionAvailable(blaster,"Transfer")); //test1
    }

    @Test
    public void CannotTransferFromCharacterInEnclosedVehicleToCharacterAtSite() {
        //test1: character at site is not 'present with' weapon holder in an enclosed vehicle, so no transfer allowed
        var scn = GetScenario();

        var blaster = scn.GetLSCard("blaster");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var landspeeder = scn.GetLSCard("landspeeder");

        scn.StartGame();

        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, landspeeder);
        scn.AttachCardsTo(rebelTrooper2, blaster);

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(rebelTrooper2,"Embark");
        scn.LSChooseOption("Passenger");

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.IsAboardAsPassenger(landspeeder,rebelTrooper2));

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardActionAvailable(blaster,"Transfer")); //test1
    }

    @Test
    public void CanTransferBetweenCharactersInEnclosedVehicle() {
        //test1: characters in same enclosed vehicle together are 'present with' and may transfer weapons
        var scn = GetScenario();

        var blaster = scn.GetLSCard("blaster");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var landspeeder = scn.GetLSCard("landspeeder");

        scn.StartGame();

        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, landspeeder);
        scn.AttachCardsTo(rebelTrooper2, blaster);

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

        assertTrue(scn.LSCardActionAvailable(blaster,"Transfer")); //test1
    }

    @Test
    public void CanTransferFromCharacterAtSiteToCharacterInOpenVehicle() {
        //test1: character in an open vehicle is 'present with' weapon holder, so transfer allowed
        var scn = GetScenario();

        var blaster = scn.GetLSCard("blaster");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var skiff = scn.GetLSCard("skiff");

        scn.StartGame();

        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, skiff);
        scn.AttachCardsTo(rebelTrooper1, blaster);

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(rebelTrooper2,"Embark");
        scn.LSChooseOption("Passenger");

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.IsAboardAsPassenger(skiff,rebelTrooper2));

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardActionAvailable(blaster,"Transfer")); //test1
    }

    @Test
    public void CanTransferFromCharacterInOpenVehicleToCharacterAtSite() {
        //test1: character at site is 'present with' weapon holder in an open vehicle, so transfer allowed
        var scn = GetScenario();

        var blaster = scn.GetLSCard("blaster");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var skiff = scn.GetLSCard("skiff");

        scn.StartGame();

        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLocation(site, rebelTrooper1, rebelTrooper2, skiff);
        scn.AttachCardsTo(rebelTrooper2, blaster);

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSUseCardAction(rebelTrooper2,"Embark");
        scn.LSChooseOption("Passenger");

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.IsAboardAsPassenger(skiff,rebelTrooper2));

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardActionAvailable(blaster,"Transfer")); //test1
    }

    @Test
    public void CanTransferBetweenStarships() {
        //test1: starship weapon can be transferred from one starship to another starship present at same location
        //test2: eligible starship can be chosen as recipient
        //test3: transfer succeeds
        //tets4: deploy cost paid
        var scn = GetScenario();

        var proton = scn.GetLSCard("proton");
        var xwing = scn.GetLSCard("xwing");
        var ywing = scn.GetLSCard("ywing");
        var kessel = scn.GetLSCard("kessel");

        scn.StartGame();

        scn.MoveLocationToTable(kessel);

        scn.MoveCardsToLocation(kessel, xwing, ywing);
        scn.AttachCardsTo(xwing, proton);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to pay proton torpedoes deploy cost to transfer
        assertTrue(scn.LSCardActionAvailable(proton,"Transfer")); //test1
        scn.LSUseCardAction(proton,"Transfer");
        assertTrue(scn.LSHasCardChoiceAvailable(ywing)); //test2
        scn.LSChooseCard(ywing);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(ywing,proton)); //test3
        assertEquals(1,scn.GetLSUsedPileCount()); //test4
    }

    @Test
    public void CanTransferFromEnclosedToOpenVehicle() {
        //test1: vehicle weapon can be transferred from one enclosed vehicle to another open vehicle present at same location
        //test2: eligible vehicle can be chosen as recipient
        //test3: transfer succeeds
        //tets4: deploy cost paid
        var scn = GetScenario();

        var laser = scn.GetDSCard("laser");
        var sandcrawler = scn.GetDSCard("sandcrawler");
        var skiff = scn.GetDSCard("skiff");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, sandcrawler, skiff);
        scn.AttachCardsTo(sandcrawler, laser);

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.GetDSForcePileCount() >= 3); //enough to pay laser cannon deploy cost to transfer
        assertTrue(scn.DSCardActionAvailable(laser,"Transfer")); //test1
        scn.DSUseCardAction(laser,"Transfer");
        assertTrue(scn.DSHasCardChoiceAvailable(skiff)); //test2
        scn.DSChooseCard(skiff);

        scn.LSPass(); //Use 3 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(skiff,laser)); //test3
        assertEquals(3,scn.GetDSUsedPileCount()); //test4
    }

    @Test
    public void CanTransferFromOpenToEnclosedVehicle() {
        //test1: vehicle weapon can be transferred from one enclosed vehicle to another open vehicle present at same location
        //test2: eligible vehicle can be chosen as recipient
        //test3: transfer succeeds
        //tets4: deploy cost paid
        var scn = GetScenario();

        var laser = scn.GetDSCard("laser");
        var sandcrawler = scn.GetDSCard("sandcrawler");
        var skiff = scn.GetDSCard("skiff");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, sandcrawler, skiff);
        scn.AttachCardsTo(skiff, laser);

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.GetDSForcePileCount() >= 3); //enough to pay laser cannon deploy cost to transfer
        assertTrue(scn.DSCardActionAvailable(laser,"Transfer")); //test1
        scn.DSUseCardAction(laser,"Transfer");
        assertTrue(scn.DSHasCardChoiceAvailable(sandcrawler)); //test2
        scn.DSChooseCard(sandcrawler);

        scn.LSPass(); //Use 3 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //TRANSFERRED_DEVICE_OR_WEAPON - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(sandcrawler,laser)); //test3
        assertEquals(3,scn.GetDSUsedPileCount()); //test4
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
    //  can transfer multiple weapons
    //  can transfer same weapon multiple times

}
