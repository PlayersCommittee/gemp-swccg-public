package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_7_226_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_019");
                }},
                new HashMap<>() {{
                    put("homestead", "7_226");
                    put("farm", "1_294");
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

    /** Farm on table, Luke elsewhere so the Utinni is not canceled, Homestead attached targeting Luke. */
    private void putHomesteadInPlay(VirtualTableScenario scn) {
        var farm = scn.GetDSCard("farm");
        var homestead = scn.GetDSCard("homestead");
        var luke = scn.GetLSCard("luke");

        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke);
        scn.AttachCardsTo(farm, homestead);
        homestead.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, luke, Filters.any);
        assertTrue(scn.IsAttachedTo(farm, homestead));
    }

    /** Leave the opening Dark Control empty, then put Homestead in play and arrive at Dark Side's next Control with Light Side Force already activated. */
    private void reachDSControlWithHomestead(VirtualTableScenario scn) {
        scn.SkipToPhase(Phase.DEPLOY);
        putHomesteadInPlay(scn);
        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
    }

    /** True when Dark Side can click Homestead's "lose 1 Force" Control action. */
    private boolean homesteadForceLossAvailable(VirtualTableScenario scn) {
        if (!scn.DSAnyDecisionsAvailable()) {
            return false;
        }
        var homestead = scn.GetDSCard("homestead");
        return scn.DSCardActionAvailable(homestead, "lose 1 Force")
                || scn.DSActionAvailable("lose 1 Force");
    }

    /** Click Homestead's Control-phase Force loss and have Light Side pay it from Force Pile. */
    private void clickHomesteadForceLoss(VirtualTableScenario scn) {
        var homestead = scn.GetDSCard("homestead");
        assertTrue(homesteadForceLossAvailable(scn));
        scn.DSUseCardAction(homestead, "lose 1 Force");
        resolveOpponentLosesOneFromForcePile(scn);
    }

    /** Pass response windows, then Light Side chooses the top of Force Pile as the 1 Force lost. */
    private void resolveOpponentLosesOneFromForcePile(VirtualTableScenario scn) {
        for (int i = 0; i < 12; i++) {
            if (scn.LSDecisionAvailable("Choose Force to lose")) {
                assertTrue(scn.GetLSForcePileCount() >= 1);
                scn.LSChooseCard(scn.GetTopOfLSForcePile());
                continue;
            }
            if (scn.GetCurrentDecision() == null) {
                return;
            }
            String text = scn.GetCurrentDecision().getText().toLowerCase();
            if (text.contains("optional") || text.contains("about to lose") || text.contains("required")
                    || text.contains("force loss initiated")) {
                scn.PassResponses();
                continue;
            }
            return;
        }
    }

    @Test
    public void StatsAndKeywordsAreCorrect_7_226_DestroyedHomestead() {
        /**
         * Title: Destroyed Homestead
         * Uniqueness: Unique
         * Side: Dark
         * Type: Effect
         * Subtype: Utinni
         * Destiny: 5
         * Game Text: Deploy on Lars' Moisture Farm. Target Obi-Wan or Luke. Target may not apply ability toward
         *         drawing destiny for Sense, Alter or battle destiny. Opponent loses 1 Force during each of your
         *         control phases. Utinni Effect canceled when reached by target.
         * Set: Special Edition
         * Rarity: R
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("homestead").getBlueprint();

        assertEquals("Destroyed Homestead", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.isCardType(CardType.EFFECT));
        assertEquals(CardSubtype.UTINNI, card.getCardSubtype());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.SPECIAL_EDITION, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
        assertEquals(1, card.getIconCount(Icon.SPECIAL_EDITION));
        assertEquals(1, card.getIconCount(Icon.EFFECT));
    }

    @Test
    public void ManualClickDuringYourControlPhaseMakesOpponentLoseOneForce_7_226_DestroyedHomestead() {
        var scn = GetScenario();
        scn.StartGame();
        reachDSControlWithHomestead(scn);

        int lsForce = scn.GetLSForcePileCount();
        int dsForce = scn.GetDSForcePileCount();
        int lsLost = scn.GetLSLostPileCount();
        assertTrue(lsForce >= 1);

        clickHomesteadForceLoss(scn);

        assertEquals(lsForce - 1, scn.GetLSForcePileCount());
        assertEquals(lsLost + 1, scn.GetLSLostPileCount());
        assertEquals(dsForce, scn.GetDSForcePileCount());
        assertFalse(homesteadForceLossAvailable(scn));
    }

    @Test
    public void BothPlayersPassingStillMakesOpponentLoseOneForce_7_226_DestroyedHomestead() {
        var scn = GetScenario();
        scn.StartGame();
        reachDSControlWithHomestead(scn);

        int lsForce = scn.GetLSForcePileCount();
        int dsForce = scn.GetDSForcePileCount();
        int lsLost = scn.GetLSLostPileCount();
        assertTrue(lsForce >= 1);
        assertTrue(homesteadForceLossAvailable(scn));

        scn.PassControlActions();
        resolveOpponentLosesOneFromForcePile(scn);

        assertEquals(lsForce - 1, scn.GetLSForcePileCount());
        assertEquals(lsLost + 1, scn.GetLSLostPileCount());
        assertEquals(dsForce, scn.GetDSForcePileCount());
    }

    @Test
    public void ClickingOnceDoesNotLoseASecondForceAtEndOfPhase_7_226_DestroyedHomestead() {
        var scn = GetScenario();
        scn.StartGame();
        reachDSControlWithHomestead(scn);

        int lsForce = scn.GetLSForcePileCount();
        int lsLost = scn.GetLSLostPileCount();
        assertTrue(lsForce >= 1);

        clickHomesteadForceLoss(scn);
        assertEquals(lsForce - 1, scn.GetLSForcePileCount());
        assertFalse(homesteadForceLossAvailable(scn));

        scn.PassControlActions();
        resolveOpponentLosesOneFromForcePile(scn);

        assertEquals(lsForce - 1, scn.GetLSForcePileCount());
        assertEquals(lsLost + 1, scn.GetLSLostPileCount());
    }

    @Test
    public void DoesNotFireDuringOpponentsControlPhase_7_226_DestroyedHomestead() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToPhase(Phase.DEPLOY);
        putHomesteadInPlay(scn);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());

        int lsForce = scn.GetLSForcePileCount();
        int dsForce = scn.GetDSForcePileCount();
        int lsLost = scn.GetLSLostPileCount();
        int dsLost = scn.GetDSLostPileCount();

        scn.SkipToPhase(Phase.DEPLOY);

        assertEquals(lsForce, scn.GetLSForcePileCount());
        assertEquals(dsForce, scn.GetDSForcePileCount());
        assertEquals(lsLost, scn.GetLSLostPileCount());
        assertEquals(dsLost, scn.GetDSLostPileCount());
    }
}
