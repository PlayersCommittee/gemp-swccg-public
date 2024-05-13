package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: Site
 * Title: Jakku: Niima Marketplace
 */
public class Card221_023 extends AbstractSite {
    public Card221_023() {
        super(Side.DARK, "Jakku: Niima Marketplace", Title.Jakku, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLocationDarkSideGameText("During your move phase, your [Episode VII] characters may move from here to a battleground site.");
        setLocationLightSideGameText("If Finn or Rey here, opponent's First Order characters may not move from here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_VII, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        Filter character = Filters.and(Filters.your(playerOnDarkSideOfLocation), Icon.EPISODE_VII, Filters.character);
        Filter battleground = Filters.and(Filters.other(self), Filters.battleground_site);

        if (GameConditions.canSpotLocation(game, battleground)
            && GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.MOVE)
            && GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, character, self, battleground, false)) {
            MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, character, self, battleground, false);
            action.setText("Move your [Episode VII] character");
            actions.add(action);
            return actions;
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotMoveFromLocationModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.First_Order_character), new HereCondition(self, Filters.or(Filters.Finn, Filters.Rey)), Filters.here(self)));
        return modifiers;
    }
}
