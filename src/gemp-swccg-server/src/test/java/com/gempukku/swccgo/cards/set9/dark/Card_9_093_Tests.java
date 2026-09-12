package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_9_093_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("bwing", "9_66");
                    put("weapon1", "9_87");
                    put("weapon2", "2_81");
                    put("hoth", "3_55");
                }},
                new HashMap<>() {{
                    put("fighterCover", "9_93");
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
    public void FighterCoverStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("fighterCover").getBlueprint();

        assertEquals("Fighter Cover", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ADMIRALS_ORDER);
        }});
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ADMIRALS_ORDER);
            add(Icon.DEATH_STAR_II);
        }});
        assertEquals(ExpansionSet.DEATH_STAR_II, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void FighterCoverDoesNotAddPowerTwiceToSameStarfighterInOneBattle() {
        // Issue #46 twin of #38: same once-per-starfighter gate as Concentrate All Fire.
        // LS B-Wing fires two weapons under DS Fighter Cover; +3 applies once only.

        var scn = GetScenario();

        var fighterCover = scn.GetDSCard("fighterCover");
        var bWing = scn.GetLSCard("bwing");
        var weapon1 = scn.GetLSCard("weapon1");
        var weapon2 = scn.GetLSCard("weapon2");
        var hoth = scn.GetLSCard("hoth");
        var executor = scn.GetDSCard("executor");

        scn.StartGame();

        scn.MoveCardsToDSHand(fighterCover);
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(hoth, bWing, executor);
        scn.AttachCardsTo(bWing, weapon1, weapon2);

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSDeployAvailable(fighterCover));
        scn.DSDeployCard(fighterCover);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertTrue(scn.GetLSForcePileCount() >= 2);
        assertTrue(scn.LSCanInitiateBattle());
        scn.LSInitiateBattle(hoth);
        scn.PassBattleStartResponses();

        assertEquals(4, scn.GetPower(bWing));
        assertEquals(4, scn.GetLSTotalPower());

        assertTrue(scn.LSCardActionAvailable(weapon1));
        scn.LSUseCardAction(weapon1);
        assertTrue(scn.LSHasCardChoiceAvailable(executor));
        scn.LSChooseCard(executor);
        scn.PassAllResponses();

        assertEquals(7, scn.GetPower(bWing));
        assertEquals(7, scn.GetLSTotalPower());

        scn.DSPass();

        assertTrue(scn.LSCardActionAvailable(weapon2));
        scn.LSUseCardAction(weapon2);
        assertTrue(scn.LSHasCardChoiceAvailable(executor));
        scn.LSChooseCard(executor);
        assertFalse(scn.LSDecisionAvailable("Add 3 to power"));
        scn.PassAllResponses();
        assertFalse(scn.LSDecisionAvailable("Add 3 to power"));

        assertEquals(7, scn.GetPower(bWing));
        assertEquals(7, scn.GetLSTotalPower());
    }
}
