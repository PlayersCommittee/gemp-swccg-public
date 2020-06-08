package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.UnderNighttimeConditionConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Title: The Shield Doors Must Be Closed
 */
public class Card3_111 extends AbstractNormalEffect {
    public Card3_111() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.The_Shield_Doors_Must_Be_Closed);
        setLore("'Your highness, there's nothing more we can do tonight. The shield doors must be closed.'");
        setGameText("Deploy between Echo Docking Bay and innermost marker site. When under 'nighttime conditions,' no movement is allowed to or from Echo Docking Bay unless it is to or from an Echo site.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.marker_site);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Echo_Docking_Bay;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition underNighttimeConditions = new UnderNighttimeConditionConditions(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveFromLocationToLocationModifier(self, Filters.any, underNighttimeConditions,
                Filters.Echo_Docking_Bay, Filters.not(Filters.Echo_site)));
        modifiers.add(new MayNotMoveFromLocationToLocationModifier(self, Filters.any, underNighttimeConditions,
                Filters.not(Filters.Echo_site), Filters.Echo_Docking_Bay));
        return modifiers;
    }
}