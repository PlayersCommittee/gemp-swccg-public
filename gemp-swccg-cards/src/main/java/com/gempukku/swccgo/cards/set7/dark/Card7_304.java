package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Capital
 * Title: Jabba's Space Cruiser
 */
public class Card7_304 extends AbstractCapitalStarship {
    public Card7_304() {
        super(Side.DARK, 2, 5, 5, 5, null, 4, 5, Title.Jabbas_Space_Cruiser, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Flying fortress of Jabba Desilijic Tiure. Reaches speeds of 800 kph in atmosphere. The crime lord installed hidden gunports as an unpleasant surprise for would-be pirates.");
        setGameText("Deploys and moves like a starfighter. May add 2 alien pilots and 6 passengers. Turbolaser Battery may deploy aboard as a 'react.' When Jabba aboard, moves for free and immune to attrition.");
        addIcons(Icon.SPECIAL_EDITION, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.UBRIKKIAN_LUXURY_SPACE_YACHT);
        addKeywords(Keyword.CRUISER);
        setPilotCapacity(2);
        setPassengerCapacity(6);
    }

    @Override
    public boolean isDeploysLikeStarfighter() {
        return true;
    }

    @Override
    public boolean isMovesLikeStarfighter() {
        return true;
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.alien;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition jabbaAboard = new HasAboardCondition(self, Filters.Jabba);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployOtherCardsAsReactToTargetModifier(self, "Deploy Turbolaser Battery as a 'react'", self.getOwner(), Filters.turbolaser_battery, self, true));
        modifiers.add(new MovesForFreeModifier(self, jabbaAboard));
        modifiers.add(new ImmuneToAttritionModifier(self, jabbaAboard));
        return modifiers;
    }
}
