package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.InBattleOrStackedInBattleEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * SubType: Imperial
 * Title: Seventh Sister
 */
public class Card501_004 extends AbstractImperial {
    public Card501_004() {
        super(Side.DARK, 2, 5, 4, 5, 7, "Seventh Sister", Uniqueness.UNIQUE);
        setLore("Female Mirialan Inquisitor.");
        setGameText("Attrition against you is -1 here for each 'Hatred' card, Jedi, Padawan, and probe droid here. Once per turn, may use 1 Force (free if your probe droid here) to draw top card of Reserve Deck. Your Inquisitors and probe droids here are immune to attrition < 4.");
        setSpecies(Species.MIRIALAN);
        addKeywords(Keyword.INQUISITOR, Keyword.FEMALE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addPersona(Persona.SEVENTH_SISTER);
        setTestingText("Seventh Sister");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.here(self),Filters.or(Keyword.INQUISITOR,Filters.probe_droid)), 4));
        modifiers.add(new AttritionModifier(self, Filters.here(self), new NegativeEvaluator(new InBattleOrStackedInBattleEvaluator(self,Filters.or(Filters.Jedi,Filters.padawan,Filters.probe_droid),Filters.hatredCard)),self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        final int forceToUse;
        if (GameConditions.isWith(game, self, Filters.probe_droid)) {
            forceToUse = 0;
        } else {
            forceToUse = 1;
        }

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.canUseForce(game, playerId, forceToUse)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new UseForceEffect(action, playerId, forceToUse)
            );
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
