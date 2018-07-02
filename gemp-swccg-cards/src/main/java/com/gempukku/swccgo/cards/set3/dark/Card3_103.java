package com.gempukku.swccgo.cards.set3.dark;

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
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotParticipateInBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.GoneMissingResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Subtype: Immediate
 * Title: High Anxiety
 */
public class Card3_103 extends AbstractImmediateEffect {
    public Card3_103() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "High Anxiety", Uniqueness.UNIQUE);
        setLore("When a Rebel is missing, ranking members of the Alliance express a professional yet dispassionate concern. Some are not so dispassionate.");
        setGameText("If an opponent's character with ability > 2 has just become missing, deploy on a Rebel with ability > 2 on same planet. Rebel may not participate in battle. Immediate Effect canceled if missing character is found or lost.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter opponentsCharacterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.abilityMoreThan(2));

        // Check condition(s)
        if (TriggerConditions.goneMissing(game, effectResult, opponentsCharacterFilter)) {
            PhysicalCard cardMissing = ((GoneMissingResult) effectResult).getMissingCharacter();

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.and(Filters.Rebel,
                    Filters.abilityMoreThan(2), Filters.onSamePlanet(cardMissing)), null);
            if (action != null) {
                action.setText("Deploy due to " + GameUtils.getFullName(cardMissing) + " missing");
                // Remember the character gone missing
                action.appendTargeting(
                        new SetTargetedCardEffect(action, self, TargetId.IMMEDIATE_EFFECT_TARGET_1, null, cardMissing, Filters.samePermanentCardId(cardMissing)));
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
        modifiers.add(new MayNotParticipateInBattleModifier(self, Filters.hasAttached(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard missingCharacter = self.getTargetedCard(game.getGameState(), TargetId.IMMEDIATE_EFFECT_TARGET_1);

        // Check condition(s)
        if (missingCharacter != null
            && (TriggerConditions.missingCharacterFound(game, effectResult, missingCharacter)
                || TriggerConditions.justLost(game, effectResult, missingCharacter))
                && GameConditions.canBeCanceled(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        if (self.getTargetedCard(game.getGameState(), TargetId.IMMEDIATE_EFFECT_TARGET_1) != null) {
            PhysicalCard missingCharacter = self.getTargetedCard(game.getGameState(), TargetId.IMMEDIATE_EFFECT_TARGET_1);
            return "Missing character is " + GameUtils.getCardLink(missingCharacter);
        }
        return null;
    }
}