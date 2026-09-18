package com.gempukku.swccgo.cards.set3.light;

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
import static org.junit.Assert.assertTrue;

public class Card_3_066_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rogue1", "3_066");
                    put("luke", "3_3");
                    put("trooper", "1_028");
                    put("ebg", "111_3");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void Rogue1StatsAndKeywordsAreCorrect() {
        /**
         * Title: Rogue 1
         * Uniqueness: Unique
         * Side: Light
         * Type: Vehicle
         * Subtype: Combat
         * Destiny: 2
         * Icons: Hoth, Vehicle
         * Keywords: Enclosed, Snowspeeder, Rogue Squadron
         * Game Text: May add 2 pilots or passengers. Immune to attrition < 3 if Luke piloting. May move as a 'react' only to Hoth sites.
         * Set: Hoth
         * Rarity: R1
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("rogue1").getBlueprint();

        assertEquals("Rogue 1", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.VEHICLE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
            add(Keyword.SNOWSPEEDER);
            add(Keyword.ROGUE_SQUADRON);
        }});
        assertEquals(ExpansionSet.HOTH, card.getExpansionSet());
        assertEquals(Rarity.R1, card.getRarity());
    }

    @Test
    public void Rogue1PassengerSharesEnclosedVehicleImmunityWhenLukePiloting() {
        var scn = GetScenario();
        var rogue1 = scn.GetLSCard("rogue1");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, rogue1);
        scn.BoardAsPilot(rogue1, luke);
        scn.BoardAsPassenger(rogue1, trooper);

        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), rogue1), scn.epsilon);
        assertEquals("Passenger aboard enclosed Rogue 1 shares the vehicle's immunity < 3",
                3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), trooper), scn.epsilon);
    }

    @Test
    public void Rogue1MatchingPilotAndEchoBaseGarrisonImmunityIsSharedWithPassenger() {
        var scn = GetScenario();
        var rogue1 = scn.GetLSCard("rogue1");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetLSCard("trooper");
        var ebg = scn.GetLSCard("ebg");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(ebg);
        scn.MoveCardsToLocation(site, rogue1);
        scn.BoardAsPilot(rogue1, luke);
        scn.BoardAsPassenger(rogue1, trooper);

        assertEquals(6, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), rogue1), scn.epsilon);
        assertEquals("Enclosed passenger shares Echo Base Garrison matching-pilot immunity < 6",
                6, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), trooper), scn.epsilon);
    }

    @Test
    public void Rogue1WithEchoBaseGarrisonIsNotForcedToForfeitAttrition1() {
        var scn = GetScenario();
        var rogue1 = scn.GetLSCard("rogue1");
        var luke = scn.GetLSCard("luke");
        var ebg = scn.GetLSCard("ebg");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(ebg);
        scn.MoveCardsToLocation(site, rogue1, vader);
        scn.BoardAsPilot(rogue1, luke);

        assertEquals("Rogue 1 with matching Luke and Echo Base Garrison is immune < 6",
                6, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), rogue1), scn.epsilon);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(1);
        scn.PrepareDSDestiny(1);
        assertTrue(scn.LSCanInitiateBattle(site));
        scn.LSInitiateBattle(site);
        scn.SkipToDamageSegment(true);

        if (scn.AwaitingLSBattleDamagePayment() && scn.GetLSReserveDeckCount() > 0) {
            scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
            scn.PassAllResponses();
        }
        if (scn.GetUnpaidLSAttrition() >= 1 && scn.AwaitingLSAttritionPayment()) {
            var decision = scn.LSGetDecision();
            String text = decision == null ? "" : decision.getText();
            assertTrue("Remaining attrition against immune Rogue 1 is optional, not required. decision=" + text,
                    text.toLowerCase().contains("if desired"));
            scn.LSPass();
            scn.PassAllResponses();
        }
        assertEquals(Zone.AT_LOCATION, rogue1.getZone());
        assertTrue(luke.getZone() == Zone.ATTACHED || luke.getZone() == Zone.AT_LOCATION);
    }
}
