package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Vehicle
 * Subtype: Combat
 * Title: TT-6
 */
public class Card14_124 extends AbstractCombatVehicle {
    public Card14_124() {
        super(Side.DARK, 4, 2, 2, 4, null, 3, 4, "TT-6", Uniqueness.UNIQUE);
        setLore("Primary command liaison transport. Contains the majority of officer battle droids. Has priority to unload as soon as the area is secured. Enclosed.");
        setGameText("May add 7 passengers. Once per turn, may deploy a battle droid leader to same site from Reserve Deck; reshuffle. Immune to attrition < 3 (< 5 if your AAT or STAP present).");
        addModelType(ModelType.MTT);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT, Icon.PRESENCE);
        addKeywords(Keyword.ENCLOSED);
        setPassengerCapacity(7);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.TT6__DOWNLOAD_BATTLE_DROID_LEADER;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a battle droid leader from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.battle_droid, Filters.leader), Filters.sameSite(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 5,
                new PresentCondition(self, Filters.and(Filters.your(self), Filters.or(Filters.AAT, Filters.STAP))))));
        return modifiers;
    }
}
