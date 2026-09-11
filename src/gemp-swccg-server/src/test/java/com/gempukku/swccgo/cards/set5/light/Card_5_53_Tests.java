package com.gempukku.swccgo.cards.set5.light;

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
import static org.junit.Assert.assertTrue;

/**
 * VHD tests for 5_53 Innocent Scoundrel.
 */
public class Card_5_53_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("scoundrel", "5_53");
					put("han", "1_11");
					put("lando", "5_5");
					put("luke", "1_19");
					put("proficiency", "1_54");
					put("saber", "1_155");
					put("rycar", "1_63");
				}},
				new HashMap<>() {{
					put("dining", "5_168");
					put("vader", "1_168");
					put("dsBlaster", "1_312");
					put("dsLando", "5_99");
					put("platform", "5_169");
				}},
				20,
				20,
				StartingSetup.DefaultLSGroundLocation,
				StartingSetup.DefaultDSGroundLocation,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	private void SafePassOptionalResponses(VirtualTableScenario scn) {
		for (int i = 0; i < 25; i++) {
			var decision = scn.GetCurrentDecision();
			if (decision == null) {
				return;
			}
			String text = decision.getText();
			if (text == null) {
				return;
			}
			String lower = text.toLowerCase();
			if (lower.contains("optional")) {
				scn.PassResponses("optional");
			} else if (lower.contains("required")) {
				scn.PassResponses("required");
			} else {
				return;
			}
		}
	}

	@Test
	public void InnocentScoundrelStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetLSCard("scoundrel").getBlueprint();
		assertEquals(Title.Innocent_Scoundrel, card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
		assertEquals(3, card.getDestiny(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.CLOUD_CITY);
			add(Icon.INTERRUPT);
		}});
		assertEquals(ExpansionSet.CLOUD_CITY, card.getExpansionSet());
		assertEquals(Rarity.U, card.getRarity());
	}

	@Test
	public void LostCancelEffectOnHan() {
		var scn = GetScenario();
		var scoundrel = scn.GetLSCard("scoundrel");
		var han = scn.GetLSCard("han");
		var proficiency = scn.GetLSCard("proficiency");
		var dining = scn.GetDSCard("dining");

		scn.StartGame();
		scn.MoveCardsToLSHand(scoundrel);
		scn.MoveLocationToTable(dining);
		scn.MoveCardsToLocation(dining, han);
		scn.AttachCardsTo(han, scn.GetLSCard("saber"), proficiency);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue("LOST should be playable vs non-Alter-immune Effect on Han",
				scn.LSPlayLostInterruptAvailable(scoundrel));
		scn.LSPlayLostInterrupt(scoundrel);
		scn.LSChooseCard(proficiency);
		SafePassOptionalResponses(scn);

		assertTrue("Effect should be canceled",
				proficiency.getZone() == Zone.LOST_PILE || proficiency.getZone() == Zone.TOP_OF_LOST_PILE);
		assertTrue("Scoundrel goes to Lost Pile",
				scoundrel.getZone() == Zone.LOST_PILE || scoundrel.getZone() == Zone.TOP_OF_LOST_PILE);
	}

	@Test
	public void LostCancelEffectOnYourLando() {
		var scn = GetScenario();
		var scoundrel = scn.GetLSCard("scoundrel");
		var lando = scn.GetLSCard("lando");
		var proficiency = scn.GetLSCard("proficiency");
		var dining = scn.GetDSCard("dining");

		scn.StartGame();
		scn.MoveCardsToLSHand(scoundrel);
		scn.MoveLocationToTable(dining);
		scn.MoveCardsToLocation(dining, lando);
		scn.AttachCardsTo(lando, scn.GetLSCard("saber"), proficiency);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSPlayLostInterruptAvailable(scoundrel));
		scn.LSPlayLostInterrupt(scoundrel);
		scn.LSChooseCard(proficiency);
		SafePassOptionalResponses(scn);

		assertTrue(proficiency.getZone() == Zone.LOST_PILE || proficiency.getZone() == Zone.TOP_OF_LOST_PILE);
	}

	@Test
	public void LostCannotCancelEffectImmuneToAlter() {
		var scn = GetScenario();
		var scoundrel = scn.GetLSCard("scoundrel");
		var han = scn.GetLSCard("han");
		var rycar = scn.GetLSCard("rycar");
		var dining = scn.GetDSCard("dining");

		scn.StartGame();
		scn.MoveCardsToLSHand(scoundrel);
		scn.MoveLocationToTable(dining);
		scn.MoveCardsToLocation(dining, han);
		scn.AttachCardsTo(han, rycar);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertFalse("Cannot cancel Effect immune to Alter",
				scn.LSPlayLostInterruptAvailable(scoundrel));
	}

	@Test
	public void LostCannotCancelEffectOnOpponentsLando() {
		var scn = GetScenario();
		var scoundrel = scn.GetLSCard("scoundrel");
		var proficiency = scn.GetLSCard("proficiency");
		var dsLando = scn.GetDSCard("dsLando");
		var dining = scn.GetDSCard("dining");

		scn.StartGame();
		scn.MoveCardsToLSHand(scoundrel);
		scn.MoveLocationToTable(dining);
		scn.MoveCardsToLocation(dining, dsLando);
		scn.AttachCardsTo(dsLando, scn.GetLSCard("saber"), proficiency);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertFalse("Cannot cancel Effect on opponent's Lando",
				scn.LSPlayLostInterruptAvailable(scoundrel));
	}

	@Test
	public void LostCannotCancelEffectOnNonHanNonLando() {
		var scn = GetScenario();
		var scoundrel = scn.GetLSCard("scoundrel");
		var luke = scn.GetLSCard("luke");
		var proficiency = scn.GetLSCard("proficiency");
		var dining = scn.GetDSCard("dining");

		scn.StartGame();
		scn.MoveCardsToLSHand(scoundrel);
		scn.MoveLocationToTable(dining);
		scn.MoveCardsToLocation(dining, luke);
		scn.AttachCardsTo(luke, scn.GetLSCard("saber"), proficiency);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertFalse("Cannot cancel Effect on non-Han / non-your-Lando",
				scn.LSPlayLostInterruptAvailable(scoundrel));
	}

	@Test
	public void UsedPlayableWhenYourGamblerTargetedForcesLose2WhenSoleTarget() {
		var scn = GetScenario();
		var scoundrel = scn.GetLSCard("scoundrel");
		var han = scn.GetLSCard("han");
		var dining = scn.GetDSCard("dining");
		var vader = scn.GetDSCard("vader");
		var dsBlaster = scn.GetDSCard("dsBlaster");

		scn.StartGame();
		scn.MoveCardsToLSHand(scoundrel);
		scn.MoveLocationToTable(dining);
		scn.MoveCardsToLocation(dining, han, vader);
		scn.AttachCardsTo(vader, dsBlaster);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(dining);
		scn.PassBattleStartResponses();
		assertTrue(scn.AwaitingDSWeaponsSegmentActions());

		scn.PrepareDSDestiny(1);
		scn.DSUseCardAction(dsBlaster);
		scn.DSChooseCard(han);

		boolean saw = false;
		for (int i = 0; i < 20; i++) {
			var decision = scn.GetCurrentDecision();
			if (decision == null) break;
			try {
				if (scn.LSCardPlayAvailable(scoundrel)) {
					saw = true;
					int dsForceBefore = scn.GetDSLifeForceRemaining();
					scn.LSPlayCard(scoundrel);
					SafePassOptionalResponses(scn);
					try { scn.DSPayRemainingForceLossFromReserveDeck(); } catch (RuntimeException ignored) {}
					assertTrue("Scoundrel USED goes to Used Pile",
							scoundrel.getZone() == Zone.USED_PILE || scoundrel.getZone() == Zone.TOP_OF_USED_PILE);
					assertEquals("Opponent loses 2 Force when no retarget", dsForceBefore - 2, scn.GetDSLifeForceRemaining());
					break;
				}
			} catch (RuntimeException ignored) {
			}
			String text = decision.getText() != null ? decision.getText().toLowerCase() : "";
			if (text.contains("optional")) {
				scn.PassResponses("optional");
			} else if (text.contains("required")) {
				scn.PassResponses("required");
			} else {
				try { scn.PassAllResponses(); } catch (RuntimeException ex) { break; }
			}
		}
		assertTrue("Innocent Scoundrel should be offered when your gambler is targeted", saw);
	}
}
