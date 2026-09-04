package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_5_067_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rescue", "5_67");
                    put("weather-vane", "5_30");
                    put("luke", "1_19");
                    put("xwing", "1_146");
                    put("cloud-car", "5_88");
                    put("freighter", "7_144");
                    put("corvette", "1_140");
                    put("bespin", "5_76");
                    put("clouds", "5_85");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
                    put("hoth", "3_143");
                    put("big-one", "4_156");
                }},
                20,
                20,
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
    public void StatsAndKeywordsAreCorrect_5_67_RescueInTheClouds() {
        /**
         * Title: Rescue In The Clouds
         * Uniqueness: Unique
         * Side: Light
         * Type: Interrupt
         * Subtype: Used Or Lost
         * Destiny: 5
         * Icons: Cloud City
         * Game Text: USED: If you have a character on Weather Vane, place that character on your Used Pile.
         *      LOST: Deploy one or more vehicles, starfighters and pilots (at normal use of the Force)
         *      as a 'react' to a cloud sector.
         * Lore: 'I know where Luke is.'
         * Set: Cloud City
         * Rarity: C
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("rescue").getBlueprint();

        assertEquals("Rescue In The Clouds", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.CLOUD_CITY, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void UsedPlacesYourCharacterFromWeatherVaneInUsedPile_5_67_RescueInTheClouds() {
        var scn = GetScenario();

        var rescue = scn.GetLSCard("rescue");
        var weatherVane = scn.GetLSCard("weather-vane");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();

        scn.MoveCardsToLSSideOfTable(weatherVane);
        scn.StackCardsOn(weatherVane, luke);
        scn.MoveCardsToLSHand(rescue);

        assertTrue(scn.IsStackedOn(weatherVane, luke));

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(rescue));
        scn.LSPlayCard(rescue);
        if (scn.LSHasCardChoiceAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, luke);
        assertFalse(scn.IsStackedOn(weatherVane, luke));
        assertInZone(Zone.USED_PILE, rescue);
    }

    @Test
    public void UsedNotPlayableIfOnlyOpponentsCharacterOnWeatherVane_5_67_RescueInTheClouds() {
        var scn = GetScenario();

        var rescue = scn.GetLSCard("rescue");
        var weatherVane = scn.GetLSCard("weather-vane");
        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveCardsToLSSideOfTable(weatherVane);
        scn.StackCardsOn(weatherVane, stormtrooper);
        scn.MoveCardsToLSHand(rescue);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(rescue));
    }

    @Test
    public void UsedNotPlayableWithoutCharacterOnWeatherVane_5_67_RescueInTheClouds() {
        var scn = GetScenario();

        var rescue = scn.GetLSCard("rescue");
        var weatherVane = scn.GetLSCard("weather-vane");

        scn.StartGame();

        scn.MoveCardsToLSSideOfTable(weatherVane);
        scn.MoveCardsToLSHand(rescue);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(rescue));
    }

    @Test
    public void LostDeploysStarfighterAsReactToCloudSectorBattle_5_67_RescueInTheClouds() {
        var scn = GetScenario();

        var rescue = scn.GetLSCard("rescue");
        var xwing = scn.GetLSCard("xwing");
        var cloudCar = scn.GetLSCard("cloud-car");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");

        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, tie, cloudCar);
        scn.MoveCardsToLSHand(rescue, xwing);

        scn.SkipToDSTurn(Phase.BATTLE);
        int forceBefore = scn.GetLSForcePileCount();
        scn.DSInitiateBattle(clouds);
        assertTrue(scn.LSCardPlayAvailable(rescue));
        scn.LSPlayCard(rescue);
        scn.PassAllResponses();
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(xwing)) {
            scn.LSChooseCard(xwing);
        }
        scn.PassAllResponses();

        assertAtLocation(clouds, xwing);
        assertInZone(Zone.LOST_PILE, rescue);
        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void LostDeploysMediumBulkFreighterAsReactToCloudSectorBattle_5_67_RescueInTheClouds() {
        var scn = GetScenario();

        var rescue = scn.GetLSCard("rescue");
        var freighter = scn.GetLSCard("freighter");
        var cloudCar = scn.GetLSCard("cloud-car");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        assertTrue(freighter.getBlueprint().isDeploysLikeStarfighter());
        assertEquals(CardSubtype.CAPITAL, freighter.getBlueprint().getCardSubtype());

        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, tie, cloudCar);
        scn.MoveCardsToLSHand(rescue, freighter);

        assertTrue(Filters.deploysLikeStarfighter.accepts(scn.game(), freighter));
        assertTrue(GameConditions.hasInHand(scn.game(), VirtualTableScenario.LS, Filters.deploysLikeStarfighter));

        scn.SkipToDSTurn(Phase.BATTLE);
        int forceBefore = scn.GetLSForcePileCount();
        scn.DSInitiateBattle(clouds);
        assertTrue("Expected Rescue after-action. LS decision: " + (scn.LSGetDecision() == null ? "none" : scn.LSGetDecision().getText()),
                scn.LSAnyDecisionsAvailable());
        assertTrue(scn.LSCardPlayAvailable(rescue));
        scn.LSPlayCard(rescue);
        scn.PassAllResponses();
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(freighter)) {
            scn.LSChooseCard(freighter);
        }
        scn.PassAllResponses();

        assertAtLocation(clouds, freighter);
        assertInZone(Zone.LOST_PILE, rescue);
        assertEquals(forceBefore - 3, scn.GetLSForcePileCount());
    }

    @Test
    public void LostOffersXwingAndMediumBulkFreighterButNotCorvetteAsReact_5_67_RescueInTheClouds() {
        var scn = GetScenario();

        var rescue = scn.GetLSCard("rescue");
        var xwing = scn.GetLSCard("xwing");
        var freighter = scn.GetLSCard("freighter");
        var corvette = scn.GetLSCard("corvette");
        var cloudCar = scn.GetLSCard("cloud-car");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, tie, cloudCar);
        scn.MoveCardsToLSHand(rescue, xwing, freighter, corvette);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(clouds);
        assertTrue(scn.LSAnyDecisionsAvailable());
        assertTrue(scn.LSCardPlayAvailable(rescue));
        scn.LSPlayCard(rescue);
        scn.PassAllResponses();
        assertTrue(scn.LSAnyDecisionsAvailable());
        assertTrue(scn.LSHasCardChoiceAvailable(xwing));
        assertTrue(scn.LSHasCardChoiceAvailable(freighter));
        assertFalse(scn.LSHasCardChoiceAvailable(corvette));
        scn.LSChooseCard(freighter);
        scn.PassAllResponses();

        assertAtLocation(clouds, freighter);
        assertInZone(Zone.HAND, xwing);
        assertInZone(Zone.HAND, corvette);
    }

    @Test
    public void LostAdditionalReactChoiceCanBeStoppedWithDone_5_67_RescueInTheClouds() {
        var scn = GetScenario();

        var rescue = scn.GetLSCard("rescue");
        var xwing = scn.GetLSCard("xwing");
        var freighter = scn.GetLSCard("freighter");
        var cloudCar = scn.GetLSCard("cloud-car");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, tie, cloudCar);
        scn.MoveCardsToLSHand(rescue, xwing, freighter);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(clouds);
        assertTrue(scn.LSCardPlayAvailable(rescue));
        scn.LSPlayCard(rescue);
        scn.PassAllResponses();
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(xwing)) {
            scn.LSChooseCard(xwing);
        }
        scn.PassAllResponses();

        assertTrue(scn.LSAnyDecisionsAvailable());
        assertTrue(scn.LSGetDecision().getText().toLowerCase().contains("done"));
        assertTrue(scn.LSHasCardChoiceAvailable(freighter));
        scn.LSPass();
        scn.PassAllResponses();

        assertAtLocation(clouds, xwing);
        assertInZone(Zone.HAND, freighter);
        assertInZone(Zone.LOST_PILE, rescue);
    }

    @Test
    public void LostNotPlayableWhenOnlyCapitalThatDoesNotDeployLikeStarfighterIsInHand_5_67_RescueInTheClouds() {
        var scn = GetScenario();

        var rescue = scn.GetLSCard("rescue");
        var corvette = scn.GetLSCard("corvette");
        var cloudCar = scn.GetLSCard("cloud-car");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, tie, cloudCar);
        scn.MoveCardsToLSHand(rescue, corvette);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(clouds);
        assertFalse(scn.LSAnyDecisionsAvailable() && scn.LSCardPlayAvailable(rescue));
    }

    @Test
    public void LostPlayableAsReactToCloudSectorForceDrain_5_67_RescueInTheClouds() {
        var scn = GetScenario();

        var rescue = scn.GetLSCard("rescue");
        var xwing = scn.GetLSCard("xwing");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, tie);
        scn.MoveCardsToLSHand(rescue, xwing);

        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(clouds));
        scn.DSForceDrainAt(clouds);
        assertTrue(scn.LSCardPlayAvailable(rescue));
        scn.LSPlayCard(rescue);
        scn.PassAllResponses();
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(xwing)) {
            scn.LSChooseCard(xwing);
        }
        scn.PassAllResponses();

        assertAtLocation(clouds, xwing);
        assertInZone(Zone.LOST_PILE, rescue);
    }

    @Test
    public void LostNotPlayableAsReactToAsteroidSectorBattle_5_67_RescueInTheClouds() {
        var scn = GetScenario();

        var rescue = scn.GetLSCard("rescue");
        var xwing = scn.GetLSCard("xwing");
        var hoth = scn.GetDSCard("hoth");
        var bigOne = scn.GetDSCard("big-one");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveLocationToTable(hoth);
        scn.MoveLocationToTable(bigOne);
        scn.MoveCardsToLocation(bigOne, tie, xwing);
        scn.MoveCardsToLSHand(rescue);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(bigOne);
        assertFalse(scn.LSAnyDecisionsAvailable() && scn.LSCardPlayAvailable(rescue));
    }

    @Test
    public void LostNotPlayableWhenBattleIsAtASite_5_67_RescueInTheClouds() {
        var scn = GetScenario();

        var rescue = scn.GetLSCard("rescue");
        var xwing = scn.GetLSCard("xwing");
        var site = scn.GetLSStartingLocation();
        var lsPresence = scn.GetLSFiller(1);
        var dsPresence = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveCardsToLocation(site, lsPresence, dsPresence);
        scn.MoveCardsToLSHand(rescue, xwing);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        assertFalse(scn.LSAnyDecisionsAvailable() && scn.LSCardPlayAvailable(rescue));
    }
}
