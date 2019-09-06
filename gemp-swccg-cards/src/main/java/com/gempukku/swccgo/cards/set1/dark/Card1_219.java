package com.gempukku.swccgo.cards.set1.dark;

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
 * Title: Jawa Pack
 */
public class Card1_219 extends AbstractNormalEffect {
    public Card1_219() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Jawa_Pack, Uniqueness.UNIQUE);
        setLore("Jawas travel in packs for protection. They use ambush tactics against unwary droids in the canyons of the Jundland Wastes. 'Aeeeyaa!'");
        setGameText("To deploy (on your side of table), requires 3 Force from both players' Force Piles. Cannot deploy otherwise. All your Jawas are forfeit +1.");
        setDeployUsingBothForcePiles(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition hasExtraModifiers = new GameTextModificationCondition(self, ModifyGameTextType.JAWA_PACK__DOUBLED_BY_WITTIN);
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        modifiers.add(new DeploysFreeModifier(self, hasExtraModifiers));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter yourJawas = Filters.and(Filters.your(self), Filters.Jawa);
        Condition isDoubled = new GameTextModificationCondition(self, ModifyGameTextType.JAWA_PACK__DOUBLED_BY_WITTIN);
        modifiers.add(new ForfeitModifier(self, yourJawas, new NotCondition(isDoubled), 1, false));
        modifiers.add(new ForfeitModifier(self, yourJawas, isDoubled, 2, true));
        return modifiers;
    }
}