package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DuringAttackWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerAttackEffect;
import com.gempukku.swccgo.cards.evaluators.ForceIconsAtLocationEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.InitiateAttackNonCreatureAction;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfAttackModifierEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NumDestinyDrawsDuringAttackModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AttackTargetSelectedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Alien
 * Title: Rabin
 */
public class Card8_025 extends AbstractAlien {
    public Card8_025() {
        super(Side.LIGHT, 3, 2, 0, 1, 1, "Rabin", Uniqueness.UNIQUE);
        setLore("Ewok. Tamer of beasts. Thief. Loner. Hunter. Survivor.");
        setGameText("Deploys only on Endor. Power and forfeit +1 for each Light side icon at same Endor site. Where present, may substitute for a character just selected to be attacked by a creature. When attacking or being attacked, power +2 and may add one destiny.");
        addIcons(Icon.ENDOR);
        addKeywords(Keyword.THIEF);
        setSpecies(Species.EWOK);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Endor;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atEndorSite = new AtCondition(self, Filters.Endor_site);
        Evaluator lightSideIconsAtLocation = new ForceIconsAtLocationEvaluator(self, false, true);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, atEndorSite, lightSideIconsAtLocation));
        modifiers.add(new ForfeitModifier(self, atEndorSite, lightSideIconsAtLocation));
        modifiers.add(new PowerModifier(self, new DuringAttackWithParticipantCondition(self), 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        //Where present, may substitute for a character just selected to be attacked by a creature.
        if (effectResult.getType() == EffectResult.Type.ATTACK_TARGET_SELECTED) {
            AttackTargetSelectedResult targetSelectedResult = ((AttackTargetSelectedResult)effectResult);
            final InitiateAttackNonCreatureAction creatureAction = targetSelectedResult.getInitiateAttackNonCreatureAction();
            PhysicalCard creature = targetSelectedResult.getCreature();
            PhysicalCard currentTarget = targetSelectedResult.getTarget();

            if (creatureAction != null
                && creatureAction.isTargetChosen()
                && !creatureAction.isTargetChanged()
                && !Filters.and(self).accepts(game, currentTarget)
                && Filters.nonCreatureCanBeAttackedByCreature(creature, false).accepts(game, self)) {

                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Substitute for creature attack");
                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        creatureAction.setTarget(self);
                    }
                });
                return Collections.singletonList(action);
            }
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isDuringAttackWithParticipant(game, self)
            && GameConditions.isOncePerAttack(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one destiny");
            action.appendUsage(
                    new OncePerAttackEffect(action));
            action.appendEffect(
                    new AddUntilEndOfAttackModifierEffect(action, new NumDestinyDrawsDuringAttackModifier(self, 1, playerId), null));

            return Collections.singletonList(action);
        }

        return null;
    }
}
