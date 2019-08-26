package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Jawa Siesta
 */
public class Card1_051 extends AbstractNormalEffect {
    public Card1_051() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Jawa_Siesta, Uniqueness.UNIQUE);
        setLore("Three Jawas take a cool, energy-saving nap in the noon-day suns at Mos Eisley.");
        setGameText("To deploy (on your side of table), requires 3 Force from both players' Force Piles. Cannot deploy otherwise. All your Jawas are forfeit +1.");
        setDeployUsingBothForcePiles(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition hasExtraModifiers = new GameTextModificationCondition(self, ModifyGameTextType.JAWA_SIESTA__MODIFIED_BY_KALIT);
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        modifiers.add(new DeploysFreeModifier(self, hasExtraModifiers));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter yourJawas = Filters.and(Filters.your(self), Filters.Jawa);
        Condition hasExtraModifiers = new GameTextModificationCondition(self, ModifyGameTextType.JAWA_SIESTA__MODIFIED_BY_KALIT);
        modifiers.add(new ForfeitModifier(self, yourJawas, new NotCondition(hasExtraModifiers), 1, false));
        modifiers.add(new ForfeitModifier(self, yourJawas, hasExtraModifiers, 2, true));
        return modifiers;
    }
}