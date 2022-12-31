package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractRebel;
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
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Rebel
 * Title: Rebel Commander
 */
public class Card2_017 extends AbstractRebel {
    public Card2_017() {
        super(Side.LIGHT, 2, 3, 1, 2, 2, "Rebel Commander", Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.C2);
        setLore("Commander Bob Hudsol. Typical of hard-line Corellian officers known for caution in battle. Leader of resistance in Bothan space. Developed strong ties to Bothan spynet.");
        setGameText("Adds 1 to forfeit of each of your other Rebels (except leaders) at same site. Bothan spies deploy free to same location.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Rebel, Filters.except(Filters.leader), Filters.atSameSite(self)), 1));
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.and(Filters.Bothan, Filters.spy), Filters.sameLocation(self)));
        return modifiers;
    }
}
