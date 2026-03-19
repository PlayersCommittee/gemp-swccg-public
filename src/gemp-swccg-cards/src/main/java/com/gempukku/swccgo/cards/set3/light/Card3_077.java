package com.gempukku.swccgo.cards.set3.light;

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
import com.gempukku.swccgo.common.TargetingReason;
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Weapon
 * Subtype: Artillery
 * Title: Medium Repeating Blaster Cannon
 */
public class Card3_077 extends AbstractArtilleryWeapon {
    public Card3_077() {
        super(Side.LIGHT, 1, 3, 1, "Medium Repeating Blaster Cannon", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C1);
        setLore("Merr-Sonn Mark II repeating blaster. Accepts power cells from a very wide variety of sources, a benefit for Rebels accustomed to scavenging for supplies.");
        setGameText("Deploy on a site. May be moved by two warriors for 1 additional Force. Your warrior present may target up to two characters or two creatures at same or adjacent site using 2 Force. Draw two destiny. Target(s) hit if total destiny > total defense value.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.BLASTER, Keyword.CANNON, Keyword.ARTILLERY_WEAPON_MAY_USE_DB_TRANSIT);
    }

    @Override
    public String getTitleAbbreviated() {
        return "MR Blaster Cannon"; //prevents firing action text truncation
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
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
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

                                        /// TODO:
                                        /// Implement a new MoveArtilleryWeaponUsingLandspeedEffect / Action
                                        /// that will handle the simultaneous aspect described in AR (including cost).
                                        /// Should accept as parameters: artillery weapon card, collection of character cards to move, and additional cost
                                        /// This action would use: (self, collection: firstWarrior,secondWarrior, 1)
                                        /// The modified action Yutani provides, would use: (self, collection: Yutani, 0)
                                        /// can look at AddCardsToMoveUsingLandspeedSimultaneouslyEffect from Rebel Squad Leader for ideas

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
                actions.add(action);
            }
        }

        //Check condition(s)
        Filter potentialWarrior = Filters.and(potentialWarriors, Filters.canMoveMediumRepeatingBlasterCannonAloneForFree);
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.isPresentWith(game, self, 1, potentialWarrior)) {

            Collection<PhysicalCard> validDestinations = new HashSet<>();
            for (PhysicalCard potentialDestination : Filters.filterTopLocationsOnTable(game, Filters.relatedSite(self))) {
                for (PhysicalCard warrior : Filters.filterActive(game, self, Filters.and(potentialWarrior,Filters.canMoveUsingLandspeed(playerId, false, false, false, 0)))) {
                    if (Filters.canMoveToUsingLandspeed(playerId, warrior, false, false, false, 0, null).accepts(game, potentialDestination)) {
                        float moveCost = game.getModifiersQuerying().getMoveUsingLandspeedCost(game.getGameState(), warrior, fromSite, potentialDestination, false, 0);
                        if (GameConditions.canUseForce(game, playerId, moveCost)) {
                            validDestinations.add(potentialDestination);
                            break; //avoid adding more than once
                        }
                    }
                }
            }

            if (!validDestinations.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Move (for free) using one warrior");

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose site to move to", Filters.in(validDestinations)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard toSite) {
                        action.addAnimationGroup(toSite);
                        action.addAnimationGroup(self);

                        Collection<PhysicalCard> eligibleWarrior = new HashSet<>();
                        for (PhysicalCard warrior : Filters.filterActive(game, self, potentialWarrior)) {
                            if (Filters.canMoveToUsingLandspeed(playerId, warrior, false, false, false, 0, null).accepts(game, toSite)) {
                                float moveCost = game.getModifiersQuerying().getMoveUsingLandspeedCost(game.getGameState(), warrior, fromSite, toSite, false, 0);
                                if (GameConditions.canUseForce(game, playerId, moveCost)) eligibleWarrior.add(warrior);
                            }
                        }

                        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose warrior to carry", Filters.in(eligibleWarrior)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard selectedWarrior) {
                                action.addAnimationGroup(selectedWarrior);
                                final float moveCost = game.getModifiersQuerying().getMoveUsingLandspeedCost(game.getGameState(), selectedWarrior, fromSite, toSite, false, 0);

                                /// see comment block in 'normal' carry action above about using MoveArtilleryWeaponUsingLandspeedEffect / Action

                                /// until the above is implemented, section below accomplishes a very similar effect
                                action.appendCost(new PayMoveUsingLandspeedCostEffect(action, playerId, selectedWarrior, toSite, false, 0));

                                // Allow response(s)
                                action.allowResponses(
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new MoveUsingLandspeedEffect(action, selectedWarrior, fromSite, toSite, false, false));
                                                action.appendEffect(
                                                        new AttachCardFromTableEffect(action, self, toSite));
                                            }
                                        }
                                );
                            }
                        });
                    }
                });
                actions.add(action);
            }
        }

        return actions;
    }


    @Override
    public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        List<FireWeaponAction> actions = new LinkedList<FireWeaponAction>();

        //1 target
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameOrAdjacentSiteUsingForce(1, Filters.or(Filters.character, targetedAsCharacter, Filters.creature), 2, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            FireWeaponAction action = actionBuilder.buildFireWeaponByCharacterPresentWithHitAction(2);
            actions.add(action);
        }

        //2 targets
        FireWeaponActionBuilder actionBuilder2 = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetAtSameOrAdjacentSiteUsingForce(2, Filters.or(Filters.character, targetedAsCharacter, Filters.creature), 2, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder2 != null) {
            // Build action using common utility
            FireWeaponAction action2 = actionBuilder2.buildFireWeaponByCharacterPresentWithHitAction(2);
            actions.add(action2);
        }

        return actions;
    }

}

