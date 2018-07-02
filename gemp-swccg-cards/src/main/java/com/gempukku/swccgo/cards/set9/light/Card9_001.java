package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Admiral's Order
 * Title: Capital Support
 */
public class Card9_001 extends AbstractAdmiralsOrder {
    public Card9_001() {
        super(Side.LIGHT, "Capital Support");
        setGameText("Each pilot deploys -1 (or -2 if an admiral) aboard a capital starship. Each capital starship with a pilot character aboard is immune to attrition < 4 (or adds 2 to immunity if starship already has immunity). During each of your control phases, opponent loses 1 Force for each battleground site your general controls that is related to a system you occupy.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter capitalStarshipWithPilotCharacterAboard = Filters.and(Filters.capital_starship, Filters.hasAboard(self,
                Filters.and(Filters.pilot, Filters.character)));
        Filter alreadyHasImmunity = Filters.alreadyHasImmunityToAttrition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, Filters.pilot, new CardMatchesEvaluator(-1, -2, Filters.admiral), Filters.capital_starship));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(capitalStarshipWithPilotCharacterAboard, Filters.not(alreadyHasImmunity)), 4));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(capitalStarshipWithPilotCharacterAboard, alreadyHasImmunity), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        Filter filter = Filters.and(Filters.battleground_site, Filters.controlsWith(playerId, self, Filters.general),
                Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.occupies(playerId))));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, filter)) {
            int numForce = Filters.countActive(game, self, filter);

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose " + numForce + " Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, numForce));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter filter = Filters.and(Filters.battleground_site, Filters.controlsWith(playerId, self, Filters.general),
                Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.occupies(playerId))));

        // Check condition(s)
        // Check if reached end of each control phase and action was not performed yet.
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, filter)) {
            int numForce = Filters.countActive(game, self, filter);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose " + numForce + " Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, numForce));
            return Collections.singletonList(action);
        }
        return null;
    }
}
