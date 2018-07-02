package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.timing.actions.attack.AttackWeaponsSegmentAction;
import com.gempukku.swccgo.logic.timing.actions.battle.BattleWeaponsSegmentAction;

import java.util.Stack;

/**
 * The action stack for the action that occur within a game process (phase of turn, battle, etc.).
 */
public class ActionStack implements Snapshotable<ActionStack> {
    private Stack<Action> _actionStack = new Stack<Action>();

    /**
     * Needed to generate snapshot.
     */
    public ActionStack() {
    }

    @Override
    public void generateSnapshot(ActionStack selfSnapshot, SnapshotData snapshotData) {
        ActionStack snapshot = selfSnapshot;

        // Set each field
        for (Action actionInStack : _actionStack) {
            if (actionInStack instanceof AttackWeaponsSegmentAction) {
                snapshot._actionStack.add(snapshotData.getDataForSnapshot((AttackWeaponsSegmentAction) actionInStack));
            }
            else if (actionInStack instanceof BattleWeaponsSegmentAction) {
                snapshot._actionStack.add(snapshotData.getDataForSnapshot((BattleWeaponsSegmentAction) actionInStack));
            }
            else if (actionInStack instanceof InitiateAttackCreatureAction) {
                snapshot._actionStack.add(snapshotData.getDataForSnapshot((InitiateAttackCreatureAction) actionInStack));
            }
            else if (actionInStack instanceof InitiateAttackNonCreatureAction) {
                snapshot._actionStack.add(snapshotData.getDataForSnapshot((InitiateAttackNonCreatureAction) actionInStack));
            }
            else if (actionInStack instanceof InitiateBattleAction) {
                snapshot._actionStack.add(snapshotData.getDataForSnapshot((InitiateBattleAction) actionInStack));
            }
            else if (actionInStack instanceof SubAction) {
                snapshot._actionStack.add(snapshotData.getDataForSnapshot((SubAction) actionInStack));
            }
            else if (actionInStack instanceof SystemQueueAction) {
                snapshot._actionStack.add(snapshotData.getDataForSnapshot((SystemQueueAction) actionInStack));
            }
            else if (actionInStack instanceof TopLevelGameTextAction) {
                snapshot._actionStack.add(snapshotData.getDataForSnapshot((TopLevelGameTextAction) actionInStack));
            }
            else {
                throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with ActionStack containing action " + actionInStack.getClass().getSimpleName());
            }
        }
    }

    /**
     * Adds an action to the action stack.
     * @param action the action to add
     */
    public void stackAction(Action action) {
        _actionStack.add(action);
    }

    /**
     * Gets the next effect of the top action on the stack to process. If the top action no remaining effects, then
     * that top action is removed from the action stack and null is returned.
     * @param game the game
     * @return the next effect of the top action to process, or null
     */
    public Effect getNextEffect(SwccgGame game) {
        Action action = _actionStack.peek();
        Effect effect = action.nextEffect(game);
        if (effect != null) {
            return effect;
        }
        Effect afterEffect = action.nextAfterEffect(game);
        if (afterEffect != null) {
            return afterEffect;
        }
        else {
            _actionStack.remove(_actionStack.lastIndexOf(action));
            return null;
        }
    }

    /**
     * Determines if the action stack is empty.
     * @return true if the action stack is empty, otherwise false
     */
    public boolean isEmpty() {
        return _actionStack.isEmpty();
    }

    /**
     * Log the contents of the action stack.
     */
    public void dumpStack(SwccgGame game) {
        GameState gameState = game.getGameState();
        int actionNum = 1;

        gameState.sendMessage("Action stack size: " + _actionStack.size());
        for (Action action : _actionStack) {
            gameState.sendMessage("Action " + (actionNum++) + ": " + action.getClass().getSimpleName() + (action.getActionSource() != null ? " Source: " + GameUtils.getFullName(action.getActionSource()) : ""));
        }
    }
}
