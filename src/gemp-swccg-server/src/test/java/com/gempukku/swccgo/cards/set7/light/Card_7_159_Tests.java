package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_7_159_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("missile", "7_159"); //Intruder Missile
                    put("bwing", "7_140");
                }},
                new HashMap<>()
                {{
                    put("vcsd","2_155"); //victory class star destroyer (capital ship)
                    put("tie", "1_304"); //(non-capital ship)
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
    public void IntruderMissileStatsAndKeywordsAreCorrect() {
        /**
         * Title: Intruder Missile
         * Uniqueness: Unrestricted
         * Side: Light
         * Type: Weapon
         * Subtype: Starship
         * Destiny: 7
         * Icons: Weapon, Special Edition
         * Keywords: Missile
         * Game Text: Deploy on your B-wing, Z-95, YT-1300 Transport, or Falcon. May target a capital starship for free.
         *      Draw destiny. Add 3 if that capital starship was targeted by another weapon this turn.
         *      Target hit if total destiny > defense value. After firing, place Missile in Used Pile.
         * Lore: Prototype missile developed by Slayn & Korpil. Ionizing warhead disrupts onboard systems.
         *      Capable of destroying capital starships by overloading shield generators.
         * Set: Special Edition
         * Rarity: F
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("missile").getBlueprint();

        assertEquals("Intruder Missile", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertFalse(card.hasAlternateImageSuffix());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.STARSHIP, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.MISSILE);
            add(Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.SPECIAL_EDITION);
        }});
        assertEquals(ExpansionSet.SPECIAL_EDITION,card.getExpansionSet());
        assertEquals(Rarity.F,card.getRarity());
    }

    @Test
    public void IntruderMissileMayTargetCapitalStarship() {
        var scn = GetScenario();

        var bwing = scn.GetLSCard("bwing");
        var missile = scn.GetLSCard("missile");
        var system = scn.GetLSStartingLocation();

        var vcsd = scn.GetDSCard("vcsd");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLocation(system, bwing, vcsd, tie);
        scn.AttachCardsTo(bwing, missile);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(system);

        scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(missile));
        scn.LSUseCardAction(missile);

        assertTrue(scn.LSHasCardChoiceAvailable(vcsd));
        assertFalse(scn.LSHasCardChoiceAvailable(tie));
    }

    @Test
    public void IntruderMissileStaysAttachedIfTargetingCanceled() {
        var scn = GetScenario();

        var bwing = scn.GetLSCard("bwing");
        var missile = scn.GetLSCard("missile");
        var system = scn.GetLSStartingLocation();

        var vcsd = scn.GetDSCard("vcsd");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLocation(system, bwing, vcsd, tie);
        scn.AttachCardsTo(bwing, missile);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(system);

        scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(missile));
        scn.LSUseCardAction(missile);

        assertTrue(scn.LSHasCardChoiceAvailable(vcsd));
        scn.LSPass(); //Done (cancel targeting)

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(missile));
    }

    @Test
    public void IntruderMissileSentToUsedPileAfterFiring() {
        var scn = GetScenario();

        var bwing = scn.GetLSCard("bwing");
        var missile = scn.GetLSCard("missile");
        var system = scn.GetLSStartingLocation();

        var vcsd = scn.GetDSCard("vcsd");

        scn.StartGame();

        scn.MoveCardsToLocation(system, bwing, vcsd);
        scn.AttachCardsTo(bwing, missile);

        scn.PrepareLSDestiny(0); //miss

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(system);

        scn.DSPass();
        scn.LSUseCardAction(missile);
        scn.LSChooseCard(vcsd);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.IsAttachedTo(bwing, missile));
        assertEquals(Zone.TOP_OF_USED_PILE, missile.getZone());
    }
}
