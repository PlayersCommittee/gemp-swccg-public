package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Resistance
 * Title: Poe Dameron
 */
public class Card204_008 extends AbstractResistance {
    public Card204_008() {
        super(Side.LIGHT, 2, 4, 3, 2, 6, Title.Poe, Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setLore("Leader.");
        setGameText("[Pilot] 3. Deploys -1 to Jakku. When piloting, adds one battle destiny. Anything he pilots is immune to attrition < 5.");
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new PilotingCondition(self), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.hasPiloting(self), 5));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Deploys_at_Jakku));
        return modifiers;
    }
}
