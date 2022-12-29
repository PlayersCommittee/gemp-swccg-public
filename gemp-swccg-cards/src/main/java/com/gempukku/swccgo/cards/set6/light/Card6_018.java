package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: H'nemthe
 */
public class Card6_018 extends AbstractAlien {
    public Card6_018() {
        super(Side.LIGHT, 2, 2, 1, 2, 3, "H'nemthe", Uniqueness.UNRESTRICTED, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Starfaring explorers. H'nemthe have a reputation for being sly and unforgiving. Many abandoned their homeworld after the Empire invaded. The females ritually kill their males.");
        setGameText("Adds 2 to power of anything she pilots. Adds 2 to deploy cost of each opponent's male Imperial when that Imperial is deploying to same or adjacent site.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.HNEMTHE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.male, Filters.Imperial),
                2, Filters.sameOrAdjacentSite(self)));
        return modifiers;
    }
}
