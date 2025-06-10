package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BlowAwayState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.*;
import com.gempukku.swccgo.logic.timing.results.BlownAwayCalculateForceLossStepResult;
import com.gempukku.swccgo.logic.timing.results.BlownAwayLastStepResult;
import com.gempukku.swccgo.logic.timing.results.BlownAwayRelocateStepResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


/**
 * An effect that causes the specified cards to be 'blown away'.
 */
public class BlowAwayEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _cardsToBlowAway;
    private BlowAwayEffect _blowAwayEffect;
    private boolean _bySuperlaser;

    /**
     * Creates an effect that causes the specified card to be 'blown away'.
     * @param action the action performing this effect
     * @param cardToBlowAway the card to be 'blown away'
     */
    public BlowAwayEffect(Action action, PhysicalCard cardToBlowAway) {
        this(action, Collections.singleton(cardToBlowAway));
    }

    /**
     * Creates an effect that causes the specified card to be 'blown away'.
     * @param action the action performing this effect
     * @param cardToBlowAway the card to be 'blown away'
     * @param bySuperlaser true if 'blown away' by Superlaser, otherwise false
     */
    public BlowAwayEffect(Action action, PhysicalCard cardToBlowAway, boolean bySuperlaser) {
        this(action, Collections.singleton(cardToBlowAway));
        _bySuperlaser = bySuperlaser;
    }

    /**
     * Creates an effect that causes the specified cards to be 'blown away'.
     * @param action the action performing this effect
     * @param cardsToBlowAway the cards to be 'blown away'
     */
    public BlowAwayEffect(Action action, Collection<PhysicalCard> cardsToBlowAway) {
        super(action);
        _cardsToBlowAway = Collections.unmodifiableCollection(cardsToBlowAway);
        _blowAwayEffect = this;
    }

    /**
     * This method is called before 'blowing away' is performed in order to get any modifiers that will last until
     * the end of this 'blow away' process.
     * @param game the game
     * @param blowAwayState the blow away state
     */
    protected List<Modifier> getBlowAwayModifiers(SwccgGame game, BlowAwayState blowAwayState) {
        return null;
    }

    /**
     * This method is called before 'blowing away' is performed in order to get any proxy actions that will last until
     * the end of this 'blow away' process.
     * @param game the game
     * @param blowAwayState the blow away state
     */
    protected List<ActionProxy> getBlowAwayActionProxies(SwccgGame game, BlowAwayState blowAwayState) {
        return null;
    }

    /**
     * This method is called to perform any additional effects on the card (if it is still in play) just before other
     * automatic and optional responses from blowing away. This should only be used when source card is a card that
     * deploys on the table.
     * @param game the game
     * @param blowAwaySubAction the sub-action that will perform the effect
\     */
    protected StandardEffect getAdditionalGameTextEffect(SwccgGame game, Action blowAwaySubAction) {
        return null;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final SubAction subAction = new SubAction(_action);

        // 1) Begin blow away
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.beginBlowAway(_blowAwayEffect);
                    }
                }
        );

        // 2) Callback to allow modifiers/proxy actions
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        BlowAwayState blowAwayState = gameState.getTopBlowAwayState();
                        List<Modifier> modifiers = _blowAwayEffect.getBlowAwayModifiers(game, blowAwayState);
                        if (modifiers != null) {
                            for (Modifier modifier : modifiers) {
                                game.getModifiersEnvironment().addUntilEndOfBlowAwayModifier(modifier);
                            }
                        }

                        List<ActionProxy> actionProxies = _blowAwayEffect.getBlowAwayActionProxies(game, blowAwayState);
                        if (actionProxies != null) {
                            for (ActionProxy actionProxy : actionProxies) {
                                game.getActionsEnvironment().addUntilEndOfBlowAwayActionProxy(actionProxy);
                            }
                        }
                    }
                }
        );

        // 3) Record cards 'blown away' and send message.
        subAction.appendEffect(
                new RecordCardsBlownAwayEffect(subAction, _cardsToBlowAway));

        // 4) Trigger to relocate cards attached to 'blown away' card.
        Collection<EffectResult> blownAwayRelocateStepResults = new HashSet<EffectResult>();
        for (PhysicalCard cardToBlowAway : _cardsToBlowAway) {
            blownAwayRelocateStepResults.add(
                    new BlownAwayRelocateStepResult(subAction, cardToBlowAway));
        }
        subAction.appendEffect(
                new TriggeringResultsEffect(
                        subAction, blownAwayRelocateStepResults));

        // Determine 'blown away' sites, systems, or starships
        final Collection<PhysicalCard> sitesToBlowAway = Filters.filter(_cardsToBlowAway, game, Filters.site);
        final Collection<PhysicalCard> systemsToBlowAway = Filters.filter(_cardsToBlowAway, game, Filters.system);
        final Collection<PhysicalCard> starshipsToBlowAway = Filters.filter(_cardsToBlowAway, game, Filters.starship);
        final Collection<PhysicalCard> locationsToFlip = Filters.filter(_cardsToBlowAway, game, Filters.and(Filters.location, Filters.not(Filters.holosite)));

        // 5) For any 'blown away' card that is a site, all cards (except the source card of this action) at the site are lost.
        //    Also, for any 'blown away' card that is a holosite, the holosite and any converted holosites under it are lost.
        if (!sitesToBlowAway.isEmpty()) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Determine all the cards to be lost
                            Filter allCardsAtSiteFilter = Filters.and(Filters.at(Filters.in(sitesToBlowAway)), Filters.not(_action.getActionSource()));
                            Filter holositeFilter = Filters.and(Filters.holosite, Filters.in(sitesToBlowAway));

                            Collection<PhysicalCard> cardsToMakeLost = Filters.filterAllOnTable(game,
                                    Filters.or(allCardsAtSiteFilter, holositeFilter, Filters.convertedLocationUnderTopLocation(holositeFilter)));
                            for (PhysicalCard siteToBlowAway : sitesToBlowAway) {
                                cardsToMakeLost.addAll(Filters.filterStacked(game, Filters.and(Filters.stackedOn(siteToBlowAway), Filters.not(_action.getActionSource()))));
                            }

                            // The cards are all lost simultaneously
                            SubAction loseCardsOnTableAction = new SubAction(subAction);
                            loseCardsOnTableAction.appendEffect(
                                    new LoseCardsFromTableSimultaneouslyEffect(loseCardsOnTableAction, cardsToMakeLost, true, false));

                            // Stack sub-action
                            subAction.stackSubAction(loseCardsOnTableAction);
                        }
                    }
            );
        }

        // 6) Calculate and cause Force loss
        Collection<EffectResult> blownAwayCalculateForceLossStepResults = new HashSet<EffectResult>();
        for (PhysicalCard cardToBlowAway : _cardsToBlowAway) {
            blownAwayCalculateForceLossStepResults.add(
                    new BlownAwayCalculateForceLossStepResult(subAction, cardToBlowAway, _bySuperlaser));
        }
        subAction.appendEffect(
                new TriggeringResultsEffect(
                        subAction, blownAwayCalculateForceLossStepResults));
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        String performingPlayerId = subAction.getPerformingPlayer();
                        String opponent = game.getOpponent(performingPlayerId);


                        boolean forceLossMayNotBeReduced = modifiersQuerying.blownAwayForceLossMayNotBeReduced(gameState);

                        // Force loss for each player due to blown away
                        SubAction loseForceAction = new SubAction(subAction);
                        // Determine opponent's Force loss
                        float opponentsForceLoss = modifiersQuerying.getBlownAwayForceLoss(gameState, opponent);
                        if (opponentsForceLoss > 0) {
                            // Perform result(s)
                            loseForceAction.appendEffect(
                                    new LoseForceEffect(loseForceAction, opponent, opponentsForceLoss, forceLossMayNotBeReduced));
                        }
                        // Determine players's Force loss
                        float playersForceLoss = modifiersQuerying.getBlownAwayForceLoss(gameState, performingPlayerId);
                        if (playersForceLoss > 0) {
                            // Perform result(s)
                            loseForceAction.appendEffect(
                                    new LoseForceEffect(loseForceAction, performingPlayerId, playersForceLoss, forceLossMayNotBeReduced));
                        }
                        // Stack sub-action
                        subAction.stackSubAction(loseForceAction);
                    }
                }
        );

        // 7) For any 'blown away' card that is a starship, all related sites (and all cards at them), and any cards aboard
        //    the starship are lost. The starship is placed out of play.
        if (!starshipsToBlowAway.isEmpty()) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Determine all the cards to be lost
                            Filter relatedLocationsToLoseFilter = Filters.and(Filters.starship_site, Filters.relatedLocation(starshipsToBlowAway));
                            Filter allCardsToLoseFilter = Filters.or(relatedLocationsToLoseFilter, Filters.at(relatedLocationsToLoseFilter), Filters.aboardOrAboardCargoOf(Filters.in(starshipsToBlowAway)));

                            Collection<PhysicalCard> cardsToMakeLost = Filters.filterAllOnTable(game, allCardsToLoseFilter);

                            // The cards are all lost simultaneously and 'blown away' starships placed out of play
                            SubAction placeCardsOutOfPlayAndLoseCardsOnTableAction = new SubAction(subAction);
                            placeCardsOutOfPlayAndLoseCardsOnTableAction.appendEffect(
                                    new LoseCardsFromTableSimultaneouslyEffect(placeCardsOutOfPlayAndLoseCardsOnTableAction, cardsToMakeLost, true, false));
                            placeCardsOutOfPlayAndLoseCardsOnTableAction.appendEffect(
                                    new PlaceCardsOutOfPlayFromTableSimultaneouslyEffect(placeCardsOutOfPlayAndLoseCardsOnTableAction, starshipsToBlowAway, false));

                            // Stack sub-action
                            subAction.stackSubAction(placeCardsOutOfPlayAndLoseCardsOnTableAction);
                        }
                    }
            );
        }

        // 8) For any 'blown away' card that is a system, all related sites, Cloud sectors, Death Star II sectors (and all cards
        //    at them), and any cards at the system except starships (and cards aboard those starships) and mobile systems are lost.
        if (!systemsToBlowAway.isEmpty()) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Determine all the cards to be lost
                            Filter relatedLocationsToLoseFilter = Filters.and(Filters.relatedLocation(systemsToBlowAway),
                                    Filters.or(Filters.site, Filters.cloud_sector, Filters.Death_Star_II_sector));
                            Filter allCardsToLoseFilter = Filters.or(relatedLocationsToLoseFilter, Filters.at(relatedLocationsToLoseFilter),
                                    Filters.and(Filters.at(Filters.in(systemsToBlowAway)), Filters.not(Filters.or(Filters.starship, Filters.mobile_system, Filters.attachedToWithRecursiveChecking(Filters.starship)))));

                            Collection<PhysicalCard> cardsToMakeLost = Filters.filterAllOnTable(game, allCardsToLoseFilter);
                            for (PhysicalCard systemToBlowAway : systemsToBlowAway) {
                                cardsToMakeLost.addAll(Filters.filterStacked(game, Filters.stackedOn(systemToBlowAway)));
                            }

                            // The cards are all lost simultaneously
                            SubAction loseCardsOnTableAction = new SubAction(subAction);
                            loseCardsOnTableAction.appendEffect(
                                    new LoseCardsFromTableSimultaneouslyEffect(loseCardsOnTableAction, cardsToMakeLost, true, false));

                            // Stack sub-action
                            subAction.stackSubAction(loseCardsOnTableAction);
                        }
                    }
            );
        }

        // 9) For any 'blown away' location that is not a holosite, flip it over.
        if (!locationsToFlip.isEmpty()) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            gameState.flipBlownAwayLocations(game, locationsToFlip);
                        }
                    }
            );
        }

        // 10) Continue with events on the card (if still on table).
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (Filters.onTable.accepts(gameState, modifiersQuerying, _action.getActionSource())) {
                            StandardEffect additionalEffect = getAdditionalGameTextEffect(game, subAction);
                            if (additionalEffect != null) {
                                subAction.insertEffect(additionalEffect);
                            }
                        }
                    }
                }
        );

        // 11) Automatic and optional responses from blowing away.
        Collection<EffectResult> blownAwayLastStepResults = new HashSet<EffectResult>();
        for (PhysicalCard cardToBlowAway : _cardsToBlowAway) {
            blownAwayLastStepResults.add(
                    new BlownAwayLastStepResult(subAction, cardToBlowAway));
        }
        subAction.appendEffect(
                new TriggeringResultsEffect(
                        subAction, blownAwayLastStepResults));

        // 12) End blow away
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.endBlowAway();
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
