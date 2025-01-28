package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MayBeReplacedByOpponentModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Komilia Lap'lamiz, Emperor's Guard
 */
public class Card304_025 extends AbstractImperial {
    public Card304_025() {
        super(Side.DARK, 3, 3, 5, 3, 4, "Komilia Lap'lamiz, Emperor's Guard", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Appointed to lead the Royal Guard by her Father, Komilia was out of her league. Her lack of experience and arrogance would have led to an early death if not for her name.");
        setGameText("Adds 2 to anything she pilots. When armed with a Force pike, adds one battle destiny. When deployed, may [retrieve] a Scholae Palatinae, Royal Guard. During battle, if with Kamjin or a Scholae Palatinae Emperor, once per game may cancel a non-[Immune to Sense] Interrupt.");
        addPersona(Persona.KOMILIA);
        addIcons(Icon.CSP, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.CSP_ROYAL_GUARD, Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
		modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
		modifiers.add(new MayBeReplacedByOpponentModifier(self, new PresentAtCondition(self, Filters.site)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KOMILIA__UPLOAD_CSP_ROYAL_GUARD;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a card into hand from Reserve Deck");
            action.setActionMsg("Take a CSP Royal Guard into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.CSP_ROYAL_GUARD, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KOMILIA__CANCEL_INTERRUPT;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.Interrupt, Filters.not(Filters.immune_to_Sense)))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isPresentAt(game, self, Filters.site)
                && GameConditions.isInBattleWith(game, self, Filters.or(Filters.Kamjin, Filters.CSP_EMPEROR))
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
