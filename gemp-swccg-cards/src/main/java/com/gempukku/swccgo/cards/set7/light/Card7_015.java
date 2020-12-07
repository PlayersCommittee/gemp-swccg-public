package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

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
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Spaceport_Speeders, ModifyGameTextType.SPACEPORT_SPEEDERS_CAN_BE_PLAYED_AT_DROID_MERCHANTS_LOCATION));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DROID_MERCHANT_ACTIVATE_RETRIEVE;
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            if (TriggerConditions.justDeployedAboard(game, effectResult, Filters.and(Filters.your(self), Filters.or(Filters.astromech_droid)), Filters.starfighter)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Retrieve 1 Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, 1));
                actions.add(action);
            }
            // Check condition(s)
            if (TriggerConditions.justDeployed(game, effectResult, Filters.and(Filters.your(self), Filters.droid))
                    && GameConditions.canActivateForce(game, playerId)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Activate 1 Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new ActivateForceEffect(action, playerId, 1));
                actions.add(action);
            }
        }
        return actions;
    }
}