package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Ric Olie, Bravo Leader
 */
public class Card14_029 extends AbstractRepublic {
    public Card14_029() {
        super(Side.LIGHT, 2, 3, 3, 3, 6, "Ric Olie, Bravo Leader", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setLore("Leader of Bravo Squadron's attack on the Trade Federation Droid Control Ship at the Battle of Naboo. With the assistance of Anakin Skywalker, his squadron succeeded.");
        setGameText("Adds 3 to power of anything he pilots. While piloting during a battle at a system where you have two piloted Bravo Squadron starfighters, adds two battle destiny.");
        addPersona(Persona.RIC);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.BRAVO_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new PilotingCondition(self, Filters.any),
                new DuringBattleAtCondition(Filters.system), new HereCondition(self, 2, Filters.and(Filters.your(self),
                Filters.piloted, Filters.Bravo_Squadron_starfigher))), 2));
        return modifiers;
    }
}
