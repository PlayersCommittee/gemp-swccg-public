package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandOrReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Lost
 * Title: I Know
 */
public class Card8_056 extends AbstractLostInterrupt {
    public Card8_056() {
        super(Side.LIGHT, 5, Title.I_Know, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Han and Leia chose unusual times to express their true feelings for each other.");
        setGameText("If Leia and Han are in a battle together, add two battle destiny. OR If opponent just initiated a battle against Han, you may 'react' by deploying there (for free) Leia and/or her matching weapon from hand and/or Reserve Deck; reshuffle.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Han)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Leia)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add two battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.I_KNOW__DOWNLOAD_LEIA_AND_OR_MATCHING_WEAPON_AS_REACT;

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Han))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, true)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy Leia and/or matching weapon as 'react'");
            // Allow response(s)
            action.allowResponses("Deploy Leia and/or her matching weapon as a 'react' from hand and/or Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            final ReactActionOption reactActionOption = new ReactActionOption(self, true, 0, false, null, null, Filters.any, null, false);
                            PhysicalCard leiaInBattle = Filters.findFirstActive(game, self, Filters.and(Filters.Leia, Filters.participatingInBattle));
                            Filter firstCardFilter;
                            if (leiaInBattle != null) {
                                firstCardFilter = Filters.and(Filters.matchingWeaponForCharacter(leiaInBattle), Filters.deployableToTarget(self, Filters.sameCardId(leiaInBattle), false, true, 0, null, null, null, null, reactActionOption));
                            }
                            else {
                                firstCardFilter = Filters.and(Filters.Leia, Filters.deployableToLocation(self, Filters.battleLocation, false, true, 0, null, null, reactActionOption));
                            }
                            // Perform result(s)
                            action.appendEffect(
                                    new ChooseCardFromHandOrReserveDeckEffect(action, playerId, firstCardFilter, true, false) {
                                        @Override
                                        protected void cardSelected(SwccgGame game, PhysicalCard firstCardToDeploy) {
                                            if (GameUtils.getZoneFromZoneTop(firstCardToDeploy.getZone()) == Zone.RESERVE_DECK) {
                                                action.appendEffect(
                                                        new DeployCardToLocationFromReserveDeckEffect(action, firstCardToDeploy, Filters.battleLocation, true, true, true));
                                            }
                                            else {
                                                action.appendEffect(
                                                        new DeployCardToLocationFromHandEffect(action, firstCardToDeploy, Filters.battleLocation, true, true));
                                            }
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            // If Leia is in battle, and we know her matching weapon is available to be deployed from hand or Reserve Deck, deploy it.
                                                            final PhysicalCard leiaInBattle = Filters.findFirstActive(game, self, Filters.and(Filters.Leia, Filters.participatingInBattle));
                                                            if (leiaInBattle != null) {
                                                                Filter secondCardFilter = Filters.and(Filters.matchingWeaponForCharacter(leiaInBattle), Filters.deployableToTarget(self, Filters.sameCardId(leiaInBattle), false, true, 0, null, null, null, null, reactActionOption));
                                                                if (GameConditions.hasInHandOrDeployableAsIfFromHand(game, playerId, secondCardFilter) || GameConditions.hasInReserveDeck(game, playerId, secondCardFilter)) {
                                                                    action.appendEffect(
                                                                            new ChooseCardFromHandOrReserveDeckEffect(action, playerId, secondCardFilter, true, true) {
                                                                                @Override
                                                                                protected void cardSelected(SwccgGame game, PhysicalCard secondCardToDeploy) {
                                                                                    if (GameUtils.getZoneFromZoneTop(secondCardToDeploy.getZone()) == Zone.RESERVE_DECK) {
                                                                                        action.appendEffect(
                                                                                                new DeployCardToTargetFromReserveDeckEffect(action, secondCardToDeploy, Filters.sameCardId(leiaInBattle), true, true, true));
                                                                                    }
                                                                                    else {
                                                                                        action.appendEffect(
                                                                                                new DeployCardToTargetFromHandEffect(action, secondCardToDeploy, Filters.sameCardId(leiaInBattle), true, true));
                                                                                    }
                                                                                }
                                                                            }
                                                                    );
                                                                }
                                                            }
                                                        }
                                                    }
                                            );
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