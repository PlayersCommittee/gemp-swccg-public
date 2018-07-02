package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Ability, Ability, Ability
 */
public class Card5_110 extends AbstractNormalEffect {
    public Card5_110() {
        super(Side.DARK, 3, PlayCardZoneOption.OPPONENTS_SIDE_OF_TABLE, "Ability, Ability, Ability", Uniqueness.UNIQUE);
        setLore("'Ben. . . Ben, please! Ben. . . Leia! Hear me, Leia!'");
        setGameText("Deploy on opponent's side of table. At the end of opponent's deploy phase, if they did not deploy a card with ability, opponent loses 2 Force. Effect lost if opponent has more cards with ability on table than you. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.DEPLOY)) {
            self.setWhileInPlayData(null);
        }
        // Check condition(s)
        else if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.hasAbilityOrHasPermanentPilotWithAbility)) {
            self.setWhileInPlayData(new WhileInPlayData());
        }
        // Check condition(s)
        else if (TriggerConditions.isEndOfOpponentsPhase(game, self, effectResult, Phase.DEPLOY)
                && !GameConditions.cardHasWhileInPlayDataSet(self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + opponent + " lose 2 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 2));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (Filters.countActive(game, self, Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility))
                    > Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.hasAbilityOrHasPermanentPilotWithAbility))) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}