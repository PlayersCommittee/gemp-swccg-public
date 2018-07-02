package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.FerocityModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.EatenResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Subtype: Immediate
 * Title: Frozen Dinner
 */
public class Card3_102 extends AbstractImmediateEffect {
    public Card3_102() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "Frozen Dinner");
        setLore("Freeze dried and ready to serve, a tasty, wholesome Rebel makes a nutritious meal for the whole wampa clan. They're Gr-r-reat!");
        setGameText("Deploy on opponent's character alone at Wampa Cave. Character is power = 0 and may not move. May be canceled if opponent has a lightsaber or total ability > 4 present. If character eaten by a creature, cumulatively adds 2 to ferocity.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.character, Filters.alone, Filters.at(Filters.Wampa_Cave));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter attachedTo = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetPowerModifier(self, attachedTo, 0));
        modifiers.add(new MayNotMoveModifier(self, attachedTo));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)) {
            PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
            if (location != null) {
                if (GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.lightsaber, Filters.present(self)))
                        || (game.getModifiersQuerying().getTotalAbilityPresentAtLocation(game.getGameState(), opponent, location) > 4)) {

                    RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setSingletonTrigger(true);
                    action.setText("Cancel");
                    action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
                    // Perform result(s)
                    action.appendEffect(
                            new CancelCardOnTableEffect(action, self));
                    return Collections.singletonList(action);
                }
            }
        }

        // Check condition(s)
        if (TriggerConditions.justEatenBy(game, effectResult, Filters.hasAttached(self), Filters.creature)) {
            PhysicalCard creature = ((EatenResult) effectResult).getEatenByCard();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 2 to ferocity");
            action.setActionMsg("Add 2 to " + GameUtils.getCardLink(creature) + "'s ferocity");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action,
                            new FerocityModifier(self, creature, 2, true),
                            "Adds 2 to " + GameUtils.getCardLink(creature) + "'s ferocity"));
            return Collections.singletonList(action);
        }
        return null;
    }
}