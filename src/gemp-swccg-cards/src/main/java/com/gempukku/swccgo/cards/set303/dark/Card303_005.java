package com.gempukku.swccgo.cards.set303.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Shadow Academy
 * Type: Effect
 * Title: Through Passion, I Gain Strength
 */
public class Card303_005 extends AbstractNormalEffect {
    public Card303_005() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Through_Passion_I_Gain_Strength, Uniqueness.UNIQUE, ExpansionSet.SA, Rarity.C);
        setLore("'It is our goal to be stronger, to achieve our potential and not rest upon our laurels. We are the seekers, not the shepherds.' - Yuthura Ban");
        setGameText("Deploy on a character. When on the mentor, adds 1 to training destiny draws. If mentor is a Headmaster/mistress or Instructor, adds 2 to training destiny draws.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.character;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachTrainingDestinyModifier(self, Filters.jediTestTargetingMentor(Filters.hasAttached(self)), 1));	
		modifiers.add(new EachTrainingDestinyModifier(self, Filters.jediTestTargetingMentor(Filters.and(Filters.hasAttached(self), Filters.or(Filters.HEADMASTER, Filters.INSTRUCTOR))), 1));		
        return modifiers;
    }

}