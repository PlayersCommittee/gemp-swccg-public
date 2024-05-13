package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Droid
 * Title: Dbbot
 */
public class Card302_027 extends AbstractDroid {
    public Card302_027() {
        super(Side.DARK, 1, 4, 2, 5, Title.Dbbot, Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("Dbb0t likes to talk a lot, he also seems to have a small amount of affection towards Summit and Dark Council members.");
		setGameText("[Deploy] free to James's site. [Deploy] mines and may use 1 Force to 'defuse' (lose) any one mine at same site. If at a Scomp link when opponent draws destiny of: 1-3, you may activate 2 Force; 4-6, you may draw top card of your Reserve Deck; 0 or 7, you may retrieve 2 Force.");
        addModelType(ModelType.MINING);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
		modifiers.add(new DeploysFreeToLocationModifier(self, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.James))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && GameConditions.isAtScompLink(game, self)) {

            if (GameConditions.isDestinyValueInRange(game, 1, 3)
                    && GameConditions.canActivateForce(game, playerId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Activate 2 Force");
                // Perform result(s)
                action.appendEffect(
                        new ActivateForceEffect(action, playerId, 2));
                return Collections.singletonList(action);
            }

            if(GameConditions.isDestinyValueInRange(game, 4, 6)
                    && GameConditions.hasReserveDeck(game, playerId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Draw top card of Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                return Collections.singletonList(action);
            }

            if(GameConditions.isDestinyValueEqualTo(game, 0)
                    || GameConditions.isDestinyValueEqualTo(game, 7)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Retrieve 2 Force");
                // Perform result(s)
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, 2));
                return Collections.singletonList(action);
            }
        }
        return null;
	}
		
	@Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.mine, Filters.atSameSite(self));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Defuse' mine");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose mine to 'defuse'", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("'Defuse' " + GameUtils.getCardLink(cardTargeted),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard cardToDefuse = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, cardToDefuse));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
