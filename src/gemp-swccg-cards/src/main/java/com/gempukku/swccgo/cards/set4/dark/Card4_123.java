package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: He Is Not Ready
 */
public class Card4_123 extends AbstractNormalEffect {
    public Card4_123() {
        super(Side.DARK, 4, null, "He Is Not Ready", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("'All his live has he looked away. To the future, to the horizon. Never his mind on where he was! Hmm? What he was doing! Hm.'");
        setGameText("Deploy on any character. Subtracts 2 from that character's training destiny draws. OR Deploy on an opponent's pilot at a site. During each of opponent's move phases, opponent must choose to move that character, lose that character or lose 1 Force.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.ATTACHED, "Deploy on a character"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.ATTACHED, "Deploy on an opponent's pilot"));
        return playCardOptions;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_1)
            return Filters.character;
        else if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_2)
            return Filters.and(Filters.opponents(self), Filters.pilot, Filters.at(Filters.site));
        else
            return Filters.none;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition playCardOptionId1 = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachTrainingDestinyModifier(self, Filters.jediTestTargetingApprentice(Filters.hasAttached(self)), playCardOptionId1, -2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());
        final PhysicalCard character = self.getAttachedTo();

        // Check condition(s)
        if (GameConditions.isPlayCardOption(game, self, PlayCardOptionId.PLAY_CARD_OPTION_2)) {
            if (TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.MOVE)) {
                self.setWhileInPlayData(null);
            }
            else if (TriggerConditions.moved(game, effectResult, character)) {
                self.setWhileInPlayData(new WhileInPlayData());
            }
            else if (TriggerConditions.isEndOfOpponentsPhase(game, self, effectResult, Phase.MOVE)
                    && !GameConditions.cardHasWhileInPlayDataSet(self)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + opponent + " make choice");
                // Perform result(s)
                action.appendEffect(
                        new PlayoutDecisionEffect(action, opponent,
                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Lose " + GameUtils.getCardLink(character), "Lose 1 Force"}) {
                                    @Override
                                    protected void validDecisionMade(int index, String result) {
                                        if (index == 0) {
                                            game.getGameState().sendMessage(opponent + " chooses to lose " + GameUtils.getCardLink(character));
                                            action.appendEffect(
                                                    new LoseCardFromTableEffect(action, character));
                                        }
                                        else {
                                            game.getGameState().sendMessage(opponent + " chooses to lose 1 Force");
                                            action.appendEffect(
                                                    new LoseForceEffect(action, opponent, 1, true));
                                        }
                                    }
                                }
                        )
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}