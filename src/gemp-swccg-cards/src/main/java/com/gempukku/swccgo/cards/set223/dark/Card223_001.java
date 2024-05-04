package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromOutsideTheGameEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotAddDestinyDrawsToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddDestinyDrawsToPowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Not Within Sight Or Reach
 */
public class Card223_001 extends AbstractUsedOrLostInterrupt {
    public Card223_001() {
        super(Side.DARK, 5, "Not Within Sight Or Reach", Uniqueness.UNRESTRICTED, ExpansionSet.SET_22, Rarity.V);
        setLore("When the Empire amasses its fleet, the only option for the Alliance is retreat.");
        setGameText("USED: [Upload] Thrawn or Vanto. OR During battle, players may not draw power or attrition destinies. " +
                "LOST: Once per game, if opponent has deployed two systems and no battleground sites (or a system is 'liberated'), " +
                "deploy a [Premium] dreadnaught from outside your deck (deploy -3).");
        addIcon(Icon.VIRTUAL_SET_23);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.NOT_WITH_SIGHT_OR_REACH__UPLOAD_THRAWN_OR_VANTO;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Thrawn or Vanto into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                                            Filters.or(Filters.Thrawn, Filters.Vanto), true));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.isDuringBattle(game)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Prevent players from drawing power or attrition destinies");
            // Allow response(s)
            action.allowResponses("Prevent players from drawing power or attrition destinies",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new MayNotAddDestinyDrawsToPowerModifier(self, new DuringBattleCondition(), playerId),
                                            playerId + " may not add destinies to power")
                            );
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new MayNotAddDestinyDrawsToPowerModifier(self, new DuringBattleCondition(), opponent),
                                            opponent + " may not add destinies to power")
                            );
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new MayNotAddDestinyDrawsToAttritionModifier(self, new DuringBattleCondition(), playerId),
                                            playerId + " may not add destinies to attrition")
                            );
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action,
                                            new MayNotAddDestinyDrawsToAttritionModifier(self, new DuringBattleCondition(), opponent),
                                            opponent + " may not add destinies to attrition")
                            );
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId1 = GameTextActionId.NOT_WITH_SIGHT_OR_REACH__DOWNLOAD_DREADNAUGHT;

        // Check condition(s)
        if (((GameConditions.hasDeployedAtLeastXCardsThisGame(game, opponent, 2, Filters.system)
                && !GameConditions.hasDeployedAtLeastXCardsThisGame(game, opponent, 1, Filters.battleground_site))
                || GameConditions.canSpot(game, self, Filters.liberated_system))
                && GameConditions.isOncePerGame(game, self, gameTextActionId1)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId1, CardSubtype.LOST);
            action.setText("Deploy card from outside your deck");
            // Append Usage
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Allow response(s)
            action.allowResponses("deploy a [Premium] Dreadnaught from outside your deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromOutsideTheGameEffect(action, Filters.and(Icon.PREMIUM, Filters.Dreadnaught), -3)
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
