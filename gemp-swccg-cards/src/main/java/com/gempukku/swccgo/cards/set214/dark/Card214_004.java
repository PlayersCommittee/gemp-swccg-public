package com.gempukku.swccgo.cards.set214.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelForceIconModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Location
 * SubType: Site
 * Title: Exegol: Sith Throne
 */
public class Card214_004 extends AbstractSite {
    public Card214_004() {
        super(Side.DARK, Title.Exegol_Sith_Throne, Title.Exegol);
        addIcon(Icon.DARK_FORCE, 2);
        setLocationDarkSideGameText("Once per game, may deploy [Episode VII] Emperor here from Reserve Deck; reshuffle.");
        setLocationLightSideGameText("No Force drains here. [Light Side] icons are canceled here. Your characters deploy +2 here.");
        addIcons(Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET, Icon.EPISODE_VII, Icon.VIRTUAL_SET_14);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.EXEGOL_SITH_THRONE__DEPLOY_EMPEROR;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId, Persona.EMPEROR)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy [Episode VII] Emperor here");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.Emperor, Filters.icon(Icon.EPISODE_VII)), Filters.here(self), false)
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self));
        modifiers.add(new CancelForceIconModifier(self, self, Integer.MAX_VALUE, Icon.LIGHT_FORCE, false));
        modifiers.add(new DeployCostToLocationModifier(self, 2, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.character)));
        return modifiers;
    }
}
