package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractCreatureVehicle;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextAbilityModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeAttackedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostUsingLandspeedModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Vehicle
 * Subtype: Creature
 * Title: Fambaa
 */
public class Card14_059 extends AbstractCreatureVehicle {
    public Card14_059() {
        super(Side.LIGHT, 5, 4, 5, 4, null, 1, 5, "Fambaa", Uniqueness.RESTRICTED_3, ExpansionSet.THEED_PALACE, Rarity.C);
        setLore("Pack animal domesticated by Gungan warriors. Able to bear heavy loads, even during the heat of combat.");
        setGameText("May add 2 'riders' (passengers). Ability = 1/2. Requires +1 Force to use landspeed. May not be attacked by creatures.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.FAMBAA);
        setPassengerCapacity(2);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextAbilityModifier(self, 0.5));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostUsingLandspeedModifier(self, 1));
        modifiers.add(new MayNotBeAttackedByModifier(self, Filters.creature));
        return modifiers;
    }
}
