package com.gempukku.swccgo.cards.set218.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.DrawsNoMoreThanBattleDestinyEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 18
 * Type: Interrupt
 * Subtype: Used
 * Title: Imperial Command & In Range
 */
public class Card218_001 extends AbstractUsedInterrupt {
    public Card218_001() {
        super(Side.DARK, 4, "Imperial Command & In Range", Uniqueness.UNIQUE, ExpansionSet.SET_18, Rarity.V);
        addComboCardTitles(Title.Imperial_Command, "In Range");
        setGameText("Take an admiral or general into hand from Reserve Deck; reshuffle. OR If your admiral is in battle at a system (or your general is in battle at a site), prevent opponent from drawing more than one battle destiny (your total battle destiny is +1 if a moff in battle). OR Raise your converted location to the top.");
        addIcons(Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_COMMAND_IN_RANGE__UPLOAD_ADMIRAL_OR_GENERAL;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take an admiral or general into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.admiral, Filters.general, Filters.grantedMayBeTargetedBy(self)), true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.or(Filters.admiral, Filters.grantedMayBeTargetedBy(self)), Filters.at(Filters.system)))
                || GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.general, Filters.at(Filters.site)))) {
            final String opponent = game.getOpponent(playerId);
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Limit opponent to one battle destiny");
            // Allow response(s)
            action.allowResponses("Prevent opponent from drawing more than one battle destiny",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawsNoMoreThanBattleDestinyEffect(action, opponent, 1));
                            if (GameConditions.isDuringBattleWithParticipant(game, Filters.moff)) {
                                action.appendEffect(
                                        new ModifyTotalBattleDestinyEffect(action, playerId, 1));
                            }
                        }
                    }
            );
            actions.add(action);
        }

        Filter yourConvertedLocationFilter = Filters.and(Filters.location, Filters.canBeConvertedByRaisingYourLocationToTop(playerId));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, yourConvertedLocationFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Raise a converted location");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose location to convert", yourConvertedLocationFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ConvertLocationByRaisingToTopEffect(action, targetedCard, true));
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