package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Bewil
 */
public class Card204_037 extends AbstractImperial {
    public Card204_037() {
        super(Side.DARK, 3, 3, 2, 2, 4, "Captain Bewil", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Tactical officer from Dentaal. Leader. Familiar with utilizing computer controls to lure an invading enemy into a tactically weak position.");
        setGameText("While at a Cloud City site, adds one to the number of Bespin locations required to cancel Dark Deal. Once per turn, if Dark Deal on table, may search your Force Pile and take one Interrupt into hand; reshuffle.");
        addIcons(Icon.CLOUD_CITY, Icon.WARRIOR, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.CAPTAIN, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Dark_Deal, new AtCondition(self, Filters.Cloud_City_site),
                ModifyGameTextType.DARK_DEAL__ADDITIONAL_BESPIN_LOCATION_TO_CANCEL));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CAPTAIN_BEWIL__UPLOAD_INTERRUPT_FROM_FORCE_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Dark_Deal)
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Force Pile");
            action.setActionMsg("Take an Interrupt into hand from Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromForcePileEffect(action, playerId, Filters.Interrupt, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
