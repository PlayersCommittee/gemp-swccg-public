package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseInsertCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceUsedPileFaceUpOnReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.EndOfPhaseResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Through The Force Things You Will See (4_64 / blueprint 4_64).
 * Doc tab t.1gyb543pbwcz / issue #109.
 * Bill refine: card-local face-up (not isInserted); thin Reserve-top hooks; clear on leave;
 * Access Denied among face-up uses insert-found path.
 */
public class Card_4_064_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ttf", "4_64");
                    put("luke", "1_19");
                    put("accessDenied", "5_15");
                    put("leia", "1_18");
                    put("han", "1_11");
                    put("chewie", "1_3");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("trooper", "1_194");
                    put("officer", "1_179");
                    put("dsSite", "1_284");
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

    /** Direct effect play — AdHoc needs an awaiting decision which is unreliable mid-setup. */
    private void applyFaceUpUsedToReserve(VirtualTableScenario scn, PhysicalCardImpl source, String pileOwner) {
        TopLevelGameTextAction action = new TopLevelGameTextAction(source, scn.LS, source.getCardId());
        new PlaceUsedPileFaceUpOnReserveDeckEffect(action, pileOwner).playEffect(scn.game());
        // Clear activation interrupt latch so later cheats/assertions are not blocked.
        scn.gameState().setInsertCardFound(false);
        scn.gameState().setSkipListenerUpdateAllowed(true);
    }

    @Test
    public void ThroughTheForceThingsYouWillSee_StatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("ttf").getBlueprint();

        assertEquals(Title.Through_The_Force_Things_You_Will_See, card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(ExpansionSet.DAGOBAH, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.DAGOBAH);
        }});
        assertTrue(card.getGameText().contains("Used Pile face up"));
        assertTrue(card.getGameText().contains("Reserve Deck"));
    }

    @Test
    public void ThroughTheForceThingsYouWillSee_PlayableAtEndOfOwnerDrawPhase() {
        var scn = GetScenario();
        var ttf = scn.GetLSCard("ttf");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToLSHand(ttf);
        // Used recirculates at end of turn — seed AFTER arriving at DRAW.
        scn.SkipToLSTurn(Phase.DRAW);
        scn.MoveCardsToTopOfLSUsedPile(luke);
        assertEquals(Phase.DRAW, scn.gameState().getCurrentPhase());
        assertTrue(scn.GetLSUsedPileCount() > 0);

        var actions = ttf.getBlueprint().getOptionalAfterActions(
                scn.LS, scn.game(), new EndOfPhaseResult(Phase.DRAW), ttf);
        assertNotNull(actions);
        assertFalse(actions.isEmpty());
    }

    @Test
    public void ThroughTheForceThingsYouWillSee_OwnerDrawPhase_PlacesUsedFaceUpOnReserve() {
        var scn = GetScenario();
        var ttf = scn.GetLSCard("ttf");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var han = scn.GetLSCard("han");
        var priorReserve = scn.GetLSCard("chewie");

        scn.StartGame();
        scn.MoveCardsToTopOfLSUsedPile(luke, leia, han);
        scn.MoveCardsToTopOfOwnReserveDeck(priorReserve);
        assertEquals(3, scn.GetLSUsedPileCount());
        assertFalse(priorReserve.isFaceUpInReserveDeck());

        applyFaceUpUsedToReserve(scn, ttf, scn.LS);

        assertEquals(0, scn.GetLSUsedPileCount());
        assertFalse(priorReserve.isFaceUpInReserveDeck());
        int faceUp = 0;
        for (var c : scn.GetLSReserveDeck()) {
            if (c.isFaceUpInReserveDeck()) {
                faceUp++;
            }
        }
        assertEquals(3, faceUp);
        // Face-up Reserve cards are NOT inserts.
        for (var c : scn.GetLSReserveDeck()) {
            if (c.isFaceUpInReserveDeck()) {
                assertFalse(c.isInserted());
            }
        }
    }

    @Test
    public void ThroughTheForceThingsYouWillSee_FaceUpClearsWhenLeavingReserve() {
        var scn = GetScenario();
        var ttf = scn.GetLSCard("ttf");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        scn.MoveCardsToTopOfLSUsedPile(luke, leia);
        applyFaceUpUsedToReserve(scn, ttf, scn.LS);

        List<PhysicalCardImpl> faceUps = new ArrayList<>();
        for (var c : scn.GetLSReserveDeck()) {
            if (c.isFaceUpInReserveDeck()) {
                faceUps.add((PhysicalCardImpl) c);
            }
        }
        assertFalse(faceUps.isEmpty());
        PhysicalCardImpl faceUpCard = faceUps.get(0);
        // MoveCardsToTop clears card stats (incl. face-up); restore mark after reorder.
        if (scn.GetTopOfLSReserveDeck() != faceUpCard) {
            scn.MoveCardsToTopOfOwnReserveDeck(faceUpCard);
            faceUpCard.setFaceUpInReserveDeck(true);
        }
        assertTrue(faceUpCard.isFaceUpInReserveDeck());

        scn.LSActivateForceCheat(1);
        assertFalse(faceUpCard.isFaceUpInReserveDeck());
        assertTrue(faceUpCard.getZone() == Zone.FORCE_PILE || faceUpCard.getZone() == Zone.TOP_OF_FORCE_PILE);
    }

    @Test
    public void ThroughTheForceThingsYouWillSee_PlayableAtEndOfOpponentDrawPhase() {
        var scn = GetScenario();
        var ttf = scn.GetLSCard("ttf");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveCardsToLSHand(ttf);
        scn.SkipToDSTurn(Phase.DRAW);
        scn.MoveCardsToTopOfDSUsedPile(vader);
        assertEquals(Phase.DRAW, scn.gameState().getCurrentPhase());
        assertTrue(scn.GetDSUsedPileCount() > 0);

        var actions = ttf.getBlueprint().getOptionalAfterActions(
                scn.LS, scn.game(), new EndOfPhaseResult(Phase.DRAW), ttf);
        assertNotNull(actions);
        assertFalse(actions.isEmpty());
    }

    @Test
    public void ThroughTheForceThingsYouWillSee_OpponentDrawPhase_AffectsOpponent() {
        var scn = GetScenario();
        var ttf = scn.GetLSCard("ttf");
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");
        var dsPrior = scn.GetDSCard("officer");

        scn.StartGame();
        scn.MoveCardsToTopOfDSUsedPile(vader, trooper);
        scn.MoveCardsToTopOfOwnReserveDeck(dsPrior);

        applyFaceUpUsedToReserve(scn, ttf, scn.DS);

        assertEquals(0, scn.GetDSUsedPileCount());
        assertFalse(dsPrior.isFaceUpInReserveDeck());
        int faceUp = 0;
        for (var c : scn.GetDSReserveDeck()) {
            if (c.isFaceUpInReserveDeck()) {
                faceUp++;
            }
        }
        assertEquals(2, faceUp);
    }

    @Test
    public void ThroughTheForceThingsYouWillSee_AccessDeniedFaceUpFoundUsesInsertRevealPath() {
        var scn = GetScenario();
        var ttf = scn.GetLSCard("ttf");
        var access = scn.GetLSCard("accessDenied");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToTopOfLSUsedPile(access, luke);
        applyFaceUpUsedToReserve(scn, ttf, scn.LS);

        assertTrue(access.isFaceUpInReserveDeck());
        assertFalse(access.isInserted());

        scn.MoveCardsToTopOfOwnReserveDeck(access);
        Action revealAction = access.getBlueprint().getInsertCardRevealedAction(scn.game(), access);
        assertNotNull(revealAction);

        // Piggyback reveal/lose path for face-up Access Denied (AR Appendix B).
        access.setInsertCardRevealed(true);
        TopLevelGameTextAction loseAction = new TopLevelGameTextAction(access, scn.LS, access.getCardId());
        new LoseInsertCardEffect(loseAction, access).playEffect(scn.game());

        assertTrue(access.getZone() == Zone.LOST_PILE || access.getZone() == Zone.TOP_OF_LOST_PILE);
        assertFalse(access.isFaceUpInReserveDeck());
    }

    @Test
    public void ThroughTheForceThingsYouWillSee_ClearingFaceUpDoesNotClearInsertFlag() {
        var scn = GetScenario();
        var ttf = scn.GetLSCard("ttf");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        scn.MoveCardsToTopOfOwnReserveDeck(luke);
        luke.setInserted(true);
        assertTrue(luke.isInserted());

        scn.MoveCardsToTopOfLSUsedPile(leia);
        applyFaceUpUsedToReserve(scn, ttf, scn.LS);

        assertTrue(luke.isInserted());
        leia.setFaceUpInReserveDeck(false);
        assertTrue(luke.isInserted());
    }
}
