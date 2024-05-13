package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitIncreasedAbovePrintedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 11
 * Type: Starship
 * Subtype: Starfighter
 * Title: Tallie Lintra In Blue 1
 */

public class Card211_035 extends AbstractStarfighter {

    public Card211_035() {
        super(Side.LIGHT, 3, 3, 4, null, 5, 4, 5, "Tallie Lintra In Blue 1", Uniqueness.UNIQUE, ExpansionSet.SET_11, Rarity.V);
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

        Filter yourEp7Character = Filters.and(Filters.your(self.getOwner()), Filters.character, Icon.EPISODE_VII);
        Filter sameLocationAsYourEp7Character = Filters.sameLocationAs(self, yourEp7Character);
        Filter charactersHere = Filters.and(Filters.character, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationModifier(self, sameLocationAsYourEp7Character));
        modifiers.add(new MayNotHaveForfeitIncreasedAbovePrintedModifier(self, charactersHere));

        return modifiers;
    }
}
