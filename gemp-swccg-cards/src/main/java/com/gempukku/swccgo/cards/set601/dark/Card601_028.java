package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInHandEqualToOrFewerThanCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotRemoveCardsFromOpponentsHandModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 6
 * Type: Effect
 * Title: Tarkin's Bounty (V)
 */
public class Card601_028 extends AbstractNormalEffect {
    public Card601_028() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Tarkin's Bounty", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'You don't know how hard I found it signing the order to terminate your life.'");
        setGameText("Deploy on table.  Each Amidala stacked on a Political Effect is a senator.  While you have < 13 cards in hand, opponent may not peek at or remove them (except with Grimtaash).  Once per turn, may take one [Virtual] Astromech Shortage into hand from Reserve Deck; reshuffle, or lose 2 Force to cancel a Political Effect. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_6);
        addKeywords(Keyword.BOUNTY);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.Amidala, Filters.stackedOn(self, Filters.Political_Effect)), Keyword.SENATOR));
        modifiers.add(new MayNotRemoveCardsFromOpponentsHandModifier(self, opponent, new CardsInHandEqualToOrFewerThanCondition(playerId, 12), Filters.except(Filters.Grimtaash)));
        //TODO opponent may not peek at cards in your hand either
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TARKINS_BOUNTY__UPLOAD_ASTROMECH_SHORTAGE_OR_CANCEL_POLITICAL_EFFECT;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
            && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take [V] Astromech Shortage into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Icon.LEGACY_BLOCK_1, Filters.title(Title.Astromech_Shortage)), true));
            actions.add(action);
        }

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTargetToCancel(game, self, Filters.Political_Effect)) {
            // Check condition(s)
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Political_Effect, "A Political Effect");
            action.appendCost(new LoseForceEffect(action, playerId, 2, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TARKINS_BOUNTY__UPLOAD_ASTROMECH_SHORTAGE_OR_CANCEL_POLITICAL_EFFECT;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && TriggerConditions.isPlayingCard(game, effect, Filters.Political_Effect)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendCost(new LoseForceEffect(action, playerId, 2, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}