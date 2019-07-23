package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

public class Card211_011 extends AbstractEpicEventDeployable {
    public Card211_011() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.Insidious_Prisoner);
        setGameText("If A Stunning Move on table, deploy on 500 Republica. While at a site (even while on a captive), adds one [Dark Side] icon here. While on Coruscant, opponent loses no more than 1 Force from your Force drains here. Once per turn, if a player controls this site, they may have this card (unless on Palpatine) follow their first character to move from here using landspeed (or docking bay transit) to a battleground site. If about to leave table, relocate to 500 Republica (if possible).");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.A_Stunning_Move);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters._500_Republica;
    }

    // TODO Adds dark side icon
    // TODO While on coruscant, force drain limit
    // TODO Follow text
    // TODO Relocate to 500 Republica text
}
