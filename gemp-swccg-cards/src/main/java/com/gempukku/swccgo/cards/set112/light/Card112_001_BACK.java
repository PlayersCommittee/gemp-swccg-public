package com.gempukku.swccgo.cards.set112.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.takeandputcards.StackCardFromHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Objective
 * Title: Agents In The Court / No Love For The Empire
 */
public class Card112_001_BACK extends AbstractObjective {
    public Card112_001_BACK() {
        super(Side.LIGHT, 7, Title.No_Love_For_The_Empire);
        setGameText("While this side up, once per turn you may cancel a Force drain or a just-drawn battle destiny by placing here from hand a copy of your Rep. Once during each of your control phases, you may retrieve a non-unique alien of your Rep's species. When two of your aliens are battling at any Tatooine location, add one destiny to total power. For remainder of game, your Rep may deploy from here as if from hand. Flip this card if you do not occupy 2 battleground sites.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        PhysicalCard rep = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;
        if (rep != null) {
            return  "Rep is " + GameUtils.getCardLink(rep);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard rep = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;
        if (rep == null) {
            return null;
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiated(game, effectResult)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.hasInHand(game, playerId, Filters.sameTitle(rep))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Force drain");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new StackCardFromHandEffect(action, playerId, self, false, Filters.sameTitle(rep)));
            // Perform result(s)
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.hasInHand(game, playerId, Filters.sameTitle(rep))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new StackCardFromHandEffect(action, playerId, self, false, Filters.sameTitle(rep)));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard rep = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;
        if (rep == null) {
            return null;
        }
        final Species repSpecies = rep.getBlueprint().getSpecies();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a non-unique alien of Rep's species");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.and(Filters.non_unique, Filters.alien, Filters.species(repSpecies))) {
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsDestinyToPowerModifier(self, new AndCondition(new DuringBattleAtCondition(Filters.Tatooine_location),
                new DuringBattleWithParticipantCondition(2, Filters.and(Filters.your(self), Filters.alien))), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.occupies(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.battleground_site)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
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