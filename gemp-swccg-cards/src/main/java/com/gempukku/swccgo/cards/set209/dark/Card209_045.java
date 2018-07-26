package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.AbstractEpicEventPlayable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseWeaponEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.actions.CommencePrimaryIgnitionState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayEpicEventAction;
import com.gempukku.swccgo.logic.actions.TopLevelEpicEventGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ResetHyperspeedModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CalculatingEpicEventTotalResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Epic Event
 * Title: Commence Primary Ignition (V)
 */
public class Card209_045 extends AbstractEpicEventDeployable {
    public Card209_045() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.Commence_Primary_Ignition);
        setVirtualSuffix(true);
        setGameText("Once per game, deploy on Death Star; it is hyperspeed = 2. Superlaser may not target planet systems. Once during your control phase, may fire Superlaser as follows: Prepare Single Reactor Ignition: If Death Star orbiting Jedha or Scarif, target your related unique (•) battleground site, even if converted (regardless of objective restrictions). Fire!: Draw destiny. It's Beautiful: If destiny +X > 8, site is 'blown away,' and opponent loses 3 Force. X = number of Death Star sites on table.");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_9);
    }

    //@Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Death_Star_system;
    }

    //@Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetHyperspeedModifier(self, Filters.Death_Star_system, 2));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.planet_system, Filters.Superlaser));
        return modifiers;
    }

    //@Override
    protected List<TopLevelEpicEventGameTextAction> getEpicEventGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canDrawDestiny(game, playerId)) {
            //find system death star is orbiting
            final PhysicalCard planetSystem = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.system, Filters.isOrbitedBy(Filters.Death_Star_system)));
            //check if it's orbiting Jedha or Scarif
            if (planetSystem != null) {
                if (planetSystem.getBlueprint().getTitle().equals(Title.Jedha) || planetSystem.getBlueprint().getTitle().equals(Title.Scarif)) {
                    //check if there's a related battleground site owned by you (even if converted)
                    final Filter yourSiteEvenIfConverted = Filters.and(Filters.or(Filters.your(self), Filters.convertedLocationOnTopOfLocation(Filters.your(self))), Filters.relatedSite(planetSystem), Filters.unique, Filters.battleground_site);
                    //final PhysicalCard relatedSite = Filters.findFirstFromTopLocationsOnTable(game, yourSiteEvenIfConverted);
                    final PhysicalCard relatedSite = Filters.findFirstFromAllOnTable(game, yourSiteEvenIfConverted);
                    if (relatedSite != null) {
                        //there is a valid site, fire away
                        final GameState gameState = game.getGameState();
                        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                        final float valueForX = modifiersQuerying.getVariableValue(gameState, self, Variable.X, Filters.countTopLocationsOnTable(game, Filters.and(Filters.Death_Star_site, Filters.notIgnoredDuringEpicEventCalculation)));

                        final PhysicalCard superlaser = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Superlaser));
                        if (superlaser != null) {
                            final PhysicalCard deathStar = superlaser.getAttachedTo();
                            final CommencePrimaryIgnitionState epicEventState = new CommencePrimaryIgnitionState(self);

                            final PlayEpicEventAction action = new PlayEpicEventAction(self);
                            // Choose target(s)
                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose site to target", yourSiteEvenIfConverted) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard selectedCard) {
                                            action.addAnimationGroup(selectedCard);
                                            action.setText("Attempt to 'blow away' " + GameUtils.getFullName(selectedCard));
                                            action.setEpicEventState(epicEventState);

                                            new PassthruEffect(action) {
                                                @Override
                                                protected void doPlayEffect(final SwccgGame game) {
                                                    // Update usage limit(s)
                                                    action.appendUsage(
                                                            new UseWeaponEffect(action, deathStar, superlaser));
                                                    action.addAnimationGroup(superlaser);
                                                    action.addAnimationGroup(selectedCard);
                                                    // Update Epic Event State
                                                    epicEventState.setSuperlaser(superlaser);
                                                    String actionText = "Have " + GameUtils.getCardLink(deathStar) + " fire " + GameUtils.getCardLink(superlaser) + " at " + GameUtils.getCardLink(selectedCard);
                                                    // Allow response(s)
                                                    action.allowResponses(actionText,
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Begin weapon firing
                                                                    action.appendEffect(
                                                                            new PassthruEffect(action) {
                                                                                @Override
                                                                                protected void doPlayEffect(SwccgGame game) {
                                                                                    gameState.sendMessage(GameUtils.getCardLink(deathStar) + " fires " + GameUtils.getCardLink(superlaser) + " at " + GameUtils.getCardLink(selectedCard));
                                                                                    gameState.activatedCard(playerId, superlaser);
                                                                                    gameState.cardAffectsCard(playerId, superlaser, selectedCard);
                                                                                    gameState.beginWeaponFiring(superlaser, null);
                                                                                    gameState.getWeaponFiringState().setCardFiringWeapon(deathStar);
                                                                                    gameState.getWeaponFiringState().setTarget(selectedCard);
                                                                                    // Finish weapon firing
                                                                                    action.appendAfterEffect(
                                                                                            new PassthruEffect(action) {
                                                                                                @Override
                                                                                                protected void doPlayEffect(SwccgGame game) {
                                                                                                    gameState.finishWeaponFiring();
                                                                                                }
                                                                                            }
                                                                                    );
                                                                                }
                                                                            }
                                                                    );
                                                                    // 2) Fire!
                                                                    action.appendEffect(
                                                                            new DrawDestinyEffect(action, playerId, 1, DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY) {
                                                                                @Override
                                                                                protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                                                    // 3) It's Beautiful
                                                                                    // Emit effect result that total is being calculated
                                                                                    action.appendEffect(
                                                                                            new TriggeringResultEffect(action, new CalculatingEpicEventTotalResult(playerId, self)));
                                                                                    action.appendEffect(
                                                                                            new PassthruEffect(action) {
                                                                                                @Override
                                                                                                protected void doPlayEffect(final SwccgGame game) {
                                                                                                    gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));

                                                                                                    gameState.sendMessage("X: " + GuiUtils.formatAsString(valueForX));

                                                                                                    //compute total
                                                                                                    float total = modifiersQuerying.getEpicEventCalculationTotal(gameState, self, (totalDestiny != null ? totalDestiny : 0) + valueForX);
                                                                                                    gameState.sendMessage("Epic Event Total: " + GuiUtils.formatAsString(total));

                                                                                                    if (total > 8) {
                                                                                                        gameState.sendMessage("Result: Succeeded");
                                                                                                        action.appendEffect(
                                                                                                                new BlowAwayEffect(action, selectedCard, true));
                                                                                                        action.appendEffect(
                                                                                                                new LoseForceEffect(action, opponent, 3));
                                                                                                    } else {
                                                                                                        gameState.sendMessage("Result: Failed");
                                                                                                    }


                                                                                                }
                                                                                            }
                                                                                    );
                                                                                }
                                                                            }
                                                                    );
                                                                }
                                                            }
                                                    );
                                                }
                                            };
                                        }
                                    }
                            );
                        }
                    }
                }
            }
        }
        return null;
    }

}
