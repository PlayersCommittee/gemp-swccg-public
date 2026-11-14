package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_8_114_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("alter", "1_071");

                }},
                new HashMap<>()
                {{
                    put("tarl", "8_114"); //Sergeant Tarl
                    put("spice", "2_125"); //Spice Mines Of Kessel
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
    public void BothanSpyStatsAndKeywordsAreCorrect() {
        /**
         * Title: Sergeant Tarl
         * Uniqueness: Unique
         * Side: Dark
         * Type: Character
         * Subtype: Imperial
         * Destiny: 3
         * Deploy: 2
         * Power: 2
         * Ability: 2
         * Forfeit: 3
         * Icons: Imperial, Endor, Warrior
         * Species: Corellian
         * Keywords: Stormtrooper
         * Game Text: While Tarl is the trooper targeted by Spice Mines of Kessel, that Utinni Effect is
         *      immune to Alter and Tarl adds 4 to Force retrieved when completed. Subtracts 3 from Force opponent
         *      retrieves with Noble Sacrifice.
         * Lore: Took part in the capture of the Rebel Blockade Runner Tantive IV. Stormtrooper trained on Corulag.
         *      Corellia native.
         * Set: Endor
         * Rarity: U
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("tarl").getBlueprint();

        assertEquals("Sergeant Tarl", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        assertEquals(Species.CORELLIAN,card.getSpecies());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.STORMTROOPER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.ENDOR);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.ENDOR,card.getExpansionSet());
        assertEquals(Rarity.U,card.getRarity());
    }

    @Test
    public void SergeantTarlMakesSpiceMinesOfKesselTargetingSelfImmuneToAlter() {
        //tests immunity both during initial play and after plans is deployed (active on table)
        var scn = GetScenario();

        var alter = scn.GetLSCard("alter");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);

        var tarl = scn.GetDSCard("tarl");
        var spice = scn.GetDSCard("spice");
        var kessel = scn.GetDSCard("kessel");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(spice);
        scn.MoveCardsToLSHand(alter);

        scn.MoveLocationToTable(kessel);

        scn.MoveCardsToLocation(site,tarl,rebelTrooper1,rebelTrooper2);
        scn.CaptureCardWith(tarl,rebelTrooper1);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(spice);
        scn.DSChooseCard(kessel);
        scn.DSChooseCard(rebelTrooper1);
        scn.DSChooseCard(tarl);

        assertTrue(scn.LSDecisionAvailable("Deploying")); //Deploying Death Star Plans - Optional responses

        assertFalse(scn.LSCardPlayAvailable(alter));
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(kessel,spice));
        assertFalse(scn.LSCardPlayAvailable(alter));
    }

    @Test
    public void SergeantTarlMakesSpiceMinesOfKesselTargetingNonSelfImmuneToAlter() {
        //tests immunity both during initial play and after plans is deployed (active on table)
        var scn = GetScenario();

        var alter = scn.GetLSCard("alter");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);

        var tarl = scn.GetDSCard("tarl");
        var spice = scn.GetDSCard("spice");
        var kessel = scn.GetDSCard("kessel");
        var site = scn.GetDSStartingLocation();
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveCardsToDSHand(spice);
        scn.MoveCardsToLSHand(alter);

        scn.MoveLocationToTable(kessel);

        scn.MoveCardsToLocation(site,tarl,trooper,rebelTrooper1,rebelTrooper2);
        scn.CaptureCardWith(trooper,rebelTrooper1);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(spice);
        scn.DSChooseCard(kessel);
        scn.DSChooseCard(rebelTrooper1);
        scn.DSChooseCard(trooper);

        assertTrue(scn.LSDecisionAvailable("Deploying")); //Deploying Death Star Plans - Optional responses

        assertTrue(scn.LSCardPlayAvailable(alter));
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(kessel,spice));
        assertTrue(scn.LSCardPlayAvailable(alter));
    }

}
