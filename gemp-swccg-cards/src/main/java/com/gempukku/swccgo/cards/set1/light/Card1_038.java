package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Device
 * Title: Restraining Bolt
 */
public class Card1_038 extends AbstractCharacterDevice {
    public Card1_038() {
        super(Side.LIGHT, 6, Title.Restraining_Bolt);
        setLore("Fits in a special socket on a droid's body. When installed, restricts actions of droids, forcing them to follow orders. Can be operated remotely by a 'caller.'");
        setGameText("Deploy on any droid at any site. Droid cannot move and cannot utilize its 'game text.' During your deploy phase, Restraining Bolt may be transferred (for free) to another droid at same site.");
        addKeywords(Keyword.DEVICE_THAT_DEPLOYS_ON_DROIDS);
    }

    @Override
    public boolean canBeDeployedOnOpponentsCharacter() {
        return true;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.droid, Filters.not(Icon.PRESENCE), Filters.at(Filters.site));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.droid, Filters.not(Icon.PRESENCE));
    }

    // TODO: Device transfer: Can transfer to any droid at same site, getTopLevelAction

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveModifier(self, hasAttached));
        modifiers.add(new CancelsGameTextModifier(self, hasAttached));
        return modifiers;
    }
}