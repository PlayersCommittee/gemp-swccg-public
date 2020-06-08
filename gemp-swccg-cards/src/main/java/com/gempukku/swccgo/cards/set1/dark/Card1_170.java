package com.gempukku.swccgo.cards.set1.dark;

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
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Imperial
 * Title: Death Star Trooper
 */
public class Card1_170 extends AbstractImperial {
    public Card1_170() {
        super(Side.DARK, 3, 2, 2, 1, 3, "Death Star Trooper");
        setLore("Elite soldiers trained in combat techniques and weapons skills. Devin Cant augmented security personnel guarding Princess Leia in Detention Block AA-23.");
        setGameText("Deploy only on Death Star, but may move elsewhere. Power -1 at a site other than a Death Star site.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.DEATH_STAR_TROOPER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Death_Star;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.and(Filters.site, Filters.not(Filters.Death_Star_site))), -1));
        return modifiers;
    }
}
