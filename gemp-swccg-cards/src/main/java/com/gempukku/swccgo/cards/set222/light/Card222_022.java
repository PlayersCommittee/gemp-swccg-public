package com.gempukku.swccgo.cards.set222.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToDeployCostModifiersToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Snow Trench (2nd Marker) (V)
 */
public class Card222_022 extends AbstractSite {
    public Card222_022() {
        super(Side.LIGHT, "Hoth: Snow Trench (2nd Marker)", Title.Hoth, Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Your deploy costs may not be modified here.");
        setLocationLightSideGameText("Your artillery weapons deploy -2 here. Once per game may [download] an artillery weapon here");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_22);
        addKeywords(Keyword.MARKER_2);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToDeployCostModifiersToLocationModifier(self, Filters.your(playerOnDarkSideOfLocation), Filters.any, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.artillery_weapon), -2, self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.SNOW_TRENCH__DOWNLOAD_ARTILLERY_WEAPON;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.DEPLOY)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy artillery weapon from Reserve Deck");
            action.setActionMsg("Deploy artillery weapon from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.artillery_weapon, Filters.here(self), true));
            actions.add(action);
        }

        return actions;
    }
}