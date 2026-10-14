package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.timing.Action;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Cloud City: Upper Plaza Corridor (#954): extra regular moves may use location-text movement.
 */
public class Card_5_084_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("plaza", "5_84");
                    put("downtown", "7_113");
                    put("poe", "204_8");
                }},
                new HashMap<>() {{
                }},
                40,
                40,
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
    public void CloudCityUpperPlazaCorridorStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("plaza").getBlueprint();

        assertEquals(Title.Upper_Plaza_Corridor, card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.INTERIOR_SITE);
            add(Icon.LIGHT_FORCE);
            add(Icon.MOBILE);
            add(Icon.SCOMP_LINK);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CLOUD_CITY_LOCATION);
        }});
        assertEquals(ExpansionSet.CLOUD_CITY, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void CloudCityUpperPlazaCorridorExtraRegularMoveMayUseLocationText() {
        var scn = GetScenario();
        var plaza = scn.GetLSCard("plaza");
        var downtown = scn.GetLSCard("downtown");
        var poe = scn.GetLSCard("poe");

        scn.StartGame();
        scn.MoveLocationToTable(plaza);
        scn.MoveLocationToTable(downtown);
        scn.MoveCardsToLocation(plaza, poe);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(Phase.CONTROL, scn.gameState().getCurrentPhase());
        Action duringControl = poe.getBlueprint().getRegularMoveAction(
                scn.LS, scn.game(), poe, false, 0, false, false, Filters.any);
        assertNull(duringControl);

        Action extraRegular = poe.getBlueprint().getRegularMoveAction(
                scn.LS, scn.game(), poe, false, 0, true, false, Filters.any);
        assertNotNull(extraRegular);
        String text = extraRegular.getText() == null ? "" : extraRegular.getText().toLowerCase();
        assertTrue("expected location-text or regular-move choice, got: " + extraRegular.getText()
                        + " (" + extraRegular.getClass().getSimpleName() + ")",
                text.contains("cloud city") || text.contains("location") || text.contains("regular move"));
    }

}
