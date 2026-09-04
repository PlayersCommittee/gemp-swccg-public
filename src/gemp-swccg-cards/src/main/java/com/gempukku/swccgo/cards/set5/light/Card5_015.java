package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseInsertCardEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Access Denied
 */
public class Card5_015 extends AbstractNormalEffect {
    public Card5_015() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Access_Denied, Uniqueness.UNRESTRICTED, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("The doors on Cloud City use a special computer controlled locking system, making them difficult to bypass without altering the security codes.");
        setGameText("Insert face up in your Reserve Deck. When Effect reaches top it is lost, along with all opponent's 'insert' cards there. Reshuffle. (Immune to Alter.) OR Deploy between two mobile sites. Opponent's characters may pass only if aboard a Lift Tube or opponent uses +1 Force each.");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        // Alter immunity is insert-only (see getGameTextAlwaysOnModifiers). Between-sites is cancelable.
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_AS_INSERT_CARD, PlayCardZoneOption.YOUR_RESERVE_DECK, "Insert face up in your Reserve Deck"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.ATTACHED, "Deploy between two mobile sites"));
        return playCardOptions;
    }

    /** Mobile sites that are not Dagobah/Ahch-To (shared Deploy Between Sites rule). */
    private static Filter eligibleMobileSite() {
        return Filters.and(
                Filters.mobile_site,
                Filters.not(Filters.or(Filters.Dagobah_location, Filters.AhchTo_location)));
    }

    @Override
    protected Filter getValidDeployTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption) {
        return Filters.location;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (playCardOptionId != PlayCardOptionId.PLAY_CARD_OPTION_1) {
            return Filters.none;
        }
        final Filter eligible = eligibleMobileSite();
        return Filters.and(eligible, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.canSpot(game, self, Filters.and(Filters.adjacentSite(physicalCard), eligible));
            }
        });
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return eligibleMobileSite();
    }

    @Override
    protected List<TargetingEffect> getGameTextTargetCardsWhenDeployedEffects(final Action action, String playerId, SwccgGame game, final PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        if (playCardOption == null || playCardOption.getId() != PlayCardOptionId.PLAY_CARD_OPTION_1) {
            return null;
        }
        final Filter targetFilter = Filters.and(eligibleMobileSite(), Filters.adjacentSite(target), Filters.not(target));
        TargetingEffect targetingEffect = new TargetCardOnTableEffect(action, playerId, "Choose adjacent mobile site", targetFilter) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard chosen) {
                action.addAnimationGroup(chosen);
                self.setTargetedCard(TargetId.EFFECT_TARGET_1, targetGroupId, chosen, targetFilter);
            }
        };
        return Collections.singletonList(targetingEffect);
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextInsertCardRevealed(SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Reveal 'insert' card");
        action.setActionMsg(null);
        // Lose this insert card
        action.appendEffect(new LoseInsertCardEffect(action, self));
        // Lose all opponent's insert cards still in this Reserve Deck, then reshuffle
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        List<PhysicalCard> toLose = new LinkedList<PhysicalCard>();
                        for (PhysicalCard card : gameState.getReserveDeck(playerId, false)) {
                            if (card.isInserted() && opponent.equals(card.getOwner())) {
                                toLose.add(card);
                            }
                        }
                        for (PhysicalCard card : toLose) {
                            action.appendEffect(new LoseInsertCardEffect(action, card));
                        }
                        action.appendEffect(new ShuffleReserveDeckEffect(action, playerId, playerId));
                    }
                }
        );
        return action;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition deployedBetweenSites = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1);
        Filter siteA = Filters.hasAttached(self);
        Filter siteB = Filters.targetedByCardOnTableAsTargetId(self, TargetId.EFFECT_TARGET_1);
        // Opponent's characters not aboard a Lift Tube pay +1 Force to pass between the bounding sites.
        Filter opponentCharactersNotAboardLiftTube = Filters.and(
                Filters.opponents(self),
                Filters.character,
                Filters.not(Filters.or(Filters.aboard(Filters.Lift_Tube), Filters.aboardAsPassenger(Filters.Lift_Tube)))
        );

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostFromLocationToLocationModifier(self, opponentCharactersNotAboardLiftTube, deployedBetweenSites, 1, siteA, siteB));
        modifiers.add(new MoveCostFromLocationToLocationModifier(self, opponentCharactersNotAboardLiftTube, deployedBetweenSites, 1, siteB, siteA));
        return modifiers;
    }

}
