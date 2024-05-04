package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ImmunityGrantedResult;

import java.util.Collection;
import java.util.Collections;

/**
 * An effect to cause a card to be immune to cards of a specified title until the end of the turn.
 */
public class ImmuneToUntilEndOfTurnEffect extends AbstractSuccessfulEffect {
    private Collection<PhysicalCard> _targetCards;
    private String _immuneToName;

    /**
     * Creates an effect that causes a card to be immune to cards of a specified until the end of the turn.
     * @param action the action performing this effect
     * @param targetCard the card whose game text is canceled
     * @param immuneToName the title of card immune to
     */
    public ImmuneToUntilEndOfTurnEffect(Action action, PhysicalCard targetCard, String immuneToName) {
        this(action, Collections.singleton(targetCard), immuneToName);
    }

    /**
     * Creates an effect that causes a card to be immune to cards of a specified until the end of the turn.
     * @param action the action performing this effect
     * @param targetCards the cards that are immune to the specified card title
     * @param immuneToName the title of card immune to
     */
    public ImmuneToUntilEndOfTurnEffect(Action action, Collection<PhysicalCard> targetCards, String immuneToName) {
        super(action);
        _targetCards = targetCards;
        _immuneToName = immuneToName;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        PhysicalCard source = _action.getActionSource();

        gameState.sendMessage(GameUtils.getAppendedNames(_targetCards) + " " + GameUtils.be(_targetCards) + " immune to " + _immuneToName + " until end of the turn");
        gameState.cardAffectsCards(_action.getPerformingPlayer(), source, _targetCards);

        for (PhysicalCard targetCard : _targetCards) {
            // Filter for same card while it is in play
            Filter cardFilter = Filters.and(Filters.sameCardId(targetCard), Filters.in_play);

            modifiersEnvironment.addUntilEndOfTurnModifier(
                    new ImmuneToTitleModifier(source, cardFilter, _immuneToName));

            actionsEnvironment.emitEffectResult(new ImmunityGrantedResult(_action.getPerformingPlayer(), targetCard));
        }
    }
}
