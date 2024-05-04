package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Twilight Is Upon Me
 */
public class Card9_046 extends AbstractNormalEffect {
    public Card9_046() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Twilight Is Upon Me", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("When a Jedi dies, the spirit spreads through the Force and touches the living.");
        setGameText("Deploy on table. Once per turn, may take The Force Is Strong With This One or Anakin Skywalker into hand from Reserve Deck: reshuffle. May use 2 Force to cancel Dark Jedi Presence or You Are Beaten (unless canceling Uncontrollable Fury). (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Card action 1
        GameTextActionId gameTextActionId = GameTextActionId.TWILIGHT_IS_UPON_ME__UPLOAD_THE_FORCE_IS_STRONG_WITH_THIS_ONE_OR_ANAKIN_SKYWALKER;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take The Force Is Strong With This One or Anakin Skywalker into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.The_Force_Is_Strong_With_This_One, Filters.Anakin_Skywalker), true));
            actions.add(action);
        }

        // Card action 2
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Dark_Jedi_Presence)
                && GameConditions.canUseForce(game, playerId, 2)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Dark_Jedi_Presence, Title.Dark_Jedi_Presence, 2);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.You_Are_Beaten, Filters.Dark_Jedi_Presence))
                && !TriggerConditions.isPlayingCardTargeting(game, effect, Filters.You_Are_Beaten, TargetingReason.TO_BE_CANCELED, Filters.Uncontrollable_Fury)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.canUseForce(game, playerId, 2)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect, 2);
            return Collections.singletonList(action);
        }
        return null;
    }
}