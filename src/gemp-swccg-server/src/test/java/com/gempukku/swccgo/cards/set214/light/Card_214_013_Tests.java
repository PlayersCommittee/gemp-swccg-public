package com.gempukku.swccgo.cards.set214.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_214_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ff", "214_13");
                    put("luke", "1_019");
                }},
                new HashMap<>() {{
                    put("oso", "3_130");
                    put("nute", "12_112");
                    put("droid1", "12_104");
                    put("droid2", "12_104");
                    put("droid3", "12_104");
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
    public void AlterAndFriendlyFireStatsAndKeywordsAreCorrect() {
        /**
         * Title: Alter & Friendly Fire
         * Uniqueness: Unrestricted
         * Side: Light
         * Type: Interrupt
         * Destiny: 4
         * Icons: Reflections II, Virtual Set 14, Interrupt
         * Set: Set 14
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("ff").getBlueprint();

        assertEquals("Alter & Friendly Fire", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REFLECTIONS_II);
            add(Icon.VIRTUAL_SET_14);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.SET_14, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AlterAndFriendlyFireOhSwitchOffNotLegalOnInitiationCountSet() {
        var scn = GetScenario();
        var ff = scn.GetLSCard("ff");
        var luke = scn.GetLSCard("luke");
        var oso = scn.GetDSCard("oso");
        var nute = scn.GetDSCard("nute");
        var droid1 = scn.GetDSCard("droid1");
        var droid2 = scn.GetDSCard("droid2");
        var droid3 = scn.GetDSCard("droid3");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(ff);
        scn.MoveCardsToDSHand(oso);
        scn.MoveCardsToLocation(site, luke, nute, droid1, droid2, droid3);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);

        assertTrue(scn.LSCardPlayAvailable(ff));
        scn.LSPlayCard(ff);

        assertFalse("Oh, Switch Off must not answer Friendly Fire's count-set; those characters are not targeted to be lost yet",
                scn.DSCardPlayAvailable(oso));
    }

    @Test
    public void AlterAndFriendlyFireOhSwitchOffCancelsChosenDroidLoseAndProtectsOnlyThatDroid() {
        var scn = GetScenario();
        var ff = scn.GetLSCard("ff");
        var luke = scn.GetLSCard("luke");
        var oso = scn.GetDSCard("oso");
        var nute = scn.GetDSCard("nute");
        var droid1 = scn.GetDSCard("droid1");
        var droid2 = scn.GetDSCard("droid2");
        var droid3 = scn.GetDSCard("droid3");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(ff);
        scn.MoveCardsToDSHand(oso);
        scn.MoveCardsToLocation(site, luke, nute, droid1, droid2, droid3);
        scn.MoveCardsToTopOfOwnReserveDeck(scn.GetLSFiller(1));

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);

        assertTrue(scn.LSCardPlayAvailable(ff));
        scn.LSPlayCard(ff);
        assertFalse(scn.DSCardPlayAvailable(oso));
        scn.PassAllResponses();

        boolean chose = false;
        boolean playedOso = false;
        for (int i = 0; i < 40; i++) {
            if (!scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
                break;
            }
            String player = scn.GetDecidingPlayer();
            var decision = scn.GetAwaitingDecision(player);
            if (decision == null) {
                break;
            }
            String text = decision.getText() == null ? "" : decision.getText().toLowerCase();
            if (!chose && player.equals(scn.DS) && scn.DSHasCardChoiceAvailable(droid1)) {
                scn.DSChooseCard(droid1);
                chose = true;
                continue;
            }
            String[] actionIds = scn.GetADParam(player, "actionId");
            String[] actionTexts = scn.GetADParam(player, "actionText");
            String[] cardIds = scn.GetADParam(player, "cardId");
            if (chose && player.equals(scn.DS) && actionTexts != null && cardIds != null) {
                boolean osoUp = false;
                for (String a : actionTexts) {
                    if (a != null && (a.toLowerCase().contains("switch off") || a.toLowerCase().contains("cancel targeting")
                            || a.toLowerCase().contains("protect"))) {
                        osoUp = true;
                        break;
                    }
                }
                if (osoUp) {
                    scn.DSPlayCard(oso);
                    playedOso = true;
                    continue;
                }
            }
            if (text.contains("optional")) {
                scn.PlayerPass(player);
                continue;
            }
            if (actionIds != null && actionIds.length > 0) {
                scn.PlayerPass(player);
                continue;
            }
            String[] chooseIds = scn.GetADParam(player, "cardId");
            if (chooseIds != null && chooseIds.length > 0) {
                scn.PlayerDecided(player, chooseIds[0]);
                continue;
            }
            String[] results = scn.GetADParam(player, "results");
            if (results != null && results.length > 0) {
                scn.PlayerDecided(player, "0");
                continue;
            }
            String[] max = scn.GetADParam(player, "max");
            if (max != null && max.length > 0) {
                scn.PlayerDecided(player, max[0]);
                continue;
            }
            try {
                scn.PlayerPass(player);
            } catch (RuntimeException e) {
                throw new AssertionError("Could not resolve " + player + " decision: " + text
                        + " keys=" + decision.getDecisionParameters().keySet()
                        + " chose=" + chose + " playedOso=" + playedOso, e);
            }
        }

        assertTrue("Dark should have chosen a droid to be lost after destiny succeeded", chose);
        assertTrue("Oh, Switch Off should be legal after the droid is targeted to be lost", playedOso);
        assertEquals("Chosen droid must remain; Oh, Switch Off canceled that lose", Zone.AT_LOCATION, droid1.getZone());
        assertEquals(Zone.AT_LOCATION, droid2.getZone());
        assertEquals(Zone.AT_LOCATION, droid3.getZone());
        assertEquals(Zone.AT_LOCATION, nute.getZone());
        assertTrue(oso.getZone() == Zone.USED_PILE || oso.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
