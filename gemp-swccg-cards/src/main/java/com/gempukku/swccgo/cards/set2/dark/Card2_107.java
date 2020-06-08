package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.conditions.UndercoverCondition;
import com.gempukku.swccgo.cards.evaluators.PowerEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardToLocationEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Droid
 * Title: U-3PO (Yoo-Threepio)
 */
public class Card2_107 extends AbstractDroid {
    public Card2_107() {
        super(Side.DARK, 3, 3, 1, 3, "U-3PO (Yoo-Threepio)", Uniqueness.UNIQUE);
        setLore("This protocol droid served in the House of Alderaan's Diplomatic Corps. Imperials altered its programming for espionage, making the droid an unwitting spy for the Empire.");
        setGameText("Deploy on opponent's side as an Undercover spy (except that if present during a battle at a site, adds its power to Light Side). If spy's 'cover is broken,' the above game text is canceled and Light Side may use as if stolen.");
        addModelType(ModelType.PROTOCOL);
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.SPY);
        setDeploysAsUndercoverSpy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.battleLocation, new AndCondition(new UndercoverCondition(self),
                new DuringBattleAtCondition(Filters.sameSite(self)), new PresentCondition(self)),
                new PowerEvaluator(self), game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.coverBroken(game, effectResult, self)) {
            final String opponent = game.getOpponent(self.getOwner());

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Choose whether to 'steal'");
            action.setActionMsg("Have " + opponent + " choose whether to 'steal'" + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, opponent,
                            new YesNoDecision("Do you want to 'steal' " + GameUtils.getCardLink(self) + "?") {
                                @Override
                                protected void yes() {
                                    action.appendEffect(
                                            new StealCardToLocationEffect(action, opponent, self));
                                }
                                @Override
                                protected void no() {
                                    game.getGameState().sendMessage(opponent + " chooses not to 'steal' " + GameUtils.getCardLink(self));
                                }

                            }
                    )
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
