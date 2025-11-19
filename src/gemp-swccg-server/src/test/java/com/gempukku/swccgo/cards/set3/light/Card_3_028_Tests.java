package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_3_028_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("remote", "3_028"); //artillery remote
                    //put("atgar", "3_072"); //Atgar Laser Cannon
                    put("golan","3_075"); //Golan Laser Battery
                    put("talz","1_031"); //non-warrior
                    put("cantina","1_128");
                    put("eg4","4_013"); //power droid (power source for artillery)
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
	public void ArtilleryRemoteStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Artillery Remote
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Device
		 * Destiny: 4
		 * Icons: Hoth, Device
		 * Game Text: Use 2 Force to deploy on your warrior. Warrior may fire artillery weapons anywhere on same
         *      planet regardless of being present. Once during your control phase, warrior may fire an artillery
         *      weapon on same planet.
		 * Lore: Although artillery weapons have a manual firing mechanism, this optional device allows weapons
         *      operation from a remote location. Uses coded signals.
		 * Set: Hoth
		 * Rarity: R2
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("remote").getBlueprint();

		assertEquals("Artillery Remote", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(4, card.getDestiny(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.DEVICE);
		}});
        //assertEquals(null, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.HOTH);
			add(Icon.DEVICE);
		}});
		assertEquals(ExpansionSet.HOTH,card.getExpansionSet());
		assertEquals(Rarity.R2,card.getRarity());
	}

	@Test
	public void ArtilleryRemoteDeployCost() {
        //Test1: deploys on warrior
        //Test2: does not deploy on non-warrior
        //Test3: deploys for 2 force
		var scn = GetScenario();

        var remote = scn.GetLSCard("remote");
        var warrior = scn.GetLSFiller(1); //rebel trooper
        var talz = scn.GetLSCard("talz");
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();

        scn.MoveLocationToTable(cantina);

		scn.MoveCardsToLocation(cantina, talz, warrior);

        scn.MoveCardsToLSHand(remote);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.GetLSForcePileCount() >= 2); //enough to deploy
        assertTrue(scn.LSDeployAvailable(remote));
        scn.LSDeployCard(remote);
        assertTrue(scn.LSHasCardChoiceAvailable(warrior)); //Test1: deploys on warrior
        assertFalse(scn.LSHasCardChoiceAvailable(talz)); //Test2: does not deploy on non-warrior
        assertEquals(0,scn.GetLSUsedPileCount());
        scn.LSChooseCard(warrior);
        scn.PassAllResponses();
        assertEquals(2,scn.GetLSUsedPileCount()); //Test3: paid 2 to deploy
    }

    //demonstrate https://github.com/PlayersCommittee/gemp-swccg-public/issues/362
    @Test @Ignore
    public void ArtilleryRemoteAllowsFiringAtSameSystemWithoutWarriorPresent() {
        //Test1: warrior with remote can remotely fire artillery weapon in battle
        var scn = GetScenario();

        var remote = scn.GetLSCard("remote");
        var warrior = scn.GetLSFiller(1); //rebel trooper
        var talz = scn.GetLSCard("talz");
        var cantina = scn.GetLSCard("cantina");
        var eg4 = scn.GetLSCard("eg4");
        var golan = scn.GetLSCard("golan");

        var tat_db = scn.GetDSCard("tat_db");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(tat_db);
        scn.MoveLocationToTable(cantina);

        scn.MoveCardsToLocation(tat_db, trooper, talz, eg4);

        scn.MoveCardsToLocation(cantina, warrior);
        scn.AttachCardsTo(warrior,remote);

        //UNCOMMENT for 'normal' firing (warrior at artillery weapon site), passes test
        //scn.MoveCardsToLocation(tat_db,warrior);

        scn.MoveCardsToLSHand(golan);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(golan));
        scn.LSDeployCard(golan);
        assertTrue(scn.LSHasCardChoiceAvailable(tat_db)); //(exterior)
        assertFalse(scn.LSHasCardChoiceAvailable(cantina)); //(interior)
        scn.LSChooseCard(tat_db);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(tat_db,golan));

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.GetLSForcePileCount() >= 3); //enough to battle and fire
        assertTrue(scn.LSCanInitiateBattle());
        scn.LSInitiateBattle(tat_db);

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        //test fails here because FireWeaponActionBuilder doesn't see warrior as a possibleWeaponUser?
        assertTrue(scn.LSCardActionAvailable(golan)); //Test1: able to fire without warrior at site
        scn.LSUseCardAction(golan);
        assertTrue(scn.LSHasCardChoiceAvailable(warrior)); //Test1: able to choose firing character holding remote
        scn.LSChooseCard(warrior);
        assertTrue(scn.LSHasCardChoiceAvailable(trooper)); //target
        scn.LSChooseCard(trooper);
    }

    //other tests:
    //planet restriction
    //control phase firing
    //uses up firing action for the turn
}
