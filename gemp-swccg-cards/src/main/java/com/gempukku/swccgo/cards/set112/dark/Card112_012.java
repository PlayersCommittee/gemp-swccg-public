package com.gempukku.swccgo.cards.set112.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Location
 * Subtype: Site
 * Title: Jabba's Palace: Lower Passages
 */
public class Card112_012 extends AbstractSite {
    public Card112_012() {
        super(Side.DARK, Title.Lower_Passages, Title.Tatooine);
        setLocationDarkSideGameText("During your move phase, your aliens may move between here and any Jabba's Palace site.");
        setLocationLightSideGameText("While your Lando here, opponent's Lower Passages game text is canceled. Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PREMIUM, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.JABBAS_PALACE_SITE);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        Filter otherJabbasPalaceSite = Filters.and(Filters.other(self), Filters.Jabbas_Palace_site);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.MOVE)
                && GameConditions.canSpotLocation(game, otherJabbasPalaceSite)) {

            if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.alien, self, otherJabbasPalaceSite, true)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.alien, self, otherJabbasPalaceSite, false);
                action.setText("Move from here to other Jabba's Palace site");
                actions.add(action);
            }
            if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.alien, otherJabbasPalaceSite, self, true)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.alien, otherJabbasPalaceSite, self, false);
                action.setText("Move from other Jabba's Palace site to here");
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Lower_Passages,
                new HereCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Lando)),
                game.getOpponent(playerOnLightSideOfLocation)));
        modifiers.add(new ForceDrainModifier(self, -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}