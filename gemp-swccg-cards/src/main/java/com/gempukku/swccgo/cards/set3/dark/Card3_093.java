package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DefeatedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Creature
 * Title: Wampa
 */
public class Card3_093 extends AbstractCreature {
    public Card3_093() {
        super(Side.DARK, 3, 4, null, 3, 0, "Wampa");
        setLore("Sly, carnivorous beast which stalks the snow-packed tundra. Wampas frequently drag their prey to an ice cave for storage. They always prefer to devour their victims alive.");
        setGameText("* Ferocity = 3 + destiny. Habitat: Hoth sites. Deploy only to Wampa Cave or unoccupied marker site. Defeated characters are eaten or relocated to Wampa Cave (opponent of victim chooses).");
        addModelType(ModelType.SNOW);
        addIcons(Icon.HOTH);
        addKeyword(Keyword.WAMPA);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.Hoth_site;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Wampa_Cave, Filters.and(Filters.marker_site, Filters.unoccupied));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextFerocityModifier(self, 3, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDefeatedBy(game, effectResult, Filters.character, self)) {
            final PhysicalCard wampaCave = Filters.findFirstFromTopLocationsOnTable(game, Filters.Wampa_Cave);
            if (wampaCave != null) {
                final PhysicalCard victim = ((DefeatedResult) effectResult).getCardDefeated();
                if (Filters.canBeRelocatedToLocation(wampaCave, true, false, true, 0, false).accepts(game, victim)) {
                    final String opponentOfVictim = game.getOpponent(victim.getOwner());

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.skipInitialMessageAndAnimation();
                    action.setText("Choose whether to relocate defeated character");
                    action.setActionMsg(null);
                    // Perform result(s)
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, opponentOfVictim,
                                    new YesNoDecision("Do you want " + GameUtils.getCardLink(victim) + " relocated to " + GameUtils.getCardLink(wampaCave) + " instead of eaten?") {
                                        @Override
                                        protected void yes() {
                                            game.getGameState().sendMessage(opponentOfVictim + " chooses to have " + GameUtils.getCardLink(victim) + " relocated to " + GameUtils.getCardLink(wampaCave));
                                            action.appendEffect(
                                                    new RelocateBetweenLocationsEffect(action, victim, wampaCave));
                                        }

                                        @Override
                                        protected void no() {
                                            game.getGameState().sendMessage(opponentOfVictim + " chooses to not have " + GameUtils.getCardLink(victim) + " relocated to " + GameUtils.getCardLink(wampaCave));
                                        }
                                    }
                            )
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
