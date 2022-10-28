package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Effect
 * Title: Silence is Golden (V)
 */
public class Card210_045 extends AbstractNormalEffect {
    public Card210_045() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Silence Is Golden");
        setLore("'Excuse me, sir, might I in--'");
        setGameText("Deploy on table. Your droids are destiny +1. Once per turn, if you just drew a non-[Presence] droid for destiny, may take that droid into hand. Once per turn, may deploy Droid Workshop, Forced Servitude, Incinerator, or Wuher from Reserve Deck; reshuffle. [Immune to Alter.]");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_10);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SILENCE_IS_GOLDEN__DOWNLOAD_CARD;

        // Once per turn, may deploy Droid Workshop, Forced Servitude, Incinerator, or Wuher from Reserve Deck; reshuffle.
        Filter deployableCards = Filters.or(
                Filters.title(Title.Droid_Workshop),
                Filters.title(Title.Forced_Servitude),
                Filters.title(Title.Incinerator),
                Filters.title(Title.Wuher));

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId,
                Arrays.asList(Title.Droid_Workshop, Title.Forced_Servitude, Title.Incinerator, Title.Wuher))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a Droid Workshop, Forced Servitude, Incinerator, or Wuher from Reserve Deck");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, deployableCards, true));
            return Collections.singletonList(action);
        }
        return null;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        // Your droids are destiny +1
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter yourDroids = Filters.and(Filters.your(self), Filters.droid);
        modifiers.add(new DestinyModifier(self, yourDroids, 1));
        return modifiers;
    }


    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Once per turn, if you just drew a non-[Presence] droid for destiny, may take that droid into hand.
        Filter nonPresenceDroid = Filters.and(Filters.your(self), Filters.droid, Filters.not(Icon.PRESENCE));

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDestinyCardMatchTo(game, nonPresenceDroid)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take destiny card into hand");
            action.setActionMsg("Take just drawn destiny card, " + GameUtils.getCardLink(((DestinyDrawnResult) effectResult).getCard()) + ", into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeDestinyCardIntoHandEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}