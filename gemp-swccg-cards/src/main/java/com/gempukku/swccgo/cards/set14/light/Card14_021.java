package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.PilotingAtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Officer Ellberger
 */
public class Card14_021 extends AbstractRepublic {
    public Card14_021() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Officer Ellberger", Uniqueness.UNIQUE);
        setLore("Computer systems troubleshooter whose sour past is not discussed by her squad mates. Years of experience enabled her to predict and adjust to droid starfighter tactics.");
        setGameText("Adds 2 to power of anythings she pilots. While piloting Bravo 5 and at same system as a droid starfighter, adds one destiny to attrition only.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT);
        addKeywords(Keyword.FEMALE, Keyword.BRAVO_SQUADRON);
        setMatchingStarshipFilter(Filters.Bravo_5);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsDestinyToAttritionModifier(self, new PilotingAtCondition(self, Filters.Bravo_5,
                Filters.at(Filters.sameSystemAs(self, Filters.droid_starfighter))), 1));
        return modifiers;
    }
}
