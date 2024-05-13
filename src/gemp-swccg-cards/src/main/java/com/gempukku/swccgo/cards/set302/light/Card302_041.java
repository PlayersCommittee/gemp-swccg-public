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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeAttackedByModifier;



import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Alien
 * Title: Infected Trooper
 */
public class Card302_041 extends AbstractAlien {
    public Card302_041() {
        super(Side.LIGHT, 1, 2, 1, 1, .5, Title.Infected_Trooper, Uniqueness.UNRESTRICTED, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("Ritobas was a regular trooper in the Brotherhood before she was infected by the crystal mist. As her mind bent to the will of the Children of Mortis she began to notice physical changes...");
        setGameText("Total power at same site is +1 for each of your Children of Mortis characters or Crystal Creatures present. Crystal Creatures at same site do not attack and cannot be attacked.");
        addKeywords(Keyword.CHILDREN_OF_MORTIS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
		modifiers.add(new PowerModifier(self, new HereEvaluator(self, Filters.and(Filters.other(self), Filters.or(Filters.CHILDREN_OF_MORTIS, Filters.CRYSTAL_CREATURE)))));
		modifiers.add(new MayNotBeAttackedByModifier(self, Filters.CRYSTAL_CREATURE));
        return modifiers;
	}
}