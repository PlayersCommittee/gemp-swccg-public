package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ArtworkCardRevealedResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Objective
 * Title: A Great Tactician Creates Plans / The Result Is Often Resentment
 */
public class Card219_001_BACK extends AbstractObjective {
    public Card219_001_BACK() {
        super(Side.DARK, 7, Title.The_Result_Is_Often_Resentment, ExpansionSet.SET_19, Rarity.V);
        setGameText("While this side up, if a battle was just initiated involving an Imperial leader or piloted TIE defender, may 'study' one artwork card. " +
                    "If it is a weapon, cancel the battle. Otherwise, if possible, if its printed destiny number is: " +
                    "(0-2) opponent's immunity to attrition is canceled; " +
                    "(3-4) opponent excludes their character from battle; " +
                    "(5+) add 3 to your total power. " +
                    "Place artwork card in owner's Lost Pile. " +
                    "Flip this card if Thrawn not on table or (except during battle) if no artwork cards on table. ");
        addIcons(Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter mayNotPlayFilter = Filters.or(Filters.Chiraneau,
                Filters.and(Filters.your(self), Filters.or(Icon.EPISODE_I, Icon.EPISODE_VII), Filters.or(Filters.hasAbilityOrHasPermanentPilotWithAbility, Icon.PRESENCE)));
        modifiers.add(new MayNotPlayModifier(self, mayNotPlayFilter, self.getOwner()));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.A_GREAT_TACTICIAN_CREATES_PLANS__DOWNLOAD_LOCATION;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a battleground system (or a site to Lothal)");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromPileEffect(action, self.getOwner(), Zone.RESERVE_DECK, Filters.or(Filters.battleground_system,
                            Filters.and(Filters.site, Filters.deployableToSystem(self, Title.Lothal, null, false, 0))), Filters.locationAndCardsAtLocation(Filters.partOfSystem(Title.Lothal)), Filters.battleground_system, Title.Lothal, null, false, Filters.none, 0, Filters.none, null, null, false, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.or(Filters.Imperial_leader, Filters.and(Filters.TIE_Defender, Filters.or(Filters.piloted, Filters.hasPermanentPilot, Filters.hasAboard(self, Filters.character)))))
                && GameConditions.canSpot(game, self, Filters.and(Filters.Thrawns_Art_Collection, Filters.hasStacked(Filters.any)))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Study artwork");
            action.setActionMsg("Peek at cards stacked on Thrawn's Art Collection and reveal one 'artwork' card");

            action.appendEffect(new ChooseStackedCardEffect(action, playerId, Filters.Thrawns_Art_Collection) {
                @Override
                protected void cardSelected(final PhysicalCard selectedCard) {
                    game.getGameState().sendMessage("Revealed "+ GameUtils.getCardLink(selectedCard));
                    game.getGameState().showCardOnScreen(selectedCard);
                    game.getActionsEnvironment().emitEffectResult(
                            new ArtworkCardRevealedResult(selectedCard));

                    if (Filters.weapon.accepts(game, selectedCard)) {
                        action.appendEffect(
                                new CancelBattleEffect(action));
                        action.appendEffect(
                                new PutStackedCardInLostPileEffect(action, playerId, selectedCard, false));
                    } else {
                        action.appendEffect(new RefreshPrintedDestinyValuesEffect(action, selectedCard) {
                            @Override
                            protected void refreshedPrintedDestinyValues() {
                                float printedDestinyValue = selectedCard.getDestinyValueToUse();

                                action.appendEffect(new SendMessageEffect(action, "Printed destiny value: "+ printedDestinyValue));

                                if (printedDestinyValue >= 0 && printedDestinyValue <= 2) {
                                    action.appendEffect(
                                            new CancelImmunityToAttritionUntilEndOfBattleEffect(action, Filters.and(Filters.participatingInBattle, Filters.opponents(playerId)), "Cancel "+ game.getOpponent(playerId) + "'s immunity to attrition"));
                                } else if (printedDestinyValue >= 3 && printedDestinyValue <= 4
                                        && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.character, Filters.opponents(playerId), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE)))) {
                                    final String opponent = game.getOpponent(playerId);
                                    action.appendEffect(new ChooseCardOnTableEffect(action, opponent, "Target a character to exclude from battle", Filters.and(Filters.participatingInBattle, Filters.character, Filters.opponents(playerId), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE))) {
                                        @Override
                                        protected void cardSelected(PhysicalCard selectedCard) {
                                            action.appendEffect(
                                                    new ExcludeFromBattleEffect(action, selectedCard));
                                        }
                                    });
                                } else if (printedDestinyValue >= 5) {
                                    action.appendEffect(
                                            new ModifyTotalPowerUntilEndOfBattleEffect(action, 3, playerId, "Add 3 to your total power"));
                                } else {
                                    game.getGameState().sendMessage("Result: No effect");
                                }
                                action.appendEffect(
                                        new PutStackedCardInLostPileEffect(action, playerId, selectedCard, false));

                            }
                        });
                    }
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new ArrayList<>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, Filters.Thrawns_Art_Collection)) {
            PhysicalCard thrawnsArtCollection = Filters.findFirstActive(game, self, Filters.Thrawns_Art_Collection);
            if (!GameConditions.isDuringBattle(game) && thrawnsArtCollection != null && !GameConditions.hasStackedCards(game, thrawnsArtCollection)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                actions.add(action);
            }
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, Filters.Thrawn)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);

        }
        return actions;
    }
}
