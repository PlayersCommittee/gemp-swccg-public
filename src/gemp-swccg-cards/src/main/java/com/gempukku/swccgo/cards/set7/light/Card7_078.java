package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.CancelOpponentsForceDrainModifiersModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: The Planet That It's Farthest From
 */
public class Card7_078 extends AbstractNormalEffect {
    public Card7_078() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.The_Planet_That_Its_Farthest_From, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("The Rebellion operates freely in many Outer Rim territories, allowing them to ignore Imperial activities in the core systems.");
        setGameText("Deploy on Tatooine system or Dagobah system. Target another system. At locations related to target system, opponent's Force drain modifiers are canceled. Effect canceled if opponent controls this system. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Filter filter = Filters.none;
        if (GameConditions.canSpotLocation(game, Filters.and(Filters.system, Filters.not(Filters.Tatooine_system)))) {
            filter = Filters.or(filter, Filters.Tatooine_system);
        }
        if (GameConditions.canSpotLocation(game, Filters.and(Filters.system, Filters.not(Filters.Dagobah_system)))) {
            filter = Filters.or(filter, Filters.Dagobah_system);
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
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelOpponentsForceDrainModifiersModifier(self, Filters.relatedLocationTo(self, Filters.targetedByCardOnTableAsTargetId(self, TargetId.EFFECT_TARGET_1))));
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