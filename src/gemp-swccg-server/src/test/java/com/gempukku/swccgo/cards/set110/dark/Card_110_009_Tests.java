package com.gempukku.swccgo.cards.set110.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Issue #291: Jodo Kast "may fire for free and add 2" must be optional (dual actions), not auto-forced.
 */
public class Card_110_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "108_003"); // Luke With Lightsaber
                }},
                new HashMap<>() {{
                    put("jodo", "110_009"); // Jodo Kast
                    put("blaster", "1_317"); // Imperial Blaster
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
    public void JodoKastStatsAndKeywordsAreCorrect() {
        /**
         * Title: Jodo Kast
         * Uniqueness: Unique
         * Side: Dark
         * Type: Character
         * Subtype: Alien
         * Destiny: 2
         * Deploy: 4
         * Power: 3
         * Ability: 3
         * Forfeit: 3
         * Armor: 5
         * Icons: Premium, Warrior
         * Keywords: Bounty Hunter, Scout
         * Game Text: When in battle, if opponent draws more than one battle destiny, may cancel one.
         *             Once per turn, when firing a rifle or blaster, may target for free and add 2 to total weapon destiny.
         *             May be targeted by Hidden Weapons. May 'fly' (landspeed = 3).
         * Set: Enhanced Jabba's Palace
         * Rarity: PM
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("jodo").getBlueprint();

        assertEquals("Jodo Kast", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BOUNTY_HUNTER);
            add(Keyword.SCOUT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PREMIUM);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.ENHANCED_JABBAS_PALACE, card.getExpansionSet());
        assertEquals(Rarity.PM, card.getRarity());
    }

    @Test
    public void JodoKastOffersPaidFireAndFreePlus2WhenForceAvailable() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var jodo = scn.GetDSCard("jodo");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, jodo);
        scn.AttachCardsTo(jodo, blaster);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.GetDSForcePileCount() >= 2);
        scn.DSInitiateBattle(site);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());

        var actions = scn.GetDSAvailableActions();
        assertTrue(actions.stream().anyMatch(a -> a.contains("Fire") && !a.contains("for free and add 2")));
        assertTrue(actions.stream().anyMatch(a -> a.contains("for free and add 2")));
        assertNull(jodo.getWhileInPlayData());
    }

    @Test
    public void JodoKastPaidFireDoesNotConsumeFreePlus2Package() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var jodo = scn.GetDSCard("jodo");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, jodo);
        scn.AttachCardsTo(jodo, blaster);

        scn.SkipToPhase(Phase.BATTLE);
        int forceBeforeBattle = scn.GetDSForcePileCount();
        scn.DSInitiateBattle(site);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        int forceAfterBattle = scn.GetDSForcePileCount();
        assertEquals(forceBeforeBattle - 1, forceAfterBattle);

        scn.DSChooseAction("Fire Imperial Blaster");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();

        assertNull(jodo.getWhileInPlayData());
        assertEquals(forceAfterBattle - 1, scn.GetDSForcePileCount()); // paid 1 to fire Imperial Blaster
    }

    @Test
    public void JodoKastFreePlus2ConsumesPackageAndDoesNotSpendFireForce() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var jodo = scn.GetDSCard("jodo");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, jodo);
        scn.AttachCardsTo(jodo, blaster);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        int forceAfterBattle = scn.GetDSForcePileCount();

        assertTrue(scn.DSCardActionAvailable(blaster, "for free and add 2"));
        scn.DSUseCardAction(blaster, "for free and add 2");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();

        assertNotNull(jodo.getWhileInPlayData());
        assertEquals(forceAfterBattle, scn.GetDSForcePileCount()); // fire itself was free

        if (scn.AwaitingDSWeaponsSegmentActions()) {
            var actions = scn.GetDSAvailableActions();
            assertFalse(actions.stream().anyMatch(a -> a.contains("for free and add 2")));
            assertTrue(actions.stream().anyMatch(a -> a.contains("Fire") && !a.contains("for free and add 2")));
        }
    }

    @Test
    public void JodoKastWithZeroForceOnlyOffersFreePlus2() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var jodo = scn.GetDSCard("jodo");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, jodo);
        scn.AttachCardsTo(jodo, blaster);

        scn.SkipToPhase(Phase.BATTLE);
        // Leave exactly 1 Force to initiate battle (cost 1), so 0 remain to pay for fire
        while (scn.GetDSForcePileCount() > 1) {
            scn.MoveCardsToTopOfDSReserveDeck((PhysicalCardImpl) scn.GetTopOfDSForcePile());
        }
        assertEquals(1, scn.GetDSForcePileCount());

        scn.DSInitiateBattle(site);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertEquals(0, scn.GetDSForcePileCount());

        var actions = scn.GetDSAvailableActions();
        assertTrue(actions.stream().anyMatch(a -> a.contains("for free and add 2")));
        assertFalse(actions.stream().anyMatch(a -> a.contains("Fire") && !a.contains("for free and add 2")));
    }
}
