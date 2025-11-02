package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.UtinniEffectStatus;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionOfExactlyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Subtype: Utinni
 * Title: Tusken Breath Mask
 */
public class Card1_067 extends AbstractUtinniEffect {
    public Card1_067() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Tusken_Breath_Mask, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("Sand People use a special mask to reclaim exhaled moisture in the hot, harsh environment of Tatooine. Protects by filtering blowing sand and dispersing excess heat.");
        setGameText("Deploy on any Tatooine site where you have just won a battle. Target one of your characters not at Tatooine. Upon reaching, target takes mask. While on Tatooine, target's power and forfeit are +2 and has immunity to attrition of exactly 3.");
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return false;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.wonBattleAt(game, effectResult, playerId, Filters.Tatooine_site)) {

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.battleLocation, null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        Filter filter = Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.at(Title.Tatooine)));
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.TUSKEN_BREATH_MASK__MODIFIED_BY_SERGEANT_DOALLYN)) {
            filter = Filters.or(filter, Filters.and(Filters.your(self), Filters.character, Filters.on(Title.Tatooine)));
        }
        return filter;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (!GameConditions.isUtinniEffectReached(game, self)
                && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Relocate to " + GameUtils.getCardLink(target));
            action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(target));
            // Update usage limit(s)
            action.appendUsage(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
                        }
                    }
            );
            // Perform result(s)
            action.appendEffect(
                    new AttachCardFromTableEffect(action, self, target));
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, 2, Filters.and(Filters.Tusken_Breath_Mask, Filters.unique))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);

            Collection<PhysicalCard> tuskenBreathMasks = Filters.filterActive(game, self, Filters.Tusken_Breath_Mask);
            PhysicalCard firstTuskenBreathMasks = Filters.findFirstActive(game, self, Filters.Tusken_Breath_Mask);
            tuskenBreathMasks.remove(firstTuskenBreathMasks);
            action.appendEffect(
                    new PlaceCardsInUsedPileFromTableEffect(action, self.getOwner(), tuskenBreathMasks)
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter targetAttachedToOnTatooine = Filters.and(Filters.targetedByCardOnTableAsTargetId(self, TargetId.UTINNI_EFFECT_TARGET_1), Filters.hasAttached(self), Filters.on(Title.Tatooine));
        Condition hasExtraModifiers = new GameTextModificationCondition(self, ModifyGameTextType.TUSKEN_BREATH_MASK__MODIFIED_BY_SERGEANT_DOALLYN);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, targetAttachedToOnTatooine, new ConditionEvaluator(2, 4, hasExtraModifiers)));
        modifiers.add(new ForfeitModifier(self, targetAttachedToOnTatooine, new ConditionEvaluator(2, 4, hasExtraModifiers)));
        modifiers.add(new ImmuneToAttritionOfExactlyModifier(self, targetAttachedToOnTatooine, 3));
        modifiers.add(new ImmuneToTitleModifier(self, targetAttachedToOnTatooine, hasExtraModifiers, Title.Gravel_Storm));
        modifiers.add(new ImmuneToTitleModifier(self, targetAttachedToOnTatooine, hasExtraModifiers, Title.Sandwhirl));
        return modifiers;
    }
}