package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCloakModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotResetTotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Starship
 * Subtype: Starfighter
 * Title: Madakor In Radiant VII
 */
public class Card200_063 extends AbstractStarfighter {
    public Card200_063() {
        super(Side.LIGHT, 2, 5, 6, 5, null, 4, 7, "Madakor In Radiant VII", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setLore("Optimized for diplomatic missions with sensor-proof pods that have ejection capabilities. Easily identified by its red coloration.");
        setGameText("May add 1 pilot and 2 passengers. Permanent pilot is •Madakor, who provides ability of 2. Opponent's starships may not 'cloak' (or reset your total battle destiny) here. Immune to attrition < 4.");
        addPersona(Persona.RADIANT_VII);
        addIcons(Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_0);
        addModelType(ModelType.CORELLIAN_REPUBLIC_CRUISER);
        setPilotCapacity(1);
        setPassengerCapacity(2);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.MADAKOR, 2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotCloakModifier(self, Filters.and(Filters.opponents(self), Filters.starship)));
        modifiers.add(new MayNotResetTotalBattleDestinyModifier(self, Filters.here(self), playerId, opponent));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
