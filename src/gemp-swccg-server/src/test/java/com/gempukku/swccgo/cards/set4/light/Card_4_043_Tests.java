package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Dagobah 4_043 Yoda's Gimer Stick.
 */
public class Card_4_043_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("yoda", "4_002");
                    put("stick", "4_043");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void YodasGimerStickAllowsBattleWhereDagobahYodaPresentWithoutAbilityThree() {
        var scn = GetScenario();

        var yoda = scn.GetLSCard("yoda");
        var stick = scn.GetLSCard("stick");
        var site = scn.GetLSStartingLocation();
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(site, yoda, trooper);
        scn.AttachCardsTo(yoda, stick);
        stick.setPlayCardOptionId(PlayCardOptionId.PLAY_CARD_OPTION_1);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(site));
    }

    @Test
    public void DagobahYodaPreventsBattleWithoutGimerStickWhenNoAbilityThreePresent() {
        var scn = GetScenario();

        var yoda = scn.GetLSCard("yoda");
        var site = scn.GetLSStartingLocation();
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(site, yoda, trooper);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertFalse(scn.LSCanInitiateBattle(site));
    }

    @Test
    public void YodasGimerStickDoesNotOverrideOtherBattleInitiationRestrictions() {
        var scn = GetScenario();

        var yoda = scn.GetLSCard("yoda");
        var stick = scn.GetLSCard("stick");
        var site = scn.GetLSStartingLocation();
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(site, yoda, trooper);
        scn.AttachCardsTo(yoda, stick);
        stick.setPlayCardOptionId(PlayCardOptionId.PLAY_CARD_OPTION_1);
        // Stand-in for Duel Of The Fates (and any other non-Yoda initiation lock).
        scn.ApplyAdHocModifier(new MayNotInitiateBattleAtLocationModifier(site, Filters.sameCardId(site), yoda.getOwner()));

        scn.SkipToLSTurn(Phase.BATTLE);
        assertFalse(scn.LSCanInitiateBattle(site));
    }
}
