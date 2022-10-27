package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: Leave Them To Me
 */
public class Card13_072 extends AbstractDefensiveShield {
    public Card13_072() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Leave_Them_To_Me, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("'I will deal with them myself.'");
        setGameText("Plays on table. While you occupy a Subjugated planet location, operatives are forfeit = 0, operatives do not add to Force drains, and your Force drains may not be reduced. (Subjugated planet is defined on the Objective card Local Uprising.)");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition occupySubjugatedPlanetLocation = new OccupiesCondition(playerId, Filters.Subjugated_planet_location);
        Filter operatives = Filters.and(Filters.operative, Filters.canBeTargetedBy(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetForfeitModifier(self, operatives, occupySubjugatedPlanetLocation, 0));
        modifiers.add(new CancelForceDrainBonusesFromCardModifier(self, operatives, occupySubjugatedPlanetLocation));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, occupySubjugatedPlanetLocation, playerId));
        return modifiers;
    }
}