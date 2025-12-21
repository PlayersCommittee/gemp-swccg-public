package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Card_7_023_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("joh", "7_023"); //joh yowza
                    put("endor_site", "8_071"); //chirpa's hut
					put("musician_ls", "6_028"); //max rebo
                    put("sense","1_109");
				}},
				new HashMap<>()
				{{
                    put("musician_ds", "7_188"); //lyn me
                    put("bane","6_095"); //bane malar
                    put("sando","13_085"); //sando aqua monster
                    put("bok","14_075"); //bok askol
                    put("setForStun","1_268");
                    put("lom","4_091"); //4-lom (protocol droid)
                    put("echuta","5_138"); //e chu ta (cancel game text)
                    put("assault","5_159"); //trooper assault
                    put("comeback","3_126"); //he hasn't come back yet
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
	public void JohYowzaStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Joh Yowza
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Character
		 * Subtype: Alien
		 * Destiny: 3
		 * Deploy: 2
		 * Power: 2
		 * Ability: 1
		 * Forfeit: 3
		 * Icons: Special Edition
         * Keywords: Musician, Thief
         * Species: Yuzzum
		 * Persona:
         * Game Text: Power +2 on Endor or when present with your musician. When opponent draws destiny, may 'jam'
         *      (place that card face down under Joh). Holds one 'jammed' card at a time. If Joh about to leave table,
         *      place 'jammed' card under Joh in owner's Used Pile
		 * Lore: Yuzzum musician and thief. Singer for The Max Rebo Band. Stage name given to him by Sy Snootles.
         *      Jabba likes his performance, even though the Hutt despises Yuzzum.
		 * Set: Special Edition
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("joh").getBlueprint();

		assertEquals("Joh Yowza", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(3, card.getDestiny(), scn.epsilon);
		assertEquals(2, card.getDeployCost(), scn.epsilon);
		assertEquals(2, card.getPower(), scn.epsilon);
		assertEquals(1, card.getAbility(), scn.epsilon);
		assertEquals(3, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.ALIEN);
		}});
        assertEquals(Species.YUZZUM,card.getSpecies());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.MUSICIAN);
            add(Keyword.THIEF);
            //null
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.ALIEN);
			add(Icon.SPECIAL_EDITION);
		}});
		assertEquals(ExpansionSet.SPECIAL_EDITION,card.getExpansionSet());
		assertEquals(Rarity.R,card.getRarity());
	}

    @Test
    public void JohYowzaPowerBonusWhenOnEndor() {
        //Test1: Joh has no power bonus when not on Endor
        //Test2: Joh has power +2 bonus when on Endor
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");
        var endor_site = scn.GetLSCard("endor_site");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveLocationToTable(endor_site);

        scn.MoveCardsToLocation(site,joh);

        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(2,scn.GetPower(joh)); //test1

        scn.MoveCardsToLocation(endor_site,joh);
        assertEquals(4,scn.GetPower(joh)); //test2
    }

    @Test
    public void JohYowzaPowerBonusWhenPresentWithMusician() {
        //Test1: Joh has no power bonus when present with opponent's musician
        //Test2: Joh has no power bonus when present with your non-musician
        //Test3: Joh has power +2 bonus when with your musician
        //Test4: Joh has power +2 bonus when with your musician and on Endor (no cumulative power bonus)
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");
        var musician_ls = scn.GetLSCard("musician_ls");
        var rebeltrooper = scn.GetLSFiller(1);
        var endor_site = scn.GetLSCard("endor_site");

        var musician_ds = scn.GetDSCard("musician_ds");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveLocationToTable(endor_site);

        scn.MoveCardsToLocation(site, joh, musician_ds,rebeltrooper);

        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(2,scn.GetPower(joh)); //test1, test2

        scn.MoveCardsToLocation(site,musician_ls);
        assertEquals(4,scn.GetPower(joh)); //test3

        scn.MoveCardsToLocation(endor_site,joh,musician_ls);
        assertEquals(4,scn.GetPower(joh)); //test4
    }

    @Test
    public void JohYowzaMayJamOpponentsJustDrawnDestiny() {
        //Test1: Joh may stack opponent's just drawn destiny as a 'jam' card
        //  use Bok to draw a (non-battle) destiny
        //Test2: 'Jam' card is stacked on Joh
        //Test3: 'Jam' card state is not active
        //Test4: 'Jam' card state is not inactive
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");

        var bok = scn.GetDSCard("bok");
        var jamDestiny = scn.GetDSFiller(1);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh, bok);
        scn.MoveCardsToDSHand(jamDestiny);

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(bok,"Draw destiny"));
        scn.DSUseCardAction(bok,"Draw destiny");

        scn.MoveCardsToTopOfDSReserveDeck(jamDestiny);

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        assertTrue(scn.LSDecisionAvailable("Just drew")); //Just drew (card) for destiny - Optional responses
        assertTrue(scn.LSCardActionAvailable(joh,"'Jam'"));
        scn.LSUseCardAction(joh,"'Jam'");

        scn.DSPass(); //DESTINY_DRAWN - Optional responses
        scn.LSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertEquals(0,scn.GetDSUsedPileCount());
        assertTrue(scn.IsStackedOn(joh,jamDestiny)); //test2
        assertTrue(jamDestiny.isJamCard());
        assertFalse(scn.IsCardActive(jamDestiny)); //test3
        assertFalse(jamDestiny.isStackedAsInactive()); //test4 - should be in a supporting state
    }

    @Test
    public void JohYowzaMayNotJamOpponentsSubstitutedDestiny() {
        //Test1: Joh may not stack an opponent's substituted destiny as a 'jam' card
        //  use Sando Aqua Monster to substitute a battle destiny and confirm Joh never has an optional action to 'jam'
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");

        var bok = scn.GetDSCard("bok");
        var sando = scn.GetDSCard("sando");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var jamDestiny = scn.GetDSFiller(3);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh, bok, trooper1, trooper2);
        scn.MoveCardsToDSHand(jamDestiny,sando);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(sando);
        scn.PassAllResponses();
        scn.LSPass();

        scn.PrepareDSDestiny(7);
        scn.DSUseCardAction(sando,"Draw");
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        scn.MoveCardsToTopOfDSReserveDeck(jamDestiny);

        assertFalse(scn.DSCardActionAvailable(sando));
        scn.DSPass(); //Choose weapons segment action to play or Pass
        scn.LSPass();

        scn.LSPass(); //BEFORE_BATTLE_DESTINY_DRAWS - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses

        //About to draw battle destiny - Optional responses
        assertTrue(scn.DSCardActionAvailable(sando,"Substitute"));
        scn.DSUseCardAction(sando,"Substitute");

        assertTrue(scn.LSDecisionAvailable("REMOVED_FROM_STACKED - Optional responses"));
        assertFalse(scn.LSCardActionAvailable(joh,"'Jam'"));
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("REMOVED_FROM_STACKED - Optional responses"));
        scn.DSPass();

        assertTrue(scn.LSDecisionAvailable("ABOUT_TO_DRAW_DESTINY_CARD - Optional responses"));
        assertFalse(scn.LSCardActionAvailable(joh,"'Jam'"));
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_DRAW_DESTINY_CARD - Optional responses"));
        scn.DSPass();

        //if Joh could stack a destiny, it would be here
        assertFalse(scn.LSDecisionAvailable("Just drew")); //Just drew (card) for destiny - Optional responses

        assertFalse(scn.LSCardActionAvailable(joh,"'Jam'"));
        assertTrue(scn.LSDecisionAvailable("DESTINY_DRAWN - Optional responses")); //test1: past window to jam
    }

    @Test
    public void JohYowzaMayNotJamOpponentsJustDrawnDestinyIfGameTextCanceled() {
        //Test1: Joh unable to stack opponent's just drawn destiny as a 'jam' card if his game text is canceled
        //  use e chu ta to cancel Joh's game text
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");

        var bok = scn.GetDSCard("bok");
        var lom = scn.GetDSCard("lom");
        var echuta = scn.GetDSCard("echuta");
        var jamDestiny = scn.GetDSFiller(1);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh, bok, lom);
        scn.MoveCardsToDSHand(jamDestiny,echuta);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(echuta));
        scn.DSUseCardAction(echuta);
        scn.DSChooseCard(lom);
        scn.DSChooseCard(joh);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(bok,"Draw destiny"));
        scn.DSUseCardAction(bok,"Draw destiny");

        scn.MoveCardsToTopOfDSReserveDeck(jamDestiny);

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        assertTrue(scn.LSDecisionAvailable("ABOUT_TO_DRAW_DESTINY_CARD - Optional responses"));
        assertFalse(scn.LSCardActionAvailable(joh,"'Jam'"));
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_DRAW_DESTINY_CARD - Optional responses"));
        scn.DSPass();

        //if Joh could stack a destiny, it would be here
        assertFalse(scn.LSDecisionAvailable("Just drew")); //Just drew (card) for destiny - Optional responses

        assertFalse(scn.LSCardActionAvailable(joh,"'Jam'"));
        assertTrue(scn.LSDecisionAvailable("DESTINY_DRAWN - Optional responses")); //test1: past window to jam
    }

    @Test
    public void JohYowzaRetainsJamCardIfGameTextCanceled() {
        //Test1: Joh keeps a stacked 'jam' card if his game text is canceled
        //  jam a destiny and then play e chu ta to cancel Joh's game text
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");

        var bok = scn.GetDSCard("bok");
        var lom = scn.GetDSCard("lom");
        var echuta = scn.GetDSCard("echuta");
        var jamDestiny = scn.GetDSFiller(1);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh, bok, lom);
        scn.MoveCardsToDSHand(jamDestiny,echuta);

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(bok,"Draw destiny"));
        scn.DSUseCardAction(bok,"Draw destiny");

        scn.MoveCardsToTopOfDSReserveDeck(jamDestiny);

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        assertTrue(scn.LSDecisionAvailable("Just drew")); //Just drew (card) for destiny - Optional responses
        assertTrue(scn.LSCardActionAvailable(joh,"'Jam'"));
        scn.LSUseCardAction(joh,"'Jam'");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsStackedOn(joh,jamDestiny));

        scn.LSPass();
        assertTrue(scn.DSCardPlayAvailable(echuta));
        scn.DSUseCardAction(echuta);
        scn.DSChooseCard(lom);
        scn.DSChooseCard(joh);

        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsStackedOn(joh,jamDestiny)); //test1
    }

    @Test
    public void JohYowzaRetainsJamCardWhenMissingAndFound() {
        //Test1: After stacking 1 'jam' card, if Joh goes missing (becomes inactive), the 'jam' card remains
        //Test2: After finding Joh (becomes active), the 'jam' card remains
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");
        var rebelTrooper = scn.GetLSFiller(1);

        var bok = scn.GetDSCard("bok");
        var jamDestiny = scn.GetDSFiller(1);
        var comeback = scn.GetDSCard("comeback");

        var site = scn.GetDSStartingLocation(); //exterior planet site to allow playing comeback

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh, bok);
        scn.MoveCardsToDSHand(jamDestiny,comeback);
        scn.MoveCardsToLSHand(rebelTrooper);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSUseCardAction(bok,"Draw destiny");

        scn.MoveCardsToTopOfDSReserveDeck(jamDestiny);

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        assertTrue(scn.LSDecisionAvailable("Just drew")); //Just drew (card) for destiny - Optional responses
        scn.LSUseCardAction(joh,"'Jam'");

        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsStackedOn(joh,jamDestiny));

        scn.SkipToLSTurn();

        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(comeback);
        scn.DSChooseCard(joh);
        scn.PassAllResponses();

        assertTrue(joh.isMissing());
        assertTrue(scn.IsStackedOn(joh,jamDestiny)); //test1
        assertTrue(jamDestiny.isJamCard());
        assertFalse(scn.IsCardActive(jamDestiny));
        assertFalse(jamDestiny.isStackedAsInactive());

        scn.MoveCardsToLocation(site,rebelTrooper);
        scn.SkipToLSTurn(Phase.CONTROL);

        scn.PrepareLSDestiny(7); //ensure joh will be found
        scn.LSUseCardAction(site,"Form search party");
        scn.LSChooseCard(rebelTrooper);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(joh.isMissing());

        assertTrue(scn.IsStackedOn(joh,jamDestiny)); //test2
        assertTrue(jamDestiny.isJamCard());
        assertFalse(scn.IsCardActive(jamDestiny));
        assertFalse(jamDestiny.isStackedAsInactive());
    }

    @Test
    public void JohYowzaMayNotJamMoreThan1Card() {
        //Test1: After stacking 1 'jam' card, Joh may not stack a second card
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");

        var bok = scn.GetDSCard("bok");
        var jamDestiny1 = scn.GetDSFiller(1);
        var jamDestiny2 = scn.GetDSFiller(2);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh, bok);
        scn.MoveCardsToDSHand(jamDestiny1,jamDestiny2);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSUseCardAction(bok,"Draw destiny");

        scn.MoveCardsToTopOfDSReserveDeck(jamDestiny1);

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        assertTrue(scn.LSDecisionAvailable("Just drew")); //Just drew (card) for destiny - Optional responses
        scn.LSUseCardAction(joh,"'Jam'");

        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsStackedOn(joh,jamDestiny1));

        scn.SkipToLSTurn();

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSUseCardAction(bok,"Draw destiny");

        scn.MoveCardsToTopOfDSReserveDeck(jamDestiny2);

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        //if Joh could stack a destiny, it would be here
        assertFalse(scn.LSDecisionAvailable("Just drew")); //Just drew (card) for destiny - Optional responses
        assertFalse(scn.LSCardActionAvailable(joh,"'Jam'")); //test1

        assertTrue(scn.IsStackedOn(joh,jamDestiny1));
    }

    @Test
    public void JohYowzaJamCardSentToUsedPileWhenJohLeavesTableLost() {
        //Test1: Joh leaving table (lost) causes a jam card to be sent to owner's Used Pile
        //  stack with Joh and then lose him to satisfy battle damage
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");

        var bok = scn.GetDSCard("bok");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var jamDestiny = scn.GetDSFiller(3);
        var setForStun = scn.GetDSCard("setForStun");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh, bok, trooper1, trooper2);
        scn.MoveCardsToDSHand(jamDestiny,setForStun);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSUseCardAction(bok,"Draw destiny");

        scn.MoveCardsToTopOfDSReserveDeck(jamDestiny);

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        //Just drew (card) for destiny - Optional responses
        scn.LSUseCardAction(joh,"'Jam'");
        scn.PassAllResponses();

        assertTrue(scn.IsStackedOn(joh,jamDestiny));
        scn.LSPass();

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        scn.SkipToDamageSegment(false);

        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(joh);

        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSBattlePhaseActions());

        assertSame(jamDestiny,scn.GetTopOfDSUsedPile()); //test1
        assertFalse(jamDestiny.isJamCard());
    }

    @Test
    public void JohYowzaJamCardSentToLostPileIfJohGameTextCanceledWhenJohLeavesTableLost() {
        //Test1: Joh leaving table (lost) with game text canceled causes jam card to go to lost pile
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");

        var bok = scn.GetDSCard("bok");
        var lom = scn.GetDSCard("lom");
        var echuta = scn.GetDSCard("echuta");
        var jamDestiny = scn.GetDSFiller(3);
        var setForStun = scn.GetDSCard("setForStun");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh, bok, lom);
        scn.MoveCardsToDSHand(jamDestiny, setForStun, echuta);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSUseCardAction(bok,"Draw destiny");

        scn.MoveCardsToTopOfDSReserveDeck(jamDestiny);

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        //Just drew (card) for destiny - Optional responses
        scn.LSUseCardAction(joh,"'Jam'");
        scn.PassAllResponses();

        assertTrue(scn.IsStackedOn(joh,jamDestiny));
        scn.LSPass();

        assertTrue(scn.DSCardPlayAvailable(echuta));
        scn.DSUseCardAction(echuta);
        scn.DSChooseCard(lom);
        scn.DSChooseCard(joh);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        scn.SkipToDamageSegment(false);

        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(joh);

        scn.DSPass(); //Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingLSBattlePhaseActions());

        assertNotSame(jamDestiny,scn.GetTopOfDSUsedPile());
        assertSame(jamDestiny,scn.GetTopOfDSLostPile()); //test1
        assertFalse(jamDestiny.isJamCard());
    }

    @Test
    public void JohYowzaJamCardSentToUsedPileWhenJohLeavesTableToHand() {
        //Test1: Joh leaving table causes a jam card to be sent to owner's Used Pile
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");

        var bok = scn.GetDSCard("bok");
        var setForStun = scn.GetDSCard("setForStun");
        var jamDestiny = scn.GetDSFiller(1);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh, bok);
        scn.MoveCardsToDSHand(jamDestiny,setForStun);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSUseCardAction(bok,"Draw destiny");

        scn.MoveCardsToTopOfDSReserveDeck(jamDestiny);

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        //Just drew (card) for destiny - Optional responses
        scn.LSUseCardAction(joh,"'Jam'");
        scn.PassAllResponses();

        assertTrue(scn.IsStackedOn(joh,jamDestiny));
        scn.LSPass();

        scn.PrepareDSDestiny(7);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardPlayAvailable(setForStun));
        scn.DSPlayCard(setForStun);
        scn.DSChooseCard(joh);

        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());

        assertSame(Zone.HAND,joh.getZone());
        assertNotSame(Zone.HAND,jamDestiny.getZone());
        assertSame(jamDestiny,scn.GetTopOfDSUsedPile()); //test1
        assertFalse(jamDestiny.isJamCard());
    }

    @Test
    public void JohYowzaJamCardSentToHandIfJohGameTextCanceledWhenJohLeavesTableToHand() {
        //Test1: Joh's game text for sending stacked 'jam' card to owner's used pile
        //  does not apply if his game text is canceled when leaving table
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");

        var bok = scn.GetDSCard("bok");
        var lom = scn.GetDSCard("lom");
        var echuta = scn.GetDSCard("echuta");
        var setForStun = scn.GetDSCard("setForStun");
        var jamDestiny = scn.GetDSFiller(1);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh, bok, lom);
        scn.MoveCardsToDSHand(jamDestiny,echuta,setForStun);

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(bok,"Draw destiny"));
        scn.DSUseCardAction(bok,"Draw destiny");

        scn.MoveCardsToTopOfDSReserveDeck(jamDestiny);

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        //Just drew (card) for destiny - Optional responses
        assertTrue(scn.LSCardActionAvailable(joh,"'Jam'"));
        scn.LSUseCardAction(joh,"'Jam'");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsStackedOn(joh,jamDestiny));
        scn.LSPass();

        assertTrue(scn.DSCardPlayAvailable(echuta));
        scn.DSUseCardAction(echuta);
        scn.DSChooseCard(lom);
        scn.DSChooseCard(joh);

        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsStackedOn(joh,jamDestiny));
        scn.LSPass();

        scn.PrepareDSDestiny(7);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardPlayAvailable(setForStun));
        scn.DSPlayCard(setForStun);
        scn.DSChooseCard(joh);

        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertSame(Zone.HAND,joh.getZone());
        assertSame(Zone.HAND,jamDestiny.getZone()); //test1 (returned to hand by setForStun)
        assertFalse(jamDestiny.isJamCard());
    }

    @Test
    public void JohYowzaMayNotJamOwnJustDrawnDestiny() {
        //Test1: Joh unable to 'jam' own destiny draw for Sense
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");
        var sense = scn.GetLSCard("sense");
        var jamDestiny = scn.GetLSFiller(1);

        var setForStun = scn.GetDSCard("setForStun");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh);
        scn.MoveCardsToLSHand(jamDestiny,sense);
        scn.MoveCardsToDSHand(setForStun);

        scn.MoveCardsToTopOfLSReserveDeck(jamDestiny);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(setForStun));
        scn.DSPlayCard(setForStun);
        scn.DSChooseCard(joh);

        scn.LSPass(); //Use 2 Force - Optional responses
        scn.DSPass();

            //Playing Set For Stun - Optional responses
        scn.LSPlayCard(sense);
        scn.LSChooseCard(joh);

        scn.DSPass(); //Playing Sense - Optional responses
        scn.LSPass();

        scn.DSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_DRAW_DESTINY_CARD - Optional responses"));
        scn.DSPass();
        assertTrue(scn.LSDecisionAvailable("ABOUT_TO_DRAW_DESTINY_CARD - Optional responses"));
        assertFalse(scn.LSCardActionAvailable(joh,"'Jam'"));
        scn.LSPass();

        assertTrue(scn.DSDecisionAvailable("DESTINY_DRAWN - Optional responses"));
        scn.DSPass();

        //if Joh could stack a destiny, it would be here
        assertFalse(scn.LSDecisionAvailable("Just drew")); //Just drew (card) for destiny - Optional responses
        assertFalse(scn.LSCardActionAvailable(joh,"'Jam'"));
        assertTrue(scn.LSDecisionAvailable("DESTINY_DRAWN - Optional responses")); //test1: past window to jam
    }

    @Test
    public void JohYowzaMindscanAllowsJamOwnJustDrawnDestiny() {
        //Test1: Bane Malar mindscanning Joh (adding Joh's game text to Bane's) allows DS an optional
        // response action on Bane Malar to stack LS just drawn destiny on Joh - 'self-jam'
        //Test2: Joh leaving table with 'self-jam' stacked card causes that card to go to LS used pile
        var scn = GetScenario();

        var joh = scn.GetLSCard("joh");
        var sense = scn.GetLSCard("sense");
        var jamDestiny = scn.GetLSFiller(1);

        var bane = scn.GetDSCard("bane");
        var trooper = scn.GetDSFiller(1);
        var assault = scn.GetDSCard("assault");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, joh, bane, trooper);
        scn.MoveCardsToLSHand(jamDestiny,sense);
        scn.MoveCardsToDSHand(assault);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.DSUseCardAction(bane,"Mindscan");
        scn.DSChooseCard(joh);

        scn.LSPass(); //Use 1 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //Mindscan a character - Optional responses
        scn.DSPass();

        scn.LSPass(); //BATTLE_INITIATED - Optional responses

        assertTrue(scn.DSCardPlayAvailable(assault));
        scn.DSPlayCard(assault);

        //Playing Trooper Assault - Optional responses
        scn.MoveCardsToTopOfLSReserveDeck(jamDestiny); //to be drawn for Sense destiny
        scn.LSPlayCard(sense);
        scn.LSChooseCard(joh);

        scn.DSPass(); //Playing Sense - Optional responses
        scn.LSPass();

        scn.DSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        scn.DSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.LSPass();

        //Just drew (card) for destiny - Optional responses
        assertTrue(scn.DSDecisionAvailable("Just drew")); // would be DESTINY_DRAWN if no response available
        assertFalse(scn.DSCardActionAvailable(joh));
        assertTrue(scn.DSCardActionAvailable(bane));
        scn.DSUseCardAction(bane,"'Jam'");

        assertTrue(scn.IsStackedOn(joh,jamDestiny)); //test1
        assertTrue(jamDestiny.isJamCard());

        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());

        scn.SkipToDamageSegment(false);
        scn.LSPayBattleDamageFromCardInPlay(joh);

        assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertFalse(jamDestiny.isJamCard());
        assertSame(jamDestiny,scn.GetTopOfLSUsedPile()); //test2

    }

    //tests to add:
    //no conflict between stacked hatred card and stacked jam card

    //more bane malar mindscan scenarios:
    //if mindscans joh and then joh is excluded, bane cannot stack
    //if mindscans joh, joh (or bane) jam a card on joh, then bane leaves table - confirm joh keeps jam card
}
