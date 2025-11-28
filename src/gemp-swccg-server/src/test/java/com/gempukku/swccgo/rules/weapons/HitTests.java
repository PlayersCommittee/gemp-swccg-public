package com.gempukku.swccgo.rules.weapons;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HitTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("beckett","213_042");
                    put("blaster","1_152");
                    put("trap","5_055"); //It's A Trap
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

    //AR: P96 Weapons - Hit
    //If the battle ends before the damage segment, then "hit" cards are immediately lost.  Any card "hit" but not
    //participating in a battle or an attack (e.g., a weapon is fired using an Interrupt such as Sniper, or the
    //character is excluded) is immediately lost.

    //shows issues:
    //https://github.com/PlayersCommittee/gemp-swccg-public/issues/691
    //https://github.com/PlayersCommittee/gemp-swccg-public/issues/807
    //https://github.com/PlayersCommittee/gemp-swccg-public/issues/806
    @Test @Ignore
    public void HitCharacterLostIfBattleCancelledBeforeWeaponsSegment() {

        var scn = GetScenario();

        var beckett = scn.GetLSCard("beckett");
        var blaster = scn.GetLSCard("blaster");
        var trap = scn.GetLSCard("trap");

        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, beckett, trooper1, trooper2);
        scn.AttachCardsTo(beckett,blaster);

        scn.MoveCardsToLSHand(trap);

        scn.LSActivateForceCheat(4); //1 to fire blaster, 3 to cancel battle
        scn.PrepareLSDestiny(7); //guarantee hit

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        assertTrue(scn.LSCardActionAvailable(beckett));
        assertTrue(scn.LSCardPlayAvailable(trap));
        scn.LSUseCardAction(beckett);
        scn.LSChooseCard(blaster); //choose weapon to fire
        scn.LSChooseCard(trooper1);

        scn.DSPass(); //Fire Blaster - Optional responses
        scn.LSPass();

        scn.DSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        scn.LSPass();

        scn.DSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.LSPass();

        scn.DSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.LSPass();

        scn.DSPass(); //ABOUT_TO_BE_HIT - Optional responses
        scn.LSPass();

        scn.DSPass(); //HIT - Optional responses
        scn.LSPass();

        scn.DSPass(); //FIRED_WEAPON - Optional responses
        scn.LSPass();

        scn.DSPass(); //BATTLE_INITIATED - Optional responses
        assertTrue(scn.LSCardPlayAvailable(trap));
        scn.LSPlayCard(trap);

        scn.DSPass(); //Use 3 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //Playing •It's A Trap! - Optional responses
        scn.LSPass();

            /// at this point, battleCanceled result becomes true and trigger checking in
            /// HitCardOutsideOfAttackOrBattleRule happens (but characters are still participating)
        scn.DSPass(); //BATTLE_CANCELED - Optional responses
        scn.LSPass();

        scn.DSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.LSPass();

        scn.DSPass(); //BATTLE_INITIATED - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingLSBattlePhaseActions()); //battle finished
        assertEquals(1,scn.GetLSLostPileCount()); //trap
        assertEquals(1,scn.GetDSLostPileCount()); //trooper1 sent to lost pile
    }
}
