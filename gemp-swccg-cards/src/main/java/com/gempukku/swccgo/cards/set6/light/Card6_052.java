package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.BreakCoverEffect;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutUndercoverEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Effect
 * Subtype: Immediate
 * Title: A Gift
 */
public class Card6_052 extends AbstractImmediateEffect {
    public Card6_052() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.A_Gift, Uniqueness.UNIQUE);
        setLore("'As a token of my good will, I present to you a gift: these two droids. Both are hardworking and will serve you well.'");
        setGameText("If you just moved a droid to Audience Chamber, deploy on the droid. Droid is an Undercover spy. Wherever opponent has an alien, opponent's battle destiny draws are -2 and Force drains are -1. Immediate Effect canceled if droid leaves Tatooine. (Immune to Control.)");
        addIcons(Icon.JABBAS_PALACE);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.movedToLocationBy(game, effectResult, playerId, Filters.droid, Filters.Audience_Chamber)) {
            Collection<PhysicalCard> droidsMoved = Filters.filter(((MovedResult) effectResult).getMovedCards(), game, Filters.droid);
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.in(droidsMoved), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.droid;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter whereverOpponentHasAlien = Filters.and(Filters.sameLocationAs(self, Filters.and(Filters.opponents(self), Filters.alien)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.hasAttached(self), Keyword.SPY));
        modifiers.add(new EachBattleDestinyModifier(self, whereverOpponentHasAlien, -2, opponent));
        modifiers.add(new ForceDrainModifier(self, whereverOpponentHasAlien, -1, opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard attachedTo = self.getAttachedTo();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());
            action.setText("Put " + GameUtils.getFullName(attachedTo) + " Undercover");
            action.setActionMsg("Put " + GameUtils.getCardLink(attachedTo) + " Undercover");
            // Perform result(s)
            action.appendEffect(
                    new PutUndercoverEffect(action, attachedTo));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.coverBroken(game, effectResult, Filters.hasAttached(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)
                && !GameConditions.isOnSystem(game, self, Title.Tatooine)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)) {
            PhysicalCard lostFromAttachedTo = ((LostFromTableResult) effectResult).getFromAttachedTo();
            if (lostFromAttachedTo != null && lostFromAttachedTo.isUndercover()) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Break " + GameUtils.getFullName(lostFromAttachedTo) + "'s cover");
                action.setActionMsg("Break " + GameUtils.getCardLink(lostFromAttachedTo) + "'s cover");
                // Perform result(s)
                action.appendEffect(
                        new BreakCoverEffect(action, lostFromAttachedTo));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}