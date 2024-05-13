package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
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
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Lieutenant Page
 */
public class Card8_019 extends AbstractRebel {
    public Card8_019() {
        super(Side.LIGHT, 2, 3, 3, 3, 4, Title.Lieutenant_Page, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Scout who served under General Veers' command. Defected and was recruited by General Madine's commando organization. Works closely with Colonel Cracken.");
        setGameText("Adds 1 to each of your battle destiny draws when with Derlin, Cracken or Madine at a site (or when with at least two of your other scouts at an exterior planet site). Your Rebel scouts deploy -1 to same exterior site (except Rebel Landing Site).");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, new OrCondition(new WithCondition(self, Filters.or(Filters.Derlin, Filters.Cracken, Filters.Madine)),
                new AndCondition(new WithCondition(self, 2, Filters.scout), new AtCondition(self, Filters.exterior_planet_site))), 1, self.getOwner()));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.Rebel_scout),
                new AtCondition(self, Filters.and(Filters.exterior_site, Filters.not(Filters.Rebel_Landing_Site))),
                -1, Filters.sameSite(self)));
        return modifiers;
    }
}
