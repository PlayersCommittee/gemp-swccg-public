package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Defensive Permimeter (3rd Marker) (V)
 */
public class Card223_038 extends AbstractSite {
    public Card223_038() {
        super(Side.LIGHT, Title.Defensive_Perimeter, Title.Hoth, Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLocationLightSideGameText("During opponent's turn, if you occupy with a [Hoth] character or [Hoth] vehicle, may activate 1 Force.");
        setLocationDarkSideGameText("If you occupy, your Force generation is +1 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_23);
        addKeywords(Keyword.MARKER_3);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, new OccupiesCondition(playerOnDarkSideOfLocation, self), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canActivateForce(game, playerOnLightSideOfLocation)
                && GameConditions.occupiesWith(game, self, playerOnLightSideOfLocation, Filters.here(self), Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.character, Filters.vehicle), Filters.icon(Icon.HOTH)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));

            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerOnLightSideOfLocation, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
