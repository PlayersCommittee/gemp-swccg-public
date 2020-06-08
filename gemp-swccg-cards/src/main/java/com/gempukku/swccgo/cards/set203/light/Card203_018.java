package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 3
 * Type: Interrupt
 * Subtype: Lost
 * Title: The Force Is Strong With This One (V)
 */
public class Card203_018 extends AbstractLostInterrupt {
    public Card203_018() {
        super(Side.LIGHT, 5, Title.The_Force_Is_Strong_With_This_One);
        setVirtualSuffix(true);
        setLore("Luke's piloting skills and Force abilities made his X-wing a difficult target for Darth Vader as they raced down the Death Star trench.");
        setGameText("If your padawan or Skywalker is in battle with a Dark Jedi, either add one battle destiny or cancel I Have You Now. OR If Luke is in battle with opponent's character of ability > 3, add one battle destiny.");
        addIcons(Icon.VIRTUAL_SET_3);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if ((GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.or(Filters.padawan, Filters.Skywalker)))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Dark_Jedi))
                || (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Luke))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(playerId), Filters.character, Filters.abilityMoreThan(3))))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.I_Have_You_Now)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.or(Filters.padawan, Filters.Skywalker)))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Dark_Jedi)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}