package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 10
 * Type: Character
 * Subtype: Republic
 * Title: Nute Gunray (V)
 */
public class Card210_040 extends AbstractRepublic {
    public Card210_040() {
        super(Side.DARK, 3, 3, 3, 4, 6, Title.Nute_Gunray, Uniqueness.UNIQUE);
        setLore("Commanding Viceroy of the Trade Federation forces assigned to the blockade of Naboo. Takes for his own actions. Neimoidian leader.");
        setGameText("During battle, may cancel game text of a Republic character with ability < 4 present. If with your Republic character (or character with 'Trade Federation' in lore), may add one battle destiny. Immune to attrition < 3.");
        addPersona(Persona.GUNRAY);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.NEIMOIDIAN);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        // Immune to attrition < 3
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // If with your Republic character (or character with 'Trade Federation' in lore), may add one battle destiny.
        Filter yourRepublicOrTradeFedInLoreCharacter = Filters.and(
                Filters.your(playerId),
                Filters.not(self),
                Filters.and(Filters.character, Filters.or(Filters.loreContains("Trade Federation"), Icon.REPUBLIC)));

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isDuringBattleWithParticipant(game, yourRepublicOrTradeFedInLoreCharacter)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
            action.setText("Add one battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1));
            actions.add(action);
        }



        // During battle, may cancel game text of a Republic character with ability < 4 present.
        Filter republicCharacterWithAbilityLessThanFour = Filters.and(
                Filters.Republic_character,
                Filters.abilityLessThan(4),
                Filters.present(self));

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2)
                && GameConditions.isDuringBattleWithParticipant(game, republicCharacterWithAbilityLessThanFour)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
            action.setText("Cancel a character's game text");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", republicCharacterWithAbilityLessThanFour) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, targetedCard));
                                        }
                                    }
                            );
                        }
                    }
            );

            actions.add(action);
        }

        return actions;
    }

}
