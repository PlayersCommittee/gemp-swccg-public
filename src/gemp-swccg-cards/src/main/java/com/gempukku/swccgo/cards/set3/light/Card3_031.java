package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachSearchPartyDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactFromAttackModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Device
 * Title: R2 Sensor Array
 */
public class Card3_031 extends AbstractCharacterDevice {
    public Card3_031() {
        super(Side.LIGHT, 6, "R2 Sensor Array", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C2);
        setLore("Popular R2 astromech accessory manufactured by Industrial Automation. Can monitor radiation levels and detect nearby lifeforms.");
        setGameText("Deploy on any R-unit droid. Your character present may move as a 'react' from a creature attack. Also, adds 3 to search party destiny draws at same and adjacent sites.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.DEVICE_THAT_DEPLOYS_ON_DROIDS, Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    public boolean canBeDeployedOnOpponentsCharacter() {
        return true;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.R_unit;
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.R_unit;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        // Gergall informal: search party adder applies to your search parties only (gametext omits "your"; Portable Scanner parallel)
        modifiers.add(new EachSearchPartyDestinyModifier(self, Filters.sameOrAdjacentSite(self), 3, playerId));
        modifiers.add(new MayMoveOtherCardsAsReactFromAttackModifier(self, "Move character away as a 'react'", playerId,
                Filters.and(Filters.your(self), Filters.character, Filters.present(self))));
        return modifiers;
    }
}
