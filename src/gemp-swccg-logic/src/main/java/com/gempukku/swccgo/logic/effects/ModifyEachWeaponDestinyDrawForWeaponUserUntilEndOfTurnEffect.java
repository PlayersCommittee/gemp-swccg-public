package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect to modify the weapon destiny draws for weapons fired by a card until end of the turn.
 */
public class ModifyEachWeaponDestinyDrawForWeaponUserUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _weaponUser;
    private float _modifierAmount;

    /**
     * Creates an effect that modifies the weapon destiny draws for weapons fired by a card until end of the turn.
     * @param action the action performing this effect
     * @param weaponUser the card whose weapon destiny draws are modified
     * @param modifierAmount the amount of the modifier
     */
    public ModifyEachWeaponDestinyDrawForWeaponUserUntilEndOfTurnEffect(Action action, PhysicalCard weaponUser, float modifierAmount) {
        super(action);
        _weaponUser = weaponUser;
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        String actionMsg;
        if (_modifierAmount < 0)
            actionMsg = "subtracts " + GuiUtils.formatAsString(-_modifierAmount) + " from " + GameUtils.getCardLink(_weaponUser) + "'s weapon destiny draws until end of the turn";
        else
            actionMsg = "adds " + GuiUtils.formatAsString(_modifierAmount) + " to " + GameUtils.getCardLink(_weaponUser) + "'s weapon destiny draws until end of the turn";

        if (_action.getPerformingPlayer() == null)
            gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " " + actionMsg);
        else
            gameState.sendMessage(_action.getPerformingPlayer() + " " + actionMsg + " using " + GameUtils.getCardLink(_action.getActionSource()));

        // Filter for same card while it is in play
        Filter cardFilter = Filters.and(Filters.sameCardId(_weaponUser), Filters.in_play);

        modifiersEnvironment.addUntilEndOfTurnModifier(
                new EachWeaponDestinyModifier(source, Filters.any, cardFilter, _modifierAmount));
    }
}
