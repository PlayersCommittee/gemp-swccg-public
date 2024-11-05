package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBePlacedOutOfPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Defensive Shield
 * Title: Your Insight Serves You Well (V)
 */
public class Card200_032 extends AbstractDefensiveShield {
    public Card200_032() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Your_Insight_Serves_You_Well, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Luke knew that while the dark side was quicker and more seductive, eventually evil would turn on itself.");
        setGameText("Plays on table. Cancels Scanning Crew and 'insert' cards just revealed. Sidious may not place Skywalkers out of play. Let Them Make the First Move may target only Undercover Spies. Unless Inner Strength on table, opponent may only use one combat card per turn.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotTargetToBePlacedOutOfPlayModifier(self, Filters.Skywalker, Filters.Sidious));
        modifiers.add(new SpecialFlagModifier(self, new UnlessCondition(new OnTableCondition(self, Filters.Inner_Strength)), ModifierFlag.MAY_ONLY_USE_ONE_COMBAT_CARD_PER_TURN, opponent));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Let_Them_Make_The_First_Move, ModifyGameTextType.LET_THEM_MAKE_THE_FIRST_MOVE__ONLY_TARGET_UNDERCOVER_SPIES_AND_R2D2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Scanning_Crew)
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
                && GameConditions.canTargetToCancel(game, self, Filters.Scanning_Crew)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Scanning_Crew, Title.Scanning_Crew);
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.justRevealedInsertCard(game, effectResult, Filters.any)
                && GameConditions.canCancelRevealedInsertCard(game, self, effectResult)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelRevealedInsertCardAction(action, effectResult);
            actions.add(action);
        }
        return actions;
    }
}
