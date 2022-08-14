package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Presence Of The Force
 */
public class Card1_227 extends AbstractNormalEffect {
    public Card1_227() {
        super(Side.DARK, 2, PlayCardZoneOption.ATTACHED, Title.Presence_Of_The_Force, Uniqueness.RESTRICTED_2);
        setLore("A location is affected by the history of the events which occur there. The Force '...binds the galaxy together' and has an ebb and flow.");
        setGameText("Deploy on any location to add one [Dark Side Force] and one [Light Side Force].");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.location;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter attachedTo = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, attachedTo, Icon.DARK_FORCE, 1));
        modifiers.add(new IconModifier(self, attachedTo, Icon.LIGHT_FORCE, 1));
        return modifiers;
    }
}