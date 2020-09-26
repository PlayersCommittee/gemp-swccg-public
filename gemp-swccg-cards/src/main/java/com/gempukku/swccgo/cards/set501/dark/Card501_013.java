package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardCombinationFromHandAndOrReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: A Sith Legend
 */
public class Card501_013 extends AbstractUsedOrLostInterrupt {
    public Card501_013() {
        super(Side.DARK, 2, "A Sith Legend", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("");
        setGameText("USED: Deploy a lightsaber (may simultaneously deploy a matching Dark Jedi or Sith character) from hand and/or Reserve Deck; reshuffle. [Immune to Sense.] LOST: Except during battle, relocate a Dark Jedi or Inquisitor to same battleground site as a Jedi.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_13);
        setTestingText("A Sith Legend");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId exchangeCardActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        Filter jediAtBattlegroundSites = Filters.and(Filters.Jedi, Filters.at(Filters.battleground_site));
        final Filter battlegroundSitesJediAreAt = Filters.sameSiteAs(self, jediAtBattlegroundSites);
        Filter darkJediOrInquisitor = Filters.or(Filters.Dark_Jedi, Filters.inquisitor);
        Filter darkJediOrInquisitorWhoCanBeRelocated = Filters.and(darkJediOrInquisitor, Filters.canBeRelocatedToLocation(battlegroundSitesJediAreAt, 0));

        if (!GameConditions.isDuringBattle(game)
            && GameConditions.canSpot(game, self, darkJediOrInquisitorWhoCanBeRelocated)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, exchangeCardActionId, CardSubtype.LOST);
            action.setText("Relocate Dark Jedi or Inquisitor");
            action.setActionMsg("Relocate a Dark Jedi or Inquisitor to same battleground site as a Jedi.");

            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Dark Jedi or Inquisitor", darkJediOrInquisitorWhoCanBeRelocated) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard characterTargeted) {
                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, self.getOwner(), "Choose site to relocate character", battlegroundSitesJediAreAt) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard siteSelected) {
                                            action.addAnimationGroup(characterTargeted);
                                            action.addAnimationGroup(siteSelected);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new PayRelocateBetweenLocationsCostEffect(action, playerId, characterTargeted, siteSelected, 0));
                                            // Allow response(s)
                                            action.allowResponses("Relocate " + GameUtils.getCardLink(characterTargeted) + " to " + GameUtils.getCardLink(siteSelected),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new RelocateBetweenLocationsEffect(action, characterTargeted, siteSelected));
                                                        }
                                                    });
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId downloadLightsaberActionId = GameTextActionId.A_SITH_FURY__DOWNLOAD_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {

            boolean canDeployCardFromReserveDeck = GameConditions.canDeployCardFromReserveDeck(game, playerId, self, downloadLightsaberActionId);
            final List<PhysicalCard> cardsInHand = game.getGameState().getHand(playerId);
            final List<PhysicalCard> validLightsabersInHand = new ArrayList<PhysicalCard>();
            Collection<PhysicalCard> lightsabersInHand = Filters.filter(cardsInHand, game, Filters.lightsaber);

//            // TODO -- not sure if characters/lightsabers are simultaneously deployable with each other
//            for (PhysicalCard lightsaber : lightsabersInHand) {
//                if (Filters.canSpot(cardsInHand, game, Filters.and(Filters.matchingCharacter(lightsaber), Filters.deployableSimultaneouslyWith(self, lightsaber, false, 0, false, 0)))
//                    || (Filters.canSpot(game, self, Filters.matchingCharacter(lightsaber)))) {
//                    validLightsabersInHand.add(lightsaber);
//                }
//            }

            if (!validLightsabersInHand.isEmpty() || canDeployCardFromReserveDeck) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, downloadLightsaberActionId, CardSubtype.USED);
                action.setText("Deploy lightsaber");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                if (!validLightsabersInHand.isEmpty() && canDeployCardFromReserveDeck) {
                    action.appendTargeting(getPlayoutDecisionEffect(self, action, playerId, validLightsabersInHand, cardsInHand));
                } else {
                    action.setActionMsg("Deploy a unique [Independent] starfighter and matching pilot from hand and/or Reserve Deck");
                    // Perform result(s)
                    action.appendEffect(getChooseCardsEffect(action));
                }
                actions.add(action);
            }
        }
        return actions;
    }

    private PlayoutDecisionEffect getPlayoutDecisionEffect(final PhysicalCard self, final PlayInterruptAction action, final String playerId, final List<PhysicalCard> validLightsabersInHand, final List<PhysicalCard> cardsInHand) {
        return new PlayoutDecisionEffect(action, playerId,
                new YesNoDecision("You have valid lightsaber and Dark Jedi/Sith combinations in hand or on table. Do you want to search Reserve Deck as well?") {
                    @Override
                    protected void yes() {
                        action.setActionMsg("Deploy a lightsaber from hand and/or Reserve Deck");
                        // Perform result(s)
                        // TODO HERE
                        action.appendEffect(getChooseCardsEffect(action));
                    }

                    @Override
                    protected void no() {
                        action.setActionMsg("Deploy a unique [Independent] starfighter and matching pilot from hand");
                        action.appendTargeting(
                                new ChooseCardFromHandEffect(action, playerId, Filters.in(validLightsabersInHand)) {
                                    @Override
                                    public String getChoiceText(int numCardsToChoose) {
                                        return "Choose unique [Independent] starfighter";
                                    }

                                    @Override
                                    protected void cardSelected(SwccgGame game, final PhysicalCard starfighter) {
                                        Collection<PhysicalCard> pilots = Filters.filter(cardsInHand, game, Filters.and(Filters.matchingPilot(starfighter),
                                                Filters.deployableSimultaneouslyWith(self, starfighter, false, -1, false, -1)));
                                        action.appendTargeting(
                                                new ChooseCardFromHandEffect(action, playerId, Filters.in(pilots)) {
                                                    @Override
                                                    public String getChoiceText(int numCardsToChoose) {
                                                        return "Choose matching pilot";
                                                    }

                                                    @Override
                                                    protected void cardSelected(SwccgGame game, PhysicalCard pilot) {
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new DeployCardsSimultaneouslyEffect(action, starfighter, false, -1, pilot, false, -1));
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
    }

    private StandardEffect getChooseCardsEffect(final PlayInterruptAction action) {

        /*
        TODO
        -- lightsabers that can deploy on their own
        -- lightsabers that can deploy only if you deploy a matching character with them
        -- lightsabers that cannot deploy
        */

        return new ChooseCardCombinationFromHandAndOrReserveDeckEffect(action) {
            @Override
            public String getChoiceText(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                return "Choose a lightsaber to deploy. May also choose a matching Dark Jedi or Sith character to deploy simultaneously.";
            }

            @Override
            public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                String playerId = action.getPerformingPlayer();
                GameState gameState = game.getGameState();
                Collection<PhysicalCard> cardsToChooseFrom = new LinkedList<PhysicalCard>(gameState.getHand(playerId));
                cardsToChooseFrom.addAll(gameState.getCardPile(playerId, Zone.RESERVE_DECK));

                if (cardsSelected.isEmpty()) {
                    final List<PhysicalCard> validStarfighters = new ArrayList<PhysicalCard>();
                    Collection<PhysicalCard> starfighters = Filters.filter(cardsToChooseFrom, game, Filters.and(Filters.starfighter, Icon.INDEPENDENT));
                    for (PhysicalCard starfighter : starfighters) {
                        if (Filters.canSpot(cardsToChooseFrom, game, Filters.and(Filters.matchingPilot(starfighter), Filters.deployableSimultaneouslyWith(action.getActionSource(), starfighter, false, -1, false, -1)))) {
                            validStarfighters.add(starfighter);
                        }
                    }
                    return Filters.in(validStarfighters);
                } else if (cardsSelected.size() == 1) {
                    PhysicalCard starfighter = cardsSelected.iterator().next();
                    return Filters.and(Filters.matchingPilot(starfighter), Filters.deployableSimultaneouslyWith(action.getActionSource(), starfighter, false, -1, false, -1));
                }
                return Filters.none;
            }

            @Override
            public boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                return (cardsSelected.size() > 0 && cardsSelected.size() <= 2);
            }

            @Override
            protected void cardsChosen(List<PhysicalCard> cardsChosen) {
                PhysicalCard lightsaber = cardsChosen.get(0);
                PhysicalCard character = null;
                if (cardsChosen.size() == 2) {
                    character = cardsChosen.get(1);
                }
                // Perform result(s)
                action.appendEffect(
                        new DeployCardsSimultaneouslyEffect(action, lightsaber, false, -1, character, false, -1));
            }
        };
    }
}