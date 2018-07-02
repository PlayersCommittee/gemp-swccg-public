package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Effect
 * Title: Astromech Shortage
 */
public class Card2_116 extends AbstractNormalEffect {
    public Card2_116() {
        super(Side.DARK, 3, PlayCardZoneOption.OPPONENTS_SIDE_OF_TABLE, Title.Astromech_Shortage, Uniqueness.UNIQUE);
        setLore("Imperial pilots often target astromech aboard Rebel starfighters in an attempt to prevent hyper-escapes. Scarcity of undamaged astromechs can delay starfighter deployment.");
        setGameText("Use 3 Force to deploy on opponent's side of table. All opponent's starships with a [Nav Computer] icon are deploy +1.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.opponents(self), Filters.starship, Icon.NAV_COMPUTER), 1));
        return modifiers;
    }
}