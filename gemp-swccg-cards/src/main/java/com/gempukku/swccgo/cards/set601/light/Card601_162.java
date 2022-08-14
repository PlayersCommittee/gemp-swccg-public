package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Block 3
 * Type: Effect
 * Title: Strikeforce (V)
 */
public class Card601_162 extends AbstractNormalEffect {
    public Card601_162() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Strikeforce", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Admiral Ackbar's hit-and-fade tactics force the Imperial Navy to spread throughout the galaxy in a futile attempt to engage the Rebels.");
        setGameText("Deploy on table. Once per game, if opponent just played an Interrupt, may stack it here. To play any new Interrupt of same name, players must first stack it here. May place this Effect in Lost Pile (place cards here in owners' Used Pile) to retrieve a starship. Immune to Alter.");
        addIcons(Icon.DEATH_STAR_II, Icon.GRABBER, Icon.LEGACY_BLOCK_3);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.STRIKEFORCE__STACK_INTERRUPT;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, opponent, Filters.Interrupt)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
            if (GameConditions.canBeGrabbed(game, self, cardBeingPlayed)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("'Grab' " + GameUtils.getFullName(cardBeingPlayed));
                action.setActionMsg("'Grab' " + GameUtils.getCardLink(cardBeingPlayed));
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromVoidEffect(action, cardBeingPlayed, self));
                return Collections.singletonList(action);
            }
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

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
        action.setText("Place in Lost Pile");
        action.setActionMsg("Retrieve a starship");
        // Pay cost(s)
        action.appendCost(
                new PlaceCardInLostPileFromTableEffect(action, self, false, Zone.USED_PILE));
        // Perform result(s)
        action.appendEffect(
                new RetrieveCardEffect(action, playerId, Filters.starship));
        return Collections.singletonList(action);
    }
}