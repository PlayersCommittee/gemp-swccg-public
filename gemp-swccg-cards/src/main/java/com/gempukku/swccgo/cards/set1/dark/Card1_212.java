package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.DarkHoursResult;

import java.util.*;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Dark Hours
 */
public class Card1_212 extends AbstractNormalEffect {

    // Note: This card stores the "discardAfterTurnNumber" as it's "WhileInPlayData"

    public Card1_212() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Dark Hours", Uniqueness.RESTRICTED_3);
        setLore("After surviving Tarkin's extortion, kidnapping, threats of execution and the assault of the Interceptor droid, Princess Leia was asleep when her rescuers came.");
        setGameText("Deploy on a site under 'nighttime conditions.' One at a time, target each non-droid character here and draw destiny. If destiny > ability, character 'sleeps' (power, forfeit and ability = 0, 'game text' is canceled, and may not pilot, drive, or move). At the end of your next turn, lose effect and restore targets (if on table) to normal.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {

        // Deploy on a site under 'nighttime conditions.'
        return Filters.and(Filters.under_nighttime_conditions, Filters.site);
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {

        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String owner = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final String playerId = self.getOwner();
            final PhysicalCard site = self.getAttachedTo();
            final GameState gameState = game.getGameState();


            // We need to lose this effect at the end of the next turn, so store that turn number
            float discardAfterTurnNumber = gameState.getPlayersLatestTurnNumber(playerId) + 1.0f;
            self.setWhileInPlayData(new WhileInPlayData(discardAfterTurnNumber));

            // Run the Dark Hours gametext against every charater
            final  RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);

            // Perform result(s)
            action.setText("Dark Hours targeting characters at " + GameUtils.getFullName(site));
            action.setActionMsg("Dark Hours targeting characters at " + GameUtils.getCardLink(site));


            // One at a time, target each non-droid character here with Dark Hours
            //
            // Note that we need to do these each one-at-a-time WITHOUT given the LS
            // player a chance to take actions in between. To do this I've constructed
            // the 'buildAnotherChooseCardEffect' function which:
            //   - Lets the player choose a character
            //   - targets them with Dark Hours
            //   - calls "buildAnotherChooseCardEffect' again, which queues up an additional
            //     copy of the steps above.
            //
            // The functions pass along the 'charactersAlreadyTargeted" collection to keep track of
            // who has already been targeted.


            // Get all possible characters here.
            Filter nonDroidCharactersHere = Filters.and(Filters.non_droid_character, Filters.here(site));
            Collection<PhysicalCard> charactersHere = Filters.filterActive(game, self, nonDroidCharactersHere);
            Collection<PhysicalCard> charactersAlreadyTargeted = new ArrayList<>();

            // Build the ChooseCardOnTableEffect to hit them with Dark Hours (if characters available)
            ChooseCardOnTableEffect targetCardWithDarkHoursEffect = buildAnotherChooseCardEffect(game, self, action, charactersHere, charactersAlreadyTargeted);
            if (targetCardWithDarkHoursEffect != null) {

                // The 'buildAnotherChooseCardEffect' will add on additional effects
                action.appendEffect(targetCardWithDarkHoursEffect);
            }


            actions.add(action);
        }


        // Check condition(s) - Lose at the end of your next turn.
        if (TriggerConditions.isEndOfYourTurn(game, effectResult, owner)
                && GameConditions.cardHasWhileInPlayDataSet(self)) {

            // We have the "discardAfterTurnNumber" stored inside this card's "WhileInPlayData"
            int discardAfterTurnNumber = Math.round(self.getWhileInPlayData().getFloatValue());
            if (GameConditions.isTurnNumber(game, discardAfterTurnNumber)) {

                final GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

                // Lose this effect (mandatory)
                RequiredGameTextTriggerAction effectLostAction = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                effectLostAction.setText("Lose " + GameUtils.getFullName(self));
                effectLostAction.appendEffect(
                        new PlaceCardInLostPileFromTableEffect(effectLostAction, self));
                actions.add(effectLostAction);
            }
        }
        return actions;
    }


    /**
     * Build a new action which allows us to target the next character. If no additional characters are available
     * then just return null.
     *
     * @param game                          Game
     * @param self                          This card
     * @param action                        Action we are processing
     * @param charactersToChooseFrom        List of all possible characters to target
     * @param charactersAlreadyTargeted     List of characters already targeted. We keep adding to this list
     * @return ChooseCardOnTableEffect  Effect which targets each character, one at a time. (or null if no more)
     */
    private static ChooseCardOnTableEffect buildAnotherChooseCardEffect(final SwccgGame game, final PhysicalCard self, final RequiredGameTextTriggerAction action, final Collection<PhysicalCard> charactersToChooseFrom, final Collection<PhysicalCard> charactersAlreadyTargeted) {

        final String owner = self.getOwner();

        // If already targeted all characters, just return null
        if (charactersToChooseFrom.size() == charactersAlreadyTargeted.size()) {
            return null;
        }

        Filter nonPickedCharacterFilter = Filters.and(Filters.in(charactersToChooseFrom), Filters.not(Filters.in(charactersAlreadyTargeted)));
        Collection<PhysicalCard> availableCharacters = Filters.filterActive(game, self, nonPickedCharacterFilter);
        if (availableCharacters.isEmpty()) {
            return null;
        }


        // We have the list of valid targets. If we still have valid targets, allow the user to click them (one at a time)
        Integer minCharactersToSelect = 1;
        ChooseCardOnTableEffect chooseCardOnTableEffect = new ChooseCardOnTableEffect(action, owner, "Choose character to target", Filters.in(availableCharacters), minCharactersToSelect) {
            @Override
            protected void cardSelected(final PhysicalCard characterTargeted) {

                // Add this to the list of people we've already targeted (so we don't target them again)
                charactersAlreadyTargeted.add(characterTargeted);

                action.addAnimationGroup(characterTargeted);
                action.setActionMsg("Targeted" + GameUtils.getCardLink(characterTargeted) + " by Dark Hours");

                action.appendEffect(
                        new DrawDestinyEffect(action, owner, 1, DestinyType.DARK_HOURS_DESTINY) {
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> playersDestinyCardDraws, List<Float> playersDestinyDrawValues, final Float playersTotalDestiny) {

                                float characterAbility = game.getModifiersQuerying().getAbility(game.getGameState(), characterTargeted);
                                GameState gameState = game.getGameState();


                                if (playersTotalDestiny == null) {

                                    // Failed destiny draw.  The LS player gets to pick
                                    gameState.sendMessage("Result: Destiny draw failed. LS player chooses if characters sleeps or not");
                                    allowOwnerToChooseSleepOrNot(game, self, action, characterTargeted);

                                } else if (playersTotalDestiny <= characterAbility) {

                                    // Draw was not high enough. Nothing happens
                                    gameState.sendMessage("Result: Failed. " + GameUtils.getCardLink(characterTargeted) + " unaffected");

                                } else {

                                    // Draw successful. Put the character to sleep!
                                    gameState.sendMessage("Result: Successful. " + GameUtils.getCardLink(characterTargeted) + " sleeps");
                                    addSleepEffect(game, self, action, characterTargeted);
                                }


                                // Since these are all effects, the opponent won't be allowed to interrupt them
                                action.appendEffect(
                                        new PassthruEffect(action) {
                                            @Override
                                            protected void doPlayEffect(SwccgGame game) {

                                                // Next, queue up another action for remaining characters
                                                ChooseCardOnTableEffect bonusEffect = buildAnotherChooseCardEffect(game, self, action, charactersToChooseFrom, charactersAlreadyTargeted);
                                                if (bonusEffect != null) {
                                                    action.appendEffect(bonusEffect);
                                                }
                                            }
                                        }
                                );

                            }
                        }
                );
            }
        };

        return chooseCardOnTableEffect;
    }



    /**
     * Helper function which allows the owner of this card to choose whether or not the given character should
     * 'sleep' or not. This is used when destiny draws fail
     *
     * @param game      Current game
     * @param self      This card (Dark Hours)
     * @param action    Action to chain the effects onto
     * @param character Character currently being targeted by Dark Hours
     */
    private static void allowOwnerToChooseSleepOrNot(final SwccgGame game, final PhysicalCard self, final RequiredGameTextTriggerAction action, final PhysicalCard character) {

        final String lightSidePlayer = game.getOpponent(self.getOwner());

        // Allow response(s)
        action.appendEffect(new PlayoutDecisionEffect(action, lightSidePlayer,
                new MultipleChoiceAwaitingDecision("Choose effect for " + GameUtils.getFullName(character), new String[]{"Character 'sleeps'", "No effect"}) {
                    @Override
                    protected void validDecisionMade(int index, String result) {
                        final GameState gameState = game.getGameState();

                        if (index == 0) {
                            gameState.sendMessage(lightSidePlayer + " chooses to have character sleep");
                            addSleepEffect(game, self, action, character);
                        } else {
                            gameState.sendMessage(lightSidePlayer + " chooses no effect");
                        }
                    }
                }
        ));
    }


    /**
     * Helper function which tells the given character to "sleep" until end of the next turn
     *
     * @param game          Current Game
     * @param self          This card (Dark Hours)
     * @param action        Action to chain the extra effects onto
     * @param character     Character currently being targeted by Dark Hours
     */
    private static void addSleepEffect(final SwccgGame game, final PhysicalCard self, final RequiredGameTextTriggerAction action, final PhysicalCard character) {

        final String owner = self.getOwner();

        // character sleeps until end of next turn
        // power, forfeit and ability = 0,
        // 'game text' is canceled,
        // and may not pilot, drive, or move)

        // Power = 0
        action.appendEffect(
                new AddUntilEndOfPlayersNextTurnModifierEffect(action, owner,
                        new ResetPowerModifier(self, character, 0), "makes " + GameUtils.getCardLink(character) + " power = 0"));
        // Forfeit = 0
        action.appendEffect(
                new AddUntilEndOfPlayersNextTurnModifierEffect(action, owner,
                        new ResetForfeitModifier(self, character, 0), "makes " + GameUtils.getCardLink(character) + " forfeit = 0"));

        // Ability = 0
        action.appendEffect(
                new AddUntilEndOfPlayersNextTurnModifierEffect(action, owner,
                        new ResetAbilityModifier(self, character, 0), "makes " + GameUtils.getCardLink(character) + " ability = 0"));

        // Gametext canceled
        action.appendEffect(
                new AddUntilEndOfPlayersNextTurnModifierEffect(action, owner,
                        new CancelsGameTextModifier(self, character), "makes " + GameUtils.getCardLink(character) + "'s gametext canceled"));

        // May not move
        action.appendEffect(
                new AddUntilEndOfPlayersNextTurnModifierEffect(action, owner,
                        new MayNotMoveModifier(self, character), "prevents " + GameUtils.getCardLink(character) + " from moving"));

        // May not pilot or drive
        action.appendEffect(
                new AddUntilEndOfPlayersNextTurnModifierEffect(action, owner,
                        new CantDriveOrPilotModifier(self, character), "prevents " + GameUtils.getCardLink(character) + " from piloting or driving"));


        // Emit the Dark Hours result so that the CancelsGameTextModifier "Rule" kicks in (only kicks in after results)
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        game.getActionsEnvironment().emitEffectResult(new DarkHoursResult(self));
                    }
                }
        );

    }

}