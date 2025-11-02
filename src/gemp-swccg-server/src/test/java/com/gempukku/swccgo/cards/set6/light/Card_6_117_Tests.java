package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
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

public class Card_6_117_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("rebeltrooper","1_028");
				}},
				new HashMap<>()
				{{
					put("nysad", "6_117");
                    put("jp_site", "6_162"); //audience chamber
                    put("tat_site", "1_292"); //non-jp tatooine site (jawa camp)
                    put("hoth_site","3_144"); //non-tat site
                    put("bantha", "1_307"); //non-enclosed vehicle
					put("walker", "3_157"); //enclosed vehicle
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
	public void NysadStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Nysad
		 * Uniqueness: Unique
		 * Side: Dark
		 * Type: Character
		 * Subtype: Alien
		 * Destiny: 3
		 * Game Text: Deploys only on Tatooine. May deploy as a 'react' to a Jabba's Palace site or aboard a vehicle
         *      at a Tatooine site. Power +3 when defending a battle at a Jabba's Palace site.
		 * Lore: Kajain'sa'Nikto. Fiercely loyal to Jabba. Stands guard over the sail barge during the Hutt's many
         *      trips to Mos Eisley.
		 * Set: Jabba's Palace
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("nysad").getBlueprint();

        assertEquals("Nysad", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertFalse(card.hasAlternateImageSuffix());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GUARD);
        }});
        assertEquals(Species.NIKTO,card.getSpecies());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            //null
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.JABBAS_PALACE);
        }});
        assertEquals(ExpansionSet.JABBAS_PALACE,card.getExpansionSet());
        assertEquals(Rarity.R,card.getRarity());
	}

    @Test
	public void NysadDeploysOnlyOnTatooine() {
        //test coverage:
        // test1: can deploy to a Tatooine site
        // test2: cannot deploy to a non-Tatooine site
		var scn = GetScenario();

		var tat_site = scn.GetDSCard("tat_site");
        var hoth_site = scn.GetDSCard("hoth_site");
		var nysad = scn.GetDSCard("nysad");

		scn.StartGame();

        scn.MoveLocationToTable(tat_site);
        scn.MoveLocationToTable(hoth_site);

        scn.MoveCardsToHand(nysad);

		scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.GetDSForcePileCount() >= nysad.getBlueprint().getDeployCost()); //enough to deploy
        assertTrue(scn.DSDeployAvailable(nysad));
        scn.DSDeployCard(nysad);
        assertTrue(scn.DSHasCardChoicesAvailable(tat_site)); //test1
        assertFalse(scn.DSHasCardChoicesAvailable(hoth_site)); //test2
        scn.DSChooseCard(tat_site);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(tat_site,nysad));
	}

    @Test
    public void NysadDeployAsReactToJP() {
        //test coverage:
        // test1: can deploy as react to JP site
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSCard("rebeltrooper");

        var jp_site = scn.GetDSCard("jp_site");
        var nysad = scn.GetDSCard("nysad");

        scn.StartGame();

        scn.MoveLocationToTable(jp_site);
        scn.MoveCardsToLocation(jp_site,rebeltrooper);

        scn.MoveCardsToHand(nysad);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetDSForcePileCount() >= nysad.getBlueprint().getDeployCost()); //enough to deploy
        scn.LSForceDrainAt(jp_site);
        assertTrue(scn.DSCardPlayAvailable(nysad)); //test1 (react to JP available)
        scn.DSPlayCard(nysad);
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoicesAvailable(jp_site));
        scn.DSChooseCards(jp_site);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(jp_site,nysad)); //test1 (successfully react deployed to JP)
    }

    @Test
    public void NysadNoDeployAsReactToNonJP() {
        //test coverage:
        // test1: cannot deploy as react to non-JP site
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSCard("rebeltrooper");

        var tat_site = scn.GetDSCard("tat_site");
        var nysad = scn.GetDSCard("nysad");

        scn.StartGame();

        scn.MoveLocationToTable(tat_site);
        scn.MoveCardsToLocation(tat_site,rebeltrooper);

        scn.MoveCardsToHand(nysad);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetDSForcePileCount() >= nysad.getBlueprint().getDeployCost()); //enough to deploy
        scn.LSForceDrainAt(tat_site);
        assertFalse(scn.DSCardPlayAvailable(nysad)); //test1 (no react to JP available)
    }

    @Test @Ignore
    public void NysadDeployAsReactToVehicleAtTat() {
        //shows bug described in https://github.com/PlayersCommittee/gemp-swccg-public/issues/315

        //test coverage:
        // test1: can deploy as react to enclosed vehicle at non-JP site
        // test2: can deploy as react to non-enclosed vehicle at non-JP site
        // test3: (CURRENTLY FAILS) cannot deploy as a react to tatooine site with a vehicle present
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSCard("rebeltrooper");

        var tat_site = scn.GetDSCard("tat_site");
        var nysad = scn.GetDSCard("nysad");
        var walker = scn.GetDSCard("walker");
        var bantha = scn.GetDSCard("bantha");

        scn.StartGame();

        scn.MoveLocationToTable(tat_site);
        scn.MoveCardsToLocation(tat_site,rebeltrooper,walker,bantha);

        scn.MoveCardsToHand(nysad);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetDSForcePileCount() >= nysad.getBlueprint().getDeployCost()); //enough to deploy
        scn.LSForceDrainAt(tat_site);
        assertTrue(scn.DSCardPlayAvailable(nysad)); //test1 (react to vehicle at tatooine available)
        scn.DSPlayCard(nysad);
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoicesAvailable(walker)); //test1 (react deploy to enclosed vehicle)
        assertTrue(scn.DSHasCardChoicesAvailable(bantha)); //test2 (react deploy to non-enclosed vehicle)
        assertFalse(scn.DSHasCardChoicesAvailable(tat_site)); //test3 (CURRENTLY FAILS) (cannot react deploy to site)
        scn.DSChooseCards(walker); //(passenger)
        scn.PassAllResponses();
        assertFalse(scn.CardsAtLocation(tat_site,nysad));
    }

    @Test
    public void NysadNoDeployAsReactToVehicleAtNonTat() {
        //test coverage:
        // test1: cannot deploy as react to vehicle at non-tatooine site
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSCard("rebeltrooper");

        var hoth_site = scn.GetDSCard("hoth_site");
        var nysad = scn.GetDSCard("nysad");
        var walker = scn.GetDSCard("walker");

        scn.StartGame();

        scn.MoveLocationToTable(hoth_site);
        scn.MoveCardsToLocation(hoth_site,rebeltrooper,walker);

        scn.MoveCardsToHand(nysad);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetDSForcePileCount() >= nysad.getBlueprint().getDeployCost()); //enough to deploy
        scn.LSForceDrainAt(hoth_site);
        assertFalse(scn.DSCardPlayAvailable(nysad)); //test1 (no react to vehicle at non-tatooine)
    }

    @Test
    public void NysadPowerBonusDefendingAtJP() {
        //test coverage:
        // test1: no power bonus attacking battle at JP site
        // test2: +3 power bonus defending battle at JP site
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSCard("rebeltrooper");

        var jp_site = scn.GetDSCard("jp_site");
        var nysad = scn.GetDSCard("nysad");

        int nysadBasePower = 2;

        scn.StartGame();

        scn.MoveLocationToTable(jp_site);
        scn.MoveCardsToLocation(jp_site,rebeltrooper,nysad);

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(jp_site);
        scn.SkipToEndOfPowerSegment(false);
        assertEquals(nysadBasePower,scn.GetDSTotalPower()); //test1
        scn.SkipToDamageSegment(false);
        scn.LSPayBattleDamageFromReserveDeck();
        assertTrue(scn.AwaitingLSBattlePhaseActions());

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle());
        scn.LSInitiateBattle(jp_site);
        scn.SkipToEndOfPowerSegment(false);
        assertEquals(nysadBasePower + 3,scn.GetDSTotalPower()); //test2
    }

    @Test
    public void NysadNoPowerBonusDefendingAtNonJP() {
        //test coverage:
        // test1: no power bonus defending battle at non-JP site
        var scn = GetScenario();

        var rebeltrooper = scn.GetLSCard("rebeltrooper");

        var tat_site = scn.GetDSCard("tat_site");
        var nysad = scn.GetDSCard("nysad");

        int nysadBasePower = 2;

        scn.StartGame();

        scn.MoveLocationToTable(tat_site);
        scn.MoveCardsToLocation(tat_site,rebeltrooper,nysad);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle());
        scn.LSInitiateBattle(tat_site);
        scn.SkipToEndOfPowerSegment(false);
        assertEquals(nysadBasePower,scn.GetDSTotalPower()); //test1
    }
}
