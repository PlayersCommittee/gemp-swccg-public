package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Starship
 * Subtype: Capital
 * Title: Invisible Hand
 */
public class Card211_023 extends AbstractCapitalStarship {
    public Card211_023() {
        super(Side.DARK, 2, 6, 6, 5, null, 3, 8, Title.Invisible_Hand, Uniqueness.UNIQUE);
        setGameText("May add 3 pilots, 5 passengers, and 4 [Trade Federation] starfighters. Permanent pilot provides ability of 3. Adds one battle destiny if Grievous on table. Immune to attrition < 5.");
        addPersona(Persona.INVISIBLE_HAND);
        addIcons(Icon.NAV_COMPUTER, Icon.TRADE_FEDERATION, Icon.EPISODE_I, Icon.SCOMP_LINK, Icon.SEPARATIST, Icon.VIRTUAL_SET_11);
        addIcon(Icon.PILOT, 1);
        addModelType(ModelType.PROVIDENCE_CLASS_DREADNAUGHT);
        setPilotCapacity(3);
        setPassengerCapacity(5);
        setStarfighterCapacity(4, Filters.Trade_Federation_starfighter);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(3) {
        });
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        modifiers.add(new AddsBattleDestinyModifier(self, new OnTableCondition(self, Filters.persona(Persona.GRIEVOUS)), 1));
        return modifiers;
    }
}

