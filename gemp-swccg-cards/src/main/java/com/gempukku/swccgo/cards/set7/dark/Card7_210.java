package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Ur'Ru'r
 */
public class Card7_210 extends AbstractAlien {
    public Card7_210() {
        super(Side.DARK, 3, 2, 2, 1, 2, "Ur'Ru'r", Uniqueness.UNIQUE);
        setLore("Fierce Tusken Raider. Ransacks homes and moisture farms on the borders of Mos Espa.");
        setGameText("Deploys only on Tatooine. While at Tusken Canyon, Jundland Wastes or same Tatooine site as UroRRuR'R'R, adds 2 to each of your battle destiny draws at Tatooine sites where you have a Tusken Raider.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        setSpecies(Species.TUSKEN_RAIDER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, Filters.and(Filters.Tatooine_site, Filters.sameSiteAs(self,
                Filters.and(Filters.your(self), Filters.Tusken_Raider))), new AtCondition(self, Filters.or(Filters.Tusken_Canyon,
                Filters.Jundland_Wastes, Filters.and(Filters.Tatooine_site, Filters.sameSiteAs(self, Filters.URoRRuRRR)))),
                2, self.getOwner(), true));
        return modifiers;
    }
}
