package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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

public class Card_225_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                }},
                new HashMap<>()
                {{
                    put("cannon", "225_021"); //IG-88's Pulse Cannon (V)
                    put("ig88", "4_101"); //IG-88
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
    public void IG88sPulseCannonVStatsAndKeywordsAreCorrect() {
        /**
         * Title: IG-88's Pulse Cannon (V)
         * Uniqueness: Unique
         * Side: Dark
         * Type: Weapon
         * Subtype: Character
         * Destiny: 1
         * Icons: Dagobah, Set 25, Weapon
         * Game Text: Deploy on your bounty hunter. May target a character. Draw destiny.
         *      If destiny +1 > defense value, target hit, its forfeit = 0 and, if IG-88 firing repeatedly,
         *      may add one destiny to power or attrition. May fire repeatedly for 2 Force each time.
         * Lore: IG-88's personal favorite for mass destruction. Rapid-fire fusion plasma bursts are extremely
         *      effective against multiple targets. Not widely used due to incidental damage.
         * Set: 25
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("cannon").getBlueprint();

        assertEquals("IG-88's Pulse Cannon (V)", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            //null
        }});
        //add matching persona check?
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.DAGOBAH);
            add(Icon.VIRTUAL_SET_25);
        }});
    }

    @Test
    public void IG88sPulseCannonVCanAddDestinyOncePerRepeatFireByIG88() {
        //shows fixed: https://github.com/PlayersCommittee/gemp-swccg-public/issues/1170
        //when repeatedly fired by IG-88, cannot add to both power and attrition during same repeat fire
        var scn = GetScenario();

        var trooper = scn.GetLSFiller(1);

        var ig88 = scn.GetDSCard("ig88");
        var cannon = scn.GetDSCard("cannon");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, ig88, trooper);
        scn.AttachCardsTo(ig88, cannon);


        scn.SkipToPhase(Phase.BATTLE);
        assertEquals(3,scn.GetDSForcePileCount()); //enough to battle (1) and repeat fire once (2)

        scn.PrepareDSDestiny(2); //hit on first repeat fire
        scn.PrepareDSDestiny(0); //miss on first fire

        scn.DSInitiateBattle(site);

        scn.AwaitingDSWeaponsSegmentActions();
        scn.DSUseCardAction(cannon, "Fire");
        scn.DSChooseCard(trooper);
        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("repeat")); //fire repeatedly?
        scn.DSChooseYes();

        scn.LSPass(); //Use 2 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //Fire IG-88's Pulse Cannon (V) - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
        scn.DSPass();

        scn.LSPass(); //FORFEIT_REDUCED_TO_ZERO - Optional responses
        scn.DSPass();

        scn.LSPass(); //Optional responses
        assertTrue(scn.DSDecisionAvailable("Optional responses"));
        assertTrue(scn.DSCardActionAvailable(cannon, "power"));
        assertTrue(scn.DSCardActionAvailable(cannon, "attrition"));
        scn.DSUseCardAction(cannon, "power");

        scn.LSPass(); //Optional responses
        assertTrue(scn.DSDecisionAvailable("Optional responses"));
        assertFalse(scn.DSCardActionAvailable(cannon, "power"));
        assertFalse(scn.DSCardActionAvailable(cannon, "attrition")); ///FAILS HERE
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        //not enough force available to offer repeated fire again

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    @Test
    public void IG88sPulseCannonVCanAddDestinyToPowerOncePerBattleWhenRepeatHitByIG88() {
        //test1: cannot add one destiny to power on subsequent repeat fire hits
        var scn = GetScenario();

        var trooper = scn.GetLSFiller(1);

        var ig88 = scn.GetDSCard("ig88");
        var cannon = scn.GetDSCard("cannon");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, ig88, trooper);
        scn.AttachCardsTo(ig88, cannon);

        scn.DSActivateForceCheat(2);

        scn.SkipToPhase(Phase.BATTLE);
        assertEquals(5,scn.GetDSForcePileCount()); //enough to battle (1) and repeat fire twice (4)

        scn.PrepareDSDestiny(3); //hit on second repeat fire
        scn.PrepareDSDestiny(2); //hit on first repeat fire
        scn.PrepareDSDestiny(0); //miss on first fire

        scn.DSInitiateBattle(site);

        scn.AwaitingDSWeaponsSegmentActions();
        scn.DSUseCardAction(cannon, "Fire");
        scn.DSChooseCard(trooper);
        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("repeat")); //fire repeatedly?
        scn.DSChooseYes();

        scn.LSPass(); //Use 2 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //Fire IG-88's Pulse Cannon (V) - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
        scn.DSPass();

        scn.LSPass(); //FORFEIT_REDUCED_TO_ZERO - Optional responses
        scn.DSPass();

        scn.LSPass(); //Optional responses
        assertTrue(scn.DSDecisionAvailable("Optional responses"));
        assertTrue(scn.DSCardActionAvailable(cannon, "power"));
        scn.DSUseCardAction(cannon, "power");

        scn.LSPass(); //Optional responses
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("repeat")); //fire repeatedly?
        scn.DSChooseYes();

        scn.LSPass(); //Use 2 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //Fire IG-88's Pulse Cannon (V) - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
        scn.DSPass();

        //target is already forfeit 0, so no FORFEIT_REDUCED_TO_ZERO on second hit...
        //scn.LSPass(); //FORFEIT_REDUCED_TO_ZERO - Optional responses
        //scn.DSPass();

        scn.LSPass(); //Optional responses
        assertTrue(scn.DSDecisionAvailable("Optional responses"));
        assertFalse(scn.DSCardActionAvailable(cannon, "power")); //test1
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    @Test
    public void IG88sPulseCannonVCanAddDestinyToAttritionOncePerBattleWhenRepeatHitByIG88() {
        //test1: cannot add one destiny to power on subsequent repeat fire hits
        var scn = GetScenario();

        var trooper = scn.GetLSFiller(1);

        var ig88 = scn.GetDSCard("ig88");
        var cannon = scn.GetDSCard("cannon");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, ig88, trooper);
        scn.AttachCardsTo(ig88, cannon);

        scn.DSActivateForceCheat(2);

        scn.SkipToPhase(Phase.BATTLE);
        assertEquals(5,scn.GetDSForcePileCount()); //enough to battle (1) and repeat fire twice (4)

        scn.PrepareDSDestiny(3); //hit on second repeat fire
        scn.PrepareDSDestiny(2); //hit on first repeat fire
        scn.PrepareDSDestiny(0); //miss on first fire

        scn.DSInitiateBattle(site);

        scn.AwaitingDSWeaponsSegmentActions();
        scn.DSUseCardAction(cannon, "Fire");
        scn.DSChooseCard(trooper);
        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("repeat")); //fire repeatedly?
        scn.DSChooseYes();

        scn.LSPass(); //Use 2 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //Fire IG-88's Pulse Cannon (V) - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
        scn.DSPass();

        scn.LSPass(); //FORFEIT_REDUCED_TO_ZERO - Optional responses
        scn.DSPass();

        scn.LSPass(); //Optional responses
        assertTrue(scn.DSDecisionAvailable("Optional responses"));
        assertTrue(scn.DSCardActionAvailable(cannon, "attrition"));
        scn.DSUseCardAction(cannon, "attrition");

        scn.LSPass(); //Optional responses
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("repeat")); //fire repeatedly?
        scn.DSChooseYes();

        scn.LSPass(); //Use 2 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //Fire IG-88's Pulse Cannon (V) - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
        scn.DSPass();

        //target is already forfeit 0, so no FORFEIT_REDUCED_TO_ZERO on second hit...
        //scn.LSPass(); //FORFEIT_REDUCED_TO_ZERO - Optional responses
        //scn.DSPass();

        scn.LSPass(); //Optional responses
        assertTrue(scn.DSDecisionAvailable("Optional responses"));
        assertFalse(scn.DSCardActionAvailable(cannon, "attrition")); //test1
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    @Test
    public void IG88sPulseCannonVCanAddDestinyToPowerAndAttritionOncePerBattleWhenRepeatHitByIG88() {
        //test1: can add one destiny to power and one destiny to attrition on subsequent repeat fire hits
        var scn = GetScenario();

        var trooper = scn.GetLSFiller(1);

        var ig88 = scn.GetDSCard("ig88");
        var cannon = scn.GetDSCard("cannon");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, ig88, trooper);
        scn.AttachCardsTo(ig88, cannon);

        scn.DSActivateForceCheat(2);

        scn.SkipToPhase(Phase.BATTLE);
        assertEquals(5,scn.GetDSForcePileCount()); //enough to battle (1) and repeat fire twice (4)

        scn.PrepareDSDestiny(3); //hit on second repeat fire
        scn.PrepareDSDestiny(2); //hit on first repeat fire
        scn.PrepareDSDestiny(0); //miss on first fire

        scn.DSInitiateBattle(site);

        scn.AwaitingDSWeaponsSegmentActions();
        scn.DSUseCardAction(cannon, "Fire");
        scn.DSChooseCard(trooper);
        scn.PassAllResponses();

        assertTrue(scn.DSDecisionAvailable("repeat")); //fire repeatedly?
        scn.DSChooseYes();

        scn.LSPass(); //Use 2 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //Fire IG-88's Pulse Cannon (V) - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
        scn.DSPass();

        scn.LSPass(); //FORFEIT_REDUCED_TO_ZERO - Optional responses
        scn.DSPass();

        scn.LSPass(); //Optional responses
        assertTrue(scn.DSDecisionAvailable("Optional responses"));
        assertTrue(scn.DSCardActionAvailable(cannon, "power"));
        scn.DSUseCardAction(cannon, "power");

        scn.LSPass(); //Optional responses
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("repeat")); //fire repeatedly?
        scn.DSChooseYes();

        scn.LSPass(); //Use 2 Force - Optional responses
        scn.DSPass();

        scn.LSPass(); //Fire IG-88's Pulse Cannon (V) - Optional responses
        scn.DSPass();

        scn.LSPass(); //COST_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_DRAW_DESTINY_CARD - Optional responses
        scn.DSPass();

        scn.LSPass(); //DESTINY_DRAWN - Optional responses
        scn.DSPass();

        scn.LSPass(); //COMPLETE_DESTINY_DRAW - Optional responses
        scn.DSPass();

        scn.LSPass(); //DRAWING_DESTINY_COMPLETE - Optional responses
        scn.DSPass();

        scn.LSPass(); //ABOUT_TO_BE_HIT - Optional responses
        scn.DSPass();

        //target is already forfeit 0, so no FORFEIT_REDUCED_TO_ZERO on second hit...
        //scn.LSPass(); //FORFEIT_REDUCED_TO_ZERO - Optional responses
        //scn.DSPass();

        scn.LSPass(); //Optional responses
        assertTrue(scn.DSDecisionAvailable("Optional responses"));
        assertTrue(scn.DSCardActionAvailable(cannon, "attrition")); //test1
        scn.DSUseCardAction(cannon, "attrition");

        scn.LSPass(); //Optional responses
        scn.DSPass();

        scn.LSPass(); //FIRED_WEAPON - Optional responses
        scn.DSPass();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    //other tests:
    //deploy: only on smuggler
    //optional destiny add not available on first hit by IG
    //no optional destiny when non-IG firing repeatedly
    //hit threshold
    //forfeit 0
    //cannot target non-characters
}
