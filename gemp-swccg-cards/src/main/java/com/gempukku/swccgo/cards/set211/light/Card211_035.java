package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 11
 * Type: Starship
 * Subtype: Starfighter
 * Title: Tallie Lintra In Blue Squadron 1
 */

public class Card211_035 extends AbstractStarfighter {

    public Card211_035() {
        super(Side.LIGHT, 3, 3, 4, null, 5, 4, 5, "Tallie Lintra In Blue Squadron 1", Uniqueness.UNIQUE);
        setGameText("Permanent pilot is •Tallie, who provides ability of 2. May move as a 'react' to same location as your [Episode VII] character. Characters here may not have their forfeit increased beyond their printed value.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_11, Icon.RESISTANCE, Icon.EPISODE_VII);
        addKeywords(Keyword.BLUE_SQUADRON);
        addModelType(ModelType.A_WING);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.TALLIE_LINTRA, 2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        Filter ep7Character = Filters.and(Filters.character, Icon.EPISODE_VII);
        Filter sameLocationAsEp7Character = Filters.sameLocationAs(self, ep7Character);
        Filter charactersHere = Filters.and(Filters.character, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationModifier(self, sameLocationAsEp7Character));
        modifiers.add(new MayNotHaveForfeitIncreasedAbovePrintedModifier(self, charactersHere));

        return modifiers;
    }
}
