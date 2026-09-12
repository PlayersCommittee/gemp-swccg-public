package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
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
                }},
                new HashMap<>() {{
                    put("restraining_bolt", "1_205");
                    put("utility_belt", "1_207");
                    put("vibro_ax", "6_180");
                    put("ds_droid", "1_192");
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
        // Steal/reattach path: bolt on droid is active; after moving to Luke it is inactive.
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var bolt = scn.GetDSCard("restraining_bolt");
        var dsDroid = scn.GetDSCard("ds_droid");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, dsDroid);
        scn.AttachCardsTo(dsDroid, bolt);

        assertTrue(scn.IsCardActive(bolt));
        assertTrue(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), dsDroid));

        scn.AttachCardsTo(luke, bolt);

        assertEquals(luke, bolt.getAttachedTo());
        assertFalse(scn.IsCardActive(bolt));
        assertFalse(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), luke));
        assertFalse(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), dsDroid));
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
    }

    @Test
    public void StolenStormtrooperUtilityBeltDoesNotGiveRebelPowerBonus() throws DecisionResultInvalidException {
        // Steal/reattach path: belt on Imperial is active; on Rebel it is inactive (no +1).
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var belt = scn.GetDSCard("utility_belt");
        var site = scn.GetLSStartingLocation();
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        int lukeBasePower = scn.GetPower(luke);
        scn.AttachCardsTo(trooper, belt);
        assertTrue(scn.IsCardActive(belt));

        scn.AttachCardsTo(luke, belt);

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
