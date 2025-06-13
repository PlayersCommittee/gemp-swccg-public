package com.gempukku.swccgo.rules.state;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class SiteAdjacencyTests
{
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("yt1300", "8_80"); //YT-1300
					put("sandcrawler", "1_150");
					put("loading", "2_066"); //Sandcrawler: Loading Bay
					put("casino", "7_111"); //Cloud City: Casino
				}},
				new HashMap<>()
				{{
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
	public void VehicleSiteForVehicleLoadedIntoStarshipBayIsRelatedToSitesFreighterIsAtAndAdjacentWhenDisembarked() {
		var scn = GetScenario();

		var casino = scn.GetLSCard("casino");

		var yt1300 = scn.GetLSCard("yt1300");
		var sandcrawler = scn.GetLSCard("sandcrawler");
		var loading = scn.GetLSCard("loading");

		var marketplace = scn.GetDSStartingLocation();

		scn.MoveCardsToHand(loading);

		scn.StartGame();

		scn.MoveLocationToTable(casino);
		scn.MoveCardsToLocation(marketplace, sandcrawler, yt1300);

		scn.SkipToLSTurn(Phase.DEPLOY);

		scn.LSDeployCard(loading);
		scn.PassAllResponses();

		assertTrue(scn.IsAdjacentTo(marketplace, loading));
		assertTrue(scn.IsAdjacentTo(loading, marketplace));

		assertTrue(scn.IsRelatedTo(loading, marketplace));
		assertFalse(scn.IsRelatedTo(marketplace, loading));

		scn.SkipToPhase(Phase.MOVE);

		assertTrue(scn.LSActionAvailable("Embark"));
		scn.LSChooseAction("Embark");
		scn.PassAllResponses();

		assertEquals(yt1300, sandcrawler.getAttachedTo());

		assertFalse(scn.IsAdjacentTo(marketplace, loading));
		assertFalse(scn.IsAdjacentTo(loading, marketplace));

		assertTrue(scn.IsRelatedTo(loading, marketplace));
		assertFalse(scn.IsRelatedTo(marketplace, loading));

		scn.MoveCardsToLocation(casino, yt1300);
		scn.DSPass();

		assertFalse(scn.IsAdjacentTo(casino, loading));
		assertFalse(scn.IsAdjacentTo(loading, casino));

		assertTrue(scn.IsRelatedTo(loading, casino));
		assertFalse(scn.IsRelatedTo(casino, loading));

		assertTrue(scn.LSActionAvailable("Disembark"));
		scn.LSChooseAction("Disembark");
		scn.PassAllResponses();

		assertTrue(scn.IsAdjacentTo(casino, loading));
		assertTrue(scn.IsAdjacentTo(loading, casino));
	}

}

