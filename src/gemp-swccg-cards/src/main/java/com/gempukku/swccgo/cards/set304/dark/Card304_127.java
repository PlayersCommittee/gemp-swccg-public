package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.DuringEpicDuelWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.LoseForceAndStackFaceDownEffect;
import com.gempukku.swccgo.logic.modifiers.CrossOverAttemptTotalModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Effect
 * Title: Competitive Advantage
 */
public class Card304_127 extends AbstractNormalEffect {
    public Card304_127() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Competitive_Advantage, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("The Empire knows when dealing with non-Imperial factions they need to always hold the competitive advantage. Whether it's ethical or not.");
        setGameText("Use 2 Force to deploy on table. Each time you win a battle, opponent loses 1 Force (cannot be reduced) and stacks lost card here face down. When attempting to cross Locita over (except with Epic Duel), add 3 to total destiny for each card in stack. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.wonBattle(game, effectResult, playerId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force and stack here");
            action.setActionMsg("Make opponent lose 1 Force and stack lost card face down on " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceAndStackFaceDownEffect(action, opponent, 1, self) {
                        @Override
                        public boolean isShownIfLostFromHand() {
                            return true;
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition targetKaiInsteadOfLocita = new GameTextModificationCondition(self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
        Condition targetHikaruInsteadOfLocita = new GameTextModificationCondition(self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);
        Condition targetLuke = new AndCondition(new NotCondition(targetKaiInsteadOfLocita), new NotCondition(targetHikaruInsteadOfLocita));

        modifiers.add(new CrossOverAttemptTotalModifier(self, Filters.Kai, new AndCondition(targetKaiInsteadOfLocita, new NotCondition(new DuringEpicDuelWithParticipantCondition(Filters.Kai))), new MultiplyEvaluator(3, new StackedEvaluator(self))));
        modifiers.add(new CrossOverAttemptTotalModifier(self, Filters.Locita, new AndCondition(targetLuke, new NotCondition(new DuringEpicDuelWithParticipantCondition(Filters.Locita))), new MultiplyEvaluator(3, new StackedEvaluator(self))));
        modifiers.add(new CrossOverAttemptTotalModifier(self, Filters.Hikaru, new AndCondition(targetHikaruInsteadOfLocita, new NotCondition(new DuringEpicDuelWithParticipantCondition(Filters.Hikaru))), new MultiplyEvaluator(3, new StackedEvaluator(self))));
        return modifiers;
    }
}