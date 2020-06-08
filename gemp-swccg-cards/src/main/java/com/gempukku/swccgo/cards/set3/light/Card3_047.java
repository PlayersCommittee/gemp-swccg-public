package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ModifyDestinyAboutToBeDrawnEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: One More Pass
 */
public class Card3_047 extends AbstractUsedInterrupt {
    public Card3_047() {
        super(Side.LIGHT, 5, Title.One_More_Pass, Uniqueness.UNIQUE);
        setLore("'Cable out. Let 'er go!'");
        setGameText("If you are about to draw power harpoon weapon destiny, add ability of one pilot aboard same vehicle.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isAboutToDrawWeaponDestiny(game, effectResult, playerId, Filters.power_harpoon)) {
            final PhysicalCard cardFiringWeapon = game.getGameState().getWeaponFiringState().getCardFiringWeapon();
            if (cardFiringWeapon != null
                    && Filters.vehicle.accepts(game, cardFiringWeapon)) {
                List<Float> abilityOfPilots = game.getModifiersQuerying().getAbilityOfPilotsAboard(game.getGameState(), cardFiringWeapon);
                if (!abilityOfPilots.isEmpty()) {
                    if (abilityOfPilots.size() == 1) {
                        final float abilityToAdd = abilityOfPilots.iterator().next();

                        final PlayInterruptAction action = new PlayInterruptAction(game, self);
                        action.setText("Add " + GuiUtils.formatAsString(abilityToAdd) + " to weapon destiny");
                        // Allow response(s)
                        action.allowResponses(
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ModifyDestinyAboutToBeDrawnEffect(action, abilityToAdd));
                                    }
                                }
                        );
                        return Collections.singletonList(action);
                    }
                    else {

                        final PlayInterruptAction action = new PlayInterruptAction(game, self);
                        action.setText("Add to weapon destiny");
                        List<String> textChoicesList = new ArrayList<String>();
                        Collections.sort(abilityOfPilots);
                        for (Float ability : abilityOfPilots) {
                            textChoicesList.add(String.valueOf(ability));
                        }
                        String[] textChoices = textChoicesList.toArray(new String[textChoicesList.size()]);

                        // Choose target(s)
                        action.appendTargeting(
                                new PlayoutDecisionEffect(action, playerId,
                                        new MultipleChoiceAwaitingDecision("Choose ability to add", textChoices) {
                                            @Override
                                            protected void validDecisionMade(int index, String result) {
                                                final float abilityToAdd = Float.valueOf(result);
                                                // Allow response(s)
                                                action.allowResponses("Add " + GuiUtils.formatAsString(abilityToAdd) + " to weapon destiny",
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new ModifyDestinyAboutToBeDrawnEffect(action, abilityToAdd));
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                )
                        );
                        return Collections.singletonList(action);
                    }
                }
            }

        }
        return null;
    }
}