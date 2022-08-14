package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Lost
 * Title: Anakin Skywalker (V)
 */
public class Card213_049 extends AbstractLostInterrupt {
    public Card213_049() {
        super(Side.LIGHT, 4, Title.Anakin_Skywalker, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'You were right about me. Tell your sister ... you were right.'");
        setGameText("If I Feel The Conflict on table, during any move phase, take [Endor] Leia or [Dagobah] Luke into hand from Reserve Deck; reshuffle. OR During a battle at a site involving [Death Star II] Luke, choose: Add one destiny to attrition. OR Cancel the game text of a character of ability < 4.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_13);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();


        GameTextActionId gameTextActionId = GameTextActionId.ANAKIN_SKYWALKER_V__UPLOAD_LEIA_OR_LUKE;

        if (GameConditions.canSpot(game, self, Filters.I_Feel_The_Conflict)
                && GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Leia or Luke into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take [Endor] Leia or [Dagobah] Luke into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.and(Icon.ENDOR, Filters.Leia), Filters.and(Icon.DAGOBAH, Filters.Luke)), true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Icon.DEATH_STAR_II, Filters.Luke, Filters.at(Filters.site)))) {

            if (GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add destiny to attrition");
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new AddDestinyToAttritionEffect(action, 1)
                        );
                    }
                });

                actions.add(action);
            }

            Filter characterAbilityLessThanFour = Filters.and(Filters.canBeTargetedBy(self), Filters.character, Filters.abilityLessThan(4));

            if (GameConditions.isDuringBattleWithParticipant(game, characterAbilityLessThanFour)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Cancel game text of character ability less than 4");
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character of ability less than 4", characterAbilityLessThanFour) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                action.allowResponses(new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                        action.appendEffect(
                                                new CancelGameTextUntilEndOfBattleEffect(action, finalTarget)
                                        );
                                    }
                                });
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }
}