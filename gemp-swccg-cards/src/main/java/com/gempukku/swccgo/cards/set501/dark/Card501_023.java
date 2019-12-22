package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeAboardModifier;
import com.gempukku.swccgo.logic.modifiers.FireWeaponCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Vehicle
 * Subtype: Transport
 * Title: Jabba's Sail Barge (V) (Errata)
 */
public class Card501_023 extends AbstractTransportVehicle {
    public Card501_023() {
        super(Side.DARK, 3, 3, 4, 5, null, 2, 6, "Jabba's Sail Barge", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Ubrikkian sail barge. Custom built for Jabba with an armored hull and weaponry. Top speed of 100 kph. Used by the Hutt crimelord on his many trips to Mos Eisley.");
        setGameText("May add 1 driver and 7 passengers. Once per turn, may deploy Passenger Deck. Double Laser Cannon fires (and deploys) aboard for -2 Force. May not be targeted by weapons unless each alien aboard is 'hit'.");
        addIcons(Icon.JABBAS_PALACE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_4);
        addPersona(Persona.JABBAS_SAIL_BARGE);
        setDriverCapacity(1);
        setPassengerCapacity(7);
        setTestingText("Jabba's Sail Barge (V) (Errata)");
    }

    @Override
    protected Filter getGameTextValidDriverFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.alien;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter doubleLaserCannon = Filters.title(Title.Double_Laser_Cannon);
        modifiers.add(new DeploysFreeAboardModifier(self, doubleLaserCannon, self));
        modifiers.add(new FireWeaponCostModifier(self, doubleLaserCannon, -2));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, new HasAboardCondition(self, Filters.and(Filters.alien, Filters.not(Filters.hit)))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.JABBAS_SAIL_BARGE__DOWNLOAD_PASSENGER_DECK;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Passenger Deck from Reserve Deck");
            action.setActionMsg("Deploy Passenger Deck from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Passenger_Deck, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}