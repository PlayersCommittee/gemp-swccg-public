package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_9_166_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("talz","1_031"); //(alien)
				}},
				new HashMap<>()
				{{
					put("scimitar1", "9_166"); //scimitar 1
                    put("pilot","1_180"); //imperial pilot
					put("proton","4_179"); //proton bombs
					put("tat","1_289");
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
	public void Scimitar1StatsAndKeywordsAreCorrect() {
		/**
		 * Title: Scimitar 1
		 * Uniqueness: Unique
		 * Side: Dark
		 * Type: Starship
		 * Subtype: Starfighter: TIE/SA
		 * Destiny: 2
		 * Deploy: 2
		 * Power: 1
		 * Maneuver: 2
		 * Forfeit: 4
		 * Icons: Starship, Death Star II
		 * Game Text: May add 1 pilot. Power +3 during a Bombing Run battle. When proton bombs aboard 'collapse'
		 * 		a site, opponent loses 1 Force for each rebel just lost.
		 * Lore: Scimitar bombing group TIE bomber. Equipped with advanced targeting system to increase damage during planetary bombardment.
		 * Set: Death Star II
		 * Rarity: U
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("scimitar1").getBlueprint();

		assertEquals(Title.Scimitar_1, card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertEquals(2, card.getDestiny(), scn.epsilon);
		assertEquals(2, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertFalse(card.hasAbilityAttribute());
		assertEquals(2,card.getManeuver(), scn.epsilon);
		assertEquals(4, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.STARSHIP);
		}});
		assertEquals(CardSubtype.STARFIGHTER,card.getCardSubtype());
		scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
			add(ModelType.TIE_SA);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
			add(Keyword.NO_HYPERDRIVE);
			add(Keyword.SCIMITAR_SQUADRON);
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.STARSHIP);
			add(Icon.DEATH_STAR_II);
		}});
		assertEquals(ExpansionSet.DEATH_STAR_II,card.getExpansionSet());
		assertEquals(Rarity.U,card.getRarity());
	}

	@Test
	public void Scimitar1CollapseCauses1ForceLossPerRebel() {
		var scn = GetScenario();

		var rebelTrooper1 = scn.GetLSFiller(1);
		var rebelTrooper2 = scn.GetLSFiller(2);
		var talz = scn.GetLSCard("talz");

		var scimitar1 = scn.GetDSCard("scimitar1");
		var pilot = scn.GetDSCard("pilot");
		var proton = scn.GetDSCard("proton");

        var tat = scn.GetDSCard("tat");
		var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

		scn.MoveLocationToTable(tat);
		scn.MoveLocationToTable(tat_db);

		scn.MoveCardsToLocation(tat, scimitar1);
		scn.MoveCardsToLocation(tat_db,rebelTrooper1,rebelTrooper2,talz); //2 rebels, 1 alien

		scn.MoveCardsToDSHand(pilot,proton);

        scn.SkipToPhase(Phase.DEPLOY);
		scn.DSDeployCard(pilot);
		scn.DSChooseCard(scimitar1);
		scn.PassAllResponses();

		scn.LSPass();

		scn.DSDeployCard(proton);
		scn.DSChooseCard(scimitar1);
		scn.PassAllResponses();

		assertTrue(scn.IsAttachedTo(scimitar1,proton));
		assertTrue(scn.IsAttachedTo(scimitar1,pilot));
		assertTrue(pilot.isPilotOf());

		scn.SkipToLSTurn();
		scn.SkipToDSTurn(Phase.CONTROL);

		scn.DSForceDrainAt(tat);

		scn.LSPass(); //FORCE_DRAIN_INITIATED - Optional responses

		assertTrue(scn.DSDecisionAvailable("Force drain initiated")); //Force drain initiated at •Tatooine - Optional responses
		scn.DSCardActionAvailable(proton,"collapse");

		scn.PrepareDSDestiny(7); //must be > 4 to collapse
		scn.DSUseCardAction(proton,"collapse");
		scn.DSChooseCard(tat_db);

		scn.LSPass(); //Fire Proton Bombs to 'collapse' site - Optional responses
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

		assertTrue(scn.DSDecisionAvailable("Choose card to be lost"));
		assertTrue(scn.DSHasCardChoicesAvailable(rebelTrooper1,rebelTrooper2,talz));
		scn.DSChooseCard(rebelTrooper1);

		scn.LSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses
		scn.DSPass();

		scn.LSPass(); //LOST_FROM_TABLE - Optional responses
		scn.DSPass();

		assertTrue(scn.DSDecisionAvailable("Choose card to be lost"));
		assertTrue(scn.DSHasCardChoicesAvailable(rebelTrooper2,talz));
		scn.DSChooseCard(talz);

		scn.LSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses
		scn.DSPass();

		scn.LSPass(); //LOST_FROM_TABLE - Optional responses
		scn.DSPass();

		//last choice (rebelTrooper2) taken automatically
		scn.LSPass(); //ABOUT_TO_BE_LOST_FROM_TABLE - Optional responses
		scn.DSPass();

		scn.LSPass(); //LOST_FROM_TABLE - Optional responses
		scn.DSPass();

		scn.DSPass(); //FORCE_LOSS_INITIATED - Optional responses
		scn.LSPass();

		scn.DSPass(); //ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE - Optional responses
		scn.LSPass();

		scn.AwaitingLSForceLossPayment(); //2 force loss remaining (2 rebels lost)
		scn.LSChooseCard(scn.GetTopOfLSReserveDeck());

		scn.DSPass(); //ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE - Optional responses
		scn.LSPass();

		scn.AwaitingLSForceLossPayment(); //1 force loss remaining
		scn.LSChooseCard(scn.GetTopOfLSReserveDeck());

		scn.LSPass(); //COLLAPSED_SITE - Optional responses
		scn.DSPass();

		scn.LSPass(); //FIRED_WEAPON - Optional responses
		scn.DSPass();

		scn.LSPass(); //FORCE_DRAIN_INITIATED - Optional responses
		scn.DSPass();

		scn.LSPass(); //FORCE_LOSS_INITIATED - Optional responses
		scn.DSPass();

		scn.LSPass(); //ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE - Optional responses
		scn.DSPass();

		scn.AwaitingLSForceLossPayment(); //force drain of 1
		scn.LSChooseCard(scn.GetTopOfLSReserveDeck());

		scn.LSPass(); //FORCE_DRAIN_COMPLETED - Optional responses
		scn.DSPass();

		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertEquals(6,scn.GetLSLostPileCount()); //3 characters + 2 damage from rebels lost during collapse + 1 force drain
	}

}
