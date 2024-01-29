package com.gempukku.swccgo.cards.set303.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.CancelCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsInGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.JediTestSuspendedInsteadOfLostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Shadow Academy
 * Type: Objective
 * Title: Peace Is A Lie, There Is Only Passion / The Force Shall Free Me
 */
public class Card303_007_BACK extends AbstractObjective {
    public Card303_007_BACK() {
        super(Side.DARK, 7, "The Force Shall Free Me", ExpansionSet.SA, Rarity.V);
        setGameText("Immediately retrieve 10 Force and place destiny card from Sith Test #5 on that Sith Test. While this side up, your Sith and Dark Jedi ignore deployment restrictions. Opponent may not play Sense or Alter. Place out of play if you Force drain at Arx. Cancel Apprentice's Sith Tests.");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.SITH_TEST_5, ModifyGameTextType.IT_IS_THE_FUTURE_YOU_SEE__STACK_DESTINY_CARD_ON_JEDI_TEST_5));
		modifiers.add(new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self, Filters.or(Filters.Sith, Filters.Dark_Jedi)));
        modifiers.add(new MayNotPlayModifier(self, Filters.or(Filters.Sense, Filters.Alter), opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            int amountToRetrieve = 10;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 10 Force");
            action.setActionMsg("Have " + playerId + " retrieve " + amountToRetrieve + " Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, amountToRetrieve));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.Arx_location)
                || TriggerConditions.justPlacedOutOfPlayFromTable(game, effectResult, Filters.apprentice)) {
            Collection<PhysicalCard> jediTests = Filters.filterAllOnTable(game, Filters.apprentice);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            action.appendEffect(
                    new CancelCardsOnTableEffect(action, jediTests));
            return Collections.singletonList(action);
        }
        return null;
    }
}