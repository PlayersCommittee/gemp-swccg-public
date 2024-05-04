package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractPodracer;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.RaceDestinyModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Podracer
 * Title: Dud Bolt's Podracer
 */
public class Card11_096 extends AbstractPodracer {
    public Card11_096() {
        super(Side.DARK, 3, "Dud Bolt's Podracer", ExpansionSet.TATOOINE, Rarity.C);
        setLore("RS 557 Podracer. Secretly hired by Sebulba to protect him during Podraces.");
        setGameText("Deploy on Podrace Arena. Adds 1 to each of your race destinies here. If Sebulba's Podracer on table, may lose this Podracer to cancel Losing Track or Neck And Neck; place all race destinies here in owner's Used Pile.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Podrace_Arena;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new RaceDestinyModifier(self, Filters.and(Filters.raceDestinyForPlayer(playerId), Filters.stackedOn(self)), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Losing_Track, Filters.Neck_And_Neck))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.canSpot(game, self, Filters.Sebulbas_Podracer)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendCost(new PassthruEffect(action) {
                @Override
                protected void doPlayEffect(SwccgGame game) {
                    Collection<PhysicalCard> raceDestinies = Filters.filter(game.getGameState().getStackedCards(self), game, Filters.raceDestiny);
                    action.appendCost(
                            new PutStackedCardsInUsedPileEffect(action, playerId, raceDestinies, false));
                    action.appendCost(
                            new LoseCardFromTableEffect(action, self));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}