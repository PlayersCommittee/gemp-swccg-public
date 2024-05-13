package com.gempukku.swccgo.cards.set104.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Empire Strikes Back Introductory Two Player Game)
 * Type: Character
 * Subtype: Rebel
 * Title: Chewie
 */
public class Card104_001 extends AbstractRebel {
    public Card104_001() {
        super(Side.LIGHT, 1, 5, 4, 2, 3, "Chewie", Uniqueness.UNIQUE, ExpansionSet.ESB_INTRO_TWO_PLAYER, Rarity.PM);
        setLore("Loyal Wookiee companion of Captain Han Solo. Co-pilot of the Millennium Falcon. Leia referred to him as a 'walking carpet.'");
        setGameText("Must deploy on Hoth, but may move elsewhere. May not be deployed if three or more of opponent's unique (•) characters on table.");
        addPersona(Persona.CHEWIE);
        addIcons(Icon.PREMIUM, Icon.PILOT);
        setSpecies(Species.WOOKIEE);
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
}
