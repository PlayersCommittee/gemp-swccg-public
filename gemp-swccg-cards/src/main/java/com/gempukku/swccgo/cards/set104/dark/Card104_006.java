package com.gempukku.swccgo.cards.set104.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Empire Strikes Back Introductory Two Player Game)
 * Type: Character
 * Subtype: Imperial
 * Title: Veers
 */
public class Card104_006 extends AbstractImperial {
    public Card104_006() {
        super(Side.DARK, 1, 4, 2, 3, 3, "Veers", Uniqueness.UNIQUE);
        setLore("General of the AT-AT assault armor division sent by Darth Vader to crush the Rebellion on Hoth. Cold and ruthless.");
        setGameText("Must deploy on Hoth, but may move elsewhere. May not be deployed if three or more of opponent's unique (•) characters on table. Snowtroopers at same site are forfeit +1.");
        addPersona(Persona.VEERS);
        addIcons(Icon.PREMIUM, Icon.WARRIOR);
        addKeywords(Keyword.GENERAL);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Hoth;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, new OnTableCondition(self, 3, Filters.and(Filters.opponents(self), Filters.unique, Filters.character))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.snowtrooper, Filters.atSameSite(self)), 1));
        return modifiers;
    }
}
