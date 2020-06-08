package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Rebel
 * Title: Rebel Trooper
 */
public class Card1_028 extends AbstractRebel {
    public Card1_028() {
        super(Side.LIGHT, 1, 1, 1, 1, 2, "Rebel Trooper");
        setLore("Corellian Corvette trooper Ensign Chad Hilse, an Alderaanian, typifies the loyal Rebel volunteers dedicated to defeating the Empire. Trained in starship and ground combat.");
        setGameText("Deploys free to same site as one of your Rebels with ability > 2.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.TROOPER);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityMoreThan(2)))));
        return modifiers;
    }
}
