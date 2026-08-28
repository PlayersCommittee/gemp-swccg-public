package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Corellian Engineering Corporation (7_56) gives your Quad Laser Cannons DeploysFreeModifier
 * while attached to Corellia. Frustration must treat that always-free deploy as not a cost.
 */
public class Card_4_142_CEC_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap()
				{{
					put("trooper", "1_28");
					put("quads", "1_159");
					put("cec", "7_56");
					put("corellia", "2_61");
				}},
				new HashMap()
				{{
					put("frustration", "4_142");
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

	private void PeekFrustrationAtHand(VirtualTableScenario scn) {
		scn.SkipToPhase(Phase.CONTROL);
		scn.DSPlayCard(scn.GetDSCard("frustration"));
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}
	}

	@Test
	public void FrustrationCanTargetQuadLaserCannonsWithoutCEC() {
		// Quads printed cost 2 via DefinedByGameTextDeployCostModifier.
		// Default systems 2 LS icons + Corellia 1 = 3, so 2 is less than 3.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var quads = scn.GetLSCard("quads");
		var trooper = scn.GetLSCard("trooper");
		var corellia = scn.GetLSCard("corellia");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(quads, trooper);

		scn.StartGame();
		scn.MoveLocationToTable(corellia);

		PeekFrustrationAtHand(scn);

		assertTrue(scn.DSHasCardChoiceAvailable(quads));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}

	@Test
	public void FrustrationCannotTargetQuadLaserCannonsWhenCECIsAttachedToCorellia() {
		// Same 3 Light icons. CEC on Corellia makes Quads deploy free. Free is not a deploy cost.
		var scn = GetScenario();
		var frustration = scn.GetDSCard("frustration");
		var quads = scn.GetLSCard("quads");
		var trooper = scn.GetLSCard("trooper");
		var cec = scn.GetLSCard("cec");
		var corellia = scn.GetLSCard("corellia");

		scn.MoveCardsToDSHand(frustration);
		scn.MoveCardsToLSHand(quads, trooper, cec);

		scn.StartGame();
		scn.MoveLocationToTable(corellia);
		scn.LSActivateForceCheat(5);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(cec) || scn.LSCardPlayAvailable(cec));
		scn.LSDeployCardAndPassResponses(cec, corellia);
		assertEquals(Zone.ATTACHED, cec.getZone());

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(frustration) || scn.DSCardActionAvailable(frustration));
		scn.DSPlayCard(frustration);
		scn.PassAllResponses();
		if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().toLowerCase().contains("hand")) {
			scn.DSDismissRevealedCards();
		}

		assertTrue(scn.DSHasCardChoiceNotAvailable(quads));
		assertTrue(scn.DSHasCardChoiceAvailable(trooper));
	}
}
