package com.gempukku.swccgo.cards.set206.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataNotSetCondition;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotParticipateInBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 6
 * Type: Effect
 * Subtype: Immediate
 * Title: Xizor's Bounty
 */
public class Card206_012 extends AbstractImmediateEffect {
    public Card206_012() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Xizor's Bounty", Uniqueness.UNIQUE);
        setGameText("Use 1 Force to deploy on opponent's just deployed character at same site as a Black Sun agent; character may not battle this turn. If character is about to leave table or be captured, peek at opponent's hand, retrieve 1 Force, and lose this Immediate Effect.");
        addIcons(Icon.VIRTUAL_SET_6);
        addKeywords(Keyword.BOUNTY);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.sameSiteAs(self, Filters.Black_Sun_agent))) {
            PhysicalCard deployedCard = ((PlayCardResult) effectResult).getPlayedCard();
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameCardId(deployedCard), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (!GameConditions.cardHasWhileInPlayDataSet(self)
                && TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(new WhileInPlayData());
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotParticipateInBattleModifier(self, Filters.hasAttached(self), new InPlayDataNotSetCondition(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        Filter characterFilter = Filters.hasAttached(self);

        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, characterFilter)
                || TriggerConditions.isAboutToBeCaptured(game, effectResult, characterFilter)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make lost");
            action.setActionMsg("Peek at opponent's hand, retrieve 1 Force, and make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new PeekAtOpponentsHandEffect(action, playerId));
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}