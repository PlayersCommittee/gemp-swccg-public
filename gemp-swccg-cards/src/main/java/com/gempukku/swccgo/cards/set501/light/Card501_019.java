package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.HasSenateMajorityCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeSuspendedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

import static com.gempukku.swccgo.common.Title.Plea_To_The_Court;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Rebel
 * Title: Leia (V) (Errata)
 */
public class Card501_019 extends AbstractRebel {
    public Card501_019() {
        super(Side.LIGHT, 1, 3, 3, 4, 5, "Leia", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Adopted daughter of the Viceroy and First Chairman of Alderaan. Became a political leader at a young age. The injustices of the New Order led her to join the Rebellion.");
        setGameText("Senator. Agenda: Rebellion. Adds one battle destiny if you have a Senate majority. If you have exactly one Political Effect on table (except Plea to the Court), it may not be suspended.");
        addPersona(Persona.LEIA);
        addIcons(Icon.PREMIUM, Icon.WARRIOR, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.LEADER, Keyword.FEMALE, Keyword.SENATOR);
        setSpecies(Species.ALDERAANIAN);
        setTestingText("Leia (v) Errata");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AgendaModifier(self, Agenda.REBELLION));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsBattleDestinyModifier(self, new HasSenateMajorityCondition(playerId), 1));
        Condition politicalEffectCondition = new OnTableCondition(self, 1, true, Filters.and(Filters.your(playerId), Filters.Political_Effect));
        Filter politicalEffectFilter = Filters.and(Filters.your(playerId), Filters.Political_Effect, Filters.except(Filters.title(Plea_To_The_Court)));
        modifiers.add(new MayNotBeSuspendedModifier(self, politicalEffectFilter, politicalEffectCondition));
        return modifiers;
    }
}
