package com.gempukku.swccgo.rules.layout;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.layout.LocationGroup;
import com.gempukku.swccgo.game.layout.RearrangeSites;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ChooseAndRearrangeRelatedSitesEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Dedicated click-to-order coverage for the rearranging-sites helper.
 * Retract The Bridge and Heart Of The Chasm are not encoded here.
 */
public class RearrangeSitesClickOrderTests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("guest", "5_080");
                }},
                new HashMap<>()
                {{
                    put("corridor", "1_284");
                    put("db327", "1_285");
                    put("war-room", "1_287");
                    put("conference", "2_144");
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

    private void putLocation(VirtualTableScenario scn, PhysicalCardImpl location) {
        scn.MoveLocationToTable(location);
    }

    private List<PhysicalCard> locationRow(VirtualTableScenario scn) {
        return scn.gameState().getLocationsInOrder();
    }

    private List<PhysicalCard> interiorTops(VirtualTableScenario scn, String systemName) {
        LocationGroup group = scn.gameState().getLocationsLayout().findGroupForSystemMatching(
                scn.game(), systemName, RearrangeSites.interiorSitesOfSystem(systemName));
        assertNotNull(group);
        return new ArrayList<PhysicalCard>(group.getTopCardsInGroup());
    }

    private void assertIndexesMatchRow(VirtualTableScenario scn) {
        List<PhysicalCard> row = locationRow(scn);
        for (int i = 0; i < row.size(); ++i) {
            assertEquals(i, row.get(i).getLocationZoneIndex());
        }
    }

    @Test
    public void ClickSitesInOrderRearrangesAtomicallyAfterAllChosen() {
        var scn = GetScenario();

        var corridor = scn.GetDSCard("corridor");
        var warRoom = scn.GetDSCard("war-room");
        var conference = scn.GetDSCard("conference");
        var db327 = scn.GetDSCard("db327");
        var marketplace = scn.GetDSStartingLocation();
        var chasm = scn.GetLSStartingLocation();

        scn.StartGame();

        putLocation(scn, corridor);
        putLocation(scn, warRoom);
        putLocation(scn, conference);
        putLocation(scn, db327);

        scn.SkipToPhase(Phase.CONTROL);

        int db327Index = db327.getLocationZoneIndex();
        List<PhysicalCard> interiors = interiorTops(scn, Title.Death_Star);
        assertEquals(3, interiors.size());
        PhysicalCardImpl left = (PhysicalCardImpl) interiors.get(0);
        PhysicalCardImpl mid = (PhysicalCardImpl) interiors.get(1);
        PhysicalCardImpl right = (PhysicalCardImpl) interiors.get(2);
        List<PhysicalCard> original = new ArrayList<PhysicalCard>(interiors);

        scn.DSExecuteAdHocEffect(corridor, new ChooseAndRearrangeRelatedSitesEffect(
                new TopLevelGameTextAction(corridor, corridor.getOwner(), corridor.getCardId()),
                corridor.getOwner(),
                RearrangeSites.interiorSitesOfSystem(Title.Death_Star)));

        assertTrue(scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.CHOICE_TEXT));
        assertTrue(scn.DSHasCardChoicesAvailable(left, mid, right));
        assertTrue(scn.DSHasCardChoicesNotAvailable(db327, marketplace, chasm));

        scn.DSChooseCard(right);
        assertEquals(original, interiorTops(scn, Title.Death_Star));
        assertTrue(scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.CHOICE_TEXT));
        assertTrue(scn.DSHasCardChoicesAvailable(left, mid));
        assertTrue(scn.DSHasCardChoiceNotAvailable(right));

        scn.DSChooseCard(left);
        if (scn.DSDecisionAvailable(ChooseAndRearrangeRelatedSitesEffect.CHOICE_TEXT)) {
            scn.DSChooseCard(mid);
        }

        assertEquals(Arrays.asList(right, left, mid), interiorTops(scn, Title.Death_Star));
        assertEquals(db327Index, db327.getLocationZoneIndex());
        assertIndexesMatchRow(scn);
    }
}
