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
import static org.junit.Assert.assertTrue;

public class Card_213_033_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("blaster", "213_033");
                    put("vigo", "10_053");
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

    // associated issue: https://github.com/PlayersCommittee/gemp-swccg-public/issues/827
    @Test
    public void BlackSunBlasterMayActivateForceAfterObjectiveRecirculates() {
        var scn = GetScenario();

        var blaster = scn.GetDSCard("blaster");
        var vigo = scn.GetDSCard("vigo");
        var shadow = scn.GetDSCard("shadow");
        var trooper = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, vigo, trooper);
        scn.AttachCardsTo(vigo, blaster);

        scn.SkipToDSTurn(Phase.BATTLE);
        while (scn.GetDSReserveDeckCount() > 0) {
            scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSReserveDeck());
        }
        scn.PrepareDSDestiny(7);

        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw();

        assertTrue(scn.DSCardActionAvailable(shadow, "Re-circulate"));
        scn.DSUseCardAction(shadow, "Re-circulate");
        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("activate 1 Force"));
    }
}
