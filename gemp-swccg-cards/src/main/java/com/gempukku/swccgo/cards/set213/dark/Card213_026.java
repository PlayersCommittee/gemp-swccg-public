package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: First Light: Dryden's Study
 */
public class Card213_026 extends AbstractUniqueStarshipSite {
    public Card213_026() {
        super(Side.DARK, "First Light: Dryden's Study", Persona.FIRST_LIGHT);
        setLocationDarkSideGameText("Once during your move phase, your Crimson Dawn leader may move between here and any site.");
        setLocationLightSideGameText("Add 1 to your blaster weapon destiny draws here. Unless Vos on table, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 0);
        addIcons(Icon.VIRTUAL_SET_13, Icon.INTERIOR_SITE, Icon.SCOMP_LINK, Icon.MOBILE, Icon.STARSHIP_SITE);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        Filter otherSite = Filters.and(Filters.other(self), Filters.site, Filters.not(Filters.or(Filters.partOfSystem(Title.Dagobah), Filters.partOfSystem(Title.Ahch_To))));
        Filter crimsonDawnLeader = Filters.and(Filters.your(playerOnDarkSideOfLocation), Keyword.CRIMSON_DAWN, Keyword.LEADER);
        Filter crimsonDawnLeaderHere = Filters.and(Filters.here(self), crimsonDawnLeader);
        Filter crimsonDawnLeaderOtherLocation = Filters.and(Filters.not(Filters.here(self)), crimsonDawnLeader);


        if (GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.MOVE)) {

            // Move FROM here to another site
            if (GameConditions.canSpotLocation(game, otherSite)
                    && GameConditions.canSpot(game, self, crimsonDawnLeaderHere)
                    && GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, crimsonDawnLeaderHere, self, otherSite, false)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, crimsonDawnLeaderHere, self, otherSite, false);
                action.setText("Move from here to another site");
                actions.add(action);
            }

            // Move TO this site from another site
            if (GameConditions.canSpotLocation(game, otherSite)
                    && GameConditions.canSpot(game, self, crimsonDawnLeaderOtherLocation)
                    && GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, crimsonDawnLeaderOtherLocation, otherSite, self, false)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, crimsonDawnLeaderOtherLocation, otherSite, self, false);
                action.setText("Move from another site to here");
                actions.add(action);
            }
        }


        return actions;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.blaster, Filters.here(self)), 1));
        modifiers.add(new ForceDrainModifier(self, new UnlessCondition(new OnTableCondition(self, Filters.Vos)), -1, playerOnLightSideOfLocation));
        return modifiers;
    }

}
