package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ExpandLocationGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Expand The Empire
 */
public class Card1_215 extends AbstractNormalEffect {
    public Card1_215() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Expand_The_Empire, Uniqueness.UNIQUE);
        setLore("The Emperor disbanded the Imperial Senate 'for the duration of the emergency,' seizing absolute power. He planned to extend rule by terrorizing planets into submission.");
        setGameText("Deploy on any site. 'Expands' your 'game text' for that site to add to your 'game text' at the adjacent sites.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ExpandLocationGameTextModifier(self, Filters.adjacentSite(self), playerId));
        return modifiers;
    }
}