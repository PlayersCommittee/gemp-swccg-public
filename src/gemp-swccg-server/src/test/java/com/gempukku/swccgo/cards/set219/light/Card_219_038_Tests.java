package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_219_038_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("lothal", "219_038");
                    put("hera", "204_003");
                    put("ghost", "207_017");
                }},
                new HashMap<>()
                {{
                }},
                10,
                10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void LothalStatsAndKeywordsAreCorrect() {
        /**
         * Title: Lothal
         * Uniqueness: Unique
         * Side: Light
         * Type: Location
         * Subtype: System
         * Destiny: 0
         * Icons: Set 19, Planet
         * Game Text: Light: Light: Once per game, may simultaneously deploy Ghost and Hera here from hand and/or Reserve Deck; reshuffle.
         *          Dark: Thrawn and Pryce deploy -1 (and shuttle for free) to here. While Lothal converted, gains one [Dark Side] icon.
         * Light Force Icons: 2
         * Dark Force Icons: 1
         * Set: Set 19
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("lothal").getBlueprint();

        assertEquals("Lothal", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.VIRTUAL_SET_19);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        assertEquals(ExpansionSet.SET_19,card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LothalLSCanDeployGhostAndHeraFromReserveToLothal() {
        //test1: can deploy ghost and hera simultaneously (from reserve) to Lothal
        //test2: cannot deploy ghost and hera simultaneously (from reserve) to non-Lothal system
        var scn = GetScenario();

        var lothal = scn.GetLSCard("lothal");
        var hera = scn.GetLSCard("hera");
        var ghost = scn.GetLSCard("ghost");

        //LS and DS starting systems on table

        scn.StartGame();

        scn.MoveLocationToTable(lothal);
        scn.MoveCardsToLSHand(hera, ghost);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(2);
        assertEquals(7, scn.GetLSForcePileCount()); //enough to deploy to other systems without -2 discount at Lothal
        scn.MoveCardsToTopOfLSReserveDeck(hera, ghost);

        scn.SkipToPhase(Phase.DEPLOY);

        scn.LSUseCardAction(lothal, "Deploy Ghost");

        scn.LSChooseCard(ghost);
        scn.LSChooseCards(hera, ghost);

        scn.DSPass(); //LOOKED_AT_CARDS_IN_CARD_PILE - Optional responses
        scn.LSPass();

        //no option to choose destination - correctly auto selected Lothal as the only viable option
        assertFalse(scn.LSDecisionAvailable("Choose where to deploy"));

        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.CardsAtLocation(lothal, ghost));
    }

    @Test
    public void LothalLSCanDeployGhostFromReserveWithHeraFromHandToLothal() {
        //test1: can deploy ghost (from reserve) and hera from hand simultaneously to Lothal
        //test2: cannot deploy ghost (from reserve) and hera from hand simultaneously to non-Lothal system
        var scn = GetScenario();

        var lothal = scn.GetLSCard("lothal");
        var hera = scn.GetLSCard("hera");
        var ghost = scn.GetLSCard("ghost");

        //LS and DS starting systems on table

        scn.StartGame();

        scn.MoveLocationToTable(lothal);
        scn.MoveCardsToLSHand(hera, ghost);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(2);
        assertEquals(7, scn.GetLSForcePileCount()); //enough to deploy to other systems without -2 discount at Lothal
        scn.MoveCardsToTopOfLSReserveDeck(ghost);

        scn.SkipToPhase(Phase.DEPLOY);

        scn.LSUseCardAction(lothal, "Deploy Ghost");

        scn.LSChooseCard(ghost);
        scn.LSChooseCards(hera, ghost);

        scn.DSPass(); //LOOKED_AT_CARDS_IN_CARD_PILE - Optional responses
        scn.LSPass();

        //no option to choose destination - correctly auto selected Lothal as the only viable option
        assertFalse(scn.LSDecisionAvailable("Choose where to deploy"));

        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.CardsAtLocation(lothal, ghost));
    }


    @Test
    public void LothalLSCanDeployGhostWithHeraFromHandToLothal() {
        //test1: can deploy ghost and hera from hand simultaneously to Lothal
        //test2: cannot deploy ghost and hera from hand simultaneously to non-Lothal system
        var scn = GetScenario();

        var lothal = scn.GetLSCard("lothal");
        var hera = scn.GetLSCard("hera");
        var ghost = scn.GetLSCard("ghost");

        //LS and DS starting systems on table

        scn.StartGame();

        scn.MoveLocationToTable(lothal);
        scn.MoveCardsToLSHand(hera, ghost);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(2);
        assertEquals(7, scn.GetLSForcePileCount()); //enough to deploy to other systems without -2 discount at Lothal

        scn.SkipToPhase(Phase.DEPLOY);

        scn.LSUseCardAction(lothal, "Deploy Ghost");

        assertTrue(scn.LSDecisionAvailable("search Reserve Deck as well?"));
        scn.LSChooseNo();
        scn.LSChooseCard(ghost);
        scn.LSChooseCards(hera);

        //no option to choose destination - correctly auto selected Lothal as the only viable option
        assertFalse(scn.LSDecisionAvailable("Choose where to deploy"));

        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.CardsAtLocation(lothal, ghost));
    }

}
