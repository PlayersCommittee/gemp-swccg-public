package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableUsingForfeitValueEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Character
 * Subtype: Alien
 * Title: Dathcha (V)
 */
public class Card205_011 extends AbstractAlien {
    public Card205_011() {
        super(Side.DARK, 2, 2, 2, 2, 3, Title.Dathcha, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Jawa adventurer and trader. Famous for taunting a krayt dragon and escaping to tell the tale. Wants to leave Tatooine to explore the galaxy.");
        setGameText("Scavenger. Once per turn, if your droid just deployed to same site, may peek at the top two cards of your Reserve Deck (may take one into hand). During battle here, may forfeit Dathcha (or one non-[Permanent Weapon] droid) to Used Pile (using forfeit value = 3).");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_5);
        addKeywords(Keyword.SCAVENGER);
        setSpecies(Species.JAWA);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Keyword.SCAVENGER));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, Filters.and(Filters.your(self), Filters.droid), Filters.sameSite(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top two cards of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, 2, 0, 1));
            return Collections.singletonList(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isInBattle(game, self)
                && (GameConditions.isBattleDamageRemaining(game, playerId) || GameConditions.isAttritionRemaining(game, playerId))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            Collection<PhysicalCard> cardsToForfeit = Filters.filterActive(game, self,
                    Filters.and(Filters.your(self), Filters.or(Filters.Dathcha, Filters.and(Filters.droid, Filters.not(Icon.PERMANENT_WEAPON))), Filters.participatingInBattle));
            if (!cardsToForfeit.isEmpty()) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Forfeit Dathcha or a droid");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose Dathcha or a non-[Permanent Weapon] droid", cardsToForfeit) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedCard) {
                                action.addAnimationGroup(selectedCard);
                                action.setActionMsg("Forfeit " + GameUtils.getCardLink(selectedCard));
                                // Perform result(s)
                                action.appendEffect(
                                        new ForfeitCardFromTableUsingForfeitValueEffect(action, selectedCard, 3, true));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
