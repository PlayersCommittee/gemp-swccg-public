package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Ice Plains (5th Marker)
 */
public class Card3_148 extends AbstractSite {
    public Card3_148() {
        super(Side.DARK, Title.Ice_Plains, Title.Hoth);
        setLocationDarkSideGameText("During your move phase, you may move free from here directly to Mountains (or vice versa).");
        setLocationLightSideGameText("If you control, and Main Power Generators on table, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.MARKER_5);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.MOVE)
                && GameConditions.canSpotLocation(game, Filters.Mountains)) {
            if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.any, self, Filters.Mountains, true)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.any, self, Filters.Mountains, true);
                action.setText("Move from here to Mountains");
                actions.add(action);
            }
            if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.any, Filters.Mountains, self, true)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.any, Filters.Mountains, self, true);
                action.setText("Move from Mountains to here");
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new AndCondition(new ControlsCondition(playerOnLightSideOfLocation, self),
                new OnTableCondition(self, Filters.Main_Power_Generators)), -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}