package com.gempukku.swccgo.cards.set202.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 2
 * Type: Starship
 * Subtype: Starfighter
 * Title: Azure Angel
 */
public class Card202_007 extends AbstractStarfighter {
    public Card202_007() {
        super(Side.LIGHT, 2, 3, 3, null, 4, 5, 6, Title.Azure_Angel, Uniqueness.UNIQUE);
        setGameText("May add one pilot and one astromech. During battle, if Anakin or R2-D2 aboard, may lose 1 Force to cancel a non-[Immune to Sense] interrupt. While Anakin or R2-D2 aboard, immune to attrition < 5 (6 if both).");
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.CLONE_ARMY, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_2);
        addModelType(ModelType.MODIFIED_DELTA_7_INTERCEPTOR);
        setPilotCapacity(1);
        setAstromechCapacity(1);
        setMatchingPilotFilter(Filters.or(Filters.Anakin, Filters.R2D2));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition anakinAboard = new HasAboardCondition(self, Filters.Anakin);
        Condition r2d2Aboard = new HasAboardCondition(self, Filters.R2D2);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OrCondition(anakinAboard, r2d2Aboard), new ConditionEvaluator(5, 6, new AndCondition(anakinAboard, r2d2Aboard))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.Interrupt, Filters.not(Filters.immune_to_Sense)))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.hasAboard(game, self, Filters.or(Filters.Anakin, Filters.R2D2))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendUsage(new OncePerBattleEffect(action));
            action.appendCost(new LoseForceEffect(action, playerId, 1, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
