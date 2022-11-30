package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfForcePileAndReserveDeckAndReturnOneCardToEachEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Character
 * Subtype: Republic
 * Title: Yane (V)
 */
public class Card201_007 extends AbstractRepublic {
    public Card201_007() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Yane", Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("The youngest handmaiden, Yane stayed behind on Naboo while Amidala pleaded her case to the Galactic Senate. Musician.");
        setGameText("Whenever opponent deploys a character to same or adjacent site, may activate 1 Force. While with Amidala, forfeit +1, and once per turn may peek at the top card of your Force Pile and Reserve Deck; return one card to each deck or pile.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.FEMALE, Keyword.HANDMAIDEN, Keyword.MUSICIAN);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, opponent, Filters.character, Filters.sameOrAdjacentSite(self))
                && GameConditions.canActivateForce(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, new WithCondition(self, Filters.Amidala), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasForcePile(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.isWith(game, self, Filters.Amidala)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top card of Force Pile and Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardOfForcePileAndReserveDeckAndReturnOneCardToEachEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
