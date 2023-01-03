package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToFireWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotUseCardToTransportToOrFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Character
 * Subtype: Alien
 * Title: Greedo (V)
 */
public class Card204_042 extends AbstractAlien {
    public Card204_042() {
        super(Side.DARK, 2, 2, 3, 1, 4, Title.Greedo, Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Male Rodian bounty hunter. Sent by Jabba to capture Han. Arrogant, overconfident and not too bright. Trained by bounty hunters Nataz and Goa, who betrayed him to Thuku.");
        setGameText("May deploy as a 'react'. Opponent must first use 1 Force to fire a weapon at same site. Weapons fired using Sorry About The Mess must target Greedo (if possible). Nabrun Leids may not transport characters to or from here.");
        addIcons(Icon.A_NEW_HOPE, Icon.WARRIOR, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setSpecies(Species.RODIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ExtraForceCostToFireWeaponModifier(self, Filters.and(Filters.opponents(self), Filters.atSameSite(self)), 1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Sorry_About_The_Mess, ModifyGameTextType.SORRY_ABOUT_THE_MESS__WEAPONS_FIRED_MUST_TARGET_GREEDO_IF_POSSIBLE));
        modifiers.add(new MayNotUseCardToTransportToOrFromLocationModifier(self, Filters.Nabrun_Leids, Filters.here(self)));
        return modifiers;
    }
}
