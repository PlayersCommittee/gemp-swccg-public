package com.gempukku.swccgo.cards.set226.dark;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.ResetForfeitEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainBonusesMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * Set: SET 26
 * Type: Objective
 * Title: This Deal Is Getting Worse All The Time / Pray I Don't Alter It Any Further (V)
 */
public class Card226_012_BACK extends AbstractObjective {
    public Card226_012_BACK() {
        super(Side.DARK, 7, Title.Pray_I_Dont_Alter_It_Any_Further, ExpansionSet.SET_26, Rarity.V);
        setGameText("While this side up, Sense and Alter may not be played. Force drain bonuses at same site as your Lando or your Lobot may not be canceled. While Vader at a Bespin location, game text of Admiral's Orders is canceled and opponent loses 3 Force if a character was just frozen. If your alien/Imperial pair in battle, your total battle destiny is +2. If your Lando in battle, may target a character present with him; target is forfeit = 0. Flip this card if opponent controls more Bespin locations than you.");
        addIcons(Icon.CLOUD_CITY, Icon.PREMIUM, Icon.VIRTUAL_SET_26);
        setVirtualSuffix(true);
    }
    
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        String playerId = self.getOwner();

        Condition vaderAtBespin = new AtCondition(self, Filters.Vader, Filters.Bespin_location);

        //For remainder of game
        modifiers.add(new MayNotDeployModifier(self, Filters.Admirals_Order, playerId));

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

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        Filter yourLandoInBattleFilter = Filters.and(Filters.your(playerId), Filters.Lando, Filters.participatingInBattle);
        Filter targetFilter = Filters.and(Filters.character, Filters.presentWith(self, yourLandoInBattleFilter), Filters.participatingInBattle);

        Condition endOfBattlCondition = new NotCondition(new DuringBattleCondition());

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSpot(game, self, yourLandoInBattleFilter)
                && GameConditions.canSpot(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reset forfeit to 0");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Reset " + GameUtils.getCardLink(targetedCard) + "'s forfeit to 0",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(final Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ResetForfeitEffect(action, targetedCard, 0, endOfBattlCondition));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;
        // Check condition(s)
        if ((TriggerConditions.frozen(game, effectResult, Filters.character)
                || TriggerConditions.frozenAndCaptured(game, effectResult, Filters.character))
                && GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.at(Filters.Bespin_location)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make " + opponent + " lose 3 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 3));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_4;
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
}
