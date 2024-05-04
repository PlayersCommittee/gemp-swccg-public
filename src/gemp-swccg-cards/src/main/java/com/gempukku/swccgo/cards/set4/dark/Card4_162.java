package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Executor: Main Corridor
 */
public class Card4_162 extends AbstractUniqueStarshipSite {
    public Card4_162() {
        super(Side.DARK, Title.Main_Corridor, Persona.EXECUTOR, ExpansionSet.DAGOBAH, Rarity.C);
        setLocationDarkSideGameText("During your move phase, you may move free between here and Executor or any Executor site.");
        setLocationLightSideGameText("If you control, opponent's Main Corridor game text is canceled.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        Filter otherExecutorSite = Filters.and(Filters.other(self), Filters.Executor_site);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.MOVE)) {
            if (GameConditions.canSpotLocation(game, otherExecutorSite)) {

                if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.any, self, otherExecutorSite, true)) {

                    MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.any, self, otherExecutorSite, true);
                    action.setText("Move from here to other Executor site");
                    actions.add(action);
                }
                if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.any, otherExecutorSite, self, true)) {

                    MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.any, otherExecutorSite, self, true);
                    action.setText("Move from other Executor site to here");
                    actions.add(action);
                }
            }
            if (GameConditions.canSpot(game, self, Filters.Executor)) {

                if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.any, self, Filters.Executor, true)) {

                    MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.any, self, Filters.Executor, true);
                    action.setText("Move from here to Executor");
                    actions.add(action);
                }
                if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.any, Filters.Executor, self, true)) {

                    MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.any, Filters.Executor, self, true);
                    action.setText("Move from Executor to here");
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Main_Corridor,
                new ControlsCondition(playerOnLightSideOfLocation, self), game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}