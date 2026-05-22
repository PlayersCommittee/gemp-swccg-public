package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_5_060_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("pirates","5_060"); //Old Pirates
                    put("han","1_011");
                    put("ls_lando","5_005");
                }},
				new HashMap<>()
				{{
                    put("ds_lando", "5_099");
                    put("barrier","1_249");
                    //put("swindler","5_137"); //Double-Crossing, No-Good Swindler (not implemented yet)
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
	public void OldPiratesStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Old Pirates
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Interrupt
         * Subtype: Lost
		 * Destiny: 5
		 * Icons: Cloud City
		 * Game Text: If a battle was just initiated involving Han and any Lando, the eventual loser of the battle
         *      may not lose cards from Life Force to satisfy battle damage while that player has any cards in hand.
         *      OR Cancel Double-Crossing, No-Good Swindler.
		 * Lore: 'How you doing, you old pirate? So good to see you.'
		 * Set: Cloud City
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("pirates").getBlueprint();

		assertEquals("Old Pirates", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(5, card.getDestiny(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.CLOUD_CITY);
			add(Icon.INTERRUPT);
		}});
		assertEquals(ExpansionSet.CLOUD_CITY,card.getExpansionSet());
		assertEquals(Rarity.R,card.getRarity());
	}

    @Test
    public void OldPiratesCanPlayIfBattleInitiatedWithHanAndLightLando() {
        //playable when LS initiates battle with LS Han, LS Lando in battle
        var scn = GetScenario();

        var pirates = scn.GetLSCard("pirates");
        var han = scn.GetLSCard("han");
        var ls_lando = scn.GetLSCard("ls_lando");

        var trooper = scn.GetDSFiller(1);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, han, ls_lando, trooper);

        scn.MoveCardsToLSHand(pirates);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        assertTrue(scn.LSCardPlayAvailable(pirates));
    }

    @Test
	public void OldPiratesCanPlayIfBattleInitiatedWithHanAndDarkLando() {
        //playable when DS initiates battle with LS Han, DS Lando in battle
		var scn = GetScenario();

        var pirates = scn.GetLSCard("pirates");
        var han = scn.GetLSCard("han");

        var ds_lando = scn.GetDSCard("ds_lando");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, han, ds_lando);

        scn.MoveCardsToLSHand(pirates);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        assertTrue(scn.LSCardPlayAvailable(pirates));
    }

    @Test
    public void OldPiratesCannotPlayIfBattleInitiatedWithHanAndExcludedLando() {
        //not playable when LS initiates battle with LS Han and excluded (barriered) LS Lando
        var scn = GetScenario();

        var pirates = scn.GetLSCard("pirates");
        var han = scn.GetLSCard("han");
        var ls_lando = scn.GetLSCard("ls_lando");

        var barrier = scn.GetDSCard("barrier");
        var trooper = scn.GetDSFiller(1);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, han, trooper);

        scn.MoveCardsToLSHand(pirates, ls_lando);
        scn.MoveCardsToDSHand(barrier);

        scn.LSActivateForceCheat(1); //enough to deploy + initiate

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(ls_lando);
        scn.LSChooseCard(site);

        scn.DSPass();
        scn.LSPass();

        scn.DSPlayCard(barrier);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        assertFalse(scn.LSCardPlayAvailable(pirates));
    }

    @Test
    public void OldPiratesPreventsLosingCardsFromLifeForceUntilHandIsEmpty() {
        //after Old Pirates is played and DS is the battle loser:
        //test1: DS can forfeit from site
        //test2: DS can lose card from hand
        //test3: DS cannot lose card from Reserve Deck (due to card in hand)
        //test4: DS cannot lose card from Force Pile (due to card in hand)
        //test5: DS cannot lose card from Used Pile (due to card in hand)
        // (DS loses all cards in hand)
        //test6: DS can lose card from Reserve Deck
        //test7: DS can lose card from Force Pile
        //test8: DS can lose card from Used Pile

        var scn = GetScenario();

        var pirates = scn.GetLSCard("pirates");
        var han = scn.GetLSCard("han");
        var ls_lando = scn.GetLSCard("ls_lando");

        var trooper = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, han, ls_lando, trooper);

        scn.MoveCardsToLSHand(pirates);
        scn.MoveCardsToDSHand(trooper2);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.LSPlayCard(pirates);
        scn.PassAllResponses();

        scn.SkipToDamageSegment();
        assertTrue(scn.AwaitingDSBattleDamagePayment());
        assertTrue(scn.DSHasCardChoiceAvailable(trooper)); //test1
        assertTrue(scn.DSHasCardChoiceAvailable(trooper2)); //test2
        assertTrue(scn.GetDSReserveDeckCount() >= 1);
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetTopOfDSReserveDeck())); //test3
        assertTrue(scn.GetDSForcePileCount() >= 1);
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetTopOfDSForcePile())); //test4
        assertTrue(scn.GetDSUsedPileCount() >= 1);
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetTopOfDSUsedPile())); //test5

        scn.DSChooseCard(trooper2);
        scn.PassAllResponses();
        assertEquals(0, scn.GetDSHandCount());
        assertTrue(scn.DSHasCardChoiceAvailable(trooper));
        assertFalse(scn.DSHasCardChoiceAvailable(trooper2)); //(already lost)
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetTopOfDSReserveDeck())); //test6
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetTopOfDSForcePile())); //test7
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetTopOfDSUsedPile())); //test8
    }

    //add test for cancelling swindler, when added

}
