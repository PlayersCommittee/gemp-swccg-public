package com.gempukku.swccgo.rules.replacement;

import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertNotAtLocation;
import static org.junit.Assert.*;

public class PersonaReplacementTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("farmboy_luke", "1_019");
					put("hoth_luke", "3_003");
					put("scout_luke", "10_010");
					put("hoth_site","3_059");
				}},
				new HashMap<>()
				{{
					put("vader", "1_168");
					put("chokevader", "7_175");
                    put("palp", "9_109");
                    put("palp_forseer", "205_012");
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
	public void PersonaReplaceNormalSucceeds() {
		//verifies:
		//persona replace works for matching persona, no deploy restriction, >= power and ability
		//persona replacement costs no force
		//replaced persona goes to lost pile

		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var farmboy_luke = scn.GetLSCard("farmboy_luke");
		var scout_luke = scn.GetLSCard("scout_luke");

		scn.StartGame();

		scn.MoveCardsToLSHand(scout_luke);

		scn.MoveCardsToLocation(site, farmboy_luke);

		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(farmboy_luke.getBlueprint().hasPersona(Persona.LUKE)); //matching personas
		assertTrue(scout_luke.getBlueprint().hasPersona(Persona.LUKE));

		assertTrue(scout_luke.getBlueprint().getPower() >= farmboy_luke.getBlueprint().getPower() );
		assertTrue(scout_luke.getBlueprint().getAbility() >= farmboy_luke.getBlueprint().getAbility() );

		assertEquals(4,scn.GetLSForcePileCount());
		assertTrue(scn.LSCardPlayAvailable(scout_luke));
		scn.LSPlayCard(scout_luke);
		assertTrue(scn.LSHasCardChoiceAvailable(farmboy_luke)); //target to replace
		scn.LSChooseCard(farmboy_luke);
		scn.PassAllResponses();
		assertEquals(4,scn.GetLSForcePileCount()); //no deploy cost to replace

		assertTrue(scn.CardsAtLocation(site,scout_luke));
		assertFalse(scn.CardsAtLocation(site,farmboy_luke));
		assertEquals(farmboy_luke,scn.GetTopOfLSLostPile()); //replaced character sent lost
	}

	@Test
	public void PersonaReplacePreventedByDeployRestrictions() {
		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var farmboy_luke = scn.GetLSCard("farmboy_luke");
		var hoth_luke = scn.GetLSCard("hoth_luke");
		var scout_luke = scn.GetLSCard("scout_luke");

		scn.StartGame();

		scn.MoveCardsToLSHand(scout_luke);

		scn.MoveCardsToLocation(site, farmboy_luke);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSCardPlayAvailable(scout_luke));
		assertFalse(scn.LSCardPlayAvailable(hoth_luke)); //may only deploy on hoth
	}

    @Test @Ignore
    public void PersonaReplacePreventedByNeverDeploysRestrictions() {
        //demonstrates bug https://github.com/PlayersCommittee/gemp-swccg-public/issues/890

        var scn = GetScenario();

        var site = scn.GetLSStartingLocation();

        var palp = scn.GetDSCard("palp");
        var palp_forseer = scn.GetDSCard("palp_forseer");

        scn.StartGame();

        scn.MoveCardsToDSHand(palp_forseer);

        scn.MoveCardsToLocation(site, palp);

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(palp_forseer));
    }

    @Test
	public void PersonaReplacePreventedByLowerPower() {
		var scn = GetScenario();

		var hoth_site = scn.GetLSCard("hoth_site");

		var farmboy_luke = scn.GetLSCard("farmboy_luke");
		var hoth_luke = scn.GetLSCard("hoth_luke");
		var scout_luke = scn.GetLSCard("scout_luke");

		scn.StartGame();

		scn.MoveCardsToLSHand(scout_luke, farmboy_luke);

		scn.MoveLocationToTable(hoth_site);
		scn.MoveCardsToLocation(hoth_site, hoth_luke);

		assertFalse(farmboy_luke.getBlueprint().getPower() >= hoth_luke.getBlueprint().getPower() ); //not eligible for persona replace
		assertTrue(farmboy_luke.getBlueprint().getAbility() >= hoth_luke.getBlueprint().getAbility() );

		assertTrue(scout_luke.getBlueprint().getPower() >= hoth_luke.getBlueprint().getPower() );
		assertTrue(scout_luke.getBlueprint().getAbility() >= hoth_luke.getBlueprint().getAbility() );

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSCardPlayAvailable(scout_luke));
		assertFalse(scn.LSCardPlayAvailable(farmboy_luke)); //due to lower power
	}

	@Test
	public void PersonaReplaceEscortTransfersCaptive() {
		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var farmboy_luke = scn.GetLSCard("farmboy_luke");

		var vader = scn.GetDSCard("vader");
		var chokevader = scn.GetDSCard("chokevader");

		scn.StartGame();

		scn.MoveCardsToDSHand(chokevader);

		scn.MoveCardsToLocation(site, vader, farmboy_luke);

		scn.CaptureCardWith(vader, farmboy_luke);

		assertTrue(farmboy_luke.isCaptive());
		assertEquals(vader, farmboy_luke.getEscort());
		assertEquals(vader, farmboy_luke.getAttachedTo());
		assertTrue(vader.getCardsEscorting().contains(farmboy_luke));

		scn.SkipToPhase(Phase.DEPLOY);
		assertTrue(scn.DSCardPlayAvailable(chokevader));

		scn.DSPlayCard(chokevader);
		assertTrue(scn.DSHasCardChoiceAvailable(vader)); //target to persona replace
		scn.DSChooseCard(vader);
		scn.PassAllResponses();

		assertAtLocation(site,chokevader);
		assertNotAtLocation(site,vader);
		assertNotAtLocation(site,farmboy_luke); //because captive

		assertTrue(farmboy_luke.isCaptive());

		assertNotEquals(vader, farmboy_luke.getEscort());
		assertNotEquals(vader, farmboy_luke.getAttachedTo());
		assertFalse(vader.getCardsEscorting().contains(farmboy_luke));

		assertEquals(chokevader, farmboy_luke.getEscort());
		assertEquals(chokevader, farmboy_luke.getAttachedTo());
		assertTrue(chokevader.getCardsEscorting().contains(farmboy_luke));

		assertEquals(1,scn.GetDSLostPileCount()); //replaced vader
		assertEquals(0,scn.GetLSLostPileCount());
	}

	//other tests to add
	//PersonaReplacePreventedByLowerAbility

	//PersonaReplacePreventedByLowerPowerModifiers

	//transfer of effects, weapons, devices

	//check that new text has been applied correctly to transferred content (ex: removes effects that new persona is immune to)
}
