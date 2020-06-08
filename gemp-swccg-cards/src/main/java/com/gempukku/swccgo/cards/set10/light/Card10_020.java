package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyCalculationVariableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Pulsar Skate
 */
public class Card10_020 extends AbstractStarfighter {
    public Card10_020() {
        super(Side.LIGHT, 2, 2, 2, null, 4, 5, 5, "Pulsar Skate", Uniqueness.UNIQUE);
        setLore("Owned by legendary Terrik family of smugglers. Used to chase down the pirates who killed Wedge's parents. On Corellian Security's most wanted list. 37.5 meters long.");
        setGameText("May add 2 pilots and 6 passengers. May add ability of your (•) unique smuggler aboard to X on Kessel Run targeting that smuggler. When Booster, Mirax or Wedge piloting, Immune to attrition < 5.");
        addPersona(Persona.PULSAR_SKATE);
        addIcons(Icon.REFLECTIONS_II, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.BAUDO_CLASS_STAR_YACHT);
        setPilotCapacity(2);
        setPassengerCapacity(6);
        setMatchingPilotFilter(Filters.or(Filters.Booster, Filters.Mirax, Filters.Wedge));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.or(Filters.Booster, Filters.Mirax, Filters.Wedge)), 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        PhysicalCard kesselRun = Filters.findFirstActive(game, self, Filters.Kessel_Run);
        if (kesselRun != null) {
            PhysicalCard smuggler = Filters.findFirstActive(game, self, Filters.and(Filters.your(self),
                Filters.unique, Filters.smuggler, Filters.hasAbility, Filters.aboard(self), Filters.targetedByCardOnTable(kesselRun)));
            if (smuggler != null) {
                final String combination = kesselRun.getCardId() + "|" + smuggler.getCardId();
                String prevCombination = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getTextValue() : null;
                if (!combination.equals(prevCombination)) {
                    float ability = game.getModifiersQuerying().getAbility(game.getGameState(), smuggler);

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Add " + GuiUtils.formatAsString(ability) + " X on Kessel Run");
                    action.setActionMsg("Add " + GameUtils.getCardLink(smuggler) + "'s ability of " + GuiUtils.formatAsString(ability) + " to X on " + GameUtils.getCardLink(kesselRun));
                    // Update usage limit(s)
                    action.appendUsage(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    self.setWhileInPlayData(new WhileInPlayData(combination));
                                }
                            });
                    // Perform result(s)
                    action.appendEffect(
                            new ModifyCalculationVariableEffect(action, kesselRun, Variable.X, ability));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
