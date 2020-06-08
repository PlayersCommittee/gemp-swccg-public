package com.gempukku.swccgo.cards.set206.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 6
 * Type: Character
 * Subtype: Rebel
 * Title: Bodhi Rook
 */
public class Card206_001 extends AbstractRebel {
    public Card206_001() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, Title.Bodhi, Uniqueness.UNIQUE);
        setLore("Gambler and spy.");
        setGameText("[Pilot] 2, 3: Rogue One or a stolen starship. May be deployed simultaneously with a piloted starship from hand to a system even without presence or Force icons. If about to be lost, your Rebel or Rebel starship at a related location may make a regular move.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_6);
        addKeywords(Keyword.GAMBLER, Keyword.SPY, Keyword.ROGUE_SQUADRON);
        setMatchingStarshipFilter(Filters.Rogue_One);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.or(Filters.Rogue_One, Filters.and(Filters.stolen, Filters.starship)))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && Filters.isUniquenessOnTableNotReached.accepts(game, self)) {
            final DeploymentRestrictionsOption evenWithPresenceOrForceIcons = DeploymentRestrictionsOption.evenWithoutPresenceOrForceIcons();
            Filter pilotedStarshipFilter = Filters.and(Filters.your(self), Filters.starship, Filters.piloted);
            List<PhysicalCard> starships = new ArrayList<PhysicalCard>();
            starships.addAll(Filters.filter(game.getGameState().getHand(playerId), game, pilotedStarshipFilter));
            starships.addAll(Filters.filter(game.getGameState().getAllStackedCards(), game, Filters.and(pilotedStarshipFilter, Filters.canDeployAsIfFromHand)));
            List<PhysicalCard> validStarships = new ArrayList<PhysicalCard>();
            for (PhysicalCard starship : starships) {
                if (Filters.deployableToLocationSimultaneouslyWith(self, self, false, 0, Filters.system, false, 0, null, evenWithPresenceOrForceIcons).accepts(game, starship)) {
                    validStarships.add(starship);
                }
            }
            if (!validStarships.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy simultaneously with piloted starship");
                action.setActionMsg(null);
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardFromHandEffect(action, playerId, Filters.in(validStarships), true) {
                            @Override
                            protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                                final PlayCardAction playCardAction = selectedCard.getBlueprint().getPlayCardAction(playerId, game, selectedCard, self, false, 0, null, evenWithPresenceOrForceIcons, null, null, self, false, 0, Filters.system, null);
                                if (playCardAction != null) {
                                    // Perform during targeting so it is aborted if the play card action is aborted
                                    action.setAllowAbort(true);
                                    action.appendTargeting(
                                            new StackActionEffect(action, playCardAction));
                                    // Perform result(s)
                                    action.appendEffect(
                                            new PassthruEffect(action) {
                                                @Override
                                                protected void doPlayEffect(SwccgGame game) {
                                                }
                                            }
                                    );
                                }
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, self)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, self)) {
            Filter filter = Filters.and(Filters.your(self), Filters.or(Filters.Rebel, Filters.Rebel_starship), Filters.at(Filters.relatedLocation(self)), Filters.movableAsRegularMove(playerId, false, 0, false, Filters.any));
            if (GameConditions.canSpot(game, self, filter)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Move a Rebel or Rebel starship");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose Rebel or Rebel starship to move", filter) {
                            @Override
                            protected void cardSelected(PhysicalCard cardToMove) {
                                action.addAnimationGroup(cardToMove);
                                action.setActionMsg("Have " + GameUtils.getCardLink(cardToMove) + " make a regular move");
                                // Perform result(s)
                                action.appendEffect(
                                        new MoveCardAsRegularMoveEffect(action, playerId, cardToMove, false, false, Filters.any));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
