package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostToLocationModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Location
 * Subtype: Site
 * Title: Kashyyyk: Kachirho
 */
public class Card215_014 extends AbstractSite {
    public Card215_014() {
        super(Side.LIGHT, Title.Kachirho, Title.Kashyyyk, Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setLocationDarkSideGameText("Unless you occupy, your non-Wookiee characters deploy and move to here for +1 Force.");
        setLocationLightSideGameText("Once per turn, if you occupy with a Wookiee, may [download] a Kashyyyk location.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.VIRTUAL_SET_15, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        Condition unlessYouOccupy = new UnlessCondition(new OccupiesCondition(playerOnDarkSideOfLocation, self));
        Filter yourNonWookieeCharacters = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.character, Filters.not(Filters.Wookiee));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourNonWookieeCharacters, unlessYouOccupy, 1, self));
        modifiers.add(new MoveCostToLocationModifier(self, yourNonWookieeCharacters, unlessYouOccupy, 1, self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KASHYYYK_KACHIRHO__DOWNLOAD_KASHYYYK_LOCATION;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)
                && GameConditions.occupiesWith(game, self, playerOnLightSideOfLocation, Filters.and(self), Filters.Wookiee)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Kashyyyk location from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Kashyyyk_location, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
