package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.GoMissingEffect;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static com.gempukku.swccgo.framework.Assertions.assertNotAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Card_6_061_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("deflection", "6_61");
					put("luke", "108_003"); //luke with saber
                    put("rebeltrooper","1_028");
				}},
				new HashMap<>()
				{{
					put("stormtrooper1", "1_194");
                    put("stormtrooper2", "1_194");
					put("blaster", "1_317");
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
	public void BlasterDeflectionStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Blaster Deflection
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Interrupt
		 * Subtype: Used or Lost
		 * Destiny: 4
		 * Game Text: USED: Cancel an attempt to use a character weapon to target your character of ability > 4.
         *      LOST: If your character of ability > 4 with a lightsaber was just targeted by a blaster,
         *      use 3 Force to re-target that blaster to an opponent's character present.
		 * Lore: A Jedi can anticipate the actions of his opponent and let the Force control his actions,
         *      causing him to effortlessly deflect an opponent's attacks.
		 * Set: Jabba's Palace
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("deflection").getBlueprint();

		assertEquals("Blaster Deflection", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
		assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
		assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.JABBAS_PALACE);
        }});
        assertEquals(ExpansionSet.JABBAS_PALACE,card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
	}

	@Test
	public void BlasterDeflectionUsedCancelsTargeting() {
		var scn = GetScenario();

		var deflection = scn.GetLSCard("deflection");
		var luke = scn.GetLSCard("luke");
		scn.MoveCardsToHand(deflection);

		var site = scn.GetLSStartingLocation();

		var stormtrooper1 = scn.GetDSCard("stormtrooper1");
        var blaster = scn.GetDSCard("blaster");

		scn.StartGame();

		scn.MoveCardsToLocation(site, luke, stormtrooper1);
		scn.AttachCardsTo(stormtrooper1, blaster);

		scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(blaster));
        scn.DSUseCardAction(blaster);
        scn.DSChooseCard(luke);
        scn.LSPass(); //Use 1 Force - Optional responses
        scn.DSPass();
		assertTrue(scn.LSPlayUsedInterruptAvailable(deflection));
        scn.LSPlayUsedInterrupt(deflection);
        scn.PassAllResponses();
	}

    @Test @Ignore
    public void BlasterDeflectionLostRetargetsTargeting() {
        //demonstrates bug https://github.com/PlayersCommittee/gemp-swccg-public/issues/859

        var scn = GetScenario();

        var deflection = scn.GetLSCard("deflection");
        var luke = scn.GetLSCard("luke");
        var rebeltrooper = scn.GetLSCard("rebeltrooper");
        scn.MoveCardsToHand(deflection);

        var site = scn.GetLSStartingLocation();

        var stormtrooper1 = scn.GetDSCard("stormtrooper1");
        var stormtrooper2 = scn.GetDSCard("stormtrooper2");
        var blaster = scn.GetDSCard("blaster");

        scn.StartGame();

        scn.MoveCardsToLocation(site, luke, rebeltrooper, stormtrooper1,stormtrooper2);
        scn.AttachCardsTo(stormtrooper1, blaster);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(blaster));
        scn.DSUseCardAction(blaster);
        scn.DSChooseCard(luke);
        scn.LSPass(); //Use 1 Force - Optional responses
        scn.DSPass();
        assertTrue(scn.GetLSForcePileCount() >= 3);
        assertTrue(scn.LSPlayLostInterruptAvailable(deflection));
        scn.LSPlayLostInterrupt(deflection);
        scn.PassAllResponses();
    }

    //add many more tests for both used and lost actions
}
