package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Vehicle
 * Subtype: Transport
 * Title: Jabba's Sail Barge (V)
 */
public class Card601_011 extends AbstractTransportVehicle {
    public Card601_011() {
        super(Side.DARK, 3, 3, 4, 5, null, 2, 5, "Jabba's Sail Barge", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Ubrikkian sail barge. Custom built for Jabba with an armored hull and weaponry. Top speed of 100 kph. Used by the Hutt crimelord on his many trips to Mos Eisley.");
        setGameText("Deploys only on Tatooine. May add 1 driver and 8 passengers. Scum And Villainy may deploy aboard. During your deploy phase, may deploy one Jabba, Pote, or Passenger Deck aboard from Reserve Deck; reshuffle. Immune to attrition < 6.");
        addIcons(Icon.JABBAS_PALACE, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_3);
        addPersona(Persona.JABBAS_SAIL_BARGE);
        setDriverCapacity(1);
        setPassengerCapacity(8);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDriverFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.alien;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Collection<PhysicalCard> jabbasSailBargeMayDeployHere = game.getModifiersQuerying().getActiveCardsAffectedByModifier(game.getGameState(), ModifierType.JABBAS_SAIL_BARGE_MAY_DEPLOY_HERE);
        return Filters.or(Filters.Deploys_on_Tatooine, Filters.in(jabbasSailBargeMayDeployHere));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ScumAndVillainyMayDeployAttachedModifier(self, self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.JABBAS_SAIL_BARGE__DOWNLOAD_PASSENGER_DECK_OR_SKIFF;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.JABBA)
                || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Passenger_Deck)
                || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, "Pote Snitkin"))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card aboard from Reserve Deck");
            action.setActionMsg("Deploy Jabba, Pote, or Passenger Deck aboard from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardAboardFromReserveDeckEffect(action, Filters.or(Filters.Passenger_Deck, Filters.Jabba, Filters.title("Pote Snitkin")), Filters.and(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }
}
