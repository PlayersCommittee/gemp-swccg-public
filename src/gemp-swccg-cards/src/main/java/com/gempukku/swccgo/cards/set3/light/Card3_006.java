package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Echo Base Trooper
 */
public class Card3_006 extends AbstractRebel {
    public Card3_006() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, "Echo Base Trooper", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C3);
        setLore("The personnel assigned to protect Echo Base are veteran warriors. Troopers such as Jess Allashane are trained to counter Imperial tactics in cold environment.");
        setGameText("Deploy only on Hoth, but may move elsewhere. Power -1 when at a site other than a Hoth site.");
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addKeywords(Keyword.ECHO_BASE_TROOPER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Hoth;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.and(Filters.site, Filters.not(Filters.Hoth_site))), -1));
        return modifiers;
    }
}
