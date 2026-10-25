package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;

/**
 * Shared effect that initiates a battle using {@link UnusualBattleInitOptions}.
 * Used by Here We Go Again and Counterattack so both cards share one re-battle path.
 */
public class InitiateUnusualBattleEffect extends AbstractSubActionEffect {
    private final UnusualBattleInitOptions _options;

    public InitiateUnusualBattleEffect(Action action, UnusualBattleInitOptions options) {
        super(action);
        _options = options;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return _options != null && _options.getLocation() != null;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _options.getPlayerId());

        // 1) Clear prior participation for cards currently at the re-battle location
        if (_options.isClearPriorParticipationForLocation()) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            PhysicalCard location = _options.getLocation();
                            Collection<PhysicalCard> atLocation = Filters.filterAllOnTable(game, Filters.at(location));
                            for (PhysicalCard card : atLocation) {
                                game.getModifiersQuerying().clearBattleParticipation(card);
                            }
                        }
                    }
            );
        }

        // 2) Pay the normal initiate-battle Force cost (text is not free)
        subAction.appendEffect(
                new PayInitiateBattleCostEffect(subAction, _options.getLocation(), _options.getPlayerId(), _options.isForFree()));

        // 3) Begin a normal battle at the location with extra until-end-of-battle modifiers
        subAction.appendEffect(
                new BattleEffect(subAction, _options.getLocation(), false, null, _options.getExtraUntilEndOfBattleModifiers()));

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
