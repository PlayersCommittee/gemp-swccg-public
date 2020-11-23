package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Tigran Jamiro
 */
public class Card3_022 extends AbstractRebel {
    public Card3_022() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, "Tigran Jamiro", Uniqueness.UNIQUE);
        setLore("Senior logistics officer from Onderon. Left Dantooine to serve on Yavin 4 before coming to Hoth. All personnel entering Echo Base must report to him.");
        setGameText("Deploy only on Yavin 4 or Hoth, but may move elsewhere. Opponent's aliens and Imperials may not move from same site as Tigran to an Echo or an interior Yavin site.");
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addPersona(Persona.TIGRAN);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_on_Hoth, Filters.Deploys_on_Yavin_4);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveFromLocationToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.alien, Filters.Imperial)),
                Filters.sameSiteAs(self, Filters.Tigran), Filters.or(Filters.Echo_site, Filters.and(Filters.interior_site, Filters.Yavin_4_site))));
        return modifiers;
    }
}
