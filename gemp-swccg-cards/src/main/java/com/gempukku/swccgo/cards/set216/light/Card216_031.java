package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.IncreaseAbilityRequiredForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Device
 * Title: Jedi Holocron
 */
public class Card216_031 extends AbstractCharacterDevice {
    public Card216_031() {
        super(Side.LIGHT, 2, "Jedi Holocron", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on your character of ability > 4. While present: adds 1 to training destiny draws and Force drains here; the first Force lost to a Force drain here is stacked here face down; opponent's ability required to draw battle destiny here is +1 for each card stacked here.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.abilityMoreThan(4));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.abilityMoreThan(4);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new EachTrainingDestinyModifier(self, Filters.jediTestTargetingApprentice(Filters.at(Filters.wherePresent(self))), new PresentCondition(self), 1));
        modifiers.add(new ForceDrainModifier(self, Filters.wherePresent(self, hasAttached), 1, self.getOwner()));
        modifiers.add(new IncreaseAbilityRequiredForBattleDestinyModifier(self, Filters.here(self), new StackedEvaluator(self), game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLostForceFromForceDrainAt(game, effectResult, game.getOpponent(self.getOwner()), Filters.here(self), true)
                && GameConditions.isAttachedTo(game, self, Filters.any)
                && GameConditions.isPresent(game, Filters.findFirstActive(game, self, Filters.hasAttached(self)))) {
            LostForceResult lostForceResult = (LostForceResult) effectResult;
            PhysicalCard cardToStack = lostForceResult.getCardLost();
            if (cardToStack != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.appendEffect(
                        new StackOneCardFromLostPileEffect(action, cardToStack, self, true, true, true)
                );
                return Collections.singletonList(action);
            }
        }

        return null;
    }
}
