package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Starship
 * Subtype: Capital
 * Title: Wild Karrde (Errata)
 */
public class Card501_041 extends AbstractCapitalStarship {
    public Card501_041() {
        super(Side.LIGHT, 2, 3, 3, 4, null, 5, 6, Title.Wild_Karrde, Uniqueness.UNIQUE);
        setGameText("May add 3 pilots and 6 passengers. Permanent pilot aboard provides ability of 2. During battle, your total battle destiny is +1 for each smuggler aboard. Deploys and moves like a starfighter. Immune to attrition < 5.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.INDEPENDENT, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_3);
        addModelType(ModelType.MODIFIED_ACTION_VI_FREIGHTER);
        setPilotCapacity(3);
        setPassengerCapacity(6);
        setTestingText("Wild Karrde (Errata)");
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, new InBattleCondition(self),
                new OnTableEvaluator(self, Filters.and(Filters.smuggler, Filters.aboard(self))), playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    public boolean isDeploysAndMovesLikeStarfighter() {
        return true;
    }
}
