package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_265_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("rebel", "1_28");
                    put("obi", "1_021");
                    put("speeder", "1_149");
                    put("sense", "1_109");
                    put("luke", "1_019");
                }},
                new HashMap<>()
                {{
                    put("pa", "1_265");
                    put("st1", "1_194");
                    put("st2", "1_194");
                    put("st3", "1_194");
                    put("rifle", "1_312");
                    put("rifle2", "1_312");
                    put("rifle3", "1_312");
                    put("blaster", "1_317");
                    put("blaster2", "1_317");
                    put("assault", "1_311");
                    put("saber", "1_314");
                    put("vader", "1_168");
                }},
                40,
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
    public void PreciseAttackStatsAndKeywordsAreCorrect() {
        /**
         * Title: Precise Attack
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Interrupt
         * Subtype: Lost
         * Destiny: 4
         * Game Text: During a battle, target opponent's character or vehicle present with two (or more) of your weapons.
         *      Add all weapon destiny draws together. Apply that total separately for each weapon in an order of your choosing.
         * Lore: 'Only Imperial stormtroopers are so precise.'
         * Set: Premiere
         * Rarity: C2
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("pa").getBlueprint();

        assertEquals("Precise Attack", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.isCardType(CardType.INTERRUPT));
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(Rarity.C2, card.getRarity());
    }

    @Test
    public void PreciseAttackCannotBePlayedOutsideBattle() {
        var scn = GetScenario();
        var pa = scn.GetDSCard("pa");
        setupTwoBlasterRiflesVsObiWan(scn);
        scn.MoveCardsToHand(pa);

        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardPlayAvailable(pa));

        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(pa));
    }

    @Test
    public void PreciseAttackCannotBePlayedWithFewerThanTwoLegalWeapons() {
        var scn = GetScenario();
        var pa = scn.GetDSCard("pa");
        setupOneBlasterRifleVsObiWan(scn);
        scn.MoveCardsToHand(pa);

        scn.DSStartBattleAndSkipToWeaponsSegment(site(scn));
        assertFalse("Precise Attack requires two or more legal character or vehicle weapons", scn.DSCardPlayAvailable(pa));
    }

    @Test
    public void PreciseAttackAddsDestinyDrawsAndAppliesTotalToEachWeapon() {
        // Two Blaster Rifles (1_312) vs Obi-Wan Kenobi (1_021) ability 6.
        // Destinies 3 then 4: separately 3+1=4 miss and 4+1=5 miss; Precise Attack 3+4=7, +1 once = 8 > 6 hits.
        var scn = GetScenario();
        var pa = scn.GetDSCard("pa");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var obi = scn.GetLSCard("obi");
        setupTwoBlasterRiflesVsObiWan(scn);
        scn.MoveCardsToHand(pa);

        scn.DSStartBattleAndSkipToWeaponsSegment(site(scn));
        assertTrue(scn.DSCardPlayAvailable(pa) || scn.DSCardActionAvailable(pa));
        playPreciseAttack(scn, obi, rifle, rifle2);

        fireOneShot(scn, obi, 3);
        assertFalse("First Precise Attack destiny must not resolve a hit by itself", obi.isHit());

        fireOneShot(scn, obi, 4);
        finishPreciseAttack(scn);
        assertTrue("Obi-Wan Kenobi ability 6 must be hit by combined 8", obi.isHit());
        assertPreciseAttackStateCleared(scn);
    }

    @Test
    public void GergallExample1ThreeBlasterRiflesHitsDefense6NotDefense7() {
        /**
         * Gergall 2015 Example 1: three Blaster Rifles (1_312) via Precise Attack (1_265).
         * Draws 1, 2, 3. TOTAL +1 from Blaster Rifle once (same title), not +3.
         * Grand total 1+2+3+1 = 7. Obi-Wan Kenobi (1_021) ability 6 is hit (7 > 6).
         * A defense value of 7 would miss.
         */
        var scn = GetScenario();
        var pa = scn.GetDSCard("pa");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var rifle3 = scn.GetDSCard("rifle3");
        var obi = scn.GetLSCard("obi");
        setupThreeBlasterRiflesVsObiWan(scn);
        scn.MoveCardsToHand(pa);

        scn.DSStartBattleAndSkipToWeaponsSegment(site(scn));
        playPreciseAttack(scn, obi, rifle, rifle2, rifle3);

        fireOneShot(scn, obi, 1);
        assertFalse(obi.isHit());
        fireOneShot(scn, obi, 2);
        assertFalse(obi.isHit());
        fireOneShot(scn, obi, 3);
        finishPreciseAttack(scn);

        assertEquals(6, scn.GetDefense(obi));
        assertTrue("Obi-Wan Kenobi ability 6 must be hit by Gergall Example 1 total 7", obi.isHit());
        String log = gameLog(scn);
        assertTrue("Precise Attack destinies 1 + 2 + 3 = 6. LOG:\n" + log, log.contains("1 + 2 + 3 = 6"));
        assertTrue("Grand total is 7 after Blaster Rifle +1 once. LOG:\n" + log, log.contains("Total weapon destiny 7"));
        assertTrue("Log names Precise Attack, not Combined Attack. LOG:\n" + log, log.contains("Precise Attack destinies"));
        assertFalse("Do not add Blaster Rifle +1 per copy (that would be 9)", log.contains("Total weapon destiny 9"));
        assertPreciseAttackStateCleared(scn);
    }

    @Test
    public void PreciseAttackCanTargetAVehicle() {
        // Two Blaster Rifles (1_312) vs Luke's X-34 Landspeeder (1_149) maneuver 5.
        // Destinies 2 then 3: combined 2+3=5, +1 once = 6 > 5 hits the landspeeder.
        var scn = GetScenario();
        var pa = scn.GetDSCard("pa");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var speeder = scn.GetLSCard("speeder");
        setupTwoBlasterRiflesVsLandspeeder(scn);
        scn.MoveCardsToHand(pa);

        scn.DSStartBattleAndSkipToWeaponsSegment(site(scn));
        playPreciseAttack(scn, speeder, rifle, rifle2);

        fireOneShot(scn, speeder, 2);
        assertFalse(speeder.isHit());
        fireOneShot(scn, speeder, 3);
        finishPreciseAttack(scn);
        assertTrue("Luke's X-34 Landspeeder maneuver 5 must be hit by combined 6", speeder.isHit());
        assertPreciseAttackStateCleared(scn);
    }

    @Test
    public void DarkJediLightsaberCannotBeChosenWhenTargetingAVehicle() {
        // Dark Jedi Lightsaber (1_314) may target a character or creature, not a vehicle.
        // Both weapons must actually be able to fire at the chosen target.
        var scn = GetScenario();
        var pa = scn.GetDSCard("pa");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var saber = scn.GetDSCard("saber");
        var speeder = scn.GetLSCard("speeder");
        setupTwoBlasterRiflesAndLightsaberVsLandspeeder(scn);
        scn.MoveCardsToHand(pa);

        scn.DSStartBattleAndSkipToWeaponsSegment(site(scn));
        if (scn.DSCardPlayAvailable(pa)) {
            scn.DSPlayCard(pa);
        }
        else {
            scn.DSUseCardAction(pa);
        }
        if (scn.DSHasCardChoiceAvailable(speeder)) {
            scn.DSChooseCard(speeder);
        }
        assertFalse("Dark Jedi Lightsaber cannot be chosen vs a vehicle. " + decisionDump(scn),
                scn.DSHasCardChoiceAvailable(saber));
        assertTrue("Blaster Rifle remains choosable vs the landspeeder", scn.DSHasCardChoiceAvailable(rifle));
        assertTrue("Second Blaster Rifle remains choosable vs the landspeeder", scn.DSHasCardChoiceAvailable(rifle2));
        scn.DSChooseCards(rifle, rifle2);
        scn.PassCardPlayResponses();
        fireOneShot(scn, speeder, 4);
        fireOneShot(scn, speeder, 4);
        finishPreciseAttack(scn);
        assertTrue(speeder.isHit());
    }

    @Test
    public void TwoDifferentWeaponTitlesBothTotalModifiersApply() {
        // Blaster Rifle (1_312) +1 and Assault Rifle (1_311) +1 are different titles, so both apply.
        // Destinies 2+3 = 5, +1 and +1 = 7 > Obi-Wan Kenobi ability 6 hits.
        // Wrong same-title-once across different titles would be 6, which does not hit.
        var scn = GetScenario();
        var pa = scn.GetDSCard("pa");
        var rifle = scn.GetDSCard("rifle");
        var assault = scn.GetDSCard("assault");
        var obi = scn.GetLSCard("obi");
        setupBlasterRifleAndAssaultRifleVsObiWan(scn);
        scn.MoveCardsToHand(pa);

        scn.DSStartBattleAndSkipToWeaponsSegment(site(scn));
        playPreciseAttack(scn, obi, rifle, assault);

        fireOneShot(scn, obi, 2);
        fireOneShot(scn, obi, 3);
        finishPreciseAttack(scn);

        assertTrue("Different titles Blaster Rifle +1 and Assault Rifle +1 must both apply (total 7)", obi.isHit());
        String log = gameLog(scn);
        assertTrue("Precise Attack destinies 2 + 3 = 5. LOG:\n" + log, log.contains("2 + 3 = 5"));
        assertTrue("Grand total is 7 after two different total modifiers. LOG:\n" + log,
                log.contains("Total weapon destiny 7"));
        assertPreciseAttackStateCleared(scn);
    }

    @Test
    public void ResultApplyOrderChooserIsOfferedAfterAllFirings() {
        var scn = GetScenario();
        var pa = scn.GetDSCard("pa");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var obi = scn.GetLSCard("obi");
        setupTwoBlasterRiflesVsObiWan(scn);
        scn.MoveCardsToHand(pa);

        scn.DSStartBattleAndSkipToWeaponsSegment(site(scn));
        playPreciseAttack(scn, obi, rifle, rifle2);
        fireOneShot(scn, obi, 4);
        fireOneShot(scn, obi, 4);

        assertTrue("After destinies, Precise Attack must ask which weapon result applies first. " + decisionDump(scn),
                scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                        && scn.DSGetDecision().getText().toLowerCase().contains("applies first"));
        finishPreciseAttack(scn, 1);
        assertTrue(obi.isHit());
        assertPreciseAttackStateCleared(scn);
    }

    @Test
    public void SenseCancelsPreciseAttackBeforeWeaponsFire() {
        // Sense (1_109) cancels Precise Attack (1_265) before any weapon fires.
        // Weapons may still fire normally afterward. Boring Conversation Anyway (1_235)
        // names Combined Attack, not Precise Attack, and is Dark (same side).
        var scn = GetScenario();
        var pa = scn.GetDSCard("pa");
        var sense = scn.GetLSCard("sense");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var obi = scn.GetLSCard("obi");
        setupTwoBlasterRiflesVsObiWan(scn);
        scn.MoveCardsToHand(pa);
        scn.MoveCardsToHand(sense);

        scn.DSStartBattleAndSkipToWeaponsSegment(site(scn));
        if (scn.DSCardPlayAvailable(pa)) {
            scn.DSPlayCard(pa);
        }
        else {
            scn.DSUseCardAction(pa);
        }
        if (scn.DSHasCardChoiceAvailable(obi)) {
            scn.DSChooseCard(obi);
        }
        if (scn.DSGetDecision() != null) {
            scn.DSChooseCards(rifle, rifle2);
        }

        int dsPass = 0;
        while (dsPass++ < 8 && scn.DSDecisionAvailable("Precise Attack") && scn.DSDecisionAvailable("Optional")) {
            scn.DSPass();
        }
        assertTrue("Sense must be able to cancel Precise Attack before weapons fire. " + decisionDump(scn),
                scn.LSCardActionAvailable(sense) || scn.LSPlayUsedInterruptAvailable(sense)
                        || scn.LSActionAvailable("Precise Attack")
                        || scn.LSActionAvailable("Sense"));
        scn.PrepareLSDestiny(1);
        if (scn.LSPlayUsedInterruptAvailable(sense)) {
            scn.LSPlayCard(sense);
        }
        else if (scn.LSCardActionAvailable(sense)) {
            scn.LSUseCardAction(sense);
        }
        else if (scn.LSActionAvailable("Sense")) {
            scn.LSChooseAction("Sense");
        }
        else {
            scn.LSChooseAction("Precise Attack");
        }
        if (scn.LSHasCardChoiceAvailable(obi)) {
            scn.LSChooseCard(obi);
        }
        scn.PassCardPlayResponses();
        scn.PassDestinyDrawResponses();
        int afterCancel = 0;
        while (afterCancel++ < 12) {
            if (scn.AwaitingDSWeaponsSegmentActions()) {
                break;
            }
            if (scn.AwaitingLSWeaponsSegmentActions()) {
                scn.LSPass();
                continue;
            }
            if (scn.LSDecisionAvailable("Playing") || scn.LSDecisionAvailable("Optional")
                    || scn.LSDecisionAvailable("PUT_IN_CARD_PILE") || scn.LSDecisionAvailable("PLACE_IN_CARD_PILE")) {
                scn.LSPass();
                continue;
            }
            if (scn.DSDecisionAvailable("Playing") || scn.DSDecisionAvailable("Optional")
                    || scn.DSDecisionAvailable("PUT_IN_CARD_PILE") || scn.DSDecisionAvailable("PLACE_IN_CARD_PILE")) {
                scn.DSPass();
                continue;
            }
            break;
        }

        assertFalse("Canceled Precise Attack must not hit Obi-Wan Kenobi", obi.isHit());
        assertTrue("After Sense cancels Precise Attack, Dark Side should still be in weapons segment. "
                        + decisionDump(scn),
                scn.AwaitingDSWeaponsSegmentActions());
        assertTrue("Blaster Rifle must still be fireable after Precise Attack is canceled. " + decisionDump(scn),
                scn.DSCardActionAvailable(rifle));
        assertPreciseAttackStateCleared(scn);
    }

    @Test
    public void PreciseAttackCannotChooseAlreadyFiredWeapon() {
        var scn = GetScenario();
        var pa = scn.GetDSCard("pa");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var rifle3 = scn.GetDSCard("rifle3");
        var obi = scn.GetLSCard("obi");
        setupThreeBlasterRiflesVsObiWan(scn);
        scn.MoveCardsToHand(pa);

        scn.DSStartBattleAndSkipToWeaponsSegment(site(scn));
        assertTrue(scn.DSCardActionAvailable(rifle, "Fire"));
        scn.DSUseCardAction(rifle, "Fire");
        fireOneShot(scn, obi, 1);
        scn.PassAllResponses();
        passLightSideWeaponsIfNeeded(scn);

        assertTrue("Precise Attack remains playable with two leftover weapons. " + decisionDump(scn),
                scn.DSCardPlayAvailable(pa) || scn.DSCardActionAvailable(pa));
        if (scn.DSCardPlayAvailable(pa)) {
            scn.DSPlayCard(pa);
        }
        else {
            scn.DSUseCardAction(pa);
        }
        if (scn.DSHasCardChoiceAvailable(obi)) {
            scn.DSChooseCard(obi);
        }
        assertFalse("Already-fired Blaster Rifle cannot be chosen for Precise Attack. " + decisionDump(scn),
                scn.DSHasCardChoiceAvailable(rifle));
        assertTrue("Unused Blaster Rifle remains choosable", scn.DSHasCardChoiceAvailable(rifle2));
        assertTrue("Second unused Blaster Rifle remains choosable", scn.DSHasCardChoiceAvailable(rifle3));
        scn.DSChooseCards(rifle2, rifle3);
        scn.PassCardPlayResponses();
        fireOneShot(scn, obi, 4);
        fireOneShot(scn, obi, 4);
        finishPreciseAttack(scn);
        assertTrue(obi.isHit());
    }

    /**
     * Dark Side starting location after both sides are moved to the Light Side ground site.
     */
    private PhysicalCardImpl site(VirtualTableScenario scn) {
        return scn.GetLSStartingLocation();
    }

    /**
     * Two Stormtroopers (1_194) with Blaster Rifles (1_312) vs Obi-Wan Kenobi (1_021) at the Light Side site.
     */
    private void setupTwoBlasterRiflesVsObiWan(VirtualTableScenario scn) {
        scn.StartGame();
        var site = site(scn);
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var obi = scn.GetLSCard("obi");
        scn.MoveCardsToLocation(site, st1, st2, obi);
        scn.AttachCardsTo(st1, rifle);
        scn.AttachCardsTo(st2, rifle2);
        ensureDarkForce(scn, 8);
    }

    /**
     * One Stormtrooper (1_194) with one Blaster Rifle (1_312) vs Obi-Wan Kenobi (1_021).
     */
    private void setupOneBlasterRifleVsObiWan(VirtualTableScenario scn) {
        scn.StartGame();
        var site = site(scn);
        var st1 = scn.GetDSCard("st1");
        var rifle = scn.GetDSCard("rifle");
        var obi = scn.GetLSCard("obi");
        scn.MoveCardsToLocation(site, st1, obi);
        scn.AttachCardsTo(st1, rifle);
        ensureDarkForce(scn, 8);
    }

    /**
     * Three Stormtroopers (1_194) with three Blaster Rifles (1_312) vs Obi-Wan Kenobi (1_021).
     */
    private void setupThreeBlasterRiflesVsObiWan(VirtualTableScenario scn) {
        scn.StartGame();
        var site = site(scn);
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var st3 = scn.GetDSCard("st3");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var rifle3 = scn.GetDSCard("rifle3");
        var obi = scn.GetLSCard("obi");
        scn.MoveCardsToLocation(site, st1, st2, st3, obi);
        scn.AttachCardsTo(st1, rifle);
        scn.AttachCardsTo(st2, rifle2);
        scn.AttachCardsTo(st3, rifle3);
        ensureDarkForce(scn, 10);
    }

    /**
     * Two Stormtroopers (1_194) with Blaster Rifles (1_312) vs Luke's X-34 Landspeeder (1_149)
     * and Rebel Trooper (1_28) for presence at the site.
     */
    private void setupTwoBlasterRiflesVsLandspeeder(VirtualTableScenario scn) {
        scn.StartGame();
        var site = site(scn);
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var speeder = scn.GetLSCard("speeder");
        var rebel = scn.GetLSCard("rebel");
        scn.MoveCardsToLocation(site, st1, st2, speeder, rebel);
        scn.AttachCardsTo(st1, rifle);
        scn.AttachCardsTo(st2, rifle2);
        ensureDarkForce(scn, 8);
    }

    /**
     * Two Blaster Rifles (1_312) plus Dark Jedi Lightsaber (1_314) on Vader (1_168)
     * vs Luke's X-34 Landspeeder (1_149).
     */
    private void setupTwoBlasterRiflesAndLightsaberVsLandspeeder(VirtualTableScenario scn) {
        scn.StartGame();
        var site = site(scn);
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var vader = scn.GetDSCard("vader");
        var rifle = scn.GetDSCard("rifle");
        var rifle2 = scn.GetDSCard("rifle2");
        var saber = scn.GetDSCard("saber");
        var speeder = scn.GetLSCard("speeder");
        var rebel = scn.GetLSCard("rebel");
        scn.MoveCardsToLocation(site, st1, st2, vader, speeder, rebel);
        scn.AttachCardsTo(st1, rifle);
        scn.AttachCardsTo(st2, rifle2);
        scn.AttachCardsTo(vader, saber);
        ensureDarkForce(scn, 10);
    }

    /**
     * Stormtrooper (1_194) with Blaster Rifle (1_312) and Stormtrooper with Assault Rifle (1_311)
     * vs Obi-Wan Kenobi (1_021).
     */
    private void setupBlasterRifleAndAssaultRifleVsObiWan(VirtualTableScenario scn) {
        scn.StartGame();
        var site = site(scn);
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rifle = scn.GetDSCard("rifle");
        var assault = scn.GetDSCard("assault");
        var obi = scn.GetLSCard("obi");
        scn.MoveCardsToLocation(site, st1, st2, obi);
        scn.AttachCardsTo(st1, rifle);
        scn.AttachCardsTo(st2, assault);
        ensureDarkForce(scn, 8);
    }

    private void ensureDarkForce(VirtualTableScenario scn, int amount) {
        try {
            scn.EnsureDSForcePile(amount);
        }
        catch (Throwable ignored) {
            // Some rigs only expose EnsureLSForcePile; leave the default Force pile if Dark has no helper.
        }
    }

    private void playPreciseAttack(VirtualTableScenario scn, PhysicalCardImpl target, PhysicalCardImpl... weapons) {
        var pa = scn.GetDSCard("pa");
        if (scn.DSCardPlayAvailable(pa)) {
            scn.DSPlayCard(pa);
        }
        else {
            scn.DSUseCardAction(pa);
        }
        if (scn.DSHasCardChoiceAvailable(target)) {
            scn.DSChooseCard(target);
        }
        if (weapons.length > 0 && scn.DSGetDecision() != null) {
            scn.DSChooseCards(weapons);
        }
        scn.PassCardPlayResponses();
    }

    private void fireOneShot(VirtualTableScenario scn, PhysicalCardImpl target, int destiny) {
        scn.PrepareDSDestiny(destiny);
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(target)) {
            scn.DSChooseCard(target);
        }
        chooseForceAmountIfPrompted(scn, null);
        scn.PassForceUseResponses();
        scn.PassWeaponFireWithDestinyDraw();
        passPostFiringResponses(scn);
    }

    private void chooseForceAmountIfPrompted(VirtualTableScenario scn, Integer forceToUse) {
        if (scn.DSGetDecision() == null || scn.DSGetDecision().getText() == null) {
            return;
        }
        String text = scn.DSGetDecision().getText().toLowerCase();
        if (!(text.contains("amount") || text.contains("how much") || text.contains("force to use") || text.contains("x ="))) {
            return;
        }
        int amount = forceToUse != null ? forceToUse : scn.DSGetChoiceMin();
        scn.DSDecided(amount);
    }

    private void passPostFiringResponses(VirtualTableScenario scn) {
        scn.PassResponses("ATTRIBUTE_RESET_OR_MODIFIED");
        scn.PassResponses("Ionized");
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassResponses("ABOUT_TO_BE_HIT");
        scn.PassResponses("HIT -");
        scn.PassResponses("FIRED_WEAPON");
        scn.PassResponses("PLACE_IN_CARD_PILE");
    }

    private void chooseResultApplyOrderIfPrompted(VirtualTableScenario scn) {
        chooseResultApplyOrderIfPrompted(scn, 0);
    }

    private void chooseResultApplyOrderIfPrompted(VirtualTableScenario scn, int applyFirstChoiceIndex) {
        int safety = 0;
        while (safety++ < 8 && scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null) {
            String text = scn.DSGetDecision().getText().toLowerCase();
            if (!(text.contains("applies first") || text.contains("weapon result"))) {
                break;
            }
            List<String> ids = scn.DSGetCardChoices();
            if (ids == null || ids.isEmpty()) {
                break;
            }
            int pick = applyFirstChoiceIndex;
            if (pick < 0 || pick >= ids.size()) {
                pick = ids.size() - 1;
            }
            scn.PlayerDecided("Dark Side Player", ids.get(pick));
        }
    }

    private void assertPreciseAttackStateCleared(VirtualTableScenario scn) {
        assertTrue("Precise Attack must clear Combined Attack firing state",
                scn.gameState().getCombinedAttackFiringState() == null);
        assertTrue("Precise Attack must clear Targeting Computer separately-or-combined firing state",
                scn.gameState().getSeparatelyOrCombinedFiringState() == null);
        scn.game().takeSnapshot("after Precise Attack");
    }

    private void finishPreciseAttack(VirtualTableScenario scn) {
        finishPreciseAttack(scn, 0);
    }

    private void finishPreciseAttack(VirtualTableScenario scn, int applyFirstChoiceIndex) {
        chooseResultApplyOrderIfPrompted(scn, applyFirstChoiceIndex);
        scn.PassResponses("PLACE_IN_CARD_PILE");
        scn.PassAllResponses();
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassResponses("ABOUT_TO_BE_HIT");
        scn.PassResponses("HIT -");
        scn.PassResponses("FIRED_WEAPON");
        chooseResultApplyOrderIfPrompted(scn);
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassAllResponses();
        chooseResultApplyOrderIfPrompted(scn);
        scn.PassResponses("ABOUT_TO_BE_LOST");
        scn.PassAllResponses();
        assertPreciseAttackStateCleared(scn);
    }

    private void passLightSideWeaponsIfNeeded(VirtualTableScenario scn) {
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            scn.LSPass();
        }
    }

    private String gameLog(VirtualTableScenario scn) {
        return String.join("\n", scn.gameState().getLastMessages());
    }

    private String decisionDump(VirtualTableScenario scn) {
        String ls = scn.LSGetDecision() == null ? "null" : scn.LSGetDecision().getText();
        String ds = scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText();
        return "LS=" + ls + " DS=" + ds;
    }
}
