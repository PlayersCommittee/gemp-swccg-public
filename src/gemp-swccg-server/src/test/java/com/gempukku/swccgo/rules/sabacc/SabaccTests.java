package com.gempukku.swccgo.rules.sabacc;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SabaccTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("ls_trooper_sabacc", "7_109");
                    put("ls_destiny5", "2_054"); //out of commision
                    put("ls_destiny3", "1_047"); //demotion
                    put("ls_clone", "1_005"); //c-3po (droid = clone card)
                }},
				new HashMap<>()
				{{
					put("ds_destiny5", "200_120"); //force push (V)
					put("ds_destiny3", "209_035"); //dr. chelli
                    put("ds_clone", "12_114"); //p-59 (droid = clone card)
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

    //unable to reproduce: https://github.com/PlayersCommittee/gemp-swccg-public/issues/58
	@Test
	public void SabaccCloneCardTarget() {
		//verifies:
		//clone card lets user choose a card to duplicate destiny value
        //clone card correctly duplicates target card's sabacc destiny value
        //able to complete sabacc game after resolving clone card value

		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var ls_trooper_sabacc = scn.GetLSCard("ls_trooper_sabacc");
        var ls_clone = scn.GetLSCard("ls_clone");
        var ls_destiny3 = scn.GetLSCard("ls_destiny3");
        var ls_destiny5 = scn.GetLSCard("ls_destiny5");
        var trooper = scn.GetLSFiller(1);

        var ds_clone = scn.GetDSCard("ds_clone");
        var ds_destiny3 = scn.GetDSCard("ds_destiny3");
        var ds_destiny5 = scn.GetDSCard("ds_destiny5");

        scn.StartGame();

		scn.MoveCardsToLSHand(ls_clone); //prevent drawing for sabacc
        scn.MoveCardsToLSHand(ls_trooper_sabacc);

        //prepare sabacc draws:
        scn.MoveCardsToTopOfDSReserveDeck(ds_clone,ds_destiny3,ds_destiny5);
        scn.MoveCardsToTopOfLSReserveDeck(ls_destiny3,ls_destiny5);

        scn.MoveCardsToLocation(site,trooper);

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSPass();
        assertTrue(scn.DSChoiceAvailable("yes")); //You have not activated Force. Do you want to Pass?
        scn.DSChoose("yes");

        assertTrue(scn.AwaitingLSActivatePhaseActions());
        assertTrue(scn.LSCardPlayAvailable(ls_trooper_sabacc));
        scn.LSPlayCard(ls_trooper_sabacc);

        assertTrue(scn.LSHasCardChoiceAvailable(trooper)); //character to play sabacc
        scn.LSChooseCard(trooper);

        scn.DSPass(); //Playing sabacc - Optional responses
        scn.LSPass();

        scn.DSPass(); //DRAW_CARD - Optional responses //LS card 1 draw
        scn.LSPass();

        scn.DSPass(); //DRAW_CARD - Optional responses //LS card 2 draw
        scn.LSPass();

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 1 draw
        scn.DSPass();

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 2 draw
        scn.DSPass();

        assertTrue(scn.DSChoiceAvailable("yes")); //Do you want to draw another sabacc card?
        scn.DSChoose("yes");

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 3 draw
        scn.DSPass();

        assertTrue(scn.LSChoiceAvailable("no")); //Do you want to draw another sabacc card?
        scn.LSChoose("no");

        assertTrue(scn.DSChoiceAvailable("no")); //Do you want to draw another sabacc card?
        scn.DSChoose("no");

        //choosing DSPass here fails because minimum requirement (setting value of clone cards) has not been met
        //(no way to verify "Done" option is unavailable?)
        //scn.DSPass(); //if uncommented, fails here because "Done" is not available

        assertEquals(7,scn.GetDSSabaccTotal()); //5 + 3 + -1 clone

        assertTrue(scn.DSHasCardChoiceAvailable(ds_clone));
        scn.DSChooseCard(ds_clone);
        assertTrue(scn.DSHasCardChoiceAvailable(ds_destiny3));
        assertTrue(scn.DSHasCardChoiceAvailable(ds_destiny5));
        scn.DSChooseCard(ds_destiny3);

        assertEquals(11,scn.GetDSSabaccTotal()); //5 + 3 + cloned 3
        assertEquals(8,scn.GetLSSabaccTotal()); //5 + 3
        //(no way to verify "Done" option is available?)
        scn.DSPass(); //choose 'Done'

        scn.DSPass(); //SABACC_TOTAL_CALCULATED - Optional responses
        scn.LSPass();

        scn.DSPass(); //SABACC_WINNER_DETERMINED - Optional responses
        scn.LSPass();

            //choose opponent's card to be lost
        assertTrue(scn.DSHasCardChoiceAvailable(ls_destiny3));
        assertTrue(scn.DSHasCardChoiceAvailable(ls_destiny5));
        scn.DSChooseCard(ls_destiny3);
        scn.PassAllResponses();

            //Choose order of cards to place in Used Pile
        assertTrue(scn.DSHasCardChoiceAvailable(ds_destiny3));
        scn.DSChooseCard(ds_destiny3);
        scn.PassAllResponses();

        assertTrue(scn.DSHasCardChoiceAvailable(ds_destiny5));
        scn.DSChooseCard(ds_destiny5);
        scn.PassAllResponses();

        //last card (ds_clone) automatically chosen
        assertTrue(scn.AwaitingDSActivatePhaseActions()); //completed Sabacc play
    }

    //add testing for:
    //wild card
    //clone card (additional)
    //stakes
    //perfect sabacc
    //results
}
