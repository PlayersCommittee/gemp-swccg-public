package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.LostInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Effect
 * Title: Close The Blast Doors!
 */
public class Card217_014 extends AbstractNormalEffect {
    public Card217_014() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Close The Blast Doors!", Uniqueness.UNIQUE);
        setLore("Imperial stormtroopers adopt strict security measures. Excellent communications and sheer numbers can hinder Rebel movement across entire territories.");
        setGameText("Deploy on table. Rebel Barrier is a Lost Interrupt. If opponent just canceled a battle (or just moved a character, starship, or vehicle away from a battle), opponent loses 1 Force. If Landing Claw just lost, place it out of play.");
        addIcons(Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new LostInterruptModifier(self, Filters.Rebel_Barrier));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        Filter filter = Filters.and(Filters.canBeTargetedBy(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle));

        if (TriggerConditions.battleCanceledAt(game, effectResult, game.getOpponent(self.getOwner()), Filters.any)
                || (GameConditions.isDuringBattle(game)
                && TriggerConditions.moved(game, effectResult, game.getOpponent(self.getOwner()), filter)
                && TriggerConditions.movedFromLocation(game, effectResult, filter, game.getGameState().getBattleLocation()))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 1 Force");
            action.appendEffect(new LoseForceEffect(action, game.getOpponent(self.getOwner()), 1));

            actions.add(action);
        }

        if (TriggerConditions.justLost(game, effectResult, Filters.Landing_Claw)) {
            final PhysicalCard landingClaw = ((LostFromTableResult) effectResult).getCard();

            if (landingClaw != null) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place " + GameUtils.getCardLink(landingClaw) + " out of play");

                // Perform result(s)
                action.appendEffect(
                        new PlaceCardOutOfPlayFromOffTableEffect(action, landingClaw));

                actions.add(action);
            }
        }

        return actions;
    }
}