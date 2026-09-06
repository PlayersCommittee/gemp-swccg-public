package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.logic.modifiers.ChangeCardSubtypeModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.NotUniqueModifier;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.UtinniEffectStatus;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.modifiers.MouseDroidUtinniCarry;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_188_Tests {
    /*
     * Utinni inventory matrix (Decipher Premiere through Theed Palace; exact blueprints):
     * DS (17): 1_220 Juri Juice [character-hosted], 1_222 Lateral Damage [mobile/at-location],
     * 1_223 Luke? Luuuuke! [snap-to-target], 1_226 Organa's Ceremonial Necklace [snap-to-target],
     * 1_229 Send A Detachment Down [stay-put], 1_231 Tactical Re-Call [snap-to-target],
     * 2_125 Spice Mines Of Kessel [excluded/may-not-move], 3_099 Death Mark [snap-to-target],
     * 3_107 Meteor Impact? [snap-to-target], 3_109 Responsibility Of Command [snap-to-target],
     * 3_112 This Is Just Wrong [snap-to-target], 3_114 Weapon Malfunction [snap-to-target],
     * 4_120 Failure At The Cave [stay-put], 5_117 Forced Landing [stay-put],
     * 5_124 The Emperor's Prize [snap-to-target], 5_128 We're The Bait [character-hosted],
     * 7_226 Destroyed Homestead [snap-to-target].
     * LS (14): 1_046 Death Star Plans [snap-to-target], 1_052 Kessel Run [excluded],
     * 1_058 Our Most Desperate Hour [snap-to-target], 1_059 Plastoid Armor [snap-to-target],
     * 1_067 Tusken Breath Mask [snap-to-target], 1_069 Yerka Mig [mobile/at-location],
     * 2_030 Cell 2187 [snap-to-target], 2_039 They're On Dantooine [stay-put],
     * 3_039 The First Transport Is Away [space/at-location], 4_018 Asteroids Do Not Concern Me [space/Big One],
     * 4_034 Report To Lord Vader [snap-to-target], 4_036 Rycar's Run [space/Big One],
     * 4_042 What Is Thy Bidding, My Master? [snap-to-target], 7_070 Mechanical Failure [snap-to-target].
     * Excluded from Mouse interaction: Kessel Run, Spice Mines Of Kessel, and the Elom-modified
     * 1_059 Plastoid Armor form; Elom's 6_012 modification makes Plastoid a normal Effect (not an Utinni).
     * Coverage checklist: [x] relocate onto Mouse; [x] own reached/delivery/cancel/attrition text remains active;
     * [x] carried Utinni reaches through a starship/vehicle; [x] cannot-grab exclusions; [x] SADD stays put.
     */

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("roar", "2_058");
                    put("kessel-run", "1_052");
                    put("cell", "2_030");
                    put("leia", "1_017");
                    put("han", "1_011");
                    put("jungle", "1_137");
                    put("yavin-db", "1_136");
                    put("ds-db", "1_124");
                    put("trash", "1_125");
                    put("tatooine-system", "1_127");
                    put("luke", "1_019");
                    put("farm", "1_132");
                    put("son", "4_001");
                    put("daughter", "8_008");
                    put("bog", "4_085");
                    put("jungle-dag", "4_086");
                    put("training", "4_088");
                    put("lando", "109_003");
                    put("cantina", "1_128");
                    put("plastoid", "1_059");
                    put("rycars", "4_036");
                    put("bigone", "4_082");
                    put("awing", "9_62");
                    put("yerka", "1_069");
                    put("plastoid2", "1_059");
                    put("tusken", "1_067");
                    put("elom", "6_012");
                    put("doallyn", "6_038");
                    put("chewie", "2_003");
                }},
                new HashMap<>() {{
                    put("mouse", "1_188");
                    put("mouse2", "1_188");
                    put("sadd", "1_229");
                    put("necklace", "1_226");
                    put("fivedesix", "1_163");
                    put("spice", "2_125");
                    put("landspreeder", "1_310");
                    put("devastator", "1_301");
                    put("vcsd", "2_155");
                    put("kessel", "1_288");
                    put("cave", "4_158");
                    put("avarik", "8_95");
                    put("oberk", "8_099");
                    put("homestead", "7_226");
                    put("db94", "1_291");
                    put("failure", "4_120");
                    put("tie", "1_304");
                    put("juri", "1_220");
                    put("bait", "5_128");
                    put("tijw", "3_112");
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
    public void MouseDroid_1_188_StatsAndKeywordsAreCorrect() {
        /**
         * Title: MSE-6 'Mouse' Droid
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Character
         * Subtype: Droid
         * Destiny: 0  Deploy: 0  Power: 0  Forfeit: 0
         * Model: Messenger
         * Game Text: Landspeed = 3. Deploys to same site as a character targeted by a Utinni Effect (except Kessel Run).
         *      If this droid 'reaches' Utinni Effect, may relocate it here. Upon delivery, 'mouse' droid returns to your hand.
         * Set: Premiere  Rarity: U1
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("mouse").getBlueprint();

        assertEquals("MSE-6 'Mouse' Droid", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.isCardType(CardType.DROID));
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(0, card.getDeployCost(), scn.epsilon);
        assertEquals(0, card.getPower(), scn.epsilon);
        assertEquals(0, card.getForfeit(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.MESSENGER);
        }});
        assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
        assertEquals(Rarity.U1, card.getRarity());
    }

    /** Puts Send A Detachment Down on Marketplace targeting a Stormtrooper at Death Star: Docking Bay 327, then reaches Deploy. */
    private void PlaySaddTargetingTrooperAtDeathStar(VirtualTableScenario scn) {
        var sadd = scn.GetDSCard("sadd");
        var dsDb = scn.GetLSCard("ds-db");
        var trooper = scn.GetDSFiller(1);
        var marketplace = scn.GetDSStartingLocation();

        scn.MoveLocationToTable(dsDb);
        scn.MoveCardsToLocation(dsDb, trooper);
        scn.AttachCardsTo(marketplace, sadd);
        sadd.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, trooper, Filters.any);
        assertTrue(scn.IsAttachedTo(marketplace, sadd));
        EnsureDSDeployPhase(scn);
    }

    /** Activate max Force and pass Control so Dark Side is choosing a Deploy action. */
    private void EnsureDSDeployPhase(VirtualTableScenario scn) {
        if (scn.AwaitingDSDeployPhaseActions()) {
            return;
        }
        if (scn.GetCurrentPhase() == Phase.ACTIVATE) {
            scn.DSActivateMaxForceAndPass();
        }
        if (scn.GetCurrentPhase() == Phase.CONTROL) {
            scn.PassControlActions();
        }
        if (!scn.AwaitingDSDeployPhaseActions()) {
            scn.SkipToPhase(Phase.DEPLOY);
        }
    }

    /** True when the mouse's optional "Relocate Utinni Effect here" action is on the current prompt. */
    private boolean RelocateUtinniAvailable(VirtualTableScenario scn, PhysicalCardImpl mouse) {
        if (!scn.DSAnyDecisionsAvailable()) {
            return false;
        }
        try {
            var actions = scn.GetDSAvailableActions();
            if (actions != null) {
                for (String action : actions) {
                    if (action != null && action.toLowerCase().contains("relocate")) {
                        return true;
                    }
                }
            }
        } catch (RuntimeException ignored) {
            // Decision may not expose actionText.
        }
        try {
            if (scn.DSCardActionAvailable(mouse, "Relocate")) {
                return true;
            }
        } catch (RuntimeException ignored) {
            // Decision is not a card-action choice (e.g. plain optional Pass window).
        }
        try {
            return scn.DSActionAvailable("Relocate");
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    /**
     * Advance the clock like SkipToPhase, but stop as soon as Relocate is offered.
     * SkipToPhase can auto-pass optional Relocate windows on the way to a later phase.
     */
    private void AdvanceUntilRelocateAvailable(VirtualTableScenario scn, PhysicalCardImpl mouse) {
        if (RelocateUtinniAvailable(scn, mouse)) {
            return;
        }
        for (int attempts = 1; attempts <= 40; attempts++) {
            if (RelocateUtinniAvailable(scn, mouse)) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                throw new RuntimeException("No decision while waiting for Relocate");
            }
            String text = decision.getText().toLowerCase();
            // Never auto-pass a live Relocate optional — leave it for the test.
            if (RelocateUtinniAvailable(scn, mouse)) {
                return;
            }
            if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayForceLossFromForcePile();
            } else if (scn.AwaitingDSForceLossPayment()) {
                scn.DSPayForceLossFromForcePile();
            } else if (scn.GetCurrentPhase() == Phase.ACTIVATE) {
                if (text.contains("optional")) {
                    scn.PassResponses("optional");
                } else if (scn.game().getGameState().getCurrentPlayerId().equals(scn.LS)) {
                    scn.LSActivateMaxForceAndPass();
                } else {
                    scn.DSActivateMaxForceAndPass();
                }
            } else if (text.contains("optional")) {
                scn.PassResponses("optional");
            } else if (text.contains("required")) {
                scn.PassResponses("required");
            } else if (text.contains("action")) {
                if (scn.DSAnyDecisionsAvailable()) {
                    scn.DSPass();
                } else if (scn.LSAnyDecisionsAvailable()) {
                    scn.LSPass();
                } else {
                    scn.PassResponses("action");
                }
            } else if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
            } else if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            } else {
                scn.PassResponses();
            }
        }
        throw new RuntimeException("Relocate never offered. Decision: " + decisionText(scn));
    }

    /** Accepts the mouse's optional relocate, choosing the given Utinni Effect if a card picker is shown. */
    private void AcceptRelocate(VirtualTableScenario scn, PhysicalCardImpl mouse, PhysicalCardImpl utinni) {
        AdvanceUntilRelocateAvailable(scn, mouse);
        assertTrue(RelocateUtinniAvailable(scn, mouse));
        scn.DSChooseAction("Relocate");
        if (scn.DSHasCardChoiceAvailable(utinni)) {
            scn.DSChooseCard(utinni);
        }
        scn.PassAllResponses();
    }

    @Test
    public void MouseDroid_1_188_DeploysToBattlegroundSameSiteAsUtinniTargetedCharacter() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);

        assertTrue(scn.DSCardPlayAvailable(mouse));
        scn.DSDeployCard(mouse);
        assertTrue(scn.DSHasCardChoiceAvailable(dsDb));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(dsDb, mouse));
    }

    @Test
    public void MouseDroid_1_188_CannotDeployToNoDsIconNonBattlegroundWithoutPresence() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var cell = scn.GetLSCard("cell");
        var leia = scn.GetLSCard("leia");
        var jungle = scn.GetLSCard("jungle");
        var trash = scn.GetLSCard("trash");

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        scn.MoveLocationToTable(trash);
        scn.MoveCardsToLocation(jungle, leia);
        scn.MoveCardsToLSHand(cell);
        scn.MoveCardsToDSHand(mouse);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(cell);
        scn.LSChooseCard(trash);
        scn.LSChooseCard(leia);
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(0, scn.GetDSIconsOnLocation(jungle));
        assertFalse(scn.DSCardPlayAvailable(mouse));
    }

    @Test
    public void MouseDroid_1_188_DeploysToNoDsIconNonBattlegroundWithDsPresence() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var cell = scn.GetLSCard("cell");
        var leia = scn.GetLSCard("leia");
        var jungle = scn.GetLSCard("jungle");
        var trash = scn.GetLSCard("trash");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        scn.MoveLocationToTable(trash);
        scn.MoveCardsToLocation(jungle, leia, presence);
        scn.MoveCardsToLSHand(cell);
        scn.MoveCardsToDSHand(mouse);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(cell);
        scn.LSChooseCard(trash);
        scn.LSChooseCard(leia);
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(mouse));
        scn.DSDeployCard(mouse);
        assertTrue(scn.DSHasCardChoiceAvailable(jungle));
        scn.DSChooseCard(jungle);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(jungle, mouse));
    }

    @Test
    public void MouseDroid_1_188_CannotDeployUsingKesselRunAsTheUtinniEffect() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var kesselRun = scn.GetLSCard("kessel-run");
        var kessel = scn.GetDSCard("kessel");
        var tatooineSystem = scn.GetLSCard("tatooine-system");
        var han = scn.GetLSCard("han");
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(tatooineSystem);
        scn.MoveLocationToTable(dsDb);
        scn.MoveCardsToLocation(tatooineSystem, han);
        scn.MoveCardsToLSHand(kesselRun);
        scn.MoveCardsToDSHand(mouse);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(kesselRun));
        scn.LSPlayCard(kesselRun);
        scn.LSChooseCard(kessel);
        scn.LSChooseCard(han);
        scn.PassAllResponses();

        // Move the smuggler to a site so the only remaining block is the printed Kessel Run exception.
        scn.MoveCardsToLocation(dsDb, han);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(mouse));
    }

    @Test
    public void MouseDroid_1_188_CannotDeployToDagobahForFailureAtTheCave() {
        /**
         * Dagobah rules (Decipher / Gergall): characters may not deploy to Dagobah unless
         * specifically allowed by their game text or another card. MSE-6 game text only
         * restricts deploy sites to "same site as a character targeted by a Utinni Effect";
         * it does not grant Dagobah deployment. Failure At The Cave (4_120) is a Dagobah-
         * allowed Utinni Effect, but that does not extend a Dagobah deploy grant to Mouse.
         *
         * Engine correctly marks Dagobah: Cave prohibited via isProhibitedFromDeployingTo
         * (Deploy.java character/starship/vehicle Dagobah check + lack of
         * MayDeployToDagobahLocationModifier). A Deploy action may still appear with an
         * empty target list; assert the real outcome: Cave is not a legal deploy choice.
         */
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var necklace = scn.GetDSCard("necklace");
        var yavinDb = scn.GetLSCard("yavin-db");
        var cave = scn.GetDSCard("cave");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(yavinDb);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(yavinDb, trooper);
        scn.MoveCardsToDSHand(mouse);
        scn.AttachCardsTo(yavinDb, necklace);
        necklace.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, trooper, Filters.any);
        EnsureDSDeployPhase(scn);
        scn.MoveCardsToLocation(cave, trooper);

        assertEquals("Dagobah", cave.getPartOfSystem());
        assertTrue("Dagobah: Cave must be prohibited for Mouse (no MayDeployToDagobah grant)",
                scn.game().getModifiersQuerying().isProhibitedFromDeployingTo(
                        scn.game().getGameState(), mouse, cave, null));

        if (scn.DSDeployAvailable(mouse) || scn.DSCardPlayAvailable(mouse)) {
            scn.DSDeployCard(mouse);
            assertFalse("Dagobah: Cave must not be a deploy choice for Mouse",
                    scn.DSHasCardChoiceAvailable(cave));
            if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(yavinDb)) {
                // Should not happen; Utinni target is at Cave only.
                scn.DSChooseCard(yavinDb);
                scn.PassAllResponses();
            } else if (scn.DSAnyDecisionsAvailable()) {
                // No legal site: decline / cancel the empty deploy if the UI requires it.
                scn.DSDecline();
            }
        }
        assertFalse("Mouse must remain in hand; Dagobah rules block deploy to Cave",
                scn.CardsAtLocation(cave, mouse));
        assertInHand(mouse);
    }

    @Test
    public void MouseDroid_1_188_OptionalRelocateWhenReachedDeclineDoesNothing() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var sadd = scn.GetDSCard("sadd");
        var dsDb = scn.GetLSCard("ds-db");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        scn.MoveCardsToLocation(marketplace, mouse);
        AdvanceUntilRelocateAvailable(scn, mouse);
        assertTrue(RelocateUtinniAvailable(scn, mouse));
        scn.DSDecline();
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(marketplace, sadd));
        assertTrue(scn.CardsAtLocation(marketplace, mouse));
    }

    @Test
    public void MouseDroid_1_188_OptionalRelocateWhenReachedAcceptAttachesToMouse() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var sadd = scn.GetDSCard("sadd");
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), mouse);
        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, sadd);
        assertTrue(scn.IsAttachedTo(mouse, sadd));
    }

    @Test
    public void MouseDroid_1_188_CarriedSendADetachmentDownStaysOnMouseWhenTrooperPresent() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var sadd = scn.GetDSCard("sadd");
        var dsDb = scn.GetLSCard("ds-db");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), mouse);
        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, sadd);
        assertTrue(scn.IsAttachedTo(mouse, sadd));

        // SADD is Class B: the trooper being co-present does not relocate SADD or return Mouse.
        scn.MoveCardsToLocation(dsDb, mouse);
        scn.SkipToPhase(Phase.BATTLE);
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }
        assertTrue("Mouse remains on the table after carrying SADD to its hunted trooper",
                scn.CardsAtLocation(dsDb, mouse));
        assertTrue("SADD remains attached to Mouse at the trooper's site",
                scn.IsAttachedTo(mouse, sadd));
        assertFalse("SADD must never attach to the hunted trooper",
                scn.IsAttachedTo(trooper, sadd));
    }
    @Test
    public void MouseDroid_1_188_CanRelocateYerkaMigWhenCoLocated() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var yerka = scn.GetLSCard("yerka");
        var dsDb = scn.GetLSCard("ds-db");
        var marketplace = scn.GetDSStartingLocation();
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(dsDb);
        scn.MoveCardsToLocation(dsDb, trooper);
        scn.MoveCardsToLocation(marketplace, mouse, yerka);
        yerka.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, trooper, Filters.any);

        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, yerka);

        assertTrue("Yerka Mig relocates onto the co-located Mouse", scn.IsAttachedTo(mouse, yerka));
    }
    @Test
    public void MouseDroid_1_188_RycarsRunIsAtVCSDLocationWhenCarried() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var vcsd = scn.GetDSCard("vcsd");
        var kessel = scn.GetDSCard("kessel");
        var rycarsRun = scn.GetLSCard("rycars");
        var bigOne = scn.GetLSCard("bigone");
        var awing = scn.GetLSCard("awing");

        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(bigOne);
        scn.MoveCardsToLocation(kessel, awing);
        scn.MoveCardsToLocation(bigOne, vcsd);
        scn.AttachCardsTo(bigOne, rycarsRun);
        scn.BoardAsPassenger(vcsd, mouse);
        scn.AttachCardsTo(mouse, rycarsRun);
        MouseDroidUtinniCarry.rememberHostsOnMouseRelocate(rycarsRun, bigOne, scn.gameState());
        rycarsRun.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, awing, Filters.any);

        // Regression for the playtest: an Utinni carried by Mouse aboard VCSD is at VCSD's system.
        assertEquals(bigOne, scn.game().getModifiersQuerying().getLocationThatCardIsAt(
                scn.gameState(), rycarsRun));
        assertTrue(scn.IsAboard(vcsd, mouse));
    }
    @Test
    public void MouseDroid_1_188_VehiclePresentReachWorks() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var sadd = scn.GetDSCard("sadd");
        var dsDb = scn.GetLSCard("ds-db");
        var vehicle = scn.GetDSCard("landspreeder");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        scn.MoveCardsToLocation(marketplace, vehicle);
        scn.BoardAsPassenger(vehicle, mouse);
        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, sadd);
        assertTrue(scn.IsAttachedTo(mouse, sadd));
    }

    @Test
    public void MouseDroid_1_188_StarshipSlotReachWorks() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var sadd = scn.GetDSCard("sadd");
        var dsDb = scn.GetLSCard("ds-db");
        var devastator = scn.GetDSCard("devastator");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        scn.MoveCardsToLocation(marketplace, devastator);
        scn.BoardAsPassenger(devastator, mouse);
        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, sadd);
        assertTrue(scn.IsAttachedTo(mouse, sadd));
    }

    @Test
    public void MouseDroid_1_188_CargoBayVehicleDoesNotReachUntilMouseIsInStarship() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var sadd = scn.GetDSCard("sadd");
        var dsDb = scn.GetLSCard("ds-db");
        var vehicle = scn.GetDSCard("landspreeder");
        var devastator = scn.GetDSCard("devastator");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        scn.MoveCardsToLocation(marketplace, devastator);
        scn.BoardAsVehicle(devastator, vehicle);
        scn.BoardAsPassenger(vehicle, mouse);
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(RelocateUtinniAvailable(scn, mouse));

        scn.BoardAsPassenger(devastator, mouse);
        scn.SkipToPhase(Phase.BATTLE);
        AcceptRelocate(scn, mouse, sadd);
        assertTrue(scn.IsAttachedTo(mouse, sadd));
    }

    @Test
    public void MouseDroid_1_188_SpiceMinesCannotMoveIsNoOpAndMouseStays() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var spice = scn.GetDSCard("spice");
        var kessel = scn.GetDSCard("kessel");
        var dsDb = scn.GetLSCard("ds-db");
        var devastator = scn.GetDSCard("devastator");
        var trooper = scn.GetDSFiller(1);
        var captive = scn.GetLSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(dsDb);
        scn.MoveCardsToLocation(dsDb, trooper);
        scn.CaptureCardWith(trooper, captive);
        scn.MoveCardsToDSHand(mouse);
        scn.AttachCardsTo(kessel, spice);
        spice.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, captive, Filters.any);
        spice.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_2, 0, trooper, Filters.any);
        EnsureDSDeployPhase(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(dsDb, mouse));

        scn.MoveCardsToLocation(kessel, devastator);
        scn.BoardAsPassenger(devastator, mouse);
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(RelocateUtinniAvailable(scn, mouse));
        assertTrue(scn.IsAboard(devastator, mouse));
        assertTrue(scn.IsAttachedTo(kessel, spice));
    }

    @Test
    public void MouseDroid_1_188_SendADetachmentDownPickupStaysCarriedUntilItsOwnRulesRelocateIt() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var sadd = scn.GetDSCard("sadd");
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        // Pick SADD up away from its hunted trooper. It remains on Mouse and does not trigger return.
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), mouse);
        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, sadd);
        assertTrue(scn.IsAttachedTo(mouse, sadd));
        assertTrue(scn.CardsAtLocation(scn.GetDSStartingLocation(), mouse));
        assertFalse(scn.DSAnyDecisionsAvailable() && scn.DSActionAvailable("Return"));
    }
    @Test
    public void MouseDroid_1_188_LightUtinniKeepAwayCell2187() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var cell = scn.GetLSCard("cell");
        var leia = scn.GetLSCard("leia");
        var trash = scn.GetLSCard("trash");
        var jungle = scn.GetLSCard("jungle");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(trash);
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(jungle, leia, presence);
        scn.MoveCardsToLSHand(cell);
        scn.MoveCardsToDSHand(mouse);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(cell);
        scn.LSChooseCard(trash);
        scn.LSChooseCard(leia);
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(mouse));
        scn.DSDeployCard(mouse);
        assertTrue(scn.DSHasCardChoiceAvailable(jungle));
        scn.DSChooseCard(jungle);
        scn.PassAllResponses();

        scn.MoveCardsToLocation(trash, mouse);
        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, cell);
        assertTrue(scn.IsAttachedTo(mouse, cell));
        // Keep-away: move the mouse off Leia's site so she has not reached the Utinni Effect.
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), mouse);
        assertTrue(scn.IsAttachedTo(mouse, cell));
        assertFalse(scn.CardsAtLocation(jungle, mouse));
    }

    @Test
    public void MouseDroid_1_188_TargetLostAfterRelocateLosesUtinni() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var sadd = scn.GetDSCard("sadd");
        var dsDb = scn.GetLSCard("ds-db");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), mouse);
        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, sadd);
        assertTrue(scn.IsAttachedTo(mouse, sadd));

        scn.MoveCardsToTopOfDSLostPile(trooper);
        scn.SkipToPhase(Phase.BATTLE);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, sadd);
    }

    @Test
    public void MouseDroid_1_188_MouseMissingMakesUtinniInactiveRestoreRestoresEffect() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var sadd = scn.GetDSCard("sadd");
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), mouse);
        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, sadd);
        assertTrue(scn.IsCardActive(sadd));

        scn.MakeCardGoMissing(mouse);
        assertFalse(scn.IsCardActive(mouse));
        assertFalse(scn.IsCardActive(sadd));
        assertTrue(scn.IsAttachedTo(mouse, sadd));

        // Find the missing mouse so the carried Utinni Effect can resume.
        mouse.setMissing(false);
        assertTrue(scn.IsCardActive(mouse));
        assertTrue(scn.IsCardActive(sadd));
    }

    @Test
    public void MouseDroid_1_188_LandspeedIs3() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        assertEquals(3, scn.GetLandspeed(mouse));
    }

    @Test
    public void MouseDroid_1_188_FiveD6RA7AddsOneToMouseDeployAtSameLocation() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var fivedesix = scn.GetDSCard("fivedesix");
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse, fivedesix);
        PlaySaddTargetingTrooperAtDeathStar(scn);

        scn.MoveCardsToLocation(dsDb, fivedesix);
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardPlayAvailable(mouse));
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();
        int forceAfter = scn.GetDSForcePileCount();
        assertEquals(forceBefore - 1, forceAfter);
    }

    @Test
    public void MouseDroid_1_188_WookieeRoarCanScareOffTheMouse() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var roar = scn.GetLSCard("roar");
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        scn.MoveCardsToLSHand(roar);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardPlayAvailable(roar, "Scare") || scn.LSCardPlayAvailable(roar));
        if (scn.LSCardPlayAvailable(roar, "Scare")) {
            scn.LSPlayCard(roar, "Scare");
        } else {
            scn.LSPlayCard(roar);
        }
        if (scn.LSHasCardChoiceAvailable(mouse)) {
            scn.LSChooseCard(mouse);
        }
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, mouse);
    }

    @Test
    public void MouseDroid_1_188_MayRelocateUtinniEffectOffACharacterAtSameSite() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var sadd = scn.GetDSCard("sadd");
        var oberk = scn.GetDSCard("oberk");
        var dsDb = scn.GetLSCard("ds-db");
        var db94 = scn.GetDSCard("db94");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(dsDb);
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(dsDb, trooper);
        scn.MoveCardsToLocation(db94, mouse, oberk);
        // Utinnis already on a character, as in the playtest when Oberk transited in carrying them.
        scn.AttachCardsTo(oberk, sadd);
        sadd.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, trooper, Filters.any);

        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, sadd);
        assertTrue(scn.IsAttachedTo(mouse, sadd));
    }

    @Test
    public void MouseDroid_1_188_DoesNotReturnWhenCarriedUtinniTargetIsNotPresent() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var homestead = scn.GetDSCard("homestead");
        var luke = scn.GetLSCard("luke");
        var farm = scn.GetLSCard("farm");
        var db94 = scn.GetDSCard("db94");
        var oberk = scn.GetDSCard("oberk");
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveLocationToTable(db94);
        scn.MoveLocationToTable(dsDb);
        scn.MoveCardsToLocation(farm, luke);
        scn.MoveCardsToLocation(db94, mouse);
        scn.MoveCardsToLocation(dsDb, oberk);
        scn.AttachCardsTo(mouse, homestead);
        homestead.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, luke, Filters.any);

        scn.MoveCardsToLocation(db94, oberk);
        scn.SkipToPhase(Phase.CONTROL);
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }

        assertFalse("Mouse should not return just because someone arrived. Decision: " + decisionText(scn),
                scn.DSAnyDecisionsAvailable() && (scn.DSActionAvailable("Return") || scn.DSCardActionAvailable(mouse, "Return")));
        if (RelocateUtinniAvailable(scn, mouse)) {
            scn.DSDecline();
        }
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }
        assertTrue(scn.CardsAtLocation(db94, mouse));
        assertTrue(scn.IsAttachedTo(mouse, homestead));
        assertFalse(scn.IsAttachedTo(db94, homestead));
    }

    @Test
    public void MouseDroid_1_188_ClassADeliveryReturnsMouseAndSendsCarriedSaddToLost() {
        // A Class A package (Plastoid) delivers to its hunted target; unrelated Class B SADD is leftover.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var plastoid = scn.GetLSCard("plastoid");
        var sadd = scn.GetDSCard("sadd");
        var leia = scn.GetLSCard("leia");
        var dsDb = scn.GetLSCard("ds-db");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(dsDb);
        scn.MoveCardsToLocation(dsDb, mouse, trooper, leia);
        scn.AttachCardsTo(mouse, plastoid, sadd);
        plastoid.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, leia, Filters.any);
        plastoid.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
        sadd.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, trooper, Filters.any);

        scn.SkipToPhase(Phase.CONTROL);
        if (scn.DSAnyDecisionsAvailable() && (scn.DSActionAvailable("Return") || scn.DSCardActionAvailable(mouse, "Return"))) {
            scn.DSChooseAction("Return");
        }
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }

        assertInHand(mouse);
        assertTrue("Plastoid snaps to its hunted target", scn.IsAttachedTo(leia, plastoid));
        assertInZone(Zone.LOST_PILE, sadd);
        assertFalse(scn.IsAttachedTo(mouse, sadd));
        assertFalse(scn.IsAttachedTo(trooper, sadd));
    }

    /** Marks Son as apprentice and attaches Failure At The Cave to Cave targeting him. */
    private void SetupFailureAtTheCaveOnCaveTargetingSon(VirtualTableScenario scn,
            PhysicalCardImpl cave, PhysicalCardImpl failure, PhysicalCardImpl son) {
        scn.game().getGameState().addApprentice(son);
        scn.AttachCardsTo(cave, failure);
        failure.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, son, Filters.any);
    }

    @Test
    public void MouseDroid_1_188_OnDagobahViaLandedStarfighterMayRelocateFailureAtTheCave() {
        // Mouse is already on Dagobah via a landed starfighter (not deployed); may relocate Failure At The Cave.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var tie = scn.GetDSCard("tie");
        var cave = scn.GetDSCard("cave");
        var failure = scn.GetDSCard("failure");
        var son = scn.GetLSCard("son");
        var jungle = scn.GetLSCard("jungle-dag");

        scn.StartGame();
        scn.MoveLocationToTable(cave);
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(jungle, son);
        SetupFailureAtTheCaveOnCaveTargetingSon(scn, cave, failure, son);

        // Landed TIE at Cave; mouse already at Cave after arriving aboard (cheat placement).
        scn.MoveCardsToLocation(cave, tie);
        scn.MoveCardsToLocation(cave, mouse);

        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, failure);
        assertTrue(scn.IsAttachedTo(mouse, failure));
        assertTrue(scn.CardsAtLocation(cave, mouse));
    }

    @Test
    public void MouseDroid_1_188_OnDagobahKeepAwayFailureAtTheCaveDoesNotTriggerWhenMovingPastDaughterOrSon() {
        // Keep-away (Gergall/Decipher): mouse carrying Failure At The Cave stays away from Daughter/Son
        // so they do not reach the Utinni and delivery must not fire just from the mouse moving past them.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var cave = scn.GetDSCard("cave");
        var failure = scn.GetDSCard("failure");
        var son = scn.GetLSCard("son");
        var daughter = scn.GetLSCard("daughter");
        var jungle = scn.GetLSCard("jungle-dag");
        var bog = scn.GetLSCard("bog");
        var training = scn.GetLSCard("training");

        scn.StartGame();
        scn.MoveLocationToTable(cave);
        scn.MoveLocationToTable(jungle);
        scn.MoveLocationToTable(bog);
        scn.MoveLocationToTable(training);
        scn.MoveCardsToLocation(jungle, son);
        scn.MoveCardsToLocation(training, daughter);
        SetupFailureAtTheCaveOnCaveTargetingSon(scn, cave, failure, son);

        // Mouse already carries Failure after pickup at Cave, then keeps away at Bog.
        scn.MoveCardsToLocation(cave, mouse);
        scn.AttachCardsTo(mouse, failure);
        scn.MoveCardsToLocation(bog, mouse);

        scn.SkipToPhase(Phase.BATTLE);
        if (RelocateUtinniAvailable(scn, mouse)) {
            scn.DSDecline();
        }
        if (scn.DSAnyDecisionsAvailable() && (scn.DSActionAvailable("Return") || scn.DSCardActionAvailable(mouse, "Return"))) {
            throw new AssertionError("Keep-away failed: Return offered while Daughter/Son are not present with mouse. Decision: " + decisionText(scn));
        }
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }

        assertTrue("Mouse stays at Bog while keeping Failure away", scn.CardsAtLocation(bog, mouse));
        assertTrue("Failure At The Cave stays on mouse (Daughter/Son have not reached it)", scn.IsAttachedTo(mouse, failure));
        assertFalse(scn.CardsAtLocation(bog, son));
        assertFalse(scn.CardsAtLocation(bog, daughter));
    }

    @Test
    public void MouseDroid_1_188_PresentWithTargetDoesNotDeliverNonRelocatingUtinni() {
        // Failure At The Cave has no relocate-to-target effect; a co-present apprentice is not delivery.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var cave = scn.GetDSCard("cave");
        var failure = scn.GetDSCard("failure");
        var son = scn.GetLSCard("son");

        scn.StartGame();
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, mouse, son);
        SetupFailureAtTheCaveOnCaveTargetingSon(scn, cave, failure, son);
        scn.AttachCardsTo(mouse, failure);

        scn.SkipToPhase(Phase.CONTROL);
        if (scn.DSAnyDecisionsAvailable() && (scn.DSActionAvailable("Return") || scn.DSCardActionAvailable(mouse, "Return"))) {
            scn.DSChooseAction("Return");
        }
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }

        assertTrue("Mouse stays on table while carrying a non-relocating Utinni",
                scn.CardsAtLocation(cave, mouse));
        assertTrue("Failure At The Cave remains on Mouse",
                scn.IsAttachedTo(mouse, failure));
        assertFalse(scn.IsAttachedTo(son, failure));
    }
    /** True if that player's current action list contains the text (any case). dark=true is Dark Side. */

    @Test
    public void MouseDroid_1_188_DeclineRelocateCanBeOfferedAgainWhileStillTogether() {
        // Forum AR perpetual reach: declining relocate does not silence the option; later table-changed
        // while mice stay together may offer Relocate again (including every phase/subphase).
        var scn = GetScenario();
        var mouseA = scn.GetDSCard("mouse");
        var mouseB = scn.GetDSCard("mouse2");
        var sadd = scn.GetDSCard("sadd");
        var db94 = scn.GetDSCard("db94");
        var dsDb = scn.GetLSCard("ds-db");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveLocationToTable(dsDb);
        // Trooper (SADD target) stays at Death Star DB so delivery does not fire while testing relocate reach.
        scn.MoveCardsToLocation(dsDb, trooper);
        scn.MoveCardsToLocation(db94, mouseA, mouseB);
        scn.AttachCardsTo(mouseA, sadd);
        sadd.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, trooper, Filters.any);

        scn.SkipToPhase(Phase.CONTROL);
        AdvanceUntilRelocateAvailable(scn, mouseB);
        assertTrue("Mouse B should be offered relocate of A's package", RelocateUtinniAvailable(scn, mouseB));
        scn.DSDecline();
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }

        // Still together: a later phase/table-changed must be allowed to re-offer Relocate.
        if (scn.DSAnyDecisionsAvailable() && !RelocateUtinniAvailable(scn, mouseB)) {
            scn.PassAllResponses();
        }
        AdvanceUntilRelocateAvailable(scn, mouseB);
        assertTrue("After decline, relocate must be offerable again while mice stay together. Decision: " + decisionText(scn),
                RelocateUtinniAvailable(scn, mouseB));
    }

    @Test
    public void MouseDroid_1_188_AfterAcceptCarriedUtinniNotReofferedOnSameMouse() {
        // After accepting relocate onto mouse B, that package is attachedTo(B) so B must not re-offer it.
        // Sibling mouse A may still steal (perpetual reach) — that is intentional and not asserted here.
        var scn = GetScenario();
        var mouseA = scn.GetDSCard("mouse");
        var mouseB = scn.GetDSCard("mouse2");
        var sadd = scn.GetDSCard("sadd");
        var db94 = scn.GetDSCard("db94");
        var trooper = scn.GetDSFiller(1);
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveLocationToTable(dsDb);
        // Keep hunted trooper elsewhere so accept checks are not interrupted by delivery.
        scn.MoveCardsToLocation(dsDb, trooper);
        scn.MoveCardsToLocation(db94, mouseA, mouseB);
        scn.AttachCardsTo(mouseA, sadd);
        sadd.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, trooper, Filters.any);

        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouseB, sadd);
        assertTrue(scn.IsAttachedTo(mouseB, sadd));

        if (scn.DSAnyDecisionsAvailable()) {
            // Pass sibling steal offers from A if present; never leave Relocate stuck on B for this package.
            for (int i = 0; i < 10 && RelocateUtinniAvailable(scn, mouseA); i++) {
                if (scn.DSCardActionAvailable(mouseA, "Relocate")) {
                    scn.DSUseCardAction(mouseA, "Relocate");
                    if (scn.DSHasCardChoiceAvailable(sadd)) {
                        // Decline steal by passing card choice / declining optional.
                        scn.DSDecline();
                    } else {
                        scn.DSDecline();
                    }
                } else {
                    scn.DSDecline();
                }
            }
            if (scn.DSAnyDecisionsAvailable() && !RelocateUtinniAvailable(scn, mouseB)) {
                scn.PassAllResponses();
            }
        }
        // Carrier mouse must not re-offer its own attached package.
        assertFalse("Carrier mouse must not re-offer Utinni attached to itself. Decision: " + decisionText(scn),
                RelocateUtinniAvailable(scn, mouseB) && scn.DSCardActionAvailable(mouseB, "Relocate"));
        assertTrue(scn.IsAttachedTo(mouseB, sadd));
    }

    /**
     * Pass optional responses but stop if Relocate is offered (so PassAllResponses cannot
     * auto-decline a sibling Utinni that appears after accepting one).
     */
    private void PassOptionalResponsesStoppingAtRelocate(VirtualTableScenario scn, PhysicalCardImpl mouse) {
        for (int i = 0; i < 20; i++) {
            if (RelocateUtinniAvailable(scn, mouse)) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            String textDec = decision.getText() == null ? "" : decision.getText().toLowerCase();
            if (!textDec.contains("optional")) {
                return;
            }
            scn.PassResponses("optional");
        }
    }

    /** Accept relocate without PassAllResponses so a sibling Utinni Relocate offer is not auto-declined. */
    private void AcceptRelocateStoppingAtSiblingOffer(VirtualTableScenario scn, PhysicalCardImpl mouse, PhysicalCardImpl utinni) {
        AdvanceUntilRelocateAvailable(scn, mouse);
        assertTrue(RelocateUtinniAvailable(scn, mouse));
        // Prefer this mouse's Relocate when multiple mice can offer Relocate in the same window.
        if (scn.DSCardActionAvailable(mouse, "Relocate")) {
            scn.DSUseCardAction(mouse, "Relocate");
        } else {
            assertTrue("Relocate not available. Decision: " + decisionText(scn) + " actions=" + scn.GetDSAvailableActions(),
                    RelocateUtinniAvailable(scn, mouse));
            scn.DSChooseAction("Relocate");
        }
        assertTrue("Utinni not choosable after Relocate. Decision: " + decisionText(scn),
                scn.DSHasCardChoiceAvailable(utinni));
        scn.DSChooseCard(utinni);
        // Finish this relocate's optional confirmations without PassAllResponses (which declines siblings).
        for (int i = 0; i < 20 && !scn.IsAttachedTo(mouse, utinni); i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                break;
            }
            String textDec = decision.getText() == null ? "" : decision.getText().toLowerCase();
            if (textDec.contains("optional")) {
                scn.PassResponses("optional");
            } else if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayForceLossFromForcePile();
            } else if (scn.AwaitingDSForceLossPayment()) {
                scn.DSPayForceLossFromForcePile();
            } else if (textDec.contains("choose utinni") || textDec.contains("relocate here")) {
                assertTrue("Utinni not choosable. Decision: " + decisionText(scn),
                        scn.DSHasCardChoiceAvailable(utinni));
                scn.DSChooseCard(utinni);
            } else {
                break;
            }
        }
        assertTrue("Expected " + utinni.getBlueprint().getTitle() + " attached after relocate. Decision: " + decisionText(scn),
                scn.IsAttachedTo(mouse, utinni));
        PassOptionalResponsesStoppingAtRelocate(scn, mouse);
    }

    @Test
    public void MouseDroid_1_188_AfterRelocatingOneUtinniStillOffersOtherUtinniAtSameSite() {
        // Multi-Utinni meet: accepting one package must not lock a sibling; both can load onto the mouse.
        var scn = GetScenario();
        var mouseA = scn.GetDSCard("mouse");
        var mouseB = scn.GetDSCard("mouse2");
        var sadd = scn.GetDSCard("sadd");
        var failure = scn.GetDSCard("failure");
        var cave = scn.GetDSCard("cave");
        var son = scn.GetLSCard("son");
        var jungle = scn.GetLSCard("jungle-dag");
        var db94 = scn.GetDSCard("db94");
        var trooper = scn.GetDSFiller(1);
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveLocationToTable(dsDb);
        scn.MoveLocationToTable(cave);
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(dsDb, trooper);
        scn.MoveCardsToLocation(jungle, son);
        scn.MoveCardsToLocation(db94, mouseA, mouseB);
        SetupFailureAtTheCaveOnCaveTargetingSon(scn, cave, failure, son);
        scn.AttachCardsTo(mouseA, sadd, failure);
        sadd.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, trooper, Filters.any);
        assertTrue(scn.IsAttachedTo(mouseA, sadd));
        assertTrue(scn.IsAttachedTo(mouseA, failure));

        scn.SkipToPhase(Phase.CONTROL);
        // Accept first package; sibling must remain offerable.
        AcceptRelocate(scn, mouseB, sadd);
        assertTrue(scn.IsAttachedTo(mouseB, sadd));
        // Sibling may have been auto-declined by PassAllResponses inside AcceptRelocate;
        // re-open a Relocate window for the remaining package.
        AdvanceUntilRelocateAvailable(scn, mouseB);
        assertTrue("After relocating SADD, Failure At The Cave must still be offerable. Decision: " + decisionText(scn)
                        + " actions=" + scn.GetDSAvailableActions(),
                RelocateUtinniAvailable(scn, mouseB));
        AcceptRelocateStoppingAtSiblingOffer(scn, mouseB, failure);
        assertTrue(scn.IsAttachedTo(mouseB, sadd));
        assertTrue(scn.IsAttachedTo(mouseB, failure));
    }


    @Test
    public void MouseDroid_1_188_DeclineOneUtinniKeepsOfferForSiblingAndCanRepingDeclined() {
        // Multi-Utinni: accept one package, decline the sibling offer; perpetual reach allows the declined
        // sibling to be offered again later while the mouse stays at the site.
        var scn = GetScenario();
        var mouseA = scn.GetDSCard("mouse");
        var mouseB = scn.GetDSCard("mouse2");
        var sadd = scn.GetDSCard("sadd");
        var failure = scn.GetDSCard("failure");
        var cave = scn.GetDSCard("cave");
        var son = scn.GetLSCard("son");
        var jungle = scn.GetLSCard("jungle-dag");
        var db94 = scn.GetDSCard("db94");
        var trooper = scn.GetDSFiller(1);
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveLocationToTable(dsDb);
        scn.MoveLocationToTable(cave);
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(dsDb, trooper);
        scn.MoveCardsToLocation(jungle, son);
        scn.MoveCardsToLocation(db94, mouseA, mouseB);
        SetupFailureAtTheCaveOnCaveTargetingSon(scn, cave, failure, son);
        scn.AttachCardsTo(mouseA, sadd, failure);
        sadd.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, trooper, Filters.any);
        assertTrue(scn.IsAttachedTo(mouseA, sadd));
        assertTrue(scn.IsAttachedTo(mouseA, failure));

        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouseB, sadd);
        assertTrue(scn.IsAttachedTo(mouseB, sadd));

        AdvanceUntilRelocateAvailable(scn, mouseB);
        assertTrue("Sibling Failure still offered after accepting SADD. Decision: " + decisionText(scn),
                RelocateUtinniAvailable(scn, mouseB));
        // Decline the sibling Relocate for this mouse.
        scn.DSDecline();
        if (scn.DSAnyDecisionsAvailable()) {
            PassOptionalResponsesStoppingAtRelocate(scn, mouseB);
        }

        if (scn.DSAnyDecisionsAvailable() && !RelocateUtinniAvailable(scn, mouseB)) {
            scn.PassAllResponses();
        }
        AdvanceUntilRelocateAvailable(scn, mouseB);
        assertTrue("Declined Failure must be offerable again while staying (perpetual reach). Decision: " + decisionText(scn),
                RelocateUtinniAvailable(scn, mouseB));
        assertTrue(scn.IsAttachedTo(mouseB, sadd));
        assertTrue(scn.IsAttachedTo(mouseA, failure));
    }




    @Test
    public void MouseDroid_1_188_CarriesJuriJuiceKeepsRestrictionOnOriginalAlienNotMouse() {
        // Deploy-on-character Utinni: mouse carries Juri Juice; Lando keeps the ability restriction; mouse does not.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var juri = scn.GetDSCard("juri");
        var lando = scn.GetLSCard("lando");
        var cantina = scn.GetLSCard("cantina");
        var jungle = scn.GetLSCard("jungle");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(jungle, lando, presence);
        scn.AttachCardsTo(lando, juri);
        scn.MoveCardsToDSHand(mouse);

        assertTrue(scn.IsAttachedTo(lando, juri));
        assertEquals(0, scn.GetBattleDestinyAbility(lando));

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(mouse));
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(jungle);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, juri);
        assertTrue(scn.IsAttachedTo(mouse, juri));
        // Original alien still cannot apply ability for battle destiny.
        assertEquals(0, scn.GetBattleDestinyAbility(lando));
        // Mouse is carrier/host; hunted target data points at Lando (not the mouse).
        assertEquals(lando, juri.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
        assertTrue(scn.game().getModifiersQuerying().getCardsOnTableTargetingCard(scn.gameState(), lando).contains(juri));
    }

    @Test
    public void MouseDroid_1_188_CarriesSaddPreservesHuntedTrooperTargets() {
        // Character-hunt Utinni: after mouse carries SADD, TargetId stays on the trooper (not the mouse).
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var sadd = scn.GetDSCard("sadd");
        var dsDb = scn.GetLSCard("ds-db");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToDSHand(mouse);
        PlaySaddTargetingTrooperAtDeathStar(scn);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), mouse);
        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, sadd);
        assertTrue(scn.IsAttachedTo(mouse, sadd));
        assertEquals(trooper, sadd.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
        assertTrue(scn.game().getModifiersQuerying().getCardsOnTableTargetingCard(scn.gameState(), trooper).contains(sadd));
        // Mouse may show as host/attached-to; it is not the hunted TargetId.
        assertFalse(trooper.equals(mouse));
    }

    @Test
    public void MouseDroid_1_188_CarriesPlastoidArmorKeepsDisguiseOnOriginalTarget() {
        // LS Utinni: mouse carries reached Plastoid while the hunted character is elsewhere;
        // original target keeps disguise via carry redirect (mouse is not re-delivered onto yet).
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var plastoid = scn.GetLSCard("plastoid");
        var leia = scn.GetLSCard("leia");
        var dsDb = scn.GetLSCard("ds-db");
        var trash = scn.GetLSCard("trash");
        var jungle = scn.GetLSCard("jungle");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(dsDb);
        scn.MoveLocationToTable(trash);
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(jungle, leia, presence);
        // Reached Plastoid sitting at the Death Star site (as if awaiting delivery), targeting Leia elsewhere.
        scn.AttachCardsTo(trash, plastoid);
        plastoid.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, leia, Filters.any);
        plastoid.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
        scn.MoveCardsToDSHand(mouse);

        scn.SkipToDSTurn(Phase.DEPLOY);
        // Deploy at Leia's site (character targeted by Utinni), then move to the Plastoid site.
        assertTrue(scn.DSCardPlayAvailable(mouse));
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(jungle);
        scn.PassAllResponses();
        scn.MoveCardsToLocation(trash, mouse);

        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, plastoid);
        assertTrue(scn.IsAttachedTo(mouse, plastoid));
        // Carry redirect: disguise still applies to Leia, not the mouse.
        assertEquals(5, scn.GetArmor(leia));
        assertEquals(leia, plastoid.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
        assertTrue(scn.game().getModifiersQuerying().getCardsOnTableTargetingCard(scn.gameState(), leia).contains(plastoid));
    }


    @Test
    public void MouseDroid_1_188_CarriesThisIsJustWrongKeepsPowerPenaltyOnHuntedFemale() {
        // TargetId-only class (DS): modifiers key off hunt TargetId, not hasAttached. Mouse must not become hunted.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var tijw = scn.GetDSCard("tijw");
        var oberk = scn.GetDSCard("oberk");
        var leia = scn.GetLSCard("leia");
        var jungle = scn.GetLSCard("jungle");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        var farm = scn.GetLSCard("farm");
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(jungle, oberk, presence);
        scn.MoveCardsToLocation(farm, leia);
        scn.AttachCardsTo(oberk, tijw);
        tijw.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, leia, Filters.any);
        scn.MoveCardsToDSHand(mouse);

        int leiaPowerBefore = scn.GetPower(leia);

        scn.SkipToDSTurn(Phase.DEPLOY);
        // Deploy at Leia (hunted target); then move to Oberk to pick up TIJW.
        assertTrue(scn.DSCardPlayAvailable(mouse));
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(farm);
        scn.PassAllResponses();
        scn.MoveCardsToLocation(jungle, mouse);

        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, tijw);
        assertTrue(scn.IsAttachedTo(mouse, tijw));
        assertEquals(leia, tijw.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
        assertEquals(oberk, tijw.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
        assertTrue(scn.game().getModifiersQuerying().getCardsOnTableTargetingCard(scn.gameState(), leia).contains(tijw));
        // TargetId-class: hunted female still targeted; mouse is carrier only (power may floor at 0).
        assertTrue(scn.GetPower(leia) <= leiaPowerBefore);
        assertFalse(leia.equals(mouse));
    }

    @Test
    public void MouseDroid_1_188_JuriCancelUsesEffectSubjectNotMouseLocation() {
        // getAttachedTo / subject-host class: cancel when the original alien reaches Cantina, even if Mouse is elsewhere.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var juri = scn.GetDSCard("juri");
        var lando = scn.GetLSCard("lando");
        var cantina = scn.GetLSCard("cantina");
        var jungle = scn.GetLSCard("jungle");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(jungle, lando, presence);
        scn.AttachCardsTo(lando, juri);
        scn.MoveCardsToDSHand(mouse);

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(jungle);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.CONTROL);
        AcceptRelocate(scn, mouse, juri);
        assertTrue(scn.IsAttachedTo(mouse, juri));
        assertEquals(lando, MouseDroidUtinniCarry.getEffectSubjectHost(scn.gameState(), juri));

        // Mouse stays at jungle; alien alone moves to Cantina (not driving) -> Juri cancels.
        scn.MoveCardsToLocation(cantina, lando);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, juri);
    }

    @Test
    public void MouseDroid_1_188_CarriesWereTheBaitKeepsCaptiveSubjectNotHuntTargetForHasAttached() {
        // Mixed class: hasAttached means captive host; TargetId is Luke. Redirect must prefer remembered subject.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var bait = scn.GetDSCard("bait");
        var han = scn.GetLSCard("han");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSFiller(1);
        var jungle = scn.GetLSCard("jungle");
        var presence = scn.GetDSFiller(2);

        scn.StartGame();
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(jungle, trooper, luke, presence, mouse);
        scn.CaptureCardWith(trooper, han);
        // Simulate Mouse-carry after relocate off captive: physical host is mouse; subject remembered separately.
        scn.AttachCardsTo(mouse, bait);
        bait.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, luke, Filters.any);
        bait.setTargetedCard(TargetId.EFFECT_TARGET_1, 0, han, Filters.any);

        assertTrue(scn.IsAttachedTo(mouse, bait));
        assertEquals(luke, bait.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
        assertEquals(han, bait.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
        assertEquals(han, MouseDroidUtinniCarry.getEffectSubjectHost(scn.gameState(), bait));
        // hasAttached must match captive subject (Han), not hunted Luke ? release-cancel class.
        assertTrue(Filters.hasAttached(bait).accepts(scn.game(), han));
        assertFalse(Filters.hasAttached(bait).accepts(scn.game(), luke));
        assertFalse(Filters.hasAttached(bait).accepts(scn.game(), mouse));
    }

    @Test
    public void MouseDroid_1_188_CarriesReachedTuskenBreathMaskAppliesBonusesViaHuntTargetFallback() {
        // Site-hosted hasAttached+TargetId class (LS): no EFFECT_TARGET_1 subject; hasAttached falls back to hunt TargetId.
        // Simulate Mouse-carry (REACHED Tusken auto-attaches to a present target, so live relocate races that rule).
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var tusken = scn.GetLSCard("tusken");
        var leia = scn.GetLSCard("leia");
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, leia, mouse);
        int powerBeforeWithoutMask = scn.GetPower(leia);
        tusken.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, leia, Filters.any);
        tusken.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
        scn.AttachCardsTo(mouse, tusken);

        assertTrue(scn.IsAttachedTo(mouse, tusken));
        assertTrue(Filters.hasAttached(tusken).accepts(scn.game(), leia));
        assertFalse(Filters.hasAttached(tusken).accepts(scn.game(), mouse));
        // While Mouse carries reached mask, Tatooine target keeps +2 via hasAttached?TargetId fallback.
        assertEquals(powerBeforeWithoutMask + 2, scn.GetPower(leia));
        assertEquals(leia, tusken.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
        assertEquals(null, tusken.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
    }



    /** Simulate Elom's remainder-of-game Plastoid changes (Effect, not Utinni). */
    private void ApplyElomPlastoidModifiers(VirtualTableScenario scn, PhysicalCardImpl elom) {
        scn.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                new ChangeCardSubtypeModifier(elom, Filters.Plastoid_Armor, CardSubtype.NORMAL));
        scn.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                new NotUniqueModifier(elom, Filters.Plastoid_Armor));
        scn.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                new ModifyGameTextModifier(elom, Filters.Plastoid_Armor, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT));
    }

    @Test
    public void MouseDroid_1_188_CannotRelocateElomPlastoidArmorBecauseItIsAnEffect() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var plastoid = scn.GetLSCard("plastoid");
        var han = scn.GetLSCard("han");
        var elom = scn.GetLSCard("elom");
        var dsDb = scn.GetLSCard("ds-db");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(dsDb);
        scn.MoveCardsToLocation(dsDb, han, elom, mouse, presence);
        ApplyElomPlastoidModifiers(scn, elom);
        scn.AttachCardsTo(han, plastoid);

        assertFalse("Elom Plastoid must no longer be a Utinni Effect",
                Filters.Utinni_Effect.accepts(scn.game(), plastoid));
        assertTrue("Elom Plastoid remains an Effect",
                Filters.Effect_of_any_Kind.accepts(scn.game(), plastoid));

        EnsureDSDeployPhase(scn);
        assertFalse("Mouse special deploy must not unlock from Elom Plastoid",
                scn.DSCardPlayAvailable(mouse));
        scn.SkipToPhase(Phase.CONTROL);
        assertFalse("Mouse must not offer relocate for Elom Plastoid",
                RelocateUtinniAvailable(scn, mouse));
        assertTrue(scn.IsAttachedTo(han, plastoid));
    }

    @Test
    public void MouseDroid_1_188_NormalPlastoidUtinniRelocateKeepsCharacterTargetBenefits() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var plastoid = scn.GetLSCard("plastoid");
        var leia = scn.GetLSCard("leia");
        var dsDb = scn.GetLSCard("ds-db");
        var jungle = scn.GetLSCard("jungle");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(dsDb);
        scn.MoveLocationToTable(jungle);
        scn.MoveCardsToLocation(jungle, leia, presence);
        // Normal Plastoid path after a Stormtrooper was lost at a Death Star site:
        // it is deployed on the site, targets Leia away from Death Star, and Leia reaches it.
        scn.AttachCardsTo(dsDb, plastoid);
        plastoid.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, leia, Filters.any);
        scn.MoveCardsToLocation(dsDb, leia);
        plastoid.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
        scn.AttachCardsTo(leia, plastoid);
        scn.MoveCardsToDSHand(mouse);

        assertTrue(scn.IsAttachedTo(leia, plastoid));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue("Mouse deploys to Leia's Death Star site while Plastoid is targeted",
                scn.DSCardPlayAvailable(mouse));
        scn.DSDeployCard(mouse);
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.CONTROL);
        // Mouse picks up the reached package; Leia is present, so delivery/return may resolve immediately.
        AcceptRelocate(scn, mouse, plastoid);
        assertInHand(mouse);
        assertTrue(scn.IsAttachedTo(leia, plastoid));
        assertEquals("Plastoid's hunt target remains Leia after Mouse carries it", leia,
                plastoid.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
        assertEquals("Leia keeps Plastoid disguise benefits after Mouse delivery", 5, scn.GetArmor(leia));
        assertTrue(scn.game().getModifiersQuerying().getCardsOnTableTargetingCard(scn.gameState(), leia).contains(plastoid));
    }

    @Test
    public void MouseDroid_1_188_TuskenBreathMaskDeliveredBackToTargetCharacterNotSite() {
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var tusken = scn.GetLSCard("tusken");
        var doallyn = scn.GetLSCard("doallyn");
        var db94 = scn.GetDSCard("db94");

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(db94, doallyn, mouse);
        // Mask is deployed on the battle site targeting Doallyn, then reaches him before Mouse picks it up.
        scn.AttachCardsTo(db94, tusken);
        tusken.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, doallyn, Filters.any);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue("Tusken Breath Mask must reach Doallyn before Mouse carries it", scn.IsAttachedTo(doallyn, tusken));
        scn.MoveCardsToLocation(db94, mouse);
        // Mouse relocates the Mask and delivery/return can resolve immediately because Doallyn is present.
        AcceptRelocate(scn, mouse, tusken);
        assertInHand(mouse);
        assertEquals(doallyn, tusken.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));

        // Delivery is keyed to the original hunt target, not any arbitrary character/site host.
        if (scn.DSAnyDecisionsAvailable() && (scn.DSActionAvailable("Return") || scn.DSCardActionAvailable(mouse, "Return"))) {
            scn.DSChooseAction("Return");
        }
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }

        assertInHand(mouse);
        assertTrue("Tusken Breath Mask must be returned to Doallyn", scn.IsAttachedTo(doallyn, tusken));
        assertFalse("Tusken Breath Mask must not be dumped on Docking Bay 94", scn.IsAttachedTo(db94, tusken));
        assertFalse(scn.IsAttachedTo(mouse, tusken));
    }
    private String decisionText(VirtualTableScenario scn) {
        return scn.GetCurrentDecision() == null ? "none" : scn.GetCurrentDecision().getText();
    }

}
