package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Resistance Character
 * Title: Lieutenant Kaydel Connix
 */
public class Card209_009 extends AbstractResistance {
    public Card209_009() {
        super(Side.LIGHT, 2, 2, 1, 2, 4, "Lieutenant Kaydel Connix", Uniqueness.UNIQUE);
        setLore("Female");
        addKeywords(Keyword.FEMALE);
        // Currently no keywords defined for Lieutenant.  Thought about creating one and marking Connix as one, but would be too much work to find all lieutenants
        //  and make sure that keyword was added for all of them too.  If this needs to be done in the future, it can be done then.
        setGameText("[Pilot]2. Your shuttling, landing, and taking off to or from here is free. Once per game, may use 1 Force to [upload] Evacuation Control, a [Resistance] bomber, or a [Resistance] transport.");
        addIcons(Icon.PILOT, Icon.EPISODE_VII, Icon.VIRTUAL_SET_9);
        addPersona(Persona.CONNIX);
    }

    // Copied and pasted over from Commander Desanne (DS
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourCards = Filters.your(self);
        Filter sameLocation = Filters.sameLocation(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ShuttlesFreeToLocationModifier(self, yourCards, sameLocation));
        modifiers.add(new ShuttlesFreeFromLocationModifier(self, yourCards, sameLocation));
        modifiers.add(new LandsFreeToLocationModifier(self, yourCards, sameLocation));
        modifiers.add(new LandsFreeFromLocationModifier(self, yourCards, sameLocation));
        modifiers.add(new TakesOffFreeToLocationModifier(self, yourCards, sameLocation));
        modifiers.add(new TakesOffFreeFromLocationModifier(self, yourCards, sameLocation));
        return modifiers;
    }

    /*
     - Once per Game: May Use 1 Force to Upload
     - Evacuation Control, or
     - a Resistance bomber, or
     - a Resistance transport
    */
    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.CONNIX__UPLOAD_EVACUATION_CONTROL_OR_RESISTANCE_BOMBER_OR_RESISTANCE_TRANSPORT;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take an Evac Control or Resistance Bomber or Resistance Transport into hand from Reserve Deck");
            action.setActionMsg("Take an Evac Control or Resistance Bomber or Resistance Transport into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Evacuation_Control, Filters.and(Filters.Resistance, Filters.or(Filters.transport, Filters.bomber))), true));
            actions.add(action);
        }

        return actions;
    }

}
