package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_223_033_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(

                new HashMap<>() {{
                    put("cb23", "223_033"); //CB-23
                    put("finn","204_006");
                    put("poe","204_008");
                    put("cracken","200_012"); //General Airen Cracken - opponent may not add destiny draws to power or attrition here
                }},
                new HashMap<>() {{
                    put("fos1","204_040"); //first order stormtrooper (ep7 character)
                    put("fos2","204_040");
                    put("tarkin","1_179");
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
    public void CB_23StatsAndKeywordsAreCorrect() {
        /**
         * Title: CB-23
         * Uniqueness: Unique
         * Side: Light
         * Type: Droid
         * Model: Astromech
         * Destiny: 3
         * Icons: Set 23
         * Game Text: [Episode VII] characters deploy -1 here. During battle, each player with two other participating
         *      [Episode VII] characters may add one destiny to attrition. While with Kazuda, Poe, or Rey, gains
         *      Resistance Agent.
         * Lore: Female.
         * Set: Set 23
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("cb23").getBlueprint();

        assertEquals("CB-23", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.ASTROMECH);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        assertNull(card.getCardSubtype());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(0, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.NAV_COMPUTER);
            add(Icon.EPISODE_VII);
            add(Icon.VIRTUAL_SET_23);
        }});
        assertEquals(ExpansionSet.SET_23,card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CB_23AddsLSAttritionDestinyWithTwoOtherEpVII() {
        //CB-23 in battle with 2 other LS episode 7 characters, and 2 other (non-episode 7) DS characters
        //test1: DS cannot add attrition destiny
        //test2: LS can add attrition destiny
        //test3: LS total attrition was 5 (3 from battle destiny + 2 from attrition destiny)
        //test3: DS total attrition was 4 (4 from battle destiny, no attrition destiny drawn)

        var scn = GetScenario();

        var cb23 = scn.GetLSCard("cb23");
        var finn = scn.GetLSCard("finn");
        var poe = scn.GetLSCard("poe");

        var tarkin = scn.GetDSCard("tarkin");
        var trooper = scn.GetDSFiller(1);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, cb23, finn, poe, tarkin, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareLSDestiny(2); //for attrition destiny
        scn.PrepareLSDestiny(3); //for battle destiny

        scn.PrepareDSDestiny(7); //for attrition destiny (in case incorrectly drawn!)
        scn.PrepareDSDestiny(4); //for battle destiny

        scn.DSInitiateBattle(site);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(cb23, "attrition")); //test1
        scn.DSPass();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(cb23, "attrition")); //test2
        scn.LSUseCardAction(cb23,"attrition");
        scn.PassAllResponses();

        scn.DSPass();
        scn.LSPass();

        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("draw 1 battle destiny"));
        scn.DSChooseYes();
        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("draw 1 battle destiny"));
        scn.LSChooseYes();
        scn.PassAllResponses();

        assertEquals(5,scn.GetUnpaidDSAttrition()); //test3: 3 + 2 attrition destiny
        assertEquals(4,scn.GetUnpaidLSAttrition()); //test4:
    }

    @Test
    public void CB_23AddsDSAttritionDestinyWithTwoOtherEpVII() {
        //CB-23 in battle with 2 other DS episode 7 characters and 2 other LS characters (only one is episode 7 character)
        //test1: DS can add attrition destiny
        //test2: LS cannot add attrition destiny
        //test3: LS total attrition was 4 (4 from battle destiny, no attrition destiny drawn)
        //test3: DS total attrition was 5 (3 from battle destiny, + 2 from attrition destiny)
        var scn = GetScenario();

        var cb23 = scn.GetLSCard("cb23");
        var finn = scn.GetLSCard("finn");
        var trooper = scn.GetLSFiller(1);

        var tarkin = scn.GetDSCard("tarkin");
        var fos1 = scn.GetDSCard("fos1");
        var fos2 = scn.GetDSCard("fos2");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, cb23, finn, trooper, tarkin, fos1, fos2);

        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(2); //for attrition destiny
        scn.PrepareDSDestiny(3); //for battle destiny

        scn.PrepareLSDestiny(7); //for attrition destiny (in case incorrectly drawn!)
        scn.PrepareLSDestiny(4); //for battle destiny

        scn.DSInitiateBattle(site);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(cb23, "attrition")); //test1
        scn.DSUseCardAction(cb23,"attrition");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(cb23, "attrition")); //test2
        scn.LSPass();

        scn.DSPass();

        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("draw 1 battle destiny"));
        scn.DSChooseYes();
        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("draw 1 battle destiny"));
        scn.LSChooseYes();
        scn.PassAllResponses();

        assertEquals(4,scn.GetUnpaidDSAttrition()); //test3:
        assertEquals(5,scn.GetUnpaidLSAttrition()); //test4: 3 + 2 attrition destiny
    }

    @Test
    public void CB_23AllowsBothPlayersToAddAttritionDestiny() {
        //CB-23 in battle with 2 other LS episode 7 characters, and 2 other DS episode 7 characters
        //test1: DS can add attrition destiny
        //test2: LS can add attrition destiny

        var scn = GetScenario();

        var cb23 = scn.GetLSCard("cb23");
        var finn = scn.GetLSCard("finn");
        var poe = scn.GetLSCard("poe");

        var tarkin = scn.GetDSCard("tarkin");
        var fos1 = scn.GetDSCard("fos1");
        var fos2 = scn.GetDSCard("fos2");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, cb23, finn, poe, tarkin, fos1, fos2);

        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareLSDestiny(2); //for attrition destiny
        scn.PrepareLSDestiny(3); //for battle destiny

        scn.PrepareDSDestiny(7); //for attrition destiny (in case incorrectly drawn!)
        scn.PrepareDSDestiny(4); //for battle destiny

        scn.DSInitiateBattle(site);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(cb23, "attrition")); //test1
        scn.DSUseCardAction(cb23,"attrition");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(cb23, "attrition")); //test2
        scn.LSUseCardAction(cb23,"attrition");
        scn.PassAllResponses();
    }

    @Test
    public void CB_23DoesNotAllowOpponentOptionalActionIfPrevented() {
        //CB-23 in battle with 2 other DS episode 7 characters and General Cracken (prevents opponent from adding attrition destinies)
        //test1: DS cannot add attrition destiny
        var scn = GetScenario();

        var cb23 = scn.GetLSCard("cb23");
        var finn = scn.GetLSCard("finn");
        var cracken = scn.GetLSCard("cracken");

        var tarkin = scn.GetDSCard("tarkin");
        var fos1 = scn.GetDSCard("fos1");
        var fos2 = scn.GetDSCard("fos2");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, cb23, finn, cracken, tarkin, fos1, fos2);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(cb23, "attrition")); //test1
    }

    @Test
    public void CB_23LSCanOnlyAddAttritionDestinyOncePerBattle() {
        var scn = GetScenario();

        var cb23 = scn.GetLSCard("cb23");
        var finn = scn.GetLSCard("finn");
        var poe = scn.GetLSCard("poe");

        var trooper = scn.GetDSFiller(1);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, cb23, finn, poe, trooper);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.DSPass();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(cb23, "attrition"));
        scn.LSUseCardAction(cb23,"attrition");
        scn.PassAllResponses();

        scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(cb23, "attrition"));
    }

    @Test
    public void CB_23DSCanOnlyAddAttritionDestinyOncePerBattle() {
        var scn = GetScenario();

        var cb23 = scn.GetLSCard("cb23");
        var trooper = scn.GetLSFiller(1);

        var tarkin = scn.GetDSCard("tarkin");
        var fos1 = scn.GetDSCard("fos1");
        var fos2 = scn.GetDSCard("fos2");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, cb23, trooper, tarkin, fos1, fos2);

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(cb23, "attrition"));
        scn.DSUseCardAction(cb23,"attrition");
        scn.PassAllResponses();

        scn.LSPass();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(cb23, "attrition"));
    }

}


