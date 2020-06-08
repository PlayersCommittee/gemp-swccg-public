package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.evaluators.PerTIEEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Effect
 * Title: Logistical Delay
 */
public class Card2_034 extends AbstractNormalEffect {
    public Card2_034() {
        super(Side.LIGHT, 3, PlayCardZoneOption.OPPONENTS_SIDE_OF_TABLE, Title.Logistical_Delay, Uniqueness.UNIQUE);
        setLore("The Empire's tremendous military machine requires extensive coordination at every level. Slight errors in any step can quickly escalate supply delays.");
        setGameText("Use 3 Force to deploy on opponent's side of table. All opponent's TIEs are deploy +1.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.opponents(self), Filters.TIE), new PerTIEEvaluator(1)));
        return modifiers;
    }
}