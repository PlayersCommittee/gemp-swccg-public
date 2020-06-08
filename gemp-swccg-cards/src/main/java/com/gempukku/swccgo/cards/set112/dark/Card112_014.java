package com.gempukku.swccgo.cards.set112.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Character
 * Subtype: Alien
 * Title: Mighty Jabba
 */
public class Card112_014 extends AbstractAlien {
    public Card112_014() {
        super(Side.DARK, 1, 5, 3, 4, 6, "Mighty Jabba", Uniqueness.UNIQUE);
        setLore("Hutt leader of notorious criminal organization. Gangster of the vilest ilk. Would rather forfeit a fortune in cash than risk one whisper attesting to his mercy.");
        setGameText("Deploys -2 to a Jabba's Palace site. When with your alien leader, adds one battle destiny. May escort a captive. To use his landspeed requires +1 Force. May not be targeted by weapons unless your other aliens present are each 'hit'. Immune to attrition < 4.");
        addPersona(Persona.JABBA);
        addIcons(Icon.PREMIUM);
        addKeywords(Keyword.LEADER, Keyword.GANGSTER);
        setSpecies(Species.HUTT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Jabbas_Palace_site));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.and(Filters.your(self), Filters.alien_leader)), 1));
        modifiers.add(new MayEscortCaptivesModifier(self));
        modifiers.add(new MoveCostUsingLandspeedModifier(self, 1));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, new PresentCondition(self, Filters.and(Filters.your(self),
                Filters.other(self), Filters.alien, Filters.not(Filters.hit)))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
