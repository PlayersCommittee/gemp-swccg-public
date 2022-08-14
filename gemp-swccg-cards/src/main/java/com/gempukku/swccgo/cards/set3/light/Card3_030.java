package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Device
 * Title: Hoth Survival Gear
 */
public class Card3_030 extends AbstractCharacterDevice {
    public Card3_030() {
        super(Side.LIGHT, 4, "Hoth Survival Gear");
        setLore("Cold-weather gear worn and carried by Echo Base troops. Enhances their ability to function and survive in Hoth's hostile environment.");
        setGameText("Deploy on any Rebel or warrior. While on Hoth, that character is power and forfeit +1 and is immune to Frostbite, Exposure and Ice Storm.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Rebel, Filters.warrior));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Rebel, Filters.warrior);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttachedOnHoth = Filters.and(Filters.hasAttached(self), Filters.on(Title.Hoth));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, hasAttachedOnHoth, 1));
        modifiers.add(new ForfeitModifier(self, hasAttachedOnHoth, 1));
        modifiers.add(new ImmuneToTitleModifier(self, hasAttachedOnHoth, Title.Frostbite));
        modifiers.add(new ImmuneToTitleModifier(self, hasAttachedOnHoth, Title.Exposure));
        modifiers.add(new ImmuneToTitleModifier(self, hasAttachedOnHoth, Title.Ice_Storm));
        return modifiers;
    }
}