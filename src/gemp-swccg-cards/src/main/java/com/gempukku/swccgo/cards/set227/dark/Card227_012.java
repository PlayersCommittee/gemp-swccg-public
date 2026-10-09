package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DrivingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerBySideEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Character
 * Subtype: Alien
 * Title: Run'heb Voend
 */
public class Card227_012 extends AbstractAlien {
    public Card227_012() {
        super(Side.DARK, 3, 2, 2, 2, 4, "Run'heb Voend", Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setLore("Jawa mechanic.");
        setGameText("Once per game, may [DOWNLOAD] a Sandcrawler here. While driving a Sandcrawler it moves for free, may not be targeted by weapons and adds one battle destiny. Once during your turn, may select a player to activate 1 Force.");
        setSpecies(Species.JAWA);
        addIcons(Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition drivingSandcrawler = new DrivingCondition(self, Filters.sandcrawler);
        Filter sandcrawlerDriven = Filters.and(Filters.sandcrawler, Filters.hasDriving(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesForFreeModifier(self, sandcrawlerDriven));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, sandcrawlerDriven));
        modifiers.add(new AddsBattleDestinyModifier(self, drivingSandcrawler, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId downloadId = GameTextActionId.RUNHEB_VOEND__DOWNLOAD_SANDCRAWLER;
        if (GameConditions.isOncePerGame(game, self, downloadId)
            && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, downloadId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, downloadId);
            action.setText("Deploy a Sandcrawler here from Reserve Deck");
            action.appendUsage(
                new OncePerGameEffect(action));
            action.appendEffect(
                new DeployCardToLocationFromReserveDeckEffect(action, Filters.sandcrawler, Filters.here(self), true));
            actions.add(action);
        }

        GameTextActionId activateId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isDuringYourTurn(game, self)
            && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, activateId)) {

            final String opponent = game.getOpponent(playerId);
            boolean playerCanActivate = GameConditions.canActivateForce(game, playerId);
            boolean opponentCanActivate = GameConditions.canActivateForce(game, opponent);

            if (playerCanActivate && opponentCanActivate) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, activateId);
                action.setText("Choose player to activate Force");
                action.appendUsage(
                    new OncePerTurnEffect(action));
                action.appendTargeting(
                    new ChoosePlayerBySideEffect(action, playerId) {
                        @Override
                        protected void playerChosen(SwccgGame game, final String playerChosen) {
                            action.appendEffect(
                                new ActivateForceEffect(action, playerChosen, 1));
                        }
                    }
                );
                actions.add(action);
            }
            else if (playerCanActivate) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, activateId);
                action.setText("Activate 1 Force");
                action.appendUsage(
                    new OncePerTurnEffect(action));
                action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
                actions.add(action);
            }
            else if (opponentCanActivate) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, activateId);
                action.setText("Make opponent activate 1 Force");
                action.appendUsage(
                    new OncePerTurnEffect(action));
                action.appendEffect(
                    new ActivateForceEffect(action, opponent, 1));
                actions.add(action);
            }
        }
        return actions;
    }
}
