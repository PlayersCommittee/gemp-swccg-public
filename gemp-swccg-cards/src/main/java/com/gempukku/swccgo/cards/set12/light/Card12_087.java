package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Watto's Junkyard
 */
public class Card12_087 extends AbstractSite {
    public Card12_087() {
        super(Side.LIGHT, Title.Wattos_Junkyard, Title.Tatooine);
        setLocationDarkSideGameText("If Watto present, Force drain +1 here and opponent's battle destiny draws here are -2.");
        setLocationLightSideGameText("While you occupy, Force generation +2 here. Immune to Revolution.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition wattoPresent = new PresentCondition(self, Filters.Watto);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, wattoPresent, 1, playerOnDarkSideOfLocation));
        modifiers.add(new EachBattleDestinyModifier(self, self, wattoPresent, -2, game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, new OccupiesCondition(playerOnLightSideOfLocation, self), 2, playerOnLightSideOfLocation));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }
}