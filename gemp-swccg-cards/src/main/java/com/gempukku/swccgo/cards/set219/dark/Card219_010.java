package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: System
 * Title: Lothal
 */
public class Card219_010 extends AbstractSystem {
    public Card219_010() {
        super(Side.DARK, Title.Lothal, 6, ExpansionSet.SET_19, Rarity.V);
        setLocationDarkSideGameText("While you control, at related battleground sites you control with an Imperial leader, your Force drains are +1.");
        setLocationLightSideGameText("If Ghost and Phantom piloted here, Force drain +1 here. If no Rebel starships piloted here, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition youControl = new ControlsCondition(playerOnDarkSideOfLocation, self);
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.relatedLocation(self), Filters.battleground, Filters.controlsWith(playerOnDarkSideOfLocation, self, Filters.Imperial_leader)), youControl, 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        AndCondition ghostAndPhantomHereCondition = new AndCondition(new HereCondition(self, Filters.Ghost), new HereCondition(self, Filters.Phantom));
        NotCondition noPilotedRebelStarshipsHereCondition = new NotCondition(new HereCondition(self, Filters.and(Filters.piloted, Filters.Rebel_starship)));
        modifiers.add(new ForceDrainModifier(self, ghostAndPhantomHereCondition, 1, playerOnLightSideOfLocation));
        modifiers.add(new ForceDrainModifier(self, noPilotedRebelStarshipsHereCondition, -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}