package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Character
 * Subtype: Imperial
 * Title: JS-1975
 */
public class Card223_015 extends AbstractImperial {
    public Card223_015() {
        super(Side.DARK, 3, 2, 2, 2, 3, "JS-1975", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Biker Scout Trooper. Imperial Remnant");
        setGameText("Adds 2 to power of anything he pilots. Once per game, may take Gideon into hand from Reserve Deck; reshuffle. While piloting a speeder bike, Grogu is forfeit -2 here and opponent's weapon destinies targeting your speeder bikes are -1 here.");
        addKeywords(Keyword.BIKER_SCOUT, Keyword.IMPERIAL_REMNANT);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_23);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Condition pilotingSpeederBike = new PilotingCondition(self, Filters.speeder_bike);
        
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.opponents(self), Filters.weapon), pilotingSpeederBike, Filters.any, -1, Filters.and(Filters.your(self), Filters.speeder_bike)));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Grogu, Filters.here(self)), pilotingSpeederBike, -2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.JS_1975__UPLOAD_GIDEON;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Moff Gideon into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Gideon, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}