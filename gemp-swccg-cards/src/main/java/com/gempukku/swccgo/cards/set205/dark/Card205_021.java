package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 5
 * Type: Starship
 * Subtype: Starfighter
 * Title: Captain Jonus In Scimitar 2
 */
public class Card205_021 extends AbstractStarfighter {
    public Card205_021() {
        super(Side.DARK, 2, 2, 4, null, 2, null, 4, "Captain Jonus In Scimitar 2", Uniqueness.UNIQUE);
        setLore("TIE bomber repaired after being struck by an asteroid in the Anoat system. Stationed aboard second Death Star battle station.");
        setGameText("Permanent pilot is •Jonus, who provides ability of 2. This starship's missile weapon destiny draws are +1. If at a system you control, your total power at related sites is +1.");
        addPersona(Persona.SCIMITAR_2);
        addIcons(Icon.PILOT, Icon.VIRTUAL_SET_5);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.SCIMITAR_SQUADRON);
        addModelType(ModelType.TIE_SA);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.JONUS, 2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 1, Filters.missile));
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.relatedSite(self),
                Filters.locationWherePowerCanBeAddedInBattleFromStarshipsControllingSystem(playerId)),
                new AtCondition(self, Filters.and(Filters.system, Filters.controls(playerId))), 1, playerId));
        return modifiers;
    }
}
