package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_224_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ben", "224_011");
                    put("rey", "204_009");
                    put("saber", "3_071");
                    put("mpws", "217_041");
                }},
                new HashMap<>() {{
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
    public void BenSoloStatsAndKeywordsAreCorrect() {
        /**
         * Title: Ben Solo
         * Uniqueness: Unique
         * Side: Light
         * Type: Character
         * Subtype: Resistance
         * Destiny: 1
         * Deploy: 5
         * Power: 6
         * Ability: 5
         * Forfeit: 8
         * Icons: Pilot, Warrior, Episode VII, Virtual Set 24
         * Persona: Ben Solo
         * Game Text: If drawn for destiny, may [upload] Rey or a lightsaber. [Pilot] 3. Deploys only if an [Episode VII]
         *         Epic Event on table. Your total battle destiny here is +1 for each of your Interrupts out of play
         *         (limit +3). Once per game, may deploy a lightsaber on Ben Solo from Lost Pile.
         * Lore: Skywalker.
         * Set: Set 24
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("ben").getBlueprint();

        assertEquals("Ben Solo", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(5, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.RESISTANCE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.BEN_SOLO);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.RESISTANCE);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_VII);
            add(Icon.VIRTUAL_SET_24);
        }});
        assertEquals(ExpansionSet.SET_24, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    // associated issue: https://github.com/PlayersCommittee/gemp-swccg-public/issues/829
    @Test
    public void BenSoloDoesNotUploadAgainAfterTakenIntoHand() {
        var scn = GetScenario();

        var ben = scn.GetLSCard("ben");
        var rey = scn.GetLSCard("rey");
        var saber = scn.GetLSCard("saber");
        var mpws = scn.GetLSCard("mpws");
        var trooper = scn.GetDSFiller(1);
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, rey, trooper);
        scn.AttachCardsTo(site, mpws);

        scn.StartBattleAndSkipToWeaponsSegment(site);
        scn.SkipToPowerSegment();
        scn.MoveCardsToTopOfLSReserveDeck(saber, ben);

        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");

        if (scn.DSDecisionAvailable("DESTINY_DRAWN")) {
            scn.DSPass();
        }

        assertTrue(scn.LSCardActionAvailable(ben, "Take Rey or a lightsaber"));
        assertTrue(scn.LSCardActionAvailable(mpws, "Take destiny card into hand"));

        scn.LSUseCardAction(ben, "Take Rey or a lightsaber");
        scn.LSChooseCard(saber);
        for (int i = 0; i < 10; i++) {
            if (scn.LSDecisionAvailable("DESTINY_DRAWN") || scn.DSDecisionAvailable("DESTINY_DRAWN")) {
                break;
            }
            if (scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
                scn.PassResponses("optional");
            } else {
                break;
            }
        }
        if (scn.DSDecisionAvailable("DESTINY_DRAWN")) {
            scn.DSPass();
        }

        assertInHand(saber);
        assertTrue(scn.LSCardActionAvailable(mpws, "Take destiny card into hand"));

        scn.LSUseCardAction(mpws, "Take destiny card into hand");
        for (int i = 0; i < 10; i++) {
            if (scn.LSDecisionAvailable("DESTINY_DRAWN") || scn.DSDecisionAvailable("DESTINY_DRAWN")) {
                break;
            }
            if (scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
                scn.PassResponses("optional");
            } else {
                break;
            }
        }
        if (scn.DSDecisionAvailable("DESTINY_DRAWN")) {
            scn.DSPass();
        }

        assertInHand(ben);
        assertFalse(scn.LSCardActionAvailable(ben, "Take Rey or a lightsaber"));
    }
}
