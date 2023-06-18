package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.FlipSingleSidedStackedCard;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Character
 * Subtype: Republic
 * Title: Clone Pilot
 */
public class Card221_055 extends AbstractRepublic {
    public Card221_055() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Clone Pilot", Uniqueness.UNRESTRICTED, ExpansionSet.SET_21, Rarity.V);
        setArmor(3);
        setLore("Clone trooper.");
        setGameText("Adds 2 to power and 1 to defense value of anything he pilots. While piloting a [Clone Army] starship or [Clone Army] vehicle, forfeit +1 and draws one battle destiny if unable to otherwise. If stacked face up on Cloning Cylinders, may turn him face down to cancel Lateral Damage.");
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.CLONE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DefenseValueModifier(self, Filters.hasPiloting(self), 1));
        modifiers.add(new ForfeitModifier(self, new PilotingCondition(self, Filters.and(Icon.CLONE_ARMY, Filters.or(Filters.starship, Filters.vehicle))), 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.and(Icon.CLONE_ARMY, Filters.or(Filters.starship, Filters.vehicle))), 1));
        return modifiers;
    }

    @Override
    public List<TopLevelGameTextAction> getGameTextTopLevelWhileStackedActions(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Cloning_Cylinders, Filters.hasStacked(self)))
                && GameConditions.canTarget(game, self, TargetingReason.TO_BE_CANCELED, Filters.Lateral_Damage)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Lateral Damage");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target Lateral Damage", TargetingReason.TO_BE_CANCELED, Filters.Lateral_Damage) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {

                    action.appendCost(new FlipSingleSidedStackedCard(action, self));
                    action.allowResponses(new RespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                            action.appendEffect(new CancelCardOnTableEffect(action, finalTarget));
                        }
                    });
                }
            });

            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggersWhenStacked(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Cloning_Cylinders, Filters.hasStacked(self)))
                && TriggerConditions.isPlayingCard(game, effect, Filters.Lateral_Damage)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendCost(new FlipSingleSidedStackedCard(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}