package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Mandalorian Hunter
 */
public class Card304_100 extends AbstractAlien {
    public Card304_100() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Mandalorian Hunter", Uniqueness.RESTRICTED_3, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Following the Night of a Thousand Tears, Mandalorian hunters take any jobs they can to support their coverts. Bounty Hunter.");
        setGameText("May 'fly' (landspeed = 3). May move as a 'react.'");
        setArmor(3);
        addIcons(Icon.WARRIOR);
        addKeyword(Keyword.BOUNTY_HUNTER);
        setSpecies(Species.MANDALORIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 3));
        modifiers.add(new MayMoveAsReactModifier(self));
        return modifiers;
    }


}