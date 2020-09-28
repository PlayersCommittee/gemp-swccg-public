package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Jonus
 */
public class Card9_101 extends AbstractImperial {
    public Card9_101() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Captain Jonus", Uniqueness.UNIQUE);
        setLore("Often flies as top cover during bombing runs. Served with Death Squadron. Narrowly escaped death by space slug. Scored several kills against rebel blockade runners.");
        setGameText("Deploys -2 aboard Scimitar 2. Adds 2 to power of anything he pilots. When forfeited from a TIE/sa, also satisfies all remaining attrition and battle damage against you.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.SCIMITAR_SQUADRON, Keyword.CAPTAIN);
        setMatchingStarshipFilter(Filters.Scimitar_2);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, -2, Filters.Scimitar_2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition condition = new PilotingCondition(self, Filters.TIE_sa);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new SatisfiesAllBattleDamageWhenForfeitedModifier(self, condition));
        modifiers.add(new SatisfiesAllAttritionWhenForfeitedModifier(self, condition));
        return modifiers;
    }
}
