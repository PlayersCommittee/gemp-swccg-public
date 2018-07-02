package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
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

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: Heavy Fire Zone
 */
public class Card7_251 extends AbstractUsedInterrupt {
    public Card7_251() {
        super(Side.DARK, 5, "Heavy Fire Zone");
        setLore("'Heavy fire boss! Twenty-three degrees.'");
        setGameText("If a battle was just initiated, deploy (for free) a vehicle weapon or starship weapon from hand (or Reserve Deck; reshuffle) on your participating vehicle or starship.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final Filter weaponFilter = Filters.or(Filters.vehicle_weapon, Filters.starship_weapon);
        final Filter targetFilter = Filters.and(Filters.your(self), Filters.or(Filters.vehicle, Filters.starship), Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, targetFilter)) {

            if (GameConditions.hasInHand(game, playerId, Filters.and(weaponFilter, Filters.deployableToTarget(self, targetFilter, true, 0)))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Deploy weapon from hand");
                // Allow response(s)
                action.allowResponses("Deploy a vehicle weapon or starship weapon from hand",
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

            GameTextActionId gameTextActionId = GameTextActionId.RAPID_FIRE__DOWNLOAD_WEAPON_FROM_RESERVE_DECK;

            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Deploy weapon from Reserve Deck");
                // Allow response(s)
                action.allowResponses("Deploy a vehicle weapon or starship weapon from Reserve Deck",
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