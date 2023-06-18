package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.modifiers.LostInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Tentacle (V)
 */
public class Card221_039 extends AbstractNormalEffect {
    public Card221_039() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Tentacle", Uniqueness.UNRESTRICTED, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("Dianogas use their seven tentacles for both locomotion and catching food. The few survivors of such attacks claim that a dianoga tentacle has the strength of a hydro-clamp.");
        setGameText("Deploy on table. Rebel Barrier is a Lost Interrupt. If no card stacked here, may stack one Interrupt just played here. To play any new Interrupt of the same name, player must first stack it here. Once per game, may shuffle any deck or pile. [Immune to Alter.]");
        addIcons(Icon.A_NEW_HOPE, Icon.GRABBER, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new LostInterruptModifier(self, Filters.Rebel_Barrier));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Interrupt)
                && !GameConditions.hasStackedCards(game, self)) {
            PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (GameConditions.canBeGrabbed(game, self, cardBeingPlayed)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("'Grab' " + GameUtils.getFullName(cardBeingPlayed));
                action.setActionMsg("'Grab' " + GameUtils.getCardLink(cardBeingPlayed));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromVoidEffect(action, cardBeingPlayed, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.TENTACLE_V__SHUFFLE_PILE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Shuffle any deck or pile");

            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId) {
                        @Override
                        protected void pileChosen(final SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                            action.allowResponses("Shuffle " + cardPileOwner + "'s " + cardPile.getHumanReadable(),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new ShufflePileEffect(action, cardPileOwner, cardPile));
                                        }
                                    });

                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.Interrupt, Filters.sameTitleAsStackedOn(self)))) {
            PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (GameConditions.canBeGrabbed(game, self, cardBeingPlayed)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("'Grab' " + GameUtils.getFullName(cardBeingPlayed));
                action.setActionMsg("'Grab' " + GameUtils.getCardLink(cardBeingPlayed));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromVoidEffect(action, cardBeingPlayed, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
