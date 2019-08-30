package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.*;


/**
 * Set: Set 9
 * Type: Objective
 * Title: They Have No Idea We're Coming / Until We Win, Or The Chances Are Spent
 */
public class Card209_029_BACK extends AbstractObjective {
    public Card209_029_BACK() {
        super(Side.LIGHT, 7, Title.Until_We_Win_Or_The_Chances_Are_Spent);
        setGameText("While this side up, your spies are defense value +2 (and power +1 if with Stardust) and are immune to Undercover. While Stardust on your spy, opponent may not cancel your Force drains at battlegrounds. Once per turn, may place a Rebel in your Lost Pile out of play to cancel a just drawn destiny targeting the ability or defense value of your non-undercover Rebel or to make a regular move with your spy during your control phase. Flip this card if you do not occupy two Scarif locations (unless Rogue One at a Scarif site you occupy).");
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_9);
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {

        String playerId = self.getOwner();
        String opponentPlayerId = game.getOpponent(playerId);

        Filter yourSpyfilter = Filters.and(Filters.your(self), Filters.spy);

        // While this side up, your spies are defense value +2 (and power +1 if with Stardust) and are immune to Undercover.
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, yourSpyfilter, 2));
        modifiers.add(new PowerModifier(self, Filters.and(yourSpyfilter, Filters.with(self, Filters.Stardust)), 1));
        modifiers.add(new ImmuneToTitleModifier(self, yourSpyfilter, Title.Undercover));

        // While Stardust on your spy, opponent may not cancel your Force drains at battlegrounds.
        Filter spyWithStardust = Filters.and(yourSpyfilter, Filters.hasAttached(Filters.Stardust));
        OnTableCondition spyWithStardustCondition = new OnTableCondition(self, spyWithStardust);
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.battleground, spyWithStardustCondition, opponentPlayerId, playerId));

        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.UNTIL_WE_WIN_OR_THE_CHANCES_ARE_SPENT__CANCEL_DESTINY_OR_MOVE;

        final Filter rebelFilter = Filters.and(Filters.Rebel, Filters.character);

        // Check condition(s) - Once per turn, may place a Rebel in your Lost Pile out of play to ... make a regular
        // move with your spy during your control phase.
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId))
        {
            // Look for spies who can move (Modified version of Black Sun Fleet)
            Collection<PhysicalCard> spies = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.spy));
            if (!spies.isEmpty()) {
                List<PhysicalCard> validSpies = new ArrayList<PhysicalCard>();
                for (PhysicalCard spy : spies) {
                    if (Filters.movableAsRegularMove(playerId, false, 0, false, Filters.any).accepts(game, spy)) {
                        validSpies.add(spy);
                    }
                }

                if (!validSpies.isEmpty()) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Place Rebel in Lost Pile out of play");
                    action.setActionMsg("Move your spy");

                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerTurnEffect(action));

                    // Choose target(s) - one spy may move
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose spy to move", Filters.in(validSpies)) {
                                @Override
                                protected void cardSelected(PhysicalCard rebel) {

                                    // Pay cost(s)
                                    action.appendCost(
                                            new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, playerId, rebelFilter, false));

                                    action.addAnimationGroup(rebel);
                                    action.setActionMsg("Have " + GameUtils.getCardLink(rebel) + " make a regular move");

                                    // Perform result(s)
                                    action.appendEffect(
                                            new MoveCardAsRegularMoveEffect(action, playerId, rebel, false, false, Filters.any));
                                }
                            }
                    );

                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {

        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        Filter rebelFilter = Filters.and(Filters.Rebel, Filters.character, Filters.not(Filters.undercover_spy));

        // Check conditions(s) - Once per turn, may place a Rebel in your Lost Pile out of play to cancel a just drawn
        // destiny targeting the ability or defense value of your Rebel
        GameTextActionId gameTextActionId = GameTextActionId.UNTIL_WE_WIN_OR_THE_CHANCES_ARE_SPENT__CANCEL_DESTINY_OR_MOVE;
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && TriggerConditions.isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(game, effectResult, rebelFilter)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Rebel in Lost Pile out of play");
            action.setActionMsg("Cancel just drawn " + ((DestinyDrawnResult) effectResult).getDestinyType().getHumanReadable());
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, playerId, rebelFilter, false));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));

            actions.add(action);
        }
        return actions;
    }



    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        Filter atScarifSiteYouOccupy = Filters.at(Filters.and(Filters.site, Filters.Scarif_location, Filters.occupies(self.getOwner())));

        // Check condition(s) - Flip this card if you do not occupy two Scarif locations (unless Rogue One at a Scarif site you occupy).
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.occupies(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Scarif_location)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Rogue_One, atScarifSiteYouOccupy))) {

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
}