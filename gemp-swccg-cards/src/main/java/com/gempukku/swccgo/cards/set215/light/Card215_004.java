package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractRebelRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnBottomOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.SubstituteDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Rebel/Republic
 * Title: Cal Kestis
 */
public class Card215_004 extends AbstractRebelRepublic {
    public Card215_004() {
        super(Side.LIGHT, 2, 4, 4, 4, 5, "Cal Kestis", Uniqueness.UNIQUE);
        setLore("Padawan");
        setGameText("Once per game, if you are about to draw a card for battle destiny here, may instead use Cal Kestis's ability number. During opponent's draw phase, may place one card from your hand under Reserve Deck; reshuffle and draw top card of Reserve Deck.");
        addPersona(Persona.CAL);
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR, Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.PADAWAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.DRAW)
                && GameConditions.isOncePerTurn(game, self, gameTextSourceCardId, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place card under Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new PutCardFromHandOnBottomOfReserveDeckEffect(action, playerId)
            );
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action)
            );
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId)
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CAL_KESTIS__SUBSTITUTE_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                && GameConditions.canSubstituteDestiny(game)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final float ability = game.getModifiersQuerying().getAbility(game.getGameState(), self);

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Substitute destiny with ability number");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendEffect(
                    new SubstituteDestinyEffect(action, ability));
            return Collections.singletonList(action);
        }
        return null;
    }
}
