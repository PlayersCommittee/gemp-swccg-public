package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 6
 * Type: Character
 * Subtype: Imperial
 * Title: Veers (V) (Errata)
 */
public class Card501_007 extends AbstractImperial {
    public Card501_007() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Veers", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("General of the AT-AT assault armor division sent by Darth Vader to crush the Rebellion on Hoth. Cold and ruthless.");
        setGameText("Adds 3 to the power of anything he pilots. Leader. If in battle on Hoth, adds one destiny to total power. If on Hoth, during your turn, may take a [H] AT-AT, 4th Marker, or 6th Marker into hand from Reserve Deck; reshuffle.");
        addPersona(Persona.VEERS);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_6);
        addKeywords(Keyword.GENERAL);
        setTestingText("Veers (V) Errata");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LeaderModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsDestinyToPowerModifier(self, new OnCondition(self, Title.Hoth), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.VEERS__UPLOAD_HOTH_COMBAT_VEHICLE_OR_SIXTH_MARKER;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isAtLocation(game, self, Filters.Hoth_location)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take 4th Marker, Sixth Marker, or a [Hoth] AT-AT into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.and(Filters.AT_AT, Icon.HOTH), Filters.Fourth_Marker, Filters.Sixth_Marker), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}