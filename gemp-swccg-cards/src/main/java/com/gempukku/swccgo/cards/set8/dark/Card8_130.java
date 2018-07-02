package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Relentless Tracking
 */
public class Card8_130 extends AbstractNormalEffect {
    public Card8_130() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Relentless Tracking", Uniqueness.UNIQUE);
        setLore("Biker scouts commonly work as a team to track enemies of the Empire. A pair of scouts on speeder bike is difficult to elude.");
        setGameText("Deploy on opponent's non-droid character at same or adjacent site as your scout. During each of your control phases, if your scout is at this site (and not Undercover), opponent loses 2 Force (or 3 Force if that scout is also a biker scout).");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.non_droid_character, Filters.at(Filters.sameOrAdjacentSiteAs(self, Filters.and(Filters.your(self), Filters.scout))));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.non_droid_character;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);
        Filter scoutFilter = Filters.and(Filters.your(self), Filters.scout, Filters.atSameSite(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, scoutFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose scout", scoutFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard cardSelected) {
                            action.addAnimationGroup(cardSelected);
                            int numForce = Filters.biker_scout.accepts(game, cardSelected) ? 3 : 2;
                            action.setText("Make opponent lose " + numForce + " Force");
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, numForce));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);
        Filter scoutFilter = Filters.and(Filters.your(self), Filters.scout, Filters.atSameSite(self));

        // Check condition(s)
        // Check if reached end of each control phase and action was not performed yet.
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, scoutFilter)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose Force");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose scout", scoutFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard cardSelected) {
                            action.addAnimationGroup(cardSelected);
                            int numForce = Filters.biker_scout.accepts(game, cardSelected) ? 3 : 2;
                            action.setText("Make opponent lose " + numForce + " Force");
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, numForce));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}