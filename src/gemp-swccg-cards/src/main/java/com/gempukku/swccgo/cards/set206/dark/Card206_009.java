package com.gempukku.swccgo.cards.set206.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 6
 * Type: Character
 * Subtype: Alien
 * Title: Boba Fett (V)
 */
public class Card206_009 extends AbstractAlien {
    public Card206_009() {
        super(Side.DARK, 1, 4, 4, 3, 6, "Boba Fett", Uniqueness.UNIQUE, ExpansionSet.SET_6, Rarity.V);
        setVirtualSuffix(true);
        setArmor(5);
        setLore("Feared bounty hunter. Collected bounties on Solo from both the Empire and Jabba the Hutt. Took exquisite pleasure in using Solo's friend to capture him.");
        setGameText("[Pilot] 3. Deploys -1 aboard Slave I. Adds one battle destiny with Vader (or while piloting Slave I). Once per game, if a battle was just initiated here, may exchange a card in hand with a card in Reserve Deck; reshuffle.");
        addPersona(Persona.BOBA_FETT);
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_6);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setMatchingStarshipFilter(Filters.Slave_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new OrCondition(new WithCondition(self, Filters.Vader), new PilotingCondition(self, Filters.Slave_I)), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, -1, Filters.Slave_I));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BOBA_FETT__EXCHANGE_CARD_WITH_CARD_IN_RESERVE_DECK;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange card with card in Reserve Deck");
            action.setActionMsg("Exchange a card in hand with a card in Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithCardInReserveDeckEffect(action, playerId, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
