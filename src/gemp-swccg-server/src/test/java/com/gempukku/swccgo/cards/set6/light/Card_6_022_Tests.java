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
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;


import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Card_6_022_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("kalit", "6_022");
					put("siesta1", "1_051"); //jawa siesta
                    put("siesta2", "1_051"); //jawa siesta
                    put("rebeltrooper", "1_028");
					put("cantina", "1_128");  //tatooine: cantina
				}},
				new HashMap<>()
				{{
					put("stunning_leader", "2_140");
                    put("stormtrooper", "1_194");
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
	public void KalitStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Kalit
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Character
		 * Subtype: Alien
		 * Destiny: 2
		 * Deploy: 3
		 * Power: 2
		 * Ability: 2
		 * Forfeit: 3
		 * Icons: Jabba's Palace
		 * Persona: none
		 * Game Text: Deploys only on Tatooine. Your Jawa Siesta is not unique (•), is doubled, deploys free
         *      (or for 6 Force from each player) and cumulatively affects your Jawas' forfeit. While at Audience
         *      Chamber or Jawa Camp, all your other Jawas are power +2.
		 * Lore: Jawa leader. Seeking to peacefully settle a long standing disagreement with his rival, Wittin.
         *      Wants Jabba to mediate their talks.
		 * Set: Jabba's Palace
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("kalit").getBlueprint();

		assertEquals("Kalit", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(2, card.getDestiny(), scn.epsilon);
		assertEquals(3, card.getDeployCost(), scn.epsilon);
		assertEquals(2, card.getPower(), scn.epsilon);
		assertEquals(2, card.getAbility(), scn.epsilon);
		assertEquals(3, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.ALIEN);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
		}});
        assertEquals(Species.JAWA,card.getSpecies());
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            //null
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.ALIEN);
			add(Icon.JABBAS_PALACE);
		}});
		assertEquals(ExpansionSet.JABBAS_PALACE,card.getExpansionSet());
		assertEquals(Rarity.R,card.getRarity());
	}

	@Test
	public void KalitMakesJawaSiestaNotUnique() {
		var scn = GetScenario();

		var kalit = scn.GetLSCard("kalit");
		var cantina = scn.GetLSCard("cantina");

		var siesta1 = scn.GetLSCard("siesta1");
        var siesta2 = scn.GetLSCard("siesta2");

		scn.StartGame();

		scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLSSideOfTable(siesta1);

        scn.MoveCardsToLSHand(kalit,siesta2);

        assertSame(Zone.SIDE_OF_TABLE,siesta1.getZone());
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.GetDSForcePileCount() >= 3); //enough to deploy siesta2
        assertTrue(scn.GetLSForcePileCount() >= 3);
        assertFalse(scn.LSCardPlayAvailable(siesta2)); // ---- currently siesta is unique

        assertTrue(scn.LSCardPlayAvailable(kalit));
        scn.LSDeployCard(kalit);
        scn.LSChooseCard(cantina);
        scn.PassAllResponses();
        assertAtLocation(cantina,kalit);

        scn.DSPass();
        assertTrue(scn.LSCardPlayAvailable(siesta2)); // ---- siesta is no longer unique
        scn.LSDeployCard(siesta2); //(can deploy for free)
        scn.PassAllResponses();
        scn.DSPass();

        assertSame(Zone.SIDE_OF_TABLE,siesta1.getZone());
        assertSame(Zone.SIDE_OF_TABLE,siesta2.getZone());
    }

    @Test
    public void KalitExcludedKeepsJawaSiestaNotUnique() {
        //confirm Kalit obeys Excluded From Battle - special rules exception:
        //"being excluded will not cause ... other cards to be canceled or otherwise removed from table"
        var scn = GetScenario();

        var kalit = scn.GetLSCard("kalit");
        var rebeltrooper = scn.GetLSCard("rebeltrooper");
        var cantina = scn.GetLSCard("cantina");

        var stormtrooper = scn.GetDSCard("stormtrooper");
        var stunning_leader = scn.GetDSCard("stunning_leader");

        var siesta1 = scn.GetLSCard("siesta1");
        var siesta2 = scn.GetLSCard("siesta2");

        scn.StartGame();

        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina,kalit,rebeltrooper,stormtrooper);
        scn.MoveCardsToLSSideOfTable(siesta1,siesta2);

        scn.MoveCardsToDSHand(stunning_leader);

        assertSame(Zone.SIDE_OF_TABLE,siesta1.getZone()); //both siesta on table before battle
        assertSame(Zone.SIDE_OF_TABLE,siesta2.getZone());

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.GetDSForcePileCount() >= 1); //enough to play stunning leader
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to battle

        assertTrue(scn.LSCanInitiateBattle());
        scn.LSInitiateBattle(cantina);

        assertTrue(scn.DSCardPlayAvailable(stunning_leader));
        scn.DSPlayCard(stunning_leader); //exclude kalit
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.IsParticipatingInBattle(kalit));
        scn.PassWeaponsSegmentActions();
        scn.PassDamageSegmentActions(); //tie - no battle damage
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSBattlePhaseActions()); //battle finished

        assertSame(Zone.SIDE_OF_TABLE,siesta1.getZone()); //both siesta stayed on table through kalit being excluded
        assertSame(Zone.SIDE_OF_TABLE,siesta2.getZone());
    }
}
