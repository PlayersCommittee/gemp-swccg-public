package com.gempukku.swccgo.cards;


import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysAndMovesLikeUndercoverSpyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * The abstract class providing the common implementation for seekers.
 */
public abstract class AbstractSeeker extends AbstractAutomatedWeapon {

    /**
     * Creates a blueprint for a seeker.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     */
    protected AbstractSeeker(Side side, float destiny, String title) {
        super(side, destiny, PlayCardZoneOption.OPPONENTS_SIDE_OF_LOCATION, title);
        addKeyword(Keyword.SEEKER);
    }

    @Override
    public final boolean isMovesLikeCharacter() {
        return true;
    }


    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.unoccupied, Filters.site);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        modifiers.add(new DeploysAndMovesLikeUndercoverSpyModifier(self));
        return modifiers;
    }
}
