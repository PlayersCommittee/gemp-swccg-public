package com.gempukku.swccgo.cards.set501.light ;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.InsteadOfFiringWeaponEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 12
 * Type: Character
 * Subtype: Resistance
 * Title: Rey, Strong in the Force
 */
public class Card501_018 extends AbstractResistance {
    public Card501_018() {
        super(Side.LIGHT, 1, 5, 5, 5, 8, "Rey, Strong In The Force", Uniqueness.UNIQUE);
        setLore("Female");
        addKeywords(Keyword.FEMALE);
        setGameText("[Pilot] 3. When Rey leaves table, place all your cards on her in Used Pile. During battle, instead of Rey firing a unique (•) lightsaber, may add one destiny to your power or attrition. Immune to attrition < 4.");
        addPersona(Persona.REY);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_VII, Icon.VIRTUAL_SET_12);
        setTestingText("Rey, Strong in the Force");
}

    // Add 2 to stuff he pilots.
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        return modifiers;
    }

    @Override
    public List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if(TriggerConditions.isAboutToLeaveTable(game, effectResult, self)){
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.appendEffect(
                    new PlaceCardsInUsedPileFromTableEffect(action,  self.getCardsAttached())
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        List<PhysicalCard> validSabers = new LinkedList<PhysicalCard>();
        final Collection<PhysicalCard> cardsInBattle = Filters.filterActive(game, self, Filters.participatingInBattle);
        for (PhysicalCard cardInBattle : cardsInBattle) {
            if (Filters.and(Filters.your(self), Filters.and(Filters.lightsaber, Filters.unique), Filters.canBeFiredForFreeAt(self, 0, Filters.in(cardsInBattle))).accepts(game, cardInBattle)) {
                validSabers.add(cardInBattle);
            }
        }

        // Check condition(s)
        if (GameConditions.isInBattle(game, self)
                && !validSabers.isEmpty()) {
            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Add Destiny To Power");
                action.appendEffect(
                        new InsteadOfFiringWeaponEffect(action, validSabers.get(0),
                                new AddDestinyToTotalPowerEffect(action, 1)));

                actions.add(action);
            }
            if (GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Add Destiny To Attrition");
                action.appendEffect(
                        new InsteadOfFiringWeaponEffect(action, validSabers.get(0),
                                new AddDestinyToAttritionEffect(action, 1)));

                actions.add(action);
            }
        }
        return actions;
    }
}