package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceTopCardOfLostPileOnTopOfReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetTotalAbilityModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Character
 * Subtype: Rebel
 * Title: Leia, Rebel Princess
 */
public class Card13_029 extends AbstractRebel {
    public Card13_029() {
        super(Side.LIGHT, 3, 4, 3, 4, 7, "Leia, Rebel Princess", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("After attracting Luke's attention and then Han's attention, Leia should not have been surprised when she got more attention than desired from Jabba.");
        setGameText("Twice per game, may place top card of Lost Pile on top of Reserve Deck to cancel a Force drain at a related site. Unless opponent's non-alien character here, opponent's total ability at same site = 0. Immune to attrition < 5 if with Luke, Han or Jabba.");
        addPersona(Persona.LEIA);
        addIcons(Icon.REFLECTIONS_III, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetTotalAbilityModifier(self, Filters.sameSite(self),
                new CantSpotCondition(self, Filters.and(Filters.opponents(self), Filters.non_alien_character, Filters.here(self))),
                0, game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new WithCondition(self, Filters.or(Filters.Luke, Filters.Han, Filters.Jabba)), 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEIA_REBEL_PRINCESS__CANCEL_FORCE_DRAIN;

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.relatedSite(self))
                && GameConditions.isTwicePerGame(game, self, gameTextActionId)
                && GameConditions.hasLostPile(game, playerId)
                && GameConditions.canCancelForceDrain(game, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Force drain");
            // Update usage limit(s)
            action.appendUsage(
                    new TwicePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PlaceTopCardOfLostPileOnTopOfReserveDeckEffect(action, playerId));
            // Perform result(s)
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
