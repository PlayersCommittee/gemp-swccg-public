package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Interrupt
 * Subtype: Used
 * Title: This Is Where The Fun Begins
 */
public class Card216_044 extends AbstractUsedInterrupt {
    public Card216_044() {
        super(Side.LIGHT, 4, "This Is Where The Fun Begins", Uniqueness.UNIQUE);
        setLore("");
        setGameText("If a battle was just initiated, each of your [Republic] starfighters present is power +1 and immune to attrition for remainder of turn. OR If Anakin and Obi-Wan are in a battle together, cancel one opponent's destiny just drawn (except a battle destiny).");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        final Filter filter = Filters.and(Filters.your(self), Filters.Republic_starship, Filters.starfighter, Filters.piloted, Filters.presentInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add power and immunity to starfighters");
            // Allow response(s)
            action.allowResponses("Make Republic starfighters present power +1 and immune to attrition",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            final Collection<PhysicalCard> starfighters = Filters.filterActive(game, self, filter);
                            if (!starfighters.isEmpty()) {

                                // Perform result(s)
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new PowerModifier(self, Filters.in(starfighters), 1),
                                                "Makes " + GameUtils.getAppendedNames(starfighters) + " power +1"));
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new ImmuneToAttritionModifier(self, Filters.in(starfighters)),
                                                "Makes " + GameUtils.getAppendedNames(starfighters) + " immune to attrition"));
                            }
                        }
                    }
            );
            actions.add(action);
        }

        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && !TriggerConditions.isDestinyDrawType(game, effectResult, DestinyType.BATTLE_DESTINY)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Anakin)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.ObiWan)
                && GameConditions.canCancelDestiny(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel destiny draw");
            action.allowResponses("Cancel destiny draw",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelDestinyEffect(action));
                        }
                    }
            );

            actions.add(action);
        }

        return actions;
    }
}