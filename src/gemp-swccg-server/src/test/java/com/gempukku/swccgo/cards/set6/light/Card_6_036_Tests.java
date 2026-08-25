package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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

public class Card_6_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("rkik","6_036"); //R'kik D'nec, Hero Of The Dune Sea
                    put("jawa","1_012");
                    put("blaster", "1_152");
                    put("ion","2_078"); //jawa ion gun (jawa weapon)
                    put("dune_sabacc","6_065"); //dune sea sabacc
                    put("jp_ac","6_081"); //jabba's palace: audience chamber
                    put("jp_sabacc","6_067"); //jabba's palace sabacc
                    put("rycar","1_063"); //rycar ryjerd
                    put("ls_destiny5","2_054"); //out of commmision
                    put("ls_destiny4","1_097"); //nabrun leids
                }},
                new HashMap<>()
                {{
                    put("ds_destiny3", "1_266"); //scanning crew
                    put("ds_destiny2", "1_268"); //set for stun
                    put("ds_destiny1", "2_132"); //ghhhk
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
    public void R_kik_D_necStatsAndKeywordsAreCorrect() {
        /**
         * Title: R'kik D'nec, Hero Of The Dune Sea
         * Uniqueness: Unique
         * Side: Light
         * Type: Character
         * Destiny: 1
         * Deploy: 3
         * Power: 3
         * Forfeit: 3
         * Icons: Alien, Jabba's Palace, Warrior
         * Game Text: Deploys only on Tatooine. Jawa weapons deploy free on R'kik; when firing one, may add up to 3
         *         to the total weapon destiny just drawn. When he is playing Dune Sea Sabacc and wins, wins double.
         * Lore: A tribe of Tusken Raiders, a herd of angry banthas, a raging Krayt Dragon and R'kik. Minutes later,
         *         the Jawa emerged from the Dune Sea, a bantha tusk over his shoulder.
         * Set: Jabba's Palace
         * Rarity: R
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("rkik").getBlueprint();

        assertEquals("R'kik D'nec, Hero Of The Dune Sea", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        assertEquals(Species.JAWA,card.getSpecies());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.JABBAS_PALACE);
        }});
        assertEquals(ExpansionSet.JABBAS_PALACE,card.getExpansionSet());
        assertEquals(Rarity.R,card.getRarity());
    }

    @Test
    public void R_kik_D_necDeploysOnTatooine() {
        //test1: can deploy on Tatooine
        //test2: cannot deploy on non-Tatooine (cloud city)
        //test3: deploy cost 3 paid
        var scn = GetScenario();

        var rkik = scn.GetLSCard("rkik");
        var cc_site = scn.GetLSStartingLocation();

        var tat_site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(rkik);

        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.GetLSForcePileCount() >= 3);
        assertTrue(scn.LSDeployAvailable(rkik));
        scn.LSDeployCard(rkik);
        assertTrue(scn.LSHasCardChoiceAvailable(tat_site)); //test1
        assertFalse(scn.LSHasCardChoiceAvailable(cc_site)); //test2
        scn.LSChooseCard(tat_site);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(3, scn.GetLSUsedPileCount()); //test3
        assertTrue(scn.CardsAtLocation(tat_site, rkik));
    }

    @Test
    public void R_kik_D_necJawaWeaponDeploysFree() {
        //test1: non-jawa weapon (blaster) does not deploy free on rkik
        //test2: jawa weapon (ion gun) deploys free on rkik
        var scn = GetScenario();

        var rkik = scn.GetLSCard("rkik");
        var ion = scn.GetLSCard("ion");
        var blaster = scn.GetLSCard("blaster");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, rkik);

        scn.MoveCardsToLSHand(ion, blaster);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(blaster));
        assertTrue(scn.LSDeployAvailable(ion));

        assertEquals(3, scn.GetLSForcePileCount());

        scn.LSDeployCard(blaster);
        scn.LSChooseCard(rkik);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(rkik, blaster));
        assertEquals(2, scn.GetLSForcePileCount()); //test1 (cost 1)

        scn.DSPass();
        scn.LSDeployCard(ion);
        scn.LSChooseCard(rkik);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(rkik, blaster));
        assertEquals(2, scn.GetLSForcePileCount()); //test2 (cost 0)
    }

    @Test
    public void R_kik_D_necMayAddToJawaWeaponTotalWeaponDestiny() {
        //test1: may choose optional action when firing jawa weapon, after destiny is drawn
        //test2: if optional action taken, may choose how much to add (1, 2, or 3)
        //test3: added amount applies to total weapon destiny
        var scn = GetScenario();

        var rkik = scn.GetLSCard("rkik");
        var ion = scn.GetLSCard("ion");

        var trooper = scn.GetDSFiller(1);
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, rkik, trooper);
        scn.AttachCardsTo(rkik, ion);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        scn.LSActivateForceCheat(1); //enough to fire ion gun
        scn.PrepareLSDestiny(0);

        scn.PassAllResponses();
        scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.LSUseCardAction(ion, "Fire");
        scn.LSChooseCard(trooper);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //Fire Jawa Ion Gun - Optional responses
        scn.LSPass();

        scn.DSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        scn.LSPass();

        scn.DSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.LSPass();

        scn.DSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        assertTrue(scn.LSCardActionAvailable(rkik, "Add")); //test1
        scn.LSUseCardAction(rkik, "Add");
        assertTrue(scn.LSDecisionAvailable("Choose amount")); //test2
        scn.LSDecided(1); //test2

        scn.LSPass();
        //ion gun should exclude trooper if total destiny (0 + 1) = defense value (1)

        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSBattlePhaseActions()); //test3: battle ended without force loss, ion gun must have excluded
    }

    @Test
    public void R_kik_D_necMayNotAddToNonJawaWeaponTotalWeaponDestiny() {
        //test1: may not choose optional action when firing non-jawa weapon (blaster)
        var scn = GetScenario();

        var rkik = scn.GetLSCard("rkik");
        var blaster = scn.GetLSCard("blaster");

        var trooper = scn.GetDSFiller(1);
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, rkik, trooper);
        scn.AttachCardsTo(rkik, blaster);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        scn.LSActivateForceCheat(1); //enough to fire ion gun
        scn.PrepareLSDestiny(0);

        scn.PassAllResponses();
        scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //Fire Blaster - Optional responses
        scn.LSPass();

        scn.DSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        scn.LSPass();

        scn.DSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.LSPass();

        scn.DSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        assertFalse(scn.LSCardActionAvailable(rkik, "Add")); //test1
    }

    @Test
    public void R_kik_D_necMayNotAddToTotalWeaponDestinyIfNotFiring() {
        //test1: may not choose optional action when another character fires a jawa weapon
        var scn = GetScenario();

        var rkik = scn.GetLSCard("rkik");
        var jawa = scn.GetLSCard("jawa");
        var ion = scn.GetLSCard("ion");

        var trooper = scn.GetDSFiller(1);
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, rkik, jawa, trooper);
        scn.AttachCardsTo(jawa, ion);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        scn.LSActivateForceCheat(1); //enough to fire ion gun
        scn.PrepareLSDestiny(0);

        scn.PassAllResponses();
        scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.LSUseCardAction(ion, "Fire");
        scn.LSChooseCard(trooper);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //Fire Jawa Ion Gun - Optional responses
        scn.LSPass();

        scn.DSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        scn.LSPass();

        scn.DSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.LSPass();

        scn.DSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        assertFalse(scn.LSCardActionAvailable(rkik, "Add")); //test1
    }

    @Test
    public void R_kik_D_necWinsDoubleAtDuneSeaSabacc() {
        //test1: wins double (picks two stake cards) after winning dune sea sabacc
        var scn = GetScenario();

        var rkik = scn.GetLSCard("rkik");
        var dune_sabacc = scn.GetLSCard("dune_sabacc");
        var ls_destiny4 = scn.GetLSCard("ls_destiny4");
        var ls_destiny5 = scn.GetLSCard("ls_destiny5");

        var ds_destiny1 = scn.GetDSCard("ds_destiny1");
        var ds_destiny2 = scn.GetDSCard("ds_destiny2");
        var ds_destiny3 = scn.GetDSCard("ds_destiny3");
        var tat_site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(tat_site, rkik);
        scn.MoveCardsToLSHand(dune_sabacc);

        scn.MoveCardsToTopOfDSReserveDeck(ds_destiny1,ds_destiny2,ds_destiny3);
        scn.MoveCardsToTopOfLSReserveDeck(ls_destiny4,ls_destiny5);

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSPass();
        assertTrue(scn.DSChoiceAvailable("yes")); //You have not activated Force. Do you want to Pass?
        scn.DSChoose("yes");

        assertTrue(scn.LSCardPlayAvailable(dune_sabacc));
        scn.LSPlayCard(dune_sabacc);

        assertTrue(scn.LSHasCardChoiceAvailable(rkik)); //character to play sabacc
        scn.LSChooseCard(rkik);

        scn.DSPass(); //Playing sabacc - Optional responses
        scn.LSPass();

        scn.DSPass(); //DRAW_CARD - Optional responses //LS card 1 draw
        scn.LSPass();

        scn.DSPass(); //DRAW_CARD - Optional responses //LS card 2 draw
        scn.LSPass();

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 1 draw
        scn.DSPass();

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 2 draw
        scn.DSPass();

        assertTrue(scn.DSChoiceAvailable("yes")); //Do you want to draw another sabacc card?
        scn.DSChoose("yes");

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 3 draw
        scn.DSPass();

        assertTrue(scn.LSChoiceAvailable("no")); //Do you want to draw another sabacc card?
        scn.LSChoose("no");

        assertTrue(scn.DSChoiceAvailable("no")); //Do you want to draw another sabacc card?
        scn.DSChoose("no");

        assertEquals(6,scn.GetDSSabaccTotal()); //3 + 2 + 1
        assertEquals(9,scn.GetLSSabaccTotal()); //5 + 4

        scn.DSPass(); //SABACC_TOTAL_CALCULATED - Optional responses
        scn.LSPass();

        scn.DSPass(); //SABACC_WINNER_DETERMINED - Optional responses
        scn.LSPass();

        //choose opponent's cards to be lost
        assertTrue(scn.LSHasCardChoiceAvailable(ds_destiny1));
        assertTrue(scn.LSHasCardChoiceAvailable(ds_destiny2));
        assertTrue(scn.LSHasCardChoiceAvailable(ds_destiny3));
        scn.LSChooseCards(ds_destiny1, ds_destiny3);

        scn.LSChooseCard(ds_destiny3); //Choose next card to lose
        scn.PassAllResponses();

        assertEquals(2, scn.GetDSLostPileCount()); //test1
    }

    @Test
    public void R_kik_D_necDoesNotWinDoubleIfNotPlayingSabacc() {
        //test1: if rkik on table but not playing, does not win double at dune sea sabacc
        var scn = GetScenario();

        var rkik = scn.GetLSCard("rkik");
        var jawa = scn.GetLSCard("jawa");
        var dune_sabacc = scn.GetLSCard("dune_sabacc");
        var ls_destiny4 = scn.GetLSCard("ls_destiny4");
        var ls_destiny5 = scn.GetLSCard("ls_destiny5");

        var ds_destiny1 = scn.GetDSCard("ds_destiny1");
        var ds_destiny2 = scn.GetDSCard("ds_destiny2");
        var ds_destiny3 = scn.GetDSCard("ds_destiny3");
        var tat_site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(tat_site, rkik, jawa);
        scn.MoveCardsToLSHand(dune_sabacc);

        scn.MoveCardsToTopOfDSReserveDeck(ds_destiny1,ds_destiny2,ds_destiny3);
        scn.MoveCardsToTopOfLSReserveDeck(ls_destiny4,ls_destiny5);

        assertTrue(scn.AwaitingDSActivatePhaseActions());
        scn.DSPass();
        assertTrue(scn.DSChoiceAvailable("yes")); //You have not activated Force. Do you want to Pass?
        scn.DSChoose("yes");

        assertTrue(scn.LSCardPlayAvailable(dune_sabacc));
        scn.LSPlayCard(dune_sabacc);

        assertTrue(scn.LSHasCardChoiceAvailable(rkik)); //character to play sabacc
        assertTrue(scn.LSHasCardChoiceAvailable(jawa));
        scn.LSChooseCard(jawa);

        scn.DSPass(); //Playing sabacc - Optional responses
        scn.LSPass();

        scn.DSPass(); //DRAW_CARD - Optional responses //LS card 1 draw
        scn.LSPass();

        scn.DSPass(); //DRAW_CARD - Optional responses //LS card 2 draw
        scn.LSPass();

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 1 draw
        scn.DSPass();

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 2 draw
        scn.DSPass();

        assertTrue(scn.DSChoiceAvailable("yes")); //Do you want to draw another sabacc card?
        scn.DSChoose("yes");

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 3 draw
        scn.DSPass();

        assertTrue(scn.LSChoiceAvailable("no")); //Do you want to draw another sabacc card?
        scn.LSChoose("no");

        assertTrue(scn.DSChoiceAvailable("no")); //Do you want to draw another sabacc card?
        scn.DSChoose("no");

        assertEquals(6,scn.GetDSSabaccTotal()); //3 + 2 + 1
        assertEquals(9,scn.GetLSSabaccTotal()); //5 + 4

        scn.DSPass(); //SABACC_TOTAL_CALCULATED - Optional responses
        scn.LSPass();

        scn.DSPass(); //SABACC_WINNER_DETERMINED - Optional responses
        scn.LSPass();

        //choose opponent's card to be lost
        assertTrue(scn.LSHasCardChoiceAvailable(ds_destiny1));
        assertTrue(scn.LSHasCardChoiceAvailable(ds_destiny2));
        assertTrue(scn.LSHasCardChoiceAvailable(ds_destiny3));
        scn.LSChooseCard(ds_destiny3);

        scn.PassAllResponses();
        assertEquals(1, scn.GetDSLostPileCount()); //test1
    }

    @Test
    public void R_kik_D_necDoesNotWinDoubleIfPlayingNonDuneSeaSabacc() {
        //deploy rycar on rkik to make him a smuggler (eligible for jp sabacc)
        //test1: does not win double after winning sabacc that is not dune sea sabacc
        var scn = GetScenario();

        var rkik = scn.GetLSCard("rkik");
        var jp_sabacc = scn.GetLSCard("jp_sabacc");
        var jp_ac = scn.GetLSCard("jp_ac");
        var rycar = scn.GetLSCard("rycar");
        var ls_destiny4 = scn.GetLSCard("ls_destiny4");
        var ls_destiny5 = scn.GetLSCard("ls_destiny5");

        var ds_destiny1 = scn.GetDSCard("ds_destiny1");
        var ds_destiny2 = scn.GetDSCard("ds_destiny2");
        var ds_destiny3 = scn.GetDSCard("ds_destiny3");

        scn.StartGame();

        scn.MoveLocationToTable(jp_ac);
        scn.MoveCardsToLocation(jp_ac, rkik);
        scn.MoveCardsToLSHand(jp_sabacc, rycar);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(rycar);
        scn.LSChooseCard(rkik);
        scn.PassAllResponses();
        scn.DSPass();

        scn.MoveCardsToTopOfDSReserveDeck(ds_destiny1,ds_destiny2,ds_destiny3);
        scn.MoveCardsToTopOfLSReserveDeck(ls_destiny4,ls_destiny5);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSCardPlayAvailable(jp_sabacc));
        scn.LSPlayCard(jp_sabacc);

        assertTrue(scn.LSHasCardChoiceAvailable(rkik)); //character to play sabacc
        scn.LSChooseCard(rkik);

        scn.DSPass(); //Playing sabacc - Optional responses
        scn.LSPass();

        scn.DSPass(); //DRAW_CARD - Optional responses //LS card 1 draw
        scn.LSPass();

        scn.DSPass(); //DRAW_CARD - Optional responses //LS card 2 draw
        scn.LSPass();

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 1 draw
        scn.DSPass();

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 2 draw
        scn.DSPass();

        assertTrue(scn.DSChoiceAvailable("yes")); //Do you want to draw another sabacc card?
        scn.DSChoose("yes");

        scn.LSPass(); //DRAW_CARD - Optional responses //DS card 3 draw
        scn.DSPass();

        assertTrue(scn.LSChoiceAvailable("no")); //Do you want to draw another sabacc card?
        scn.LSChoose("no");

        assertTrue(scn.DSChoiceAvailable("no")); //Do you want to draw another sabacc card?
        scn.DSChoose("no");

        assertEquals(6,scn.GetDSSabaccTotal()); //3 + 2 + 1
        assertEquals(9,scn.GetLSSabaccTotal()); //5 + 4

        scn.DSPass(); //SABACC_TOTAL_CALCULATED - Optional responses
        scn.LSPass();

        scn.DSPass(); //SABACC_WINNER_DETERMINED - Optional responses
        scn.LSPass();

        //choose opponent's card to be lost
        assertTrue(scn.LSHasCardChoiceAvailable(ds_destiny1));
        assertTrue(scn.LSHasCardChoiceAvailable(ds_destiny2));
        assertTrue(scn.LSHasCardChoiceAvailable(ds_destiny3));
        scn.LSChooseCard(ds_destiny3);

        scn.PassAllResponses();
        assertEquals(1, scn.GetDSLostPileCount()); //test1
    }

}
