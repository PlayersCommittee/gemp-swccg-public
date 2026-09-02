package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.actions.PlayCardAsAttachedAction;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.BattleEffect;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.PayInitiateBattleCostEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Set: A New Hope
 * Type: Effect
 * Title: Besieged
 */
public class Card2_117 extends AbstractNormalEffect {
    public Card2_117() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Besieged, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.R2);
        setLore("Stormtroopers blasted through the main airlock of the Tantive IV. The Rebel soldiers' attempt to defend the intrusion was no match for the Empire's superior firepower.");
        setGameText("Deploy on a captured starship. Your characters present with captured starship may battle opponent's characters aboard it (as if present together at a site). Effect canceled if starship escapes or is stolen.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    public Map<InactiveReason, Boolean> getDeployTargetSpotOverride(PlayCardOptionId playCardOptionId) {
        return SpotOverride.INCLUDE_CAPTIVE;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.captured_starship;
    }

    @Override
    protected Filter getValidDeployTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, PlayCardOption playCardOption, boolean forFree, float changeInCost, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons) {
        // Captured starships are inactive; skip canBeTargetedBy so Besieged can deploy on them.
        return Filters.captured_starship;
    }


    @Override
    public List<PlayCardAction> getPlayCardActions(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions) {
        List<PlayCardAction> actions = super.getPlayCardActions(playerId, game, self, sourceCard, forFree, changeInCost, deploymentOption, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deployTargetFilter, specialLocationConditions);
        Filter captured = Filters.and(Filters.captured_starship, deployTargetFilter != null ? deployTargetFilter : Filters.any);
        if ((actions == null || actions.isEmpty())
                && !Filters.filterAllOnTable(game, captured).isEmpty()) {
            List<PlayCardOption> options = getPlayCardOptions(playerId, game);
            if (options != null && !options.isEmpty()) {
                if (actions == null) {
                    actions = new ArrayList<PlayCardAction>();
                }
                actions.add(new PlayCardAsAttachedAction(sourceCard, self, options.get(0), true, changeInCost, reactActionOption, SpotOverride.INCLUDE_CAPTIVE, captured));
            }
        }
        return actions;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        // Launch leaves Besieged on the starship even though it is no longer captured
        return Filters.starship;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsWhenInactiveInPlay(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return getGameTextTopLevelActions(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return getGameTextRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final PhysicalCard capturedStarship = self.getAttachedTo();
        if (capturedStarship == null || !capturedStarship.isCapturedStarship()) {
            return null;
        }

        final PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), capturedStarship);
        if (location == null) {
            return null;
        }

        Filter dsCharacterFilter = Filters.and(Filters.your(self), Filters.character,
                Filters.or(Filters.at(location), Filters.aboard(Filters.relatedStarshipOrVehicle(location))));
        Filter trappedCharacterFilter = Filters.and(Filters.opponents(self), Filters.character,
                Filters.aboardExceptRelatedSites(capturedStarship));
        // Droids have ability 0 and do not provide presence (forum: cannot Besiege with only a droid aboard).
        Filter trappedPresenceFilter = Filters.and(trappedCharacterFilter, Filters.not(Filters.droid), Filters.abilityMoreThan(0));

        if (GameConditions.isDuringYourPhase(game, playerId, Phase.BATTLE)
                && !GameConditions.isDuringBattle(game)
                && !game.getModifiersQuerying().mayNotInitiateBattleAtLocation(game.getGameState(), location, playerId)
                && !game.getModifiersQuerying().isBattleOccurredAtLocationThisTurn(location)
                && Filters.canSpot(game, null, dsCharacterFilter)
                && Filters.canSpot(game, null, SpotOverride.INCLUDE_CAPTIVE, trappedPresenceFilter)
                && GameConditions.canInitiateBattleAtLocation(playerId, game, location, false, false, true)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Initiate Besieged battle");
            action.setActionMsg("Initiate a Besieged battle against characters aboard " + GameUtils.getCardLink(capturedStarship));
            action.appendTargeting(
                    new TargetCardsOnTableEffect(action, playerId, "Choose characters to participate in Besieged battle", 1, Integer.MAX_VALUE, dsCharacterFilter) {
                        @Override
                        protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> targetedCards) {
                            action.addAnimationGroup(targetedCards);
                            action.appendCost(
                                    new PayInitiateBattleCostEffect(action, location, playerId, false));
                            action.allowResponses("Initiate Besieged battle",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            Collection<PhysicalCard> dsCharacters = action.getPrimaryTargetCards(targetGroupId);
                                            Collection<PhysicalCard> trappedCharacters = Filters.filterActive(game, self, SpotOverride.INCLUDE_CAPTIVE, trappedCharacterFilter);
                                            if (dsCharacters == null || dsCharacters.isEmpty() || trappedCharacters.isEmpty()) {
                                                return;
                                            }

                                            Collection<PhysicalCard> participants = new LinkedList<PhysicalCard>();
                                            participants.addAll(dsCharacters);
                                            participants.addAll(trappedCharacters);
                                            Collection<PhysicalCard> attachedSupport = Filters.filterAllOnTable(game,
                                                    Filters.and(Filters.or(Filters.weapon, Filters.device), Filters.attachedTo(Filters.in(participants))));
                                            participants.addAll(attachedSupport);

                                            action.appendEffect(
                                                    new BattleEffect(action, location, false, null, true, participants, Collections.emptyList()));
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

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard attachedTo = self.getAttachedTo();
        if (attachedTo == null) {
            return null;
        }

        boolean stolen = TriggerConditions.isAboutToBeStolen(game, effectResult, Filters.sameCardId(attachedTo))
                || TriggerConditions.justStolen(game, effectResult, Filters.sameCardId(attachedTo));
        boolean escaped = TriggerConditions.isAboutToLeaveTable(game, effectResult, Filters.sameCardId(attachedTo));
        if ((stolen || escaped) && GameConditions.canBeCanceled(game, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Besieged");
            action.setActionMsg("Cancel Besieged because the starship escaped or was stolen");
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
