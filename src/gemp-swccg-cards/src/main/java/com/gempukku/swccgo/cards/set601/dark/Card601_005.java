package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.EnslavedEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 8
 * Type: Effect
 * Title: Den Of Thieves & Special Delivery
 */
public class Card601_005 extends AbstractNormalEffect {
    public Card601_005() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Den Of Thieves & Special Delivery", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        addComboCardTitles(Title.Den_Of_Thieves, Title.Special_Delivery);
        setGameText("Deploy on table. Once per turn, if Skyhook Platform on table and opponent just forfeited a character present with your slaver, may 'enslave' that character (stack character face down under Skyhook Platform). May place an 'enslaved' character in owner's Lost Pile to activate up to 3 Force. Once per turn, while Indentured To The Empire on table, may cancel a Force drain by placing here from hand any non-unique slaver. Slavers may deploy from here as if from hand. May not be canceled. (Immune to Alter.)");
        addIcons(Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_8);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeCanceledModifier(self, self));
        modifiers.add(new MayDeployAsIfFromHandModifier(self, Filters.and(Filters.stackedOn(self), Filters.slaver)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        String opponent = game.getOpponent(playerId);



        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Skyhook_Platform)
                && TriggerConditions.justForfeited(game, effectResult, opponent, Filters.character)
                && TriggerConditions.justForfeitedToLostPileFromLocation(game, effectResult, Filters.and(Filters.opponents(playerId), Filters.character),Filters.any)) {

            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            final PhysicalCard justForfeitedCard = lostFromTableResult.getCard();
            Collection<PhysicalCard> wasPresentWith = lostFromTableResult.getWasPresentWith();
            final PhysicalCard skyhookPlatform = Filters.findFirstFromTopLocationsOnTable(game, Filters.Skyhook_Platform);

            if (justForfeitedCard != null && skyhookPlatform != null && !Filters.filter(wasPresentWith, game, Filters.and(Filters.your(playerId), Filters.slaver)).isEmpty()) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("'Enslave' character");
                action.appendUsage(new OncePerTurnEffect(action));
                action.allowResponses(new RespondableEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new EnslavedEffect(action, justForfeitedCard, skyhookPlatform));
                    }
                });
                actions.add(action);
            }
        }

        Filter nonuniqueSlaver = Filters.and(Filters.non_unique, Filters.slaver);

        //optional after trigger action: Once per turn, while Indentured To The Empire on table, may cancel a Force drain by placing here from hand any non-unique slaver.
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiated(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.Indentured_To_The_Empire)
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.hasInHand(game, playerId, nonuniqueSlaver)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force drain");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new StackCardFromHandEffect(action, playerId, self, nonuniqueSlaver));
            // Perform result(s)
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            actions.add(action);
        }


        return actions;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        if (GameConditions.canSpot(game, self, Filters.hasStacked(Filters.enslavedCard))
                && GameConditions.canActivateForce(game, playerId)
        ) {
            Collection<PhysicalCard> enslavedCharacters = Filters.filterStacked(game, Filters.enslavedCard);

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Activate up to 3 Force");
            action.appendTargeting(new ChooseCardEffect(action, playerId, "Choose an 'enslaved' character", enslavedCharacters) {
                @Override
                protected void cardSelected(PhysicalCard selectedCard) {
                    action.appendCost(new PutStackedCardInLostPileEffect(action, playerId, selectedCard, false));

                    int maxForce = Math.min(game.getGameState().getReserveDeckSize(playerId), 3);
                    if (maxForce > 0) {
                        action.setText("Activate up to " + maxForce + " Force");
                        // Choose target(s)
                        action.appendEffect(
                                new PlayoutDecisionEffect(action, playerId,
                                        new IntegerAwaitingDecision("Choose amount of Force to activate ", 1, maxForce, maxForce) {
                                            @Override
                                            public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                action.setActionMsg("Activate " + result + " Force");
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ActivateForceEffect(action, playerId, result));
                                            }
                                        }
                                )
                        );
                    }
                }
            });

            actions.add(action);
        }

        return actions;
    }

}