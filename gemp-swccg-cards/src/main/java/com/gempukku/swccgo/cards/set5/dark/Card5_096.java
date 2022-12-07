package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.LandsFreeFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.LandsFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ShuttlesFreeFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ShuttlesFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.TakesOffFreeFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.TakesOffFreeToLocationModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Desanne
 */
public class Card5_096 extends AbstractImperial {
    public Card5_096() {
        super(Side.DARK, 2, 2, 1, 2, 3, "Commander Desanne", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("Officer from Kalist VI. Due to a political blunder, was stripped of most official duties. Relegated to shuttling dignitaries and high-ranking Imperials.");
        setGameText("Adds 2 to power of anything he pilots. Your shuttling, landing and taking off to or from same location is free. During your control phase, may take one Lambda shuttle or Landing Craft into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT);
        addKeywords(Keyword.COMMANDER);
    }

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

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.COMMANDER_DESANNE__UPLOAD_LAMBDA_SHUTTLE_OR_LANDING_CRAFT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Lambda shuttle or Landing Craft into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Lambda_shuttle, Filters.Landing_Craft), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
