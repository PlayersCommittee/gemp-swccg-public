package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: Malachor: Sith Temple Entrance
 */
public class Card213_029 extends AbstractSite {
    public Card213_029() {
        super(Side.DARK, "Malachor: Sith Temple Entrance", Title.Malachor);
        setLocationDarkSideGameText("You initiate battles here for free. Unless you occupy, Inquisitors deploy -1 here.");
        setLocationLightSideGameText("Once per game, you may deploy a Padawan (except Anakin) here from Reserve Deck; reshuffle.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.UNDERGROUND, Icon.PLANET, Icon.VIRTUAL_SET_13);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new InitiateBattlesForFreeModifier(self, self, playerOnDarkSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.inquisitor, new UnlessCondition(new OccupiesCondition(playerOnDarkSideOfLocation, self)), -1, self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MALACHOR_SITH_TEMPLE_ENTRANCE__DEPLOY_PADAWAN;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Padawan");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.padawan, Filters.not(Filters.persona(Persona.ANAKIN))), Filters.here(self), true)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}