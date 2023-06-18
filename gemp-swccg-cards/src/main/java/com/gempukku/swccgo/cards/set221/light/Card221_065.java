package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Grievous Will Run And Hide
 */
public class Card221_065 extends AbstractNormalEffect {
    public Card221_065() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Grievous_Will_Run_And_Hide, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setGameText("Deploy on a battleground. [Clone Army] Jedi deploy -1 here. If you just won a battle (or just Force drained here), relocate this card to your [Clone Army] objective. If opponent just won a battle, opponent may relocate this Effect to an [Episode I] battleground. [Immune to Alter.]");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.battleground;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Icon.CLONE_ARMY, Filters.Jedi), -1, Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        Filter filter = Filters.and(Filters.your(self), Icon.CLONE_ARMY, Filters.Objective, Filters.not(Filters.hasAttached(self)));
        if (GameConditions.canTarget(game, self, filter)
                && (TriggerConditions.wonBattle(game, effectResult, playerId)
                || TriggerConditions.forceDrainCompleted(game, effectResult, playerId, Filters.here(self)))) {

            PhysicalCard objective = Filters.findFirstActive(game, self, filter);
            if (objective != null) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Relocate to "+GameUtils.getFullName(objective));
                action.setActionMsg("Relocate to "+GameUtils.getCardLink(objective));

                action.appendEffect(
                        new AttachCardFromTableEffect(action, self, objective));

                return Collections.singletonList(action);

            }
        }

        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.wonBattle(game, effectResult, playerId)
                && GameConditions.canSpotLocation(game, Filters.and(Icon.EPISODE_I, Filters.battleground, Filters.not(Filters.here(self))))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Relocate to a battleground");
            action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to an [Episode I] battleground");

            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose an [Episode I] battleground", Filters.and(Icon.EPISODE_I, Filters.battleground, Filters.not(Filters.here(self)))) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(new RespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                            action.appendEffect(
                                    new AttachCardFromTableEffect(action, self, finalTarget));
                        }
                    });
                }
            });
            return Collections.singletonList(action);
        }

        return null;
    }
}