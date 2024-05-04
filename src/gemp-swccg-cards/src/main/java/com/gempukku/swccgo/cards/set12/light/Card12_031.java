package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyPoliticsUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Supreme Chancellor Valorum (AI)
 */
public class Card12_031 extends AbstractRepublic {
    public Card12_031() {
        super(Side.LIGHT, 3, 4, 2, 4, 7, Title.Valorum, Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setAlternateImageSuffix(true);
        setPolitics(5);
        setLore("Although Finis Valorum maintains the Galactic Senate's ultimate title, his real power is mired by endless bureaucracy, petty corruption, and incessant plotting.");
        setGameText("Agendas: justice, order. While in a senate majority, once per turn may use 2 Force to cancel a battle destiny just drawn at another site where you have a Republic Character. Opponent may use X Force; Valorum is politics -X for remainder of turn.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.JUSTICE, Agenda.ORDER));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.isInSenateMajority(game, self)
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.isDuringBattleAt(game, Filters.and(Filters.not(Filters.here(self)),
                Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.Republic, Filters.character))))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 1)) {
            int maxForceToUse = Math.min(GameConditions.forceAvailableToUse(game, playerId), (int) Math.ceil(game.getModifiersQuerying().getPolitics(game.getGameState(), self)));
            if (maxForceToUse > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Reduce politics");
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to use ", 1, maxForceToUse, maxForceToUse) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, result));
                                        action.setActionMsg("Reduce " + GameUtils.getCardLink(self) + "'s politics by " + result);
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ModifyPoliticsUntilEndOfTurnEffect(action, self, -result,
                                                        "Reduces " + GameUtils.getCardLink(self) + "'s politics by " + result));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
