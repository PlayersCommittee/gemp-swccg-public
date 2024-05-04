package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Jawa
 */
public class Card1_012 extends AbstractAlien {
    public Card1_012() {
        super(Side.LIGHT, 3, null, 1, 1, 1, "Jawa", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Pesky scavenger species. One meter tall. Trade language nearly unintelligible...unless Jawas wish to be understood. Sometimes build massive stone fortresses.");
        setGameText("*To deploy, requires 1 Force from each players' Force Pile. Cannot deploy otherwise. Deploy only on Tatooine, but may move elsewhere.");
        setSpecies(Species.JAWA);
        addKeywords(Keyword.SCAVENGER);
        setDeployUsingBothForcePiles(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }
}
