package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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
 * Set: The Great Hutt Expansion
 * Type: Location
 * Subtype: Site
 * Title: Ferfiek Chawa: Claudius's Waiting Room
 */
public class Card304_104 extends AbstractUniqueStarshipSite {
    public Card304_104() {
        super(Side.LIGHT, "Ferfiek Chawa: Claudius's Waiting Room", Persona.FERFIEK_CHAWA, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLocationDarkSideGameText("Add 1 to your lightsaber weapon destiny draws here. Unless Claudius on table, Force drain -1 here.");
        setLocationLightSideGameText("During your move phase, your Tiure leader may move between here and a site.");
        addIcon(Icon.DARK_FORCE, 0);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.SCOMP_LINK, Icon.MOBILE, Icon.STARSHIP_SITE);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        Filter otherSite = Filters.and(Filters.other(self), Filters.site, Filters.not(Filters.or(Filters.underHothEnergyShield(), Filters.partOfSystem(Title.Dagobah), Filters.partOfSystem(Title.Ahch_To))));
        Filter clanTiureLeader = Filters.and(Filters.your(playerOnLightSideOfLocation), Keyword.CLAN_TIURE, Keyword.LEADER);
        Filter clanTiureLeaderHere = Filters.and(Filters.here(self), clanTiureLeader);
        Filter clanTiureLeaderOtherLocation = Filters.and(Filters.not(Filters.here(self)), clanTiureLeader);


        if (GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.MOVE)) {

            // Move FROM here to another site
            if (GameConditions.canSpotLocation(game, otherSite)
                    && GameConditions.canSpot(game, self, clanTiureLeaderHere)
                    && GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, clanTiureLeaderHere, self, otherSite, false)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, clanTiureLeaderHere, self, otherSite, false);
                action.setText("Move from here to another site");
                actions.add(action);
            }

            // Move TO this site from another site
            if (GameConditions.canSpotLocation(game, otherSite)
                    && GameConditions.canSpot(game, self, clanTiureLeaderOtherLocation)
                    && GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, clanTiureLeaderOtherLocation, otherSite, self, false)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, clanTiureLeaderOtherLocation, otherSite, self, false);
                action.setText("Move from another site to here");
                actions.add(action);
            }
        }


        return actions;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.lightsaber, Filters.here(self)), 1));
        modifiers.add(new ForceDrainModifier(self, new UnlessCondition(new OnTableCondition(self, Filters.Claudius)), -1, playerOnDarkSideOfLocation));
        return modifiers;
    }

}
