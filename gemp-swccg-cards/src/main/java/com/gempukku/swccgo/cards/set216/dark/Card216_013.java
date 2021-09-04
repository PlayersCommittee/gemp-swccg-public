package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.HasAttachedCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AttemptToBlowAwayShieldGateTotalModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Location
 * Subtype: System
 * Title: Scarif
 */
public class Card216_013 extends AbstractSystem {
    public Card216_013() {
        super(Side.DARK, Title.Scarif, 7);
        setLocationDarkSideGameText("While Shield Gate here, opponent's non-spy characters deploy +1 to Scarif sites.");
        setLocationLightSideGameText("While Profundity here, add 2 to attempts to 'blow away' Shield Gate.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition shieldGateHere = new HasAttachedCondition(self, Filters.Shield_Gate);
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.not(Filters.spy), Filters.character),
                shieldGateHere, 1, Filters.Scarif_site));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AttemptToBlowAwayShieldGateTotalModifier(self, new HereCondition(self, Filters.Profundity), 2));
        return modifiers;
    }
}
