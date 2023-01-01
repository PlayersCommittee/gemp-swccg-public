package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Kin Kian
 */
public class Card9_020 extends AbstractRebel {
    public Card9_020() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, Title.Kian, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Rogue Squadron pilot. Assigned as gunner aboard Colonel Salm's Y-wing at Battle of Endor, as part of Gray Squadron. Former member of Aggressor Squadron.");
        setGameText("Adds 1 to power of anything he pilots. While aboard your starship, adds 2 to each of its weapon destiny draws. While aboard a unique (•) Gray Squadron Y-wing at a system or sector, adds 1 to each of your Force drains there.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ROGUE_SQUADRON, Keyword.GUNNER, Keyword.GRAY_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, new AboardCondition(self, Filters.and(Filters.your(self),
                Filters.starship)), Filters.hasAboard(self), 2, Filters.any));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new AndCondition(new AboardCondition(self,
                Filters.and(Filters.unique, Filters.Gray_Squadron_Y_wing)), new AtCondition(self, Filters.system_or_sector)),
                1, self.getOwner()));
        return modifiers;
    }
}
