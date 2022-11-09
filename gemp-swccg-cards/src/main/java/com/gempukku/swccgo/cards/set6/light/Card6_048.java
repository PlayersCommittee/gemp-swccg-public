package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
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
 * Title: Worrt
 */
public class Card6_048 extends AbstractCreature {
    public Card6_048() {
        super(Side.LIGHT, 4, 2, 3, 4, 0, "Worrt", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("Large, froglike creature often used by Jawas and other Tatooine denizens as guards. Keeps raiding parties and other unwelcomed guests at bay.");
        setGameText("Habitat: planet sites (except Hoth). Does not attack your characters. When at a Tatooine site, prevents opponent's characters present from using their landspeed.");
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
                Filters.present(self)), new AtCondition(self, Filters.Tatooine_site), Filters.sameLocation(self), Filters.any));
        return modifiers;
    }
}
