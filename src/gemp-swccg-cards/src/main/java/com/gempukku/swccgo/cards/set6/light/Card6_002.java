package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Ardon 'Vapor' Crell
 */
public class Card6_002 extends AbstractAlien {
    public Card6_002() {
        super(Side.LIGHT, 4, 2, 2, 1, 2, Title.Ardon_Crell, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Representative from the Moisture Farmers of Tatooine (local 253). Pays Jabba for protection from Sand People.");
        setGameText("Deploys free on Tatooine. While on Tatooine, your Force drains are +1 at exterior sites where you have a Vaporator or Hydroponics Station (+2 if both). While present with a Vaporator, each of your characters present is immune to attrition < 3.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Deploys_on_Tatooine));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.exterior_site, Filters.sameLocationAs(self, Filters.or(Filters.Vaporator, Filters.Hydroponics_Station))),
                new OnCondition(self, Title.Tatooine),
                new CardMatchesEvaluator(1, 2, Filters.and(Filters.sameLocationAs(self, Filters.Vaporator), Filters.sameLocationAs(self, Filters.Hydroponics_Station))),
                self.getOwner()));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.present(self)),
                new PresentWithCondition(self, Filters.Vaporator), 3));
        return modifiers;
    }
}
