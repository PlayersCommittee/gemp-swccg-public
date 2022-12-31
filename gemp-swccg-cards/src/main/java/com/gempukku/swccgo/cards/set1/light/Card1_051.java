package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Jawa Siesta
 */
public class Card1_051 extends AbstractNormalEffect {
    public Card1_051() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Jawa_Siesta, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("Three Jawas take a cool, energy-saving nap in the noon-day suns at Mos Eisley.");
        setGameText("To deploy (on your side of table), requires 3 Force from both players' Force Piles. Cannot deploy otherwise. All your Jawas are forfeit +1.");
        setDeployUsingBothForcePiles(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition hasExtraModifiers = new GameTextModificationCondition(self, ModifyGameTextType.JAWA_SIESTA__DOUBLED_BY_KALIT);
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        modifiers.add(new DeploysFreeModifier(self, hasExtraModifiers));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter yourJawas = Filters.and(Filters.your(self), Filters.Jawa);
        Condition isDoubled = new GameTextModificationCondition(self, ModifyGameTextType.JAWA_SIESTA__DOUBLED_BY_KALIT);
        modifiers.add(new ForfeitModifier(self, yourJawas, new NotCondition(isDoubled), 1, false));
        modifiers.add(new ForfeitModifier(self, yourJawas, isDoubled, 2, true));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, 2, Filters.and(Filters.Jawa_Siesta, Filters.unique))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);

            Collection<PhysicalCard> jawaSiestas = Filters.filterActive(game, self, Filters.Jawa_Siesta);
            PhysicalCard firstJawaSiesta = Filters.findFirstActive(game, self, Filters.Jawa_Siesta);
            jawaSiestas.remove(firstJawaSiesta);
            action.appendEffect(
                    new PlaceCardsInUsedPileFromTableEffect(action, self.getOwner(), jawaSiestas)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}