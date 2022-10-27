package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.DuringPlayersTurnNumberCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Defensive Shield
 * Title: Goldenrod
 */
public class Card200_027 extends AbstractDefensiveShield {
    public Card200_027() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE,"Goldenrod", ExpansionSet.SET_0, Rarity.V);
        setGameText("Plays on table. For opponent to deploy a character, starship, or vehicle for free (except by that card's own game text), opponent must first use 2 Force.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle)), 2));
        modifiers.add(new SuspendsCardModifier(self, Filters.title(Title.They_Must_Never_Again_Leave_This_City), new DuringPlayersTurnNumberCondition(game.getOpponent(self.getOwner()), 1)));
        return modifiers;
    }
}