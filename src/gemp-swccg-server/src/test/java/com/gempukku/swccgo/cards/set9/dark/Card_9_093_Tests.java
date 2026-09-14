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
import com.gempukku.swccgo.logic.modifiers.MayFireAnyNumberOfWeaponsModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_9_093_Tests {
    /**
     * Fighter Cover (9_093) uses the same +3 power text as Concentrate All Fire (9_003).
     * The original case fires two weapons from a Light Side B-wing Bomber (9_66), because that
     * ship is allowed to fire more than one weapon in battle.
     * A second case uses a Dark TIE Fighter (1_304) (same side as Fighter Cover) firing two
     * SFS L-s7.2 TIE Cannon (9_181). Fighter Cover only boosts starfighters, so a capital
     * Star Destroyer would never get this +3.
     */
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
                    put("tie", "1_304");
                    put("cannon1", "9_181");
                    put("cannon2", "9_181");
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
        /**
         * Title: Fighter Cover
         * Uniqueness: Unique
         * Side: Dark
         * Type: Admirals Order
         * Destiny: 6
         * Icons: Admirals Order, Death Star II
         * Game Text: Each starfighter that fires a weapon in battle is power +3 for the remainder of battle.
         * Set: Death Star II
         * Rarity: R
         */
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

    /**
     * Fighter Cover (9_093) adds 3 power the first time a starfighter fires in a battle.
     * A second weapon fire from the same B-wing Bomber (9_66) does not add another 3.
     */
    @Test
    public void FighterCoverPowerBonusIsNotCumulativePerStarshipPerBattle() {
        var scn = GetScenario();

        var fighterCover = scn.GetDSCard("fighterCover");
        var bwing = scn.GetLSCard("bwing");
        var weapon1 = scn.GetLSCard("weapon1");
        var weapon2 = scn.GetLSCard("weapon2");
        var hoth = scn.GetLSCard("hoth");
        var executor = scn.GetDSCard("executor");

        scn.StartGame();

        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(hoth, bwing, executor);
        scn.AttachCardsTo(bwing, weapon1, weapon2);
        scn.MoveCardsToDSSideOfTable(fighterCover);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertTrue(scn.GetLSForcePileCount() >= 2);
        assertTrue(scn.LSCanInitiateBattle());
        scn.LSInitiateBattle(hoth);
        scn.PassBattleStartResponses();

        // B-wing Bomber printed power 4
        assertEquals(4, scn.GetLSTotalPower());

        assertTrue(scn.LSCardActionAvailable(weapon1));
        scn.LSUseCardAction(weapon1);
        assertTrue(scn.LSHasCardChoiceAvailable(executor));
        scn.LSChooseCard(executor);
        scn.PassAllResponses();

        // Fighter Cover adds 3 once
        assertEquals(7, scn.GetLSTotalPower());

        scn.DSPass();

        assertTrue(scn.LSCardActionAvailable(weapon2));
        scn.LSUseCardAction(weapon2);
        assertTrue(scn.LSHasCardChoiceAvailable(executor));
        scn.LSChooseCard(executor);
        scn.PassAllResponses();

        // Same clause from the same unique card must not add another 3
        assertEquals(7, scn.GetLSTotalPower());
    }

    /**
     * Dark TIE Fighter (1_304) is a starfighter on the same side as Fighter Cover (9_093).
     * Capitals such as Dominator (9_155) never get this bonus. With two SFS L-s7.2 TIE Cannon (9_181)
     * aboard, the Dark TIE fires both in one battle and is power +3 once, not +6.
     */
    @Test
    public void FighterCoverPowerBonusIsNotCumulativeWhenDarkStarDestroyerFiresTwoWeapons() {
        var scn = GetScenario();

        var fighterCover = scn.GetDSCard("fighterCover");
        var tie = scn.GetDSCard("tie");
        var cannon1 = scn.GetDSCard("cannon1");
        var cannon2 = scn.GetDSCard("cannon2");
        var bwing = scn.GetLSCard("bwing");
        var hoth = scn.GetLSCard("hoth");

        scn.StartGame();

        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(hoth, tie, bwing);
        scn.AttachCardsTo(tie, cannon1, cannon2);
        scn.MoveCardsToDSSideOfTable(fighterCover);
        // No Dark starfighter prints may fire any number of weapons; B-wing Bomber (9_66) is Light.
        scn.ApplyAdHocModifier(new MayFireAnyNumberOfWeaponsModifier(tie));

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        assertTrue(scn.GetDSForcePileCount() >= 3);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(hoth);

        // TIE Fighter printed power 1
        assertEquals(1, scn.GetPower(tie));
        assertEquals(1, scn.GetDSTotalPower());

        assertTrue(scn.DSCardActionAvailable(cannon1));
        scn.DSUseCardAction(cannon1);
        assertTrue(scn.DSHasCardChoiceAvailable(bwing));
        scn.DSChooseCard(bwing);
        scn.PassAllResponses();

        // Fighter Cover adds 3 once to the firing Dark starfighter
        assertEquals(4, scn.GetPower(tie));
        assertEquals(4, scn.GetDSTotalPower());

        scn.LSPass();

        assertTrue(scn.DSCardActionAvailable(cannon2));
        scn.DSUseCardAction(cannon2);
        assertTrue(scn.DSHasCardChoiceAvailable(bwing));
        scn.DSChooseCard(bwing);
        scn.PassAllResponses();

        // Same unique Fighter Cover POWER clause must not add another 3
        assertEquals(4, scn.GetPower(tie));
        assertEquals(4, scn.GetDSTotalPower());
    }
}
