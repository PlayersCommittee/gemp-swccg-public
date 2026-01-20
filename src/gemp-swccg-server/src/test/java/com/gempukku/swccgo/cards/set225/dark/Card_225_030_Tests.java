package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_225_030_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("ac_ls", "6_081"); //jabba's palace: audience chamber (LS)
				}},
				new HashMap<>()
				{{
                    put("desert_heart", "225_030"); //tatooine: desert heart (V)
					put("ac_ds", "6_162"); //jabba's palace: audience chamber (DS)
                    put("alien", "1_190"); //ponda baba
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
	public void TatooineDesertHeartVDarkStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Tatooine: Desert Heart
		 * Uniqueness: UNIQUE
		 * Side: DARK
		 * Type: Location
		 * Subtype: Site
		 * Destiny: 0
		 * Icons: Planet, Exterior, Jabba's Palace, V Set 25
		 * Game Text: Dark: Once per turn, if you just deployed an alien here, may raise your converted [Jabba's Palace] site to the top.
         *  Light: Unless you occupy, you must first use 1 Force to deploy a non-alien character here.
		 * Lore:
		 * Set: 25
		 * Rarity: V
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("desert_heart").getBlueprint();

		assertEquals("Tatooine: Desert Heart", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
		assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            //null
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.JABBAS_PALACE);
            add(Icon.VIRTUAL_SET_25);
        }});
		assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
		assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
	}

	//to demonstrate https://github.com/PlayersCommittee/gemp-swccg-public/issues/925 fixed
	@Test
	public void TatooineDesertHeartVDarkStub() {
        //test1: after DS deploys alien to desert heart, able to raise converted JP site (audience chamber) to top
		var scn = GetScenario();

		var ac_ls = scn.GetLSCard("ac_ls");

        var ac_ds = scn.GetDSCard("ac_ds");
        var alien = scn.GetDSCard("alien");
        var desert_heart = scn.GetDSCard("desert_heart");

		scn.StartGame();

		scn.MoveLocationToTable(ac_ds);
		scn.MoveLocationToTable(desert_heart);


		scn.MoveCardsToLSHand(ac_ls);
        scn.MoveCardsToDSHand(alien);

		assertEquals(Zone.LOCATIONS,ac_ds.getZone());

		scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(ac_ls));
		scn.LSDeployLocation(ac_ls);
		scn.PassAllResponses();

		assertEquals(Zone.LOCATIONS,ac_ls.getZone());
		assertEquals(Zone.CONVERTED_LOCATIONS,ac_ds.getZone());

		scn.SkipToDSTurn(Phase.DEPLOY);
		assertTrue(scn.DSDeployAvailable(alien));
		scn.DSDeployCard(alien);
		scn.DSHasCardChoiceAvailable(desert_heart);
		scn.DSChooseCard(desert_heart);

		scn.LSPass(); //Use 2 Force - Optional responses
		scn.DSPass();

		scn.LSPass(); //PLAY - Optional responses
		assertTrue(scn.DSDecisionAvailable("just deployed - Optional responses"));

		assertTrue(scn.DSCardActionAvailable(desert_heart,"Raise"));
		scn.DSUseCardAction(desert_heart,"Raise");
		assertTrue(scn.DSHasCardChoiceAvailable(ac_ls));
		scn.DSChooseCard(ac_ls);

		scn.PassAllResponses();
		assertEquals(Zone.LOCATIONS,ac_ds.getZone());
		assertEquals(Zone.CONVERTED_LOCATIONS,ac_ls.getZone());
	}

}
