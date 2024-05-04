package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Fozec
 */
public class Card6_103 extends AbstractAlien {
    public Card6_103() {
        super(Side.DARK, 2, 2, 1, 1, 2, "Fozec", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Spy for the Empire. Keeping tabs on Jabba's activities for the ISB. Secretly hoping to leave the Empire and pursue lucrative opportunities in the underworld.");
        setGameText("Adds 2 to power of anything he pilots. While at a site you control, Imperials are immune to Ke Chu Ke Kukuta? at that site.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.Imperial, Filters.atSameSite(self)),
                new AtCondition(self, Filters.and(Filters.site, Filters.controls(self.getOwner()))), Title.Ke_Chu_Ke_Kukuta));
        return modifiers;
    }
}
