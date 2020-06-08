package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Starship
 * Subtype: Starfighter
 * Title: Phantom
 */
public class Card208_027 extends AbstractStarfighter {
    public Card208_027() {
        super(Side.LIGHT, 4, 2, 2, null, 4, 3, 4, Title.Phantom, Uniqueness.UNIQUE);
        setLore("Phoenix Squadron.");
        setGameText("May add 2 pilots and 2 passengers. May deploy (with a pilot) or move as a 'react' to same system as a Rebel starship. When with Ghost, adds one destiny to total power.");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_8);
        addKeywords(Keyword.PHOENIX_SQUADRON);
        addModelType(ModelType.MODIFIED_VCX_SHUTTLE);
        setPilotCapacity(2);
        setPassengerCapacity(2);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.sameSystemAs(self, Filters.Rebel_starship)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationModifier(self, Filters.sameSystemAs(self, Filters.Rebel_starship)));
        modifiers.add(new AddsDestinyToPowerModifier(self, new WithCondition(self, Filters.Ghost), 1));
        return modifiers;
    }
}
