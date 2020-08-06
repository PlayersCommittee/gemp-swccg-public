package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


////You Know Who I Answer To
////DARK - OBJECTIVE 7
////Immediately recirculate; may 'peek' at top two cards of Reserve Deck and take one into hand.
////While this side up, your starships and armed characters are power and immunity to attrition +2. If your gangster leader in battle, may add one destiny to your total power.
////Flip this card at the end of each turn; you may retrieve a blaster or character with “Black Sun,” “Crimson Dawn” or “Hutt” in lore and, if you occupy 3 battlegrounds, opponent loses 1 Force.

/**
 * Set: Set 13
 * Type: Objective
 * Title: Shadow Collective / You Know Who I Answer To
 */
public class Card501_058_BACK extends AbstractObjective {
    public Card501_058_BACK() {
        super(Side.DARK, 7, "You Know Who I Answer To");
        setGameText("Immediately recirculate; may 'peek' at top two cards of Reserve Deck and take one into hand." +
                "While this side up, your starships and armed characters are power and immunity to attrition +2. If your gangster leader in battle, may add one destiny to your total power." +
                "Flip this card at the end of each turn; you may retrieve a blaster or character with “Black Sun,” “Crimson Dawn” or “Hutt” in lore and, if you occupy 3 battlegrounds, opponent loses 1 Force.");
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
            action.setText("Recirculate and peek at cards.");
            action.setActionMsg("Recirculate and peek at cards.");

            action.appendEffect(
                    new RecirculateEffect(action, playerId)
            );
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, 2, 1, 1)
            );

            actions.add(action);
        }

        //Flip this card at the end of each turn; you may retrieve a blaster (if you occupy 3 battlegrounds, opponent loses 1 Force).
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            if (GameConditions.occupies(game, playerId, 3, Filters.battleground)) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new YesNoDecision("Retrieve a card and make opponent lose 1 force?") {
                                    @Override
                                    protected void yes() {
                                        action.appendEffect(
                                                new RetrieveCardEffect(action, playerId, Filters.or(Filters.blaster, Filters.and(Filters.character, Filters.or(Filters.loreContains("Black Sun"), Filters.loreContains("Crimson Dawn"), Filters.loreContains("Hutt")))))
                                        );
                                    }
                                }
                        )
                );
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(playerId), 1)
                );
                actions.add(action);
            } else {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new YesNoDecision("Retrieve a card?") {
                                    @Override
                                    protected void yes() {
                                        action.appendEffect(
                                                new RetrieveCardEffect(action, playerId, Filters.or(Filters.blaster, Filters.loreContains("Black Sun"), Filters.loreContains("Crimson Dawn"), Filters.loreContains("Hutt")))
                                        );
                                    }
                                }
                        )
                );
                actions.add(action);
            }
        }

        return actions;

    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        //While this side up, your starships and armed characters are power and immunity to attrition +2.
        List<Modifier> modifiers = new LinkedList<>();
        Filter starshipsAndArmedCharacters = Filters.and(Filters.your(self.getOwner()), Filters.or(Filters.starship, Filters.and(Filters.character, Filters.armedWith(Filters.any))));
        modifiers.add(new PowerModifier(self, starshipsAndArmedCharacters, 2));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, starshipsAndArmedCharacters, 2));
        //From front side
        Filter independentStarships = Filters.and(Icon.INDEPENDENT, Filters.starship);
        Filter v13Maul = Filters.and(Icon.VIRTUAL_SET_13, Filters.Maul);
        Filter cardsThatMayNotDeploy = Filters.and(Filters.hasAbilityOrHasPermanentPilotWithAbility, Filters.not(Filters.or(independentStarships, v13Maul, Filters.alien)));
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.your(self.getOwner()), cardsThatMayNotDeploy), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId1 = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.and(Filters.leader, Keyword.GANGSTER)))
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

        GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId2, Phase.DEPLOY)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId2);
            action.setText("Deploy a card from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action)
            );
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.and(Filters.non_unique, Filters.blaster), Filters.titleContains("First Light")), true)
            );
            actions.add(action);
        }
        return actions;
    }
}