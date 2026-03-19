package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractArtilleryWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayMoveUsingLandspeedCostEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.MoveUsingLandspeedEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Set: Hoth
 * Type: Weapon
 * Subtype: Artillery
 * Title: E-web Blaster
 */
public class Card3_159 extends AbstractArtilleryWeapon {
    public Card3_159() {
        super(Side.DARK, 5, 3, 1, Title.E_web_Blaster, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C1);
        setLore("Massive infantry weapon powerful enough to damage even starfighters.");
        setGameText("Deploy on any site. May be moved with two warriors for 1 additional Force. Your warrior present may target a starfighter (use 5 as defense value), character, creature or vehicle using 2 Force. Draw destiny. Target hit if destiny +1 > defense value.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.BLASTER, Keyword.ARTILLERY_WEAPON_MAY_USE_DB_TRANSIT);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.warrior, Filters.present(self));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter potentialWarriors = Filters.and(Filters.your(self), Filters.presentWith(self), Filters.warrior, Filters.hasNotPerformedRegularMove, Filters.canMoveUsingLandspeed(playerId, false, false, false, 0), Filters.notProhibitedFromCarrying(self));
        final PhysicalCard fromSite = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.isPresentWith(game, self, 2, potentialWarriors)) {

            Collection<PhysicalCard> validDestinations = new HashSet<>();

            for (PhysicalCard potentialDestination : Filters.filterTopLocationsOnTable(game, Filters.relatedSite(self))) { // landspeed implies site must be related, so this reduces sites to check
                //scan warriors (tracking the cheapest pair) and add valid site as soon as cost is payable
                float cost1 = 99;
                float cost2 = 99;
                float moveCost;
                for (PhysicalCard warrior : Filters.filterActive(game, self, potentialWarriors)) {
                    if (Filters.canMoveToUsingLandspeed(playerId, warrior, false, false, false, 0, null).accepts(game, potentialDestination)) {
                        moveCost = game.getModifiersQuerying().getMoveUsingLandspeedCost(game.getGameState(), warrior, fromSite, potentialDestination, false, 0);
                        if (cost1 > cost2) {
                            cost1 = Math.min(cost1, moveCost);
                        } else {
                            cost2 = Math.min(cost2, moveCost);
                        }
                        if (GameConditions.canUseForce(game, playerId, cost1 + cost2 + 1)) {
                            validDestinations.add(potentialDestination);
                            break; //avoid adding more than once
                        }
                    }
                }
            }

            if (!validDestinations.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Move using two warriors");

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose site to move to", Filters.in(validDestinations)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard toSite) {
                        action.addAnimationGroup(toSite);
                        action.addAnimationGroup(self);

                        Collection<PhysicalCard> eligibleFirstWarriors = new HashSet<>();

                        //add first warrior only if they can reach the destination and at least one other warrior can reach the destination (and can pay combined cost)
                        for (PhysicalCard warrior : Filters.filterActive(game, self, potentialWarriors)) {
                            if (Filters.canMoveToUsingLandspeed(playerId, warrior, false, false, false, 0, null).accepts(game, toSite)) {
                                float cost1 = game.getModifiersQuerying().getMoveUsingLandspeedCost(game.getGameState(), warrior, fromSite, toSite, false, 0);

                                for (PhysicalCard warrior2 : Filters.filterActive(game, self, Filters.and(Filters.not(warrior), potentialWarriors))) {

                                    if (Filters.canMoveToUsingLandspeed(playerId, warrior2, false, false, false, 0, null).accepts(game, toSite)) {
                                        float cost2 = game.getModifiersQuerying().getMoveUsingLandspeedCost(game.getGameState(), warrior2, fromSite, toSite, false, 0);
                                        if (GameConditions.canUseForce(game, playerId, cost1 + cost2 + 1)) {
                                            eligibleFirstWarriors.add(warrior);
                                            break; //avoid adding more than once
                                        }
                                    }
                                }
                            }
                        }

                        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose first warrior to carry", Filters.in(eligibleFirstWarriors)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard firstWarrior) {
                                action.addAnimationGroup(firstWarrior);
                                final float cost1 = game.getModifiersQuerying().getMoveUsingLandspeedCost(game.getGameState(), firstWarrior, fromSite, toSite, false, 0);
                                Collection<PhysicalCard> eligibleSecondWarriors = new HashSet<>();

                                //add second warrior only if they can reach the destination (and can pay combined cost)
                                for (PhysicalCard warrior : Filters.filterActive(game, self, Filters.and(Filters.not(firstWarrior), potentialWarriors))) {
                                    if (Filters.canMoveToUsingLandspeed(playerId, warrior, false, false, false, 0, null).accepts(game, toSite)) {
                                        if (GameConditions.canUseForce(game, playerId, cost1 + game.getModifiersQuerying().getMoveUsingLandspeedCost(game.getGameState(), warrior, fromSite, toSite, false, 0) + 1)) {
                                            eligibleSecondWarriors.add(warrior);
                                        }
                                    }
                                }

                                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose second warrior to carry", Filters.in(eligibleSecondWarriors)) {
                                    @Override
                                    protected void cardTargeted(final int targetGroupId, PhysicalCard secondWarrior) {
                                        action.addAnimationGroup(secondWarrior);

                                        /// see comment block Medium Repeating Blaster Cannon 'normal' carry action above about using MoveArtilleryWeaponUsingLandspeedEffect / Action

                                        /// until the above is implemented, section below accomplishes a very similar effect

                                        // Pay cost(s)
                                        action.appendCost(new PayMoveUsingLandspeedCostEffect(action, playerId, firstWarrior, toSite, false, 0));
                                        action.appendCost(new PayMoveUsingLandspeedCostEffect(action, playerId, secondWarrior, toSite, false, 0));
                                        action.appendCost(new UseForceEffect(action, playerId, 1));

                                        // Allow response(s)
                                        action.allowResponses(
                                                new UnrespondableEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new MoveUsingLandspeedEffect(action, firstWarrior, fromSite, toSite, false, false));
                                                        action.appendEffect(
                                                                new MoveUsingLandspeedEffect(action, secondWarrior, fromSite, toSite, false, false));
                                                        action.appendEffect(
                                                                new AttachCardFromTableEffect(action, self, toSite));
                                                    }
                                                }
                                        );
                                    }
                                });

                            }
                        });
                    }
                });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.starfighter, Filters.character, targetedAsCharacter, Filters.creature, Filters.vehicle), 2, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponByCharacterPresentWithHitAction(1, 1, Statistic.DEFENSE_VALUE, Filters.starfighter, 5f);
            return Collections.singletonList(action);
        }
        return null;
    }

}

