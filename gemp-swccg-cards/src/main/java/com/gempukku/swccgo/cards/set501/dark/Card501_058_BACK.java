package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Objective
 * Title: Shadow Collective / You Know Who I Answer To
 */
public class Card501_058_BACK extends AbstractObjective {
    public Card501_058_BACK() {
        super(Side.DARK, 7, "You Know Who I Answer To");
        setGameText("Immediately recirculate; may 'peek' at top two cards of Reserve Deck and take one into hand." +
                "While this side up, your starships and armed characters are power and immunity to attrition +2. May lose a Force to add a battle destiny where you have an alien leader or a gangster (lose 2 Force if against Qi’ra to add 2 battle destiny instead)." +
                "Flip this card at the end of each turn; you may retrieve a blaster (if you occupy 3 battlegrounds, opponent loses 1 Force).");
        addIcons(Icon.REFLECTIONS_II);
        setTestingText("You Know Who I Answer To");
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        final String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Recirculate and peek at cards.");
            action.setActionMsg("Recirculate and peek at cards.");

            action.appendEffect(
                    new RecirculateEffect(action, playerId)
            );
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, 2, 1, 1)
            );

            actions.add(action);
        }

        //Flip this card at the end of each turn; you may retrieve a blaster (if you occupy 3 battlegrounds, opponent loses 1 Force).
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            if (GameConditions.occupies(game, playerId, 3, Filters.battleground)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                action.appendEffect(
                        new RetrieveCardEffect(action, playerId, Filters.blaster)
                );
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(playerId), 1)
                );
                actions.add(action);
            } else {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                action.appendEffect(
                        new RetrieveCardEffect(action, playerId, Filters.blaster)
                );
                actions.add(action);
            }
        }

        return actions;

    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        //While this side up, your starships and armed characters are power and immunity to attrition +2.
        List<Modifier> modifiers = new LinkedList<>();
        Filter starshipsAndArmedCharacters = Filters.and(Filters.your(self.getOwner()), Filters.or(Filters.starship, Filters.and(Filters.character, Filters.armedWith(Filters.any))));
        modifiers.add(new PowerModifier(self, starshipsAndArmedCharacters, 2));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, starshipsAndArmedCharacters, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // May lose a Force to add a battle destiny where you have an alien leader or a gangster (lose 2 Force if against Qi’ra to add 2 battle destiny instead).
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.or(Filters.alien_leader, Keyword.GANGSTER)))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            int numForceToLose = 1;
            int numBattleDestiniesToAdd = 1;
            if (GameConditions.isDuringBattleWithParticipant(game, Persona.QIRA)) {
                numForceToLose = 2;
                numBattleDestiniesToAdd = 2;
            }
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Lose 1 to add a battle destiny");
            action.appendUsage(
                    new OncePerBattleEffect(action)
            );
            action.appendEffect(
                    new LoseForceEffect(action, playerId, numForceToLose)
            );
            action.appendEffect(
                    new AddBattleDestinyEffect(action, numBattleDestiniesToAdd)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}