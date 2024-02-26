package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LookAtForcePileEffect;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Objective
 * Title: Shadow Collective / You Know Who I Answer To
 */
public class Card213_032_BACK extends AbstractObjective {
    public Card213_032_BACK() {
        super(Side.DARK, 7, Title.You_Know_Who_I_Answer_To, ExpansionSet.SET_13, Rarity.V);
        setGameText("May immediately re-circulate and shuffle your Reserve Deck." +
                "While this side up, if your gangster leader in battle at same site as your non-unique blaster, may add one destiny to total power. If Maul alone, during your draw phase may peek at the cards in your Force Pile" +
                "Flip this card at end of turn. If you are about to flip this card and you occupy three battlegrounds, opponent loses 1 Force.");
        addIcons(Icon.VIRTUAL_SET_13);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Re-circulate and reshuffle.");
            action.setActionMsg("Re-circulate and reshuffle.");

            action.appendEffect(
                    new RecirculateEffect(action, playerId)
            );
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, playerId)
            );

            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        //Flip this card at the end of each turn; (if you occupy 3 battlegrounds, opponent loses 1 Force).
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }

        return null;
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
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.leader, Filters.gangster, Filters.at(Filters.site)))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.non_unique, Filters.blaster))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 1 destiny to power");
            action.setActionMsg("Add 1 destiny to power");
            action.appendUsage(
                    new OncePerBattleEffect(action)
            );
            action.appendEffect(
                    new AddDestinyToTotalPowerEffect(action, 1)
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_2;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId2, Phase.DRAW)
                && GameConditions.hasForcePile(game, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Maul, Filters.alone))) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId2);
            action.setText("Peek at Force Pile");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendEffect(
                    new LookAtForcePileEffect(action, playerId, playerId));
            actions.add(action);
        }

        GameTextActionId gameTextActionId3 = GameTextActionId.SHADOW_COLLECTIVE__DOWNLOAD_BLASTER_OR_FIRST_LIGHT_CARD;
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId3)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId3)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId3);
            action.setText("Deploy a card from Reserve Deck");
            action.setActionMsg("Deploy a non-unique blaster (or a card with 'First Light' in title) from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.and(Filters.non_unique, Filters.blaster), Filters.titleContains("First Light")), true)
            );
            actions.add(action);
        }
        return actions;
    }
}