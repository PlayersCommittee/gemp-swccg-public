package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractFireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.List;

/**
 * The action to choose which way to fire a weapon from multiple fire weapon options.
 */
public class FireWeaponChoiceAction extends AbstractFireWeaponAction {
    private List<FireWeaponAction> _fireWeaponActionChoices;
    private Action _that;

    /**
     * Creates an action for the specified player to choose which way to fire a weapon from multiple play options.
     * @param playerId the player
     * @param weaponToFire the weapon (or card with permanent weapon)
     * @param fireWeaponActionChoices the fire weapon action choices
     */
    public FireWeaponChoiceAction(String playerId, PhysicalCard weaponToFire, final List<FireWeaponAction> fireWeaponActionChoices) {
        super(weaponToFire, null, false, Filters.none);
        _fireWeaponActionChoices = fireWeaponActionChoices;
        _text = "Choose fire weapon option for " + GameUtils.getFullName(weaponToFire);
        _that = this;

        String[] actionChoiceTexts = new String[_fireWeaponActionChoices.size()];
        for (int i=0; i<actionChoiceTexts.length; ++i) {
            actionChoiceTexts[i] = _fireWeaponActionChoices.get(i).getText();
        }
        appendEffect(
                new PlayoutDecisionEffect(_that, playerId,
                        new MultipleChoiceAwaitingDecision("Choose fire weapon option for " + GameUtils.getCardLink(weaponToFire), actionChoiceTexts) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                FireWeaponAction actionChosen = _fireWeaponActionChoices.get(index);
                                actionChosen.setActionSource(getActionSource());
                                appendEffect(
                                        new StackActionEffect(_that, actionChosen));
                            }
                        }));
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }
        return null;
    }
}
