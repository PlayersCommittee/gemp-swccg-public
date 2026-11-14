package com.gempukku.swccgo.cards.set9.dark;

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
import com.gempukku.swccgo.common.Zone;
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

public class Card_9_141_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
                put("luke", "1_19");
            }},
            new HashMap<>() {{
                put("youngFool", "9_141");
                put("throne", "9_147");
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
    public void YoungFoolStatsAndKeywordsAreCorrect() {
        /**
         * Title: Young Fool
         * Uniqueness: Unique
         * Side: Dark
         * Type: Interrupt
         * Subtype: Lost
         * Destiny: 6
         * Icons: Death Star II, Interrupt
         * Game Text: If opponent's character present with Emperor was just lost, lose 1 Force to place that character
         *      out of play. OR Release frozen Luke at your Throne Room (Luke may not be battled until end of your next
         *      turn) OR Cancel NOOOOOOOOOOOO!
         * Lore: 'Now, young Skywalker ... you will die.'
         * Set: Death Star II
         * Rarity: R
         */

        var scn = GetScenario();
        var card = scn.GetDSCard("youngFool").getBlueprint();

        assertEquals(Title.Young_Fool, card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.DEATH_STAR_II, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void YoungFoolReleasesFrozenLukeAtYourThroneRoom() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var youngFool = scn.GetDSCard("youngFool");
        var throne = scn.GetDSCard("throne");

        scn.StartGame();
        scn.MoveLocationToTable(throne);
        scn.MoveCardsToDSHand(youngFool);
        scn.MoveCardsToLocation(throne, luke);
        scn.FreezeCard(luke);

        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(luke.isFrozen());
        assertTrue(luke.isCaptive());
        assertFalse(scn.IsCardActive(luke));
        assertAtLocation(throne, luke);
        assertTrue(scn.DSCardPlayAvailable(youngFool));

        scn.DSPlayCardAndPassResponses(youngFool, luke);

        assertFalse(luke.isFrozen());
        assertFalse(luke.isCaptive());
        assertTrue(scn.IsCardActive(luke));
        assertAtLocation(throne, luke);
        assertInZone(Zone.LOST_PILE, youngFool);
    }

    @Test
    public void YoungFoolDoesNotOfferReleaseWhenFrozenLukeIsNotAtThroneRoom() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var youngFool = scn.GetDSCard("youngFool");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(youngFool);
        scn.MoveCardsToLocation(lsSite, luke);
        scn.FreezeCard(luke);

        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(luke.isFrozen());
        assertFalse(scn.DSCardPlayAvailable(youngFool));
    }

    @Test
    public void YoungFoolReleasedLukeMayNotBeBattledUntilEndOfDSNextTurn() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");

        var youngFool = scn.GetDSCard("youngFool");
        var throne = scn.GetDSCard("throne");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(throne);
        scn.MoveCardsToDSHand(youngFool);
        scn.MoveCardsToLocation(throne, luke, trooper);
        scn.FreezeCard(luke);

        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(youngFool));
        scn.DSPlayCardAndPassResponses(youngFool, luke);
        assertFalse(luke.isFrozen());

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.game().getModifiersQuerying().mayNotBeBattled(scn.gameState(), luke));
        assertFalse(scn.DSCanInitiateBattle(throne));

        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.game().getModifiersQuerying().mayNotBeBattled(scn.gameState(), luke));
        assertFalse(scn.DSCanInitiateBattle(throne));

        scn.SkipToLSTurn();
        assertFalse(scn.game().getModifiersQuerying().mayNotBeBattled(scn.gameState(), luke));

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(throne));
    }

    @Test
    public void YoungFoolReleasedLukeParticipatesInBattle() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var rebelTrooper = scn.GetLSFiller(1);

        var youngFool = scn.GetDSCard("youngFool");
        var throne = scn.GetDSCard("throne");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(throne);
        scn.MoveCardsToDSHand(youngFool);
        scn.MoveCardsToLocation(throne, luke, trooper, rebelTrooper);
        scn.FreezeCard(luke);

        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(youngFool));
        scn.DSPlayCardAndPassResponses(youngFool, luke);
        assertFalse(luke.isFrozen());

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(throne)); //cannot battle luke, but can battle the rebel trooper
        scn.DSInitiateBattle(throne);
        scn.SkipToDamageSegment();
        assertEquals(3, scn.GetUnpaidDSBattleDamage()); //luke participated - otherwise would be a tie with no battle damage
    }

}
