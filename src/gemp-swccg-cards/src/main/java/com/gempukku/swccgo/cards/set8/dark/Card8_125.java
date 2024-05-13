package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.PresentInBattleEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Imperial Academy Training
 */
public class Card8_125 extends AbstractNormalEffect {
    public Card8_125() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Imperial Academy Training", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.C);
        setLore("Graduates of the Empire's military academies train to fire standard-issue weapons efficiently.");
        setGameText("Deploy on your side of table. Each of your non-unique Imperials armed with a non-unique blaster is forfeit +2, adds 1 to his total weapon destiny and, where present, cumulatively adds 1 to total battle destiny. (Immune to Alter while your non-unique blaster is on table.)");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter armedImperialFilter = Filters.and(Filters.your(self), Filters.non_unique, Filters.Imperial, Filters.armedWith(Filters.and(Filters.non_unique, Filters.blaster)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, armedImperialFilter, 2));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.any, armedImperialFilter, 1));
        modifiers.add(new TotalBattleDestinyModifier(self, new DuringBattleAtCondition(Filters.wherePresent(self, armedImperialFilter)),
                new PresentInBattleEvaluator(self, armedImperialFilter), playerId));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new OnTableCondition(self, Filters.and(Filters.your(self), Filters.non_unique, Filters.blaster)), Title.Alter));
        return modifiers;
    }
}