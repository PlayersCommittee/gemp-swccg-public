package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasStackedCondition;
import com.gempukku.swccgo.cards.effects.takeandputcards.StackCardsFromHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.ShutDownDeathStarPowerEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeUsingLandspeedModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Effect
 * Title: A Power Loss
 */
public class Card215_002 extends AbstractEpicEventDeployable {
    public Card215_002() {
        super(Side.LIGHT, PlayCardZoneOption.ATTACHED, Title.A_Power_Loss, Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setLore("");
        setGameText("Deploy on Central Core. Once per game, opponent may stack up to two cards from hand face-down here. " +
                "All This Sneaking Around: Path Of Least Resistance is canceled. While no cards stacked here, Leia moves for free using her landspeed. " +
                "Allow The Ship To Leave: If you just won a battle at a Death Star site (or initiated a Force drain here), place a card stacked here in owner’s Lost Pile. " +
                "That Old Man Did It!: If you occupy this site and another Death Star site while no cards stacked here, for remainder of game, power 'shut down.'");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_15);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Death_Star_Central_Core;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MovesForFreeUsingLandspeedModifier(self, Filters.Leia, new NotCondition(new HasStackedCondition(self, Filters.any))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.A_POWER_LOSS__STACK_CARDS;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasHand(game, playerId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack cards from hand");
            action.setActionMsg("Stack cards from hand");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendEffect(
                    new StackCardsFromHandEffect(action, playerId, 0, 2, self, true)
            );
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Path_Of_Least_Resistance))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new ArrayList<>();

        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        if (GameConditions.hasStackedCards(game, self)
                && (TriggerConditions.wonBattleAt(game, effectResult, playerId, Filters.on(Title.Death_Star))
                || TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.Death_Star_Central_Core))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Place a card stacked here in " + opponent + "'s Lost Pile");
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, self, Filters.any, true) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendEffect(
                                    new PutStackedCardInLostPileEffect(action, playerId, selectedCard, true)
                            );

                        }
                    }
            );

            actions.add(action);
        }

        if (!GameConditions.hasStackedCards(game, self)
                && GameConditions.occupies(game, playerId, self.getAttachedTo())
                && GameConditions.occupies(game, playerId, Filters.and(Filters.Death_Star_site, Filters.other(self.getAttachedTo())))
                && !game.getModifiersQuerying().isDeathStarPowerShutDown()) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setActionMsg("That Old Man Did It!");
            action.setSingletonTrigger(true);
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self)
            );
            action.appendEffect(
                    new ShutDownDeathStarPowerEffect(action)
            );

            actions.add(action);
        }

        return actions;
    }
}
