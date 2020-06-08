package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Effect
 * Title: Droid Racks
 */
public class Card14_096 extends AbstractNormalEffect {
    public Card14_096() {
        super(Side.DARK, 1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Droid_Racks, Uniqueness.UNIQUE);
        setLore("The Trade Federation utilizes such efficient methods of droid deployment that it is rumored even they do not know exactly how many battle druids are in circulation");
        setGameText("Use 4 Force to deploy on table. If your battle droid was just lost, may place it here. During your control phase, may use 2 Force (1 Force if your piloted MTT on table) to take any battle droid from here into hand. (Immune to Alter.)");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.battle_droid))) {
            PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack " + GameUtils.getFullName(cardLost));
            action.setActionMsg("Stack " + GameUtils.getCardLink(cardLost));
            // Perform result(s)
            action.appendEffect(
                    new StackOneCardFromLostPileEffect(action, cardLost, self, false, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.hasStackedCards(game, self, Filters.battle_droid)) {
            int cost = GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.piloted, Filters.MTT)) ? 1 : 2;
            if (GameConditions.canUseForce(game, playerId, cost)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take a battle droid into hand");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, cost));
                // Perform result(s)
                action.appendEffect(
                        new TakeStackedCardIntoHandEffect(action, playerId, self, Filters.battle_droid));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}