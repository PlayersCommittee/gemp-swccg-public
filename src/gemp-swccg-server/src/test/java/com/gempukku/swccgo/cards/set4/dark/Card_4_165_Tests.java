package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class Card_4_165_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("ywing","1_147");
				}},
				new HashMap<>()
				{{
                    put("bay", "4_165"); //Star Destroyer: Launch Bay
                    put("vcsd", "2_155"); //Victory-Class Star Destroyer
                    put("tiescout","1_305"); //tie scout
                    put("zimh", "110_012"); //zuckuss in mist hunter (non-tie starfighter)
                }},
				10,
				10,
				StartingSetup.DefaultLSSpaceSystem,
				StartingSetup.DefaultDSSpaceSystem,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}


    @Test
    public void StarDestroyerLaunchBayStatsAndKeywordsAreCorrect() {
        /**
         * Title: Star Destroyer: Launch Bay
         * Uniqueness: Restricted 3
         * Side: Dark
         * Type: Location
         * Subtype: Site
         * Destiny: 0
         * Icons: Dagobah, Interior, Exterior, Starship Site, Mobile, Scomp Link
         * Game Text: Light: Starships captured by Star Destroyer go here and may be Besieged. Immune to Revolution.
         *          Dark: Your TIEs deploy -2 here. You may shuttle, transfer, embark and disembark here for free.
         * Light Force Icons: 0
         * Dark Force Icons: 1
         * Set: Dagobah
         * Rarity: C
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("bay").getBlueprint();

        assertEquals("Star Destroyer: Launch Bay", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.INTERIOR_SITE);
            add(Icon.EXTERIOR_SITE);
            add(Icon.STARSHIP_SITE);
            add(Icon.MOBILE);
            add(Icon.SCOMP_LINK);
            add(Icon.DARK_FORCE);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        assertEquals(ExpansionSet.DAGOBAH,card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

	@Test
	public void StarDestroyerLaunchBayLSCanLandTest() {
        //test1: LS can land starfighter from a system (where the related star destroyer is) to the launch bay
        //test2: starfighter lands at the bay
        //test3: landing was free (treated as if from a docking bay)
        //test4: regular move was used (unable to make a regular move to take off, this turn)
        var scn = GetScenario();

		var ywing = scn.GetLSCard("ywing");

        var bay = scn.GetDSCard("bay");
        var vcsd = scn.GetDSCard("vcsd");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(bay);

        scn.MoveCardsToLocation(system, vcsd, ywing);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(bay);
        //scn.DSChooseCard(vcsd); //only card is auto-selected
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.MOVE);
        assertEquals(0, scn.GetLSUsedPileCount());
        assertTrue(scn.LSCardActionAvailable(ywing,"Land")); //test1
        scn.LSUseCardAction(ywing,"Land");
        scn.LSChooseCard(bay);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.CardsAtLocation(bay,ywing)); //test2
        assertEquals(0, scn.GetLSUsedPileCount()); //test3
        scn.DSPass();

        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertFalse(scn.LSCardActionAvailable(ywing)); //test4
    }

    @Test
    public void StarDestroyerLaunchBayLSCanTakeOffTest() {
        //test1: LS can take a starfighter off from launch bay to a system (where the related star destroyer is)
        //test2: starfighter moves to system of the related star destroyer
        //test3: take off was free (treated as if from a docking bay)
        //test4: regular move was used (unable to make a regular move to land or use hyperspeed this turn)
        var scn = GetScenario();

        var ywing = scn.GetLSCard("ywing");

        var bay = scn.GetDSCard("bay");
        var vcsd = scn.GetDSCard("vcsd");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(bay);
        scn.MoveCardsToLSHand(ywing);

        scn.MoveCardsToLocation(system, vcsd);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(bay);
        //scn.DSChooseCard(vcsd); //only card is auto-selected
        scn.PassAllResponses();

        scn.MoveCardsToLocation(bay, ywing);

        scn.SkipToLSTurn(Phase.MOVE);
        assertEquals(0, scn.GetLSUsedPileCount());
        assertTrue(scn.LSCardActionAvailable(ywing,"Take off")); //test1
        scn.LSUseCardAction(ywing,"Take off");
        scn.LSChooseCard(system);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.CardsAtLocation(system,ywing)); //test2
        assertEquals(0, scn.GetLSUsedPileCount()); //test3
        scn.DSPass();

        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertFalse(scn.LSCardActionAvailable(ywing)); //test4
    }

    @Test @Ignore
    public void StarDestroyerLaunchBayDSCanLandTieTest() {
        //test1: DS can land (embark) Tie starfighter from a system (where the related star destroyer is) to the launch bay
        //test2: Tie lands (embarks) at the bay
        //test3: movement was free
        //test4: (FAILS) unlimited move (able to take another regular move action, this turn)
        var scn = GetScenario();

        var bay = scn.GetDSCard("bay");
        var vcsd = scn.GetDSCard("vcsd");
        var tiescout = scn.GetDSCard("tiescout");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(bay);

        scn.MoveCardsToLocation(system, vcsd, tiescout);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(bay);
        //scn.DSChooseCard(vcsd); //only card is auto-selected
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.MOVE);
        assertEquals(0, scn.GetDSUsedPileCount());
        assertTrue(scn.DSCardActionAvailable(tiescout,"Land")); //test1
        scn.DSUseCardAction(tiescout,"Land");
        scn.DSChooseCard(bay);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertTrue(scn.CardsAtLocation(bay, tiescout)); //test2
        assertEquals(0, scn.GetDSUsedPileCount()); //test3
        scn.LSPass();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.DSCardActionAvailable(tiescout, "Take off"));
        scn.DSUseCardAction(tiescout,"Take off");
        scn.DSChooseCard(system);
        scn.PassAllResponses();
        scn.LSPass();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        ///FAILS HERE (regular action used up because getLandAction uses 'false' for asUnlimitedMove?)
        assertTrue(scn.DSCardActionAvailable(tiescout, "hyperspeed")); //test4
    }

    @Test @Ignore
    public void StarDestroyerLaunchBayDSCanLandNonTieStarfighterTest() {
        //test1: DS can land (embark) non-Tie starfighter from a system (where the related star destroyer is) to the launch bay
        //test2: starfighter lands (embarks) at the bay
        //test3: movement was free
        //test4: (FAILS) unlimited move (able to take another regular move action, this turn)
        var scn = GetScenario();

        var bay = scn.GetDSCard("bay");
        var vcsd = scn.GetDSCard("vcsd");
        var zimh = scn.GetDSCard("zimh");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(bay);

        scn.MoveCardsToLocation(system, vcsd, zimh);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(bay);
        //scn.DSChooseCard(vcsd); //only card is auto-selected
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.MOVE);
        assertEquals(0, scn.GetDSUsedPileCount());
        assertTrue(scn.DSCardActionAvailable(zimh,"Land")); //test1
        scn.DSUseCardAction(zimh,"Land");
        scn.DSChooseCard(bay);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertTrue(scn.CardsAtLocation(bay, zimh)); //test2
        assertEquals(0, scn.GetDSUsedPileCount()); //test3
        scn.LSPass();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.DSCardActionAvailable(zimh, "Take off"));
        scn.DSUseCardAction(zimh,"Take off");
        scn.DSChooseCard(system);
        scn.PassAllResponses();
        scn.LSPass();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        ///FAILS HERE (regular action used up because getLandAction uses 'false' for asUnlimitedMove?)
        assertTrue(scn.DSCardActionAvailable(zimh, "hyperspeed")); //test4
    }

    @Test
    public void StarDestroyerLaunchBayDSCanTakeOffTest() {
        //test1: DS can take starfighter off (disembark) from launch bay to a system (where the related star destroyer is)
        //test2: starfighter takes off (disembarks) to system
        //test3: movement was free
        //test4: unlimited move was used (able to take another regular move action, this turn)
        var scn = GetScenario();

        var bay = scn.GetDSCard("bay");
        var vcsd = scn.GetDSCard("vcsd");
        var tiescout = scn.GetDSCard("tiescout");

        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSHand(bay, tiescout);

        scn.MoveCardsToLocation(system, vcsd);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(bay);
        //scn.DSChooseCard(vcsd); //only card is auto-selected
        scn.PassAllResponses();

        scn.MoveCardsToLocation(bay, tiescout);

        scn.SkipToPhase(Phase.MOVE);
        assertEquals(0, scn.GetDSUsedPileCount());
        assertTrue(scn.DSCardActionAvailable(tiescout,"Take off")); //test1
        scn.DSUseCardAction(tiescout,"Take off");
        scn.DSChooseCard(system);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertTrue(scn.CardsAtLocation(system, tiescout)); //test2
        assertEquals(0, scn.GetDSUsedPileCount()); //test3
        scn.LSPass();

        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.DSCardActionAvailable(tiescout, "hyperspeed")); //test4
    }

}
