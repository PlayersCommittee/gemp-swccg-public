package com.gempukku.swccgo.cards.set303.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.MayDeployToShadowAcademyLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalTrainingDestinyModifier;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Title;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Shadow Academy
 * Type: Character
 * Subtype: Alien
 * Title: Instructor Aleema
 */
public class Card303_011 extends AbstractAlien {
    public Card303_011() {
        super(Side.DARK, 1, 5, 3, 6, 6, Title.Instructor_Aleema, Uniqueness.UNIQUE, ExpansionSet.SA, Rarity.C);
        setLore("Currently serving as the Headmistress and Leader of the Shadow Academy. Her leadership has shaped a new generation of  Brotherhood members. Probably harmless.");
        setGameText("Deploys only to Shadow Academy location, but may move elsewhere. When the Master, adds 1 to training destiny.");
		addIcons(Icon.WARRIOR);
        addKeywords(Keyword.INSTRUCTOR);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Shadow_Academy_location;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToShadowAcademyLocationModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalTrainingDestinyModifier(self, Filters.jediTestTargetingMentor(Filters.sameCardId(self)), 1));
        return modifiers;
    }
}
