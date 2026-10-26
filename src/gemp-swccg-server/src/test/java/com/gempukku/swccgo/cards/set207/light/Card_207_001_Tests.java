package com.gempukku.swccgo.cards.set207.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_207_001_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("baze", "207_1");
                }},
                new HashMap<>() {{
                    put("sniper", "2_139");
                    put("eppVader", "108_006");
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
    public void BazeMalbusWithCannonStatsAndKeywordsAreCorrect() {
        /**
         * Title: Baze Malbus With Cannon
         * Uniqueness: Unique
         * Side: Light
         * Type: Character
         * Subtype: Rebel
         * Destiny: 2
         * Deploy: 4
         * Power: 4
         * Ability: 3
         * Forfeit: 5
         * Armor: 5
         * Persona: Baze
         * Icons: Warrior, Permanent Weapon, Virtual Set 7
         * Game Text: If about to be lost, may fire Baze's Cannon. Permanent weapon is •Baze's Cannon
         *             (may target a character for free; draw destiny; add 1 if targeting a character of ability < 3;
         *             target hit, and its forfeit = 0, if total destiny + 1 > defense value).
         * Set: Set 7
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("baze").getBlueprint();

        assertEquals("Baze Malbus With Cannon", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.BAZE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.WARRIOR);
            add(Icon.PERMANENT_WEAPON);
            add(Icon.VIRTUAL_SET_7);
        }});
        assertEquals(ExpansionSet.SET_7, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BazeMalbusWithCannonFiresOnceWhenAboutToBeLostFromSniper() {
        var scn = GetScenario();
        var baze = scn.GetLSCard("baze");
        var eppVader = scn.GetDSCard("eppVader");
        var trooper = scn.GetDSCard("trooper");
        var sniper = scn.GetDSCard("sniper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, baze, eppVader, trooper);
        scn.MoveCardsToDSHand(sniper);

        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareDSDestiny(6);
        scn.PrepareDSDestiny(7);
        scn.PrepareLSDestiny(6);

        scn.DSPlayCard(sniper);
        scn.DSChooseCard(eppVader);
        scn.LSPass();
        scn.DSPass();
        // Only Baze is a legal saber target, so the fire target is auto-chosen.
        passUntilLsAboutToBeLost(scn);

        assertTrue("Baze may fire when about to be lost from Sniper; got: " + decisionText(scn)
                        + " lsActions=" + scn.GetLSAvailableActions(),
                scn.LSDecisionAvailable("ABOUT_TO_BE_LOST")
                        || scn.LSDecisionAvailable("About to lose")
                        || scn.LSDecisionAvailable("Fire Baze")
                        || bazeFireOffered(scn));
        takeBazeFire(scn);
        passUntilCardChoiceOrIdle(scn);
        if (scn.LSHasCardChoiceAvailable(trooper)) {
            scn.LSChooseCard(trooper);
        }
        passUntilLsAboutToBeLost(scn);

        assertFalse("Baze must not fire again after the nested hit; got: " + decisionText(scn)
                        + " lsActions=" + scn.GetLSAvailableActions(),
                bazeFireOffered(scn));
        scn.PassAllResponses();

        assertTrue("Baze is lost after the one fire-back; zone=" + baze.getZone(),
                baze.getZone() == Zone.LOST_PILE || baze.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue("Trooper hit by Baze is lost; zone=" + trooper.getZone(),
                trooper.getZone() == Zone.LOST_PILE || trooper.getZone() == Zone.TOP_OF_LOST_PILE);
        assertEquals("Vader stays; zone=" + eppVader.getZone(), Zone.AT_LOCATION, eppVader.getZone());
    }

    private static boolean bazeFireOffered(VirtualTableScenario scn) {
        return scn.GetLSAvailableActions().stream().anyMatch(a -> a != null && a.toLowerCase().contains("fire"));
    }

    private static void takeBazeFire(VirtualTableScenario scn) {
        if (bazeFireOffered(scn)) {
            scn.LSChooseAction("Fire");
            return;
        }
        scn.LSChooseYes();
    }

    private static void passUntilLsAboutToBeLost(VirtualTableScenario scn) {
        for (int i = 0; i < 40; i++) {
            if (scn.LSDecisionAvailable("ABOUT_TO_BE_LOST")
                    || scn.LSDecisionAvailable("About to lose")
                    || scn.LSDecisionAvailable("Fire Baze")
                    || bazeFireOffered(scn)) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            String text = decision.getText();
            if (text == null || !text.toLowerCase().contains("optional response")) {
                return;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }

    private static void passUntilCardChoiceOrIdle(VirtualTableScenario scn) {
        for (int i = 0; i < 30; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            String text = decision.getText();
            if (text != null && (text.toLowerCase().contains("choose") || text.toLowerCase().contains("target"))) {
                return;
            }
            if (text == null || !text.toLowerCase().contains("optional response")) {
                return;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }

    private static String decisionText(VirtualTableScenario scn) {
        var d = scn.GetCurrentDecision();
        return d == null ? "null" : d.getText();
    }
}
