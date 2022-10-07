package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: System
 * Title: Malachor
 */
public class Card219_036 extends AbstractSystem {
    public Card219_036() {
        super(Side.LIGHT, Title.Malachor, 6);
        setLocationDarkSideGameText("While you control, Ezra is power -2 and may not apply ability toward drawing battle destiny.");
        setLocationLightSideGameText("While you control with a Phoenix Squadron character, Vader is power -2 and his weapon destiny draws are -1.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.Ezra, new ControlsCondition(playerOnDarkSideOfLocation, self), -2));
        modifiers.add(new MayNotApplyAbilityForBattleDestinyModifier(self, Filters.Ezra, new ControlsCondition(playerOnDarkSideOfLocation, self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition controlsWithAPhoenixSquadronCharacter = new ControlsWithCondition(playerOnLightSideOfLocation, self, Filters.Phoenix_Squadron_character);
        modifiers.add(new PowerModifier(self, Filters.Vader, controlsWithAPhoenixSquadronCharacter, -2));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.weapon, controlsWithAPhoenixSquadronCharacter, Filters.Vader, -1));
        return modifiers;
    }
}
