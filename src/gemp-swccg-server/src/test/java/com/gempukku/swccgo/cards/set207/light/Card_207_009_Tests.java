package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Issue #291: Sabine Wren shares may-fire-for-free path with Jodo Kast; free+2 must be optional.
 */
public class Card_207_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("sabine", "207_009"); // Sabine Wren
                    put("blaster", "1_152"); // Blaster
                }},
                new HashMap<>() {{
                    put("trooper", "1_194"); // Stormtrooper
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
    public void SabineWrenStatsAndKeywordsAreCorrect() {
        /**
         * Title: Sabine Wren
         * Uniqueness: Unique
         * Side: Light
         * Type: Character
         * Subtype: Rebel
         * Destiny: 2
         * Deploy: 3
         * Power: 4
         * Ability: 2
         * Forfeit: 6
         * Armor: 5
         * Persona: Sabine
         * Species: Mandalorian
         * Icons: Pilot, Warrior x2, Virtual Set 7
         * Keywords: Female, Scout
         * Game Text: Once per turn, when firing a rifle or blaster, may target for free and add 2 to total weapon destiny.
         *             While with an Imperial (or two Rebels), whenever you win a battle here, opponent loses 2 Force.
         *             Immune to Hidden Weapons.
         * Set: Set 7
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("sabine").getBlueprint();

        assertEquals("Sabine Wren", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        assertEquals(Species.MANDALORIAN, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.SABINE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FEMALE);
            add(Keyword.SCOUT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.VIRTUAL_SET_7);
        }});
        assertEquals(2, card.getIconCount(Icon.WARRIOR));
        assertEquals(ExpansionSet.SET_7, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SabineWrenOffersPaidFireAndFreePlus2WhenForceAvailable() {
        var scn = GetScenario();

        var sabine = scn.GetLSCard("sabine");
        var blaster = scn.GetLSCard("blaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sabine, trooper);
        scn.AttachCardsTo(sabine, blaster);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(site));
        scn.LSInitiateBattle(site);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());

        var actions = scn.GetLSAvailableActions();
        assertTrue(actions.stream().anyMatch(a -> a.contains("Fire") && !a.contains("for free and add 2")));
        assertTrue(actions.stream().anyMatch(a -> a.contains("for free and add 2")));
        assertNull(sabine.getWhileInPlayData());
    }

    @Test
    public void SabineWrenFreePlus2ConsumesPackage() {
        var scn = GetScenario();

        var sabine = scn.GetLSCard("sabine");
        var blaster = scn.GetLSCard("blaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sabine, trooper);
        scn.AttachCardsTo(sabine, blaster);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        int forceAfterBattle = scn.GetLSForcePileCount();

        scn.LSUseCardAction(blaster, "for free and add 2");
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();

        assertNotNull(sabine.getWhileInPlayData());
        assertEquals(forceAfterBattle, scn.GetLSForcePileCount());
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            var actions = scn.GetLSAvailableActions();
            assertFalse(actions.stream().anyMatch(a -> a.contains("for free and add 2")));
        }
    }
}
