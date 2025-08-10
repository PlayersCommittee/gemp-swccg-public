package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Battle Of Geonosis
 */
public class Card221_051 extends AbstractNormalEffect {
    public Card221_051() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Battle Of Geonosis", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setGameText("If a Geonosis location on table, deploy on table. Once per battle, may deploy or move a clone as a 'react' to same location as your Jedi or Padawan. While your clone in battle with a Jedi or Padawan, opponent's total battle destiny is -1 (-2 if Yoda in battle). [Immune to Alter.]");
        addIcons(Icon.EPISODE_I, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.Geonosis_location);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        final String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        Condition oncePerBattleCondition = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return game.getModifiersQuerying().getUntilEndOfBattleLimitCounter(self, playerId, self.getCardId(), GameTextActionId.OTHER_CARD_ACTION_1).getUsedLimit()<1;
            }
        };

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalBattleDestinyModifier(self,
                new InBattleCondition(self, Filters.and(Filters.your(self), Filters.clone, Filters.with(self, Filters.or(Filters.Jedi, Filters.padawan)))),
                new ConditionEvaluator(-1, -2, new InBattleCondition(self, Filters.Yoda)), opponent));
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy a clone as a react", oncePerBattleCondition, playerId, Filters.and(Filters.your(self), Filters.clone), Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.or(Filters.Jedi, Filters.padawan)))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.or(Filters.Jedi, Filters.padawan)))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            Filter filter = Filters.and(Filters.your(self), Filters.clone, Filters.canMoveAsReactAsActionFromOtherCard(self, false, 0, false));

            if (GameConditions.canTarget(game, self, filter)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Move a clone as a react");

                action.appendUsage(
                        new OncePerBattleEffect(action));

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose clone to move as a react", filter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.allowResponses(new RespondableEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                PhysicalCard finalClone = action.getPrimaryTargetCard(targetGroupId);
                                action.appendEffect(
                                        new MoveAsReactEffect(action, finalClone, false));
                            }
                        });
                    }
                });

                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isReact(game, effect)
                && effect.getAction()!=null) {
            PhysicalCard source = effect.getAction().getActionSource();

            // if this card is the source of the react then increment the per battle limit so the condition above can check for it
            if (source != null
                    && Filters.sameCardId(self).accepts(game, source)) {
                game.getModifiersQuerying().getUntilEndOfBattleLimitCounter(self, self.getOwner(), self.getCardId(), GameTextActionId.OTHER_CARD_ACTION_1).incrementToLimit(1,1);
            }
        }

        return null;
    }
}