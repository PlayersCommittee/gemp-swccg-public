package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_210_020_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("luke", "210_020"); //luke skywalker, the last jedi
                    put("farmboy_luke", "1_019");
					put("snowspeeder", "3_69"); //vehicle with permanent pilot
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
	public void LukeSkywalkerTheLastJediStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Luke Skywalker, The Last Jedi
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Character
		 * Subtype: Jedi Master
		 * Destiny: 4
		 * Deploy: 5
		 * Power: 5
		 * Ability: 7
		 * Forfeit: 8
		 * Icons: Pilot, Warrior, Episode VII, Virtual Set 10
		 * Persona: Luke
		 * Game Text: Never deploys to a site opponent occupies. Deploys -2 to Ahch-To. Once per turn,
         *      may take Force Projection into hand from Reserve Deck; reshuffle. During battle,
         *      may cancel an opponent's just drawn destiny to cause a re-draw. Immune to attrition.
		 * Lore:
		 * Set: Set 10
		 * Rarity: V
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("luke").getBlueprint();

		assertEquals("Luke Skywalker, The Last Jedi", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(4, card.getDestiny(), scn.epsilon);
		assertEquals(5, card.getDeployCost(), scn.epsilon);
		assertEquals(5, card.getPower(), scn.epsilon);
		assertEquals(7, card.getAbility(), scn.epsilon);
		assertEquals(8, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.JEDI_MASTER);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
			//null
		}});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
			add(Persona.LUKE);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.JEDI_MASTER);
			add(Icon.PILOT);
			add(Icon.WARRIOR);
			add(Icon.EPISODE_VII);
			add(Icon.VIRTUAL_SET_10);
		}});
		assertEquals(ExpansionSet.SET_10,card.getExpansionSet());
		assertEquals(Rarity.V,card.getRarity());
	}

	@Test
	public void LukeSkywalkerTheLastJediCannotDeployToOccupiedSite() {
		var scn = GetScenario();

		var luke = scn.GetLSCard("luke");
		var snowspeeder = scn.GetLSCard("snowspeeder");

        var stormtrooper1 = scn.GetDSFiller(1);
        var stormtrooper2 = scn.GetDSFiller(2);

        var site1 = scn.GetLSStartingLocation(); //interior
        var site2 = scn.GetDSStartingLocation(); //exterior

        scn.StartGame();

        scn.MoveCardsToLSHand(luke);
        scn.MoveCardsToDSHand(stormtrooper1,stormtrooper2);

        scn.SkipToLSTurn(); //lazy way to generate force
        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.GetLSForcePileCount() >= 5); //enough to deploy Luke
        assertTrue(scn.LSDeployAvailable(luke));

        scn.SkipToDSTurn();
        scn.MoveCardsToLocation(site1,stormtrooper1);
        scn.MoveCardsToLocation(site2,stormtrooper2);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.GetLSForcePileCount() >= 5); //enough to deploy Luke
        assertFalse(scn.LSDeployAvailable(luke)); //both possible locations occupied by opponent

        scn.SkipToDSTurn();
        scn.MoveCardsToLocation(site2,snowspeeder); //make sure cannot deploy to enclosed vehicle

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.GetLSForcePileCount() >= 5); //enough to deploy Luke
        assertFalse(scn.LSDeployAvailable(luke)); //both possible locations occupied by opponent
	}

    @Test
    public void LukeSkywalkerTheLastJediCannotPersonaReplaceAtOccupiedSite() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var farmboy_luke = scn.GetLSCard("farmboy_luke");

        var stormtrooper1 = scn.GetDSFiller(1);
        var stormtrooper2 = scn.GetDSFiller(2);

        var site1 = scn.GetLSStartingLocation();
        var site2 = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(luke);
        scn.MoveCardsToDSHand(stormtrooper1,stormtrooper2);

        scn.MoveCardsToLocation(site1,farmboy_luke);
        scn.MoveCardsToLocation(site2,stormtrooper2);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(luke));

        scn.SkipToDSTurn();
        scn.MoveCardsToLocation(site1,stormtrooper1);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(luke)); //both possible locations occupied by opponent
    }

}
