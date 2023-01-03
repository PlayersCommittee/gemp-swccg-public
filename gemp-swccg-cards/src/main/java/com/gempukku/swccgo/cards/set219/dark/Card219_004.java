package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Alien
 * Title: Elan Sleazebaggano
 */
public class Card219_004 extends AbstractAlien {
    public Card219_004() {
        super(Side.DARK, 5, 2, 2, 1, 3, "Elan Sleazebaggano", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setLore("Balosar information broker.");
        setGameText("Power and forfeit +2 at a Coruscant location. At the beginning of opponent's control phase, " +
                    "if present with opponent’s character, ‘sell death sticks’ (opponent must use or lose 1 Force; " +
                    "if a Jedi present, also place Elan in Used Pile).");
        addKeywords(Keyword.INFORMATION_BROKER);
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_19);
        setSpecies(Species.BALOSAR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        AtCondition atCoruscantLocationCondition = new AtCondition(self, Filters.Coruscant_location);
        modifiers.add(new PowerModifier(self, atCoruscantLocationCondition, 2));
        modifiers.add(new ForfeitModifier(self, atCoruscantLocationCondition, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        final String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isPresentWith(game, self, Filters.and(Filters.opponents(self), Filters.character))
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Sell death sticks");
            action.appendUsage(
                    new OncePerPhaseEffect(action));

            final String USE_FORCE = "Use 1 Force";
            final String LOSE_FORCE = "Lose 1 Force";
            List<String> optionsTextList = new ArrayList<>();
            if (GameConditions.canUseForce(game, opponent, 1)) {
                optionsTextList.add(USE_FORCE);
            }
            optionsTextList.add(LOSE_FORCE);
            String[] optionTextArray = new String[optionsTextList.size()];
            optionsTextList.toArray(optionTextArray);
            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, opponent,
                            new MultipleChoiceAwaitingDecision("Buy death sticks (use or lose 1 Force)", optionTextArray) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (result.equals(USE_FORCE)) {
                                        game.getGameState().sendMessage(opponent + " chooses to use 1 Force");
                                        action.appendEffect(
                                                new UseForceEffect(action, opponent, 1));
                                    } else if (result.equals(LOSE_FORCE)) {
                                        game.getGameState().sendMessage(opponent + " chooses to lose 1 Force");
                                        action.appendEffect(
                                                new LoseForceEffect(action, opponent, 1, true));
                                    }
                                    if(GameConditions.isPresentWith(game, self, Filters.Jedi)){
                                        action.appendEffect(
                                                new PlaceCardInUsedPileFromTableEffect(action, self)
                                        );
                                    }
                                }
                            }
                    )
            );
            actions.add(action);

        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        // Check if reached end of each control phase and action was not performed yet.
        if (GameConditions.isPresentWith(game, self, Filters.and(Filters.opponents(self), Filters.character))
                && TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Sell death sticks");

            final String USE_FORCE = "Use 1 Force";
            final String LOSE_FORCE = "Lose 1 Force";
            List<String> optionsTextList = new ArrayList<>();
            if (GameConditions.canUseForce(game, opponent, 1)) {
                optionsTextList.add(USE_FORCE);
            }
            optionsTextList.add(LOSE_FORCE);
            String[] optionTextArray = new String[optionsTextList.size()];
            optionsTextList.toArray(optionTextArray);
            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, opponent,
                            new MultipleChoiceAwaitingDecision("Buy death sticks (use or lose 1 Force)", optionTextArray) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (result.equals(USE_FORCE)) {
                                        game.getGameState().sendMessage(opponent + " chooses to use 1 Force");
                                        action.appendEffect(
                                                new UseForceEffect(action, opponent, 1));
                                    } else if (result.equals(LOSE_FORCE)) {
                                        game.getGameState().sendMessage(opponent + " chooses to lose 1 Force");
                                        action.appendEffect(
                                                new LoseForceEffect(action, opponent, 1, true));
                                    }
                                    if(GameConditions.isPresentWith(game, self, Filters.Jedi)){
                                        action.appendEffect(
                                                new PlaceCardInUsedPileFromTableEffect(action, self)
                                        );
                                    }
                                }
                            }
                    )
            );
            actions.add(action);

        }

        return actions;
    }
}