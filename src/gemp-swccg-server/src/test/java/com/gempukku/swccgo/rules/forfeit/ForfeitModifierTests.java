package com.gempukku.swccgo.rules.forfeit;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ForfeitModifierTests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("cantina", "1_128"); //Tatooine: Cantina - (dark text) aliens here are each forfeit +1
                    put("tallie", "211_035"); //Tallie Lintra In Blue 1 - characters here may not have their forfeit increased above their printed value
                    put("techs", "221_072"); //Scanner Techs (V) - forfeit values may not be increased here
                    put("sic","6_040"); //Sic-Six - subtracts 2 from forfeit of opponent's pilot at same site
                    put("lando","5_005"); //Lando Calrissian - (use with below)
                    put("man","13_028"); //Lando's Not A System, He's A Man - target a character present with (CC) Lando. For remainder of turn, target is forfeit = 0
                    put("ord","3_064"); //Ord Mantell - (light text) if you control, each of opponent's bounty hunters is forfeit -2
                }},
                new HashMap<>()
                {{
                    put("zuck", "4_107"); //Zuckuss - unique, dagobah, bounty hunter, pilot, alien with printed forfeit value 3
                    put("wrapped", "224_001"); //All Wrapped Up (V) - dag and cc bounty hunters are forfeit +2
                    put("sorry", "226_006"); //I'm Sorry (V) - unique characters with printed forfeit < 5 are forfeit +2 (limit +2)
                    put("chokk","12_099"); //Chokk - your characters present may not have their forfeit value reduced
                    put("vcsd","2_155"); //Victory-Class Star Destroyer (ship with capacity for characters)
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation, //Cloud City: Chasm Walkway (no forfeit-related text)
                StartingSetup.DefaultDSSpaceSystem, //Dantooine (no forfeit-related text)
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /////////////////BASIC TESTS: try to test simplest case for each type of modifier that can affect forfeit
    @Test
    public void ForfeitIncrease() {
        //Zuckuss at Cantina
        //Printed forfeit: 3
        //Modifiers: +1
        //Limits: none
        //Prevention: none
        //Reset: none
        //Result: 4
        var scn = GetScenario();

        var cantina = scn.GetLSCard("cantina");

        var zuck = scn.GetDSCard("zuck");

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, zuck);

        assertEquals(4,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitReduce() {
        //Zuckuss at site with Sic-Six
        //Printed forfeit: 3
        //Modifiers: -2
        //Limits: none
        //Prevention: none
        //Reset: none
        //Result: 1
        var scn = GetScenario();

        var site = scn.GetLSStartingLocation();
        var sic = scn.GetLSCard("sic");

        var zuck = scn.GetDSCard("zuck");

        scn.StartGame();

        scn.MoveCardsToLocation(site, zuck, sic);

        assertEquals(1,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitIncreasePrevention() {
        //Zuckuss at Cantina with Scanner Techs (V)
        //Printed forfeit: 3
        //Modifiers: +1
        //Limits: none
        //Prevention: increase prevented
        //Reset: none
        //Result: 3
        var scn = GetScenario();

        var cantina = scn.GetLSCard("cantina");
        var techs = scn.GetLSCard("techs");

        var zuck = scn.GetDSCard("zuck");

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, zuck);
        scn.AttachCardsTo(cantina,techs);

        assertEquals(3,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitReducePrevention() {
        //Zuckuss at site with Chokk and Sic-Six
        //Printed forfeit: 3
        //Modifiers: -2
        //Limits: none
        //Prevention: reduce prevented
        //Reset: none
        //Result: 3
        var scn = GetScenario();

        var site = scn.GetLSStartingLocation();
        var sic = scn.GetLSCard("sic");

        var zuck = scn.GetDSCard("zuck");
        var chokk = scn.GetDSCard("chokk");

        scn.StartGame();

        scn.MoveCardsToLocation(site, zuck, chokk, sic);

        assertEquals(3,scn.GetForfeit(zuck));
    }

    //demonstrates: https://github.com/PlayersCommittee/gemp-swccg-public/issues/989
    @Test @Ignore
    public void ForfeitIncreaseLimitX() {
        //Zuckuss at Cantina with I'm Sorry (V)
        //Printed forfeit: 3
        //Modifiers: +1, +2
        //Limits: +2
        //Prevention: none
        //Reset: none
        //Result: 5
        var scn = GetScenario();

        var cantina = scn.GetLSCard("cantina");

        var zuck = scn.GetDSCard("zuck");
        var sorry = scn.GetDSCard("sorry");

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, zuck);
        scn.MoveCardsToDSSideOfTable(sorry);

            ///FAILS HERE - actual: 6
        assertEquals(5,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitIncreaseLimitPrinted() {
        //Zuckuss at system with Tallie Lintra In Blue 1 and All Wrapped Up (V) on table
        //Printed forfeit: 3
        //Modifiers: +2
        //Limits: printed forfeit
        //Prevention: none
        //Reset: none
        //Result: 3
        var scn = GetScenario();

        var tallie = scn.GetLSCard("tallie");

        var system = scn.GetDSStartingLocation();
        var zuck = scn.GetDSCard("zuck");
        var vcsd = scn.GetDSCard("vcsd");
        var wrapped = scn.GetDSCard("wrapped");

        scn.StartGame();

        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, vcsd);
        scn.BoardAsPassenger(vcsd, zuck);
        scn.MoveCardsToDSSideOfTable(wrapped);
        assertEquals(5,scn.GetForfeit(zuck));

        scn.MoveCardsToLocation(system, tallie);

        assertEquals(3,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitResetTo0() {
        //Zuckuss at site with Lando using Lando's Not A System, He's A Man
        //Printed forfeit: 3
        //Modifiers: none
        //Limits: none
        //Prevention: none
        //Reset: = 0
        //Result: 0
        var scn = GetScenario();

        var site = scn.GetLSStartingLocation();
        var lando = scn.GetLSCard("lando");
        var man = scn.GetLSCard("man");

        var zuck = scn.GetDSCard("zuck");

        scn.StartGame();

        scn.MoveCardsToLocation(site, zuck, lando);
        scn.MoveCardsToLSSideOfTable(man);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();
        scn.LSUseCardAction(man);
        scn.LSChooseCard(zuck);
        scn.PassAllResponses();

        assertEquals(0,scn.GetForfeit(zuck));
    }

    /////////////////COMPLEX TESTS: combine multiple forfeit modifiers
    @Test
    public void ForfeitIncreaseIsAdditive() {
        //Zuckuss at Cantina with All Wrapped Up (V) on table
        //Printed forfeit: 3
        //Modifiers: +1, +2
        //Limits: none
        //Prevention: none
        //Reset: none
        //Result: 6
        var scn = GetScenario();

        var cantina = scn.GetLSCard("cantina");

        var zuck = scn.GetDSCard("zuck");
        var wrapped = scn.GetDSCard("wrapped");

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, zuck);
        scn.MoveCardsToDSSideOfTable(wrapped);

        assertEquals(6,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitReduceIsAdditive() {
        //Zuckuss at site with Sic-Six and Ord Mantell controlled by Light
        //Printed forfeit: 3
        //Modifiers: -2, -2
        //Limits: none
        //Prevention: none
        //Reset: none
        //Result: 0
        var scn = GetScenario();

        var site = scn.GetLSStartingLocation();
        var sic = scn.GetLSCard("sic");
        var ord = scn.GetLSCard("ord");
        var tallie = scn.GetLSCard("tallie");

        var zuck = scn.GetDSCard("zuck");

        scn.StartGame();

        scn.MoveLocationToTable(ord);
        scn.MoveCardsToLocation(ord, tallie);
        scn.MoveCardsToLocation(site, zuck, sic);

        assertEquals(0,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitIncreaseReduceAreAdditive() {
        //Zuckuss at Cantina with Sic-Six
        //Printed forfeit: 3
        //Modifiers: +1, -2
        //Limits: none
        //Prevention: none
        //Reset: none
        //Result: 2
        var scn = GetScenario();

        var cantina = scn.GetLSCard("cantina");
        var sic = scn.GetLSCard("sic");

        var zuck = scn.GetDSCard("zuck");

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, zuck, sic);

        assertEquals(2,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitMultipleIncreaseLimitsAppliesLowest() {
        //Zuckuss at system with Tallie Lintra In Blue 1, All Wrapped Up (V) and I'm Sorry (V) on table
        //Printed forfeit: 3
        //Modifiers: +2, +2
        //Limits: printed forfeit, +2
        //Prevention: none
        //Reset: none
        //Result: 3
        var scn = GetScenario();

        var tallie = scn.GetLSCard("tallie");

        var system = scn.GetDSStartingLocation();
        var zuck = scn.GetDSCard("zuck");
        var vcsd = scn.GetDSCard("vcsd");
        var wrapped = scn.GetDSCard("wrapped");
        var sorry = scn.GetDSCard("sorry");

        scn.StartGame();

        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, vcsd, tallie);
        scn.BoardAsPassenger(vcsd, zuck);
        scn.MoveCardsToDSSideOfTable(wrapped);
        scn.MoveCardsToDSSideOfTable(sorry);

        assertEquals(3,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitReducePreventionAllowsIncrease() {
        //Zuckuss at Cantina with Chokk and Sic-Six
        //Printed forfeit: 3
        //Modifiers: -2, +1
        //Limits: none
        //Prevention: reduce prevented
        //Reset: none
        //Result: 4
        var scn = GetScenario();

        var cantina = scn.GetLSCard("cantina");
        var sic = scn.GetLSCard("sic");

        var zuck = scn.GetDSCard("zuck");
        var chokk = scn.GetDSCard("chokk");

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, zuck, chokk, sic);

        assertEquals(4,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitIncreasePreventionAllowsReduce() {
        //Zuckuss at Cantina with Sic-Six and Scanner Techs (V)
        //Printed forfeit: 3
        //Modifiers: +1, -2
        //Limits: none
        //Prevention: increase prevented
        //Reset: none
        //Result: 1
        var scn = GetScenario();

        var cantina = scn.GetLSCard("cantina");
        var techs = scn.GetLSCard("techs");
        var sic = scn.GetLSCard("sic");

        var zuck = scn.GetDSCard("zuck");

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, zuck, sic);
        scn.AttachCardsTo(cantina,techs);

        assertEquals(1,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitReducePreventionPreventsResetTo0() {
        //AR entry for Reduce: "Any time a value is modified or reset to a lower number, that value has been reduced."
        //Zuckuss at site with Chokk and Lando using Lando's Not A System, He's A Man
        //Printed forfeit: 3
        //Modifiers: none
        //Limits: none
        //Prevention: reduce prevented
        //Reset: = 0
        //Result: 3
        var scn = GetScenario();

        var site = scn.GetLSStartingLocation();
        var lando = scn.GetLSCard("lando");
        var man = scn.GetLSCard("man");

        var zuck = scn.GetDSCard("zuck");
        var chokk = scn.GetDSCard("chokk");

        scn.StartGame();

        scn.MoveCardsToLocation(site, zuck, chokk, lando);
        scn.MoveCardsToLSSideOfTable(man);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();
        scn.LSUseCardAction(man);
        scn.LSChooseCard(zuck);
        scn.PassAllResponses();

        assertEquals(3,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitSetToXSupersedesIncreaseAndReduce() {
        //Zuckuss at Cantina with Sic-Six and Lando using Lando's Not A System, He's A Man
        //Printed forfeit: 3
        //Modifiers: +1, -2
        //Limits: none
        //Prevention: none
        //Reset: = 0
        //Result: 0
        var scn = GetScenario();

        var cantina = scn.GetLSCard("cantina");
        var lando = scn.GetLSCard("lando");
        var man = scn.GetLSCard("man");

        var zuck = scn.GetDSCard("zuck");

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, zuck, lando);
        scn.MoveCardsToLSSideOfTable(man);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();
        scn.LSUseCardAction(man);
        scn.LSChooseCard(zuck);
        scn.PassAllResponses();

        assertEquals(0,scn.GetForfeit(zuck));
    }

    @Test
    public void ForfeitIncreaseLimitXAppliesAfterIncreaseAndReduce() {
        //Zuckuss at Cantina with Sic-Six, I'm Sorry (V) on table
        //Printed forfeit: 3
        //Modifiers: +1, +2, -2
        //Limits: +2
        //Prevention: none
        //Reset: none
        //Result: 4
        var scn = GetScenario();

        var cantina = scn.GetLSCard("cantina");
        var sic = scn.GetLSCard("sic");

        var zuck = scn.GetDSCard("zuck");
        var sorry = scn.GetDSCard("sorry");

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, zuck, sic);
        scn.MoveCardsToDSSideOfTable(sorry);

        assertEquals(4,scn.GetForfeit(zuck));
    }

}
