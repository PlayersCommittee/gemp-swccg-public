package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Card_2_029_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("sensorPanel", "2_029");
                    put("landspeeder", "1_149");
                    put("bantha", "2_076");
                    put("radarScanner", "1_104");
                    put("radarScanner2", "1_104");
                }},
                new HashMap<>()
                {{
                    put("barrier", "1_215");
                    put("elis", "1_251");
                    put("trooper", "1_194");
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

    /**
     * Puts Luke's X-34 Landspeeder on the starting site with a Rebel Trooper driving,
     * Sensor Panel attached, and Radar Scanner in hand.
     */
    private void setupSensorPanelOnLandspeeder(VirtualTableScenario scn) {
        var site = scn.GetLSStartingLocation();
        var landspeeder = scn.GetLSCard("landspeeder");
        var sensorPanel = scn.GetLSCard("sensorPanel");
        var driver = scn.GetLSFiller(1);
        scn.MoveCardsToLocation(site, landspeeder);
        scn.BoardAsPilot(landspeeder, driver);
        scn.AttachCardsTo(landspeeder, sensorPanel);
        scn.MoveCardsToHand(scn.GetLSCard("radarScanner"));
    }

    /**
     * Moves Light Side Force Pile cards back to Reserve Deck until the pile has the given size.
     */
    private void leaveExactlyLSForce(VirtualTableScenario scn, int remaining) {
        for (var card : List.copyOf(scn.GetLSForcePile())) {
            if (scn.GetLSForcePileCount() <= remaining) {
                break;
            }
            scn.MoveCardsToTopOfOwnReserveDeck((PhysicalCardImpl) card);
        }
    }

    /**
     * Drains Light Side Force Pile so Sensor Panel cannot pay its 1 Force cost.
     */
    private void emptyLSForcePile(VirtualTableScenario scn) {
        leaveExactlyLSForce(scn, 0);
    }

    /**
     * Current Dark Side and Light Side decision text, used when a test cannot find the next window.
     */
    private String currentDecisionText(VirtualTableScenario scn) {
        String ds = scn.DSGetDecision() == null ? "ds=none" : "ds=" + scn.DSGetDecision().getText();
        String ls = scn.LSGetDecision() == null ? "ls=none" : "ls=" + scn.LSGetDecision().getText();
        return ds + " | " + ls;
    }

    /**
     * True if Light Side is being offered Sensor Panel's Radar Scanner optional.
     */
    private boolean sensorPanelOptionalAvailable(VirtualTableScenario scn) {
        return scn.LSAnyDecisionsAvailable()
                && scn.LSCardActionAvailable(scn.GetLSCard("sensorPanel"), "Place Effect or Interrupt in Used Pile");
    }

    /**
     * Plays Radar Scanner, answers the vehicle target if asked, passes play and Force responses,
     * and dismisses the single peek window. Does not pass Sensor Panel's optional window.
     */
    private void playRadarScannerAndPeek(VirtualTableScenario scn) {
        playRadarScannerAndPeek(scn, scn.GetLSCard("radarScanner"));
    }

    private void playRadarScannerAndPeek(VirtualTableScenario scn, PhysicalCardImpl radarScanner) {
        var landspeeder = scn.GetLSCard("landspeeder");
        assertTrue("Radar Scanner should be playable", scn.LSCardPlayAvailable(radarScanner));
        scn.LSPlayCard(radarScanner);
        if (scn.LSAnyDecisionsAvailable() && scn.LSDecisionAvailable("Choose vehicle or starship")) {
            scn.LSChooseCard(landspeeder);
        }
        scn.PassAllResponses();
        assertTrue("Radar Scanner should show opponent's hand once. Decision: " + currentDecisionText(scn),
                scn.LSAnyDecisionsAvailable() && scn.LSDecisionAvailable("Opponent's hand"));
        scn.LSDecided("");
        settleAfterRadarScannerPeek(scn);
    }

    /**
     * After Radar Scanner's peek, Dark Side may be asked to pass an empty optional window
     * before Light Side sees Sensor Panel.
     */
    private void settleAfterRadarScannerPeek(VirtualTableScenario scn) {
        for (int i = 0; i < 12; i++) {
            if (sensorPanelOptionalAvailable(scn)) {
                return;
            }
            if (scn.LSAnyDecisionsAvailable() && scn.LSDecisionAvailable("Opponent's hand")) {
                fail("Radar Scanner showed opponent's hand a second time");
            }
            if ((scn.LSAnyDecisionsAvailable() && scn.LSDecisionAvailable("Do you want to place Jawas"))
                    || scn.AwaitingLSControlPhaseActions()) {
                return;
            }
            String text = currentDecisionText(scn).toLowerCase();
            if (text.contains("optional response")) {
                if (scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
                    scn.DSPass();
                    continue;
                }
                if (scn.LSAnyDecisionsAvailable() && !sensorPanelOptionalAvailable(scn)) {
                    scn.LSPass();
                    continue;
                }
            }
            return;
        }
    }

    /**
     * Passes leftover Radar Scanner windows until Light Side is back at Control phase actions.
     */
    private void finishRadarScannerToControl(VirtualTableScenario scn) {
        for (int i = 0; i < 20; i++) {
            if (scn.AwaitingLSControlPhaseActions()) {
                return;
            }
            if (scn.LSDecisionAvailable("Do you want to place Jawas")) {
                scn.LSChooseNo();
                continue;
            }
            String text = currentDecisionText(scn).toLowerCase();
            if (text.contains("optional response")) {
                scn.PassResponses("optional");
                continue;
            }
            if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
                continue;
            }
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
                continue;
            }
            return;
        }
    }

    @Test
    public void SensorPanelStatsAndKeywordsAreCorrect() {
        /**
         * Title: Sensor Panel
         * Uniqueness: Unique
         * Side: Light
         * Type: Device
         * Destiny: 3
         * Game Text: Use 1 Force to deploy on your non-creature vehicle. Adds 1 to power and landspeed.
         *      Once per turn, when you play Radar Scanner, you may use 1 Force to move one additional Effect or Interrupt
         *      card found in opponent's hand to Used Pile.
         * Lore: Monitors all nearby traffic in exterior locations. Takes advantage of multiple backup systems to minimize breakdowns under harsh conditions.
         * Set: A New Hope
         * Rarity: U2
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("sensorPanel").getBlueprint();

        assertEquals("Sensor Panel", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.A_NEW_HOPE);
        }});
        assertEquals(ExpansionSet.A_NEW_HOPE, card.getExpansionSet());
        assertEquals(Rarity.U2, card.getRarity());
    }

    @Test
    public void SensorPanelCanDeployOnOwnersNonCreatureVehicle() {
        var scn = GetScenario();
        var sensorPanel = scn.GetLSCard("sensorPanel");
        var landspeeder = scn.GetLSCard("landspeeder");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, landspeeder);
        scn.MoveCardsToHand(sensorPanel);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue("Sensor Panel should deploy on owner's non-creature vehicle", scn.LSDeployAvailable(sensorPanel));
        scn.LSDeployCard(sensorPanel);
        if (scn.LSHasCardChoiceAvailable(landspeeder)) {
            scn.LSChooseCard(landspeeder);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(landspeeder, sensorPanel));
    }

    @Test
    public void SensorPanelMayNotDeployOnCreatureVehicle() {
        var scn = GetScenario();
        var sensorPanel = scn.GetLSCard("sensorPanel");
        var landspeeder = scn.GetLSCard("landspeeder");
        var bantha = scn.GetLSCard("bantha");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, landspeeder, bantha);
        scn.MoveCardsToHand(sensorPanel);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(sensorPanel);
        assertTrue("Luke's X-34 Landspeeder is a legal non-creature vehicle", scn.LSHasCardChoiceAvailable(landspeeder));
        assertFalse("Rogue Bantha is a creature vehicle and is not a legal target", scn.LSHasCardChoiceAvailable(bantha));
    }

    @Test
    public void SensorPanelMayNotDeployWithoutOneForce() {
        var scn = GetScenario();
        var sensorPanel = scn.GetLSCard("sensorPanel");
        var landspeeder = scn.GetLSCard("landspeeder");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, landspeeder);
        scn.MoveCardsToHand(sensorPanel);

        // Empty Force after activate, then enter Deploy so the action list is built with 0 Force.
        scn.SkipToLSTurn(Phase.CONTROL);
        emptyLSForcePile(scn);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(0, scn.GetLSForcePileCount());
        assertFalse("Sensor Panel costs 1 Force to deploy", scn.LSDeployAvailable(sensorPanel));
    }

    @Test
    public void SensorPanelAddsOneToVehiclePowerAndLandspeed() {
        var scn = GetScenario();
        var landspeeder = scn.GetLSCard("landspeeder");
        var sensorPanel = scn.GetLSCard("sensorPanel");
        var driver = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, landspeeder);
        scn.BoardAsPilot(landspeeder, driver);

        assertEquals("Luke's X-34 Landspeeder printed power", 1, scn.GetPower(landspeeder));
        assertEquals("Luke's X-34 Landspeeder printed landspeed", 4, scn.GetLandspeed(landspeeder));

        scn.AttachCardsTo(landspeeder, sensorPanel);

        assertEquals("Sensor Panel adds 1 to power", 2, scn.GetPower(landspeeder));
        assertEquals("Sensor Panel adds 1 to landspeed", 5, scn.GetLandspeed(landspeeder));
    }

    @Test
    public void SensorPanelOptionalActionUnavailableWithoutOneForce() {
        var scn = GetScenario();
        var sensorPanel = scn.GetLSCard("sensorPanel");
        var barrier = scn.GetDSCard("barrier");

        scn.StartGame();
        setupSensorPanelOnLandspeeder(scn);
        scn.MoveCardsToHand(barrier);

        scn.SkipToLSTurn(Phase.CONTROL);
        // Radar Scanner costs 1 Force; leave exactly 1 so Sensor Panel cannot pay afterwards.
        leaveExactlyLSForce(scn, 1);
        assertEquals(1, scn.GetLSForcePileCount());

        playRadarScannerAndPeek(scn);
        assertEquals(0, scn.GetLSForcePileCount());
        assertFalse("Sensor Panel optional action costs 1 Force", sensorPanelOptionalAvailable(scn));
    }

    @Test
    public void SensorPanelOptionalActionUnavailableIfNoEffectOrInterruptInHand() {
        var scn = GetScenario();
        var sensorPanel = scn.GetLSCard("sensorPanel");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        setupSensorPanelOnLandspeeder(scn);
        scn.MoveCardsToHand(trooper);

        scn.SkipToLSTurn(Phase.CONTROL);
        playRadarScannerAndPeek(scn);
        assertFalse("Stormtrooper is not an Effect or Interrupt", sensorPanelOptionalAvailable(scn));
    }

    @Test
    public void SensorPanelMayBeUsedWhenRadarScannerDoesNotRemoveACard() {
        var scn = GetScenario();
        var sensorPanel = scn.GetLSCard("sensorPanel");
        var barrier = scn.GetDSCard("barrier");

        scn.StartGame();
        setupSensorPanelOnLandspeeder(scn);
        scn.MoveCardsToHand(barrier);

        scn.SkipToLSTurn(Phase.CONTROL);
        playRadarScannerAndPeek(scn);
        assertTrue("Sensor Panel may be used even if Radar Scanner found no Jawa or Tusken Raider",
                sensorPanelOptionalAvailable(scn));
    }

    @Test
    public void SensorPanelMovesOneEffectFromOpponentsHandToUsedPile() {
        var scn = GetScenario();
        var sensorPanel = scn.GetLSCard("sensorPanel");
        var barrier = scn.GetDSCard("barrier");
        var elis = scn.GetDSCard("elis");

        scn.StartGame();
        setupSensorPanelOnLandspeeder(scn);
        scn.MoveCardsToHand(barrier, elis);

        scn.SkipToLSTurn(Phase.CONTROL);
        int forceBefore = scn.GetLSForcePileCount();
        playRadarScannerAndPeek(scn);

        assertTrue(sensorPanelOptionalAvailable(scn));
        scn.LSUseCardAction(sensorPanel, "Place Effect or Interrupt in Used Pile");
        scn.PassAllResponses();
        assertTrue("Imperial Barrier should be a legal Sensor Panel target. Decision: " + currentDecisionText(scn),
                scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(barrier));
        assertTrue("Elis Helrot should be a legal Sensor Panel target", scn.LSHasCardChoiceAvailable(elis));
        scn.LSChooseCard(barrier);
        scn.PassAllResponses();

        assertTrue("Imperial Barrier should be in Used Pile, not " + barrier.getZone(),
                barrier.getZone() == Zone.USED_PILE || barrier.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(scn.GetDSUsedPile().contains(barrier));
        assertEquals(Zone.HAND, elis.getZone());
        assertEquals("Sensor Panel uses 1 Force after Radar Scanner's 1 Force", forceBefore - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void SensorPanelOncePerTurnEnforced() {
        var scn = GetScenario();
        var sensorPanel = scn.GetLSCard("sensorPanel");
        var radarScanner2 = scn.GetLSCard("radarScanner2");
        var barrier = scn.GetDSCard("barrier");
        var elis = scn.GetDSCard("elis");

        scn.StartGame();
        setupSensorPanelOnLandspeeder(scn);
        scn.MoveCardsToHand(radarScanner2);
        scn.MoveCardsToHand(barrier, elis);

        scn.SkipToLSTurn(Phase.CONTROL);
        playRadarScannerAndPeek(scn);
        assertTrue(sensorPanelOptionalAvailable(scn));
        scn.LSUseCardAction(sensorPanel, "Place Effect or Interrupt in Used Pile");
        scn.PassAllResponses();
        assertTrue("Imperial Barrier should be a legal Sensor Panel target. Decision: " + currentDecisionText(scn),
                scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(barrier));
        scn.LSChooseCard(barrier);
        scn.PassAllResponses();
        finishRadarScannerToControl(scn);

        playRadarScannerAndPeek(scn, radarScanner2);
        assertFalse("Sensor Panel is once per turn", sensorPanelOptionalAvailable(scn));
    }
}
