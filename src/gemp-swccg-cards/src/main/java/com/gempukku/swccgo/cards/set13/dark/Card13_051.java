package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInCardPileFromOffTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: A Useless Gesture
 */
public class Card13_051 extends AbstractDefensiveShield {
    public Card13_051() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.A_Useless_Gesture, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Imperial officers aboard the Death Star considered the Rebellion a minor threat.");
        setGameText("Plays on table. Cancels Don't Underestimate Our Chances. When opponent plays an Interrupt and has 3 smugglers on table, if that Interrupt is placed in Lost Pile, place it out of play. Ketwol may exchange a docking bay only once per game.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Dont_Underestimate_Our_Chances)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Dont_Underestimate_Our_Chances)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Dont_Underestimate_Our_Chances, Title.Dont_Underestimate_Our_Chances);
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.justPlacedPlayedInterruptInLostPile(game, effectResult, Filters.opponents(self))
                && GameConditions.canSpot(game, self, 3, Filters.and(Filters.opponents(self), Filters.smuggler))) {
            PhysicalCard interrupt = ((PutCardInCardPileFromOffTableResult) effectResult).getCard();

            if(Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY).accepts(game, interrupt)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place " + GameUtils.getFullName(interrupt) + " out of play");
                action.setActionMsg("Place " + GameUtils.getCardLink(interrupt) + " out of play");
                // Perform result(s)
                action.appendEffect(
                        new PlaceCardOutOfPlayFromOffTableEffect(action, interrupt));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Ketwol, ModifyGameTextType.KETWOL__MAY_EXCHANGE_DOCKING_BAY_ONCE_PER_GAME));
        return modifiers;
    }
}