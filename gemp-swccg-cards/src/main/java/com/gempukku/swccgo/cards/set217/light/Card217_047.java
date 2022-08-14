package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysAdjacentToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeploySitesBetweenSitesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Skywalker Hut
 */
public class Card217_047 extends AbstractSite {
    public Card217_047() {
        super(Side.LIGHT, "Tatooine: Skywalker Hut", Title.Tatooine);
        setLocationDarkSideGameText("May not be separated from Slave Quarters.");
        setLocationLightSideGameText("Once per turn, may deploy Shmi or [Tatooine] C-3PO here from Reserve Deck; reshuffle.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_17);
    }

    @Override
    public List<Modifier> getAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeploysAdjacentToLocationModifier(self, self, Filters.Slave_Quarters, true));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeploysAdjacentToLocationModifier(self, Filters.Slave_Quarters, self, true));
        modifiers.add(new MayNotDeploySitesBetweenSitesModifier(self, self, Filters.Slave_Quarters));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SKYWALKER_HUT__DOWNLOAD_SHMI_OR_THREEPIO;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && (GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId, Title.Shmi)
                || GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId, Persona.C3PO))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Shmi or C-3PO here");
            action.setActionMsg("Deploy Shmi or [Tatooine] C-3PO here from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.Shmi, Filters.and(Icon.TATOOINE, Filters.C3PO)), Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}