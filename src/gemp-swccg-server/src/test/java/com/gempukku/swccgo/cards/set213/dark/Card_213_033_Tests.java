package com.gempukku.swccgo.cards.set213.dark;

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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Card_213_033_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("pao", "214_17");
                    put("scarif", "209_23");
                    put("beach", "209_24");
                    put("vault", "209_25");
                    put("lightmaker", "209_30");
                    put("sefla", "209_13");
                    put("tantive", "201_19");
                    put("bodhi", "206_1");
                }},
                new HashMap<>() {{
                    put("blaster", "213_033");
                    put("vigo", "10_053");
                    put("qira", "217_19");
                    put("zuckuss", "110_12");
                    put("chokk", "12_99");
                    put("balatik", "209_33");
                    put("fourlom", "109_6");
                    put("zam", "204_46");
                    put("cdb", "213_34");
                    put("firststrike", "7_229");
                    put("secretplans", "13_86");
                    put("reception", "213_27");
                    put("hoth", "3_144");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.ShadowCollectiveObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void BlackSunBlasterStatsAndKeywordsAreCorrect() {
        /**
         * Title: Black Sun Blaster
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Weapon
         * Subtype: Character
         * Destiny: 4
         * Icons: Special Edition, Set 13
         * Keyword: Blaster
         * Game Text: Use 1 Force to deploy on your alien warrior (free if your Black Sun agent leader on table).
         *         May target a character or vehicle for free. Draw destiny. If destiny +1 > defense value, target hit
         *         and you may activate 1 Force.
         * Set: Set 13
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("blaster").getBlueprint();

        assertEquals(Title.Black_Sun_Blaster, card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.SPECIAL_EDITION);
            add(Icon.VIRTUAL_SET_13);
        }});
        assertEquals(ExpansionSet.SET_13, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BlackSunBlasterMissThenRevertDoesNotCrash() {
        var scn = GetScenario();

        var blaster = scn.GetDSCard("blaster");
        var vigo = scn.GetDSCard("vigo");
        var pao = scn.GetLSCard("pao");
        var trooper = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vigo, pao, trooper, scn.GetDSFiller(1), scn.GetLSFiller(2));
        scn.AttachCardsTo(vigo, blaster);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(0);

        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(pao);
        scn.PassWeaponFireWithDestinyDraw();
        assertFalse(pao.isHit());

        scn.IssueRevert("Start of Dark Side Player's battle phase #1");

        pao = scn.GetPostRevertCard(pao);
        site = scn.GetPostRevertCard(site);
        assertNotNull(scn.GetCurrentDecision());
        assertFalse(pao.isHit());
        assertTrue(scn.DSCanInitiateBattle(site) || scn.AwaitingDSBattlePhaseActions() || scn.IsActiveBattle());
    }

    /**
     * Replay CoffeePass$s81mh1lzr9v1gt2k last battle: Lightmaker vs loaded Zuckuss at Scarif,
     * fat table still in play (Beach, Data Vault, First Light, Hoth, First Strike, Secret Plans).
     * That is the "revert last battle" window. Layout is cheated; the battle and revert are played.
     */
    @Test
    public void LastScarifBattleRevertDoesNotCrash() {
        var scn = GetScenario();

        var scarif = scn.GetLSCard("scarif");
        var beach = scn.GetLSCard("beach");
        var vault = scn.GetLSCard("vault");
        var lightmaker = scn.GetLSCard("lightmaker");
        var sefla = scn.GetLSCard("sefla");
        var tantive = scn.GetLSCard("tantive");
        var pao = scn.GetLSCard("pao");
        var bodhi = scn.GetLSCard("bodhi");

        var zuckuss = scn.GetDSCard("zuckuss");
        var chokk = scn.GetDSCard("chokk");
        var balatik = scn.GetDSCard("balatik");
        var fourlom = scn.GetDSCard("fourlom");
        var qira = scn.GetDSCard("qira");
        var blaster = scn.GetDSCard("blaster");
        var zam = scn.GetDSCard("zam");
        var cdb = scn.GetDSCard("cdb");
        var firststrike = scn.GetDSCard("firststrike");
        var secretplans = scn.GetDSCard("secretplans");
        var reception = scn.GetDSCard("reception");
        var hoth = scn.GetDSCard("hoth");

        scn.StartGame();
        scn.MoveLocationToTable(scarif);
        scn.MoveLocationToTable(beach);
        scn.MoveLocationToTable(vault);
        scn.MoveLocationToTable(reception);
        scn.MoveLocationToTable(hoth);

        scn.MoveCardsToLocation(scarif, lightmaker, zuckuss, tantive);
        scn.BoardAsPilot(zuckuss, balatik);
        scn.BoardAsPassenger(zuckuss, chokk, fourlom);
        scn.BoardAsPassenger(tantive, sefla);
        scn.MoveCardsToLocation(beach, pao, bodhi, qira);
        scn.AttachCardsTo(qira, blaster);
        scn.MoveCardsToLocation(reception, zam);
        scn.AttachCardsTo(zam, cdb);
        scn.MoveCardsToLocation(hoth, scn.GetDSFiller(1));
        scn.MoveCardsToLocation(vault, scn.GetDSFiller(2));
        scn.MoveCardsToDSSideOfTable(firststrike, secretplans);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(scarif));
        scn.LSInitiateBattle(scarif);

        revertFromFirstEligiblePrompt(scn);

        scarif = scn.GetPostRevertCard(scarif);
        lightmaker = scn.GetPostRevertCard(lightmaker);
        zuckuss = scn.GetPostRevertCard(zuckuss);
        assertNotNull(scn.GetCurrentDecision());
        assertTrue(scn.LSCanInitiateBattle(scarif) || scn.AwaitingLSBattlePhaseActions() || scn.IsActiveBattle());
    }

    private void revertFromFirstEligiblePrompt(VirtualTableScenario scn) {
        for (int i = 0; i < 30; ++i) {
            if (scn.AwaitingDSForceLossPayment()) {
                scn.DSPayRemainingForceLossFromReserveDeck();
                continue;
            }
            if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayRemainingForceLossFromReserveDeck();
                continue;
            }
            if (hasRevert(scn.LSGetDecision()) || hasRevert(scn.DSGetDecision())) {
                scn.IssueRevert("Start of Light Side Player's battle phase #1");
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision != null && decision.getText() != null
                    && decision.getText().toLowerCase().contains("optional")) {
                scn.PassResponses("optional");
                continue;
            }
            break;
        }
        var ls = scn.LSGetDecision();
        var ds = scn.DSGetDecision();
        throw new AssertionError("No revert-eligible prompt. LS="
                + (ls == null ? "null" : ls.getText())
                + " DS=" + (ds == null ? "null" : ds.getText()));
    }

    private boolean hasRevert(com.gempukku.swccgo.logic.decisions.AwaitingDecision decision) {
        if (decision == null) {
            return false;
        }
        var revertEligible = decision.getDecisionParameters().get("revertEligible");
        return revertEligible != null && revertEligible.length > 0 && "true".equals(revertEligible[0]);
    }
}
