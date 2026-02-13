package com.gempukku.swccgo.cards.set207.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 7
 * Type: Character
 * Subtype: Alien
 * Title: Sidon Ithano
 */
public class Card207_024 extends AbstractAlien {
    public Card207_024() {
        super(Side.DARK, 4, 3, 3, 3, 5, Title.Sidon, Uniqueness.UNIQUE, ExpansionSet.SET_7, Rarity.V);
        setLore("Delphidian pirate. Leader.");
        setGameText("[Pilot] 2, 3: Meson Martinet. Once per turn, may place opponent's character just lost from here out of play, unless opponent loses 1 Force (2 if your Rep is a pirate or you did not deploy an Objective).");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_VII, Icon.VIRTUAL_SET_7);
        addKeywords(Keyword.PIRATE, Keyword.LEADER);
        setSpecies(Species.DELPHIDIAN);
        setMatchingStarshipFilter(Filters.Meson_Martinet);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Meson_Martinet)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.here(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            final GameState gameState = game.getGameState();
            final PhysicalCard lostCard = ((LostFromTableResult) effectResult).getCard();
            final int numForceToLose = (GameConditions.hasRep(game, playerId, Filters.pirate) || GameConditions.didNotDeployAnObjective(game, playerId)) ? 2 : 1;

            if(Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY).accepts(game, lostCard)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place " + GameUtils.getFullName(lostCard) + " out of play");
                action.setActionMsg("Place " + GameUtils.getCardLink(lostCard) + " out of play");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PlayoutDecisionEffect(action, opponent,
                                new YesNoDecision("Do you want to lose " + numForceToLose + " Force instead of having " + GameUtils.getCardLink(lostCard) + " placed out of play?") {
                                    @Override
                                    protected void yes() {
                                        gameState.sendMessage(opponent + " chooses to lose " + numForceToLose + " Force instead of having " + GameUtils.getCardLink(lostCard) + " placed out of play");
                                        action.appendEffect(
                                                new LoseForceEffect(action, opponent, numForceToLose, true));
                                    }
                                    protected void no() {
                                        gameState.sendMessage(opponent + " chooses to not lose " + numForceToLose + " Force instead of having " + GameUtils.getCardLink(lostCard) + " placed out of play");
                                        action.appendEffect(
                                                new PlaceCardOutOfPlayFromOffTableEffect(action, lostCard));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
