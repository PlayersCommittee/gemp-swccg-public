package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Card_9_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("concentrateAllFire", "9_3");
                    put("bwing", "9_66");
                    put("weapon1", "9_87");
                    put("weapon2", "2_81");
                    put("hoth", "3_55");
                }},
                new HashMap<>() {{
                    put("executor", "4_167");
                }},
                10,
                10,
                StartingSetup.ThereIsGoodInHimObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ConcentrateAllFireStatsAndKeywordsAreCorrect() {
        /**
         * Title: Concentrate All Fire
         * Uniqueness: Unique
         * Side: Light
         * Type: Admirals Order
         * Destiny: 6
         * Icons: Admirals Order, Death Star II
         * Game Text: Each starfighter that fires a weapon in battle is power +3 for the remainder of battle. Once per turn you may cancel and redraw your starship weapon destiny just drawn. At sites related to systems you occupy, your characters who have immunity to attrition each add 2 to immunity and 1 to each of that character's weapon destiny draws.
         * Set: Death Star II
         * Rarity: R
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("concentrateAllFire").getBlueprint();

        assertEquals("Concentrate All Fire", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ADMIRALS_ORDER);
        }});
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ADMIRALS_ORDER);
            add(Icon.DEATH_STAR_II);
        }});
        assertEquals(ExpansionSet.DEATH_STAR_II,card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test @Ignore
    public void ConcentrateAllFirePowerBonusIsNotCumulative() {
        //this test fails and demonstrates issue #38
        //https://github.com/PlayersCommittee/gemp-swccg-public/issues/38

        var scn = GetScenario();

        var concentrateAllFire = scn.GetLSCard("concentrateAllFire");
        var bwing = scn.GetLSCard("bwing");
        var weapon1 = scn.GetLSCard("weapon1");
        var weapon2 = scn.GetLSCard("weapon2");
        var hoth = scn.GetLSCard("hoth");

        var executor = scn.GetDSCard("executor");

        scn.StartGame();

        scn.MoveCardsToLSHand(concentrateAllFire);

        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(hoth, bwing, executor);
        scn.AttachCardsTo(bwing, weapon1, weapon2);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());

        assertTrue(scn.LSDeployAvailable(concentrateAllFire));
        scn.LSDeployCard(concentrateAllFire);

        scn.SkipToPhase(Phase.BATTLE);

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertTrue(scn.GetLSForcePileCount() >= 2); //enough to initiate and fire both weapons
        assertTrue(scn.LSCanInitiateBattle());
        scn.LSInitiateBattle(hoth);
        scn.PassBattleStartResponses();

        // -- base power 4
        assertEquals(4,scn.GetLSTotalPower());

        assertTrue(scn.LSCardActionAvailable(weapon1));
        scn.LSUseCardAction(weapon1); //cost: free
        assertTrue(scn.LSHasCardChoiceAvailable(executor));
        scn.LSChooseCard(executor);
        scn.PassAllResponses();

        // -- concentrate all fire adds 3
        assertEquals(7,scn.GetLSTotalPower());

        scn.DSPass();

        assertTrue(scn.LSCardActionAvailable(weapon2));
        scn.LSUseCardAction(weapon2); //cost: 1
        assertTrue(scn.LSHasCardChoiceAvailable(executor));
        scn.LSChooseCard(executor);
        scn.PassAllResponses();

        // -- concentrate all fire should not add further (not cumulative)
        assertEquals(7,scn.GetLSTotalPower());
    }
}
