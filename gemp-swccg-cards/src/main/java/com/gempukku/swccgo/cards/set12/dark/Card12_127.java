package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractPoliticalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Subtype: Political
 * Title: Accepting Trade Federation Control
 */
public class Card12_127 extends AbstractPoliticalEffect {
    public Card12_127() {
        super(Side.DARK, 3, "Accepting Trade Federation Control", Uniqueness.UNIQUE);
        setLore("Palpatine suggested to the Queen that she acquiesce to the Trade Federation, just for the time being.");
        setGameText("Deploy on table. If no senator here, you may place a senator here from hand to subtract 3 from any battle destiny just drawn. If a taxation agenda here, during your draw phase, you may activate 1 Force; opponent must use 1 Force (if possible).");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && !GameConditions.hasStackedCards(game, self, Filters.senator)
                && GameConditions.hasInHand(game, playerId, Filters.or(Filters.senator, Filters.grantedToBePlacedOnOwnersPoliticalEffect))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Subtract 3 from battle destiny");
            // Pay cost(s)
            action.appendCost(
                    new StackCardFromHandEffect(action, playerId, self, Filters.or(Filters.senator, Filters.grantedToBePlacedOnOwnersPoliticalEffect)));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -3));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)
                && GameConditions.hasStackedCards(game, self, Filters.taxation_agenda)
                && GameConditions.canActivateForce(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force and make opponent use 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            action.appendEffect(
                    new UseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}