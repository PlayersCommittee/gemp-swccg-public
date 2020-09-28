package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardStarshipOrVehicleOfPersonaCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Captain Daultay Dofine
 */
public class Card14_076 extends AbstractRepublic {
    public Card14_076() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Captain Daultay Dofine", Uniqueness.UNIQUE);
        setLore("Neimoidian captain of the Trade Federation Droid Control Ship blockading Naboo. Cowardly, lazy, and extremely greedy.");
        setGameText("Adds 3 to power of any capital starship he pilots. Once per game may take Activate The Droids into hand from Reserve Deck; reshuffle. While aboard Blockade Flagship, it is immune to attrition < 5 and Activate The Droids is immune to Alter.");
        addPersona(Persona.DOFINE);
        addKeyword(Keyword.CAPTAIN);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT);
        setSpecies(Species.NEIMOIDIAN);
        setMatchingStarshipFilter(Filters.Blockade_Flagship);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition aboardBlockadeFlagship = new AboardStarshipOrVehicleOfPersonaCondition(self, Persona.BLOCKADE_FLAGSHIP);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.capital_starship));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.Blockade_Flagship, aboardBlockadeFlagship, 5));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Activate_The_Droids, aboardBlockadeFlagship, Title.Alter));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CAPTAIN_DAULTAY_DOFINE__UPLOAD_ACTIVATE_THE_DROIDS;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Activate The Droids into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Activate_The_Droids, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
