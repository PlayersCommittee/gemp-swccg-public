package com.gempukku.swccgo.cards.set3.light;

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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_3_080_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("cannon1", "3_080"); //surface defense cannon
                    put("cannon2", "3_080"); //surface defense cannon
                    put("ywing","1_147");
                    put("falcon","1_143");
				}},
				new HashMap<>()
				{{
                    put("tat_db","1_291");
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
	public void SurfaceDefenseCannonStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Surface Defense Cannon
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Weapon
		 * Subtype: Starship
		 * Destiny: 5
		 * Icons: Hoth, Weapon
		 * Game Text: Use 1 Force to deploy on your starfighter, free on Falcon. May target a character or creature
         *      at same site using 1 Force. Draw destiny. Target hit if destiny +1 > defense value.
		 * Lore: BlasTech Ax-108 'Ground Buzzer' blaster cannon. Designed to drop from a concealed gun pod
         *      on a starship. Targets using proximity motion sensors. Has 360-degree firing arc.
		 * Set: Hoth
		 * Rarity: R2
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("cannon1").getBlueprint();

		assertEquals("Surface Defense Cannon", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(5, card.getDestiny(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.WEAPON);
		}});
        assertEquals(CardSubtype.STARSHIP, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CANNON);
            add(Keyword.BLASTER);
            add(Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.HOTH);
			add(Icon.WEAPON);
		}});
		assertEquals(ExpansionSet.HOTH,card.getExpansionSet());
		assertEquals(Rarity.R2,card.getRarity());
	}

	@Test
	public void SurfaceDefenseCannonDeployCost() {
        //Tets1: deploys for 1 on non-Falcon starfighter
        //Test2: deploys for free on Falcon
        //Test3: is non-unique
		var scn = GetScenario();

        var cannon1 = scn.GetLSCard("cannon1");
        var cannon2 = scn.GetLSCard("cannon2");
        var ywing = scn.GetLSCard("ywing");
        var falcon = scn.GetLSCard("falcon");

        var tat_db = scn.GetDSCard("tat_db");

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

		scn.MoveCardsToLocation(tat_db, ywing, falcon);

        scn.MoveCardsToLSHand(cannon1,cannon2);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to deploy
        assertTrue(scn.LSDeployAvailable(cannon1));
        scn.LSDeployCard(cannon1);
        assertTrue(scn.LSHasCardChoiceAvailable(ywing));
        scn.LSChooseCard(ywing);
        scn.PassAllResponses();
        assertEquals(1,scn.GetLSUsedPileCount()); //Test1: paid 1 to deploy

        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(cannon2)); //Test3: non-unique
        scn.LSDeployCard(cannon2);
        assertTrue(scn.LSHasCardChoiceAvailable(falcon));
        scn.LSChooseCard(falcon);
        scn.PassAllResponses();
        assertEquals(1,scn.GetLSUsedPileCount()); //Test2: free to deploy
    }

    @Test
    public void SurfaceDefenseCannonFiringCost() {
        //Test1: cannot fire on unpiloted starfighter
        //Tets2: fires for 1 on starfighter
        var scn = GetScenario();

        var cannon1 = scn.GetLSCard("cannon1");
        var cannon2 = scn.GetLSCard("cannon2");
        var ywing = scn.GetLSCard("ywing");
        var falcon = scn.GetLSCard("falcon");

        var tat_db = scn.GetDSCard("tat_db");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);

        scn.MoveCardsToLocation(tat_db, trooper, ywing, falcon);
        scn.AttachCardsTo(ywing,cannon1);
        scn.AttachCardsTo(falcon,cannon2);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertEquals(4,scn.GetLSForcePileCount());
        scn.MoveCardsToHand(scn.GetTopOfLSForcePile());
        scn.MoveCardsToHand(scn.GetTopOfLSForcePile());
        assertEquals(2,scn.GetLSForcePileCount()); //just enough to battle and fire

        assertTrue(scn.LSCanInitiateBattle());
        scn.LSInitiateBattle(tat_db);
        assertEquals(1,scn.GetLSForcePileCount()); //1 to battle
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(cannon1)); //piloted, 1 force available
        assertFalse(scn.LSCardActionAvailable(cannon2)); //Test1: unpiloted, 1 force available

        scn.LSUseCardAction(cannon1); //fire
        assertTrue(scn.LSHasCardChoiceAvailable(trooper)); //target
        scn.LSChooseCard(trooper);

        scn.PassAllResponses();
        assertEquals(0,scn.GetLSForcePileCount()); //Test2: 1 to fire
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
    }

    //other tests:
    //hit when destiny + 1 > defense
    //not hit when destiny + 1 = defense
    //can fire at a creature
}
