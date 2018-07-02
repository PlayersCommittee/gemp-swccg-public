package com.gempukku.swccgo.cards.set206.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 6
 * Type: Effect
 * Title: What Chance Do We Have?
 */
public class Card206_006 extends AbstractNormalEffect {
    public Card206_006() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "What Chance Do We Have?", Uniqueness.UNIQUE);
        setGameText("Deploy on table. Whenever you lose Force (except from a Force drain or your card) during opponent's control phase, unless opponent occupies more battlegrounds than you, may reduce loss (to a minimum of 1) by the number of battlegrounds you occupy. [Immune to Alter]");
        addIcons(Icon.VIRTUAL_SET_6);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForceMoreThan(game, effectResult, playerId, 1)
                && !TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, playerId, Filters.your(self))
                && !TriggerConditions.isAboutToLoseForceFromForceDrainAt(game, effectResult, playerId, Filters.any)
                && GameConditions.isDuringOpponentsPhase(game, self, Phase.CONTROL)
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.isOncePerForceLoss(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            int numToReduce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupies(playerId)));
            if (numToReduce > 0 && numToReduce >= Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupies(opponent)))) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Reduce Force loss by " + numToReduce);
                action.setActionMsg("Reduce Force loss by " + numToReduce + " (to a minimum of 1)");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerForceLossEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new ReduceForceLossEffect(action, playerId, numToReduce, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}