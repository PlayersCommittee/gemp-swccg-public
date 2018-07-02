package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttackRunTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Yavin 4: Massassi Headquarters
 */
public class Card7_134 extends AbstractSite {
    public Card7_134() {
        super(Side.LIGHT, Title.Massassi_Headquarters, Title.Yavin_4);
        setLocationDarkSideGameText("May not be separated from interior Yavin 4 sites. If you occupy, Attack Run total is -4.");
        setLocationLightSideGameText("May not be separated from interior Yavin 4 sites. If you control, Attack Run total is +2.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttackRunTotalModifier(self, new OccupiesCondition(playerOnDarkSideOfLocation, self), -4));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttackRunTotalModifier(self, new ControlsCondition(playerOnLightSideOfLocation, self), 2));
        return modifiers;
    }
}