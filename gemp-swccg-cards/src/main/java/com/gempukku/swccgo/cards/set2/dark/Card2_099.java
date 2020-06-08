package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Droid
 * Title: R2-Q2 (Artoo-Kyootoo)
 */
public class Card2_099 extends AbstractDroid {
    public Card2_099() {
        super(Side.DARK, 3, 2, 1, 3, "R2-Q2 (Artoo-Kyootoo)");
        setLore("R2 units are known for expertise in computer uplinking. R2-Q2 spent several decades serving with an Imperial reconnaissance fleet in the Expansion Region.");
        setGameText("While aboard any starfighter, adds 1 to power, maneuver and hyperspeed. When at a Scomp link during your draw phase, may use 1 Force to peek at top three cards of your Reserve Deck.");
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition aboardStarfighter = new AboardCondition(self, Filters.starfighter);
        Filter starfighterAboard = Filters.and(Filters.starfighter, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, starfighterAboard, aboardStarfighter, 1));
        modifiers.add(new ManeuverModifier(self, starfighterAboard, aboardStarfighter, 1));
        modifiers.add(new HyperspeedModifier(self, starfighterAboard, aboardStarfighter, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DRAW)
                && GameConditions.isAtScompLink(game, self)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top 3 cards of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckEffect(action, playerId, 3));
            return Collections.singletonList(action);
        }
        return null;
    }
}
