package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Subtype: Utinni
 * Title: Luke? Luuuuke!
 */
public class Card1_223 extends AbstractUtinniEffect {
    public Card1_223() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Luke? Luuuuke!", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("'All right. I'll be right there, Aunt Beru.'");
        setGameText("Deploy on any Tatooine site. Target any Rebel. Target is power and forfeit -1 (-3 if target is Luke). If Rebel's forfeit reaches zero, Rebel is lost. Utinni Effect canceled when reached by target.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Tatooine_site;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.Rebel;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.Rebel;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter target = Filters.targetedByCardOnTableAsTargetId(self, TargetId.UTINNI_EFFECT_TARGET_1);
        Evaluator evaluator = new CardMatchesEvaluator(-1, -3, Filters.Luke);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, target, evaluator));
        modifiers.add(new ForfeitModifier(self, target, evaluator));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (game.getModifiersQuerying().getForfeit(gameState, target) == 0) {
                GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setSingletonTrigger(true);
                action.setText("Make " + GameUtils.getFullName(target) + " lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(target) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, target));
                actions.add(action);
            }

            // Check condition(s)
            if (GameConditions.isAtLocation(game, self, Filters.sameLocation(target))
                    && GameConditions.canBeCanceled(game, self)) {
                GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setSingletonTrigger(true);
                action.setText("Cancel");
                action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
                // Perform result(s)
                action.appendEffect(
                        new CancelCardOnTableEffect(action, self));
                actions.add(action);
            }
        }
        return actions;
    }
}