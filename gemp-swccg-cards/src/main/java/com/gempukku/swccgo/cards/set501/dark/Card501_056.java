package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: First Light: Dryden’s Study
 */
public class Card501_056 extends AbstractUniqueStarshipSite {
    public Card501_056() {
        super(Side.DARK, "First Light: Dryden’s Study", Persona.FIRST_LIGHT);
        setLocationDarkSideGameText("Once during your move phase, your Crimson Dawn leader may move between here and any site.");
        setLocationLightSideGameText("During battle here, add one battle destiny and your blaster weapon destinies are +1.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 0);
        addIcons(Icon.VIRTUAL_SET_13, Icon.INTERIOR_SITE, Icon.SCOMP_LINK, Icon.MOBILE, Icon.STARSHIP_SITE);
        setTestingText("First Light: Dryden’s Study");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        Filter otherSite = Filters.and(Filters.other(self), Filters.site);
        Filter crimsonDawnLeader = Filters.and(Filters.your(playerOnDarkSideOfLocation), Keyword.CRIMSON_DAWN, Keyword.LEADER);
        Filter crimsonDawnLeaderHere = Filters.and(Filters.here(self), crimsonDawnLeader);
        Filter crimsonDawnLeaderOtherLocation = Filters.and(Filters.not(Filters.here(self)), crimsonDawnLeader);


        if (GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.MOVE)) {

            // Move FROM here to another site
            if (GameConditions.canSpotLocation(game, otherSite)
                    && GameConditions.canSpot(game, self, crimsonDawnLeaderHere)
                    && GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, crimsonDawnLeaderHere, self, otherSite, false)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, crimsonDawnLeaderHere, self, otherSite, false);
                action.setText("Move from here to other battleground site");
                actions.add(action);
            }

            // Move TO this site from another site
            if (GameConditions.canSpotLocation(game, otherSite)
                    && GameConditions.canSpot(game, self, crimsonDawnLeaderOtherLocation)
                    && GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, crimsonDawnLeaderOtherLocation, otherSite, self, false)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, crimsonDawnLeaderOtherLocation, otherSite, self, false);
                action.setText("Move from other battleground site to here");
                actions.add(action);
            }
        }


        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, self)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add one battle destiny");
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1, playerOnLightSideOfLocation));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Filter blasterInBattle = Filters.and(Filters.participatingInBattle, Filters.blaster);
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), blasterInBattle, Filters.here(self)), 1));
        return modifiers;
    }

}
