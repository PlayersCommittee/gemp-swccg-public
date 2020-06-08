package com.gempukku.swccgo.cards.set106.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardPlayedThisTurnByPlayerCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayPlayToCancelForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Effect
 * Title: Faithful Service
 */
public class Card106_003 extends AbstractNormalEffect {
    public Card106_003() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, "Faithful Service", Uniqueness.UNIQUE);
        setLore("Unlike the Empire, the Alliance treats their droids with respect. Many droids volunteered to share the risk of battle and aid the Rebellion's assault on the Death Star.");
        setGameText("Deploy on opponent's location. Whenever you lose a droid from hand or Life Force, it satisfies Force loss up to its forfeit value. Once per turn, you play Scomp Link Access to cancel a Force drain where you have a droid. Effect canceled if opponent controls this location.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self.getOwner()), Filters.location);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition condition = new NotCondition(new CardPlayedThisTurnByPlayerCondition(self.getOwner(), Filters.Scomp_Link_Access));

        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.DROIDS_SATISFY_FORCE_LOSS_UP_TO_THEIR_FORFEIT_VALUE, self.getOwner()));
        modifiers.add(new MayPlayToCancelForceDrainModifier(self, Filters.Scomp_Link_Access, condition, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.droid))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)
                && GameConditions.controls(game, opponent, Filters.sameLocation(self))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}