package com.gempukku.swccgo.cards.set8.light;

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

public class Card_8_080_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("yt1300","8_080"); //YT-1300 Transport
                    put("cannon", "3_080"); //surface defense cannon
				}},
				new HashMap<>()
				{{
                    put("tat_db","1_291");
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
	public void YT1300TransportStatsAndKeywordsAreCorrect() {
		/**
		 * Title: YT-1300 Transport
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Starship
		 * Subtype: Starfighter
		 * Destiny: 3
         * Deploy: 3
         * Power: 2
         * Maneuver: 3
         * Hyperspeed: 5
         * Forfeit: 5
		 * Icons: Endor, Starship, Scomp Link, Nav Comp, Pilot, Independent
		 * Game Text: May add 1 pilot, 2 passengers and 1 vehicle. Has ship-docking capability. Permanent pilot
         *      provides ability of 1. Quad Laser Cannon and Surface Defense Cannon may deploy (and fire free) aboard.
		 * Lore: Reliable and durable. Widely used freighter made by Corellian Engineering Corporation. Sales have
         *      dramatically increased in proportion to the fame of Han Solo's ship.
		 * Set: Endor
		 * Rarity: C
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("yt1300").getBlueprint();

		assertEquals("YT-1300 Transport", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getManeuver(), scn.epsilon);
        assertEquals(5, card.getHyperspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.STARSHIP);
		}});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SHIP_DOCKING_CAPABILITY);
            add(Keyword.TRANSPORT_SHIP);
		}});
        assertEquals(1,card.getPilotCapacity());
        assertEquals(2,card.getPassengerCapacity());
        assertEquals(1,card.getVehicleCapacity());
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.ENDOR);
			add(Icon.STARSHIP);
            add(Icon.SCOMP_LINK);
            add(Icon.INDEPENDENT);
            add(Icon.PILOT);
            add(Icon.NAV_COMPUTER);
		}});
		assertEquals(ExpansionSet.ENDOR,card.getExpansionSet());
		assertEquals(Rarity.C,card.getRarity());
	}

	@Test
	public void YT1300TransportSDCDeployCost() {
        //Tets1: surface defense cannon deploys free
		var scn = GetScenario();

        var cannon = scn.GetLSCard("cannon");
        var yt1300 = scn.GetLSCard("yt1300");

        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

		scn.MoveCardsToLocation(tat_db, yt1300);

        scn.MoveCardsToLSHand(cannon);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(4,scn.GetLSForcePileCount());
        scn.MoveCardsToHand(scn.GetTopOfLSForcePile());
        scn.MoveCardsToHand(scn.GetTopOfLSForcePile());
        scn.MoveCardsToHand(scn.GetTopOfLSForcePile());
        scn.MoveCardsToHand(scn.GetTopOfLSForcePile());
        assertEquals(0,scn.GetLSForcePileCount()); //should be free

        assertTrue(scn.LSDeployAvailable(cannon)); //Test1: free
        scn.LSDeployCard(cannon);
        assertTrue(scn.LSHasCardChoiceAvailable(yt1300));
        scn.LSChooseCard(yt1300);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(yt1300,cannon));
    }

    //shows fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/667
    @Test
    public void YT1300TransportSDCFiringCost() {
        //Test1: fires for free YT-1300
        var scn = GetScenario();

        var cannon = scn.GetLSCard("cannon");
        var yt1300 = scn.GetLSCard("yt1300");

        var tat_db = scn.GetDSCard("tat_db");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, trooper, yt1300);
        scn.AttachCardsTo(yt1300,cannon);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertEquals(4,scn.GetLSForcePileCount());
        scn.MoveCardsToHand(scn.GetTopOfLSForcePile());
        scn.MoveCardsToHand(scn.GetTopOfLSForcePile());
        scn.MoveCardsToHand(scn.GetTopOfLSForcePile());
        assertEquals(1,scn.GetLSForcePileCount()); //enough to battle
        assertTrue(scn.LSCanInitiateBattle());
        scn.LSInitiateBattle(tat_db);
        assertEquals(1,scn.GetLSUsedPileCount()); //1 to battle
        assertEquals(0,scn.GetLSForcePileCount()); //should be free
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(cannon)); //piloted, free to fire

        scn.LSUseCardAction(cannon); //fire
        assertTrue(scn.LSHasCardChoiceAvailable(trooper)); //target
        scn.LSChooseCard(trooper);

        scn.PassAllResponses();
        assertEquals(2,scn.GetLSUsedPileCount()); //1 to battle + 1 destiny draw
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
    }

    //other tests:
    //check perm pilot ability
}
