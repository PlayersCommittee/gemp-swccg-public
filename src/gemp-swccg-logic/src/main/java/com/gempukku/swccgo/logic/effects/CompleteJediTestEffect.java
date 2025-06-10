package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.JediTestStatus;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.JediTestCompletedResult;


/**
 * An effect that completes the specified Jedi Test.
 */
public class CompleteJediTestEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _jediTest;

    /**
     * Creates an effect that completes the specified Jedi Test.
     * @param action the action performing this effect
     * @param jediTest the Jedi Test to complete
     */
    public CompleteJediTestEffect(Action action, PhysicalCard jediTest) {
        super(action);
        _jediTest = jediTest;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        if (_jediTest.getJediTestStatus() != JediTestStatus.COMPLETED) {
            PhysicalCard apprentice = _jediTest.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE);
            gameState.sendMessage(GameUtils.getCardLink(_jediTest) + " is 'completed' by " + GameUtils.getCardLink(apprentice));
            if (_jediTest.getZone() == Zone.ATTACHED) {
                if (modifiersQuerying.isJediTestPlacedOnTableWhenCompleted(gameState, _jediTest)) {
                    gameState.relocateCardToSideOfTable(_jediTest, _jediTest.getOwner());
                }
                else {
                    gameState.moveCardToAttached(_jediTest, apprentice);
                }
            }
            _jediTest.setJediTestStatus(JediTestStatus.COMPLETED);
            modifiersQuerying.completedJediTest(_jediTest, apprentice);

            game.getActionsEnvironment().emitEffectResult(new JediTestCompletedResult(_action, _jediTest, apprentice));
        }
    }
}
