package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Droid
 * Title: R2-D2 (Artoo-Detoo)
 */
public class Card2_014 extends AbstractDroid {
    public Card2_014() {
        super(Side.LIGHT, 2, 3, 1, 4, "R2-D2 (Artoo-Detoo)", Uniqueness.UNIQUE);
        setAlternateDestiny(5);
        setLore("Fiesty. Loyal. Heroic. Insecure. Rebel spy. Excels at trouble. Incorrigible counterpart of a mindless philosopher. Has picked up a slight flutter. A bit eccentric.");
        setGameText("While aboard any starfighter, adds 2 to power, maneuver and hyperspeed (3 on Red 5). If at a Scomp link when opponent draws destiny of: 1-3, you may activate one Force; 4-6, you may draw top card from Reserve Deck.");
        addPersona(Persona.R2D2);
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition aboardStarfighter = new AboardCondition(self, Filters.starfighter);
        Filter starfighterAboard = Filters.and(Filters.starfighter, Filters.hasAboard(self));
        Evaluator onRed5Evaluator = new CardMatchesEvaluator(2, 3, Filters.Red_5);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, starfighterAboard, aboardStarfighter, onRed5Evaluator));
        modifiers.add(new ManeuverModifier(self, starfighterAboard, aboardStarfighter, onRed5Evaluator));
        modifiers.add(new HyperspeedModifier(self, starfighterAboard, aboardStarfighter, onRed5Evaluator));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && GameConditions.isAtScompLink(game, self)) {

            if (GameConditions.isDestinyValueInRange(game, 1, 3)
                    && GameConditions.canActivateForce(game, playerId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Activate a Force");
                // Perform result(s)
                action.appendEffect(
                        new ActivateForceEffect(action, playerId, 1));
                return Collections.singletonList(action);
            }

            if(GameConditions.isDestinyValueInRange(game, 4, 6)
                    && GameConditions.hasReserveDeck(game, playerId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Draw top card of Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
