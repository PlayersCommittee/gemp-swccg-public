package com.gempukku.swccgo.rules.immunity;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImmuneToCardTests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("demotion", "1_047");
					put("revolution", "1_062");
                    put("fury","5_029"); //uncontrollable fury
                    put("yavin","1_135");
                    put("yavin_db","1_136");
                    put("kessel","1_126");
                    put("corvette","1_140");
                    put("r2d2","2_014");
                    put("r3a2_v","217_045");
				}},
				new HashMap<>()
				{{
					put("weakvader", "101_005"); //power 4
					put("lordvader", "9_113"); //immune to uncontrollable fury
                    put("promotion", "4_121"); //field promotion (target is immune to demotion)
                    put("justice", "2_121"); //imperial justice (target is immune to revolution)
                    put("ozzel", "3_082");
                    put("war_room","1_287");
                    put("lateral_damage","1_222");
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
	public void MayNotDeployOnImmuneTarget() {
		//verifies:
		//card A cannot be deployed on card B that already has immunity to card A

		var scn = GetScenario();

		var site = scn.GetLSStartingLocation();

		var demotion = scn.GetLSCard("demotion");

        var ozzel = scn.GetDSCard("ozzel");
        var promotion = scn.GetDSCard("promotion");
        var lordvader = scn.GetDSCard("lordvader");

        scn.StartGame();

		scn.MoveCardsToLSHand(demotion);
        scn.MoveCardsToDSHand(promotion);

		scn.MoveCardsToLocation(site,ozzel,lordvader);

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(promotion));
        scn.DSDeployCard(promotion);
        assertTrue(scn.DSHasCardChoiceAvailable(ozzel));
        scn.DSChooseCard(ozzel);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(ozzel, promotion));
        assertEquals(4,scn.GetPower(ozzel)); //3 + 1 from promotion

		scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.GetLSForcePileCount() >= 2); //enough to play demotion
        assertFalse(scn.LSCardPlayAvailable(demotion)); //because ozzel is immune
	}

    //shows fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/845
    @Test
    public void GainingImmunityByDeployMakesAttachedCardLost1() {
        //verifies:
        //if card A is attached to card B and card B later gains immunity to card A, card A is lost
        var scn = GetScenario();

        var site = scn.GetLSStartingLocation();

        var demotion = scn.GetLSCard("demotion");

        var ozzel = scn.GetDSCard("ozzel");
        var promotion = scn.GetDSCard("promotion");
        var lordvader = scn.GetDSCard("lordvader");

        scn.StartGame();

        scn.MoveCardsToDSHand(promotion);

        scn.MoveCardsToLocation(site,ozzel,lordvader);
        scn.AttachCardsTo(ozzel,demotion);

        assertTrue(scn.IsAttachedTo(ozzel, demotion));
        assertEquals(1,scn.GetPower(ozzel)); //2 - 1 from demotion

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(promotion));
        scn.DSDeployCard(promotion);
        assertTrue(scn.DSHasCardChoiceAvailable(ozzel));
        scn.DSChooseCard(ozzel);
        scn.PassAllResponses(); //demotion sent lost here

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(ozzel, promotion));
        assertFalse(scn.IsAttachedTo(ozzel,demotion));
        assertEquals(1,scn.GetLSLostPileCount()); //demotion in lost
        assertEquals(4,scn.GetPower(ozzel)); //3 + 1 from promotion
    }

    //shows fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/654
    @Test
    public void GainingImmunityByDeployMakesAttachedCardLost2() {
        //verifies:
        //if card A is attached to card B and card B later gains immunity to card A, card A is lost
        var scn = GetScenario();

        var revolution = scn.GetLSCard("revolution");

        var justice = scn.GetDSCard("justice");
        var war_room = scn.GetDSCard("war_room");

        scn.StartGame();

        scn.MoveLocationToTable(war_room);

        scn.MoveCardsToLSHand(revolution);
        scn.MoveCardsToDSHand(justice);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(0,scn.GetLSIconsOnLocation(war_room));
        assertTrue(scn.GetLSForcePileCount() >= 3);
        assertTrue(scn.LSCardPlayAvailable(revolution));
        scn.LSPlayCard(revolution);
        assertTrue(scn.LSHasCardChoiceAvailable(war_room));
        scn.LSChooseCard(war_room);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(war_room, revolution));

        assertEquals(3,scn.GetDSForcePileCount());
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(6,scn.GetDSForcePileCount()); //activate 3 means no force from war room
        assertTrue(scn.DSCardPlayAvailable(justice));
        scn.DSPlayCard(justice);
        assertTrue(scn.DSHasCardChoiceAvailable(war_room));
        scn.DSChooseCard(war_room);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(war_room, justice));
        assertFalse(scn.IsAttachedTo(war_room,revolution));
        assertEquals(1,scn.GetLSLostPileCount()); //revolution in lost
    }

    @Test
    public void GainingImmunityMakesUtinniEffectLost() {
        //verifies:
        //if utinni effect card A has targeted card B and card B later gains immunity to card A, card A is lost
        var scn = GetScenario();

        var yavin = scn.GetLSCard("yavin");
        var yavin_db = scn.GetLSCard("yavin_db");
        var kessel = scn.GetLSCard("kessel");
        var corvette = scn.GetLSCard("corvette");
        var r2d2 = scn.GetLSCard("r2d2");
        var r3a2_v = scn.GetLSCard("r3a2_v");

        var lateral_damage = scn.GetDSCard("lateral_damage");

        scn.StartGame();

        scn.MoveLocationToTable(yavin);
        scn.MoveLocationToTable(yavin_db);
        scn.MoveLocationToTable(kessel);

        scn.MoveCardsToLocation(yavin,corvette);
        scn.MoveCardsToLocation(yavin_db,r3a2_v,r2d2);

        scn.MoveCardsToDSHand(lateral_damage);

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(lateral_damage));
        scn.DSPlayCard(lateral_damage);
        assertTrue(scn.DSHasCardChoiceAvailable(kessel));
        scn.DSChooseCard(kessel); //attach utinni effect to kessel
        assertTrue(scn.DSHasCardChoiceAvailable(corvette));
        scn.DSChooseCard(corvette); //corvette is utinni target
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(kessel, lateral_damage));
        assertEquals(0,scn.GetPower(corvette));

        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.GetLSForcePileCount() >= 2); //enough to shuttle

        assertTrue(scn.LSCardActionAvailable(r2d2));
        scn.LSUseCardAction(r2d2); //shuttle
        assertTrue(scn.LSHasCardChoiceAvailable(corvette));
        scn.LSChooseCard(corvette);
        scn.PassAllResponses(); //astromech character now on corvette

        scn.DSPass();
        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertTrue(scn.IsAttachedTo(kessel,lateral_damage)); //lateral damage still in play
        assertEquals(0,scn.GetPower(corvette));

        assertTrue(scn.LSCardActionAvailable(r3a2_v));
        scn.LSUseCardAction(r3a2_v); //shuttle
        assertTrue(scn.LSHasCardChoiceAvailable(corvette));
        scn.LSChooseCard(corvette);
        scn.PassAllResponses(); //r3a2_v gametext should cause corvette to become immune to lateral damage

        assertEquals(6,scn.GetPower(corvette)); //5 + 1 from R3-A2
        assertFalse(scn.IsAttachedTo(kessel,lateral_damage));
        assertEquals(1,scn.GetDSLostPileCount()); //lateral damage in lost
    }

    @Test
    public void GainingImmunityByPersonaReplacementMakesAttachedCardLost() {
        //verifies:
        //if card A is attached to card B and card B is persona replaced by a card with immunity to card A, card A is lost

        var scn = GetScenario();

        var site = scn.GetLSStartingLocation();

        var fury = scn.GetLSCard("fury");

        var weakvader = scn.GetDSCard("weakvader");
        var lordvader = scn.GetDSCard("lordvader");

        scn.StartGame();

        scn.MoveCardsToLSHand(fury);
        scn.MoveCardsToDSHand(lordvader);

        scn.MoveCardsToLocation(site,weakvader);
        scn.AttachCardsTo(weakvader,fury);

        assertTrue(scn.IsAttachedTo(weakvader, fury));
        assertEquals(6,scn.GetPower(weakvader)); //4 + 2 from fury
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(lordvader));
        scn.DSPlayCard(lordvader);
        assertTrue(scn.DSHasCardChoiceAvailable(weakvader));
        scn.DSChooseCard(weakvader);
        scn.PassAllResponses(); //fury sent lost here

        assertFalse(scn.IsAttachedTo(lordvader, fury));
        assertEquals(7,scn.GetPower(lordvader)); //7 + 0 (no fury)
        assertEquals(1,scn.GetLSLostPileCount()); //fury in lost
        assertEquals(1,scn.GetDSLostPileCount()); //weakvader in lost
    }

    //add testing for immunity to interrupt targeting?
}
