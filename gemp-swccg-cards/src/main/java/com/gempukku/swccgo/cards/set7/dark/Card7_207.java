package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Swoop Mercenary
 */
public class Card7_207 extends AbstractAlien {
    public Card7_207() {
        super(Side.DARK, 3, 2, 2, 1, 3, "Swoop Mercenary");
        setLore("Members of swoop gangs on Tatooine often have experience piloting larger craft. Many wear tall shock-helmets.");
        setGameText("Adds 2 to power of anything he pilots or drives and, when driving a swoop, adds 3 to landspeed. When present at a site with another Swoop Mercenary, may draw one battle destiny if not able to otherwise.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsPowerToDrivenBySelfModifier(self, 2));
        modifiers.add(new LandspeedModifier(self, Filters.and(Filters.swoop, Filters.hasDriving(self)), 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AndCondition(new PresentAtCondition(self, Filters.site),
                new PresentWithCondition(self, Filters.Swoop_Mercenary)), 1));
        return modifiers;
    }
}
