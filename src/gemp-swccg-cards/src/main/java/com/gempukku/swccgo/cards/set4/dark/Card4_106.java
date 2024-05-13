package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Warrant Officer M'Kae
 */
public class Card4_106 extends AbstractImperial {
    public Card4_106() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Warrant Officer M'Kae", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.U);
        setLore("Hard-working Avenger signal officer. Coordinates and tests tractor beam operations. Apprises Executor of his ship's activities and relays orders from the flagship to his captain.");
        setGameText("Adds 2 to power of anything he pilots. May use 1 Force to take one tractor beam, Our First Catch Of The Day or Besieged into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.DAGOBAH, Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.WARRANT_OFFICER_MKEA__UPLOAD_TRACTOR_BEAM_OUR_FIRST_CATCH_OF_THE_DAY_OR_BESIEGED;

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a tractor beam, Our First Catch Of The Day, or Besieged into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.tractor_beam, Filters.Our_First_Catch_Of_The_Day, Filters.Besieged), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
