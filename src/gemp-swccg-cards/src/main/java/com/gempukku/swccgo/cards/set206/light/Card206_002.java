package com.gempukku.swccgo.cards.set206.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToFireWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotUseCardToTransportToOrFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 6
 * Type: Character
 * Subtype: Rebel
 * Title: Cal Alder (V)
 */
public class Card206_002 extends AbstractRebel {
    public Card206_002() {
        super(Side.LIGHT, 2, 2, 2, 1, 4, "Cal Alder", Uniqueness.UNIQUE, ExpansionSet.SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("An expert Scout originally from Kai'Shebbol in the Kathol sector. Served with Bren Derlin for many years. Patrols the outer perimeter of Echo Base.");
        setGameText("May deploy as a 'react'. Deploys -1 to same site as a Rebel leader. Unless with Luke, opponent must first use 1 Force to fire a weapon at same site. Elis Helrot may not transport characters to or from here.");
        addPersona(Persona.CAL);
        addIcons(Icon.HOTH, Icon.WARRIOR, Icon.VIRTUAL_SET_6);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self));
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameSiteAs(self, Filters.Rebel_leader)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ExtraForceCostToFireWeaponModifier(self, Filters.and(Filters.opponents(self), Filters.atSameSite(self)),
                new UnlessCondition(new WithCondition(self, Filters.Luke)), 1));
        modifiers.add(new MayNotUseCardToTransportToOrFromLocationModifier(self, Filters.Elis_Helrot, Filters.here(self)));
        return modifiers;
    }
}
