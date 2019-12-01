package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.LeaderModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

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
        setGameText("Adds 3 to power of anything he pilots. If on Hoth, once during your turn, may take a [Hoth] combat vehicle or 6th Marker into hand from Reserve Deck; reshuffle. Adds one battle destiny while piloting an AT-AT.");
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
        modifiers.add(new AddsBattleDestinyModifier(self, new PilotingCondition(self, Filters.AT_AT), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.VEERS__UPLOAD_HOTH_COMBAT_VEHICLE_OR_SIXTH_MARKER;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isAtLocation(game, self, Filters.Hoth_location)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a [Hoth] combat vehicle or Sixth Marker into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId,1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.and(Filters.AT_AT, Icon.HOTH), Filters.Sixth_Marker), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
