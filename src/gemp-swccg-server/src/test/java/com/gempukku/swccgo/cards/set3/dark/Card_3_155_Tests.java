package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_3_155_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_019");
                    put("leia", "1_017");
                    put("lsTrooper", "1_028");
                    put("xwing1", "1_146");
                    put("xwing2", "1_146");
                }},
                new HashMap<>() {{
                    put("blizzard2", "3_155");
                    put("trooper", "1_194");
                    put("siteTrooper", "1_194");
                    put("siteTrooper2", "1_194");
                    put("punishingOne", "4_171");
                    put("dengar", "4_100");
                    put("dantooine", "1_282");
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
    public void Blizzard2PassengerSharesEnclosedVehicleImmunity() {
        var scn = GetScenario();
        var blizzard2 = scn.GetDSCard("blizzard2");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard2);
        scn.BoardAsPassenger(blizzard2, trooper);

        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), blizzard2), scn.epsilon);
        assertEquals("Passenger aboard enclosed Blizzard 2 shares the vehicle's immunity < 4",
                4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), trooper), scn.epsilon);
    }

    @Test
    public void Blizzard2PassengerIsNotForfeitedToAttrition1() {
        var scn = GetScenario();
        var blizzard2 = scn.GetDSCard("blizzard2");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var lsTrooper = scn.GetLSCard("lsTrooper");
        var siteTrooper = scn.GetDSCard("siteTrooper");
        var siteTrooper2 = scn.GetDSCard("siteTrooper2");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard2, luke, leia, lsTrooper, siteTrooper, siteTrooper2);
        scn.BoardAsPassenger(blizzard2, trooper);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(1);
        scn.PrepareDSDestiny(1);
        assertTrue("Light should be able to initiate at Marketplace", scn.LSCanInitiateBattle(site));
        scn.LSInitiateBattle(site);
        scn.SkipToDamageSegment(true);

        assertTrue("Dark should owe attrition; unpaid=" + scn.GetUnpaidDSAttrition(),
                scn.GetUnpaidDSAttrition() >= 1);
        for (int i = 0; i < 20; i++) {
            if (!scn.AwaitingDSAttritionPayment() && !scn.AwaitingDSBattleDamagePayment()) {
                break;
            }
            assertFalse("Stormtrooper aboard immune Blizzard 2 must not be forfeited to attrition 1",
                    scn.DSHasCardChoiceAvailable(trooper));
            if (scn.GetDSReserveDeckCount() > 0) {
                scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
                scn.PassAllResponses();
            } else {
                break;
            }
        }
        assertTrue(trooper.getZone() == Zone.ATTACHED || trooper.getZone() == Zone.AT_LOCATION);
        assertEquals(Zone.AT_LOCATION, blizzard2.getZone());
    }

    @Test
    public void PunishingOnePassengerDoesNotShareStarshipImmunity() {
        var scn = GetScenario();
        var punishingOne = scn.GetDSCard("punishingOne");
        var dengar = scn.GetDSCard("dengar");
        var trooper = scn.GetDSCard("trooper");
        var system = scn.GetDSCard("dantooine");

        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, punishingOne);
        scn.BoardAsPilot(punishingOne, dengar);
        scn.BoardAsPassenger(punishingOne, trooper);

        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), punishingOne), scn.epsilon);
        assertEquals("Characters aboard a starship do not share that starship's immunity",
                0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), trooper), scn.epsilon);
    }

    @Test
    public void PunishingOnePassengerCanBeForfeitedInSpaceBattle() {
        var scn = GetScenario();
        var punishingOne = scn.GetDSCard("punishingOne");
        var dengar = scn.GetDSCard("dengar");
        var trooper = scn.GetDSCard("trooper");
        var xwing1 = scn.GetLSCard("xwing1");
        var xwing2 = scn.GetLSCard("xwing2");
        var system = scn.GetDSCard("dantooine");

        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, punishingOne, xwing1, xwing2);
        scn.BoardAsPilot(punishingOne, dengar);
        scn.BoardAsPassenger(punishingOne, trooper);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(1);
        scn.PrepareDSDestiny(1);
        assertTrue("Light should be able to initiate at Dantooine", scn.LSCanInitiateBattle(system));
        scn.LSInitiateBattle(system);
        scn.SkipToDamageSegment(true);

        assertTrue("Dark should owe attrition or battle damage so a forfeit choice is posted; unpaid attrition="
                        + scn.GetUnpaidDSAttrition() + " unpaid battle damage=" + scn.GetUnpaidDSBattleDamage(),
                scn.GetUnpaidDSAttrition() >= 1 || scn.GetUnpaidDSBattleDamage() >= 1);
        assertTrue("Characters aboard a starship can be forfeited in a space battle",
                scn.DSHasCardChoiceAvailable(trooper));
        scn.DSChooseCard(trooper);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, trooper);
    }
}
