package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ExplodingProgramTrapResult;

import java.util.Collection;

/**
 * An effect that carries out the effects of an 'exploding' program trap.
 */
public class ExplodeProgramTrapEffect extends AbstractSubActionEffect {
    private PhysicalCard _programTrap;

    /**
     * Creates an effect that carries out the effects of an 'exploding' program trap.
     * @param action the action performing this effect
     * @param programTrap the site to 'collapse'
     */
    public ExplodeProgramTrapEffect(Action action, PhysicalCard programTrap) {
        super(action);
        _programTrap = programTrap;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new SendMessageEffect(subAction, GameUtils.getCardLink(_programTrap) + " 'explodes'"));
        subAction.appendEffect(
                new TriggeringResultEffect(subAction, new ExplodingProgramTrapResult(_programTrap)));
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (Filters.onTable.accepts(game, _programTrap)) {
                            Collection<PhysicalCard> charactersToLose = Filters.filterAllOnTable(game,
                                    Filters.or(Filters.hasAttached(_programTrap), Filters.and(Filters.character, Filters.present(_programTrap))));
                            subAction.appendEffect(
                                    new LoseCardsFromTableEffect(subAction, charactersToLose, true));
                        }
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
