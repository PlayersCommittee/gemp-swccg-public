package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.MayNotParticipateInBattleModifier;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class Card_200_057_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("anakin", "216_021");
                }},
                new HashMap<>() {{
                    put("xizor", "10_045");
                }},
                10,
                10,
                StartingSetup.LSStartingLocation("200_057"),
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    // associated issue: https://github.com/PlayersCommittee/gemp-swccg-public/issues/819
    @Test
    public void NightclubCountsAbilityOfCharacterThatMayNotParticipateInBattle() {
        var scn = GetScenario();

        var anakin = scn.GetLSCard("anakin");
        var xizor = scn.GetDSCard("xizor");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, anakin, xizor);
        scn.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                new MayNotParticipateInBattleModifier(xizor, anakin));

        scn.StartBattleAndSkipToWeaponsSegment(site);
        scn.SkipToPowerSegment();

        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
    }
}
