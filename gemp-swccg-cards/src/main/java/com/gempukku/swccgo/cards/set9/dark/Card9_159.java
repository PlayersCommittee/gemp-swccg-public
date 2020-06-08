package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Obsidian 10
 */
public class Card9_159 extends AbstractStarfighter {
    public Card9_159() {
        super(Side.DARK, 3, 1, 1, null, 3, null, 3, Title.Obsidian_10, Uniqueness.UNIQUE);
        setLore("Recalled to defend second Death Star during construction. Stationed aboard Thunderflare.");
        setGameText("May deploy with a pilot as a 'react' to any asteroid sector or cloud sector. May add one pilot. OS-72-10 deploys -2 aboard.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.OBSIDIAN_SQUADRON, Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.TIE_LN);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.OS_72_10);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.or(Filters.asteroid_sector, Filters.cloud_sector)));
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.OS_72_10, -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.OS_72_10, -2, self));
        return modifiers;
    }
}
