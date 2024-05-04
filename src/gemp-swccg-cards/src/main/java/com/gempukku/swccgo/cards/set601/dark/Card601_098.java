package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RearmCharacterEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.ShuffleUsedPileEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 6
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Weapon Levitation & The Empire's Back
 */
public class Card601_098 extends AbstractUsedOrLostInterrupt {
    public Card601_098() {
        super(Side.DARK, 4, "Weapon Levitation & The Empire's Back", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        addComboCardTitles("Weapon Levitation", "The Empire's Back");
        setGameText("USED: Cancel Disarmed or Wookie Strangle. OR Once per battle involving Vader, reveal a device or character weapon from Used Pile and add its printed destiny number to your total power; reshuffle. \n" +
                "LOST: Use 1 Force to deploy (or retrieve) Vader from Reserve Deck; reshuffle. OR If opponent just initiated a battle against Vader defending alone at a site, his forfeit cannot be reduced.");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        //Cancel Disarmed or Wookiee Strangle
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.title(Title.Disarmed), Filters.title(Title.Wookiee_Strangle)))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        boolean mayTreatVaderAsGalen = game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.LEGACY__THE_EMPIRES_BACK__VADER_MAY_BE_TREATED_AS_GALEN);
        Filter vaderFilter = Filters.Vader;
        if (mayTreatVaderAsGalen) {
            vaderFilter = Filters.or(Filters.Vader, Filters.Galen);
        }

        //Cancel Disarmed (if it's on table)
        final Filter charactersWhoAreDisarmed = Filters.Disarmed;

        if (GameConditions.canTarget(game, self, charactersWhoAreDisarmed)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel Disarmed");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Disarmed Character", charactersWhoAreDisarmed) {

                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses("Restore " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(new RearmCharacterEffect(action, targetedCard));
                                        }
                                    });
                        }
                    }
            );
            action.setImmuneTo(Title.Sense);

            actions.add(action);
        }


        // Once per battle involving Vader, reveal a device or character weapon from Used Pile and add its printed destiny number to your total power; reshuffle.
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__WEAPON_LEVITATION_THE_EMPIRES_BACK__REVEAL_CARD_FROM_USED_PILE;

        if (GameConditions.isDuringBattleWithParticipant(game, vaderFilter)
            && GameConditions.canSearchUsedPile(game, playerId, self, gameTextActionId)
            && GameConditions.isOncePerBattle(game, self, playerId, self.getCardId(), gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Reveal card from Used Pile");
            action.setActionMsg("Reveal a device or character weapon from Used Pile and add its printed destiny number to your total power");
            action.appendUsage(new OncePerBattleEffect(action));

            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new ChooseCardFromUsedPileEffect(action, playerId, Filters.or(Filters.device, Filters.character_weapon)) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            action.appendEffect(new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(new SendMessageEffect(action, playerId + " reveals " + GameUtils.getCardLink(selectedCard) + " from Used Pile using " + GameUtils.getCardLink(self)));
                            float destiny = game.getModifiersQuerying().getDestiny(game.getGameState(), selectedCard);
                            action.appendEffect(
                                    new ModifyTotalPowerUntilEndOfBattleEffect(action, destiny, playerId,
                                            "Adds " + GuiUtils.formatAsString(destiny) + " to total power"));
                        }
                    });
                    action.appendEffect(new ShuffleUsedPileEffect(action, self));
                }
            });

            actions.add(action);
        }


        gameTextActionId = GameTextActionId.LEGACY__WEAPON_LEVITATION_THE_EMPIRES_BACK__DEPLOY_OR_RETRIEVE_VADER;
        final Filter searchFilter = Filters.and(vaderFilter);

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
            && (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.VADER)
            || (mayTreatVaderAsGalen && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.GALEN)))) {

            String text = "Deploy Vader from Reserve Deck";
            if (mayTreatVaderAsGalen)
                text = "Deploy Vader or Galen from Reserve Deck";

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText(text);
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(text,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, searchFilter, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            String text = "Retrieve Vader";
            if (mayTreatVaderAsGalen)
                text = "Retrieve Vader or Galen";

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText(text);
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, searchFilter));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        boolean mayTreatVaderAsGalen = game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.LEGACY__THE_EMPIRES_BACK__VADER_MAY_BE_TREATED_AS_GALEN);
        Filter vaderFilter = Filters.Vader;
        if (mayTreatVaderAsGalen) {
            vaderFilter = Filters.or(Filters.Vader, Filters.Galen);
        }

        final String opponent = game.getOpponent(playerId);
        Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(vaderFilter, Filters.alone, Filters.at(Filters.site)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Protect forfeit");
            // Allow response(s)
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target character to protect forfeit", Filters.and(vaderFilter, Filters.alone, Filters.at(Filters.site))) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    PhysicalCard card = action.getPrimaryTargetCard(targetGroupId);
                                    action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                            new MayNotHaveForfeitValueReducedModifier(self, card),
                                            "Forfeit of " + GameUtils.getCardLink(card) + " cannot be reduced"));
                                }
                            }
                    );
                }
            });

            return Collections.singletonList(action);
        }
        return null;
    }
}