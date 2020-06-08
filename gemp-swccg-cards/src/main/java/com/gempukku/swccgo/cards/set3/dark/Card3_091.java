package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Imperial
 * Title: Snowtrooper
 */
public class Card3_091 extends AbstractImperial {
    public Card3_091() {
        super(Side.DARK, 3, 2, 2, 1, 3, "Snowtrooper");
        setLore("Cold Assault troopers are specially trained and equipped to operate in frozen environments. Blizzard Force snowtroopers often work in tandem with AT-ATs.");
        setGameText("Deploys only on Hoth. Power -1 when not at a Hoth site.");
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addKeywords(Keyword.SNOWTROOPER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Hoth;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new NotCondition(new AtCondition(self, Filters.Hoth_site)), -1));
        return modifiers;
    }
}
