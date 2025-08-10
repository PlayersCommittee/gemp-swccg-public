package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.StackOneCardFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardsEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Effect
 * Title: Commence Recharging
 */
public class Card2_031 extends AbstractNormalEffect {
    public Card2_031() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "Commence Recharging", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R2);
        setLore("The huge strain on the little-tested power generator matrix of the superlaser's fusion reactor can require full recharging before the system can be used again.");
        setGameText("Deploy on a superlaser. May not fire at a planet unless 8 cards stacked here. If fewer than 8 cards stacked here, opponent may stack top card of Force Pile here. If just fired at a system, Effect is canceled. If Effect canceled, cards stacked here are placed in Used Pile.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.superlaser_weapon;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.superlaser_weapon;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final int permCardId = self.getPermanentCardId();
        Condition condition = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return GameConditions.hasStackedCards(gameState.getGame(), card, 8);
            }
        };
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.planet_system, new UnlessCondition(condition), Filters.or(Filters.Superlaser, Filters.Commence_Primary_Ignition)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        // Check condition(s)
        if (!GameConditions.hasStackedCards(game, self, 8)
                && GameConditions.hasForcePile(game, playerId)) {

            PhysicalCard topCard = game.getGameState().getTopOfCardPile(playerId, Zone.FORCE_PILE);

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Stack top card of Force Pile");
            // Perform result(s)
            action.appendEffect(
                    new StackOneCardFromForcePileEffect(action, topCard, self, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.weaponJustFired(game, effectResult, Filters.and(Filters.superlaser_weapon, Filters.hasAttached(self)))
                && GameConditions.canBeCanceled(game, self)) {

            FiredWeaponResult weaponFiredResult = (FiredWeaponResult) effectResult;

            if (weaponFiredResult != null
                    && !Filters.filter(weaponFiredResult.getTargets(), game, Filters.system).isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setSingletonTrigger(true);
                action.setText("Cancel");
                action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
                // Perform result(s)
                action.appendEffect(
                        new CancelCardOnTableEffect(action, self));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        // Check condition(s)
        if ((TriggerConditions.isAboutToBeCanceledFromTableBy(game, effectResult, playerId, self)
                || TriggerConditions.isAboutToBeCanceledFromTableBy(game, effectResult, opponent, self))
                && GameConditions.hasStackedCards(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place stacked cards in Used Pile");
            // Perform result(s)

            final int stacked = game.getGameState().getStackedCards(self).size();

            if (stacked > 1) {
                final String MANUAL = "Choose manually";
                final String AUTOMATIC = "Default order";
                action.appendTargeting(new PlayoutDecisionEffect(action, opponent, new MultipleChoiceAwaitingDecision("Do you want to manually choose the order the cards from " + GameUtils.getCardLink(self) + " are placed in Used Pile?", new String[]{MANUAL, AUTOMATIC}) {
                    @Override
                    protected void validDecisionMade(int index, String result) {
                        final boolean manualSelection = MANUAL.equals(result);

                        action.appendTargeting(new ChooseStackedCardsEffect(action, opponent, self, stacked, stacked, Filters.any, true) {
                            @Override
                            protected boolean getUseShortcut() {
                                return !manualSelection;
                            }

                            @Override
                            protected boolean forceManualSelection() {
                                return manualSelection;
                            }

                            @Override
                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                for (PhysicalCard card : selectedCards) {
                                    action.appendEffect(
                                            new PutStackedCardInUsedPileEffect(action, opponent, card, true));
                                }
                            }
                        });
                    }
                }));
            } else {
                action.appendTargeting(new ChooseStackedCardsEffect(action, opponent, self, stacked, stacked, Filters.any, true) {
                    @Override
                    protected boolean getUseShortcut() {
                        return true;
                    }

                    @Override
                    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                        for (PhysicalCard card : selectedCards) {
                            action.appendEffect(
                                    new PutStackedCardInUsedPileEffect(action, opponent, card, true));
                        }
                    }
                });
            }

            actions.add(action);
        }

        return actions;
    }
}