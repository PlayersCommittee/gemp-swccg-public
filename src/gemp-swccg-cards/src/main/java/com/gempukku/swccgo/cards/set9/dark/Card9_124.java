package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OrbitingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Endor Shield
 */
public class Card9_124 extends AbstractNormalEffect {
    public Card9_124() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Endor_Shield, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Planetary scale shield projected from surface of Endor moon. Protected second Death Star during construction. Only another superlaser could penetrate it while operational.");
        setGameText("Deploy on Bunker. Imperials deploy -1 here. While Death Star II system orbits Endor and you control Bunker, at Death Star II system and each Death Star site opponent may not deploy and must use +3 Force to move there.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Bunker;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition condition = new AndCondition(new OrbitingCondition(Filters.Death_Star_II_system, Title.Endor), new ControlsCondition(playerId, Filters.Bunker));
        Filter locationFilter = Filters.or(Filters.Death_Star_II_system, Filters.Death_Star_II_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Imperial, -1, Filters.here(self)));
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.opponents(self), condition, locationFilter));
        modifiers.add(new MoveCostToLocationModifier(self, Filters.opponents(self), condition, 3, locationFilter));
        return modifiers;
    }
}