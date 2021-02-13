package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.DeionizeStarshipEffect;
import com.gempukku.swccgo.logic.effects.RestoreArmorManeuverHyperspeedToNormalEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Droid
 * Title: R5-D4 (Arfive-Defour)
 */
public class Card2_015 extends AbstractDroid {
    public Card2_015() {
        super(Side.LIGHT, 4, 2, 1, 3, "R5-D4 (Arfive-Defour)");
        setLore("Cheap astromech droid commonly referred to as 'Red'. Purposely blew his motivator to prevent splitting up R2-D2 and C-3PO on Tatooine. Poor navigator but skilled mechanic.");
        setGameText("While aboard any starship, adds 1 to power and maneuver. During your control phase, if aboard your starship damaged by an Ion cannon, restores armor/maneuver and hyperspeed.");
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition aboardStarship = new AboardCondition(self, Filters.starship);
        Filter starshipAboard = Filters.and(Filters.starship, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, starshipAboard, aboardStarship, 1));
        modifiers.add(new ManeuverModifier(self, starshipAboard, aboardStarship, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.isAboard(game, self, Filters.and(Filters.or(Filters.starship_defense_ionized,Filters.starship_hyperspeed_ionized), Filters.your(self)))){

            PhysicalCard starship = Filters.findFirstActive(game, self, Filters.and(Filters.or(Filters.starship_defense_ionized,Filters.starship_hyperspeed_ionized), Filters.your(self), Filters.hasAboard(self)));
            if (starship != null) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Restore armor/maneuver and hyperspeed");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                action.appendEffect(
                        new DeionizeStarshipEffect(action, starship, false, true, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
