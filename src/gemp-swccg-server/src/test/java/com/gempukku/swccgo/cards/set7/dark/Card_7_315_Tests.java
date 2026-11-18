package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Patrol Craft (7_315) — simultaneous driver only when destination requires a pilot (cloud sector).
 * Closes #968.
 */
public class Card_7_315_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("rebel", "1_028"); // Rebel Trooper (LS filler / drain presence)
                    put("ls_cloud_car", "5_088"); // Cloud Car with permanent pilot (presence at cloud sector)
                    put("ls_bespin", "5_076"); // Bespin system
                }},
                new HashMap<>()
                {{
                    put("patrol_craft", "7_315");
                    put("mercenary_pilot", "112_013");
                    put("east_platform", "5_169"); // Cloud City: East Platform (Docking Bay) — site
                    put("bespin_cloud_city", "5_165"); // Bespin: Cloud City — cloud sector
                    put("bespin", "5_164"); // Bespin system
                    put("bantha", "1_307"); // other vehicle regression
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
    public void PatrolCraftStatsAndKeywordsAreCorrect() {
        /**
         * Title: Patrol Craft
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Vehicle
         * Subtype: Transport
         * Destiny: 4
         * Deploy: 1  Power: 1  Armor: —  Maneuver: 3  Landspeed: 3  Forfeit: 3
         * Game Text: Power +1 at Coruscant or Bespin. May add 1 driver and 1 passenger. May deploy or move as a 'react.'
         *      At cloud sectors, may move and be targeted by weapons like a starfighter.
         * Set: Special Edition
         * Rarity: C
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("patrol_craft").getBlueprint();

        assertEquals("Patrol Craft", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.TRANSPORT, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(3, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getLandspeed(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        assertEquals(1, card.getPassengerCapacity());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.SPECIAL_EDITION);
        }});
        assertEquals(ExpansionSet.SPECIAL_EDITION, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void PatrolCraftSiteReactDoesNotOfferSimultaneousDriver() {
        // AR example: react to a site — Patrol Craft may deploy unpiloted, so no simultaneous driver.
        var scn = GetScenario();

        var rebel = scn.GetLSCard("rebel");
        var patrol = scn.GetDSCard("patrol_craft");
        var pilot = scn.GetDSCard("mercenary_pilot");
        var eastPlatform = scn.GetDSCard("east_platform");

        scn.StartGame();

        scn.MoveLocationToTable(eastPlatform);
        scn.MoveCardsToLocation(eastPlatform, rebel);
        scn.MoveCardsToDSHand(patrol, pilot);
        scn.DSActivateForceCheat(5);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(eastPlatform));
        scn.LSForceDrainAt(eastPlatform);

        assertTrue(scn.DSCardPlayAvailable(patrol));
        scn.DSPlayCard(patrol);
        scn.PassAllResponses();

        assertFalse(scn.DSDecisionAvailable("simultaneously deploy a driver"));
        assertTrue(scn.DSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.DSHasCardChoiceAvailable(eastPlatform));
        scn.DSChooseCard(eastPlatform);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(eastPlatform, patrol));
        assertEquals(Zone.HAND, pilot.getZone());
        assertFalse(scn.IsAboard(patrol, pilot));
    }

    @Test
    public void PatrolCraftCloudSectorReactAllowsSimultaneousPilot() {
        // AR example: react to Bespin: Cloud City with Mercenary Pilot — legal because cloud sector requires a pilot.
        var scn = GetScenario();

        var lsCloudCar = scn.GetLSCard("ls_cloud_car");
        var lsBespin = scn.GetLSCard("ls_bespin");
        var patrol = scn.GetDSCard("patrol_craft");
        var pilot = scn.GetDSCard("mercenary_pilot");
        var cloudCity = scn.GetDSCard("bespin_cloud_city");
        var bespin = scn.GetDSCard("bespin");

        scn.StartGame();

        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(cloudCity);
        scn.MoveLocationToTable(lsBespin); // ensure system present if needed
        scn.MoveCardsToLocation(cloudCity, lsCloudCar);
        scn.MoveCardsToDSHand(patrol, pilot);
        scn.DSActivateForceCheat(5);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(cloudCity));
        scn.LSForceDrainAt(cloudCity);

        assertTrue(scn.DSCardPlayAvailable(patrol));
        scn.DSPlayCard(patrol);
        scn.PassAllResponses();

        // Cloud sector requires a pilot — forced simultaneous driver/pilot choice (no optional Yes/No for site path).
        assertFalse(scn.DSDecisionAvailable("Do you want to simultaneously deploy a driver"));
        assertTrue(scn.DSDecisionAvailable("Choose a driver from hand to simultaneously deploy"));
        assertTrue(scn.DSHasCardChoiceAvailable(pilot));
        scn.DSChooseCard(pilot);

        assertTrue(scn.DSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.DSHasCardChoiceAvailable(cloudCity));
        scn.DSChooseCard(cloudCity);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(cloudCity, patrol));
        assertTrue(scn.IsAboardAsPilot(patrol, pilot));
    }

    @Test
    public void PatrolCraftNormalSiteDeployDoesNotOfferSimultaneousDriver() {
        // Normal deploy to a site: driver would be a separate top-level action, not a ride-along.
        var scn = GetScenario();

        var patrol = scn.GetDSCard("patrol_craft");
        var pilot = scn.GetDSCard("mercenary_pilot");
        var eastPlatform = scn.GetDSCard("east_platform");

        scn.StartGame();

        scn.MoveLocationToTable(eastPlatform);
        scn.MoveCardsToDSHand(patrol, pilot);
        scn.DSActivateForceCheat(5);

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(patrol));
        scn.DSDeployCard(patrol);

        assertFalse(scn.DSDecisionAvailable("simultaneously deploy a driver"));
        assertTrue(scn.DSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.DSHasCardChoiceAvailable(eastPlatform));
        scn.DSChooseCard(eastPlatform);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(eastPlatform, patrol));
        assertEquals(Zone.HAND, pilot.getZone());
        assertFalse(scn.IsAboard(patrol, pilot));
    }

    @Test
    public void PatrolCraftNormalCloudSectorDeployRequiresSimultaneousPilot() {
        var scn = GetScenario();

        var patrol = scn.GetDSCard("patrol_craft");
        var pilot = scn.GetDSCard("mercenary_pilot");
        var cloudCity = scn.GetDSCard("bespin_cloud_city");
        var bespin = scn.GetDSCard("bespin");

        scn.StartGame();

        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(cloudCity);
        scn.MoveCardsToDSHand(patrol, pilot);
        scn.DSActivateForceCheat(5);

        var marketplace = scn.GetDSStartingLocation();

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(patrol));
        scn.DSDeployCard(patrol);

        // Starting exterior sites remain legal without a driver; cloud requires one — optional prompt appears.
        assertTrue(scn.DSDecisionAvailable("Do you want to simultaneously deploy a driver"));
        scn.DSChooseYes();
        assertTrue(scn.DSDecisionAvailable("Choose a driver from hand to simultaneously deploy"));
        scn.DSChooseCard(pilot);
        assertTrue(scn.DSHasCardChoiceAvailable(cloudCity));
        assertFalse(scn.DSHasCardChoiceAvailable(marketplace)); // simultaneous driver not offered to sites
        scn.DSChooseCard(cloudCity);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(cloudCity, patrol));
        assertTrue(scn.IsAboardAsPilot(patrol, pilot));
    }

    @Test
    public void BanthaNormalSiteDeployStillWorksWithoutSimultaneousDriverPrompt() {
        // Smoke check: other vehicles that do not use the cloud-sector simultaneous path still deploy to sites.
        var scn = GetScenario();

        var bantha = scn.GetDSCard("bantha");
        var eastPlatform = scn.GetDSCard("east_platform");

        scn.StartGame();

        scn.MoveLocationToTable(eastPlatform);
        scn.MoveCardsToDSHand(bantha);
        scn.DSActivateForceCheat(3);

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(bantha));
        scn.DSDeployCard(bantha);

        assertFalse(scn.DSDecisionAvailable("simultaneously deploy a driver"));
        assertTrue(scn.DSHasCardChoiceAvailable(eastPlatform));
        scn.DSChooseCard(eastPlatform);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(eastPlatform, bantha));
    }
}
