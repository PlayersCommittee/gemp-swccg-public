package com.gempukku.swccgo.cards.set8.light;

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
import static org.junit.Assert.assertTrue;

/**
 * Issue #999 / #911: Chewbacca's Bowcaster should fire for free while on Chewie.
 * Printed: "using 3 Force (for free if Chewie firing)."
 */
public class Card_8_086_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("chewie", "8_2");
                    put("bowcaster", "8_86");
                }},
                new HashMap<>() {{
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

    private void drainLsForce(VirtualTableScenario scn) {
        int leftover = scn.GetLSForcePileCount();
        if (leftover > 0) {
            scn.LSUseForceCheat(leftover);
        }
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void ChewbaccasBowcasterStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("bowcaster").getBlueprint();

        assertEquals(Title.Chewbaccas_Bowcaster, card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ENDOR);
            add(Icon.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BOWCASTER);
        }});
        assertEquals(ExpansionSet.ENDOR, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void ChewbaccasBowcasterFiresForFreeOnChewieAtZeroForce() {
        var scn = GetScenario();
        var chewie = scn.GetLSCard("chewie");
        var bowcaster = scn.GetLSCard("bowcaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, chewie, trooper);
        scn.AttachCardsTo(chewie, bowcaster);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        drainLsForce(scn);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue("Chewie firing must be free (printed 3 Force otherwise)",
                scn.LSCardActionAvailable(bowcaster, "Fire"));
    }
}
