package com.gempukku.swccgo.cards.set214.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInLostPileEqualToOrMoreThanCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToDeployCostModifiersToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Character
 * Subtype: Resistance
 * Title: Beaumont Kin
 */
public class Card214_016 extends AbstractResistance {
    public Card214_016() {
        super(Side.LIGHT, 3, 2, 3, 2, 4, "Beaumont Kin", Uniqueness.UNIQUE, ExpansionSet.SET_14, Rarity.V);
        setLore("");
        setGameText("Deploy cost of opponent's characters may not be modified at same and related locations. If you have ten cards in your Lost Pile, Force drain +1 here. Once during battle, may add Beaumont's power to another character present; Beaumont is 'hit'.");
        addPersona(Persona.BEAUMONT);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_14, Icon.EPISODE_VII);
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
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.character, Filters.other(self), Filters.present(self), Filters.participatingInBattle))
                && GameConditions.canSpot(game, self, Filters.Beaumont)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Add power to another character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Add power to character", Filters.and(Filters.character, Filters.other(self), Filters.present(self), Filters.participatingInBattle)) {
                                       @Override
                                       protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                           action.allowResponses(new RespondableEffect(action) {
                                               @Override
                                               protected void performActionResults(Action targetingAction) {
                                                   PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                   PhysicalCard beaumont = Filters.findFirstActive(game, self, Filters.Beaumont);

                                                   if (beaumont != null) {
                                                       float toAdd = game.getModifiersQuerying().getPower(game.getGameState(), beaumont);
                                                       action.appendEffect(new ModifyPowerUntilEndOfBattleEffect(action, finalTarget, toAdd));
                                                       action.appendEffect(new HitCardEffect(action, beaumont, self));
                                                   }
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
