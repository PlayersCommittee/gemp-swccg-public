package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: That's One
 */
public class Card8_041 extends AbstractNormalEffect {
    public Card8_041() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Thats_One, Uniqueness.UNIQUE);
        setLore("Considering the life debt he owed to Han and his personal commitment to the Rebel Alliance, Chewie quickly volunteered to fly the stolen shuttle to Endor.");
        setGameText("Deploy on Chewie. He is immune to attrition < 4 at exterior sites. Visored Vision is immune to Sense. While at same battleground site as Han, Chewie adds 1 to each of his weapon destiny draws and Life Debt is a Used Interrupt during a battle there.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.ENDOR);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Chewie;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Chewie;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter attachedTo = Filters.hasAttached(self);
        Condition atExteriorSite = new AtCondition(self, Filters.exterior_site);
        Condition atSameBattlegroundSiteAsHan = new AtCondition(self, Filters.and(Filters.battleground_site, Filters.sameLocationAs(self, Filters.Han)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, attachedTo, atExteriorSite, 4));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Visored_Vision, Title.Sense));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, atSameBattlegroundSiteAsHan, attachedTo, 1));
        modifiers.add(new UsedInterruptModifier(self, Filters.Life_Debt, new AndCondition(atSameBattlegroundSiteAsHan,
                new DuringBattleAtCondition(Filters.here(self)))));
        return modifiers;
    }
}