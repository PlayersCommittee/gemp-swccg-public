package com.gempukku.swccgo.cards.set225.light;

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
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Defensive Shield
 * Title: Let's Keep A Little Optimism Here (V)
 */
public class Card225_051 extends AbstractDefensiveShield {
    public Card225_051() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Lets_Keep_A_Little_Optimism_Here, ExpansionSet.SET_25, Rarity.V);
        setLore("The heroes of the Rebellion know that where there is life, there is hope.");
        setGameText("Plays on table. Do They Have A Code Clearance? does not modify forfeit values. While you occupy a Renegade planet location, operatives are forfeit = 0, operatives do not add to Force drains and your Force drains may not be reduced.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_DEFENSIVE_SHIELD);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition occupyRenegadePlanetLocation = new OccupiesCondition(playerId, Filters.Renegade_planet_location);
        Filter operatives = Filters.and(Filters.operative, Filters.canBeTargetedBy(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Do_They_Have_A_Code_Clearance, ModifyGameTextType.DO_THEY_HAVE_A_CODE_CLEARANCE__DOESNT_MODIFY_FORFEIT));
        modifiers.add(new ResetForfeitModifier(self, operatives, occupyRenegadePlanetLocation, 0));
        modifiers.add(new CancelForceDrainBonusesFromCardModifier(self, operatives, occupyRenegadePlanetLocation));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, occupyRenegadePlanetLocation, playerId));
        return modifiers;
    }
}