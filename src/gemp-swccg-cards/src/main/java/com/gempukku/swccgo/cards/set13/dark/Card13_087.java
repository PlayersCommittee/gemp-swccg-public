package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Character
 * Subtype: Imperial
 * Title: Stormtrooper Garrison
 */
public class Card13_087 extends AbstractImperial {
    public Card13_087() {
        super(Side.DARK, 4, 6, 8, 1, 4, "Stormtrooper Garrison", Uniqueness.UNRESTRICTED, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setArmor(3);
        setLore("The Imperial Navy's ruthless shock troops. The distinctive white armor of the stormtrooper is recognized throughout the galaxy as a feared occupational force.");
        setGameText("Imperial leaders present may not be targeted by weapons. While with an Imperial leader, adds one battle destiny. While at an opponent's site, Force drain +1 here (or +2 if also a Rebel Base). End of your turn: Use 1 Force to maintain OR Lose 1 Force to place in Used Pile OR Place out of play.");
        addIcons(Icon.REFLECTIONS_III, Icon.WARRIOR, Icon.MAINTENANCE);
        addKeywords(Keyword.STORMTROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.and(Filters.Imperial, Filters.leader, Filters.present(self))));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.and(Filters.Imperial, Filters.leader)), 1));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new AtCondition(self, Filters.and(Filters.opponents(self), Filters.site)),
                new CardMatchesEvaluator(1, 2, Filters.Rebel_Base_location), self.getOwner()));
        return modifiers;
    }

    @Override
    protected StandardEffect getGameTextMaintenanceMaintainCost(Action action, final String playerId) {
        return new UseForceEffect(action, playerId, 1);
    }

    @Override
    protected StandardEffect getGameTextMaintenanceRecycleCost(Action action, final String playerId) {
        return new LoseForceEffect(action, playerId, 1, true);
    }
}
