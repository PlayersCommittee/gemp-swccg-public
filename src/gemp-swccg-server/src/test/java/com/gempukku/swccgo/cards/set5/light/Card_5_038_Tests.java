package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Clash Of Sabers (#57): may-not-battle exclusion must make the target INACTIVE in battle
 * so permanent / weapon top-level fire actions are unavailable.
 *
 * Covered by ExcludeBattleParticipantsRule: Clash / You Are Beaten / Imperial Barrier apply
 * MayNotParticipateInBattleModifier; battle start seeds via initiallyParticipatesInBattle then
 * excludes ineligible participants (ExcludedFromBattleModifier -> getCardState INACTIVE).
 * Distinct from #297/#1072 (reacted already-battled mid-battle arrivals).
 */
public class Card_5_038_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("clash", "5_38"); // Clash Of Sabers
                    put("quigon", "14_27"); // Qui-Gon Jinn With Lightsaber (Theed Palace)
                }},
                new HashMap<>() {{
                    put("maul", "14_77"); // Darth Maul With Lightsaber (Theed Palace)
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
    public void ClashOfSabersStatsAndKeywordsAreCorrect() {
        /**
         * Title: Clash Of Sabers
         * Uniqueness: Unique
         * Side: Light
         * Type: Interrupt
         * Subtype: Lost
         * Destiny: 3
         * Icons: Cloud City
         * Game Text: Use 2 Force to target a character present with your warrior with a lightsaber.
         *             Target cannot move or battle until end of your next turn. OR Use 1 Force to search your
         *             Reserve Deck, take one Uncontrollable Fury into hand and reshuffle. OR Cancel Presence Of The Force.
         * Set: Cloud City
         * Rarity: U
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("clash").getBlueprint();

        assertEquals("Clash Of Sabers", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertTrue(card.isCardType(CardType.INTERRUPT));
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getIconCount(Icon.CLOUD_CITY));
        assertEquals(ExpansionSet.CLOUD_CITY, card.getExpansionSet());
        assertEquals(Rarity.U, card.getRarity());
    }

    @Test
    public void ClashOfSabersMaulCannotFireWeaponDuringBattle() {
        // Issue #57: Clash Of Sabers / may-not-battle exclusion correctly omits Maul from battle
        // power/participation, but he must also be INACTIVE so permanent weapon fire is blocked
        // during the weapons segment (same gate as ExcludedFromBattle / Imperial Barrier).
        var scn = GetScenario();

        var clash = scn.GetLSCard("clash");
        var quigon = scn.GetLSCard("quigon");
        var trooperLS = scn.GetLSFiller(1);

        var maul = scn.GetDSCard("maul");
        var trooperDS = scn.GetDSFiller(1);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(clash);
        scn.MoveCardsToLocation(site, quigon, trooperLS, maul, trooperDS);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(clash));
        scn.LSPlayCard(clash);
        scn.LSChooseCard(maul);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site);

        // ExcludeBattleParticipantsRule: Clash may-not-battle cards join initially then are excluded
        assertTrue(scn.LSDecisionAvailable("ABOUT_TO_BE_EXCLUDED_FROM_BATTLE")
                || scn.DSDecisionAvailable("ABOUT_TO_BE_EXCLUDED_FROM_BATTLE"));
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions() || scn.AwaitingLSWeaponsSegmentActions());

        // May-not-battle: excluded/inactive for weapons (same inactive gate as Imperial Barrier)
        assertFalse(scn.IsParticipatingInBattle(maul));
        assertFalse(scn.IsCardActive(maul));

        // Sibling DS character still participates and remains active
        assertTrue(scn.IsParticipatingInBattle(trooperDS));
        assertTrue(scn.IsCardActive(trooperDS));

        // LS saber warrior who Clashed is still active/participating
        assertTrue(scn.IsParticipatingInBattle(quigon));
        assertTrue(scn.IsCardActive(quigon));

        // Advance to DS weapons segment and confirm Maul has no fire action
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            scn.LSPass();
        }
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(maul));
    }

    @Test
    public void ClashOfSabersExcludedMaulDoesNotContributePowerInBattle() {
        // Power exclusion / may-not-battle still correct: Clash'd Maul is omitted from battle
        // power while a non-Clashed sibling still contributes.
        var scn = GetScenario();

        var clash = scn.GetLSCard("clash");
        var quigon = scn.GetLSCard("quigon");
        var trooperLS = scn.GetLSFiller(1);

        var maul = scn.GetDSCard("maul");
        var trooperDS = scn.GetDSFiller(1);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(clash);
        scn.MoveCardsToLocation(site, quigon, trooperLS, maul, trooperDS);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPlayCard(clash);
        scn.LSChooseCard(maul);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();

        assertFalse(scn.IsParticipatingInBattle(maul));
        assertTrue(scn.IsParticipatingInBattle(trooperDS));
        assertFalse(scn.IsCardActive(maul));
        assertTrue(scn.IsCardActive(trooperDS));
    }

}
