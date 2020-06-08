package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 8
 * Type: Character
 * Subtype: Imperial
 * Title: Kir Kanos (V)
 */
public class Card208_032 extends AbstractImperial {
    public Card208_032() {
        super(Side.DARK, 3, 3, 5, 3, 4, "Kir Kanos", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Fiercely devoted Royal Guard. Feels deeply indebted to those who risk their life for him. Unaware of the extent of Palpatine's atrocities and cruelty.");
        setGameText("When deployed, may [upload] a Royal Guard. If in battle with an Imperial, once per game may cancel a non-[Immune to Sense] Interrupt.");
        addIcons(Icon.REFLECTIONS_II, Icon.WARRIOR, Icon.VIRTUAL_SET_8);
        addKeywords(Keyword.ROYAL_GUARD);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KIR_KANOS__UPLOAD_REBEL_GUARD;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a card into hand from Reserve Deck");
            action.setActionMsg("Take a Royal Guard into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Royal_Guard, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KIR_KANOS__CANCEL_INTERRUPT;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.Interrupt, Filters.not(Filters.immune_to_Sense)))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isInBattleWith(game, self, Filters.Imperial)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendUsage(new OncePerGameEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
