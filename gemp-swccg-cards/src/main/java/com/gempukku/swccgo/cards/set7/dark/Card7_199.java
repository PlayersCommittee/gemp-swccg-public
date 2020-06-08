package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: RR'uruurrr
 */
public class Card7_199 extends AbstractAlien {
    public Card7_199() {
        super(Side.DARK, 3, 2, 1, 2, 2, "RR'uruurrr", Uniqueness.UNIQUE);
        setLore("Tusken Raider who tends to the banthas used by URoRRuR'R'R's tribe. Expert in wielding a gaffi stick. Attacked Luke Skywalker in the Jundland Wastes.");
        setGameText("Deploys only on Tatooine. Power +3 while armed with a Gaderffi Stick. Where present, each of your banthas is power and forfeit +2 unless a Weequay is at a related site. When 'riding' a bantha, adds one battle destiny.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        setSpecies(Species.TUSKEN_RAIDER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter banthaFilter = Filters.and(Filters.your(self), Filters.bantha, Filters.atSameLocation(self));
        Condition presentAndNoWeequayCondition = new AndCondition(new PresentCondition(self),
                new UnlessCondition(new AtCondition(self, Filters.Weequay, Filters.relatedSite(self))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new ArmedWithCondition(self, Filters.Gaderffii_Stick), 3));
        modifiers.add(new PowerModifier(self, banthaFilter, presentAndNoWeequayCondition, 2));
        modifiers.add(new ForfeitModifier(self, banthaFilter, presentAndNoWeequayCondition, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new AttachedCondition(self, Filters.bantha), 1));
        return modifiers;
    }
}
