package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Vehicle
 * Subtype: Combat
 * Title: Sandspeeder
 */
public class Card7_156 extends AbstractCombatVehicle {
    public Card7_156() {
        super(Side.LIGHT, 1, 2, 3, null, 4, 4, 4, "Sandspeeder");
        setLore("Enclosed Incom T-47 airspeeder adapted for use in hot, dry conditions. Maximum speed 600 kilometers per hour. Drive section left exposed for easier maintenance.");
        setGameText("May add 1 pilot or passenger. Permanent pilot provides ability of 1. May move as a 'react' to Tatooine or desert sites.");
        addModelType(ModelType.T_47);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.ENCLOSED, Keyword.SANDSPEEDER);
        setPilotOrPassengerCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationModifier(self, Filters.or(Filters.Tatooine_site, Filters.desert)));
        return modifiers;
    }
}
