package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.PutRandomCardsFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Title: Drop!
 */
public class Card12_132 extends AbstractNormalEffect {
    public Card12_132() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Drop, Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setLore("Anakin had to heed Qui-Gon's advice to avoid the rapidly approaching storm.");
        setGameText("Use 3 Force to deploy on table. During your move phase, if opponent has 13 or more cards in hand, you may use 4 Force to place all but 8 (random choice) in Used Pile. Once during any control phase, you may place up to three cards from hand in Used Pile.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.numCardsInHand(game, opponent) >= 13
                && GameConditions.canUseForce(game, playerId, 4)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place cards from opponent's hand in Used Pile");
            action.setActionMsg("Place random cards from opponent's hand in Used Pile");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 4));
            // Perform result(s)
            action.appendEffect(
                    new PutRandomCardsFromHandOnUsedPileEffect(action, playerId, opponent, 8));
            return Collections.singletonList(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasHand(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place cards from hand in Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PutCardsFromHandOnUsedPileEffect(action, playerId, 1, 3));
            return Collections.singletonList(action);
        }
        return null;
    }
}