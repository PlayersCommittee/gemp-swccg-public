package com.gempukku.swccgo.cards.set4.dark;

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
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class Card_4_159_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("tat","1_127");
                    put("kessel","1_126");
                    put("corvette","1_140"); //Corellian Corvette
                    put("ywing","1_147");
				}},
				new HashMap<>()
				{{
                    put("comm", "4_159"); //Executor: Comm Station
                    put("executor", "4_167"); //Executor
                    put("vcsd", "2_155"); //Victory-Class Star Destroyer
                    put("tarkin","1_179"); //Grand Moff Tarkin
				}},
				40,
				40,
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
    public void ExecutorCommStationStatsAndKeywordsAreCorrect() {
        /**
         * Title: Executor: Comm Station
         * Uniqueness: Unique
         * Side: Dark
         * Type: Location
         * Subtype: Site
         * Destiny: 0
         * Icons: Dagobah, Interior, Starship Site, Mobile, Scomp Link
         * Game Text: Light: If you control, your starships may 'react' from same system as Executor.
         *          Dark: If you occupy with Tarkin, Piett or any admiral, your starships may 'react' to same system.
         * Light Force Icons: 0
         * Dark Force Icons: 1
         * Set: Dagobah
         * Rarity: U
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("comm").getBlueprint();

        assertEquals("Executor: Comm Station", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.INTERIOR_SITE);
            add(Icon.STARSHIP_SITE);
            add(Icon.MOBILE);
            add(Icon.SCOMP_LINK);
            add(Icon.DARK_FORCE);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        assertEquals(ExpansionSet.DAGOBAH,card.getExpansionSet());
        assertEquals(Rarity.U, card.getRarity());
    }

	@Test
	public void ExecutorCommStationDSCanMoveAsReactToExecutorsSystemTest() {
        //test1: Can use DS text to have starship move as react to Executor's system
        //test2: Can choose a starship in range to react
        //test3: Selected starship completed movement
        //test4: Movement was not free (regular move cost)
        var scn = GetScenario();

		var corvette = scn.GetLSCard("corvette");
        var tat = scn.GetLSCard("tat");
        var kessel = scn.GetLSCard("kessel");

        var comm = scn.GetDSCard("comm");
        var executor = scn.GetDSCard("executor");
        var tarkin = scn.GetDSCard("tarkin");
        var vcsd = scn.GetDSCard("vcsd");

        scn.StartGame();

        scn.MoveLocationToTable(tat);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(comm);

        scn.MoveCardsToLocation(comm, tarkin);
        scn.MoveCardsToLocation(kessel, executor, corvette);
        scn.MoveCardsToLocation(tat, vcsd);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(kessel);
        assertTrue(scn.DSCardActionAvailable(comm, "Move starship")); //test1
        scn.DSUseCardAction(comm, "Move starship");
        assertTrue(scn.DSHasCardChoiceAvailable(vcsd)); //test2
        scn.DSChooseCard(vcsd);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(kessel, vcsd)); //test3
        assertEquals(1, scn.GetDSUsedPileCount()); //test4
    }

    @Test
    public void ExecutorCommStationDSCanDeployAsReactToExecutorsSystemTest() {
        //test1: Can use DS text to have starship deploy as react to Executor's system
        //test2: Can choose a starship in hand
        //test3: Selected starship completed deployment
        //test4: Deployment was not free (regular deploy cost)
        var scn = GetScenario();

        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");

        var comm = scn.GetDSCard("comm");
        var executor = scn.GetDSCard("executor");
        var tarkin = scn.GetDSCard("tarkin");
        var vcsd = scn.GetDSCard("vcsd");

        scn.StartGame();

        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(comm);

        scn.MoveCardsToLocation(comm, tarkin);
        scn.MoveCardsToLocation(kessel, executor, corvette);
        scn.MoveCardsToDSHand(vcsd);

        scn.DSActivateForceCheat(3);

        scn.SkipToLSTurn(Phase.BATTLE);

        assertEquals(6 ,scn.GetDSForcePileCount()); //enough to deploy vcsd

        scn.LSInitiateBattle(kessel);
        assertTrue(scn.DSCardActionAvailable(comm, "Deploy starship")); //test1
        scn.DSUseCardAction(comm, "Deploy starship");
            //only one choice available - automatically picked
        //assertTrue(scn.DSHasCardChoiceAvailable(vcsd)); //test2
        //scn.DSChooseCard(vcsd);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(kessel, vcsd)); //test3
        assertEquals(6, scn.GetDSUsedPileCount()); //test4
    }

    @Test
    public void ExecutorCommStationLSCanMoveAsReactToForceDrainFromExecutorsSystemTest() {
        //test1: Can use LS text to have starship move as react from Executor's system to a force drain
        //test2: Can choose a starship in range to react
        //test3: Selected starship completed movement
        //test4: Movement was not free (regular move cost)
        var scn = GetScenario();

        var corvette = scn.GetLSCard("corvette");
        var tat = scn.GetLSCard("tat");
        var kessel = scn.GetLSCard("kessel");
        var trooper = scn.GetLSFiller(1);

        var comm = scn.GetDSCard("comm");
        var executor = scn.GetDSCard("executor");
        var vcsd = scn.GetDSCard("vcsd");

        scn.StartGame();

        scn.MoveLocationToTable(tat);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(comm);

        scn.MoveCardsToLocation(comm, trooper);
        scn.MoveCardsToLocation(kessel, executor, corvette);
        scn.MoveCardsToLocation(tat, vcsd);

        scn.LSActivateForceCheat(1); //to pay for react movement

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSForceDrainAt(tat);
        assertTrue(scn.LSCardActionAvailable(comm, "Move starship")); //test1
        scn.LSUseCardAction(comm, "Move starship");
        assertTrue(scn.LSHasCardChoiceAvailable(corvette)); //test2
        scn.LSChooseCard(corvette);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(tat, corvette)); //test3
        assertEquals(1, scn.GetLSUsedPileCount()); //test4
    }

    @Test
    public void ExecutorCommStationLSCanMoveAsReactToBattleFromExecutorsSystemTest() {
        //test1: Can use LS text to have starship move as react from Executor's system to a battle
        //test2: Can choose a starship in range to react
        //test3: Selected starship completed movement
        //test4: Movement was not free (regular move cost)
        var scn = GetScenario();

        var corvette = scn.GetLSCard("corvette");
        var ywing = scn.GetLSCard("ywing");
        var tat = scn.GetLSCard("tat");
        var kessel = scn.GetLSCard("kessel");
        var trooper = scn.GetLSFiller(1);

        var comm = scn.GetDSCard("comm");
        var executor = scn.GetDSCard("executor");
        var vcsd = scn.GetDSCard("vcsd");

        scn.StartGame();

        scn.MoveLocationToTable(tat);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(comm);

        scn.MoveCardsToLocation(comm, trooper);
        scn.MoveCardsToLocation(kessel, executor, corvette);
        scn.MoveCardsToLocation(tat, vcsd, ywing);

        scn.LSActivateForceCheat(1); //to pay for react movement

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(tat);
        assertTrue(scn.LSCardActionAvailable(comm, "Move starship")); //test1
        scn.LSUseCardAction(comm, "Move starship");
        assertTrue(scn.LSHasCardChoiceAvailable(corvette)); //test2
        scn.LSChooseCard(corvette);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(tat, corvette)); //test3
        assertEquals(1, scn.GetLSUsedPileCount()); //test4
    }

    @Test
    public void ExecutorCommStationLSCannotMoveAwayAsReactFromBattleAtExecutorsSystemTest() {
        //test1: Cannot use LS text to have starship 'move away' as react from Executor's system
        var scn = GetScenario();

        var corvette = scn.GetLSCard("corvette");
        var ywing = scn.GetLSCard("ywing");
        var tat = scn.GetLSCard("tat");
        var kessel = scn.GetLSCard("kessel");
        var trooper = scn.GetLSFiller(1);

        var comm = scn.GetDSCard("comm");
        var executor = scn.GetDSCard("executor");
        var vcsd = scn.GetDSCard("vcsd");

        scn.StartGame();

        scn.MoveLocationToTable(tat);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(comm);

        scn.MoveCardsToLocation(comm, trooper);
        scn.MoveCardsToLocation(kessel, executor, corvette);
        scn.MoveCardsToLocation(tat, vcsd, ywing);

        scn.LSActivateForceCheat(1); //to pay for react movement

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(kessel);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions()); //past opportunity to react
    }
}
