package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Armed And Dangerous
 */
public class Card5_032 extends AbstractLostInterrupt {
    public Card5_032() {
        super(Side.LIGHT, 4, "Armed And Dangerous");
        setLore("The ability to retrieve his weapon by use of the Force was now second nature to Luke.");
        setGameText("If a battle or duel was just initiated at a site, deploy (for free) a unique matching weapon on one of your participating characters from hand or Reserve Deck; reshuffle (if from Reserve Deck).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final Filter targetFilter = Filters.and(Filters.your(self), Filters.character, Filters.or(Filters.participatingInDuel, Filters.participatingInBattle));

        Collection<PhysicalCard> matchingWeapons = new LinkedList<>();
        Collection<PhysicalCard> list = Filters.filterActive(game, self, targetFilter);
        for(PhysicalCard character : list){
            matchingWeapons.addAll(Filters.filter(game.getGameState().getReserveDeck(playerId), game, Filters.matchingWeaponForCharacter(character)));
            matchingWeapons.addAll(Filters.filter(game.getGameState().getHand(playerId), game, Filters.matchingWeaponForCharacter(character)));
        }
        final Filter weaponFilter = Filters.in(matchingWeapons);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.site)
                || TriggerConditions.duelInitiatedAt(game, effectResult, Filters.site)
                && GameConditions.isDuringBattleWithParticipant(game, targetFilter)
                || GameConditions.isDuringDuelWithParticipant(game, targetFilter)) {

            if (GameConditions.hasInHand(game, playerId, Filters.and(weaponFilter, Filters.deployableToTarget(self, targetFilter, true, 0)))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Deploy weapon from hand");
                // Allow response(s)
                action.allowResponses("Deploy a matching character weapon from hand",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DeployCardToTargetFromHandEffect(action, playerId, weaponFilter, targetFilter, true));
                            }
                        }
                );
                actions.add(action);
            }

            GameTextActionId gameTextActionId = GameTextActionId.ARMED_AND_DANGEROUS__DOWNLOAD_WEAPON_FROM_RESERVE_DECK;

            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Deploy weapon from Reserve Deck");
                // Allow response(s)
                action.allowResponses("Deploy a matching character weapon from Reserve Deck",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DeployCardToTargetFromReserveDeckEffect(action, weaponFilter, targetFilter, true, true));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}