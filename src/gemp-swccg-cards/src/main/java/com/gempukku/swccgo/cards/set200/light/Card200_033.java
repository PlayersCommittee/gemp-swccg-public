package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Device
 * Title: Luke's Bionic Hand
 */
public class Card200_033 extends AbstractCharacterDevice {
    public Card200_033() {
        super(Side.LIGHT, 7, "Luke's Bionic Hand", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setLore("Uses state-of-the-art digital processors. Although Luke had lost his hand, the Alliance could rebuild it. They had the technology. They could make it better, stronger, faster.");
        setGameText("Deploy on Luke. Luke's power and immunity to attrition are +2. Once per game, during battle, may exchange a card in hand with an Interrupt in Lost Pile. This device lost if Luke Disarmed.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_0);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Luke);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Luke;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LUKES_BIONIC_HAND__EXCHANGE_CARD_IN_HAND_WITH_INTERRUPT_IN_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canUseDevice(game, self)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange card in hand for card in Lost Pile");
            action.setActionMsg("Exchange a card in hand for an Interrupt in Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, Filters.any, Filters.Interrupt));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, hasAttached, 2));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, hasAttached, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.justDisarmed(game, effectResult, Filters.hasAttached(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}