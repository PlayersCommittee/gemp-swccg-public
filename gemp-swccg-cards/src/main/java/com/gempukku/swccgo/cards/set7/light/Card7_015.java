package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Droid Merchant
 */
public class Card7_015 extends AbstractAlien {
    public Card7_015() {
        super(Side.LIGHT, 3, 1, 1, 2, 3, Title.Droid_Merchant, Uniqueness.RESTRICTED_3);
        setLore("Careful manipulator of funds and Imperial taxation codes. Buys droids from Jawas and sells them to the Alliance.");
        setGameText("Spaceport Speeders may be played at same site. Once per game, may do one of the following: activate 1 Force when you deploy a droid OR retrieve 1 Force when you deploy an astromech to a starfighter.");
    }
    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DROID_MERCHANT_ACTIVATE_RETRIEVE;
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.justDeployedAboard(game, effectResult, Filters.and(Filters.your(self), Filters.or(Filters.astromech_droid)), Filters.starfighter) && (GameConditions.isOncePerGame(game, self, gameTextActionId))) {

            final OptionalGameTextTriggerAction action1 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action1.setText("Retrieve 1 Force");

            // Update usage limit(s)
            action1.appendUsage(
                    new OncePerGameEffect(action1));

            // Perform result(s)
            action1.appendEffect(
                    new RetrieveForceEffect(action1, playerId, 1));
            actions.add(action1);

            final OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action2.setText("Activate 1 Force");

            // Update usage limit(s)
            action2.appendUsage(
                    new OncePerGameEffect(action2));

            // Perform result(s)
            action2.appendEffect(
                    new ActivateForceEffect(action2, playerId, 1));
            actions.add(action2);

        //    return Collections.singletonList(action);
            return actions;
        }

        // Check condition(s)
        if (TriggerConditions.justDeployedFromHandToLocation(game, effectResult, Filters.and(Filters.your(self), Filters.or(Filters.droid)), Filters.any)
                && GameConditions.canActivateForce(game, playerId) && (GameConditions.isOncePerGame(game, self, gameTextActionId))) {


            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));

            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }

        return null;
    }
}