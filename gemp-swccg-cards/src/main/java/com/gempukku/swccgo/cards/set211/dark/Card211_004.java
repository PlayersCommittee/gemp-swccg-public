package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ResetForfeitUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsAtSameLocationEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Character
 * Subtype: First Order
 * Title: Lt. Poldin Lehuse
 */
public class Card211_004 extends AbstractFirstOrder {
    public Card211_004() {
        super(Side.DARK, 2, 2, 2, 2, 4, "Lt. Poldin Lehuse", Uniqueness.UNIQUE);
        setLore("");
        setGameText("[Pilot] 2. During battle, if piloting a [First Order] TIE, may target an opponent's capital starship at same system. Draw destiny. If destiny > 2, choose one pilot character aboard target to be forfeit = 0 for remainder of battle.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.PILOT, Icon.EPISODE_VII);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_7;
        Filter opponentsCapitalsWithPilotsAboardHere = Filters.and(Filters.opponents(playerId), Filters.atSameSystem(self), Filters.capital_starship, Filters.hasAboard(self, Filters.pilot));

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattle(game)
                && GameConditions.isPiloting(game, self, Filters.and(Filters.TIE, Icon.FIRST_ORDER))
                && GameConditions.isDuringBattleWithParticipant(game, opponentsCapitalsWithPilotsAboardHere)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw destiny against opponent's capital.");
            action.appendUsage(
                    new OncePerBattleEffect(action)
            );
            action.appendTargeting(
                    new TargetCardsAtSameLocationEffect(action, playerId, "Target opponent's capital starship.", 1, 1, opponentsCapitalsWithPilotsAboardHere) {
                        @Override
                        protected void cardsTargeted(int targetGroupId, final Collection<PhysicalCard> targetedCards) {
                            if(!targetedCards.isEmpty()){
                                final PhysicalCard opponentsCapital = targetedCards.iterator().next();
                                action.addAnimationGroup(opponentsCapital);
                                action.allowResponses("Draw destiny against " + GameUtils.getCardLink(opponentsCapital),
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId) {
                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                final Collection<PhysicalCard> pilotsAboard = Filters.filterActive(game, self, Filters.and(Filters.pilot, Filters.aboardOrAboardCargoOf(opponentsCapital)));
                                                                if (totalDestiny > 2 && !pilotsAboard.isEmpty()) {
                                                                    action.appendEffect(
                                                                            new ChooseCardOnTableEffect(action, playerId, "Make pilot character aboard forfeit = 0", pilotsAboard) {
                                                                                @Override
                                                                                protected void cardSelected(PhysicalCard selectedCard) {
                                                                                    action.setText("Reset forfeit of " + GameUtils.getCardLink(selectedCard) + " to 0");
                                                                                    action.setActionMsg("Reset forfeit of " + GameUtils.getCardLink(selectedCard) + " to 0");
                                                                                    // Perform result(s)
                                                                                    action.appendEffect(
                                                                                            new ResetForfeitUntilEndOfTurnEffect(action, selectedCard, 0));
                                                                                }
                                                                            }
                                                                    );
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                    }
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}
