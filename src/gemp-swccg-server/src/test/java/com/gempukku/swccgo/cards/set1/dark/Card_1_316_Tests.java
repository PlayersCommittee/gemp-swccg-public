package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.CardSubtype;
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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1_316_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("alien", "1_15");
                    put("weapon", "1_152");
                }},
                new HashMap<>() {{
                    put("seeker", "1_316");
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
    public void HanSeekerStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("seeker").getBlueprint();
        assertEquals("Han Seeker", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.AUTOMATED, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SEEKER);
        }});
        assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
        assertEquals(Rarity.R2, card.getRarity());
    }

    @Test
    public void HanSeekerLosesPresentAlienAndAttachedWeapon() {
        var scn = GetScenario();
        var alien = scn.GetLSCard("alien");
        var weapon = scn.GetLSCard("weapon");
        var seeker = scn.GetDSCard("seeker");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, alien);
        scn.AttachCardsTo(alien, weapon);
        scn.MoveCardsToLocation(site, seeker);
        try {
            scn.SkipToDSTurn(Phase.CONTROL);
        } catch (RuntimeException ignored) {
            // Required Seeker trigger interrupts the skip.
        }

        for (int i = 0; i < 12; i++) {
            if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(alien)) {
                scn.DSChooseCard(alien);
                break;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                break;
            }
            if (decision.getText().toLowerCase().contains("optional")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            } else {
                break;
            }
        }
        scn.PassAllResponses();
        for (int i = 0; i < 8; i++) {
            if (alien.getZone() == Zone.LOST_PILE && weapon.getZone() == Zone.LOST_PILE && seeker.getZone() == Zone.LOST_PILE) {
                break;
            }
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(alien)) {
                scn.LSChooseCard(alien);
            } else if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(weapon)) {
                scn.LSChooseCard(weapon);
            } else if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(seeker)) {
                scn.DSChooseCard(seeker);
            } else {
                scn.PassAllResponses();
            }
        }

        assertInZone(Zone.LOST_PILE, alien);
        assertInZone(Zone.LOST_PILE, weapon);
        assertInZone(Zone.LOST_PILE, seeker);
    }
}
