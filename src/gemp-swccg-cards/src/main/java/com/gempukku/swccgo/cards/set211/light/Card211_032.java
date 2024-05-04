package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PlaceInUsedPileWhenCanceledModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Location
 * Subtype: System
 * Title: Yavin 4 (V)
 */
public class Card211_032 extends AbstractSystem {
    public Card211_032() {
        super(Side.LIGHT, Title.Yavin_4, 4, ExpansionSet.SET_11, Rarity.V);
        setLocationDarkSideGameText("Unless you control, you may not deploy Effects on related locations.");
        setLocationLightSideGameText("If Haven is canceled, place it in your Used Pile.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_11);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToTargetModifier(self, Filters.and(Filters.owner(playerOnDarkSideOfLocation), Filters.Effect),
                new UnlessCondition(new ControlsCondition(playerOnDarkSideOfLocation, Filters.Yavin_4_system)), Filters.relatedLocation(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PlaceInUsedPileWhenCanceledModifier(self, Filters.Haven));
        return modifiers;
    }
}