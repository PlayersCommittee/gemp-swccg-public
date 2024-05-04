package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.InSenateMajorityCondition;
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
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Aks Moe
 */
public class Card12_097 extends AbstractRepublic {
    public Card12_097() {
        super(Side.DARK, 2, 2, 3, 3, 5, "Aks Moe", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setPolitics(3);
        setLore("Gran senator from Malastare. A skillful politician who demanded that a commission be sent to Naboo to investigate the alleged occupation there.");
        setGameText("Agendas: ambition, blockade. While in a senate majority, your capital starships are each power +2, and your Force drains are +1 at each battleground system where you occupy a related site.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.SENATOR);
        setSpecies(Species.GRAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.AMBITION, Agenda.BLOCKADE));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition inSenateMajority = new InSenateMajorityCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.capital_starship), inSenateMajority, 2));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.battleground_system, Filters.relatedLocationTo(self,
                Filters.and(Filters.site, Filters.occupies(playerId)))), inSenateMajority, 1, playerId));
        return modifiers;
    }
}
