package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Dark Jedi Master/Imperial
 * Title: The Emperor
 */
public class Card10_051 extends AbstractDarkJediMasterImperial {
    public Card10_051() {
        super(Side.DARK, 1, 6, 4, 7, 9, "The Emperor", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setLore("Leader. Secretive manipulator of the galaxy. Played Darth Vader and Prince Xizor off against one another in his relentless pursuit of 'young Skywalker'.");
        setGameText("Deploys only to Coruscant or Death Star II. Never moves to a site occupied by opponent (even if carried). If Vader or Xizor here, and Luke is not on table, adds 2 to attrition against opponent at other locations. Immune to attrition.");
        addPersona(Persona.SIDIOUS);
        addIcons(Icon.REFLECTIONS_II, Icon.DEATH_STAR_II);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_at_Coruscant, Filters.Deploys_at_Death_Star_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        Filter siteOpponentOccupies = Filters.and(Filters.site, Filters.occupies(game.getOpponent(self.getOwner())));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.or(self, Filters.hasAttachedWithRecursiveChecking(self)), siteOpponentOccupies));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, Filters.otherLocation(self), new AndCondition(new HereCondition(self,
                Filters.or(Filters.Vader, Filters.Xizor)), new NotCondition(new OnTableCondition(self, Filters.Luke))),
                2, game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }
}
