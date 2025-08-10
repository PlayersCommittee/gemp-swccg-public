package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ArtworkCardRevealedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Understand Art, Understand A Species
 */
public class Card219_024 extends AbstractUsedOrLostInterrupt {
    public Card219_024() {
        super(Side.DARK, 4, "Understand Art, Understand A Species", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setGameText("USED: Target a character in battle. Target and opponent's cards that share any characteristic(s) with it are power and forfeit -1. " +
                    "LOST: Once per game, if your [Set 19] objective just 'studied' a character, add one battle destiny (two if a character of same species is in battle).");
        addIcons(Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canTarget(game, self, Filters.and(Filters.character, Filters.participatingInBattle))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Reduce power and forfeit");
            // Choose target(s)
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a character in battle", Filters.and(Filters.character, Filters.participatingInBattle)) {
                @Override
                protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                    action.allowResponses("Reduce power and forfeit of " + GameUtils.getCardLink(targetedCard) + " and characters that share a characteristic", new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                            Filter toReduce = Filters.none;
                            for (Keyword keyword : Keyword.values()) {
                                if (keyword.isCharacteristic()
                                        && game.getModifiersQuerying().hasKeyword(game.getGameState(), finalTarget, keyword)) {
                                    toReduce = Filters.or(toReduce, Filters.characteristic(keyword));
                                }
                            }
                            for (Species species : Species.values()) {
                                if (game.getModifiersQuerying().isSpecies(game.getGameState(), finalTarget, species)) {
                                    toReduce = Filters.or(toReduce, Filters.species(species));
                                }
                            }

                            Collection<PhysicalCard> characters = Filters.filterActive(game, self, Filters.or(finalTarget, Filters.and(Filters.opponents(playerId), Filters.canBeTargetedBy(self), Filters.character, Filters.participatingInBattle, toReduce)));
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action, new PowerModifier(self, Filters.in(characters), -1), "Makes " + GameUtils.getAppendedNames(characters) + " power -1"));
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action, new ForfeitModifier(self, Filters.in(characters), -1), "Makes " + GameUtils.getAppendedNames(characters) + " forfeit -1"));
                        }
                    });
                }
            });

            actions.add(action);

        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.UNDERSTAND_ART_UNDERSTAND_A_SPECIES__ADD_BATTLE_DESTINY;

        if (effectResult.getType() == EffectResult.Type.ARTWORK_CARD_REVEALED
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            PhysicalCard artwork = ((ArtworkCardRevealedResult) effectResult).getCard();

            if (artwork != null
                    && Filters.character.accepts(game, artwork)) {

                final int destiniesToAdd = ((artwork.getBlueprint().hasSpeciesAttribute()
                        && artwork.getBlueprint().getSpecies() != null
                        && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.character, Filters.species(artwork.getBlueprint().getSpecies())))) ? 2 : 1);

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
                if (destiniesToAdd == 1)
                    action.setText("Add one battle destiny");
                else
                    action.setText("Add " + destiniesToAdd + " battle destinies");

                action.appendUsage(
                        new OncePerGameEffect(action));

                action.allowResponses("Add " + destiniesToAdd + " battle " + (destiniesToAdd == 1 ? "destiny" : "destinies"), new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new AddBattleDestinyEffect(action, destiniesToAdd, playerId));
                    }
                });

                return Collections.singletonList(action);
            }
        }

        return null;
    }
}