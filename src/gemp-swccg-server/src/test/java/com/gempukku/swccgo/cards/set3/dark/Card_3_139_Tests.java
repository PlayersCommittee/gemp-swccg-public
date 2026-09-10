package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.CardSubtype;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_3_139_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("toolkit", "4_010");
                    put("crash", "1_045");
                    put("han", "1_011");
                }},
                new HashMap<>() {{
                    put("tio", "3_139");
                    put("tio2", "3_139");
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
    public void TurnItOffTurnItOffStatsAndKeywordsAreCorrect() {
        /**
         * Title: Turn It Off! Turn It Off!
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Interrupt
         * Subtype: Used Or Lost
         * Destiny: 5
         * Icons: Hoth
         * Game Text: USED: Cancel any attempt to place a 'hit' starship, vehicle or droid in the Used Pile rather than
         *         the Lost Pile. OR Cancel Han's Toolkit. LOST: Cancel Crash Site Memorial.
         * Lore: 'Turn it off! Turn it off! Off! TURN IT OFF!'
         * Set: Hoth
         * Rarity: C1
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("tio").getBlueprint();

        assertEquals("Turn It Off! Turn It Off!", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.HOTH);
        }});
        assertEquals(ExpansionSet.HOTH, card.getExpansionSet());
        assertEquals(Rarity.C1, card.getRarity());
    }

    @Test
    public void TurnItOffTurnItOffCancelsHansToolkitOnTable() {
        var scn = GetScenario();

        var tio = scn.GetDSCard("tio");
        var toolkit = scn.GetLSCard("toolkit");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(tio);
        scn.MoveCardsToLocation(site, han);
        scn.AttachCardsTo(han, toolkit);

        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(tio));
        scn.DSPlayCard(tio);
        assertTrue(scn.DSHasCardChoiceAvailable(toolkit));
        scn.DSChooseCard(toolkit);
        scn.PassAllResponses();

        assertEquals(Zone.LOST_PILE, toolkit.getZone());
    }

    @Test
    public void TurnItOffTurnItOffCancelsCrashSiteMemorialOnTable() {
        var scn = GetScenario();

        var tio = scn.GetDSCard("tio");
        var crash = scn.GetLSCard("crash");

        scn.StartGame();

        scn.MoveCardsToDSHand(tio);
        scn.MoveCardsToLSSideOfTable(crash);

        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(tio));
        scn.DSPlayCard(tio);
        assertTrue(scn.DSHasCardChoiceAvailable(crash));
        scn.DSChooseCard(crash);
        scn.PassAllResponses();

        assertEquals(Zone.LOST_PILE, crash.getZone());
    }

    @Test
    public void TurnItOffTurnItOffCanPlayMultipleCopiesSameTurn() {
        var scn = GetScenario();

        var tio = scn.GetDSCard("tio");
        var tio2 = scn.GetDSCard("tio2");
        var toolkit = scn.GetLSCard("toolkit");
        var crash = scn.GetLSCard("crash");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(tio, tio2);
        scn.MoveCardsToLocation(site, han);
        scn.AttachCardsTo(han, toolkit);

        scn.SkipToPhase(Phase.CONTROL);

        scn.DSPlayCard(tio);
        scn.DSChooseCard(toolkit);
        scn.PassAllResponses();
        assertEquals(Zone.LOST_PILE, toolkit.getZone());

        scn.MoveCardsToLSSideOfTable(crash);

        assertTrue(scn.DSCardPlayAvailable(tio2));
        scn.DSPlayCard(tio2);
        assertTrue(scn.DSHasCardChoiceAvailable(crash));
        scn.DSChooseCard(crash);
        scn.PassAllResponses();
        assertEquals(Zone.LOST_PILE, crash.getZone());
    }
}
