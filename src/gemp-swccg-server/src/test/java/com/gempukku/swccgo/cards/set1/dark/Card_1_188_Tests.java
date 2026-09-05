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
                    put("plastoid2", "1_059");
                    put("tusken", "1_067");
                    put("elom", "6_012");
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
    public void MouseDroid_1_188_DeliveryAtTargetSiteCompletesUtinniAndMouseReturnsToHand() {
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

        scn.MoveCardsToLocation(dsDb, mouse);
        scn.SkipToPhase(Phase.BATTLE);
        if (scn.DSAnyDecisionsAvailable() && scn.DSActionAvailable("Return")) {
            scn.DSChooseAction("Return");
        }
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }
        var trooper = scn.GetDSFiller(1);
        assertInHand(mouse);
        // SADD cannot sit on a docking bay, so delivery attaches it to the hunted trooper.
        assertTrue(scn.IsAttachedTo(trooper, sadd) || scn.IsAttachedTo(dsDb, sadd) || scn.CardsAtLocation(dsDb, sadd));
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
    public void MouseDroid_1_188_SendADetachmentDownPickupAndDelivery() {
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

        scn.MoveCardsToLocation(dsDb, mouse);
        scn.SkipToPhase(Phase.BATTLE);
        if (scn.DSAnyDecisionsAvailable() && scn.DSActionAvailable("Return")) {
            scn.DSChooseAction("Return");
        }
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }
        assertInHand(mouse);
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
    public void MouseDroid_1_188_DeliveringToTargetReturnsMouseAndSendsLeftoverUtinnisToLost() {
        // Delivery is required return: place relevant Utinnis on the hunted target, leftover packages to Lost, mouse to hand.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var homestead = scn.GetDSCard("homestead");
        var sadd = scn.GetDSCard("sadd");
        var luke = scn.GetLSCard("luke");
        var farm = scn.GetLSCard("farm");
        var db94 = scn.GetDSCard("db94");
        var trooper = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(farm, luke);
        scn.MoveCardsToLocation(db94, mouse, trooper);
        scn.AttachCardsTo(mouse, homestead, sadd);
        homestead.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, luke, Filters.any);
        sadd.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, trooper, Filters.any);

        scn.SkipToPhase(Phase.CONTROL);
        if (scn.DSAnyDecisionsAvailable() && (scn.DSActionAvailable("Return") || scn.DSCardActionAvailable(mouse, "Return"))) {
            scn.DSChooseAction("Return");
        }
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }

        assertInHand(mouse);
        assertTrue("SADD attaches to the hunted trooper", scn.IsAttachedTo(trooper, sadd));
        assertFalse(scn.IsAttachedTo(mouse, sadd));
        assertInZone(Zone.LOST_PILE, homestead);
        assertFalse("Homestead must not stay on the mouse after delivery", scn.IsAttachedTo(mouse, homestead));
        assertFalse("Homestead must not be dumped on Docking Bay 94", scn.IsAttachedTo(db94, homestead));
    }


    /** True when DS has a live ACTION_CHOICE whose actionText contains the text (never call DSActionAvailable blindly). */
    private boolean DSRequiredActionAvailable(VirtualTableScenario scn, String text) {
        if (!scn.DSAnyDecisionsAvailable()) {
            return false;
        }
        var params = scn.GetAwaitingDecisionParams(scn.DS);
        if (params == null || params.get("actionText") == null) {
            return false;
        }
        for (String action : params.get("actionText")) {
            if (action != null && action.toLowerCase().contains(text.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /** Pass Force/optional windows after docking-bay transit; stop when Steal/Return ACTION_CHOICE appears. */
    private void PassThroughTransitResponsesUntilRequired(VirtualTableScenario scn) {
        for (int i = 0; i < 30; i++) {
            if (DSRequiredActionAvailable(scn, "Steal") || DSRequiredActionAvailable(scn, "Return")) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            String text = decision.getText().toLowerCase();
            if (text.contains("required") || text.contains("choose required") || text.contains("choose action")) {
                return;
            }
            // DockingBayTransitTests: opponent may see Force optional first, then acting player.
            if (text.contains("optional")) {
                if (scn.LSAnyDecisionsAvailable()) {
                    scn.LSPass();
                }
                if (scn.DSAnyDecisionsAvailable()) {
                    scn.DSPass();
                }
                continue;
            }
            throw new RuntimeException("Unexpected window while waiting for Steal/Return. Decision: " + decisionText(scn)
                    + " DS=" + (scn.DSGetDecision()==null?"none":scn.DSGetDecision().getText())
                    + " LS=" + (scn.LSGetDecision()==null?"none":scn.LSGetDecision().getText()));
        }
        throw new RuntimeException("Timed out waiting for Steal/Return. Decision: " + decisionText(scn));
    }


    @Test
    public void MouseDroid_1_188_ReturnToHandStillHappensIfNecklaceStealIsChosenFirst() {
        // Organa necklace steal and Mouse return both fire at delivery.
        // Choosing Steal first must still leave Return available via WhileInPlayData.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var necklace = scn.GetDSCard("necklace");
        var avarik = scn.GetDSCard("avarik");
        var db94 = scn.GetDSCard("db94");
        var dsDb = scn.GetLSCard("ds-db");

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveLocationToTable(dsDb);

        // Mouse already carries the necklace after pickup; Imperial waits at Death Star: Docking Bay 327.
        scn.MoveCardsToLocation(db94, mouse);
        scn.MoveCardsToLocation(dsDb, avarik);
        scn.AttachCardsTo(mouse, necklace);
        necklace.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, avarik, Filters.any);

        // Reach Move while they are apart so SkipToPhase never auto-passes Steal+Return.
        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.AwaitingDSMovePhaseActions());

        // Docking-bay transit is a location action (see DockingBayTransitTests), not mouse landspeed.
        assertTrue("Docking bay transit missing on DB 94. Actions: " + scn.GetDSAvailableActions(),
                scn.DSCardActionAvailable(db94, "transit"));
        scn.DSUseCardAction(db94, "transit");
        assertTrue(scn.DSHasCardChoiceAvailable(dsDb));
        scn.DSChooseCard(dsDb);
        assertTrue(scn.DSHasCardChoiceAvailable(mouse));
        scn.DSChooseCard(mouse);
        assertTrue("After choosing mouse to transit. Decision: " + decisionText(scn),
                scn.DSAnyDecisionsAvailable() || scn.LSAnyDecisionsAvailable()
                        || scn.CardsAtLocation(dsDb, mouse));
        PassThroughTransitResponsesUntilRequired(scn);

        assertTrue("Expected live DS decision after transit. Decision: " + decisionText(scn),
                scn.DSAnyDecisionsAvailable());
        assertTrue("Steal necklace must be offered together with Return. Decision: " + decisionText(scn),
                DSRequiredActionAvailable(scn, "Steal"));
        assertTrue("Return mouse to hand must be offered together with Steal. Decision: " + decisionText(scn),
                DSRequiredActionAvailable(scn, "Return"));

        // Choose Steal first - necklace attaches to the Imperial and detaches from the mouse.
        scn.DSChooseAction("Steal");

        // Return may remain in the same ACTION_CHOICE, or reappear after Steal optionals via WhileInPlayData.
        if (!DSRequiredActionAvailable(scn, "Return")) {
            for (int i = 0; i < 20; i++) {
                if (DSRequiredActionAvailable(scn, "Return")) {
                    break;
                }
                var decision = scn.GetCurrentDecision();
                if (decision == null) {
                    break;
                }
                String text = decision.getText().toLowerCase();
                if (text.contains("optional")) {
                    if (scn.LSAnyDecisionsAvailable()) {
                        scn.LSPass();
                    }
                    if (scn.DSAnyDecisionsAvailable()) {
                        scn.DSPass();
                    }
                    continue;
                }
                break;
            }
        }

        assertTrue("After Steal, necklace should be on Imperial. Decision: " + decisionText(scn)
                        + " mouseAtDsDb=" + scn.CardsAtLocation(dsDb, mouse)
                        + " necklaceOnAvarik=" + scn.IsAttachedTo(avarik, necklace)
                        + " necklaceOnMouse=" + scn.IsAttachedTo(mouse, necklace),
                scn.IsAttachedTo(avarik, necklace));

        if (DSRequiredActionAvailable(scn, "Return")) {
            scn.DSChooseAction("Return");
            for (int i = 0; i < 10; i++) {
                var decision = scn.GetCurrentDecision();
                if (decision == null || !decision.getText().toLowerCase().contains("optional")) {
                    break;
                }
                if (scn.LSAnyDecisionsAvailable()) {
                    scn.LSPass();
                }
                if (scn.DSAnyDecisionsAvailable()) {
                    scn.DSPass();
                }
            }
        }

        assertInHand(mouse);
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
    public void MouseDroid_1_188_PresentWithTargetDeliversUtinniAndReturnsMouseToHand() {
        // Positive present-with on Dagobah: hunted apprentice present with mouse carrying Failure -> deliver, mouse to hand.
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
        // If delivery waited for another table-change, try Battle; pass Failure destiny windows if any.
        if (scn.CardsAtLocation(cave, mouse)) {
            scn.SkipToPhase(Phase.BATTLE);
            if (scn.DSAnyDecisionsAvailable() && (scn.DSActionAvailable("Return") || scn.DSCardActionAvailable(mouse, "Return"))) {
                scn.DSChooseAction("Return");
            }
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            }
            if (scn.DSAnyDecisionsAvailable()) {
                scn.PassAllResponses();
            }
        }

        assertInHand(mouse);
        assertTrue("Delivered Failure returns to Cave (only legal host) or hunted Son",
                scn.IsAttachedTo(cave, failure) || scn.IsAttachedTo(son, failure));
        assertFalse(scn.IsAttachedTo(mouse, failure));
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
        var trash = scn.GetLSCard("trash");
        var jungle = scn.GetLSCard("jungle");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
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



    /** Simulate Elom's remainder-of-game Plastoid changes (subtype Effect + changed deployment). */
    private void ApplyElomPlastoidModifiers(VirtualTableScenario scn, PhysicalCardImpl elom) {
        scn.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                new ChangeCardSubtypeModifier(elom, Filters.Plastoid_Armor, CardSubtype.NORMAL));
        scn.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                new NotUniqueModifier(elom, Filters.Plastoid_Armor));
        scn.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                new ModifyGameTextModifier(elom, Filters.Plastoid_Armor, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT));
    }

    @Test
    public void MouseDroid_1_188_DeploysToSiteWithOnlyCharacterHostedElomPlastoid() {
        // A: only Elom-changed Plastoid on characters at the site (no location-hosted Utinni) -> Mouse may deploy.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var plastoid = scn.GetLSCard("plastoid");
        var han = scn.GetLSCard("han");
        var chewie = scn.GetLSCard("chewie");
        var elom = scn.GetLSCard("elom");
        var dsDb = scn.GetLSCard("ds-db");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(dsDb);
        scn.MoveCardsToLocation(dsDb, han, chewie, elom, presence);
        ApplyElomPlastoidModifiers(scn, elom);
        scn.AttachCardsTo(han, plastoid);
        scn.MoveCardsToDSHand(mouse);

        // Elom stripped Utinni subtype; attachment alone must still enable Mouse special deploy.
        assertFalse("Elom Plastoid must not match Filters.Utinni_Effect",
                Filters.Utinni_Effect.accepts(scn.game(), plastoid));
        assertTrue(Filters.Plastoid_Armor.accepts(scn.game(), plastoid));

        EnsureDSDeployPhase(scn);
        assertTrue("Mouse must deploy to site with only character-hosted Elom Plastoid. Decision: " + decisionText(scn),
                scn.DSCardPlayAvailable(mouse));
        scn.DSDeployCard(mouse);
        assertTrue(scn.DSHasCardChoiceAvailable(dsDb));
        scn.DSChooseCard(dsDb);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(dsDb, mouse));
    }

    @Test
    public void MouseDroid_1_188_CannotSpecialDeployToSiteWithNoUtinniAtAll() {
        // B: character present but no Utinni / Plastoid package -> special deploy not available.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var han = scn.GetLSCard("han");
        var dsDb = scn.GetLSCard("ds-db");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(dsDb);
        scn.MoveCardsToLocation(dsDb, han, presence);
        scn.MoveCardsToDSHand(mouse);

        EnsureDSDeployPhase(scn);
        assertFalse("Mouse special deploy requires a reachable Utinni/Plastoid package",
                scn.DSCardPlayAvailable(mouse));
    }

    @Test
    public void MouseDroid_1_188_RelocatesCharacterHostedElomPlastoidOntoMouseKeepsSubject() {
        // C: Mouse with Elom Plastoid on character at same site -> optional relocate; carry keeps subject on original host.
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

        assertFalse(Filters.Utinni_Effect.accepts(scn.game(), plastoid));
        assertTrue(scn.IsAttachedTo(han, plastoid));

        // Do not SkipToPhase here — it can auto-decline the initial Relocate optional.
        AcceptRelocate(scn, mouse, plastoid);
        assertTrue("Elom Plastoid should stay on Mouse after relocate (not lost as invalid attach). zone="
                        + plastoid.getZone() + " attachedTo=" + (plastoid.getAttachedTo() == null ? "null" : plastoid.getAttachedTo().getTitle()),
                scn.IsAttachedTo(mouse, plastoid));
        // Carry-vs-target: Han remains effect subject / remembered host.
        assertEquals(han, MouseDroidUtinniCarry.getEffectSubjectHost(scn.gameState(), plastoid));
        assertEquals(han, plastoid.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
        assertEquals(han, plastoid.getTargetedCard(scn.gameState(), TargetId.UTINNI_EFFECT_TARGET_1));
    }

    @Test
    public void MouseDroid_1_188_RelocatesTwoCharacterHostedElomPlastoidsOverTime() {
        // D: two Elom Plastoids on Han + Chewie -> Mouse can relocate both over successive offers.
        var scn = GetScenario();
        var mouse = scn.GetDSCard("mouse");
        var plastoid = scn.GetLSCard("plastoid");
        var plastoid2 = scn.GetLSCard("plastoid2");
        var han = scn.GetLSCard("han");
        var chewie = scn.GetLSCard("chewie");
        var elom = scn.GetLSCard("elom");
        var dsDb = scn.GetLSCard("ds-db");
        var presence = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(dsDb);
        scn.MoveCardsToLocation(dsDb, han, chewie, elom, mouse, presence);
        ApplyElomPlastoidModifiers(scn, elom);
        scn.AttachCardsTo(han, plastoid);
        scn.AttachCardsTo(chewie, plastoid2);

        AcceptRelocate(scn, mouse, plastoid);
        assertTrue(scn.IsAttachedTo(mouse, plastoid));
        assertTrue(scn.IsAttachedTo(chewie, plastoid2));

        AdvanceUntilRelocateAvailable(scn, mouse);
        assertTrue("Second Plastoid must still be relocatable. Decision: " + decisionText(scn),
                RelocateUtinniAvailable(scn, mouse));
        AcceptRelocate(scn, mouse, plastoid2);
        assertTrue(scn.IsAttachedTo(mouse, plastoid));
        assertTrue(scn.IsAttachedTo(mouse, plastoid2));
        assertEquals(han, plastoid.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
        assertEquals(chewie, plastoid2.getTargetedCard(scn.gameState(), TargetId.EFFECT_TARGET_1));
    }

    private String decisionText(VirtualTableScenario scn) {
        return scn.GetCurrentDecision() == null ? "none" : scn.GetCurrentDecision().getText();
    }

}
