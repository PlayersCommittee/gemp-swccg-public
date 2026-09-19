package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_208_034_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                }},
                new HashMap<>()
                {{
                    put("maul", "208_034");
                    put("saber", "1_314");
                    put("saberv", "211_025");
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
    public void LordMaulWithLightsaberStatsAndKeywordsAreCorrect() {
        /**
         * Title: Lord Maul With Lightsaber
         * Uniqueness: Unique
         * Side: Dark
         * Type: Character
         * Subtype: Sith
         * Destiny: 1
         * Deploy: 6
         * Power: 7
         * Ability: 6
         * Forfeit: 7
         * Icons: Pilot, Warrior, Episode 1, Permanent Weapon, Virtual Set 8
         * Persona: Maul
         * Game Text: Permanent weapon is •Maul's Lightsaber (may add 1 to Force drain where present;
         *      adds 1 to your battle destiny draws here; may target a character for free;
         *      draw two destiny; target hit, and its forfeit = 0, if total destiny > defense value).
         * Lore: Trade Federation.
         * Set: Set 8
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("maul").getBlueprint();

        assertEquals("Lord Maul With Lightsaber", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(7, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.SITH);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.MAUL);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SITH);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.PERMANENT_WEAPON);
            add(Icon.VIRTUAL_SET_8);
        }});
        assertEquals(ExpansionSet.SET_8,card.getExpansionSet());
        assertEquals(Rarity.V,card.getRarity());
    }

    @Test
    public void LordMaulWithLightsaberMayAddToForceDrain() {
        //Tets1: may add 1 to force drain
        //Test2: may only add 1 to force drain once per turn
        var scn = GetScenario();

        var maul = scn.GetDSCard("maul");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, maul);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(site,"drain"));
        scn.DSUseCardAction(site);
        scn.LSPass(); //FORCE_DRAIN_INITIATED - Optional responses
        //assertTrue(scn.DSAwaitingResponse("Force drain initiated at");
        assertTrue(scn.DSCardActionAvailable(maul,"Add")); //Test1: may add to force drain
        scn.DSUseCardAction(maul);
            /// if coded correctly, should cause this additional response?
        //scn.LSPass(); //FORCE_DRAIN_ENHANCED_BY_WEAPON - Optional responses
        //scn.DSPass();
        scn.LSPass(); //FORCE_DRAIN_INITIATED - Optional responses
        //assertTrue(scn.DSAwaitingResponse("Force drain initiated at");
        assertFalse(scn.DSCardActionAvailable(maul,"Add")); //Test2: can only add once to drain
        scn.PassAllResponses();

        //assertTrue(scn.LSAwaitingForceLossPayment());
        //scn.LSPayRemainingForceLossFromReserveDeck();
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(2,scn.GetLSLostPileCount()); //1 + 1 from Maul
    }

    @Test
    public void LordMaulWithLightsaberMayNotAddToForceDrainIfUsedOtherWeapon() {
        assertSaberAddBlocksMaulAdd("saber");
    }

    @Test
    public void LordMaulWithLightsaberAddingToForceDrainUsesWeapon() {
        assertMaulAddBlocksSaberAdd("saber");
    }

    @Test
    public void LordMaulWithLightsaberMayNotAddToForceDrainIfUsedDarkJediLightsaberV() {
        assertSaberAddBlocksMaulAdd("saberv");
    }

    @Test
    public void LordMaulWithLightsaberAddingToForceDrainBlocksDarkJediLightsaberV() {
        assertMaulAddBlocksSaberAdd("saberv");
    }

    private void assertSaberAddBlocksMaulAdd(String saberName) {
        var scn = GetScenario();
        var maul = scn.GetDSCard("maul");
        var saber = scn.GetDSCard(saberName);
        var site = placeMaulWithSaber(scn, saber);

        drainUntilAdd(scn, site, saber);
        assertTrue(scn.DSCardActionAvailable(saber, "Add"));
        assertTrue(scn.DSCardActionAvailable(maul, "Add"));
        scn.DSUseCardAction(saber, "Add");
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        assertFalse("Maul already used a Dark Jedi Lightsaber this turn", scn.DSCardActionAvailable(maul, "Add"));
    }

    private void assertMaulAddBlocksSaberAdd(String saberName) {
        var scn = GetScenario();
        var maul = scn.GetDSCard("maul");
        var saber = scn.GetDSCard(saberName);
        var site = placeMaulWithSaber(scn, saber);

        drainUntilAdd(scn, site, saber);
        assertTrue(scn.DSCardActionAvailable(saber, "Add"));
        assertTrue(scn.DSCardActionAvailable(maul, "Add"));
        scn.DSUseCardAction(maul, "Add");
        scn.LSPass();
        assertFalse("Maul already used his permanent lightsaber this turn", scn.DSCardActionAvailable(saber, "Add"));
    }

    private PhysicalCardImpl placeMaulWithSaber(VirtualTableScenario scn, PhysicalCardImpl saber) {
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("maul"));
        scn.AttachCardsTo(scn.GetDSCard("maul"), saber);
        return site;
    }

    private void drainUntilAdd(VirtualTableScenario scn, PhysicalCardImpl site, PhysicalCardImpl saber) {
        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(site, "drain"));
        scn.DSUseCardAction(site);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("maul"), "Add")
                || scn.DSCardActionAvailable(saber, "Add"));
    }

}
