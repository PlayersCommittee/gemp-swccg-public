package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Biker Scout Gear
 */
public class Card8_119 extends AbstractNormalEffect {
    public Card8_119() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Biker Scout Gear", Uniqueness.UNIQUE);
        setLore("Standard-issue equipment for Imperial biker scouts. Adds electromagnetic vision enhancement visor and boosted comlink. Protects with lightweight head and upper body armor.");
        setGameText("Deploy on your side of table. Biker scouts have armor = 3 and are immune to Scramble. Also, Scout Blaster, Comlink, DH-17 blaster and Blaster Rack deploy for free and are destiny +2 when drawn for weapon or battle destiny (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.or(Filters.scout_blaster, Filters.Comlink, Filters.DH17_blaster, Filters.Blaster_Rack);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetArmorModifier(self, Filters.biker_scout, 3));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.biker_scout, Title.Scramble));
        modifiers.add(new DeploysFreeModifier(self, filter));
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, filter, 2));
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, filter, 2));
        return modifiers;
    }
}