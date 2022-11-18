package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: System
 * Title: Mandalore
 */
public class Card219_037 extends AbstractSystem {
    public Card219_037() {
        super(Side.LIGHT, Title.Mandalore, 4, ExpansionSet.SET_19, Rarity.V);
        setLocationDarkSideGameText("If your character armed with Darksaber on table, opponent may not deploy (or Force drain) here.");
        setLocationLightSideGameText("If your Mandalorian here, Force drain +1 here (+2 if armed with Darksaber).");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition condition = new OnTableCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Darksaber, Filters.attachedTo(Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.character))));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.opponents(playerOnDarkSideOfLocation), condition, Filters.here(self)));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.here(self), condition, game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, new HereCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Mandalorian)), new ConditionEvaluator(1, 2, new HereCondition(self, Filters.Darksaber)), playerOnLightSideOfLocation));
        return modifiers;
    }
}
