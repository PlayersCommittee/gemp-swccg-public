package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Vehicle
 * Subtype: Combat
 * Title: Multi Troop Transport
 */
public class Card14_122 extends AbstractCombatVehicle {
    public Card14_122() {
        super(Side.DARK, 3, 1, 2, 4, null, 2, 3, "Multi Troop Transport", Uniqueness.UNRESTRICTED, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("Vehicle utilized by the Trade Federation droid army. 31 meters tall. Capable of holding, transporting, and deploying up to 112 battle droids. Enclosed.");
        setGameText("May add 1 pilot and 7 passengers. Your battle droids may move from this site for free. Immune to attrition < 3.");
        addModelType(ModelType.MTT);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
        setPassengerCapacity(7);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeFromLocationModifier(self, Filters.and(Filters.your(self), Filters.battle_droid), Filters.sameSite(self)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
