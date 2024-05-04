package com.gempukku.swccgo.cards.set106.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardPlayedThisTurnByPlayerCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Title: Forced Servitude
 */
public class Card106_014 extends AbstractNormalEffect {
    public Card106_014() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Forced Servitude", Uniqueness.UNIQUE, ExpansionSet.OTSD, Rarity.PM);
        setLore("The Empire often uses droids for nefarious purposes. Imperials compel droids to do jobs that are repugnant to humans. An automaton has no ethical conscience.");
        setGameText("Deploy on opponent's location. Whenever you lose a droid from hand or Life Force, it satisfies Force loss up to its forfeit value. Once per turn, you play Imperial Code Cylinder to cancel a Force drain where you have a droid. Effect canceled if opponent controls this location.");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self.getOwner()), Filters.location);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition condition = new NotCondition(new CardPlayedThisTurnByPlayerCondition(self.getOwner(), Filters.Imperial_Code_Cylinder));

        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.DROIDS_SATISFY_FORCE_LOSS_UP_TO_THEIR_FORFEIT_VALUE, self.getOwner()));
        modifiers.add(new MayPlayToCancelForceDrainModifier(self, Filters.Imperial_Code_Cylinder, condition, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.droid))));
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