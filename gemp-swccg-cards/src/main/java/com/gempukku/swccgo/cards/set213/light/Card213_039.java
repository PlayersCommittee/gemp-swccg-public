package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageAndAttritionEffect;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * SubType: Alien
 * Title: Qi'ra
 */
public class Card213_039 extends AbstractAlien {
    public Card213_039() {
        super(Side.LIGHT, 2, 3, 3, 4, 3, "Qi'ra", Uniqueness.UNIQUE);
        setLore("Female thief. Corellian smuggler.");
        setGameText(" When forfeited at same location as Han or Vos, may satisfy all remaining battle damage against you. If you just initiated a battle or Force drain at same battleground and you have completed a Kessel Run, opponent loses 1 Force. Immune to attrition < 3.");
        addPersona(Persona.QIRA);
        setSpecies(Species.CORELLIAN);
        addKeywords(Keyword.FEMALE, Keyword.THIEF, Keyword.SMUGGLER);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        setArmor(5);
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.hasCompletedUtinniEffect(game, self.getOwner(), Filters.Kessel_Run)
                && (TriggerConditions.battleInitiatedAt(game, effectResult, self.getOwner(), Filters.and(Filters.sameLocation(self), Filters.battleground)) ||
                TriggerConditions.forceDrainInitiatedBy(game, effectResult, self.getOwner(), Filters.and(Filters.sameLocation(self), Filters.battleground)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            action.setActionMsg("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(self.getOwner()), 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.canForfeitToSatisfyAttritionAndBattleDamage(game, playerId, self)
                && GameConditions.isInBattleWith(game, self, Filters.or(Filters.Han, Filters.Vos))) {
            boolean cannotSatisfyAttrition = game.getModifiersQuerying().cannotSatisfyAttrition(game.getGameState(), self);

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            if (cannotSatisfyAttrition)
                action.setText("Forfeit to satisfy all battle damage");
            else
                action.setText("Forfeit to satisfy all battle damage and attrition");
            // Pay cost(s)
            action.appendCost(
                    new ForfeitCardFromTableEffect(action, self));
            action.setActionMsg(null);
            // Perform result(s)
            if (cannotSatisfyAttrition)
                action.appendEffect(
                        new SatisfyAllBattleDamageEffect(action, playerId));
            else
                action.appendEffect(
                        new SatisfyAllBattleDamageAndAttritionEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
