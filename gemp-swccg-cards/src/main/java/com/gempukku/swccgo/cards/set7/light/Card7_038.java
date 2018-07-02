package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.TargetedByUtinniEffectCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Ralltiir Freighter Captain
 */
public class Card7_038 extends AbstractAlien {
    public Card7_038() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Ralltiir Freighter Captain");
        setLore("After Ralltiir's fall to the Empire, many former members of its defense force became smugglers and scouts. Hoping to liberate their planet.");
        setGameText("Power +2 at a Ralltiir site. Adds 2 to power and 1 to maneuver of anything he pilots. When piloting a starship and Ralltiir on table, may draw one battle destiny if not able to otherwise. When making a Kessel Run from Ralltiir, doubles X.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.SMUGGLER, Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.Ralltiir_site), 2));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AndCondition(new PilotingCondition(self, Filters.starship),
                new OnTableCondition(self, Filters.Ralltiir_system)), 1));
        modifiers.add(new VariableMultiplierModifier(self, Filters.kesselRunFromRalltiir, new TargetedByUtinniEffectCondition(self, Filters.kesselRunFromRalltiir), 2, Variable.X));
        return modifiers;
    }
}
