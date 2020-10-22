package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.StackCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Droid
 * Title: L3-37 (Elthree-threeseven)
 */
public class Card501_023 extends AbstractDroid {
    public Card501_023() {
        super(Side.LIGHT, 3, 2, 2, 4, "L3-37 (Elthree-threeseven)", Uniqueness.UNIQUE);
        setArmor(4);
        setLore("Female Smuggler.");
        setGameText("While aboard (or stacked on) a freighter, adds 1 to power, maneuver, and hyperspeed, and when forfeiting L3-37, stack her on that freighter. May lose L3-37 to cancel a non-[Immune to Sense] Interrupt targeting a freighter she is aboard or stacked on.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.FEMALE);
        addModelTypes(ModelType.CUSTOM_PILOT_DROID);
        addPersona(Persona.L3_37);
        setTestingText("L3-37 (Elthree-threeseven)");
    }

    @Override
    protected List<Modifier> getGameTextWhileStackedModifiers(SwccgGame game, PhysicalCard self) {
        Filter starshipStackedOn = Filters.and(Filters.freighter, Filters.hasStacked(self));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, starshipStackedOn, 1));
        modifiers.add(new ManeuverModifier(self, starshipStackedOn, 1));
        modifiers.add(new HyperspeedModifier(self, starshipStackedOn, 1));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, starshipStackedOn, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starshipAboard = Filters.and(Filters.freighter, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, starshipAboard, 1));
        modifiers.add(new ManeuverModifier(self, starshipAboard, 1));
        modifiers.add(new HyperspeedModifier(self, starshipAboard, 1));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, starshipAboard, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToBeForfeited(game, effectResult, self)
                && GameConditions.isAboard(game, self, Filters.freighter)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardAboutToLeaveTable();
            final PhysicalCard starship = Filters.findFirstActive(game, self, Filters.hasAboard(self));
            if (starship != null) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack on " + GameUtils.getCardLink(starship));
                action.setActionMsg("Stack on " + GameUtils.getCardLink(starship));
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                                action.appendEffect(
                                        new StackCardFromTableEffect(action, cardToBeLost, starship));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.and(Filters.Interrupt, Filters.not(Filters.immune_to_Sense)),
                Filters.and(Filters.freighter, Filters.or(Filters.hasAboard(self), Filters.hasStacked(self))))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendCost(new LoseCardFromTableEffect(action, self, false));
            return Collections.singletonList(action);
        }
        return null;
    }
}
