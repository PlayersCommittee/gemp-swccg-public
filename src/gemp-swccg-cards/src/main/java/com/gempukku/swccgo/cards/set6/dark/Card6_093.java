package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringAttackWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerAttackEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfAttackModifierEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NumDestinyDrawsDuringAttackModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Amanin
 */
public class Card6_093 extends AbstractAlien {
    public Card6_093() {
        super(Side.DARK, 3, 3, 3, 1, 2, "Amanin", Uniqueness.UNRESTRICTED, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Amanin are a primitive hunting species. Exposed to space travel when their planet became a mining world for the Empire. Fierce-tempered when angered.");
        setGameText("When attacking or being attacked by a creature, power +3 and may add one destiny. If Amanin is alone and causes a non-selective creature to be lost, creature is placed out of play and you may retrieve Force equal to its deploy cost.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.AMANIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, new DuringAttackWithParticipantCondition(self), 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isDuringAttackWithParticipant(game, self)
                && GameConditions.isOncePerAttack(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one destiny");
            action.appendUsage(
                    new OncePerAttackEffect(action));
            action.appendEffect(
                    new AddUntilEndOfAttackModifierEffect(action, new NumDestinyDrawsDuringAttackModifier(self, 1, playerId), null));

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        //If Amanin is alone and causes a non-selective creature to be lost, creature is placed out of play and you may retrieve Force equal to its deploy cost.
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.creature, Filters.not(Icon.SELECTIVE_CREATURE)))
                && GameConditions.isAlone(game, self)) {
            final PhysicalCard creatureLost = ((LostFromTableResult) effectResult).getCard();

            //check if Amanin "caused" it to be lost (defeating it in an attack or Amanin using a weapon to make it lost)
            boolean defeatedInAttack = GameConditions.isDuringAttack(game)
                    && game.getGameState().getAttackState().getCardsAttacking().contains(self)
                    && game.getGameState().getAttackState().getCardsDefending().contains(creatureLost);
            boolean amaninHitItWithAWeapon = game.getModifiersQuerying().wasHitOrMadeLostByWeapon(creatureLost, Filters.and(self));

            if (defeatedInAttack || amaninHitItWithAWeapon) {
                final String playerId = self.getOwner();
                final float amountToRetrieve = game.getModifiersQuerying().getDeployCost(game.getGameState(), creatureLost);

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place creature out of play");
                action.appendEffect(new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, creatureLost.getOwner(), creatureLost, false));
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new YesNoDecision("Retrieve " + amountToRetrieve + " Force?") {
                                    @Override
                                    protected void yes() {
                                        game.getGameState().sendMessage(playerId + " chooses to retrieve " + amountToRetrieve + " Force");
                                        action.appendEffect(
                                                new RetrieveForceEffect(action, playerId, amountToRetrieve));
                                    }
                                    @Override
                                    protected void no() {
                                        game.getGameState().sendMessage(playerId + " chooses not to retrieve " + amountToRetrieve + " Force");
                                    }
                                }
                        )
                );

                return Collections.singletonList(action);
            }
        }

        return null;
    }
}
