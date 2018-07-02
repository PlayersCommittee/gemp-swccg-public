package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtSameSystemAsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Starship
 * Subtype: Starfighter
 * Title: Republic Cruiser
 */
public class Card12_093 extends AbstractStarfighter {
    public Card12_093() {
        super(Side.LIGHT, 2, 4, 5, 4, null, 4, 7, "Republic Cruiser");
        setLore("Manufactured by Corellian Engineering Corporation, this consular ship design serves the Republic for a variety of dignitary transportation and diplomatic missions.");
        setGameText("May add 1 pilot and 3 passengers. Permanent pilot provides ability of 2. Has ship-docking capability. While at same system as opponent's battleship, opponent's battle destiny draws are each -1 here.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY);
        addModelType(ModelType.CORELLIAN_REPUBLIC_CRUISER);
        setPilotCapacity(1);
        setPassengerCapacity(3);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), new AtSameSystemAsCondition(self,
                Filters.and(Filters.opponents(self), Filters.battleship)), -1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
