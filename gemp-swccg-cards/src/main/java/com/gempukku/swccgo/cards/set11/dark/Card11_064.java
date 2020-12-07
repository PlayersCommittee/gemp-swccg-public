package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployOrMoveSithProbeDroidToLocationsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Droid
 * Title: Sith Probe Droid
 */
public class Card11_064 extends AbstractDroid {
    public Card11_064() {
        super(Side.DARK, 3, 1, 1, 2, Title.Sith_Probe_Droid);
        setManeuver(3);
        setLore("Patrol droids utilized by the Sith. Each droid has several multispectral imaging devices and a communications package. Used by Maul to track down Amidala.");
        setGameText("When deployed, immediately retrieve 1 Force. Limit 1 Sith Probe Droid per location. If present with Amidala during your control phase may use 3 Force to relocate this Sith Probe Droid to Maul's site, and relocate Maul to same site as Amidala.");
        addIcons(Icon.TATOOINE,Icon.EPISODE_I);
        addModelTypes(ModelType.PROBE, ModelType.RECON);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        //this needs to work while it's in hand, but it shouldn't work while its game text is canceled while on table
        if(self.isGameTextCanceled())
            return null;
        List<Modifier> modifiers = new LinkedList<Modifier>();
        //limit 1 Sith Probe Droid per location
        modifiers.add(new MayNotDeployOrMoveSithProbeDroidToLocationsModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        if(self.isGameTextCanceled())
            return null;
        List<Modifier> modifiers = new LinkedList<Modifier>();
        //limit 1 Sith Probe Droid per location
        //when this is only in getGameTextAlwaysOnModifiers it doesn't seem work when Sith Probe Droid (V) is being deployed
        modifiers.add(new MayNotDeployOrMoveSithProbeDroidToLocationsModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        //limit 1 Sith Probe Droid per location
        //when this is only in getGameTextAlwaysOnModifiers it doesn't seem work when Sith Probe Droid (V) is being deployed
        modifiers.add(new MayNotDeployOrMoveSithProbeDroidToLocationsModifier(self));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        //when deployed, immediately retrieve 1 Force
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            String playerId = self.getOwner();
            final RequiredGameTextTriggerAction action;
            action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendEffect(new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        //needs to work while inactive, but not while gametext is canceled
        if(self.isGameTextCanceled())
            return null;

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        //limit one Sith Probe Droid per location
        if(TriggerConditions.isTableChanged(game, effectResult)) {
            //check if there are multiple Sith Probe Droids at any locations

            Collection<PhysicalCard> sithProbeDroids = Filters.filterAllOnTable(game, Filters.and(Filters.Sith_Probe_Droid,Filters.with(null,SpotOverride.INCLUDE_ALL,Filters.Sith_Probe_Droid)));
            if(!sithProbeDroids.isEmpty()) {
                //find all of the locations that have multiple sith probe droids
                Collection<PhysicalCard> locationsToEnforceLimit = new HashSet<PhysicalCard>();
                for(PhysicalCard spd:sithProbeDroids) {
                    locationsToEnforceLimit.addAll(Filters.filterTopLocationsOnTable(game, Filters.sameLocation(spd)));
                }

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);

                action.setText("Enforce limit of 1 Sith Probe Droid per location");

                final SwccgGame gm = game;

                //the player whose turn it is chooses a location to enforce the limit
                String currentPlayerId = gm.getGameState().getCurrentPlayerId();

                action.insertEffect(new ChooseCardEffect (action,currentPlayerId,"Choose a location to enforce limit of 1 Sith Probe Droid",
                        locationsToEnforceLimit) {

                    protected void cardSelected(PhysicalCard selectedCard) {
                        if(selectedCard!=null) {
                            gm.getGameState().sendMessage("Enforcing limit of 1 Sith Probe Droid at "+GameUtils.getCardLink(selectedCard));

                            String playerOne = self.getOwner();
                            String playerTwo = gm.getOpponent(playerOne);

                            Filter sithProbeDroidHere = Filters.and(Filters.Sith_Probe_Droid,Filters.at(selectedCard));

                            //check if both players have Sith Probe Droids at the selected location
                            Collection<PhysicalCard> playerOneDroids = Filters.filterActive(gm, null, SpotOverride.INCLUDE_ALL, Filters.and(sithProbeDroidHere,Filters.owner(playerOne)));
                            Collection<PhysicalCard> playerTwoDroids = Filters.filterActive(gm, null, SpotOverride.INCLUDE_ALL, Filters.and(sithProbeDroidHere,Filters.owner(playerTwo)));

                            if(playerOneDroids.size()>0&&playerTwoDroids.size()>0) {
                                LinkedList<PhysicalCard> allDroidsHere = new LinkedList<PhysicalCard>();
                                allDroidsHere.addAll(playerOneDroids);
                                allDroidsHere.addAll(playerTwoDroids);
                                Collections.shuffle(allDroidsHere);

                                PhysicalCard toLose = allDroidsHere.getFirst();
                                gm.getGameState().sendMessage("Randomly selected to lose " + toLose.getOwner() + "'s "+GameUtils.getCardLink(toLose));
                                action.insertEffect(new LoseCardFromTableEffect(action, allDroidsHere.getFirst(), true));
                            } else if(playerOneDroids.size()>1) {
                                action.setText("Have " + playerOne + " choose a Sith Probe Droid to be lost");
                                action.insertEffect(new ChooseCardToLoseFromTableEffect(action, playerOne, null,true, Filters.in(playerOneDroids), SpotOverride.INCLUDE_ALL));
                            } else if(playerTwoDroids.size()>1) {
                                action.setText("Have " + playerTwo + " choose a Sith Probe Droid to be lost");
                                action.insertEffect(new ChooseCardToLoseFromTableEffect(action, playerTwo, null,true, Filters.in(playerTwoDroids), SpotOverride.INCLUDE_ALL));
                            }

                        }
                    }
                });

                return Collections.singletonList(action);
            }
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        final PhysicalCard maul = Filters.findFirstActive(game, self, Filters.Maul);
        final PhysicalCard amidala = Filters.findFirstActive(game, self, Filters.Amidala);

        if (maul!=null&&amidala!=null
                && GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.isPresentWith(game, self, amidala)) {

            final PhysicalCard maulsSite = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.site,Filters.sameSite(maul),Filters.locationCanBeRelocatedTo(self,3)));
            final PhysicalCard amidalasSite = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.site,Filters.sameSite(amidala),Filters.locationCanBeRelocatedTo(maul,3)));

            if (amidalasSite!=null&&maulsSite!=null
                    && GameConditions.canTarget(game, self, maul)
                    && GameConditions.canTarget(game, self, amidala)
                    && GameConditions.canTarget(game, self, amidalasSite)
                    && GameConditions.canTarget(game, self, maulsSite)
            ) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);

                action.appendCost(new PayRelocateBetweenLocationsCostEffect(action, playerId, self, maulsSite, 3));

                action.setText("Relocate Sith Probe Droid and Maul");
                action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(maul) + "'s site and relocate "
                        + GameUtils.getCardLink(maul) + " to " + GameUtils.getCardLink(amidala) + "'s site");
                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        //droid to Maul's site
                        action.appendEffect(new RelocateBetweenLocationsEffect(action, self, maulsSite));
                        //Maul to Amidala's site
                        action.appendEffect(new RelocateBetweenLocationsEffect(action, maul, amidalasSite));
                    }
                });

                return Collections.singletonList(action);
            }
        }
        return null;
    }
}