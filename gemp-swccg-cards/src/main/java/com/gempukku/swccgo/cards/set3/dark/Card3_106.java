package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataEqualsCondition;
import com.gempukku.swccgo.cards.conditions.PlayersTurnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Title: Imperial Domination
 */
public class Card3_106 extends AbstractNormalEffect {
    public Card3_106() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Imperial Domination", Uniqueness.RESTRICTED_2);
        setLore("When Vader's forces impose the New Order upon a region, Rebel resources and lifelines are quickly eliminated.");
        setGameText("Deploy on any location. Whenever you control this location during your control phase but do not Force drain here, opponent generates no Force here on opponent's next turn.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.location;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new GenerateNoForceModifier(self, Filters.here(self), new AndCondition(new PlayersTurnCondition(opponent),
                new InPlayDataEqualsCondition(self, false)), opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.sameLocation(self))) {
            self.setWhileInPlayData(new WhileInPlayData(true));
            return null;
        }
        // Check condition(s)
        if (self.getWhileInPlayData() == null
                && (TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.CONTROL)
                || (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)))
                && GameConditions.controls(game, playerId, Filters.sameLocation(self))) {
            self.setWhileInPlayData(new WhileInPlayData(false));
            return null;
        }
        // Check condition(s)
        if (TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self)) {
            self.setWhileInPlayData(null);
        }
        return null;
    }
}