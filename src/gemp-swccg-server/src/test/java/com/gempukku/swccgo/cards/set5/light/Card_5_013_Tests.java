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
                    put("leesub", "1_16");
                    put("kabe", "1_14");
                    put("jawa", "1_12");
                    put("luke", "1_19");
                    put("dodge", "5_45");
                    put("trooper", "1_28");
                    put("plaza", "5_84"); // adjacent CC site so Dodge can move-as-react
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

    private static final String STORE = "'Store'";

    /**
     * Store a card from hand. Auto-picks when only one card is in hand; otherwise chooses {@code card}.
     * After optional responses, phase-action play order may be on DS — call {@link #returnLSPhaseWindow} before more LS actions.
     */
    private void storeCard(VirtualTableScenario scn, PhysicalCardImpl construct, PhysicalCardImpl card) throws DecisionResultInvalidException {
        assertNotNull("LS needs an action window to Store", scn.LSGetDecision());
        assertTrue(scn.LSCardActionAvailable(construct, STORE));
        scn.LSUseCardAction(construct, STORE);
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(card)) {
            scn.LSChooseCard(card);
        }
        scn.PassAllResponses();
    }

    /** After LS takes a phase action, play order often sits on DS — pass until LS has the window again. */
    private void returnLSPhaseWindow(VirtualTableScenario scn) throws DecisionResultInvalidException {
        for (int i = 0; i < 5 && scn.LSGetDecision() == null; i++) {
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                break;
            }
        }
        assertNotNull("Expected LS phase-action window", scn.LSGetDecision());
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
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        scn.MoveCardsToHand(construct);
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, kabe);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(construct));
        scn.LSDeployCard(construct);
        assertFalse(scn.LSHasCardChoiceAvailable(luke));
        assertTrue(scn.LSHasCardChoiceAvailable(kabe));
    }

    @Test
    public void CyborgConstructMayNotDeployOnAlienAbilityThree() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var leesub = scn.GetLSCard("leesub");
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        scn.MoveCardsToHand(construct);
        scn.StartGame();
        scn.MoveCardsToLocation(site, leesub, kabe);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(construct));
        scn.LSDeployCard(construct);
        assertFalse(scn.LSHasCardChoiceAvailable(leesub));
        assertTrue(scn.LSHasCardChoiceAvailable(kabe));
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
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(construct));
        scn.LSDeployCard(construct);
        assertTrue(scn.LSHasCardChoiceAvailable(kabe));
    }

    @Test
    public void CyborgConstructMayStoreOnceDuringOwnerTurn() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        var dodge = scn.GetLSCard("dodge");
        var trooper = scn.GetLSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        scn.AttachCardsTo(kabe, construct);
        scn.MoveCardsToHand(dodge, trooper);
        scn.SkipToLSTurn(Phase.DEPLOY);

        storeCard(scn, construct, dodge);
        assertEquals(Zone.STACKED_FACE_DOWN, dodge.getZone());
        assertEquals(construct, dodge.getStackedOn());

        returnLSPhaseWindow(scn);
        assertFalse("Store should be once per turn", scn.LSCardActionAvailable(construct, STORE));
    }

    @Test
    public void CyborgConstructMayStoreOnceDuringOpponentTurn() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        var dodge = scn.GetLSCard("dodge");
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        scn.AttachCardsTo(kabe, construct);
        scn.MoveCardsToHand(dodge);
        scn.SkipToDSTurn(Phase.DEPLOY);
        // Phase actions alternate — DS passes so LS gets a window during DS turn
        assertNotNull(scn.DSGetDecision());
        scn.DSPass();
        storeCard(scn, construct, dodge);
        assertEquals(Zone.STACKED_FACE_DOWN, dodge.getZone());
        assertEquals(construct, dodge.getStackedOn());
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
        var extra = scn.GetLSFiller(1);
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        scn.AttachCardsTo(kabe, construct);

        scn.MoveCardsToHand(c1);
        scn.SkipToLSTurn(Phase.DEPLOY);
        storeCard(scn, construct, c1);

        scn.MoveCardsToHand(c2);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPass();
        storeCard(scn, construct, c2);

        scn.MoveCardsToHand(c3);
        scn.SkipToLSTurn(Phase.DEPLOY);
        storeCard(scn, construct, c3);
        assertEquals(3, scn.GetStackedCards(construct).size());

        scn.MoveCardsToHand(extra);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(construct, STORE));
    }

    @Test
    public void CyborgConstructHoldsSixOnLobot() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var lobot = scn.GetLSCard("lobot");
        var construct = scn.GetLSCard("construct");
        PhysicalCardImpl[] fillers = new PhysicalCardImpl[6];
        for (int i = 0; i < 6; i++) {
            fillers[i] = scn.GetLSFiller(i + 1);
        }
        var extra = scn.GetLSCard("dodge");
        scn.StartGame();
        scn.MoveCardsToLocation(site, lobot);
        scn.AttachCardsTo(lobot, construct);

        for (int i = 0; i < 6; i++) {
            scn.MoveCardsToHand(fillers[i]);
            if (i % 2 == 0) {
                scn.SkipToLSTurn(Phase.DEPLOY);
            } else {
                scn.SkipToDSTurn(Phase.DEPLOY);
                scn.DSPass();
            }
            storeCard(scn, construct, fillers[i]);
        }
        assertEquals(6, scn.GetStackedCards(construct).size());
        scn.MoveCardsToHand(extra);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(construct, STORE));
    }

    @Test
    public void CyborgConstructMayDeployCardFromUnderneath() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        var trooper = scn.GetLSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        scn.AttachCardsTo(kabe, construct);
        scn.MoveCardsToHand(trooper);
        scn.SkipToLSTurn(Phase.DEPLOY);
        storeCard(scn, construct, trooper);
        assertEquals(Zone.STACKED_FACE_DOWN, trooper.getZone());

        returnLSPhaseWindow(scn);
        assertTrue(scn.LSDeployAvailable(trooper));
        scn.LSDeployCard(trooper);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertTrue(trooper.getZone().isInPlay());
    }

    @Test
    public void CyborgConstructMayPlayInterruptResponseFromUnderneath() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var plaza = scn.GetLSCard("plaza");
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        var dodge = scn.GetLSCard("dodge");
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        scn.AttachCardsTo(kabe, construct);
        // Adjacent Cloud City site so Dodge can move Kabe away as a react
        scn.MoveCardsToHand(plaza, dodge);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(plaza));
        scn.LSDeployLocation(plaza);
        scn.PassAllResponses();
        returnLSPhaseWindow(scn);
        storeCard(scn, construct, dodge);
        assertEquals(Zone.STACKED_FACE_DOWN, dodge.getZone());

        var trooperDS = scn.GetDSFiller(1);
        scn.MoveCardsToLocation(site, trooperDS);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(site));
        // DSInitiateBattle auto-passes BATTLE_INITIATED — initiate manually so LS can answer
        scn.DSUseCardAction(site, "Initiate battle");
        scn.PassForceUseResponses();
        // Response order may hit DS first with nothing to do
        for (int i = 0; i < 4 && scn.LSGetDecision() == null; i++) {
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                break;
            }
        }
        assertNotNull("Expected LS optional response window after battle initiated", scn.LSGetDecision());
        System.out.println("LS actions: " + scn.GetLSAvailableActions());
        boolean dodgeResponse = scn.LSCardActionAvailable(dodge)
                || scn.LSPlayLostInterruptAvailable(dodge)
                || scn.LSActionAvailable("Move character away")
                || scn.LSActionAvailable("Dodge")
                || scn.LSActionAvailable("'react'");
        assertTrue("Dodge under Construct should be playable as response; actions=" + scn.GetLSAvailableActions(), dodgeResponse);
    }

    @Test
    public void CyborgConstructStackedCardsToUsedWhenDeviceLost() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var construct = scn.GetLSCard("construct");
        var dodge = scn.GetLSCard("dodge");
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe);
        scn.AttachCardsTo(kabe, construct);
        scn.MoveCardsToHand(dodge);
        scn.SkipToLSTurn(Phase.DEPLOY);
        storeCard(scn, construct, dodge);
        assertEquals(Zone.STACKED_FACE_DOWN, dodge.getZone());

        returnLSPhaseWindow(scn);
        scn.LSExecuteAdHocEffect(construct, new LoseCardFromTableEffect(new TopLevelGameTextAction(construct, construct.getCardId()), construct));
        scn.PassAllResponses();
        assertTrue("stored cards go to Used Pile", dodge.getZone() == Zone.USED_PILE || dodge.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void CyborgConstructStackedCardsToUsedWhenDeviceTransferred() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var kabe = scn.GetLSCard("kabe");
        var jawa = scn.GetLSCard("jawa");
        var construct = scn.GetLSCard("construct");
        var dodge = scn.GetLSCard("dodge");
        scn.StartGame();
        scn.MoveCardsToLocation(site, kabe, jawa);
        scn.AttachCardsTo(kabe, construct);
        scn.MoveCardsToHand(dodge);
        scn.SkipToLSTurn(Phase.DEPLOY);
        storeCard(scn, construct, dodge);

        returnLSPhaseWindow(scn);
        assertTrue(scn.LSActionAvailable("Transfer"));
        scn.LSChooseAction("Transfer");
        assertTrue(scn.LSHasCardChoiceAvailable(jawa));
        scn.LSChooseCard(jawa);
        scn.PassAllResponses();
        assertTrue(dodge.getZone() == Zone.USED_PILE || dodge.getZone() == Zone.TOP_OF_USED_PILE);
        assertEquals(jawa, construct.getAttachedTo());
    }
}
