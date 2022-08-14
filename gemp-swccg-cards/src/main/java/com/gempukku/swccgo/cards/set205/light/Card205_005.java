package com.gempukku.swccgo.cards.set205.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Effect
 * Title: Leia Of Alderaan (V)
 */
public class Card205_005 extends AbstractNormalEffect {
    public Card205_005() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Leia_Of_Alderaan, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("The face that launched a thousand starships.");
        setGameText("Deploy on Leia (or your female of ability < 4). Opponent may not cancel or reduce Force drains at same battleground. If on Leia, she is power +2 and, once per battle, may cancel the game text of an opponent's leader of ability < 4 here.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_5);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Persona.LEIA, Filters.and(Filters.your(self), Filters.female, Filters.abilityLessThan(4)));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.or(Persona.LEIA, Filters.and(Filters.female, Filters.abilityLessThan(4)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter sameBattleground = Filters.and(Filters.sameLocation(self), Filters.battleground);
        Condition isOnLeia = new AttachedCondition(self, Filters.Leia);
        Filter attachedTo = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, sameBattleground, opponent, null));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, sameBattleground, opponent, null));
        modifiers.add(new PowerModifier(self, attachedTo, isOnLeia, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        final Filter leaderFilter = Filters.and(Filters.leader, Filters.opponents(self), Filters.with(self), Filters.abilityLessThan(4));

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canTarget(game, self, leaderFilter)
                && GameConditions.isAttachedTo(game, self, Filters.Leia)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel a leader's game text");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose leader", leaderFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard target = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, target));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}