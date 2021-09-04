package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: System
 * Title: Bothawui
 */
public class Card7_110 extends AbstractSystem {
    public Card7_110() {
        super(Side.LIGHT, Title.Bothawui, 2);
        setLocationDarkSideGameText("Force drain -X here, where X = number of spies opponent has on table.");
        setLocationLightSideGameText("Your spies deploy -1 here and at related sites. If you control, characters targeted by Undercover are immune to Hutt Smooch.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.SPECIAL_EDITION, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new NegativeEvaluator(new OnTableEvaluator(self, SpotOverride.INCLUDE_UNDERCOVER,
                Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.spy))), playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.spy),
                -1, Filters.or(self, Filters.relatedSite(self))));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.character, Filters.hasAttached(Filters.Undercover)), new ControlsCondition(playerOnLightSideOfLocation, self), Title.Hutt_Smooch));
        return modifiers;
    }
}