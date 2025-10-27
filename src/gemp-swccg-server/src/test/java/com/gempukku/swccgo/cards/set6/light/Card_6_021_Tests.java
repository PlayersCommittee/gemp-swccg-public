package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_6_021_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("jess", "6_021");
					put("chewie", "2_003"); //ls alien - ability 2
                    put("corellian", "2_006"); //ls female alien - ability 1
				}},
				new HashMap<>()
				{{
					put("drE", "1_172"); //ds alien - ability 2
                    put("dannik", "2_084"); //ds alien - ability 3
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
	public void JessStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Jess
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Character
		 * Subtype: Alien
		 * Destiny: 1
		 * Deploy: 3
		 * Power: 1
		 * Ability: 2
		 * Forfeit: 3
		 * Icons: Warrior, Jabba's Palace
		 * Persona:
         * Keywords: Musician, Female
		 * Game Text: May retrieve 1 Force each time you deploy a musician to same site. During your turn,
         *      may use 1 Force to 'charm' one male alien of ability < 3 present; that male is forfeit = 0
         *      for remainder of turn.
		 * Lore: Popular musician. Often seen with Bib Fortuna. Captivates those around her.
         *      Hoping to join a band and leave Jabba's palace.
		 * Set: Jabba's Palace
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("jess").getBlueprint();

		assertEquals("Jess", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(3, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertEquals(2, card.getAbility(), scn.epsilon);
		assertEquals(3, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.ALIEN);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
			add(Keyword.MUSICIAN);
            add(Keyword.FEMALE);
		}});
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
	public void JessCanCharmMaleAlienWithAbilityLessThan3() {
		var scn = GetScenario();

		var jess = scn.GetLSCard("jess");
		var chewie = scn.GetLSCard("chewie");
        var site = scn.GetLSStartingLocation();

		var drE = scn.GetDSCard("drE");

		scn.StartGame();

		scn.MoveCardsToLocation(site, jess, chewie,drE);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to use game text
        assertTrue(scn.LSCardActionAvailable(jess)); //can use text during self's turn
        scn.LSUseCardAction(jess);
        assertTrue(scn.LSHasCardChoiceAvailable(drE)); //can target opponent male alien, ability < 3
        assertTrue(scn.LSHasCardChoiceAvailable(chewie)); //can target self's male alien, ability < 3
	}

    @Test
    public void JessCannotCharmDuringOpponentTurn() {
        var scn = GetScenario();

        var jess = scn.GetLSCard("jess");
        var chewie = scn.GetLSCard("chewie");
        var site = scn.GetLSStartingLocation();

        var drE = scn.GetDSCard("drE");

        scn.StartGame();

        scn.MoveCardsToLocation(site, jess, chewie,drE);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to use game text
        assertFalse(scn.LSCardActionAvailable(jess)); //cannot use text during opponent's turn
    }

    @Test
    public void JessCannotCharmAtDifferentSite() {
        var scn = GetScenario();

        var jess = scn.GetLSCard("jess");
        var chewie = scn.GetLSCard("chewie");
        var site = scn.GetLSStartingLocation();

        var drE = scn.GetDSCard("drE");
        var site2 = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, jess, chewie);
        scn.MoveCardsToLocation(site2,drE);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to use game text
        assertTrue(scn.LSCardActionAvailable(jess)); //can use text during self's turn
        scn.LSUseCardAction(jess);
        assertFalse(scn.LSHasCardChoiceAvailable(drE)); //cannot target opponent male alien, ability < 3 at dif. site
        assertTrue(scn.LSHasCardChoiceAvailable(chewie)); //can target self's male alien, ability < 3
    }

    @Test
    public void JessCannotCharmFemale() {
        var scn = GetScenario();

        var jess = scn.GetLSCard("jess");
        var corellian = scn.GetLSCard("corellian");
        var site = scn.GetLSStartingLocation();

        var drE = scn.GetDSCard("drE");

        scn.StartGame();

        scn.MoveCardsToLocation(site, jess, corellian,drE);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to use game text
        assertTrue(scn.LSCardActionAvailable(jess)); //can use text during self's turn
        scn.LSUseCardAction(jess);
        assertTrue(scn.LSHasCardChoiceAvailable(drE)); //can target opponent male alien, ability < 3
        assertFalse(scn.LSHasCardChoiceAvailable(corellian)); //can target female alien, ability < 3
    }

    @Test
    public void JessCannotCharmMaleAlienWithAbility3() {
        var scn = GetScenario();

        var jess = scn.GetLSCard("jess");
        var chewie = scn.GetLSCard("chewie");
        var site = scn.GetLSStartingLocation();

        var dannik = scn.GetDSCard("dannik");

        scn.StartGame();

        scn.MoveCardsToLocation(site, jess, chewie,dannik);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to use game text
        assertTrue(scn.LSCardActionAvailable(jess)); //can use text during self's turn
        scn.LSUseCardAction(jess);
        assertFalse(scn.LSHasCardChoiceAvailable(dannik)); //cannot target opponent male alien, ability 3
        assertTrue(scn.LSHasCardChoiceAvailable(chewie)); //can target male alien, ability < 3
    }


    @Test
    public void JessCannotCharmMaleNonAlienWithAbilityLessThan3() {
        var scn = GetScenario();

        var jess = scn.GetLSCard("jess");
        var chewie = scn.GetLSCard("chewie");
        var site = scn.GetLSStartingLocation();

        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveCardsToLocation(site, jess, chewie,stormtrooper);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to use game text
        assertTrue(scn.LSCardActionAvailable(jess)); //can use text during self's turn
        scn.LSUseCardAction(jess);
        assertFalse(scn.LSHasCardChoiceAvailable(stormtrooper)); //cannot target opponent male non-alien, ability < 3
        assertTrue(scn.LSHasCardChoiceAvailable(chewie)); //can target male alien, ability < 3
    }

    //add more tests:
    //forfeit reduction works
    //forfeit reduction lasts until end of turn
    //forfeit reduction does not last into next turn
    //forfeit reduction can only be used once per turn
    //jess may self-retrieve
    //jess may retrieve when your musician is deployed to same site
    //jess may not retrieve when your non-musician is deployed to same site
    //jess may not retrieve when your musician is deployed to different site
    //jess may not retrieve when opponent's musician is deployed to same site
}
