package com.gempukku.swccgo.cards.effects.complete;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to choose a card pile.
 * The card piles chosen from are not required to have cards in them.
 */
public abstract class ChoosePileEffect extends AbstractChoosePileEffect {
    private String _playerId;
    private String _text;
    private String _zoneOwner;
    private Zone[] _zones;

    public ChoosePileEffect(Action action, String playerId, String text, String zoneOwner, Zone[] zones) {
        super(action);
        _playerId = playerId;
        _text = text;
        _zoneOwner = zoneOwner;
        _zones = zones;
    }

    @Override
    protected void doPlayEffect(final SwccgGame game) {
        String[] zoneNames = new String[_zones.length];
        for (int i=0; i<_zones.length; ++i) {
            zoneNames[i] = _zones[i].getHumanReadable();
        }

        game.getUserFeedback().sendAwaitingDecision(_playerId,
                new MultipleChoiceAwaitingDecision(_text, zoneNames) {
                    @Override
                    public void validDecisionMade(int index, String result) {
                        Zone zone = _zones[index];
                        pileChosen(game, _zoneOwner, zone);
                    }
                });
    }
}
