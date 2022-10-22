package com.gempukku.swccgo.cards.set209.dark;


import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Effect
 * Title: Prepare For A Surface Attack (V)
 */
public class Card209_042 extends AbstractNormalEffect {
    public Card209_042() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Prepare For A Surface Attack", Uniqueness.UNIQUE);
        setLore("Ruthless and well-equipped for both air and ground assault, Vader's feared Death Squadron came to the icy Rebel Base with total domination in mind.");
        setGameText("Deploy on table. AT-AT Cannons deploy -1, fire for free, and add 1 to armor. During your control phase, may take a non-[Jabba's Palace] vehicle weapon (without 'lost' in game text), [Hoth] device, or [Hoth] Epic Event into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.VIRTUAL_SET_9, Icon.VIRTUAL_SET_9);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter atAtCannons = Filters.AT_AT_Cannon;
        Filter cardsUsingAtAtCannon = Filters.hasAttached(atAtCannons);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, atAtCannons, -1));
        modifiers.add(new FiresForFreeModifier(self, atAtCannons));
        modifiers.add(new ArmorModifier(self, cardsUsingAtAtCannon, 1));

        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId;


        // Check condition(s) - During your control phase, may take a:
        //  - non-[Jabba's Palace] vehicle weapon (without "lost" in game text),
        //  - [Hoth] device,
        //  - or [Hoth] Epic Event
        // into hand from Reserve Deck; reshuffle.

        Filter nonJpVehicleWeaponWithoutLost = Filters.and(
                Filters.not(Icon.JABBAS_PALACE),
                Filters.vehicle_weapon,
                Filters.not(Filters.gameTextContains("lost"))
        );
        Filter hothDevice = Filters.and(Icon.HOTH, Filters.device);
        Filter hothEpicEvent = Filters.and(Icon.HOTH, Filters.Epic_Event);
        Filter searchReserveDeckTargets = Filters.or(nonJpVehicleWeaponWithoutLost, hothDevice, hothEpicEvent);

        gameTextActionId = GameTextActionId.PREPARE_FOR_A_SURFACE_ATTACK_V__UPLOAD_CARD;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a non-[Jabba's Palace] vehicle weapon (without 'lost' in game text), [Hoth] device, or [Hoth] Epic Event into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, searchReserveDeckTargets, true));
            actions.add(action);
        }

        return actions;
    }
}
