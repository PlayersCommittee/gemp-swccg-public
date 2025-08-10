package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used
 * Title: Choke (V)
 */
public class Card223_034 extends AbstractLostInterrupt {
    public Card223_034() {
        super(Side.LIGHT, 4, "Choke", Uniqueness.UNRESTRICTED, ExpansionSet.SET_23, Rarity.V);
        setLore("Reaching out with the Force, Luke rendered Ortugg unconscious without doing the Gamorrean any actual harm.");
        setGameText("Cancel None Shall Pass. OR If a battle just initiated at a First Light or Jabba's Palace site, cancel game text of a participating character of ability < 4. OR If your character of ability = 5 on table, take an Interrupt with printed destiny = 4 into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_23);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
                if (GameConditions.canTargetToCancel(game, self, Filters.None_Shall_Pass)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.None_Shall_Pass, Title.None_Shall_Pass);
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.CHOKE__UPLOAD_DESTINY4;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.character, Filters.abilityEqualTo(5)))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take an Interrupt into hand from Reserve Deck");
            action.setActionMsg("Take an Interrupt with printed destiny = 4 into hand from Reserve Deck; reshuffle");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.destinyEqualTo(4), Filters.Interrupt), true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
    
    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.None_Shall_Pass)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter characterAbilityLessThanFour = Filters.and(Filters.canBeTargetedBy(self), Filters.character, Filters.abilityLessThan(4), Filters.participatingInBattle);
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.or(Filters.First_Light_site, Filters.Jabbas_Palace_site))
                && GameConditions.isDuringBattleWithParticipant(game, characterAbilityLessThanFour)) {
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
            return Collections.singletonList(action);
        }
        return null;
    }
}
