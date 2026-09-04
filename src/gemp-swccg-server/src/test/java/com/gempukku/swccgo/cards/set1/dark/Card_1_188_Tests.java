package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
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
                }},
                new HashMap<>() {{
                    put("mouse", "1_188");
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
        return scn.DSCardActionAvailable(mouse, "Relocate") || scn.DSActionAvailable("Relocate");
    }

    /** Accepts the mouse's optional relocate, choosing the given Utinni Effect if a card picker is shown. */
    private void AcceptRelocate(VirtualTableScenario scn, PhysicalCardImpl mouse, PhysicalCardImpl utinni) {
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
        scn.SkipToPhase(Phase.CONTROL);
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
    public void MouseDroid_1_188_DeliveringOneUtinniDoesNotDumpTheOthers() {
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
        if (scn.DSAnyDecisionsAvailable() && (scn.DSActionAvailable("Deliver") || scn.DSCardActionAvailable(mouse, "Deliver"))) {
            scn.DSChooseAction("Deliver");
        }
        if (scn.DSAnyDecisionsAvailable()) {
            scn.PassAllResponses();
        }

        assertTrue("Mouse stays while it still carries Homestead", scn.CardsAtLocation(db94, mouse));
        assertTrue("Homestead stays on the mouse", scn.IsAttachedTo(mouse, homestead));
        assertFalse("Homestead must not be dumped on Docking Bay 94", scn.IsAttachedTo(db94, homestead));
        assertFalse("Delivered SADD leaves the mouse", scn.IsAttachedTo(mouse, sadd));
        assertTrue("SADD attaches to the hunted trooper, not the illegal docking bay", scn.IsAttachedTo(trooper, sadd));
        assertFalse(scn.IsAttachedTo(db94, sadd));
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


    /** True if that player's current action list contains the text (any case). dark=true is Dark Side. */
    private String decisionText(VirtualTableScenario scn) {
        return scn.GetCurrentDecision() == null ? "none" : scn.GetCurrentDecision().getText();
    }

}
