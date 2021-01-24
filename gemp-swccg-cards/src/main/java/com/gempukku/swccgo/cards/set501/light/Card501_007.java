package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInLostPileEqualToOrMoreThanCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Character
 * Subtype: Resistance
 * Title: Beaumont Kin
 */
public class Card501_007 extends AbstractResistance {
    public Card501_007() {
        super(Side.LIGHT, 2, 1, 3, 2, 4, "Beaumont Kin", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Opponent's characters deploy cost may not be modified at same and related locations. If you have ten cards in your lost pile, Force drain + 1 here. During battle, may add Beaumont's power to another character present; Beaumont is 'hit'.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_14, Icon.EPISODE_VII);
        setTestingText("Beaumont Kin");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToDeployCostModifiersToLocationModifier(self, Filters.and(Filters.opponents(self.getOwner()), Filters.character), Filters.any, Filters.sameOrRelatedLocation(self)));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new CardsInLostPileEqualToOrMoreThanCondition(self.getOwner(), 10), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isPresentWith(game, self, Filters.character)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Add power to another character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Add power to character", Filters.and(Filters.character, Filters.presentWith(self), Filters.participatingInBattle)) {
                                       @Override
                                       protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                           action.allowResponses(new RespondableEffect(action) {
                                               @Override
                                               protected void performActionResults(Action targetingAction) {
                                                   PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                   //if text says Beaumont this needs to specifically look for Beaumont to interact correctly with Bane Malar
                                                   float toAdd = game.getModifiersQuerying().getPower(game.getGameState(), self);
                                                   action.appendEffect(new ModifyPowerUntilEndOfBattleEffect(action, finalTarget, toAdd));
                                                   action.appendEffect(new HitCardEffect(action, self, self));
                                               }
                                           });

                                       }
                                   }
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}
