package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Card_3_042_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("fallback", "3_042"); //Fall Back!
                    put("rogue1", "3_066"); //(enclosed vehicle)
                    put("guard","1_026"); //rebel guard
                    put("landspeeder","1_149"); //luke's x34 landspeeder
                }},
				new HashMap<>()
				{{
                    put("hothsite1","3_150"); //Hoth: Wampa Cave (7th marker)
                    put("hothsite2","3_148"); //Hoth: Ice Plains (5th marker)
                    put("hothsite3","3_149"); //Hoth: North Ridge (4th marker)
                    put("hothsite4","3_144"); //Hoth: Defensive Perimeter (3rd marker)
                    put("keed","1_183"); //Kitik Keed'kak
                    put("droid","1_192"); //R1-G4 (no presence)
                    put("blizzard2","3_155");
                    put("surprise","5_156");
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
	public void FallBackStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Fall Back!
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Interrupt
		 * Subtype: Lost
		 * Destiny: 5
		 * Icons: Interrupt, Hoth
		 * Game Text: If opponent just initiated a battle at an exterior site with more than double your total power,
         *      use 1 Force to target an adjacent site where opponent has no presence.
         *      All your characters present in battle move away (for free) to the target site. The battle is canceled.
		 * Lore: 'K-one zero...all troops disengage.'
		 * Set: Hoth
		 * Rarity: C2
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("fallback").getBlueprint();

		assertEquals("Fall Back!", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
		assertEquals(CardSubtype.LOST, card.getCardSubtype());
		assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.HOTH);
        }});
        assertEquals(ExpansionSet.HOTH,card.getExpansionSet());
        assertEquals(Rarity.C2,card.getRarity());

	}

	@Test
	public void FallBackBasicFunctionalTest() {
        //this tests the basic working conditions for the card in a simple case
        //other tests change specific parts of this setup to demonstrate restrictions are working
        //test1: character moves away from battle location to target adjacent site
        //test2: cost of 1 force was paid
        //test3: card goes to lost pile after being played
		var scn = GetScenario();

		var fallback = scn.GetLSCard("fallback");
        var rebelTrooper1 = scn.GetLSFiller(1);

        var hothsite1 = scn.GetDSCard("hothsite1");
        var hothsite2 = scn.GetDSCard("hothsite2");
        var hothsite3 = scn.GetDSCard("hothsite3");
        var hothsite4 = scn.GetDSCard("hothsite4");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);

		scn.StartGame();

		scn.MoveCardsToLSHand(fallback);

        scn.MoveLocationToTable(hothsite1);
        scn.MoveLocationToTable(hothsite2);
        scn.MoveLocationToTable(hothsite3);
        scn.MoveLocationToTable(hothsite4);

        scn.MoveCardsToLocation(hothsite2,trooper1,trooper2,trooper3,rebelTrooper1);

        scn.LSActivateForceCheat(1); //enough to pay Fall Back! cost

		scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(hothsite2);

        assertTrue(scn.LSDecisionAvailable("Battle just initiated")); //Battle just initiated at - Optional responses
        assertTrue(scn.LSCardPlayAvailable(fallback,"Fall back"));
        scn.LSPlayCard(fallback);

        assertTrue(scn.LSHasCardChoicesAvailable(hothsite1,hothsite3)); //adjacent to hothsite2
        assertFalse(scn.LSHasCardChoicesAvailable(hothsite2,hothsite4));
        scn.LSChooseCard(hothsite3);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //Playing Fall Back! - Optional responses
        scn.LSPass();

        assertTrue(scn.LSDecisionAvailable("Choose next card to move away"));
        assertTrue(scn.LSHasCardChoicesAvailable(rebelTrooper1));
        scn.LSChooseCard(rebelTrooper1);

        scn.DSPass(); //MOVING_USING_LANDSPEED - Optional responses
        scn.LSPass();

        scn.DSPass(); //MOVED_USING_LANDSPEED - Optional responses
        scn.LSPass();

        scn.DSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.LSPass();

        scn.DSPass(); //BATTLE_INITIATED - Optional responses
        scn.LSPass();

        //battle ended here due to lack of presence (not canceled)

        assertTrue(scn.AwaitingLSBattlePhaseActions());

        assertTrue(scn.CardsAtLocation(hothsite3,rebelTrooper1)); //test1: successfully moved away
        assertEquals(1,scn.GetLSUsedPileCount()); //test2: used 1 force to play Fall Back!
        assertSame(Zone.TOP_OF_LOST_PILE,fallback.getZone()); //test3: placed in lost pile
	}

    @Test
    public void FallBackCancelsBattleIfPresenceRemains() {
        //test1: if battle doesn't end due to moving away (because LS still has presence),
        //  the battle is canceled
        var scn = GetScenario();

        var fallback = scn.GetLSCard("fallback");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var rogue1 = scn.GetLSCard("rogue1");

        var hothsite1 = scn.GetDSCard("hothsite1");
        var hothsite2 = scn.GetDSCard("hothsite2");
        var hothsite3 = scn.GetDSCard("hothsite3");
        var hothsite4 = scn.GetDSCard("hothsite4");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);

        scn.StartGame();

        scn.MoveCardsToLSHand(fallback);

        scn.MoveLocationToTable(hothsite1);
        scn.MoveLocationToTable(hothsite2);
        scn.MoveLocationToTable(hothsite3);
        scn.MoveLocationToTable(hothsite4);

        scn.MoveCardsToLocation(hothsite2,trooper1,trooper2,trooper3,rebelTrooper1,rogue1);
        scn.BoardAsPassenger(rogue1,rebelTrooper2);

        scn.LSActivateForceCheat(1); //enough to pay Fall Back! cost

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(hothsite2);

        assertTrue(scn.LSDecisionAvailable("Battle just initiated")); //Battle just initiated at - Optional responses
        assertTrue(scn.LSCardPlayAvailable(fallback,"Fall back"));
        scn.LSPlayCard(fallback);

        assertTrue(scn.LSHasCardChoicesAvailable(hothsite1,hothsite3));
        assertFalse(scn.LSHasCardChoicesAvailable(hothsite2,hothsite4));
        scn.LSChooseCard(hothsite3);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //Playing Fall Back! - Optional responses
        scn.LSPass();

        assertTrue(scn.LSDecisionAvailable("Choose next card to move away"));
        assertTrue(scn.LSHasCardChoicesAvailable(rebelTrooper1));
        assertFalse(scn.LSHasCardChoicesAvailable(rebelTrooper2,rogue1));
        scn.LSChooseCard(rebelTrooper1);

        //automatically chooses destination, since only 1 option
        scn.DSPass(); //MOVING_USING_LANDSPEED - Optional responses
        scn.LSPass();

        scn.DSPass(); //MOVED_USING_LANDSPEED - Optional responses
        scn.LSPass();

        assertTrue(scn.DSDecisionAvailable("BATTLE_CANCELED")); //test1
        scn.DSPass(); //BATTLE_CANCELED - Optional responses
        scn.LSPass();

        scn.DSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.LSPass();

        scn.DSPass(); //BATTLE_INITIATED - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingLSBattlePhaseActions()); //test1 (battle actually ended)

        assertTrue(scn.CardsAtLocation(hothsite3,rebelTrooper1));
        assertTrue(scn.CardsAtLocation(hothsite2,rogue1));
        assertTrue(scn.IsAboardAsPassenger(rogue1,rebelTrooper2));
    }

    @Test
    public void FallBackRequiresForceCost() {
        //test1: if no force available, cannot play Fall Back!
        var scn = GetScenario();

        var fallback = scn.GetLSCard("fallback");
        var rebelTrooper1 = scn.GetLSFiller(1);

        var hothsite1 = scn.GetDSCard("hothsite1");
        var hothsite2 = scn.GetDSCard("hothsite2");
        var hothsite3 = scn.GetDSCard("hothsite3");
        var hothsite4 = scn.GetDSCard("hothsite4");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);

        scn.StartGame();

        scn.MoveCardsToLSHand(fallback);

        scn.MoveLocationToTable(hothsite1);
        scn.MoveLocationToTable(hothsite2);
        scn.MoveLocationToTable(hothsite3);
        scn.MoveLocationToTable(hothsite4);

        scn.MoveCardsToLocation(hothsite2,trooper1,trooper2,trooper3,rebelTrooper1);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(hothsite2);

        assertEquals(0,scn.GetLSForcePileCount()); //not enough to pay 1 force cost
        assertTrue(scn.AwaitingDSWeaponsSegmentActions()); //test1: past window for LS to respond with Fall Back
    }

    @Test
    public void FallBackRequiresOpponentInitiateBattle() {
        //test1: cannot play if all other conditions are met, but owner initiates battle instead of opponent
        var scn = GetScenario();

        var fallback = scn.GetLSCard("fallback");
        var rebelTrooper1 = scn.GetLSFiller(1);

        var hothsite1 = scn.GetDSCard("hothsite1");
        var hothsite2 = scn.GetDSCard("hothsite2");
        var hothsite3 = scn.GetDSCard("hothsite3");
        var hothsite4 = scn.GetDSCard("hothsite4");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);

        scn.StartGame();

        scn.MoveCardsToLSHand(fallback);

        scn.MoveLocationToTable(hothsite1);
        scn.MoveLocationToTable(hothsite2);
        scn.MoveLocationToTable(hothsite3);
        scn.MoveLocationToTable(hothsite4);

        scn.MoveCardsToLocation(hothsite2,trooper1,trooper2,trooper3,rebelTrooper1);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertEquals(4,scn.GetLSForcePileCount());
        scn.LSInitiateBattle(hothsite2);

        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to pay 1 force cost
        assertTrue(scn.AwaitingLSWeaponsSegmentActions()); //test1: past window for LS to respond with Fall Back
    }

    @Test
    public void FallBackRequiresLessThanHalfOpponentPower() {
        //test1: cannot play if all other conditions are met, but opponent has only twice owner's power
        var scn = GetScenario();

        var fallback = scn.GetLSCard("fallback");
        var rebelTrooper1 = scn.GetLSFiller(1);

        var hothsite1 = scn.GetDSCard("hothsite1");
        var hothsite2 = scn.GetDSCard("hothsite2");
        var hothsite3 = scn.GetDSCard("hothsite3");
        var hothsite4 = scn.GetDSCard("hothsite4");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);

        scn.StartGame();

        scn.MoveCardsToLSHand(fallback);

        scn.MoveLocationToTable(hothsite1);
        scn.MoveLocationToTable(hothsite2);
        scn.MoveLocationToTable(hothsite3);
        scn.MoveLocationToTable(hothsite4);

        scn.MoveCardsToLocation(hothsite2,trooper1,trooper2,rebelTrooper1);

        scn.LSActivateForceCheat(1); //enough to pay Fall Back! cost

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(hothsite2);

        assertEquals(1,scn.GetLSTotalPower());
        assertEquals(2,scn.GetDSTotalPower());

        assertTrue(scn.AwaitingDSWeaponsSegmentActions()); //test1: past window for LS to respond with Fall Back
    }

    @Test
    public void FallBackRequiresAtLeastOneCharacterPresentInBattle() {
        //test1: cannot play if all other conditions are met, but no characters are present
        var scn = GetScenario();

        var fallback = scn.GetLSCard("fallback");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rogue1 = scn.GetLSCard("rogue1");

        var hothsite1 = scn.GetDSCard("hothsite1");
        var hothsite2 = scn.GetDSCard("hothsite2");
        var hothsite3 = scn.GetDSCard("hothsite3");
        var hothsite4 = scn.GetDSCard("hothsite4");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);

        scn.StartGame();

        scn.MoveCardsToLSHand(fallback);

        scn.MoveLocationToTable(hothsite1);
        scn.MoveLocationToTable(hothsite2);
        scn.MoveLocationToTable(hothsite3);
        scn.MoveLocationToTable(hothsite4);

        scn.MoveCardsToLocation(hothsite2,trooper1,trooper2,trooper3,rogue1);
        scn.BoardAsPassenger(rogue1,rebelTrooper1); //not present inside enclosed vehicle

        scn.LSActivateForceCheat(1); //enough to pay Fall Back! cost

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(hothsite2);

        assertEquals(0,scn.GetLSTotalPower());

        assertTrue(scn.AwaitingDSWeaponsSegmentActions()); //test1: past window for LS to respond with Fall Back
    }

    @Test
    public void FallBackTargetsAdjacentSitesWithoutOpponentPresent() {
        //test1: cannot target an adjacent site where opponent has presence
        var scn = GetScenario();

        var fallback = scn.GetLSCard("fallback");
        var rebelTrooper1 = scn.GetLSFiller(1);

        var hothsite1 = scn.GetDSCard("hothsite1");
        var hothsite2 = scn.GetDSCard("hothsite2");
        var hothsite3 = scn.GetDSCard("hothsite3");
        var hothsite4 = scn.GetDSCard("hothsite4");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);
        var trooper4 = scn.GetDSFiller(4);
        var droid = scn.GetDSCard("droid");

        scn.StartGame();

        scn.MoveCardsToLSHand(fallback);

        scn.MoveLocationToTable(hothsite1);
        scn.MoveLocationToTable(hothsite2);
        scn.MoveLocationToTable(hothsite3);
        scn.MoveLocationToTable(hothsite4);

        scn.MoveCardsToLocation(hothsite1,droid);
        scn.MoveCardsToLocation(hothsite2,trooper1,trooper2,trooper3,rebelTrooper1);
        scn.MoveCardsToLocation(hothsite3,trooper4); //DS has presence at this adjacent site

        scn.LSActivateForceCheat(1); //enough to pay Fall Back! cost

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(hothsite2);

        assertTrue(scn.LSDecisionAvailable("Battle just initiated")); //Battle just initiated at - Optional responses
        assertTrue(scn.LSCardPlayAvailable(fallback,"Fall back"));
        scn.LSPlayCard(fallback);

        assertTrue(scn.LSHasCardChoicesAvailable(hothsite1)); //opponent has character bot not presence
        assertFalse(scn.LSHasCardChoicesAvailable(hothsite2)); //not adjacent
        assertFalse(scn.LSHasCardChoicesAvailable(hothsite3)); //test1: opponent has presence
        assertFalse(scn.LSHasCardChoicesAvailable(hothsite4)); //not adjacent
        scn.LSChooseCard(hothsite1);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //Playing Fall Back! - Optional responses
        scn.LSPass();

        assertTrue(scn.LSDecisionAvailable("Choose next card to move away"));
        assertTrue(scn.LSHasCardChoicesAvailable(rebelTrooper1));
        scn.LSChooseCard(rebelTrooper1);

        //automatically chooses destination, since only 1 option
        scn.DSPass(); //MOVING_USING_LANDSPEED - Optional responses
        scn.LSPass();

        scn.DSPass(); //MOVED_USING_LANDSPEED - Optional responses
        scn.LSPass();

        scn.DSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.LSPass();

        scn.DSPass(); //BATTLE_INITIATED - Optional responses
        scn.LSPass();

        //battle canceled here due to lack of presence?

        assertTrue(scn.AwaitingLSBattlePhaseActions());

        assertTrue(scn.CardsAtLocation(hothsite1,rebelTrooper1));
        assertEquals(1,scn.GetLSUsedPileCount()); //cost to play Fall Back!
        assertSame(Zone.TOP_OF_LOST_PILE,fallback.getZone());
    }

    @Test
    public void FallBackCanBePlayedEvenIfNoCharactersCanMove() {
        //test1: playable even if move away will fail for all characters
        //test2: character that cannot move away is still at battle location after playing
        var scn = GetScenario();

        var fallback = scn.GetLSCard("fallback");
        var guard = scn.GetLSCard("guard");

        var hothsite1 = scn.GetDSCard("hothsite1");
        var hothsite2 = scn.GetDSCard("hothsite2");
        var hothsite3 = scn.GetDSCard("hothsite3");
        var hothsite4 = scn.GetDSCard("hothsite4");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);
        var blizzard2 = scn.GetDSCard("blizzard2");

        scn.StartGame();

        scn.MoveCardsToLSHand(fallback);

        scn.MoveLocationToTable(hothsite1);
        scn.MoveLocationToTable(hothsite2);
        scn.MoveLocationToTable(hothsite3);
        scn.MoveLocationToTable(hothsite4);

        scn.MoveCardsToLocation(hothsite2,trooper1,trooper2,trooper3,blizzard2,guard);

        scn.LSActivateForceCheat(1); //enough to pay Fall Back! cost

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(hothsite2);

        assertTrue(scn.GetDSTotalPower() >= (2 * scn.GetLSTotalPower() + 1));

        assertTrue(scn.LSDecisionAvailable("Battle just initiated")); //Battle just initiated at - Optional responses
        assertTrue(scn.LSCardPlayAvailable(fallback,"Fall back")); //test1
        scn.LSPlayCard(fallback);

        assertTrue(scn.LSHasCardChoicesAvailable(hothsite1,hothsite3));
        assertFalse(scn.LSHasCardChoicesAvailable(hothsite2,hothsite4));
        scn.LSChooseCard(hothsite3);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //Playing Fall Back! - Optional responses
        scn.LSPass();

        assertTrue(scn.DSDecisionAvailable("BATTLE_CANCELED - Optional responses")); //guard could not move
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSBattlePhaseActions());

        assertTrue(scn.CardsAtLocation(hothsite2,guard)); //test2: did not move away
    }

    @Test
    public void FallBackTargetsAllCharactersPresentInBattle() {
        //test1: all characters present in battle (including on non-enclosed vehicles) are targeted to move away
        //test2: character on non-enclosed vehicle disembarks and moves away
        //test3: character not present in battle (in enclosed vehicle) is not targeted, does not move away
        var scn = GetScenario();

        var fallback = scn.GetLSCard("fallback");
        var rebelTrooper1 = scn.GetLSFiller(1);
        var rebelTrooper2 = scn.GetLSFiller(2);
        var rebelTrooper3 = scn.GetLSFiller(3);
        var guard = scn.GetLSCard("guard");
        var landspeeder = scn.GetLSCard("landspeeder");
        var rogue1 = scn.GetLSCard("rogue1");

        var hothsite1 = scn.GetDSCard("hothsite1");
        var hothsite2 = scn.GetDSCard("hothsite2");
        var hothsite3 = scn.GetDSCard("hothsite3");
        var hothsite4 = scn.GetDSCard("hothsite4");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);
        var trooper4 = scn.GetDSFiller(4);
        var trooper5 = scn.GetDSFiller(5);
        var keed = scn.GetDSCard("keed");

        scn.StartGame();

        scn.MoveCardsToLSHand(fallback);

        scn.MoveLocationToTable(hothsite1);
        scn.MoveLocationToTable(hothsite2);
        scn.MoveLocationToTable(hothsite3);
        scn.MoveLocationToTable(hothsite4);

        scn.MoveCardsToLocation(hothsite2,trooper1,trooper2,trooper3,trooper4,trooper5,keed,rebelTrooper1,rogue1,landspeeder);
        scn.BoardAsPassenger(rogue1,rebelTrooper2);
        scn.BoardAsPassenger(landspeeder,rebelTrooper3,guard);

        scn.LSActivateForceCheat(1); //enough to pay Fall Back! cost

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(hothsite2);

        assertTrue(scn.GetDSTotalPower() >= (2 * scn.GetLSTotalPower() + 1));

        assertTrue(scn.LSDecisionAvailable("Battle just initiated")); //Battle just initiated at - Optional responses
        assertTrue(scn.LSCardPlayAvailable(fallback,"Fall back"));
        scn.LSPlayCard(fallback);

        assertTrue(scn.LSHasCardChoicesAvailable(hothsite1,hothsite3));
        assertFalse(scn.LSHasCardChoicesAvailable(hothsite2,hothsite4));
        scn.LSChooseCard(hothsite3);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        scn.DSPass(); //Playing Fall Back! - Optional responses
        scn.LSPass();

        assertTrue(scn.LSDecisionAvailable("Choose next card to move away"));
        assertTrue(scn.LSHasCardChoicesAvailable(rebelTrooper1,rebelTrooper3)); //test1
        assertFalse(scn.LSHasCardChoiceAvailable(rebelTrooper2));//test3
        assertFalse(scn.LSHasCardChoiceAvailable(guard));
        scn.LSChooseCard(rebelTrooper1);

        scn.DSPass(); //MOVING_USING_LANDSPEED - Optional responses
        scn.LSPass();

        scn.DSPass(); //MOVED_USING_LANDSPEED - Optional responses
        scn.LSPass();

        //only 1 character left to move, so automatically disembarks and moves
        scn.DSPass(); //DISEMBARKING - Optional responses
        scn.LSPass();

        scn.DSPass(); //DISEMBARKED - Optional responses
        scn.LSPass();

        scn.DSPass(); //MOVING_USING_LANDSPEED - Optional responses
        scn.LSPass();

        scn.DSPass(); //MOVED_USING_LANDSPEED - Optional responses
        scn.LSPass();

        scn.DSPass(); //BATTLE_CANCELED - Optional responses
        scn.LSPass();

        scn.DSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.LSPass();

        scn.DSPass(); //BATTLE_INITIATED - Optional responses
        scn.LSPass();

        assertTrue(scn.AwaitingLSBattlePhaseActions());

        assertTrue(scn.CardsAtLocation(hothsite3,rebelTrooper1,rebelTrooper3)); //test2
        assertTrue(scn.CardsAtLocation(hothsite2,landspeeder,rogue1)); //test3
        assertFalse(scn.CardsAtLocation(hothsite3,guard));
        assertFalse(scn.CardsAtLocation(hothsite3,rebelTrooper2));
        assertTrue(guard.isPassengerOf());
        assertTrue(rebelTrooper2.isPassengerOf());
        assertEquals(1,scn.GetLSUsedPileCount()); //cost to play Fall Back!
        assertSame(Zone.TOP_OF_LOST_PILE,fallback.getZone());
    }

    @Test
    public void FallBackDestinationCanBeRetargetedBySurprise() {
        //test1: surprise can retarget from the selected site to a different adjacent site
        var scn = GetScenario();

        var fallback = scn.GetLSCard("fallback");
        var rebelTrooper1 = scn.GetLSFiller(1);

        var hothsite1 = scn.GetDSCard("hothsite1");
        var hothsite2 = scn.GetDSCard("hothsite2");
        var hothsite3 = scn.GetDSCard("hothsite3");
        var hothsite4 = scn.GetDSCard("hothsite4");
        var trooper1 = scn.GetDSFiller(1);
        var trooper2 = scn.GetDSFiller(2);
        var trooper3 = scn.GetDSFiller(3);
        var surprise = scn.GetDSCard("surprise");

        scn.StartGame();

        scn.MoveCardsToLSHand(fallback);
        scn.MoveCardsToDSHand(surprise);

        scn.MoveLocationToTable(hothsite1);
        scn.MoveLocationToTable(hothsite2);
        scn.MoveLocationToTable(hothsite3);
        scn.MoveLocationToTable(hothsite4);

        scn.MoveCardsToLocation(hothsite2,trooper1,trooper2,trooper3,rebelTrooper1);

        scn.LSActivateForceCheat(1); //enough to pay Fall Back! cost
        scn.DSActivateForceCheat(4); //enough to battle and play surprise

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(hothsite2);

        assertTrue(scn.LSDecisionAvailable("Battle just initiated")); //Battle just initiated at - Optional responses
        assertTrue(scn.LSCardPlayAvailable(fallback,"Fall back"));
        scn.LSPlayCard(fallback);

        assertTrue(scn.LSHasCardChoicesAvailable(hothsite1,hothsite3));
        assertFalse(scn.LSHasCardChoicesAvailable(hothsite2,hothsite4));
        scn.LSChooseCard(hothsite3);

        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();

        assertTrue(scn.DSDecisionAvailable("Playing")); //Playing Fall Back! - Optional responses
        assertTrue(scn.DSCardPlayAvailable(surprise));
        scn.DSPlayCard(surprise);

        //Choose card (or card in group) to re-target from, or click 'Done' to cancel
        assertTrue(scn.DSHasCardChoiceAvailable(hothsite3)); //original selection
        assertFalse(scn.DSHasCardChoicesAvailable(hothsite1,hothsite2,hothsite4));
        scn.DSChooseCard(hothsite3);

        //Choose card (or card in group) to re-target to, or click 'Done' to cancel
        assertTrue(scn.DSHasCardChoiceAvailable(hothsite1)); //DS site that meets criteria
        assertFalse(scn.DSHasCardChoiceAvailable(hothsite2)); //not adjacent
        assertFalse(scn.DSHasCardChoiceAvailable(hothsite3)); //must retarget somewhere different
        assertFalse(scn.DSHasCardChoiceAvailable(hothsite4)); //not adjacent
        scn.DSChooseCard(hothsite1);

        scn.LSPass(); //Use 3 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //Playing Surprise - Optional responses
        scn.DSPass();

        scn.LSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.DSPass();

        scn.LSPass(); //Playing Fall Back! - Optional responses
        scn.DSPass();

        assertTrue(scn.LSDecisionAvailable("Choose next card to move away"));
        assertTrue(scn.LSHasCardChoicesAvailable(rebelTrooper1));
        scn.LSChooseCard(rebelTrooper1);

        //automatically chooses destination, since only 1 option
        scn.DSPass(); //MOVING_USING_LANDSPEED - Optional responses
        scn.LSPass();

        scn.DSPass(); //MOVED_USING_LANDSPEED - Optional responses
        scn.LSPass();

        scn.DSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.LSPass();

        scn.DSPass(); //BATTLE_INITIATED - Optional responses
        scn.LSPass();

        //battle canceled here due to lack of presence?

        assertTrue(scn.AwaitingLSBattlePhaseActions());

        assertTrue(scn.CardsAtLocation(hothsite1,rebelTrooper1)); //test1: successfully re-routed from hothsite3 to hothsite1
    }

}
