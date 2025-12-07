package com.gempukku.swccgo.cards.set226.light;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Set: Set 26
 * Type: Character
 * Subtype: Jedi Master
 * Title: Quinlan Vos
 */
public class Card226_026 extends AbstractJediMaster {
    public Card226_026() {
        super(Side.LIGHT, 1, 8, 7, 7, 8, "Quinlan Vos", Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("Jedi survivor.");
        setGameText("[Pilot] 2. Adds one battle destiny with Dooku, Grievous, or Ventress. Once per turn, may peek at the top card of any Reserve Deck or subtract 1 from a just drawn weapon destiny here. Dark Approach is a Used interrupt. Immune to Sniper and attrition < 6 (< 8 if alone).");
        addKeyword(Keyword.JEDI_SURVIVOR);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_26);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.or(Filters.Dooku, Filters.Grievous, Filters.Ventress)), 1));
        modifiers.add(new UsedInterruptModifier(self, Filters.Dark_Approach));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Sniper));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(6, 8, new AloneCondition(self))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1; //Action ID shared with the weapon destiny subtract action

        // Check condition(s)
        if ((GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top card of any Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                        @Override
                        protected void pileChosen(final SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            action.setActionMsg("Peek at top card of " + cardPileOwner + "'s Reserve Deck");
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, cardPileOwner));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1; //Action ID shared with the Reserve Deck peek action
        
        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult, Filters.here(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Subtract 1 from weapon destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            return Collections.singletonList(action);
        }
        return null;
    }

}
