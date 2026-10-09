package com.gempukku.swccgo.cards.set227.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yaddle
 */
public class Card227_028 extends AbstractJediMaster {
    public Card227_028() {
        super(Side.LIGHT, 2, 4, 2, 7, 7, "Yaddle", Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setLore("Female Jedi Council member.");
        setGameText("Battle destiny draws may not be canceled where you have a Jedi. While with a Dark Jedi Master, all immunity to attrition here is canceled. Once per turn, if you just drew an [Episode I] Jedi for destiny, may take that card into hand. Immune to attrition < 6.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_27);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.Jedi))));
        modifiers.add(new CancelImmunityToAttritionModifier(self, Filters.here(self), new WithCondition(self, Filters.Dark_Jedi_Master)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
            && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
            && GameConditions.isDestinyCardMatchTo(game, Filters.and(Icon.EPISODE_I, Filters.Jedi))
            && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take destiny card into hand");
            action.setActionMsg("Take just drawn destiny card, " + GameUtils.getCardLink(((DestinyDrawnResult) effectResult).getCard()) + ", into hand");
            // Update usage limit(s)
            action.appendUsage(
                new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                new TakeDestinyCardIntoHandEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
