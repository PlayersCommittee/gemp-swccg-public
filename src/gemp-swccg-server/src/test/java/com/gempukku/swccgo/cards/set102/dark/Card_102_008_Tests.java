package com.gempukku.swccgo.cards.set102.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.modifiers.MayNotParticipateInBattleModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_102_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("han", "1_11");
                    put("falcon", "1_143");
                }},
                new HashMap<>() {{
                    put("shadow", "102_8");
                    put("tie", "1_304");
                    put("kessel", "1_288");
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
    public void GravityShadowStatsAndKeywordsAreCorrect() {
        /**
         * Title: Gravity Shadow
         * Uniqueness: Unique
         * Side: Dark
         * Type: Interrupt
         * Subtype: Lost
         * Destiny: 4
         * Icons: Premium
         * Game Text: If opponent's starship is about to move through hyperspace, target that starship and its highest-ability pilot.
         *         Draw destiny. If destiny > pilot's ability, starship may not move this turn. If destiny = pilot's ability, starship is lost.
         * Set: Jedi Pack
         * Rarity: PM
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("shadow").getBlueprint();

        assertEquals(Title.Gravity_Shadow, card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PREMIUM);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.JEDI_PACK, card.getExpansionSet());
        assertEquals(Rarity.PM, card.getRarity());
    }

    @Test
    public void GravityShadowIgnoresExcludedHighestAbilityPilotWhenChoosingTarget() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        var falcon = scn.GetLSCard("falcon");
        var kessel = scn.GetDSCard("kessel");
        var shadow = scn.GetDSCard("shadow");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, falcon, tie);
        scn.BoardAsPilot(falcon, luke, han);
        scn.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                new MayNotParticipateInBattleModifier(shadow, luke));

        scn.StartBattleAndSkipToWeaponsSegment(kessel);
        scn.PassAllResponses();

        Collection<PhysicalCard> spottedPilots = Filters.filterActive(scn.game(), shadow, Filters.piloting(falcon));
        assertFalse(spottedPilots.contains(luke));
        assertTrue(spottedPilots.contains(han));

        float spottedHighest = 0;
        for (PhysicalCard pilot : spottedPilots) {
            spottedHighest = Math.max(spottedHighest, scn.game().getModifiersQuerying().getAbility(scn.gameState(), pilot));
        }
        assertEquals(scn.game().getModifiersQuerying().getAbility(scn.gameState(), han), spottedHighest, scn.epsilon);
        assertTrue(scn.game().getModifiersQuerying().getAbility(scn.gameState(), luke)
                > scn.game().getModifiersQuerying().getAbility(scn.gameState(), han));
    }
}
