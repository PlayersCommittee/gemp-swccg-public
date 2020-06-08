package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayNotAttackTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationToLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Creature
 * Title: Bubo
 */
public class Card6_138 extends AbstractCreature {
    public Card6_138() {
        super(Side.DARK, 4, 3, 4, 5, 0, "Bubo", Uniqueness.UNIQUE);
        setLore("Watchbeast. Unwittingly foiled Ree Yees' plot to kill Jabba with a thermal detonator when it ate a crucial component. Louder than it is tough. Keeps watch for unwary intruders.");
        setGameText("Habitat: planet sites (except Hoth). Does not attack your characters. When at a Jabba's palace site, prevents opponents characters present from using their landspeed.");
        addModelType(ModelType.GUARD);
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.planet_site, Filters.except(Filters.Hoth_site));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotAttackTargetModifier(self, Filters.and(Filters.your(self), Filters.character)));
        modifiers.add(new MayNotMoveFromLocationToLocationUsingLandspeedModifier(self, Filters.and(Filters.opponents(self), Filters.character,
                Filters.present(self)), new AtCondition(self, Filters.Jabbas_Palace_site), Filters.sameLocation(self), Filters.any));
        return modifiers;
    }
}
