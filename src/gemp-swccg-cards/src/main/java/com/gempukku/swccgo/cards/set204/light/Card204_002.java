package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractRebelRepublic;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.InSenateMajorityCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Rebel/Republic
 * Title: Bail Organa
 */
public class Card204_002 extends AbstractRebelRepublic {
    public Card204_002() {
        super(Side.LIGHT, 3, 2, 3, 3, 5, "Bail Organa", Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setPolitics(1);
        setLore("Alderaanian senator and leader.");
        setGameText("Agenda: rebellion. While in a senate majority (or Stolen Data Tapes on table), your Force drains at battlegrounds with your Alderaanian of ability < 4 are +1. While aboard a corvette, adds one battle destiny and it may not be targeted by weapons.");
        addIcons(Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.SENATOR, Keyword.LEADER);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.REBELLION));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition aboardCorvette = new AboardCondition(self, Filters.corvette);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.battleground, Filters.sameLocationAs(self,
                Filters.and(Filters.your(self), Filters.Alderaanian, Filters.abilityLessThan(4)))),
                new OrCondition(new InSenateMajorityCondition(self), new OnTableCondition(self, Filters.Stolen_Data_Tapes)), 1, playerId));
        modifiers.add(new AddsBattleDestinyModifier(self, aboardCorvette, 1));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.and(Filters.corvette, Filters.hasAboard(self)), aboardCorvette));
        return modifiers;
    }
}
