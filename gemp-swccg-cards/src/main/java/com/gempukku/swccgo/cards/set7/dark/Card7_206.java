package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
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
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Torent
 */
public class Card7_206 extends AbstractImperial {
    public Card7_206() {
        super(Side.DARK, 3, 2, 2, 2, 2, "Sergeant Torent", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Senior watch trooper at Death Star's command center. Monitors external sensor data, scanning for Rebel activity. Vigilant in his duties.");
        setGameText("Deploys -2 on Death Star. When in battle, adds 1 to your total battle destiny for each of your Death Star troopers present. While on Death Star, adds 1 to each of your Force drains at a battleground related to system Death Star orbits.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.DEATH_STAR_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_on_Death_Star));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, new InBattleCondition(self), new PresentEvaluator(self,
                Filters.and(Filters.your(self), Filters.other(self), Filters.Death_Star_trooper)), playerId));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.battleground, Filters.relatedLocationTo(self,
                Filters.isOrbitedBy(Filters.Death_Star_system))), new OnCondition(self, Title.Death_Star), 1, playerId));
        return modifiers;
    }
}
