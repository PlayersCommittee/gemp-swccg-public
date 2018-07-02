package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Alien
 * Title: Aurra Sing (AI)
 */
public class Card11_052 extends AbstractAlien {
    public Card11_052() {
        super(Side.DARK, 1, 4, 4, 4, 3, Title.Aurra_Sing, Uniqueness.UNIQUE);
        setAlternateImageSuffix(true);
        setLore("Bounty hunter. Former student of the Force. After failing her Jedi training, Aurra became known for hunting down and killing Jedi Knights.");
        setGameText("May use any 'stolen' lightsaber. Once per turn, may steal a lightsaber from an opponent's character just lost where present. Immune to attrition < 3 (< 5 while armed with a lightsaber).");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayUseWeaponModifier(self, Filters.and(Filters.stolen, Filters.lightsaber)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 5, new ArmedWithCondition(self, Filters.lightsaber))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.wherePresent(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            Collection<PhysicalCard> lightsabers = Filters.filter(justLostCard.getCardsPreviouslyAttached(), game, self,
                    TargetingReason.TO_BE_STOLEN, Filters.and(Filters.lightsaber, Filters.inLostPile(game.getOpponent(playerId))));
            if (!lightsabers.isEmpty()) {
                List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
                for (PhysicalCard cardToSteal : lightsabers) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Steal " + GameUtils.getFullName(cardToSteal) + " from " + GameUtils.getFullName(justLostCard));
                    action.setActionMsg("Steal " + GameUtils.getCardLink(cardToSteal) + " from " + GameUtils.getCardLink(justLostCard));
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerTurnEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new StealCardAndAttachFromLostPileEffect(action, playerId, self, Filters.sameCardId(cardToSteal)));
                    actions.add(action);
                }
                return actions;
            }
        }
        return null;
    }
}
