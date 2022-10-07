package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: Site
 * Title: Lothal: Capital City
 */
public class Card219_039 extends AbstractSite {
    public Card219_039() {
        super(Side.LIGHT, Title.Lothal_Capital_City, Title.Lothal);
        setLocationDarkSideGameText("May [download] Thrawn here.");
        setLocationLightSideGameText("If a player controls, their total power is +2 at related locations.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalPowerModifier(self, Filters.relatedLocation(self), new ControlsCondition(playerOnDarkSideOfLocation, self), 2, playerOnDarkSideOfLocation));
        modifiers.add(new TotalPowerModifier(self, Filters.relatedLocation(self), new ControlsCondition(game.getOpponent(playerOnDarkSideOfLocation), self), 2, game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LOTHAL_CAPITAL_CITY__DOWNLOAD_THRAWN;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId, Persona.THRAWN)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Thrawn here");
            action.setActionMsg("Deploy Thrawn here from Reserve Deck");

            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Thrawn, Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
