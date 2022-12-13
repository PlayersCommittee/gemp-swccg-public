package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LandedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Covert Landing
 */
public class Card8_045 extends AbstractUsedOrLostInterrupt {
    public Card8_045() {
        super(Side.LIGHT, 4, Title.Covert_Landing, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.U);
        setLore("The stolen code allowed the Rebels to land their strike team on the Endor surface undetected - or so they thought.");
        setGameText("USED: If your shuttle (including a shuttle vehicle) is in battle, add one battle destiny. LOST: If your starfighter just landed at a site where opponent has no presence, deploy scouts (and weapons on those scouts) aboard it (capacity permitting) from Reserve Deck; reshuffle.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.or(Filters.shuttle, Filters.shuttle_vehicle, Filters.grantedMayBeTargetedBy(self))))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.COVERT_LANDING__DOWNLOAD_SCOUTS_AND_WEAPONS;

        // Check condition(s)
        if (TriggerConditions.justLandedAt(game, effectResult, Filters.and(Filters.starfighter, Filters.hasAnyAvailablePilotOrPassengerCapacity), Filters.and(Filters.site, Filters.not(Filters.occupies(opponent))))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {
            final PhysicalCard cardLanded = ((LandedResult) effectResult).getMovedCards().iterator().next();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Deploy scouts aboard " + GameUtils.getFullName(cardLanded) + " from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy scouts (and weapons on those scouts) aboard " + GameUtils.getCardLink(cardLanded) + " from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            Collection<PhysicalCard> cardsAlreadyAboard = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.scout, Filters.aboard(cardLanded)));
                            Filter targetFilter = Filters.or(cardLanded, Filters.and(Filters.your(self), Filters.scout, Filters.aboard(cardLanded), Filters.not(Filters.in(cardsAlreadyAboard))));
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardsToTargetFromReserveDeckEffect(action, Filters.or(Filters.scout, Filters.character_weapon), 1, Integer.MAX_VALUE, targetFilter, false, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}