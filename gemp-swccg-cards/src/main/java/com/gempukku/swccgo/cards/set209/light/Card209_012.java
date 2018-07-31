package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Rebel
 * Title: Saw Gerrera
 */
public class Card209_012 extends AbstractRebel {
    public Card209_012() {
        super(Side.LIGHT, 2, 4, 4, 3, 6, "Saw Gerrera", Uniqueness.UNIQUE);
        setLore("Leader.");
        setGameText("Attrition against opponent is +1 here for each of their characters present. Opponent may not reduce your Force drains here. Once per game, if in battle with an Imperial or Rebel, may lose top card of Reserve Deck to cancel a non-[Immune to Sense] Interrupt.");
        addIcons(Icon. WARRIOR, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, Filters.here(self), new PresentEvaluator(self, Filters.and(Filters.opponents(self), Filters.character)), opponent));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, Filters.here(self), opponent, playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SAW_GERRERA__CANCEL_INTERRUPT;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.Interrupt, Filters.not(Filters.immune_to_Sense)))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isInBattleWith(game, self, Filters.or(Filters.Imperial, Filters.Rebel))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendUsage(new OncePerGameEffect(action));
            action.appendCost(new LoseForceFromReserveDeckEffect(action, playerId, 1, true));
            return Collections.singletonList(action);
        }
        return null;
    }

}
