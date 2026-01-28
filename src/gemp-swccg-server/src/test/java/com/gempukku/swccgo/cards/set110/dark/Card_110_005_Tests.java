package com.gempukku.swccgo.cards.set110.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_110_005_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
				}},
				new HashMap<>()
				{{
                    put("bossk", "110_005"); //bossk with mortar gun
                    put("bounty","5_113");
                    put("sniper","2_139");
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
	public void BosskWithMortarGunStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Bossk With Mortar Gun
		 * Uniqueness: Unique
		 * Side: Dark
		 * Type: Character
		 * Subtype: Alien
		 * Destiny: 1
		 * Deploy: 5
		 * Power: 4
		 * Ability: 2
		 * Forfeit: 3
		 * Icons: Premium
		 * Persona: Bossk
		 * Game Text: Adds 2 to power of anything he pilots. Permanent weapon is •Bossk's Mortar Gun
         *      (may fire for free; draw destiny; may subtract or add 1 if at same site as a bounty;
         *      choose one character with that destiny number present to be captured.)
		 * Lore: Trandoshan bounty hunter. Modified his mortar gun to fire stun cartridges for live captures.
         *      Uses non-fragmentary capture rounds to minimize collateral damage.
		 * Set: Enhanced Jabba's Palace
		 * Rarity: PM
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("bossk").getBlueprint();

		assertEquals("Bossk With Mortar Gun", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(5, card.getDeployCost(), scn.epsilon);
		assertEquals(4, card.getPower(), scn.epsilon);
		assertEquals(2, card.getAbility(), scn.epsilon);
		assertEquals(3, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.ALIEN);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BOUNTY_HUNTER);
		}});
        assertEquals(Species.TRANDOSHAN,card.getSpecies());
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.BOSSK);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.ALIEN);
			add(Icon.PREMIUM);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.PERMANENT_WEAPON);
		}});
		assertEquals(ExpansionSet.ENHANCED_JABBAS_PALACE,card.getExpansionSet());
		assertEquals(Rarity.PM,card.getRarity());
	}

	@Test
	public void BosskWithMortarGunCanCaptureInBattle() {

		var scn = GetScenario();

		var rebelTrooper = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        var bossk = scn.GetDSCard("bossk");

        scn.StartGame();

		scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site,bossk,rebelTrooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(bossk,"Fire"));
        scn.PrepareDSDestiny(1); //match destiny of the rebel trooper
        scn.DSUseCardAction(bossk,"Fire");

        scn.LSPass(); //Fire Bossk's Mortar Gun - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //null - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_CAPTURED - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("Choose option for capturing"));
        scn.DSChooseSeizeCaptive();

        scn.LSPass(); //CAPTURED - Optional responses
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSBattlePhaseActions()); //battle ended due to no presence
        assertTrue(rebelTrooper.isCaptive());
        assertTrue(scn.IsAttachedTo(bossk,rebelTrooper));
    }

    @Test
    public void BosskWithMortarGunCannotCaptureInBattleIfDestinyDoesNotMatch() {

        var scn = GetScenario();

        var rebelTrooper = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        var bossk = scn.GetDSCard("bossk");

        scn.StartGame();

        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site,bossk,rebelTrooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(bossk,"Fire"));
        scn.PrepareDSDestiny(0); //no match
        scn.DSUseCardAction(bossk,"Fire");

        scn.LSPass(); //Fire Bossk's Mortar Gun - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        assertTrue(scn.LSDecisionAvailable("FIRED_WEAPON - Optional responses")); //past opportunity to capture
    }

    @Test
    public void BosskWithMortarGunCanAdd1WithBountyInBattle() {

        var scn = GetScenario();

        var rebelTrooper = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        var bossk = scn.GetDSCard("bossk");
        var bounty = scn.GetDSCard("bounty");

        scn.StartGame();

        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site,bossk,rebelTrooper);

        scn.MoveCardsToDSHand(bounty);

        scn.AttachCardsTo(rebelTrooper,bounty);
//        scn.SkipToPhase(Phase.DEPLOY);
//        scn.DSPlayCard(bounty);
//        scn.DSChooseCard(rebelTrooper);
//        scn.PassAllResponses();
//        assertTrue(scn.IsAttachedTo(rebelTrooper,bounty));

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(bossk,"Fire"));
        scn.PrepareDSDestiny(0);
        scn.DSUseCardAction(bossk,"Fire");

        scn.LSPass(); //Fire Bossk's Mortar Gun - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses

        assertTrue(scn.DSDecisionAvailable("Just completed drawing weapon destiny - Optional responses"));
        assertTrue(scn.DSCardActionAvailable(bossk,"Add 1")); //test1
        assertTrue(scn.DSCardActionAvailable(bossk,"Subtract 1"));
        scn.DSUseCardAction(bossk,"Add 1");

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //null - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_CAPTURED - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("Choose option for capturing")); //test1: destiny 0 + 1 allows capturing a destiny 1 target
        scn.DSChooseSeizeCaptive();

        scn.LSPass(); //CAPTURED - Optional responses
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSBattlePhaseActions()); //battle ended due to no presence
        assertTrue(rebelTrooper.isCaptive());
        assertTrue(scn.IsAttachedTo(bossk,rebelTrooper));
    }

    @Test
    public void BosskWithMortarGunCanSubtract1WithBountyInBattle() {

        var scn = GetScenario();

        var rebelTrooper = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        var bossk = scn.GetDSCard("bossk");
        var bounty = scn.GetDSCard("bounty");

        scn.StartGame();

        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site,bossk,rebelTrooper);

        scn.MoveCardsToDSHand(bounty);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSPlayCard(bounty);
        scn.DSChooseCard(rebelTrooper);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(rebelTrooper,bounty));

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(bossk,"Fire"));
        scn.PrepareDSDestiny(2);
        scn.DSUseCardAction(bossk,"Fire");

        scn.LSPass(); //Fire Bossk's Mortar Gun - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses

        assertTrue(scn.DSDecisionAvailable("Just completed drawing weapon destiny - Optional responses"));
        assertTrue(scn.DSCardActionAvailable(bossk,"Add 1")); //test1
        assertTrue(scn.DSCardActionAvailable(bossk,"Subtract 1"));
        scn.DSUseCardAction(bossk,"Subtract 1");

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //null - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_CAPTURED - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("Choose option for capturing")); //test1: destiny 2 - 1 allows capturing a destiny 1 target
        scn.DSChooseSeizeCaptive();

        scn.LSPass(); //CAPTURED - Optional responses
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSBattlePhaseActions()); //battle ended due to no presence
        assertTrue(rebelTrooper.isCaptive());
        assertTrue(scn.IsAttachedTo(bossk,rebelTrooper));
    }

    @Test
    public void BosskWithMortarGunCanCaptureDuringSniper() {

        var scn = GetScenario();

        var rebelTrooper = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        var bossk = scn.GetDSCard("bossk");
        var sniper = scn.GetDSCard("sniper");

        scn.StartGame();

        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site,bossk,rebelTrooper);

        scn.MoveCardsToDSHand(sniper);

        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareDSDestiny(1); //match destiny of the rebel trooper
        scn.DSPlayCard(sniper);
        scn.DSChooseCard(bossk);

        scn.LSPass(); //Playing •Sniper - Optional responses
        scn.DSPass();

        scn.LSPass(); //Fire Bossk's Mortar Gun - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //null - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_CAPTURED - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("Choose option for capturing"));
        scn.DSChooseSeizeCaptive();

        scn.LSPass(); //CAPTURED - Optional responses
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        scn.LSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(rebelTrooper.isCaptive());
        assertTrue(scn.IsAttachedTo(bossk,rebelTrooper));
    }

    //demonstrates fixed https://github.com/PlayersCommittee/gemp-swccg-public/issues/932
    @Test
    public void BosskWithMortarGunCanCanAdd1WithBountyDuringSniper() {

        var scn = GetScenario();

        var rebelTrooper = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        var bossk = scn.GetDSCard("bossk");
        var sniper = scn.GetDSCard("sniper");
        var bounty = scn.GetDSCard("bounty");

        scn.StartGame();

        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site,bossk,rebelTrooper);

        scn.AttachCardsTo(rebelTrooper,bounty);

        scn.MoveCardsToDSHand(sniper);

        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareDSDestiny(0);
        scn.DSPlayCard(sniper);
        scn.DSChooseCard(bossk);

        scn.LSPass(); //Playing •Sniper - Optional responses
        scn.DSPass();

        scn.LSPass(); //Fire Bossk's Mortar Gun - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses

        assertTrue(scn.DSDecisionAvailable("Just completed drawing weapon destiny - Optional responses"));
        assertTrue(scn.DSCardActionAvailable(bossk,"Add 1")); //test1
        assertTrue(scn.DSCardActionAvailable(bossk,"Subtract 1"));
        scn.DSUseCardAction(bossk,"Add 1");

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //null - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_CAPTURED - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("Choose option for capturing"));
        scn.DSChooseSeizeCaptive();

        scn.LSPass(); //CAPTURED - Optional responses
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        scn.LSPass(); //PUT_IN_CARD_PILE_FROM_OFF_TABLE - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(rebelTrooper.isCaptive());
        assertTrue(scn.IsAttachedTo(bossk,rebelTrooper));
    }
}
