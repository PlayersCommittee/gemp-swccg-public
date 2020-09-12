package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Objective
 * Title: Shadow Collective / You Know Who I Answer To
 */
public class Card501_058_BACK extends AbstractObjective {
    public Card501_058_BACK() {
        super(Side.DARK, 7, Title.You_Know_Who_I_Answer_To);
        setGameText("May Immediately re-circulate; reshuffle" +
                "While this side up, if your gangster leader in battle with your non-unique blaster, may add one destiny to total power." +
                "Flip this card at end of each turn (opponent loses 1 Force if you occupy 3 battlegrounds).");
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("You Know Who I Answer To");
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        final String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Re-circulate and reshuffle.");
            action.setActionMsg("Re-circulate and reshuffle.");

            action.appendEffect(
                    new RecirculateEffect(action, playerId)
            );
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, playerId)
            );

            actions.add(action);
        }

        //Flip this card at the end of each turn; (if you occupy 3 battlegrounds, opponent loses 1 Force).
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            if (GameConditions.occupies(game, playerId, 3, Filters.battleground)) {
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(playerId), 1)
                );
            }
            actions.add(action);
        }

        return actions;

    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter independentStarships = Filters.and(Icon.INDEPENDENT, Filters.starship);
        Filter episode1BountyHunters = Filters.and(Filters.icon(Icon.EPISODE_I), Filters.bounty_hunter);
        Filter loreCharacters = Filters.or(Filters.loreContains("Crimson Dawn"), Filters.loreContains("Black Sun"), Filters.loreContains("Hutt"));
        Filter cardsThatMayNotDeploy = Filters.or(Filters.and(Filters.icon(Icon.EPISODE_I), Filters.droid), Filters.and(Filters.hasAbilityOrHasPermanentPilotWithAbility, Filters.not(Filters.or(independentStarships, episode1BountyHunters, Filters.assassin, Filters.gangster, loreCharacters))));
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.your(self.getOwner()), cardsThatMayNotDeploy), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId1 = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.and(Filters.leader, Filters.gangster)))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.non_unique, Filters.blaster))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId1)
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId1);
            action.setText("Add 1 destiny to power.");
            action.setActionMsg("Add 1 destiny to power.");
            action.appendUsage(
                    new OncePerBattleEffect(action)
            );
            action.appendEffect(
                    new AddDestinyToTotalPowerEffect(action, 1)
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.SHADOW_COLLECTIVE__DOWNLOAD_BLASTER_OR_FIRST_LIGHT_CARD;
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a card from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new ChooseCardFromReserveDeckEffect(action, playerId, Filters.or(Filters.and(Filters.non_unique, Filters.blaster), Filters.titleContains("First Light"))) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            if (Filters.and(Filters.non_unique, Filters.blaster).accepts(game, selectedCard)) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DeployCardToTargetFromReserveDeckEffect(action, selectedCard, Filters.and(Filters.your(playerId), Filters.alien), false, false, true)
                                );
                            } else {
                                action.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action, Filters.sameCardId(selectedCard), true)
                                );
                            }
                        }
                    });
        }
        return actions;
    }
}