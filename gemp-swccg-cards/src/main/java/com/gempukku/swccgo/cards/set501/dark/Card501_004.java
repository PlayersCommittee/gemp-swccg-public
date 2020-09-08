package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

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
        setGameText("While with a Jedi, Padawan, or 'Hatred' card, opponent loses 1 Force whenever you initiate battle here. Once per turn, may use 1 Force (free if with your probe droid) to draw top card of Reserve Deck. Immune to attrition < 4.");
        setSpecies(Species.MIRIALAN);
        addKeywords(Keyword.INQUISITOR, Keyword.FEMALE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addPersona(Persona.SEVENTH_SISTER);
        setTestingText("Seventh Sister");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
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

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.battleInitiatedAt(game, effectResult, self.getOwner(), Filters.here(self))
                && (GameConditions.isDuringBattleWithParticipant(game, Filters.or(Filters.Jedi, Filters.padawan, Filters.hasStacked(Filters.hatredCard)))
                || GameConditions.isDuringBattleAt(game, Filters.hasStacked(Filters.hatredCard)))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make Opponent lose 1 force");
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(self.getOwner()), 1)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
