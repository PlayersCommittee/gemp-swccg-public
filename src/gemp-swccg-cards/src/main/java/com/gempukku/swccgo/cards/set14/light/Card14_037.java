package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForceDrainCompletedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Effect
 * Subtype: Immediate
 * Title: They Win This Round
 */
public class Card14_037 extends AbstractImmediateEffect {
    public Card14_037() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "They Win This Round", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setLore("The Queen realized that you must give up one battle in order to have an advantage in the next.");
        setGameText("If opponent just Force drained you at a location, deploy on that location. Opponent's Force drains here are +1. Your battle destiny draws here are +1 If you control this location, may place Immediate Effect in Used Pile to retrieve 4 Force. (Immune to Control.)");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.forceDrainCompleted(game, effectResult, opponent)) {
            PhysicalCard location = ((ForceDrainCompletedResult) effectResult).getLocation();

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameLocationId(location), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), 1, opponent));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), 1, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.controls(game, playerId, Filters.sameLocation(self))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile to retrieve 4 Force");
            action.setActionMsg("Retrieve 4 Force");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 4));
            return Collections.singletonList(action);
        }
        return null;
    }
}