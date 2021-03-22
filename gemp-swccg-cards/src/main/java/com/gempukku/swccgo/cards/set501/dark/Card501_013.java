package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.*;


/**
 * Set: Set 15
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: A Sith Legend
 */
public class Card501_013 extends AbstractUsedOrLostInterrupt {
    public Card501_013() {
        super(Side.DARK, 2, "A Sith Legend", Uniqueness.UNIQUE);
        setLore("");
        setGameText("USED: Deploy a lightsaber (may simultaneously deploy a matching Dark Jedi or Sith character) from hand and/or Reserve Deck; reshuffle. [Immune to Sense.] LOST: Except during battle, relocate a Dark Jedi or Inquisitor to same battleground site as a Jedi.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_15);
        setTestingText("A Sith Legend");
        hideFromDeckBuilder();
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
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
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard characterTargeted) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, self.getOwner(), "Choose site to relocate character", battlegroundSitesJediAreAt) {
                                        @Override
                                        protected void cardTargeted(final int siteTargetingGroupId, final PhysicalCard siteSelected) {
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
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            Collection<PhysicalCard> finalCharacter = action.getPrimaryTargetCards(targetGroupId);
                                                            PhysicalCard finalToSite = action.getPrimaryTargetCard(siteTargetingGroupId);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new RelocateBetweenLocationsEffect(action, finalCharacter, finalToSite));
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

        GameTextActionId downloadLightsaberActionId = GameTextActionId.A_SITH_LEGEND__DOWNLOAD_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            final boolean canDeployCardFromReserveDeck = GameConditions.canDeployCardFromReserveDeck(game, playerId, self, downloadLightsaberActionId);
            final List<PhysicalCard> cardsInHand = game.getGameState().getHand(playerId);
            final LinkedHashMap<PhysicalCard, List<PhysicalCard>> validPlaysFromHandOnly = getValidPlays(self, game, cardsInHand);
            if (!validPlaysFromHandOnly.isEmpty() || canDeployCardFromReserveDeck) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, downloadLightsaberActionId, CardSubtype.USED);
                action.setText("Deploy a lightsaber");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                if (!validPlaysFromHandOnly.isEmpty() && canDeployCardFromReserveDeck) {
                                    // DS gets a choice for if they want to search reserve deck or not
                                    action.appendTargeting(getPlayoutDecisionEffect(game, self, action, playerId, cardsInHand));
                                } else if (validPlaysFromHandOnly.isEmpty()) {
                                    // Must search reserve deck
                                    appendActionForReserveDeckAndHand(game, self, playerId, action);
                                } else {
                                    // Play must come from hand
                                    appendActionForHandOnly(game, self, playerId, action);
                                }
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    private void appendActionForHandOnly(SwccgGame game, PhysicalCard self, String playerId, final PlayInterruptAction action) {
        action.setActionMsg("Deploy a lightsaber from hand");
        final List<PhysicalCard> cardPool = game.getGameState().getHand(playerId);
        LinkedHashMap<PhysicalCard, List<PhysicalCard>> playsFromHand = getValidPlays(self, game, cardPool);
        action.appendEffect(new PlayoutDecisionEffect(action, playerId, getMultipleChoiceForLightsaberPlay(playerId, playsFromHand, action, false)));
    }

    private void appendActionForReserveDeckAndHand(SwccgGame game, PhysicalCard self, String playerId, final PlayInterruptAction action) {
        action.appendEffect(
                new LookAtReserveDeckEffect(action, playerId, playerId));
        action.setActionMsg("Deploy a lightsaber from hand and/or Reserve Deck");
        List<PhysicalCard> cardPool = new ArrayList<>(game.getGameState().getHand(playerId));
        cardPool.addAll(game.getGameState().getReserveDeck(playerId));
        LinkedHashMap<PhysicalCard, List<PhysicalCard>> playsFromHandAndReserve = getValidPlays(self, game, cardPool);
        if (!playsFromHandAndReserve.isEmpty()) {
            action.appendEffect(new PlayoutDecisionEffect(action, playerId, getMultipleChoiceForLightsaberPlay(playerId, playsFromHandAndReserve, action, true)));
        } else {
            // This deploy fails and opponent verifies, if there are no valid play combinations
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.lightsaber, true));
        }
    }

    private LinkedHashMap<PhysicalCard, List<PhysicalCard>> appendToValidPlays(LinkedHashMap<PhysicalCard, List<PhysicalCard>> validPlays, PhysicalCard lightsaber, PhysicalCard character) {
        List<PhysicalCard> charactersCanDeployTo;
        if (validPlays.get(lightsaber) == null) {
            charactersCanDeployTo = new ArrayList<>();
        } else {
            charactersCanDeployTo = validPlays.get(lightsaber);
        }
        charactersCanDeployTo.add(character);
        validPlays.put(lightsaber, charactersCanDeployTo);
        return validPlays;
    }

    private boolean isNoPersonaConflictBetweenChosenCards(SwccgGame game, PhysicalCard lightsaber, PhysicalCard character) {
        Set<Persona> lightsaberPersonas = game.getModifiersQuerying().getPersonas(game.getGameState(), lightsaber);
        Set<Persona> characterPersonas = game.getModifiersQuerying().getPersonas(game.getGameState(), character);
        if (Collections.disjoint(lightsaberPersonas, characterPersonas) == false) {
            return false;
        }
        SwccgBuiltInCardBlueprint permanentWeapon = character.getBlueprint().getPermanentWeapon(character);
        if (permanentWeapon != null) {
            Set<Persona> permanentWeaponPersonas = permanentWeapon.getPersonas(game);
            if (Collections.disjoint(lightsaberPersonas, permanentWeaponPersonas) == false) {
                return false;
            }
        }
        return true;
    }

    private LinkedHashMap<PhysicalCard, List<PhysicalCard>> getValidPlays(PhysicalCard self, SwccgGame game, List<PhysicalCard> cardPool) {
        LinkedHashMap<PhysicalCard, List<PhysicalCard>> validPlays = new LinkedHashMap<>();
        Collection<PhysicalCard> lightsabersInPool = Filters.filter(cardPool, game, Filters.lightsaber);
        Collection<PhysicalCard> deployableLightsabers = Filters.filter(cardPool, game, Filters.and(Filters.lightsaber, Filters.deployable(self, null, false, 0)));
        // 1) Get all standalone lightsaber plays
        for (PhysicalCard lightsaber : deployableLightsabers) {
            // Adding a null character to signify standalone
            appendToValidPlays(validPlays, lightsaber, null);
        }
        // 2) Get all lightsaber pairs deployable with Dark Jedi/Inquisitor
        for (PhysicalCard lightsaber : lightsabersInPool) {
            // Get all DJ/Sith this lightsaber is a matching weapon for, and also can deploy
            Filter validCharacters = Filters.and(Filters.or(Filters.Dark_Jedi, Filters.Sith), Filters.matchingCharacter(lightsaber), Filters.deployable(self, null, false, 0));
            for (PhysicalCard character : Filters.filter(cardPool, game, validCharacters)) {
                if (cardPool.contains(character) && isNoPersonaConflictBetweenChosenCards(game, lightsaber, character)) {
                    appendToValidPlays(validPlays, lightsaber, character);
                }
            }
        }
        return validPlays;
    }

    private MultipleChoiceAwaitingDecision getMultipleChoiceForLightsaberPlay(final String playerId, final LinkedHashMap<PhysicalCard, List<PhysicalCard>> validPlays, final PlayInterruptAction action, final boolean reserveDeckSearched) {
        final List<String> choicesText = new LinkedList<>();
        final List<PhysicalCard> lightsaberList = new ArrayList<>();
        for (PhysicalCard lightsaber : validPlays.keySet()) {
            choicesText.add(lightsaber.getTitle() + " from " + lightsaber.getZone().getHumanReadable());
            lightsaberList.add(lightsaber);
        }
        final String[] lightsaberChoices = new String[choicesText.size()];
        for (int i = 0; i < choicesText.size(); i++) {
            lightsaberChoices[i] = choicesText.get(i);
        }
        return new MultipleChoiceAwaitingDecision("Choose a lightsaber to deploy", lightsaberChoices) {
            @Override
            protected void validDecisionMade(int index, String result) {
                PhysicalCard lightsaberChosen = lightsaberList.get(index);
                List<PhysicalCard> characterList = validPlays.get(lightsaberChosen);
                action.appendEffect(new PlayoutDecisionEffect(action, playerId, getMultipleChoiceForCharacterPlay(action, playerId, lightsaberChosen, characterList, reserveDeckSearched)));
            }
        };
    }

    private MultipleChoiceAwaitingDecision getMultipleChoiceForCharacterPlay(final PlayInterruptAction action, final String playerId, final PhysicalCard lightsaber, final List<PhysicalCard> characterList, final boolean reserveDeckSearched) {
        final List<String> choicesText = new LinkedList<>();
        for (PhysicalCard character : characterList) {
            if (character == null) {
                choicesText.add("None");
            } else {
                choicesText.add(character.getTitle() + " from " + character.getZone().getHumanReadable());
            }
        }
        final String[] characterChoices = new String[choicesText.size()];
        for (int i = 0; i < choicesText.size(); i++) {
            characterChoices[i] = choicesText.get(i);
        }

        return new MultipleChoiceAwaitingDecision("Choose a character to deploy with lightsaber", characterChoices) {
            @Override
            protected void validDecisionMade(int index, String result) {
                if (result == "None") {
                    appendDeployEffect(lightsaber, playerId, action);
                    appendReshuffleEffect(playerId, lightsaber.getZone(), Zone.AT_LOCATION, action, reserveDeckSearched);
                } else {
                    PhysicalCard characterToDeploy = characterList.get(index);
                    appendDeployEffect(characterToDeploy, playerId, action);
                    appendDeployEffect(lightsaber, playerId, action);
                    appendReshuffleEffect(playerId, lightsaber.getZone(), characterToDeploy.getZone(), action, reserveDeckSearched);
                }
            }
        };
    }

    private void appendDeployEffect(PhysicalCard cardToDeploy, String playerId, PlayInterruptAction action) {
        if (cardToDeploy.getZone() == Zone.RESERVE_DECK) {
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.sameCardId(cardToDeploy), true));
        } else if (cardToDeploy.getZone() == Zone.HAND) {
            action.appendEffect(new DeployCardFromHandEffect(action, playerId, Filters.sameCardId(cardToDeploy), false));
        }
    }

    private void appendReshuffleEffect(String playerId, Zone lightsaberZone, Zone characterZone, PlayInterruptAction action, final boolean reserveDeckSearched) {
        // Cases where reserve deck was viewed but not otherwise reshuffled.
        if (lightsaberZone != Zone.RESERVE_DECK && characterZone != Zone.RESERVE_DECK && reserveDeckSearched == true) {
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, playerId));
        }
    }

    private PlayoutDecisionEffect getPlayoutDecisionEffect(final SwccgGame game, final PhysicalCard self, final PlayInterruptAction action, final String playerId, final List<PhysicalCard> cardsInHand) {
        return new PlayoutDecisionEffect(action, playerId,
                new YesNoDecision("You have valid lightsaber and Dark Jedi/Sith combinations in hand or on table. Do you want to search Reserve Deck as well?") {
                    @Override
                    protected void yes() {
                        appendActionForReserveDeckAndHand(game, self, playerId, action);
                    }

                    @Override
                    protected void no() {
                        appendActionForHandOnly(game, self, playerId, action);
                    }
                }
        );
    }
}