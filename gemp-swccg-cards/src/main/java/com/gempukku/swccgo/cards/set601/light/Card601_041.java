package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.AbstractEpicEventPlayable;
import com.gempukku.swccgo.cards.AbstractStartingEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AfterPlayersTurnNumberCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.FourTimesPerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.modifiers.MayBePlayedInsteadOfStartingInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Epic Event
 * Title: Communing
 */
public class Card601_041 extends AbstractEpicEventDeployable {
    public Card601_041() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Communing");
        setGameText("If your starting location has exactly 2 [Light Side Force], play instead of a starting interrupt. deploy from Reserve Deck two always [Immune to Alter] Effects; reshuffle. Deploy on table; take into hand and stack a Jedi here from Reserve Deck (that Jedi is 'communing'); reshuffle. \n" +
                "Luminous Beings: Whenever a Jedi is lost (or placed out of play) from table, may stack it here. Jedi stacked here are considered out of play. You generate +1 Force for each card stacked here. I Can't Believe He's Gone Is canceled. Once per turn, if two cards here, may use 1 Force to look at top card of Reserve Deck, Force Pile, and/or Used Pile; return one card to each deck or pile.");
        addIcons(Icon.SPECIAL_EDITION, Icon.BLOCK_7);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayBePlayedInsteadOfStartingInterruptModifier(self, self,
                new OnTableCondition(self, Filters.and(Filters.iconCount(Icon.LIGHT_FORCE, 2), game.getModifiersQuerying().getStartingLocation(self.getOwner())))));
        return modifiers;
    }

//    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        final PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
        if (startingLocation != null && Filters.system.accepts(game, startingLocation)) {
            final String systemName = startingLocation.getPartOfSystem();
            if (systemName != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
                action.setText("Deploy site and Effects from Reserve Deck");
                // Allow response(s)
                action.allowResponses("Deploy a related site and up to three Effects from Reserve Deck",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                Filter specialLocationConditions = (!startingLocation.isStartingLocationBattleground() || Filters.Endor_system.accepts(game, startingLocation)) ? Filters.battleground : null;
                                // Perform result(s)
                                action.appendEffect(
                                        new DeployCardToSystemFromReserveDeckEffect(action, Filters.and(Filters.site, Filters.partOfSystem(systemName), Filters.iconCountLessThan(Icon.DARK_FORCE, 3)), systemName, specialLocationConditions, true, false));
                                action.appendEffect(
                                        new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 1, 3, true, false));
                                action.appendEffect(
                                        new PutCardFromVoidInLostPileEffect(action, playerId, self));
                            }
                        }
                );
                return action;
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.opponents(self), Filters.Uncertain_Is_The_Future),
                new NotCondition(new AfterPlayersTurnNumberCondition(playerId, 1)), ModifyGameTextType.UNCERTAIN_IS_THE_FUTURE__MAY_NOT_BE_PLAYED_EXCEPT_TO_CANCEL_INTERRUPT));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            // Perform result(s)
            action.appendEffect(
                    new StackCardsFromOutsideDeckEffect(action, playerId, 1, 15, self, Filters.Defensive_Shield));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANGER_FEAR_AGGRESSION__PLAY_CARD;
        Filter filter = Filters.playable(self);

        // Check condition(s)
        if (GameConditions.isFourTimesPerGame(game, self, gameTextActionId)
                && GameConditions.hasStackedCards(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Play a card");
            // Update usage limit(s)
            action.appendUsage(
                    new FourTimesPerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, self, filter) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlayStackedCardEffect(action, self, selectedCard));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}