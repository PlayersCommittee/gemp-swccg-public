package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.MayNotBeConvertedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Detention Block Control Room
 */
public class Card7_280 extends AbstractSite {
    public Card7_280() {
        super(Side.DARK, Title.Detention_Block_Control_Room, Title.Death_Star);
        setLocationDarkSideGameText("If you occupy with an Imperial, this site may not be converted.");
        setLocationLightSideGameText("If you control, you may raise converted Detention Block Control Room to the top.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeConvertedModifier(self, new OccupiesWithCondition(playerOnDarkSideOfLocation, self, Filters.Imperial)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.controls(game, playerOnDarkSideOfLocation, self)) {
            PhysicalCard controlRoomToConvert = Filters.findFirstFromTopLocationsOnTable(game,
                    Filters.and(Filters.Detention_Block_Control_Room, Filters.canBeConvertedByRaisingLocationToTop(playerOnDarkSideOfLocation)));
            if (controlRoomToConvert != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
                action.setText("Raise converted Detention Block Control Room");
                action.setActionMsg("Raise converted " + GameUtils.getCardLink(controlRoomToConvert) + " to the top");
                // Perform result(s)
                action.appendEffect(
                        new ConvertLocationByRaisingToTopEffect(action, controlRoomToConvert, false));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}