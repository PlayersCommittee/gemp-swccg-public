package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Disarming Creature (3_033 / blueprint 3_33).
 * Doc tab t.2cr1dhmytc1 / issue #90.
 */
public class Card_3_033_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("disarming", "3_33");
                    put("luke", "1_19");
                    put("saber", "3_71");
                    put("quigon", "14_27");
                    put("worrt", "6_48");
                    put("dockingBay", "1_129");
                }},
                new HashMap<>() {{
                    put("wampa", "3_93");
                    put("slug", "4_112");
                    put("bubo", "6_138");
                    put("wampaCave", "3_150");
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

    @Test
    public void DisarmingCreatureStatsAndKeywordsAreCorrect() {
        /**
         * Title: Disarming Creature
         * Uniqueness: Unrestricted
         * Side: Light
         * Type: Effect
         * Subtype: Immediate
         * Destiny: 6
         * Icons: Effect, Hoth
         * Game Text: If you have a character with a weapon at same site as any creature (except Space Slug),
         *      deploy on that creature during any deploy phase, Creature is Disarmed (each time ferocity is calculated,
         *      draw destiny, subtract that amount). (Immune to Control.)
         * Lore: Luke's defensive maneuver put him out of arm's way.
         * Set: Hoth
         * Rarity: R1
         */

        var scn = GetScenario();
        var card = scn.GetLSCard("disarming").getBlueprint();

        assertEquals(Title.Disarming_Creature, card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DISARMING_CARD);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.HOTH, card.getExpansionSet());
        assertEquals(Rarity.R1, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Control));
        assertTrue(card.getGameText().contains("except Space Slug"));
        assertTrue(card.getGameText().contains("any deploy phase"));
        assertTrue(card.getLore().contains("out of arm's way"));
    }

    @Test
    public void DisarmingCreatureCannotPlayWithoutWeaponPresentDuringDeployPhase() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke, wampa);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardPlayAvailable(disarming));
    }

    @Test
    public void DisarmingCreatureCannotPlayAgainstSpaceSlugOnly() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var slug = scn.GetDSCard("slug");
        var dockingBay = scn.GetLSCard("dockingBay");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(dockingBay);
        scn.MoveCardsToLocation(dockingBay, luke, slug);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardPlayAvailable(disarming));
    }

    @Test
    public void DisarmingCreatureCannotPlayOutsideDeployPhase() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke, wampa);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardPlayAvailable(disarming));

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertFalse(scn.LSCardPlayAvailable(disarming));
    }

    @Test
    public void DisarmingCreatureCanPlayOnSelfOrOpponentCreatureDuringYourDeployPhase() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var worrt = scn.GetLSCard("worrt");
        var bubo = scn.GetDSCard("bubo");
        var dockingBay = scn.GetLSCard("dockingBay");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(dockingBay);
        scn.MoveCardsToLocation(dockingBay, luke, worrt, bubo);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSCardPlayAvailable(disarming));
        scn.LSPlayCard(disarming);

        assertTrue(scn.LSHasCardChoicesAvailable(worrt, bubo));
    }

    @Test
    public void DisarmingCreatureCanPlayWithPermanentWeaponDuringOpponentDeployPhase() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var quigon = scn.GetLSCard("quigon");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, quigon, wampa);

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.LSCardPlayAvailable(disarming));
    }

    @Test
    public void DisarmingCreaturePlayingAttachesOnTargetCreature() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke, wampa);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(disarming));
        scn.LSPlayCard(disarming);
        assertTrue(scn.LSHasCardChoicesAvailable(wampa));
        scn.LSChooseCard(wampa);
        scn.PassAllResponses();

        assertSame(wampa, disarming.getAttachedTo());
    }

    @Test
    public void DisarmingCreatureOwnerDrawsDestinyToSubtractWhenFerocityCalculated() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke, wampa);
        scn.AttachCardsTo(luke, saber);
        scn.AttachCardsTo(wampa, disarming);

        // Wampa ferocity destiny (creature owner) then LS subtract destiny (Disarming Creature owner)
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(1);

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCardActionAvailable(wampa, "Initiate attack"));
        scn.DSUseCardAction(wampa, "Initiate attack");
        scn.PassAllResponses();

        // Power segment: ferocity destiny then subtract destiny (automatic draws)
        scn.PassDestinyDrawResponses(); // DS ferocity destiny
        scn.PassDestinyDrawResponses(); // LS subtract destiny
        scn.PassAllResponses();

        var attackState = scn.gameState().getAttackState();
        assertTrue(attackState != null && attackState.isAttackInProgress());
        Float ferocityDestinyTotal = attackState.getFerocityDestinyTotal(wampa);
        assertTrue("Ferocity destiny total should be set after draws", ferocityDestinyTotal != null);
        // Printed base 3 + DS destiny 2 - LS subtract 1 = 4 ferocity; destiny total stored as 2 - 1 = 1
        assertEquals(1f, ferocityDestinyTotal, scn.epsilon);
        float ferocity = scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, ferocityDestinyTotal);
        assertEquals(4f, ferocity, scn.epsilon);
    }
}