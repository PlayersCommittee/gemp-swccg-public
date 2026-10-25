package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class Card_1_064_Tests {
    /**
     * Sai'torr Kal Fas (1_64) is an unrestricted Effect. Deployed on a warrior it is power +1.
     * That bonus does not say Cumulatively, so extra copies still add only 1.
     */
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("skf1", "1_64");
                    put("skf2", "1_64");
                    put("skf3", "1_64");
                    put("skf4", "1_64");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void SaitorrKalFasStatsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("skf1").getBlueprint();
        assertEquals("Sai'torr Kal Fas", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
    }

    /**
     * Luke Skywalker (1_19) is a Light warrior with printed power 3.
     * Four copies of Sai'torr Kal Fas (1_64) on him still add only 1 power, not 4.
     * AttachCardsTo resets play option, so option 2 (Deploy on warrior) is set after attach.
     */
    @Test
    public void SaitorrKalFasWarriorPowerBonusDoesNotStackFromFourCopies() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var skf1 = scn.GetLSCard("skf1");
        var skf2 = scn.GetLSCard("skf2");
        var skf3 = scn.GetLSCard("skf3");
        var skf4 = scn.GetLSCard("skf4");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);

        scn.AttachCardsTo(luke, skf1, skf2, skf3, skf4);
        skf1.setPlayCardOptionId(PlayCardOptionId.PLAY_CARD_OPTION_2);
        skf2.setPlayCardOptionId(PlayCardOptionId.PLAY_CARD_OPTION_2);
        skf3.setPlayCardOptionId(PlayCardOptionId.PLAY_CARD_OPTION_2);
        skf4.setPlayCardOptionId(PlayCardOptionId.PLAY_CARD_OPTION_2);

        scn.SkipToPhase(Phase.CONTROL);

        // Luke printed power 3, plus 1 from Sai'torr Kal Fas, not plus 4 from four copies
        assertEquals(4, scn.GetPower(luke));
    }
}
