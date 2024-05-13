package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
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
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Alien
 * Title: Vigo
 */
public class Card10_053 extends AbstractAlien {
    public Card10_053() {
        super(Side.DARK, 3, 3, 3, 2, 4, Title.Vigo, Uniqueness.RESTRICTED_3, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setLore("One of Xizor's hand-picked lieutenants. Ascended as Black Sun agent from gangster to manager. Earned title of Vigo from old Tionese for 'nephew'.");
        setGameText("Deploys for free on Coruscant. Adds 2 to power of anything he pilots. While at a non-Coruscant battleground site and Vengeance Of The Dark Prince is on table, Force drain +1 here. Forfeit +2 when present with Xizor or another Vigo.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GANGSTER, Keyword.BLACK_SUN_AGENT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Deploys_on_Coruscant));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new AndCondition(new AtCondition(self,
                Filters.and(Filters.battleground_site, Filters.not(Filters.Coruscant_site))),
                new OnTableCondition(self, Filters.Vengeance_Of_The_Dark_Prince)), 1, self.getOwner()));
        modifiers.add(new ForfeitModifier(self, new PresentWithCondition(self, Filters.or(Filters.Xizor,
                Filters.and(Filters.other(self), Filters.Vigo))), 2));
        return modifiers;
    }
}
