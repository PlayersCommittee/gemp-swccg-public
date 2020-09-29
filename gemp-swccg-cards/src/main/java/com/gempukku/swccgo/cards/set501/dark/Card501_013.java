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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.*;


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
            LinkedHashMap<PhysicalCard, List<PhysicalCard>> validPlaysFromHandOnly = getValidPlays(self, game, cardsInHand);
            if (!validPlaysFromHandOnly.isEmpty() || canDeployCardFromReserveDeck) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, downloadLightsaberActionId, CardSubtype.USED);
                action.setText("Deploy lightsaber from hand and/or reserve deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
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
                actions.add(action);
            }
        }
        return actions;
    }

    private void appendActionForHandOnly(SwccgGame game, PhysicalCard self, String playerId, final PlayInterruptAction action) {
        action.setActionMsg("Deploy a lightsaber from hand");
        final List<PhysicalCard> cardPool = game.getGameState().getHand(playerId);
        LinkedHashMap<PhysicalCard, List<PhysicalCard>> playsFromHand = getValidPlays(self, game, cardPool);
        action.appendEffect(new PlayoutDecisionEffect(action, playerId, getMultipleChoiceForValidPlays(playsFromHand)));
    }

    private void appendActionForReserveDeckAndHand(SwccgGame game, PhysicalCard self, String playerId, final PlayInterruptAction action) {
        action.setActionMsg("Deploy a lightsaber from hand and/or Reserve Deck");
        final List<PhysicalCard> cardPool = game.getGameState().getHand(playerId);
        cardPool.addAll(game.getGameState().getReserveDeck(playerId));
        LinkedHashMap<PhysicalCard, List<PhysicalCard>> playsFromHandAndReserve = getValidPlays(self, game, cardPool);
        action.appendEffect(new PlayoutDecisionEffect(action, playerId, getMultipleChoiceForValidPlays(playsFromHandAndReserve)));
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
                appendToValidPlays(validPlays, lightsaber, character);
            }
        }
        return validPlays;
    }

    private MultipleChoiceAwaitingDecision getMultipleChoiceForValidPlays(ArrayList<List<PhysicalCard>> validPlays) {
        // TODO create choices -- see Card1_195 Tonnika Sisters
        List<String> choicesText = new LinkedList<String>();
        String[] choices = new String[choicesText.size()];

        return new MultipleChoiceAwaitingDecision("Choose a lightsaber play combination", choices) {
            @Override
            protected void validDecisionMade(int index, String result) {
                // TODO
            }
        };
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