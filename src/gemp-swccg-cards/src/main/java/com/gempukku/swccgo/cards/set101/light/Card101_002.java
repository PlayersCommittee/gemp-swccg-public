package com.gempukku.swccgo.cards.set101.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Set: Premium (Premiere Introductory Two Player Game)
 * Type: Character
 * Subtype: Rebel
 * Title: Luke
 */
public class Card101_002 extends AbstractRebel {
    public Card101_002() {
        super(Side.LIGHT, 1, 4, 2, 3, 4, "Luke", Uniqueness.UNIQUE, ExpansionSet.PREMIERE_INTRO_TWO_PLAYER, Rarity.PM);
        setLore("Raised by guardians Owen and Beru Lars on a moisture farm on Tatooine, where Owen wanted him to stay. Nicknamed 'Wormie' by childhood friends Camie and Fixer.");
        setGameText("Must deploy on Tatooine, but may move elsewhere. May not be deployed if two or more of opponent's unique (•) characters on table. Your warriors at same site as Luke, or adjacent sites are forfeit +1.");
        addPersona(Persona.LUKE);
        addIcons(Icon.WARRIOR);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, new OnTableCondition(self, 2, Filters.and(Filters.opponents(self), Filters.unique, Filters.character))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.warrior, Filters.atSameOrAdjacentSite(self)), 1));
        return modifiers;
    }
}
