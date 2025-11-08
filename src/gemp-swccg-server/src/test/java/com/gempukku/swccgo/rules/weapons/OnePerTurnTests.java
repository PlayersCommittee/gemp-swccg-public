package com.gempukku.swccgo.rules.weapons;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OnePerTurnTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
				}},
				new HashMap<>()
				{{
                    put("epp_vader","108_006");
                    put("saber","1_314"); //dark jedi lightsaber
                    put("sniper","2_139");
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

    //AR: P94 Weapons - Using Weapons
    //Unless specified otherwise, each character, vehicle or starfighter may use (as in using its game text) only one
    //weapon per turn (but see Characters - Warrior for a specific exception).

    @Test @Ignore
    public void UsingPermanentWeaponPreventsUsingOtherWeaponsForRestOfTurn() {
        //Test1: character with permanent weapon that 'uses' the permanent weapon may not use other attached weapons
        //       for remainder of turn
        var scn = GetScenario();

        var trooper = scn.GetLSFiller(1); //rebel trooper

        var epp_vader = scn.GetDSCard("epp_vader");
        var saber = scn.GetDSCard("saber");
        var sniper = scn.GetDSCard("sniper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, epp_vader, trooper);
        scn.AttachCardsTo(epp_vader,saber);

        scn.MoveCardsToDSHand(sniper);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(sniper));
        scn.DSPlayCard(sniper);
        scn.PassAllResponses();
        scn.DSHasCardChoiceAvailable(epp_vader);
        scn.DSChooseCard(epp_vader);
        scn.PassAllResponses();
        assertEquals(1,scn.GetLSLostPileCount()); //trooper lost

        assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.LSPass();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.DSCardActionAvailable(site,"drain"));

        scn.DSUseCardAction(site);
        scn.LSPass(); //FORCE_DRAIN_INITIATED - Optional responses
        //assertFalse(scn.DSAwaitingResponse("Force drain initiated at"); //no optional response available to add
        assertFalse(scn.DSCardActionAvailable(saber,"Add"));

        scn.PassAllResponses();

        //assertTrue(scn.LSAwaitingForceLossPayment());
        //scn.LSPayRemainingForceLossFromReserveDeck();
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(1,scn.GetLSLostPileCount()); //1
    }

    //add testing for using an attached weapon prevents using permanent weapon later in the turn
}
