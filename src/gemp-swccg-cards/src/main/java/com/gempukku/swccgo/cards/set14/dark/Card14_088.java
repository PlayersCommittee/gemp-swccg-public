package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Sil Unch
 */
public class Card14_088 extends AbstractRepublic {
    public Card14_088() {
        super(Side.DARK, 2, 2, 2, 2, 4, "Sil Unch", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("Neimoidian Trade Federation Droid Control Ship officer. Specialized in battle droid control programming and interfaces. Does not enjoy being commanded by Daultay Dofine.");
        setGameText("Adds 3 to power of anything he pilots. While aboard a battleship, that battleship is immune to attrition < 4 and draws a battle destiny if not able to otherwise.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT);
        setSpecies(Species.NEIMOIDIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter battleshipAboard = Filters.and(Filters.battleship, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, battleshipAboard, 4));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, battleshipAboard, 1));
        return modifiers;
    }
}
