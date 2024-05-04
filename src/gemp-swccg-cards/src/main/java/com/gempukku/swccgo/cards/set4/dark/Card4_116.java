package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Bad Feeling Have I
 */
public class Card4_116 extends AbstractNormalEffect {
    public Card4_116() {
        super(Side.DARK, 3, PlayCardZoneOption.OPPONENTS_SIDE_OF_TABLE, Title.Bad_Feeling_Have_I, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("'Ready are you? What know you of ready?'");
        setGameText("Deploy on opponent's side of table. Luke, Leia, Han, Chewie, Lando, Yoda, and Obi-Wan are deploy +2. Also, opponent may not play any cards with the words 'bad feeling' in the title.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.or(Filters.Luke, Filters.Leia, Filters.Han, Filters.Chewie,
                Filters.Lando, Filters.Yoda, Filters.ObiWan), 2));
        modifiers.add(new MayNotPlayModifier(self, Filters.titleContains("bad feeling"), opponent));
        return modifiers;
    }
}