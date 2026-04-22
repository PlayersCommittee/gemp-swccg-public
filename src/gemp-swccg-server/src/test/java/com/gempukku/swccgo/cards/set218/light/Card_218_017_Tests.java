package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_218_017_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("lev6", "218_017"); //Death Star: Level 6 Core Shaft Corridor (V)
                    put("ds_db", "1_124"); //Death Star: Docking Bay
                    put("finn", "204_006"); //(stormtrooper)
                    put("tk422v", "215_020"); //(spy, stormtrooper)
                    put("undercover", "2_040");
				}},
				new HashMap<>()
				{{
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
    public void DeathStarLevel6CoreShaftCorridorVStatsAndKeywordsAreCorrect() {
        /**
         * Title: Death Star: Level 6 Core Shaft Corridor
         * Uniqueness: Unique
         * Side: Light
         * Type: Location
         * Subtype: Site
         * Destiny: 0
         * Icons: Set 18, Scomplink, Mobile, Interior
         * Game Text: Light: Your stormtroopers here may move to an adjacent site as a 'react.'
         *          Dark: If you occupy, opponent's Level 6 Core Shaft Corridor game text is canceled.
         * Light Force Icons: 1
         * Dark Force Icons: 1
         * Set: Set 18
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("lev6").getBlueprint();

        assertEquals("Death Star: Level 6 Core Shaft Corridor", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.MOBILE);
            add(Icon.SCOMP_LINK);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.VIRTUAL_SET_18);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        assertEquals(ExpansionSet.SET_18,card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

	@Test
	public void DeathStarLevel6CoreShaftCorridorVStormtrooperMayReactToAdjacentTest() {
        //test1: site action available to move stormtrooper (tk422v) at this site as a react to a force drain at adjacent site
        //test2: can select a stormtrooper to move over
        //test3: stormtrooper completed the move to the adjacent site
        //test4: force drain canceled
        var scn = GetScenario();

		var tk422v = scn.GetLSCard("tk422v");
        var lev6 = scn.GetLSCard("lev6");
        var ds_db = scn.GetLSCard("ds_db");

        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(lev6);
        scn.MoveLocationToTable(ds_db);

        scn.MoveCardsToLocation(lev6, tk422v);
        scn.MoveCardsToLocation(ds_db, stormtrooper);

        scn.LSActivateForceCheat(1); //enough to move

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPlayCard(ds_db, "drain");

        assertTrue(scn.LSCardActionAvailable(lev6, "Move")); //test1
        scn.LSUseCardAction(lev6, "Move");
        scn.LSChooseCard(tk422v); //test2

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ds_db, tk422v, stormtrooper)); //test3: successfully moved over
        assertTrue(scn.AwaitingLSControlPhaseActions()); //force drain was canceled
    }

    @Test @Ignore
    public void DeathStarLevel6CoreShaftCorridorVUndercoverStormtrooperMayReactToAdjacentTest() {
        //test1: site action available to move undercover stormtrooper (TK-422 V + Undercover) at this site as a react to a force drain at adjacent site
        //test2: can select undercover stormtrooper to move over
        //test3: undercover stormtrooper completed the move to the adjacent site
        //test4: force drain canceled
        var scn = GetScenario();

        var tk422v = scn.GetLSCard("tk422v");
        var undercover = scn.GetLSCard("undercover");
        var lev6 = scn.GetLSCard("lev6");
        var ds_db = scn.GetLSCard("ds_db");

        var stormtrooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.MoveLocationToTable(lev6);
        scn.MoveLocationToTable(ds_db);

        scn.MoveCardsToLSHand(undercover);

        scn.MoveCardsToLocation(lev6, tk422v);
        scn.MoveCardsToLocation(ds_db, stormtrooper);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(undercover);
        scn.LSChooseCard(tk422v);
        scn.PassAllResponses();

        assertTrue(tk422v.isUndercover());

        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(ds_db, "drain");

        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to move

        ///FAILS HERE - getMoveOtherCardsAsReactOption uses filterActive which excludes undercover spies
        ///this could be tricky to resolve since some MayMoveOtherCardsAsReactToLocationModifier should
        ///apply to spies (this site, Solomahal, etc) but others should not (Wrist Comlink, etc)
        assertTrue(scn.LSCardActionAvailable(lev6, "Move")); //test1
        scn.LSUseCardAction(lev6, "Move");
        scn.LSChooseCard(tk422v); //test2

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(ds_db, tk422v, stormtrooper)); //test3: successfully moved over
        assertTrue(scn.AwaitingLSControlPhaseActions()); //force drain was canceled
    }

}
