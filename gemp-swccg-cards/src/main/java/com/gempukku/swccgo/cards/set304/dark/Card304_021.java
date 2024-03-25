package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Dark Jedi Master
 * Title: Darth Renatus, Deputy Grandmaster
 */
public class Card304_021 extends AbstractDarkJediMaster {
    public Card304_021() {
        super(Side.DARK, 6, 7, 3, 7, 8, "Darth Renatus, Deputy Grandmaster", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Following Dacien's ascendent to the Iron Throne, Atra took on the role of Deputy Grandmaster. In his new leader role he is focused on ensuring the Brotherhood is protected from all threats. ");
        setGameText("May use two weapons. Force Drains by Atra at an Arx locations are +1 for each Councilor present at same site, including captives. While armed with a weapon at a site, Force drain +1 here.");
		addIcon(Icon.WARRIOR, 2);
        addKeywords(Keyword.LEADER, Keyword.DARK_COUNCILOR);
		addPersona(Persona.ATRA);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atArx = new AtCondition(self, Filters.Arx_location);

        List<Modifier> modifiers = new LinkedList<Modifier>();
		modifiers.add(new ForceDrainModifier(self, Filters.sameSite(self), new ArmedWithCondition(self, Filters.lightsaber),
                1, self.getOwner()));
		modifiers.add(new MayForceDrainModifier(self, atArx));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), atArx,
                new PresentEvaluator(self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.other(self), Filters.Dark_Councilor)),
                self.getOwner()));
        return modifiers;
    }
}
