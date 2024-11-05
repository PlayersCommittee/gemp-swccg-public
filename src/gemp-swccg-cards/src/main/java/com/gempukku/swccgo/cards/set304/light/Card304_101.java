package com.gempukku.swccgo.cards.set304.light;

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
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Tiure Street Thug
 */
public class Card304_101 extends AbstractAlien {
    public Card304_101() {
        super(Side.LIGHT, 3, 2, 2, 1, 2, "Tiure Street Thug", Uniqueness.RESTRICTED_3, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Looked down on by society the under privileged often turn to crime. Even as gangsters they disapprove of going to extremes.");
        setGameText("Adds 1 to power of each of your bounty hunters and gangsters (but subtracts 1 from Sqygorn's power) at same site.");
        addIcons(Icon.WARRIOR);
        setSpecies(Species.RODIAN);
        addKeywords(Keyword.GANGSTER, Keyword.CLAN_TIURE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.bounty_hunter, Filters.gangster),
                Filters.not(Filters.Greedo), Filters.atSameSite(self)), 1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Sqygorn, Filters.atSameSite(self)), -1));
        return modifiers;
    }
}
