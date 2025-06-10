package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ResetOrModifyCardAttributeResult;

import java.util.List;


/**
 * An effect to restore a card's Armor, Maneuver & Hyperspeed to normal.
 */
public class RestoreArmorManeuverHyperspeedToNormalEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToRestore;

    /**
     * Creates an effect that restores a card's forfeit to normal.
     *
     * @param action        the action performing this effect
     * @param cardToRestore the card to restore
     */
    public RestoreArmorManeuverHyperspeedToNormalEffect(Action action, PhysicalCard cardToRestore) {
        super(action);
        _cardToRestore = cardToRestore;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        gameState.sendMessage(GameUtils.getCardLink(_cardToRestore) + "'s armor/maneuver, and hyperspeed are set to normal by " + GameUtils.getCardLink(_action.getActionSource()));
        game.getGameState().cardAffectsCard(_action.getActionSource().getOwner(), _action.getActionSource(), _cardToRestore);

        // Get all the persistent modifiers that can affect the card's armor, maneuver, and hyperspeed and exclude this card from those modifiers.
        List<Modifier> modifiers = modifiersQuerying.getPersistentModifiersAffectingCard(gameState, _cardToRestore);

        // Only clean modifiers that are originating specifically from an ion cannon weapon
        for (Modifier modifier : modifiers) {
            PhysicalCard affectingCard = modifier.getSource(gameState);
            if (modifiersQuerying.hasKeyword(gameState, affectingCard, Keyword.ION_CANNON)) {
                if ((modifier.getModifierType() == ModifierType.ARMOR || modifier.getModifierType() == ModifierType.UNMODIFIABLE_ARMOR) ||
                        (modifier.getModifierType() == ModifierType.MANEUVER || modifier.getModifierType() == ModifierType.UNMODIFIABLE_MANEUVER) ||
                        (modifier.getModifierType() == ModifierType.HYPERSPEED || modifier.getModifierType() == ModifierType.UNMODIFIABLE_HYPERSPEED)) {
                    if (!modifier.isNotRemovedOnRestoreToNormal()) {
                        modifiersQuerying.excludeFromBeingAffected(modifier, _cardToRestore);
                    }
                }
            }
        }

        game.getActionsEnvironment().emitEffectResult(new ResetOrModifyCardAttributeResult(_action.getPerformingPlayer(), _cardToRestore));
    }
}