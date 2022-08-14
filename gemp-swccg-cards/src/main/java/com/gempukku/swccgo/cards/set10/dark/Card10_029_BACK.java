package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Objective
 * Title: Agents of Black Sun / Vengeance of the Dark Prince
 */
public class Card10_029_BACK extends AbstractObjective {
    public Card10_029_BACK() {
        super(Side.DARK, 7, Title.Vengeance_Of_The_Dark_Prince);
        setGameText("While this side up, once per turn, may place a card from hand in Used Pile to peek at cards in your Force Pile. Once during each of your battle phases, may peek at top X cards of any Reserve Deck, where X = number of locations you occupy. For each Black Sun Agent in battle, attrition against opponent is +1. During your control phase, opponent loses 1 Force for each battleground location occupied by Xizor or Emperor. Flip this card if Luke is at a battleground site or if Xizor not on table.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)) {
            Collection<PhysicalCard> bountyHunters = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.bounty_hunter, Filters.at(Filters.site)));
            if (!bountyHunters.isEmpty()) {
                List<PhysicalCard> validBountyHunter = new ArrayList<PhysicalCard>();
                for (PhysicalCard bountyHunter : bountyHunters) {
                    if (Filters.movableAsRegularMove(playerId, false, 0, false, Filters.and(Filters.adjacentSite(bountyHunter), Filters.sameSiteAs(self, Filters.any_bounty))).accepts(game, bountyHunter)) {
                        validBountyHunter.add(bountyHunter);
                    }
                }
                if (!validBountyHunter.isEmpty()) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Move bounty hunter to a bounty");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose bounty hunter to move", Filters.in(validBountyHunter)) {
                                @Override
                                protected void cardSelected(PhysicalCard bountyHunter) {
                                    action.addAnimationGroup(bountyHunter);
                                    action.setActionMsg("Move " + GameUtils.getCardLink(bountyHunter) + " to an adjacent site where there is a bounty");
                                    // Perform result(s)
                                    action.appendEffect(
                                            new MoveCardAsRegularMoveEffect(action, playerId, bountyHunter, false, false, Filters.and(Filters.adjacentSite(bountyHunter), Filters.sameSiteAs(self, Filters.any_bounty))));
                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasForcePile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PutCardFromHandOnUsedPileEffect(action, playerId));
            // Perform result(s)
            action.appendEffect(
                    new LookAtForcePileEffect(action, playerId, playerId));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.BATTLE)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent))) {
            final int numCards = Filters.countTopLocationsOnTable(game, Filters.occupies(playerId));
            if (numCards > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Peek at top " + numCards + " cards of any Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                            @Override
                            protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                                action.setActionMsg("Peek at top " + numCards + " cards of " + cardPileOwner + "'s Reserve Deck");
                                // Perform result(s)
                                action.appendEffect(
                                        new PeekAtTopCardsOfReserveDeckEffect(action, playerId, cardPileOwner, numCards));
                            }
                        }
                );
                actions.add(action);
            }
        }

        Filter xizorFilter = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.LEGACY__TREAT_XIZOR_AS_SHADA) ? Filters.title("Shada") : Filters.Xizor;
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_4;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int amountToLose = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupiesWith(playerId, self, Filters.or(xizorFilter, Filters.Emperor))));
            if (amountToLose > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make " + opponent + " lose " + amountToLose + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, amountToLose));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter yourBlackSunAgents = Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.alien, Filters.loreContains("Black Sun")), Filters.bounty_hunter, Filters.information_broker));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, yourBlackSunAgents, Keyword.BLACK_SUN_AGENT));
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.hasAbilityOrHasPermanentPilotWithAbility, Filters.not(Filters.or(Filters.Black_Sun_agent, yourBlackSunAgents, Filters.Emperor, Filters.and(Icon.INDEPENDENT, Filters.starship)))), playerId));
        modifiers.add(new MayNotPlayModifier(self, Filters.Scanning_Crew));
        modifiers.add(new AttritionModifier(self, new InBattleEvaluator(self, Filters.Black_Sun_agent), opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        Filter xizorFilter = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.LEGACY__TREAT_XIZOR_AS_SHADA) ? Filters.title("Shada") : Filters.Xizor;

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_4;

        // Check condition(s)
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int amountToLose = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupiesWith(playerId, self, Filters.or(xizorFilter, Filters.Emperor))));
            if (amountToLose > 0) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make " + opponent + " lose " + amountToLose + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, amountToLose));
                actions.add(action);
            }
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)) {
            Filter lukeFilter = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.REFLECTIONS_II_OBJECTIVE__TARGETS_REY_INSTEAD_OF_LUKE) ? Filters.Rey : Filters.Luke;
            if (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(lukeFilter, Filters.at(Filters.battleground_site)))
                    || !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, xizorFilter)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                actions.add(action);
            }
        }
        return actions;
    }
}