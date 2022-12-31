package com.gempukku.swccgo.cards.set109.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Cloud City)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Dengar In Punishing One
 */
public class Card109_010 extends AbstractStarfighter {
    public Card109_010() {
        super(Side.DARK, 1, 5, 2, null, 5, 3, 5, "Dengar In Punishing One", Uniqueness.UNIQUE, ExpansionSet.ENHANCED_CLOUD_CITY, Rarity.PM);
        setLore("Corellian starfighter. Dengar replaced its passenger capacity and TIE cannons with enhanced targeting systems. Allows Dengar to track and engage multiple enemies at once.");
        setGameText("Permanent pilot is •Dengar, who provides ability of 2 and adds 2 to power. When in battle, adds 1 to total battle destiny for each opponent's starship present. Cancels opponent's immunity to attrition here.");
        addPersonas(Persona.PUNISHING_ONE);
        addIcons(Icon.PREMIUM, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER);
        addModelType(ModelType.CORELLIAN_JM_5000);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(Persona.DENGAR, 2) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 2));
                        return modifiers;
                    }
                });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, new InBattleCondition(self), new PresentEvaluator(self,
                Filters.and(Filters.opponents(self), Filters.starship)), self.getOwner()));
        modifiers.add(new CancelImmunityToAttritionModifier(self, Filters.and(Filters.opponents(self), Filters.atSameLocation(self))));
        return modifiers;
    }
}
