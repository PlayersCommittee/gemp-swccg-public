package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Character
 * Subtype: Rebel
 * Title: Leia (V)
 */
public class Card200_018 extends AbstractRebel {
    public Card200_018() {
        super(Side.LIGHT, 1, 3, 3, 4, 5, "Leia", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Adopted daughter of the Viceroy and First Chairman of Alderaan. Became a political leader at a young age. The injustices of the New Order led her to join the Rebellion.");
        setGameText("While at opponent's Galactic Senate, Leia's game text may not be canceled and may lose 1 Force to place Leia and any character present out of play. During battle, may cancel game text of a leader of ability < 5 with Leia for remainder of turn.");
        addPersona(Persona.LEIA);
        addIcons(Icon.REPUBLIC, Icon.PREMIUM, Icon.WARRIOR, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.LEADER, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, new AtCondition(self, Filters.and(Filters.opponents(self), Filters.Galactic_Senate))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isAtLocation(game, self, Filters.and(Filters.opponents(self), Filters.Galactic_Senate))
                && Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY).accepts(game, self)) {
            TargetingReason targetingReason = TargetingReason.TO_BE_PLACED_OUT_OF_PLAY;
            Filter characterFilter = Filters.and(Filters.character, Filters.not(self), Filters.present(self));
            if (GameConditions.canTarget(game, self, targetingReason, characterFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place " + GameUtils.getFullName(self) + " and other character out of play");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose other character", targetingReason, characterFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                                action.addAnimationGroup(self, cardTargeted);
                                // Pay cost(s)
                                action.appendCost(
                                        new LoseForceEffect(action, playerId, 1, true));
                                // Allow response(s)
                                action.allowResponses("Place " + GameUtils.getCardLink(self) + " and " + GameUtils.getCardLink(cardTargeted) + " out of play",
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard otherCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PlaceCardsOutOfPlayFromTableEffect(action, Arrays.asList(self, otherCharacter)));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        final Filter leaderFilter = Filters.and(Filters.leader, Filters.with(self), Filters.abilityLessThan(5));

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canTarget(game, self, leaderFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Cancel a leader's game text");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose leader", leaderFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, targetedCard));
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
