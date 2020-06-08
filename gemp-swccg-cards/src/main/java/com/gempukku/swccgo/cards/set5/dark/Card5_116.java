package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetTargetedCardEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ExcludedFromBeingTheHighestAbilityCharacterModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;
import com.gempukku.swccgo.logic.timing.results.FrozenResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Subtype: Immediate
 * Title: Despair
 */
public class Card5_116 extends AbstractImmediateEffect {
    public Card5_116() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, Title.Despair, Uniqueness.UNIQUE);
        setLore("The carbonite froze more than just Han's body.");
        setGameText("If a Rebel was just captured or 'frozen,' deploy on another Rebel. That Rebel may not apply ability toward drawing battle destiny (if Leia, she is also excluded from being the 'highest ability character'). If captive released, lose Immediate Effect.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        PhysicalCard rebelCapturedOrFrozen = null;
        if (TriggerConditions.captured(game, effectResult, Filters.Rebel) || TriggerConditions.frozenAndCaptured(game, effectResult, Filters.Rebel)) {
            rebelCapturedOrFrozen = ((CaptureCharacterResult) effectResult).getCapturedCard();
        }
        else if (TriggerConditions.frozen(game, effectResult, Filters.Rebel)) {
            rebelCapturedOrFrozen = ((FrozenResult) effectResult).getCaptive();
        }

        if (rebelCapturedOrFrozen != null && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(rebelCapturedOrFrozen, Filters.captive))) {

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.and(Filters.other(rebelCapturedOrFrozen), Filters.Rebel), null);
            if (action != null) {
                // Remember the captive
                action.appendTargeting(
                        new SetTargetedCardEffect(action, self, TargetId.IMMEDIATE_EFFECT_TARGET_1, null, rebelCapturedOrFrozen, Filters.samePermanentCardId(rebelCapturedOrFrozen)));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Rebel;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotApplyAbilityForBattleDestinyModifier(self, Filters.hasAttached(self)));
        modifiers.add(new ExcludedFromBeingTheHighestAbilityCharacterModifier(self, Filters.and(Filters.hasAttached(self), Filters.Leia)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        PhysicalCard captive = self.getTargetedCard(game.getGameState(), TargetId.IMMEDIATE_EFFECT_TARGET_1);
        if (captive != null && (TriggerConditions.released(game, effectResult, captive)
                || TriggerConditions.leavesTable(game, effectResult, captive))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}