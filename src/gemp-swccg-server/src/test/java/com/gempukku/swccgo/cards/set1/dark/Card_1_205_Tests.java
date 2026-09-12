package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * VHD for #943: stolen character devices must become inactive when held by an
 * invalid user (via getGameTextValidToUseDeviceFilter), matching weapons.
 */
public class Card_1_205_Tests {
    protected VirtualTableScenario GetScenario() throws DecisionResultInvalidException {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("lin", "1_18");
                    put("merc_sunlet", "2_36");
                }},
                new HashMap<>() {{
                    put("restraining_bolt", "1_205");
                    put("utility_belt", "1_207");
                    put("vibro_ax", "6_180");
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
    public void RestrainingBoltStatsAndKeywordsAreCorrect() throws DecisionResultInvalidException {
        /**
         * Title: Restraining Bolt
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Device
         * Subtype: Character
         * Destiny: 6
         */

        var scn = GetScenario();
        var card = scn.GetDSCard("restraining_bolt").getBlueprint();

        assertEquals("Restraining Bolt", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.isCardType(CardType.DEVICE));
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        assertEquals(6, card.getDestiny(), scn.epsilon);
    }

    @Test
    public void StolenRestrainingBoltDoesNotAffectNonDroidHolder() throws DecisionResultInvalidException {
        // Real-path: Merc Sunlet steals Restraining Bolt onto Luke (non-droid).
        // Bolt must be inactive; Luke keeps gametext and may move.
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var lin = scn.GetLSCard("lin");
        var merc = scn.GetLSCard("merc_sunlet");
        var bolt = scn.GetDSCard("restraining_bolt");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, lin);
        scn.AttachCardsTo(luke, merc);
        // Bolt starts on a valid droid so Merc Sunlet can steal it.
        scn.AttachCardsTo(lin, bolt);

        assertTrue(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), lin));
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), lin));
        assertTrue(scn.IsCardActive(bolt));

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(merc, "Steal device"));
        scn.PrepareLSDestiny(0); // destiny 0 < bolt destiny 6
        scn.LSUseCardAction(merc, "Steal device");
        scn.LSChooseCard(bolt);
        scn.PassAllResponses();

        assertEquals(luke, bolt.getAttachedTo());
        assertFalse(scn.IsCardActive(bolt));
        assertFalse(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), luke));
        assertFalse(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), luke));
        assertFalse(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), lin));
        assertFalse(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), lin));
    }

    @Test
    public void RestrainingBoltStillAffectsDroidHolder() throws DecisionResultInvalidException {
        var scn = GetScenario();

        var lin = scn.GetLSCard("lin");
        var bolt = scn.GetDSCard("restraining_bolt");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lin);
        scn.AttachCardsTo(lin, bolt);

        assertTrue(scn.IsCardActive(bolt));
        assertTrue(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), lin));
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), lin));
    }

    @Test
    public void StolenStormtrooperUtilityBeltDoesNotGiveRebelPowerBonus() throws DecisionResultInvalidException {
        // Real-path: Merc Sunlet steals Utility Belt onto Luke (Rebel, not Imperial/alien).
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var merc = scn.GetLSCard("merc_sunlet");
        var belt = scn.GetDSCard("utility_belt");
        var site = scn.GetLSStartingLocation();
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.AttachCardsTo(luke, merc);
        scn.AttachCardsTo(trooper, belt);

        int lukeBasePower = scn.GetPower(luke);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(merc, "Steal device"));
        scn.PrepareLSDestiny(0); // destiny 0 < belt destiny 4
        scn.LSUseCardAction(merc, "Steal device");
        scn.LSChooseCard(belt);
        scn.PassAllResponses();

        assertEquals(luke, belt.getAttachedTo());
        assertFalse(scn.IsCardActive(belt));
        assertEquals(lukeBasePower, scn.GetPower(luke));
    }

    @Test
    public void StolenVibroAxStillDoesNotGiveInvalidUserPowerBonus() throws DecisionResultInvalidException {
        // Weapons path regression: Rebel is not an alien warrior.
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var ax = scn.GetDSCard("vibro_ax");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        int lukeBasePower = scn.GetPower(luke);
        scn.AttachCardsTo(luke, ax);

        assertFalse(scn.IsCardActive(ax));
        assertEquals(lukeBasePower, scn.GetPower(luke));
    }
}
