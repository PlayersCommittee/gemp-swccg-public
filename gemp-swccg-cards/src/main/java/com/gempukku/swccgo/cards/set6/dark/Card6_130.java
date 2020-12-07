package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Weequay Guard
 */
public class Card6_130 extends AbstractAlien {
    public Card6_130() {
        super(Side.DARK, 3, 3, 3, 1, 1, Title.Weequay_Guard, Uniqueness.RESTRICTED_3);
        setLore("Weequay are extremely fierce warriors. Species name means, 'follower of Quay'. Very religious. Communicate through pheromones. Smell really bad.");
        setGameText("Deploys only on Tatooine. Deploy -1 to same site as any Weequay. When at same site as any Weequay, (except Weequay Guards), may draw one battle destiny if not able to otherwise.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.WEEQUAY);
        addKeyword(Keyword.GUARD);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameSiteAs(self, Filters.Weequay)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AtSameSiteAsCondition(self,
                Filters.and(Filters.Weequay, Filters.except(Filters.Weequay_Guard))), 1));
        return modifiers;
    }
}
