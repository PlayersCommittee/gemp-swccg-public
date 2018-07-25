package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfForcePileAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 ••Krennic, Death Star Commandant [Officer Evax (V)] 2
 [A New Hope - C1]
 CHARACTER - IMPERIAL
 Lore: Commander. Leader.
 POWER 3 ABILITY 3 FORCE-ATTUNED
 Text: [Pilot]2. Once per turn, if Death Star orbiting a battleground, may peek at top two cards of your Force Pile and take one into hand. Once per game, may /\ a non-unique Star Destroyer.
 DEPLOY 3 FORFEIT 5
 [Pilot] [Warrior]
 [Set 9]
 */

public class Card209_036 extends AbstractImperial {
    public Card209_036() {
        super(Side.DARK, 2, 3, 3, 3, 5, "Krennic, Death Star Commandant", Uniqueness.UNIQUE);
        setLore("Commander.  Leader");
        addKeywords(Keyword.LEADER, Keyword.COMMANDER);
        setGameText("[Pilot]2. Once per turn, if Death Star orbiting a battleground, may peek at top two cards of your Force Pile and take one into hand. Once per game, may /\\ a non-unique Star Destroyer.");
        addPersona(Persona.KRENNIC);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_9);
    }

    // Add 2 to stuff he pilots.
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    //Once per game, may /\ a non-unique Star Destroyer.

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Take Non-Unique Star Destroyer into hand from reserve
        // Check condition(s)
        GameTextActionId gameTextActionId1 = GameTextActionId.KRENNIC__UPLOAD_NON_UNIQUE_STAR_DESTROYER;
        GameTextActionId gameTextActionId2 = GameTextActionId.KRENNIC__PEEK_AT_TOP_OF_FORCE_PILE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId1);

            action.setText("Take a non-unique Star Destroyer into hand from reserve deck");
            action.setActionMsg("Take a non-unique Star Destroyer into hand from reserve deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 0));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.non_unique, Filters.Star_Destroyer), true));
            actions.add(action);
        }

        // Peek at top two cards of force

//        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextActionId2))
        Filter systemFilter = Filters.and(Filters.isOrbitedBy(Filters.Death_Star_system), Filters.battleground_system);
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId2)
            && GameConditions.canSpotLocation(game, systemFilter) && GameConditions.canUseForce(game, playerId, 2)) {
//        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
//                && GameConditions.canSpotLocation(game, systemFilter) && GameConditions.canUseForce(game, playerId, 2)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId2);
            action.setText("Peek at top cards of Force Pile");
            // Update usage limit(s)
            action.appendUsage(new OncePerTurnEffect(action));
//            action.appendUsage(
//                    new OncePerPhaseEffect(action));
            // Perform result(s)
            // peek at the top two cards of force pile, take a min of 1 and max of 1 into hand.
            action.appendEffect(new PeekAtTopCardsOfForcePileAndChooseCardsToTakeIntoHandEffect(action, playerId, 2, 1, 1));

            return Collections.singletonList(action);

        }


        return actions;
    }
}
