package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Imperial
 * Title: Sandtrooper
 */
public class Card7_201 extends AbstractImperial {
    public Card7_201() {
        super(Side.DARK, 3, 2, 2, 1, 3, "Sandtrooper", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.F);
        setLore("Equipped for harsh, high-temperature environments. Primarily assigned to search-and-destroy missions, but sometimes beaks up local disputes or recover stolen droids.");
        setGameText("Deploys only to Tatooine or any desert. Immune to Sandwhirl. Power -1 when not on Tatooine.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.SANDTROOPER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Deploys_at_Tatooine, Filters.locationAndCardsAtLocation(Filters.desert));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Sandwhirl));
        modifiers.add(new PowerModifier(self, new NotCondition(new OnCondition(self, Title.Tatooine)), -1));
        return modifiers;
    }
}
