package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_5_013_Tests {
    protected VirtualTableScenario GetScenario() throws DecisionResultInvalidException {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("construct", "5_13");
                    put("lobot", "5_6");
                    put("leesub", "1_16"); // ability 3 alien
                    put("kabe", "1_14"); // ability 1 alien host
                    put("jawa", "1_12"); // ability 1 alien transfer target
                    put("luke", "1_19"); // non-alien
                    put("dodge", "5_45");
                    put("trooper", "1_28");
                }},
                new HashMap<>() {{
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

    private void deployConstructOn(VirtualTableScenario scn, PhysicalCardImpl host, PhysicalCardImpl construct) throws DecisionResultInvalidException {
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(construct));
        scn.LSDeployCard(construct);
        assertTrue(scn.LSHasCardChoiceAvailable(host));
        scn.LSChooseCard(host);
        assertEquals(host, construct.getAttachedTo());
    }

    @Test
    public void CyborgConstructBlueprintIconCheck() throws DecisionResultInvalidException {
        /**
         * Title: Cyborg Construct
         * Uniqueness: Unique
         * Side: Light
         * Type: Device
         * Destiny: 4
         * Icons: Cloud City, Device
         * Set: Cloud City
         * Rarity: U
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("construct").getBlueprint();

        assertEquals("Cyborg Construct", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertTrue(card.isCardType(CardType.DEVICE));
        assertEquals(CardCategory.DEVICE, card.getCardCategory());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getIconCount(Icon.CLOUD_CITY));
        assertEquals(1, card.getIconCount(Icon.DEVICE));
    }

    @Test
    public void CyborgConstructKeywordCheck() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var card = scn.GetLSCard("construct").getBlueprint();
        assertTrue(card.hasKeyword(Keyword.DEPLOYS_ON_CHARACTERS));
    }

    @Test
    public void CyborgConstructMayNotDeployOnNonAlien() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var luke = scn.GetLSCard("luke");
        var construct = scn.GetLSCard("construct");
        scn.MoveCardsToHand(construct);
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(construct));
        scn.LSDeployCard(construct);
        assertFalse(scn.LSHasCardChoiceAvailable(luke));
    }

    @Test
    public void CyborgConstructMayNotDeployOnAlienAbilityThree() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var leesub = scn.GetLSCard("leesub");
        var construct = scn.GetLSCard("construct");
        scn.MoveCardsToHand(construct);
        scn.StartGame();
        scn.MoveCardsToLocation(site, leesub);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(construct);
        assertFalse(scn.LSHasCardChoiceAvailable(leesub));
    }

    @Test
    public void CyborgConstructMayDeployOnAlienAbilityTwoOrLess() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        scn.MoveCardsToHand(construct);
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        deployConstructOn(scn, kabe, construct);
    }

    @Test
    public void CyborgConstructMayStoreOnceDuringOwnerTurn() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        var dodge = scn.GetLSCard("dodge");
        scn.MoveCardsToHand(construct, dodge);
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        deployConstructOn(scn, kabe, construct);

        assertTrue(scn.LSCardActionAvailable(construct, "Store"));
        scn.LSUseCardAction(construct, "Store");
        scn.LSChooseCard(dodge);
        assertEquals(Zone.STACKED_FACE_DOWN, dodge.getZone());
        assertEquals(construct, dodge.getStackedOn());
        assertFalse(scn.LSCardActionAvailable(construct, "Store"));
    }

    @Test
    public void CyborgConstructMayStoreOnceDuringOpponentTurn() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        var dodge = scn.GetLSCard("dodge");
        scn.MoveCardsToHand(construct, dodge);
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        deployConstructOn(scn, kabe, construct);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardActionAvailable(construct, "Store"));
        scn.LSUseCardAction(construct, "Store");
        scn.LSChooseCard(dodge);
        assertEquals(Zone.STACKED_FACE_DOWN, dodge.getZone());
    }

    @Test
    public void CyborgConstructHoldsThreeOnNonLobot() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        var c1 = scn.GetLSCard("dodge");
        var c2 = scn.GetLSCard("trooper");
        var c3 = scn.GetLSCard("jawa");
        scn.MoveCardsToHand(construct, c1, c2, c3);
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        deployConstructOn(scn, kabe, construct);

        scn.LSUseCardAction(construct, "Store");
        scn.LSChooseCard(c1);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.LSUseCardAction(construct, "Store");
        scn.LSChooseCard(c2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSUseCardAction(construct, "Store");
        scn.LSChooseCard(c3);
        assertEquals(3, scn.GetStackedCards(construct).size());

        var extra = scn.GetLSFiller(1);
        scn.MoveCardsToHand(extra);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(construct, "Store"));
    }

    @Test
    public void CyborgConstructHoldsSixOnLobot() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var lobot = scn.GetLSCard("lobot");
        var construct = scn.GetLSCard("construct");
        scn.MoveCardsToHand(construct);
        scn.StartGame();
        scn.MoveCardsToLocation(site, lobot);
        deployConstructOn(scn, lobot, construct);

        for (int i = 0; i < 6; i++) {
            var filler = scn.GetLSFiller(i + 1);
            scn.MoveCardsToHand(filler);
            assertTrue("store #" + (i + 1), scn.LSCardActionAvailable(construct, "Store"));
            scn.LSUseCardAction(construct, "Store");
            scn.LSChooseCard(filler);
            if (i < 5) {
                if (i % 2 == 0) {
                    scn.SkipToDSTurn(Phase.DEPLOY);
                } else {
                    scn.SkipToLSTurn(Phase.DEPLOY);
                }
            }
        }
        assertEquals(6, scn.GetStackedCards(construct).size());
        var extra = scn.GetLSCard("dodge");
        scn.MoveCardsToHand(extra);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(construct, "Store"));
    }

    @Test
    public void CyborgConstructMayDeployCardFromUnderneath() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        var trooper = scn.GetLSCard("trooper");
        scn.MoveCardsToHand(construct, trooper);
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        deployConstructOn(scn, kabe, construct);
        scn.LSUseCardAction(construct, "Store");
        scn.LSChooseCard(trooper);
        assertEquals(Zone.STACKED_FACE_DOWN, trooper.getZone());
        assertTrue(scn.LSDeployAvailable(trooper));
        scn.LSDeployCard(trooper);
        scn.LSChooseCard(site);
        assertTrue(trooper.getZone().isInPlay());
    }

    @Test
    public void CyborgConstructMayPlayTopLevelInterruptFromUnderneath() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        var dodge = scn.GetLSCard("dodge");
        scn.MoveCardsToHand(construct, dodge);
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        deployConstructOn(scn, kabe, construct);
        scn.LSUseCardAction(construct, "Store");
        scn.LSChooseCard(dodge);
        assertEquals(Zone.STACKED_FACE_DOWN, dodge.getZone());
        assertTrue(scn.LSCardActionAvailable(dodge) || scn.LSPlayUsedInterruptAvailable(dodge) || scn.LSPlayLostInterruptAvailable(dodge));
    }

    @Test
    public void CyborgConstructStackedCardsToUsedWhenDeviceLost() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        var dodge = scn.GetLSCard("dodge");
        scn.MoveCardsToHand(construct, dodge);
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        deployConstructOn(scn, kabe, construct);
        scn.LSUseCardAction(construct, "Store");
        scn.LSChooseCard(dodge);

        scn.LSExecuteAdHocEffect(construct, new LoseCardFromTableEffect(new TopLevelGameTextAction(construct, construct.getCardId()), construct));
        assertEquals(Zone.USED_PILE, dodge.getZone());
    }

    @Test
    public void CyborgConstructStackedCardsToUsedWhenDeviceTransferred() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var jawa = scn.GetLSCard("jawa");
        var construct = scn.GetLSCard("construct");
        var dodge = scn.GetLSCard("dodge");
        scn.MoveCardsToHand(construct, dodge);
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe, jawa);
        deployConstructOn(scn, kabe, construct);
        scn.LSUseCardAction(construct, "Store");
        scn.LSChooseCard(dodge);

        assertTrue(scn.LSTransferAvailable(construct));
        scn.LSTransferCard(construct);
        assertTrue(scn.LSHasCardChoiceAvailable(jawa));
        scn.LSChooseCard(jawa);
        assertEquals(Zone.USED_PILE, dodge.getZone());
        assertEquals(jawa, construct.getAttachedTo());
    }
}
