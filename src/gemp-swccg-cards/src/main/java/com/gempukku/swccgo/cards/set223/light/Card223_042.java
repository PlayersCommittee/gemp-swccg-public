package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DeployCardsSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardCombinationFromHandAndOrReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Effect
 * Title: Passage To The Alderaan System
 */
public class Card223_042 extends AbstractNormalEffect {
    public Card223_042() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Passage To The Alderaan System", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Han's smuggling adventures in Corporate Sector and Hutt Space put him in many tight scrapes.  He's about to be in another.");
        setGameText("If Stolen Data Tapes on table, deploy on table. Twice per game, may [upload] a general or a senator. Once per game, may simultaneously deploy [A New Hope] Falcon (deploy -1) and [A New Hope] Han from hand and/or Reserve Deck; reshuffle. Falcon is immune to Gravity Shadow. [Immune to Alter.]");
        addIcon(Icon.VIRTUAL_SET_23);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.and(Filters.your(self), Filters.Stolen_Data_Tapes));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId1 = GameTextActionId.PASSAGE_TO_THE_ALDERAAN_SYSTEM__DEPLOY_FALCON_AND_HAN;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId1)
                && GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            boolean canDeployCardFromReserveDeck = GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId1, Persona.FALCON);
            final List<PhysicalCard> cardsInHand = game.getGameState().getHand(playerId);
            final List<PhysicalCard> validStarfighters = new ArrayList<PhysicalCard>();
            Collection<PhysicalCard> starfighters = Filters.filter(cardsInHand, game, Filters.and(Filters.icon(Icon.A_NEW_HOPE), Filters.Falcon));
            for (PhysicalCard starfighter : starfighters) {
                if (Filters.canSpot(cardsInHand, game, Filters.and(Filters.icon(Icon.A_NEW_HOPE), Filters.Han, Filters.deployableSimultaneouslyWith(self, starfighter, false, -1, false, 0)))) {
                    validStarfighters.add(starfighter);
                }
            }

            if (!validStarfighters.isEmpty() || canDeployCardFromReserveDeck) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId1);
                action.setText("Deploy a starfighter and pilot");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                if (!validStarfighters.isEmpty() && canDeployCardFromReserveDeck) {
                    action.appendTargeting(
                            new PlayoutDecisionEffect(action, playerId,
                                    new YesNoDecision("You have valid starfighter and pilot combinations in hand. Do you want to search Reserve Deck as well?") {
                                        @Override
                                        protected void yes() {
                                            action.setActionMsg("Deploy a [ANH] Falcon and [ANH] Han from hand and/or Reserve Deck");
                                            // Perform result(s)
                                            action.appendEffect(getChooseCardsEffect(action));
                                        }
                                        @Override
                                        protected void no() {
                                            action.setActionMsg("Deploy a [ANH] Falcon and [ANH] Han from hand");
                                            action.appendTargeting(
                                                    new ChooseCardFromHandEffect(action, playerId, Filters.in(validStarfighters)) {
                                                        @Override
                                                        public String getChoiceText(int numCardsToChoose) {
                                                            return "Choose a [ANH] Falcon";
                                                        }
                                                        @Override
                                                        protected void cardSelected(SwccgGame game, final PhysicalCard starfighter) {
                                                            Collection<PhysicalCard> pilots = Filters.filter(cardsInHand, game, Filters.and(Filters.icon(Icon.A_NEW_HOPE),
                                                                    Filters.Han, Filters.deployableSimultaneouslyWith(self, starfighter, false, -1, false, -1)));
                                                            action.appendTargeting(
                                                                    new ChooseCardFromHandEffect(action, playerId, Filters.in(pilots)) {
                                                                        @Override
                                                                        public String getChoiceText(int numCardsToChoose) {
                                                                            return "Choose a [ANH] Han";
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
                            )
                    );
                }
                else {
                    action.setActionMsg("Deploy a [ANH] Falcon and Han from hand and/or Reserve Deck");
                    // Perform result(s)
                    action.appendEffect(getChooseCardsEffect(action));
                }
                actions.add(action);
            }
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.PASSAGE_TO_THE_ALDERAAN_SYSTEM__UPLOAD_GENERAL_OR_SENATOR;
        // Check condition(s)
        if (GameConditions.isTwicePerGame(game, self, gameTextActionId2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId2)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId2);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take any general or senator into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new TwicePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.general, Filters.senator), true));
            actions.add(action);
        }
        return actions;
    }

    private StandardEffect getChooseCardsEffect(final TopLevelGameTextAction action) {
        return new ChooseCardCombinationFromHandAndOrReserveDeckEffect(action) {
            @Override
            public String getChoiceText(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                return "Choose a [ANH] Falcon and Han from hand and/or Reserve Deck";
            }

            @Override
            public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                String playerId = action.getPerformingPlayer();
                GameState gameState = game.getGameState();
                Collection<PhysicalCard> cardsToChooseFrom = new LinkedList<PhysicalCard>(gameState.getHand(playerId));
                cardsToChooseFrom.addAll(gameState.getCardPile(playerId, Zone.RESERVE_DECK));

                if (cardsSelected.isEmpty()) {
                    final List<PhysicalCard> validStarfighters = new ArrayList<PhysicalCard>();
                    Collection<PhysicalCard> starfighters = Filters.filter(cardsToChooseFrom, game, Filters.and(Filters.icon(Icon.A_NEW_HOPE), Filters.Falcon));
                    for (PhysicalCard starfighter : starfighters) {
                        if (Filters.canSpot(cardsToChooseFrom, game, Filters.and(Icon.A_NEW_HOPE, Filters.Han, Filters.deployableSimultaneouslyWith(action.getActionSource(), starfighter, false, -1, false, 0)))) {
                            validStarfighters.add(starfighter);
                        }
                    }
                    return Filters.in(validStarfighters);
                } else if (cardsSelected.size() == 1) {
                    PhysicalCard starfighter = cardsSelected.iterator().next();
                    return Filters.and(Filters.Han, Filters.deployableSimultaneouslyWith(action.getActionSource(), starfighter, false, -1, false, 0));
                }
                return Filters.none;
            }

            @Override
            public boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                return (cardsSelected.size() == 2);
            }

            @Override
            protected void cardsChosen(List<PhysicalCard> cardsChosen) {
                PhysicalCard starfighter = cardsChosen.get(0);
                PhysicalCard pilot = cardsChosen.get(1);

                // Perform result(s)
                action.appendEffect(
                        new DeployCardsSimultaneouslyEffect(action, starfighter, false, -1, pilot, false, 0));
            }
        };
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Falcon, Title.Gravity_Shadow));
        return modifiers;
    }
}
