package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Corporal Drelosyn
 */
public class Card8_097 extends AbstractImperial {
    public Card8_097() {
        super(Side.DARK, 3, 2, 1, 2, 3, "Corporal Drelosyn", Uniqueness.UNIQUE);
        setLore("Stormtrooper from Coruscant. Honed piloting skills by racing swoops in underworld of Imperial capital. Still tempering his skills as a biker scout.");
        setGameText("Adds 3 to power of any speeder bike he pilots or anything he drives. When in battle with Irol at a site, adds one battle destiny. Power +1 at any mobile, interior or spaceport site.");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.BIKER_SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.speeder_bike));
        modifiers.add(new AddsPowerToDrivenBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new InBattleWithCondition(self, Filters.Irol),
                new AtCondition(self, Filters.site)), 1));
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.mobile_site, Filters.interior_site,
                Filters.spaceport_site)), 1));
        return modifiers;
    }
}
