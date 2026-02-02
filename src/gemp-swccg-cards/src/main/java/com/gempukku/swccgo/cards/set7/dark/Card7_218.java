package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.CancelOpponentsForceDrainModifiersModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: A Bright Center To The Universe
 */
public class Card7_218 extends AbstractNormalEffect {
    public Card7_218() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.A_Bright_Center_To_The_Universe, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("The intimidating power of the Empire was focused in the core systems, allowing the Emperor to ignore minor activities occurring on the Outer Rim.");
        setGameText("Deploy on Death Star system or Coruscant system. Target another system. At locations related to target system, opponent's Force drain modifiers are canceled. Effect canceled if opponent controls this system. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Filter filter = Filters.none;
        if (GameConditions.canSpotLocation(game, Filters.and(Filters.system, Filters.not(Filters.Death_Star_system)))) {
            filter = Filters.or(filter, Filters.Death_Star_system);
        }
        if (GameConditions.canSpotLocation(game, Filters.and(Filters.system, Filters.not(Filters.Coruscant_system)))) {
            filter = Filters.or(filter, Filters.Coruscant_system);
        }
        return filter;
    }

    @Override
    protected List<TargetingEffect> getGameTextTargetCardsWhenDeployedEffects(final Action action, String playerId, SwccgGame game, final PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        final Filter targetFilter = Filters.and(Filters.system, Filters.not(target));
        TargetingEffect targetingEffect = new TargetCardOnTableEffect(action, playerId, "Choose system", targetFilter) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard target) {
                action.addAnimationGroup(target);
                self.setTargetedCard(TargetId.EFFECT_TARGET_1, targetGroupId, target, targetFilter);
            }
        };
        return Collections.singletonList(targetingEffect);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition cancelsOpponentsForceDrainModifiersEverywhere = new GameTextModificationCondition(self, ModifyGameTextType.A_BRIGHT_CENTER_TO_THE_UNIVERSE__CANCELS_OPPONENTS_FORCE_DRAIN_MODIFIERS_EVERYWHERE);
        
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelOpponentsForceDrainModifiersModifier(self, new NotCondition(cancelsOpponentsForceDrainModifiersEverywhere), Filters.relatedLocationTo(self, Filters.targetedByCardOnTableAsTargetId(self, TargetId.EFFECT_TARGET_1))));
        modifiers.add(new CancelOpponentsForceDrainModifiersModifier(self, cancelsOpponentsForceDrainModifiersEverywhere));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)
                && GameConditions.controls(game, opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.sameSystem(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
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