package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAttemptJediTestsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Subtype: Immediate
 * Title: At Peace
 */
public class Card4_019 extends AbstractImmediateEffect {
    public Card4_019() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "At Peace", Uniqueness.UNIQUE);
        setGameText("");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachTrainingDestinyModifier(self, Filters.hasAttached(self), 3));
        return modifiers;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, final int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isStartOfYourTurn(game, effectResult, self)
            && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_ALL, Filters.apprentice)) {
                final PhysicalCard apprentice = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_ALL, Filters.apprentice);
                PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.apprentice, null);
                if (action != null) {
                    action.setText("Have " + GameUtils.getCardLink(apprentice) + " 'rest'.");
                    action.appendEffect(
                            new AddUntilEndOfPlayersNextTurnModifierEffect(action,  playerId, new MayNotAttemptJediTestsModifier(self, apprentice), GameUtils.getCardLink(apprentice) + " 'rests' until end of turn." )
                    );
                    final int cardId = self.getCardId();
                    final int permCardId = self.getPermanentCardId();
                    final int nextTurnNumber = game.getGameState().getPlayersLatestTurnNumber(playerId) + 1;
                    action.appendEffect(
                            new AddUntilEndOfPlayersNextTurnActionProxyEffect(action,  new AbstractActionProxy() {
                                @Override
                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                    List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                    GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
                                    final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                    // Check condition(s)
                                    if (TriggerConditions.isEndOfYourTurn(game, effectResult, playerId)
                                            && self.getCardId() == cardId
                                            && GameConditions.isTurnNumber(game, nextTurnNumber)
                                            && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_ALL, apprentice)
                                            && GameConditions.canSpot(game, self, Filters.and(Filters.Jedi_Test, Filters.not(Filters.completed_Jedi_Test)))) {

                                        PhysicalCard jediTest = Filters.findFirstActive(game, self, Filters.and(Filters.Jedi_Test, Filters.not(Filters.completed_Jedi_Test)));
                                        RequiredGameTextTriggerAction action1 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                                        action1.setText("Relocate to Jedi Test ");
                                        // Perform result(s)
                                        action1.appendEffect(
                                                new AttachCardFromTableEffect(action1, self, jediTest));
                                        actions.add(action1);
                                    }
                                    return actions;
                                }
                            }, playerId)
                    );
                    return Collections.singletonList(action);
                }

        }
        return null;
    }
}
