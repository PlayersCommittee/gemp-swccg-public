package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Starship
 * Subtype: Capital
 * Title: Lightmaker
 */
public class Card209_030 extends AbstractCapitalStarship {
    public Card209_030() {
        super(Side.LIGHT, 2, 5, 4, 5, null, 3, 5, "Lightmaker", Uniqueness.UNIQUE);
        setLore("Phoenix Squadron.");
        setGameText("Permanent pilot provides ability of 2. Cancels opponent's immunity to attrition here. While at Scarif, adds 2 to attempts to 'blow away' Shield Gate.");
        addIcons(Icon.VIRTUAL_SET_9, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.PHOENIX_SQUADRON);
        addModelType(ModelType.HAMMERHEAD_CORVETTE);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelImmunityToAttritionModifier(self, Filters.and(Filters.opponents(self), Filters.atSameLocation(self))));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Shield_Gate, new AtCondition(self, Title.Scarif), ModifyGameTextType.ADD_TWO_TO_BLOW_AWAY_SHIELD_GATE));
        return modifiers;
    }
}
