package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 16
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: The Works
 */
public class Card216_004 extends AbstractSite {
    public Card216_004() {
        super(Side.DARK, "Coruscant: The Works", Title.Coruscant);
        setLocationDarkSideGameText("May [download] non-[Theed Palace] Sidious here.");
        setLocationLightSideGameText("If opponent's [Special Edition] objective on table, gains one [Light Side] icon and Force drain +2 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 0);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        Filter opponentsSEObjective = Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.Objective, Filters.icon(Icon.SPECIAL_EDITION));
        OnTableCondition onTableCondition = new OnTableCondition(self, opponentsSEObjective);

        modifiers.add(new IconModifier(self, self, onTableCondition, Icon.LIGHT_FORCE));
        modifiers.add(new ForceDrainModifier(self, self, onTableCondition, 2, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_WORKS__DEPLOY_SIDIOUS;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId, Persona.SIDIOUS)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy non-[Theed Palace] Sidious here from Reserve Deck");
            action.setActionMsg("Deploy non-[Theed Palace] Sidious here from Reserve Deck");
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.not(Filters.icon(Icon.THEED_PALACE)), Filters.Sidious), Filters.here(self), true)
            );
            return Collections.singletonList(action);
        }

        return null;
    }
}
