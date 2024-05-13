package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Vehicle
 * Subtype: Transport
 * Title: Kalit's Sandcrawler
 */
public class Card7_152 extends AbstractTransportVehicle {
    public Card7_152() {
        super(Side.LIGHT, 2, 3, 3, 3, null, 2, 5, "Kalit's Sandcrawler", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Provides enclosed shelter from harsh Tatooine climate, Tusken Raiders and krayt dragons. Kalit offers sanctuary to others when traveling (even members of enemy Jawa tribes).");
        setGameText("Deploys only on Tatooine. May add 1 driver and 7 passengers. While your Jawa is aboard, this vehicle and your Jawas at same site are immune to attrition < 3 (< 5 if Kalit at same site).");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.SANDCRAWLER, Keyword.ENCLOSED);
        setDriverCapacity(1);
        setPassengerCapacity(7);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.or(self, Filters.and(Filters.your(self), Filters.Jawa, Filters.atSameSite(self))),
                new HasAboardCondition(self, Filters.and(Filters.your(self), Filters.Jawa)),
                new ConditionEvaluator(3, 5, new AtSameSiteAsCondition(self, Filters.Kalit))));
        return modifiers;
    }
}
