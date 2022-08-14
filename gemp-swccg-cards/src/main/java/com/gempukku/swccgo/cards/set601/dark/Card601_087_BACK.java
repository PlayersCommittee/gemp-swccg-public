package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.TrueCondition;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Objective
 * Title: Hunt Down And Destroy The Jedi / Their Fire Has Gone Out Of The Universe (V)
 */
public class Card601_087_BACK extends AbstractObjective {
    public Card601_087_BACK() {
        super(Side.DARK, 7, Title.Their_Fire_Has_Gone_Out_Of_The_Universe);
        setVirtualSuffix(true);
        setGameText("While this side up, opponent's Force drain bonuses are canceled and your Force drains at battlegrounds may not be canceled.  During your control phase, may retrieve 1 Force.  During your control phase, may use 2 Force to take any one card without ability into hand from Reserve Deck; reshuffle.  If targeting a vehicle or starship with Lightsaber Parry, Tarkin's Orders, or There Is No Conflict, that destiny draw is +2.\n" +
                "Flip this card if opponent has a unique (•) character of ability > 3 present at a battleground site.");
        addIcons(Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_4);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.and(Icon.EPISODE_I, Filters.Dark_Jedi), self.getOwner()));

        modifiers.add(new CancelOpponentsForceDrainBonusesModifier(self, new TrueCondition()));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.battleground, playerId));
        //TODO If targeting a vehicle or starship with Lightsaber Parry, Tarkin's Orders, or There Is No Conflict, that destiny draw is +2.
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasLostPile(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            action.setActionMsg("Retrieve 1 Force");
            action.appendUsage(new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__THEIR_FIRE_HAS_GONE_OUT_OF_THE_UNIVERSE_V__UPLOAD_CARD;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 2)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a card into hand from Reserve Deck");
            action.setActionMsg("Take a card without ability into hand from Reserve Deck");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.not(Filters.hasAbilityOrHasPermanentPilotWithAbility), true, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.unique, Filters.character, Filters.abilityMoreThan(3), Filters.at(Filters.battleground_site)))
                    || (!GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Vader)
                        && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Galen)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Whenever a character hit by Galen's Lightsaber or Vader's Lightsaber leaves table, opponent loses 2 Force.
        if (TriggerConditions.justHitBy(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.or(Persona.GALENS_LIGHTSABER, Persona.VADERS_LIGHTSABER))) {
            PhysicalCard justHitCard = ((HitResult)effectResult).getCardHit();

            if (justHitCard != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.skipInitialMessageAndAnimation();
                action.setSingletonTrigger(true);
                action.setActionMsg(null);

                final int permCardIdSelf = self.getPermanentCardId();
                final int permCardIdHitCharacter = justHitCard.getPermanentCardId();
                action.appendEffect(new AddUntilEndOfTurnActionProxyEffect(action, new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        List<TriggerAction> actions1 = new LinkedList<TriggerAction>();

                        //might need to check for being restored to normal

                        PhysicalCard objective = game.findCardByPermanentId(permCardIdSelf);
                        PhysicalCard hitCharacter = game.findCardByPermanentId(permCardIdHitCharacter);

                        if (TriggerConditions.leavesTable(game, effectResult, Filters.samePermanentCardId(hitCharacter))) {
                            RequiredGameTextTriggerAction action1 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                            action1.setPerformingPlayer(objective.getOwner());
                            action1.setSingletonTrigger(true);
                            action1.setText("Lose 2 Force");
                            action1.setActionMsg("Lose 2 Force whenever a character hit by Galen's Lightsaber or Vader's Lightsaber leaves table");
                            action1.appendEffect(new LoseForceEffect(action1, game.getOpponent(objective.getOwner()), 2));
                            actions1.add(action1);
                        }
                        return actions1;
                    }
                }));

                actions.add(action);
            }
        }
        return actions;
    }
}
