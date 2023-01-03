package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfCardPileEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 7
 * Type: Character
 * Subtype: Alien
 * Title: Sy Snootles
 */
public class Card601_153 extends AbstractAlien {
    public Card601_153() {
        super(Side.DARK, 3, 1, 1, 1, 3, "Sy Snootles", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Pa'lowick Musician. Manager of The Max Rebo Band. Reports on Jabba to Lady Valarian. Bib Fortuna uses her as a double agent, feeding her false information.");
        setGameText("Once per turn, may use 1 force to 'court' a male present. That character is power -3 and requires +2 Force to use landspeed for remainder of turn. Once per game, may take Set For Stun, They're Still Coming Through!, or You Want This, Don't You? into hand from reserve deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_7);
        addKeywords(Keyword.FEMALE, Keyword.MUSICIAN);
        setSpecies(Species.PALOWICK);
        setAsLegacy(true);
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, Filters.and(Filters.male, Filters.present(self)))
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("'Court' male");

            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target male to 'court'", Filters.and(Filters.male, Filters.present(self))) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.appendCost(
                            new UseForceEffect(action, playerId, 1));
                    action.allowResponses(new RespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                            action.appendEffect(
                                    new SendMessageEffect(action, GameUtils.getCardLink(self) + " 'courts' " + GameUtils.getCardLink(finalTarget)));
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action, new PowerModifier(self, finalTarget, -3), "Make target power -3"));
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action, new MoveCostUsingLandspeedModifier(self, finalTarget, 2), "Target requires +2 Force to use landspeed"));
                        }
                    });
                }
            });

            actions.add(action);
        }




        gameTextActionId = GameTextActionId.LEGACY__SY_SNOOTLES_V__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Set For Stun, They're Still Coming Through!, or You Want This, Don't You? into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Set_For_Stun, Filters.title("They're Still Coming Through!"), Filters.title("You Want This, Don't You?")), true));
            actions.add(action);
        }

        return actions;
    }
}
