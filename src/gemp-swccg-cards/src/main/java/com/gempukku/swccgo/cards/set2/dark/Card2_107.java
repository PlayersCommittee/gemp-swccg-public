package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.UndercoverCondition;
import com.gempukku.swccgo.cards.evaluators.PowerEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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
        super(Side.DARK, 3, 3, 1, 3, "U-3PO (Yoo-Threepio)", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLore("This protocol droid served in the House of Alderaan's Diplomatic Corps. Imperials altered its programming for espionage, making the droid an unwitting spy for the Empire.");
        setGameText("Deploys only to a site as an Undercover spy. If Undercover at a battle, adds his power to Light Side. If U-3PO just had his 'cover broken,' Light Side may steal him.");
        addModelType(ModelType.PROTOCOL);
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.SPY);
        setDeploysAsUndercoverSpy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.battleLocation, new AndCondition(new UndercoverCondition(self),
                new DuringBattleAtCondition(Filters.sameLocation(self))),
                new PowerEvaluator(self), game.getLightPlayer()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.coverBroken(game, effectResult, self)
                && GameConditions.canSteal(game, self)) {
            final String lightPlayer = game.getLightPlayer();

            if (!lightPlayer.equals(self.getOwner())) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Choose whether to 'steal'");
                action.setActionMsg("Have " + lightPlayer + " choose whether to 'steal'" + GameUtils.getCardLink(self));
                // Perform result(s)
                action.appendEffect(
                        new PlayoutDecisionEffect(action, lightPlayer,
                                new YesNoDecision("Do you want to 'steal' " + GameUtils.getCardLink(self) + "?") {
                                    @Override
                                    protected void yes() {
                                        action.appendEffect(
                                                new StealCardToLocationEffect(action, lightPlayer, self));
                                    }

                                    @Override
                                    protected void no() {
                                        game.getGameState().sendMessage(lightPlayer + " chooses not to 'steal' " + GameUtils.getCardLink(self));
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
