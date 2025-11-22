package com.gempukku.swccgo.rules.react;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReactAwayTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("arcona", "2_001");
					put("planetary", "13_038"); //planetary defenses
                    put("cantina","1_128");
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
	public void ReactAwayFromBattle() {
		var scn = GetScenario();

		var arcona = scn.GetLSCard("arcona");
        var cantina = scn.GetLSCard("cantina");

        var adjacentsite = scn.GetDSStartingLocation(); //tatooine site
        var trooper = scn.GetDSFiller(1);

		scn.StartGame();

        scn.MoveLocationToTable(cantina);

        scn.MoveCardsToLocation(cantina, arcona, trooper);

        scn.AttachCardsTo(cantina);

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle());
        assertTrue(scn.GetLSForcePileCount() >= 1); //can pay move cost to react away
        scn.DSInitiateBattle(cantina);
        assertTrue(scn.LSCardActionAvailable(arcona)); //eligible to react away
        scn.LSUseCardAction(arcona);
        assertTrue(scn.LSHasCardChoiceAvailable(adjacentsite));
        scn.LSChooseCard(adjacentsite);

        scn.PassAllResponses();
        assertAtLocation(adjacentsite,arcona);
        assertTrue(scn.AwaitingLSBattlePhaseActions());
	}

    //demonstrates https://github.com/PlayersCommittee/gemp-swccg-public/issues/891
    @Test
    public void ReactAwayFromBattleWithDefensiveShield() {
        var scn = GetScenario();

        var arcona = scn.GetLSCard("arcona");
        var planetary = scn.GetLSCard("planetary");
        var cantina = scn.GetLSCard("cantina");

        var adjacentsite = scn.GetDSStartingLocation(); //tatooine site
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(cantina);

        scn.MoveCardsToLocation(cantina, arcona, trooper);
        scn.AttachCardsTo(cantina,planetary);

        assertTrue(scn.IsAttachedTo(cantina,planetary));

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(cantina);
        scn.LSUseCardAction(arcona);
        scn.LSChooseCard(adjacentsite);

        scn.DSPass();
        scn.LSPass();

    }
}
