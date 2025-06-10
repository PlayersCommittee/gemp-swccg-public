package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

/**
 * An effect to resets the armor, maneuver, and hyperspeed values of a card.
 */
public class ResetArmorManeuverAndHyperspeedEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToReset;
    private float _resetValue;

    /**
     * Creates an effect that resets the armor, maneuver, and hyperspeed values of a card.
     * @param action the action performing this effect
     * @param cardToReset the card whose armor, maneuver, and hyperspeed values are reset
     * @param resetValue the reset value
     */
    public ResetArmorManeuverAndHyperspeedEffect(Action action, PhysicalCard cardToReset, float resetValue) {
        super(action);
        _cardToReset = cardToReset;
        _resetValue = resetValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getCardLink(_cardToReset) + "'s armor, maneuver, and hyperspeed are reset to " + GuiUtils.formatAsString(_resetValue));
        gameState.cardAffectsCard(_action.getPerformingPlayer(), source, _cardToReset);

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_cardToReset), Filters.in_play);

        Modifier armorModifier = new ResetArmorModifier(source, cardFilter, _resetValue);
        armorModifier.skipSettingNotRemovedOnRestoreToNormal();
        Modifier hyperspeedModifier = new ResetHyperspeedModifier(source, cardFilter, _resetValue);
        hyperspeedModifier.skipSettingNotRemovedOnRestoreToNormal();
        Modifier maneuverModifier = new ResetManeuverModifier(source, cardFilter, _resetValue);
        maneuverModifier.skipSettingNotRemovedOnRestoreToNormal();

        // If during battle and the source if the action is not a weapon, then reset until end of the battle, otherwise
        // lasts for remainder of game (until card leaves play).
        if (gameState.isDuringBattle()
                && !Filters.weapon_or_character_with_permanent_weapon.accepts(gameState, modifiersQuerying, source)) {
            modifiersEnvironment.addUntilEndOfBattleModifier(armorModifier);
            modifiersEnvironment.addUntilEndOfBattleModifier(hyperspeedModifier);
            modifiersEnvironment.addUntilEndOfBattleModifier(maneuverModifier);
        }
        else {
            modifiersEnvironment.addUntilEndOfGameModifier(armorModifier);
            modifiersEnvironment.addUntilEndOfGameModifier(hyperspeedModifier);
            modifiersEnvironment.addUntilEndOfGameModifier(maneuverModifier);
        }

        actionsEnvironment.emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), _cardToReset));
    }
}
