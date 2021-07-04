package com.gempukku.swccgo.cards.set214.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Starship
 * Subtype: Starfighter
 * Title: ARC-170 Starfighter
 */
public class Card214_014 extends AbstractStarfighter {
    public Card214_014() {
        super(Side.LIGHT, 3, 3, 3, null, 4, 4, 6, "ARC-170 Starfighter");
        setGameText("May add 1 pilot or passenger. Permanent pilot provides ability of 2. Opponent may not 'react' to or from here. During battle, if about to be lost before the damage segment, lost at end of battle instead.");
        addIcons(Icon.CLONE_ARMY, Icon.EPISODE_I, Icon.REPUBLIC, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_14, Icon.SCOMP_LINK, Icon.PILOT);
        addModelType(ModelType.RECONNAISSANCE_FIGHTER);
        setPilotOrPassengerCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter here = Filters.here(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotReactToLocationModifier(self, here, opponent));
        modifiers.add(new MayNotReactFromLocationModifier(self, here, opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToBeLost(game, effectResult, self)
                && GameConditions.isDuringBattle(game)
                && !GameConditions.isDamageSegmentOfBattle(game)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard cardToBeLost = self;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(cardToBeLost) + " lost at end of battle");
            action.setActionMsg("Make " + GameUtils.getCardLink(cardToBeLost) + " lost at end of battle instead");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);

                            final int cardId = self.getCardId();
                            action.appendEffect(
                                    new AddUntilEndOfGameActionProxyEffect(action,
                                            new AbstractActionProxy() {
                                                @Override
                                                public List<TriggerAction> getRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult) {
                                                    List<TriggerAction> actions = new LinkedList<TriggerAction>();

                                                    GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_2;

                                                    // Check condition(s)
                                                    if (Filters.onTable.accepts(game,cardToBeLost)
                                                        &&(TriggerConditions.battleEnded(game, effectResult)
                                                            || TriggerConditions.battleCanceled(game, effectResult))) {
                                                        final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId2);
                                                        action2.setRepeatableTrigger(true);
                                                        action2.setText("Make "+GameUtils.getFullName(cardToBeLost)+" lost");
                                                        action2.appendEffect(new LoseCardFromTableEffect(action2, cardToBeLost, true));
                                                        actions.add(action2);
                                                    }
                                                    return actions;
                                                }
                                            }
                                    ));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
