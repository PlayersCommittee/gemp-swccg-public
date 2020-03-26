package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Human Shield
 */
public class Card5_145 extends AbstractUsedOrLostInterrupt {
    public Card5_145() {
        super(Side.DARK, 5, "Human Shield");
        setLore("A shining example of Imperial honor and bravery.");
        setGameText("USED: If an opponent's weapon just 'hit' an Imperial present with a a captive being escorted, the captive is 'hit' instead. LOST: During a battle at site, you may forfeit any or all captives present.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        Filter impPresentWithCaptive = Filters.and(Filters.Imperial, Filters.presentInBattle, Filters.escorting(Filters.any));
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, impPresentWithCaptive, Filters.and(Filters.opponents(playerId), Filters.weapon))) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            Collection<PhysicalCard> validCaptives = Filters.filter(game.getGameState().getCaptivesOfEscort(cardHit), game, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_HIT));
            if(!validCaptives.isEmpty()){
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Make captive 'hit' instead");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose captive", SpotOverride.INCLUDE_CAPTIVE, TargetingReason.TO_BE_HIT, Filters.in(validCaptives)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s)
                                action.allowResponses("Make " + GameUtils.getCardLink(cardTargeted) + " 'hit' instead",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard captiveToHit = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                ((AboutToBeHitResult) effectResult).getPreventableCardEffect().preventEffectOnCard(self);
                                                action.appendEffect(
                                                        new HitCardEffect(action, captiveToHit, self));
                                            }
                                        });
                            }
                        }
                );
                actions.add(action);
            }
        }

        Filter yourCharacterEscortingACaptive = Filters.and(Filters.your(playerId), Filters.character, Filters.escorting(Filters.any));

        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isDuringBattleWithParticipant(game, yourCharacterEscortingACaptive)
                && GameConditions.isDuringBattleAt(game, Filters.site)) {

            Collection<PhysicalCard> charactersEscortingCaptives = Filters.filterActive(game, self, yourCharacterEscortingACaptive);
            Collection<PhysicalCard> validCaptives = new LinkedList<>();
            for(PhysicalCard characterEscortingCaptives: charactersEscortingCaptives){
                validCaptives.addAll(game.getGameState().getCaptivesOfEscort(characterEscortingCaptives));
            }

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Forfeit captives");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardsOnTableEffect(action, playerId, "Target captives(s) to forfeit", 1, validCaptives.size(), Filters.in(validCaptives)) {
                        @Override
                        protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> targetedCards) {
                            action.allowResponses("Forfeit ",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.addAnimationGroup(targetedCards);
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId);
                                            action.appendEffect(
                                                    new ForfeitCardsFromTableSimultaneouslyEffect(action, finalCharacters)
                                            );
                                        }
                                    });
                        }

                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
