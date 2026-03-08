package com.gempukku.swccgo.cards.set226.dark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainBonusesMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Set: SET 26
 * Type: Objective
 * Title: This Deal Is Getting Worse All The Time / Pray I Don't Alter It Any Further (V)
 */
public class Card226_012_BACK extends AbstractObjective {
    public Card226_012_BACK() {
        super(Side.DARK, 7, Title.Pray_I_Dont_Alter_It_Any_Further, ExpansionSet.SET_26, Rarity.V);
        setGameText("While this side up, Sense and Alter may not be played. Force drain bonuses at same site as your Lando or your Lobot may not be canceled. While Vader at a Bespin location, game text of Admiral's Orders is canceled. If your alien/Imperial pair in battle, your total battle destiny is +2. Once per battle involving your Lando (twice if any Lobot also there), may add or subtract 1 from a just drawn destiny. Flip this card if opponent controls more Bespin locations than you.");
        addIcons(Icon.CLOUD_CITY, Icon.PREMIUM, Icon.VIRTUAL_SET_26);
        setVirtualSuffix(true);
    }
    
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        String playerId = self.getOwner();

        Condition vaderAtBespin = new AtCondition(self, Filters.Vader, Filters.Bespin_location);

        //For remainder of game
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.Admirals_Order, Filters.and(Icon.DEATH_STAR_II, Filters.Executor)), playerId));

        //While this side up
        modifiers.add(new MayNotPlayModifier(self, Filters.or(Filters.Sense, Filters.Alter)));
        modifiers.add(new ForceDrainBonusesMayNotBeCanceledModifier(self, Filters.your(playerId), Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.or(Filters.Lando, Filters.Lobot)))));
        modifiers.add(new CancelsGameTextModifier(self, Filters.Admirals_Order, vaderAtBespin));
        modifiers.add(new TotalBattleDestinyModifier(self,
                        new InBattleCondition(self, Filters.and(Filters.your(self), Filters.alien, Filters.with(self, Filters.and(Filters.your(self), Filters.Imperial, Filters.participatingInBattle)))),
                        2, playerId));

        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter yourLandoFilter = Filters.and(Filters.your(playerId), Filters.Lando, Filters.movableAsRegularMove(playerId, false, 0, false, Filters.any));
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, yourLandoFilter)) {

            PhysicalCard yourLandoCard = Filters.findFirstActive(game, self, yourLandoFilter);

            if (yourLandoCard != null) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Have your Lando make a regular move");
                action.setActionMsg("Have " + GameUtils.getCardLink(yourLandoCard) + " make a regular move");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new MoveCardAsRegularMoveEffect(action, playerId, yourLandoCard, false, false, Filters.any));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.THIS_DEAL_IS_GETTING_WORSE_ALL_THE_TIME_V__UPLOAD_CARD;
        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Dark Deal, Vader's Bounty, or [Special Edition] Bespin into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Dark_Deal, Filters.Vaders_Bounty, Filters.and(Icon.SPECIAL_EDITION, Filters.Bespin_system)), true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_4;
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (Filters.countTopLocationsOnTable(game, Filters.and(Filters.Bespin_location, Filters.controls(opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE))) >
                Filters.countTopLocationsOnTable(game, Filters.and(Filters.Bespin_location, Filters.controls(playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE))))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if ((TriggerConditions.isDestinyJustDrawn(game, effectResult))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.Lando))) {

            int numTimes = GameConditions.canSpot(game, self, Filters.and(Filters.Lobot, Filters.participatingInBattle)) ? 2 : 1;

            if (GameConditions.isNumTimesPerBattle(game, self, playerId, numTimes, gameTextSourceCardId, gameTextActionId))
            {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Add or subtract 1 from destiny draw");
                // Update usage limit(s)
                action.appendUsage(
                        new NumTimesPerBattleEffect(action, numTimes));
                // Perform result(s)
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new MultipleChoiceAwaitingDecision("Choose an option", new String[]{"Add 1", "Subtract 1"}) {
                                    @Override
                                    protected void validDecisionMade(int index, String result) {
                                        if (index == 0) {
                                            action.appendEffect(
                                                    new ModifyDestinyEffect(action, 1));
                                        } else {
                                            action.appendEffect(
                                                    new ModifyDestinyEffect(action, -1));
                                        }
                                    }                                    
                                }
                        )
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
