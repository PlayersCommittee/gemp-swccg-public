package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsInGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Objective
 * Title: ISB Operations / Empire's Sinister Agents
 */
public class Card7_299 extends AbstractObjective {
    public Card7_299() {
        super(Side.DARK, 0, Title.ISB_Operations, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy any Coruscant location. For remainder of game, your characters with 'ISB,' 'Rebel' or 'Rebellion' in lore are ISB agents and spies, and may deploy regardless of deployment restrictions listed in their game text. Flip this card if ISB agents control at least two Rebel Base locations or if four ISB agents are on table.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Coruscant_location, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Coruscant location to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter yourISBAgents = Filters.and(Filters.your(self), Filters.character,
                Filters.or(Filters.loreContains("ISB"), Filters.loreContains("Rebel"), Filters.loreContains("Rebels"), Filters.loreContains("Rebellion")));
        modifiers.add(new KeywordModifier(self, yourISBAgents, Keyword.ISB_AGENT));
        modifiers.add(new KeywordModifier(self, yourISBAgents, Keyword.SPY));
        modifiers.add(new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self, yourISBAgents));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (GameConditions.canSpot(game, self, 4, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.ISB_agent)
                || GameConditions.controlsWith(game, self, playerId, 2, Filters.Rebel_Base_location, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.ISB_agent))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}