package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsToLoseFromTableEffect;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Trips (reveals) all buried cards under a site. Duds are lost. Mines explode targeting the tripper if applicable.
 * Defuse may be offered when buried mines are tripped during the owner's turn with a mining droid present.
 */
public class TripBuriedMinesEffect extends AbstractSubActionEffect {
    private final PhysicalCard _site;
    private final PhysicalCard _tripper;
    private final Collection<PhysicalCard> _buriedCards;

    public TripBuriedMinesEffect(Action action, PhysicalCard site, PhysicalCard tripper, Collection<PhysicalCard> buriedCards) {
        super(action);
        _site = site;
        _tripper = tripper;
        _buriedCards = new ArrayList<PhysicalCard>(buriedCards);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !_buriedCards.isEmpty();
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        game.getGameState().sendMessage(GameUtils.getCardLink(_tripper) + " trips buried cards under " + GameUtils.getCardLink(_site));

                        for (PhysicalCard buried : _buriedCards) {
                            if (buried.getZone() == Zone.STACKED_FACE_DOWN) {
                                game.getGameState().flipCard(game, buried, false);
                            }
                        }

                        String owner = null;
                        for (PhysicalCard buried : _buriedCards) {
                            if (buried.isBuriedMine()) {
                                owner = buried.getOwner();
                                break;
                            }
                        }
                        final String mineOwner = owner;
                        boolean mayDefuse = mineOwner != null
                                && game.getGameState().getCurrentPlayerId().equals(mineOwner)
                                && Filters.canSpot(game, _site, Filters.and(Filters.your(mineOwner), Filters.mining_droid, Filters.present(_site)));

                        if (mayDefuse) {
                            offerDefuse(game, subAction, mineOwner, new ArrayList<PhysicalCard>(_buriedCards));
                        }
                        else {
                            explodeOrLoseAll(game, subAction, new ArrayList<PhysicalCard>(_buriedCards));
                        }
                    }
                }
        );
        return subAction;
    }

    private void offerDefuse(final SwccgGame game, final SubAction subAction, final String mineOwner, final List<PhysicalCard> remaining) {
        final List<PhysicalCard> stillBuried = filterStillBuried(remaining);
        if (stillBuried.isEmpty()) {
            return;
        }
        List<String> options = new ArrayList<String>();
        options.add("Do not defuse");
        for (PhysicalCard card : stillBuried) {
            options.add("Defuse " + GameUtils.getFullName(card) + " (Use 1 Force)");
        }
        game.getUserFeedback().sendAwaitingDecision(mineOwner,
                new MultipleChoiceAwaitingDecision("Defuse buried mines before they explode?", options.toArray(new String[0])) {
                    @Override
                    protected void validDecisionMade(int index, String result) {
                        if (index == 0) {
                            explodeOrLoseAll(game, subAction, stillBuried);
                            return;
                        }
                        final PhysicalCard toDefuse = stillBuried.get(index - 1);
                        subAction.appendEffect(new UseForceEffect(subAction, mineOwner, 1));
                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        toDefuse.setBuriedMine(false);
                                        game.getGameState().sendMessage(mineOwner + " defuses " + GameUtils.getCardLink(toDefuse));
                                        subAction.appendEffect(new PutStackedCardInLostPileEffect(subAction, mineOwner, toDefuse, false));
                                        List<PhysicalCard> left = new ArrayList<PhysicalCard>(stillBuried);
                                        left.remove(toDefuse);
                                        if (!left.isEmpty() && Filters.canSpot(game, _site, Filters.and(Filters.your(mineOwner), Filters.mining_droid, Filters.present(_site)))) {
                                            offerDefuse(game, subAction, mineOwner, left);
                                        }
                                        else {
                                            explodeOrLoseAll(game, subAction, left);
                                        }
                                    }
                                }
                        );
                    }
                });
    }

    private void explodeOrLoseAll(final SwccgGame game, final SubAction subAction, List<PhysicalCard> cards) {
        for (PhysicalCard card : filterStillBuried(cards)) {
            resolveOne(game, subAction, card);
        }
    }

    private List<PhysicalCard> filterStillBuried(List<PhysicalCard> cards) {
        List<PhysicalCard> still = new LinkedList<PhysicalCard>();
        for (PhysicalCard card : cards) {
            if (card.isBuriedMine() && (card.getZone() == Zone.STACKED || card.getZone() == Zone.STACKED_FACE_DOWN)) {
                still.add(card);
            }
        }
        return still;
    }

    private void resolveOne(final SwccgGame game, final SubAction subAction, final PhysicalCard buried) {
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!buried.isBuriedMine()) {
                            return;
                        }
                        boolean isMine = Filters.mine.accepts(game, buried);
                        if (!isMine) {
                            buried.setBuriedMine(false);
                            game.getGameState().sendMessage(GameUtils.getCardLink(buried) + " is a dud and is lost");
                            subAction.appendEffect(new PutStackedCardInLostPileEffect(subAction, buried.getOwner(), buried, false));
                            return;
                        }

                        String title = buried.getTitle();
                        boolean infantry = Title.Infantry_Mine.equals(title);
                        boolean vehicle = "Vehicle Mine".equals(title);
                        boolean timer = "Timer Mine".equals(title);
                        boolean orbital = Title.Orbital_Mine.equals(title);

                        // Bill lock: Orbital Mine buried = dud (reveal → lost, no explode)
                        if (orbital) {
                            buried.setBuriedMine(false);
                            game.getGameState().sendMessage(GameUtils.getCardLink(buried) + " is a dud (Orbital Mine cannot be buried as a valid mine) and is lost");
                            subAction.appendEffect(new PutStackedCardInLostPileEffect(subAction, buried.getOwner(), buried, false));
                            return;
                        }

                        // Bill lock: Timer Mine buried explode
                        if (timer) {
                            explodeBuriedTimerMine(game, subAction, buried);
                            return;
                        }

                        // Infantry / Vehicle: attach as if laid so automated-weapon explode can use present/same-site
                        buried.setBuriedMine(false);
                        game.getGameState().removeCardsFromZone(Collections.singleton(buried));
                        buried.stackOn(null, false, false);
                        game.getGameState().attachCard(buried, _site);
                        game.getGameState().sendMessage(GameUtils.getCardLink(buried) + " is revealed from a buried state");

                        if (infantry) {
                            boolean valid = Filters.and(Filters.character, Filters.not(Filters.aboardOrAboardCargoOf(Filters.or(Filters.vehicle, Filters.starfighter)))).accepts(game, _tripper);
                            if (!valid) {
                                subAction.appendEffect(new LoseCardFromTableEffect(subAction, buried, true));
                                return;
                            }
                            explodeInfantryOrVehicle(game, subAction, buried, _tripper, true);
                            return;
                        }

                        if (vehicle) {
                            boolean valid = Filters.or(Filters.starfighter, Filters.non_creature_vehicle).accepts(game, _tripper);
                            if (!valid) {
                                subAction.appendEffect(new LoseCardFromTableEffect(subAction, buried, true));
                                return;
                            }
                            explodeInfantryOrVehicle(game, subAction, buried, _tripper, false);
                            return;
                        }

                        // Other Keyword.MINE titles: lose without special targeting
                        subAction.appendEffect(new LoseCardFromTableEffect(subAction, buried, true));
                    }
                }
        );
    }


    private void explodeBuriedTimerMine(final SwccgGame game, final SubAction subAction, final PhysicalCard mine) {
        final String mineOwner = mine.getOwner();
        final String tripperOwner = _tripper.getOwner();

        // Bill/AR: If YOU trip your own buried Timer Mine → simply discarded
        if (mineOwner.equals(tripperOwner)) {
            mine.setBuriedMine(false);
            game.getGameState().sendMessage(GameUtils.getCardLink(mine) + " is discarded (owner tripped own buried Timer Mine)");
            subAction.appendEffect(new PutStackedCardInLostPileEffect(subAction, mineOwner, mine, false));
            return;
        }

        // Opponent tripped it — attach for presence, then destiny (NOT a weapon destiny)
        mine.setBuriedMine(false);
        game.getGameState().removeCardsFromZone(Collections.singleton(mine));
        mine.stackOn(null, false, false);
        game.getGameState().attachCard(mine, _site);
        game.getGameState().sendMessage(GameUtils.getCardLink(mine) + " explodes (buried Timer Mine)");

        subAction.appendEffect(
                new DrawDestinyEffect(subAction, mineOwner) {
                    @Override
                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                        GameState gameState = game.getGameState();
                        gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                        if (totalDestiny != null) {
                            int numCharacters = (int) Math.floor(totalDestiny);
                            if (numCharacters > 0) {
                                // Timer Mines do not affect your characters; owner's choice = owner of affected characters
                                Collection<PhysicalCard> characters = Filters.filterActive(game, mine, SpotOverride.INCLUDE_UNDERCOVER,
                                        Filters.and(Filters.opponents(mine), Filters.character, Filters.present(mine)));
                                if (!characters.isEmpty()) {
                                    numCharacters = Math.min(characters.size(), numCharacters);
                                    subAction.appendEffect(
                                            new ChooseCardsToLoseFromTableEffect(subAction, tripperOwner, numCharacters, numCharacters, false, Filters.in(characters)));
                                }
                            }
                        }
                        subAction.appendEffect(new LoseCardFromTableEffect(subAction, mine, false));
                    }
                }
        );
    }

    private void explodeInfantryOrVehicle(final SwccgGame game, final SubAction subAction, final PhysicalCard mine, final PhysicalCard target, final boolean infantry) {
        final String playerId = mine.getOwner();
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        game.getGameState().beginWeaponFiring(mine, null);
                        game.getGameState().getWeaponFiringState().setTarget(target);
                        subAction.appendEffect(
                                new DrawDestinyEffect(subAction, playerId, 1, DestinyType.WEAPON_DESTINY) {
                                    @Override
                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                        return Collections.singletonList(target);
                                    }

                                    @Override
                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                        GameState gameState = game.getGameState();
                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                        if (totalDestiny == null) {
                                            gameState.sendMessage("Result: Failed due to failed destiny draw");
                                            subAction.appendEffect(new LoseCardFromTableEffect(subAction, mine, true));
                                            return;
                                        }
                                        gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                        float defenseValue;
                                        if (!infantry && Filters.starfighter.accepts(game, target)) {
                                            defenseValue = 5;
                                        }
                                        else {
                                            defenseValue = modifiersQuerying.getDefenseValue(gameState, target);
                                        }
                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));
                                        if ((totalDestiny + 2) > defenseValue) {
                                            gameState.sendMessage("Result: Succeeded");
                                            subAction.appendEffect(new LoseCardFromTableEffect(subAction, target, true));
                                        }
                                        else {
                                            gameState.sendMessage("Result: Failed");
                                        }
                                        subAction.appendEffect(new LoseCardFromTableEffect(subAction, mine, true));
                                    }
                                }
                        );
                        subAction.appendAfterEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        game.getGameState().finishWeaponFiring();
                                    }
                                }
                        );
                    }
                }
        );
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
