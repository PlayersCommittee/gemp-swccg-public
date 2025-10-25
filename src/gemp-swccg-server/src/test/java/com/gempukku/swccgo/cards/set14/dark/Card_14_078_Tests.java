package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//mirror changes to Card_14_079_Tests (AI)

public class Card_14_078_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("luke", "9_024"); //LSJK
                    put("insight_shield","200_032"); //Insight defensive shield
				}},
				new HashMap<>()
				{{
                    put("sidious", "14_078"); //darth sidious
					put("vader", "9_113"); //lord vader
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
	public void DarthSidiousStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Darth Sidious
		 * Uniqueness: Unique
		 * Side: Dark
		 * Type: Character
		 * Subtype: Dark Jedi Master
		 * Destiny: 1
		 * Deploy: 6
		 * Power: 5
		 * Ability: 7
		 * Forfeit: 8
		 * Icons: Pilot, Theed Palace, Episode 1
		 * Persona: Sidious
		 * Game Text: While no other characters present, if opponent just lost a Jedi from table, may lose 1 Force
         *      to place that Jedi out of play. While on Coruscant, may use 1 Force to add one battle destiny
         *      in a battle your Neimoidian is in. Immune to attrition.
		 * Lore: Mysterious Sith Master who is manipulating the Trade Federation for his own nefarious ends.
         *      Shrouded in mystery, his identity and agenda remain unclear.
		 * Set: Theed Palace
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("sidious").getBlueprint();

		assertEquals("Darth Sidious", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
        assertFalse(card.hasAlternateImageSuffix());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(6, card.getDeployCost(), scn.epsilon);
		assertEquals(5, card.getPower(), scn.epsilon);
		assertEquals(7, card.getAbility(), scn.epsilon);
		assertEquals(8, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.DARK_JEDI_MASTER);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
			//null
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
			add(Persona.SIDIOUS);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.DARK_JEDI_MASTER);
			add(Icon.WARRIOR);
			add(Icon.THEED_PALACE);
			add(Icon.EPISODE_I);
		}});
		assertEquals(ExpansionSet.THEED_PALACE,card.getExpansionSet());
		assertEquals(Rarity.R,card.getRarity());
	}

	@Test
	public void DarthSidiousMayPlaceLostJediOutOfPlay() {
        //tests basic functionality of action1

		var scn = GetScenario();

		var luke = scn.GetLSCard("luke");

		var vader = scn.GetDSCard("vader");
        var sidious = scn.GetDSCard("sidious");

        var site1 = scn.GetDSStartingLocation();
        var site2 = scn.GetLSStartingLocation();

        scn.StartGame();

		scn.MoveCardsToLocation(site1, sidious);
        scn.MoveCardsToLocation(site2, vader, luke);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site2);
        scn.SkipToDamageSegment(false);

        assertTrue(scn.DSWonBattle());
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertTrue(scn.LSHasCardChoiceAvailable(luke)); //option to forfeit obi
        scn.LSChooseCard(luke);

        assertTrue(scn.DSCardActionAvailable(sidious,"Place")); //"Place <name> out of play"
        scn.DSUseCardAction(sidious);

        scn.PassAllResponses(); //(FORCE_LOSS_INITIATED, ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE)
        scn.DSChooseCard(scn.GetTopOfDSReserveDeck()); //lose 1 off the top
        scn.PassAllResponses(); //(ABOUT_TO_BE_PLACED_OUT_OF_PLAY_FROM_OFF_TABLE,PLACED_OUT_OF_PLAY_FROM_OFF_TABLE,FORFEITED_TO_LOST_PILE_FROM_TABLE

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertEquals(0,scn.GetLSLostPileCount()); //luke not in lost pile
        //add an assert that luke is actually in the out of play zone
        assertEquals(1,scn.GetDSLostPileCount()); //paid cost of losing 1 force
    }

    @Test
    public void DarthSidiousInsightPreventsPlaceLostSkywalkerOutOfPlay() {
        //this test probably belongs with Card200_032 - Your Insight Serves You Well (V) defensive shield
        //same setup as DarthSidiousMayPlaceLostJediOutOfPlay but with Insight on table

        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var insight_shield = scn.GetLSCard("insight_shield");

        var vader = scn.GetDSCard("vader");
        var sidious = scn.GetDSCard("sidious");

        var site1 = scn.GetDSStartingLocation();
        var site2 = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSSideOfTable(insight_shield);
        scn.MoveCardsToLocation(site1, sidious);
        scn.MoveCardsToLocation(site2, vader, luke);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site2);
        scn.SkipToDamageSegment(false);

        assertTrue(scn.DSWonBattle());
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertTrue(scn.LSHasCardChoiceAvailable(luke)); //option to forfeit obi
        scn.LSChooseCard(luke);

        //sidious action prevented by insight_shield
        assertFalse(scn.DSCardActionAvailable(sidious,"Place")); //"Place <name> out of play"
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertEquals(1,scn.GetLSLostPileCount()); //luke in lost pile
        assertEquals(0,scn.GetDSLostPileCount()); //no cost paid
    }
}
