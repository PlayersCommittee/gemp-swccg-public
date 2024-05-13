package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeAttackedByModifier;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.logic.GameUtils;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Alien
 * Title: Mob of Crystal Troopers
 */
public class Card302_042 extends AbstractAlien {
    public Card302_042() {
        super(Side.LIGHT, 3, null, 3, 3, 6, "Mob of Crystal Troopers", Uniqueness.UNRESTRICTED, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("Infected troopers over time found crystals growing from their bodies. As their bodies became deformed they became mindless fodder for the Children of Mortis.");
        setGameText("* Replaces 3 Infected Troopers at one location (Infected Troopers go to Used Pile). Total power at same site is +1 for each of your Children of Mortis characters or Crystal Creatures present. Cannot be targeted by Crystal Creatures.");
        addKeywords(Keyword.CHILDREN_OF_MORTIS);
		setReplacementForSquadron(3, Filters.Infected_Trooper);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
		modifiers.add(new PowerModifier(self, new HereEvaluator(self, Filters.and(Filters.other(self), Filters.or(Filters.CHILDREN_OF_MORTIS, Filters.CRYSTAL_CREATURE)))));
		modifiers.add(new MayNotBeAttackedByModifier(self, Filters.CRYSTAL_CREATURE));
        return modifiers;
	}
	
	@Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_NA;
    }
}