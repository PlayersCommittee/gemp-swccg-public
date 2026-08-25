package com.gempukku.swccgo.rules.weapons;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
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
                    put("boushh","110_001");
                    put("satm","2_057"); //Sorry About The Mess
                }},
                new HashMap<>()
                {{
                    put("sniper","2_139");
                    put("yab","5_163"); //You Are Beaten
                    put("eppVader","108_006");
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

    @Test
    public void HitCharacterLostIfBattleCancelledBeforeWeaponsSegment() {
        //shows resolved https://github.com/PlayersCommittee/gemp-swccg-public/issues/691 (and related issues)
        //test1: hit character is lost immediately after battle is canceled
        //test2: hit character is lost before the card canceling the battle is fully resolved (It's A Trap! sent to Lost)

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

        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE"));
        scn.DSPass();
        scn.LSPass();

        assertEquals(Zone.TOP_OF_LOST_PILE,trooper1.getZone()); //test1
        assertEquals(0,scn.GetLSLostPileCount()); //test2

        scn.DSPass(); //LOST_FROM_TABLE - Optional responses
        scn.LSPass();

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

    @Test
    public void HitCharacterLostIfExcludedDuringBattle() {
        //Hit character with lightsaber and then exclude with You Are Beaten
        var scn = GetScenario();

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);

        var eppVader = scn.GetDSCard("eppVader");
        var yab = scn.GetDSCard("yab");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, eppVader, trooper1, trooper2);
        scn.MoveCardsToDSHand(yab);

        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareDSDestiny(6); //guarantee hit with saber swing
        scn.PrepareDSDestiny(7);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        scn.PassAllResponses();
        scn.DSUseCardAction(eppVader);
        scn.DSChooseCard(trooper1);
        scn.PassAllResponses();

        scn.LSPass();

        scn.DSPlayCard(yab);
        scn.DSChooseCard(trooper1);

        scn.LSPass(); //Use 2 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //Playing You Are Beaten - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_EXCLUDED_FROM_BATTLE"));
        scn.DSPass(); //ABOUT_TO_BE_EXCLUDED_FROM_BATTLE - Optional responses
        scn.LSPass();

        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE"));
        scn.DSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses
        scn.LSPass();

        assertTrue(scn.DSDecisionAvailable("LOST_FROM_TABLE"));
        assertEquals(Zone.TOP_OF_LOST_PILE, trooper1.getZone());
        scn.DSPass(); //LOST_FROM_TABLE - Optional responses
        scn.LSPass();

        scn.LSPass(); //EXCLUDED_FROM_BATTLE - Optional responses
        scn.DSPass();

        scn.LSPass(); //ATTRIBUTE_RESET_OR_MODIFIED - Optional responses
        scn.DSPass();

        scn.LSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    @Test
    public void HitCharacterLostOutsideOfBattle() {
        //Sniper to hit character with lightsaber during Control Phase
        var scn = GetScenario();

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);

        var eppVader = scn.GetDSCard("eppVader");
        var sniper = scn.GetDSCard("sniper");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, eppVader, trooper1, trooper2);
        scn.MoveCardsToDSHand(sniper);

        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareDSDestiny(6); //guarantee hit with saber swing
        scn.PrepareDSDestiny(7);

        scn.DSPlayCard(sniper);
        scn.DSChooseCard(eppVader);

        scn.LSPass(); //Playing Sniper - Optional responses
        scn.DSPass();

        scn.DSChooseCard(trooper1);

        scn.LSPass(); //Fire Vader's Lightsaber - Optional responses
        scn.DSPass();


        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();


        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();


        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
        scn.DSPass();

        scn.LSPass(); //FORFEIT_REDUCED_TO_ZERO - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE"));
        scn.DSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses
        scn.LSPass();

        assertTrue(scn.DSDecisionAvailable("LOST_FROM_TABLE"));
        assertEquals(Zone.TOP_OF_LOST_PILE, trooper1.getZone());
        scn.DSPass(); //LOST_FROM_TABLE - Optional responses
        scn.LSPass();

        scn.LSPass(); //Optional responses
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        scn.LSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSControlPhaseActions());
    }

    @Test
    public void HitUndercoverSpyLostOutsideOfBattle() {
        //Sniper to hit undercover spy with lightsaber during Control Phase
        var scn = GetScenario();

        var boushh = scn.GetLSCard("boushh");

        var eppVader = scn.GetDSCard("eppVader");
        var sniper = scn.GetDSCard("sniper");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, eppVader, boushh);
        scn.MakeCardGoUndercover(boushh);
        scn.MoveCardsToDSHand(sniper);

        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareDSDestiny(6); //guarantee hit with saber swing
        scn.PrepareDSDestiny(7);

        assertTrue(boushh.isUndercover());
        scn.DSPlayCard(sniper);
        scn.DSChooseCard(eppVader);

        scn.LSPass(); //Playing Sniper - Optional responses
        scn.DSPass();

        //only target is automatically chosen
        //scn.DSChooseCard(boushh);

        scn.LSPass(); //Fire Vader's Lightsaber - Optional responses
        scn.DSPass();


        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();


        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();


        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
        scn.DSPass();

        scn.LSPass(); //FORFEIT_REDUCED_TO_ZERO - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE"));
        scn.DSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses
        scn.LSPass();

        assertTrue(scn.DSDecisionAvailable("LOST_FROM_TABLE"));
        assertEquals(Zone.TOP_OF_LOST_PILE, boushh.getZone());
        scn.DSPass(); //LOST_FROM_TABLE - Optional responses
        scn.LSPass();

        scn.LSPass(); //Optional responses
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        scn.LSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSControlPhaseActions());
    }

    @Test
    public void HitEscortLostOutsideOfBattleReleasesCaptive() {
        //Sorry About The Mess used to hit escort during Control Phase causes the captive to be released
        var scn = GetScenario();

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);
        var blaster = scn.GetLSCard("blaster");
        var satm = scn.GetLSCard("satm");

        var eppVader = scn.GetDSCard("eppVader");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, eppVader, trooper1, trooper2);
        scn.AttachCardsTo(trooper1, blaster);
        scn.CaptureCardWith(eppVader, trooper2);
        scn.MoveCardsToLSHand(satm);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(7); //guarantee hit with blaster

        scn.LSPlayCard(satm);
        scn.LSChooseCard(blaster);

        scn.DSPass(); //Playing Sorry About The Mess - Optional responses
        scn.LSPass();

        //only one target available - auto selected
        //scn.DSChooseCard(eppVader);

        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("Choose release option"));
        scn.LSChoose("Rally");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.CardsAtLocation(site, trooper1));
        assertTrue(scn.CardsAtLocation(site, trooper2));
        assertEquals(Zone.TOP_OF_LOST_PILE,eppVader.getZone());
    }

}
