package com.gempukku.swccgo.rules.replacement;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class CharacterConversionTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("ls_lando", "5_005");
					put("leia","1_017");
				}},
				new HashMap<>()
				{{
					put("ds_lando", "5_099");
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
	public void ConvertDSLandoToLS_ReplacedCharacterLost() {
		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var ds_lando = scn.GetDSCard("ds_lando");

		var ls_lando = scn.GetLSCard("ls_lando");

		scn.StartGame();

		scn.MoveCardsToLSHand(ls_lando);

		scn.MoveCardsToLocation(site, ds_lando);

		scn.SkipToLSTurn(Phase.DEPLOY);

		assertEquals(3,scn.GetLSForcePileCount());
		assertTrue(scn.LSCardPlayAvailable(ls_lando));
		scn.LSPlayCard(ls_lando);
		assertTrue(scn.LSHasCardChoiceAvailable(ds_lando)); //target to replace
		scn.LSChooseCard(ds_lando);
		scn.PassAllResponses();
		assertEquals(3,scn.GetLSForcePileCount()); //no deploy cost to replace

		assertTrue(scn.CardsAtLocation(site,ls_lando));
		assertFalse(scn.CardsAtLocation(site,ds_lando));
		assertEquals(ds_lando,scn.GetTopOfDSLostPile()); //replaced character sent lost
	}

	@Test
	public void ConvertDSLandoToLS_CaptiveReleased() {
		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var ds_lando = scn.GetDSCard("ds_lando");

		var ls_lando = scn.GetLSCard("ls_lando");
		var leia = scn.GetLSCard("leia");

		scn.StartGame();

		scn.MoveCardsToLSHand(ls_lando);

		scn.MoveCardsToLocation(site, ds_lando, leia);

		scn.CaptureCardWith(ds_lando, leia);
		assertTrue(leia.isCaptive());
		assertEquals(ds_lando, leia.getEscort());
		assertEquals(ds_lando, leia.getAttachedTo());
		assertTrue(ds_lando.getCardsEscorting().contains(leia));

		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(scn.LSCardPlayAvailable(ls_lando));
		scn.LSPlayCard(ls_lando);
		assertTrue(scn.LSHasCardChoiceAvailable(ds_lando)); //target to replace
		scn.LSChooseCard(ds_lando);
		scn.LSChoiceAvailable("Escape");
		scn.LSChoiceAvailable("Rally");
		scn.LSChoose("Rally");
		scn.PassAllResponses();

		assertFalse(leia.isCaptive());
		assertEquals(null, leia.getEscort());
		assertEquals(null, leia.getAttachedTo());
		assertFalse(ds_lando.getCardsEscorting().contains(leia));

		assertTrue(scn.CardsAtLocation(site,ls_lando, leia));
		assertFalse(scn.CardsAtLocation(site,ds_lando));
		assertEquals(ds_lando,scn.GetTopOfDSLostPile()); //replaced character sent lost
	}

	//other tests to add:

	//attached effects transfer (disarmed?)
	//attached weapons transfer and change ownership
	//attached devices transfer and change ownership

}
