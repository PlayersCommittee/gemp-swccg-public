package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.FiredWeaponsInBattleCondition;
import com.gempukku.swccgo.cards.effects.CancelForceRetrievalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.AboutToRetrieveForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Aim High
 */
public class Card8_034 extends AbstractNormalEffect {
    public Card8_034() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Aim_High, Uniqueness.UNIQUE);
        setLore("The destruction of a command vehicle negatively impacts Imperial battle efficiency.");
        setGameText("Deploy on table. When you fire two weapons (except lightsabers) in a battle, your total power is +5. Also, whenever opponent retrieves X cards, opponent must first use X Force or that retrieval is canceled. (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String player = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.battleLocation, new FiredWeaponsInBattleCondition(player, 2, Filters.except(Filters.lightsaber)), 5, player));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isAboutToRetrieveForce(game, effectResult, opponent)) {
            final float amountOfForce = ((AboutToRetrieveForceResult) effectResult).getAmountOfForceToRetrieve();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Use Force or cancel retrieval");
            action.setActionMsg("Make " + opponent + " use " + GuiUtils.formatAsString(amountOfForce) + " Force or Force retrieval is canceled");
            if (GameConditions.canUseForce(game, opponent, amountOfForce)) {
                // Ask player to Use Force or retrieval is canceled
                action.appendEffect(
                        new PlayoutDecisionEffect(action, opponent,
                                new YesNoDecision("Do you want to use " + GuiUtils.formatAsString(amountOfForce) + " Force to proceed with Force retrieval?") {
                                    @Override
                                    protected void yes() {
                                        action.appendEffect(
                                                new UseForceEffect(action, opponent, amountOfForce));
                                    }

                                    @Override
                                    protected void no() {
                                        action.appendEffect(
                                                new CancelForceRetrievalEffect(action));
                                    }
                                }
                        )
                );
            }
            else {
                action.appendEffect(
                        new CancelForceRetrievalEffect(action));
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}