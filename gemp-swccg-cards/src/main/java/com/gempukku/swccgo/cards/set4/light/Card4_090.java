package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: System
 * Title: Raithal
 */
public class Card4_090 extends AbstractSystem {
    public Card4_090() {
        super(Side.LIGHT, Title.Raithal, 3);
        setLocationDarkSideGameText("If you control, you may raise your converted Raithal system to the top.");
        setLocationLightSideGameText("If you control, all opponent's troopers are forfeit and destiny -1.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.DAGOBAH, Icon.PLANET);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.controls(game, playerOnDarkSideOfLocation, self)
                && Filters.canBeConvertedByRaisingYourLocationToTop(playerOnDarkSideOfLocation).accepts(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
            action.setText("Raise converted system");
            action.setActionMsg("Raise converted " + GameUtils.getCardLink(self) + " to the top");
            // Perform result(s)
            action.appendEffect(
                    new ConvertLocationByRaisingToTopEffect(action, self, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition youControl = new ControlsCondition(playerOnLightSideOfLocation, self);
        Filter opponentsTroopers = Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.trooper);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, opponentsTroopers, youControl, -1));
        modifiers.add(new DestinyModifier(self, opponentsTroopers, youControl, -1));
        return modifiers;
    }
}