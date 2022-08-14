package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayTargetAdjacentSiteModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Device
 * Title: Blaster Scope
 */
public class Card1_199 extends AbstractDevice {
    public Card1_199() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Blaster_Scope);
        setLore("The effectiveness of a blaster can sometimes be enhanced through an electronic targeting scope mounted on top, especially for long range targets.");
        setGameText("Deploy on [Dagobah] 4-LOM's Concussion Rifle or on your blaster weapon card. Allows that weapon to target at an adjacent site.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.and(Icon.DAGOBAH, Filters.title("4-LOM's Concussion Rifle")), Filters.and(Filters.blaster, Filters.weapon)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayTargetAdjacentSiteModifier(self, Filters.hasAttached(self)));
        return modifiers;
    }
}