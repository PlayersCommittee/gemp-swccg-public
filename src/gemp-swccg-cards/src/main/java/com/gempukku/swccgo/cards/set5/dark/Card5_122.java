package com.gempukku.swccgo.cards.set5.dark;

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
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
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
 * Title: Restricted Access
 */
public class Card5_122 extends AbstractNormalEffect {
    public Card5_122() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Restricted_Access, Uniqueness.UNRESTRICTED, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("In an effort to direct Luke toward Vader, Captain Bewil used his control of hatchways and lift tubes to cut off Luke's support, limiting his options and resources.");
        setGameText("Insert face up in your Reserve Deck. When Effect reaches top it is lost, along with all opponent's 'insert' cards there. Reshuffle. (Immune to Alter.) OR Deploy between two mobile sites. Opponent's characters may pass only if aboard a Lift Tube or opponent uses +1 Force each.");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        // Bill ruling: insert mode Immune to Alter; between-sites Effect NOT Immune (see AlwaysOn).
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
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // Insert function only — same pattern as Projection Of A Skywalker dual-option Alter immunity.
        modifiers.add(new ImmuneToTitleModifier(self, new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_AS_INSERT_CARD), Title.Alter));
        return modifiers;
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
