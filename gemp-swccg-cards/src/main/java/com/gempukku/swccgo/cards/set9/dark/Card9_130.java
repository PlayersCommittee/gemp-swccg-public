package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Overseeing It Personally
 */
public class Card9_130 extends AbstractNormalEffect {
    public Card9_130() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Overseeing It Personally", Uniqueness.UNIQUE);
        setLore("Though reluctant to leave Coruscant. Emperor Palpatine occasionally finds it necessary to personally put lagging Imperial operations back on schedule.");
        setGameText("Deploy on Emperor. While at a battleground planet site you control, at each related site where an Imperial is present, your Force drains are +1.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Emperor;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Emperor;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.relatedSite(self), Filters.wherePresent(self, Filters.Imperial)),
                new AtCondition(self, Filters.and(Filters.battleground, Filters.planet_site, Filters.controls(playerId))), 1, playerId));
        return modifiers;
    }
}