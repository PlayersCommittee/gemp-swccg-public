package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractAutomatedWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Weapon
 * Subtype: Automated
 * Title: Vehicle Mine
 */
public class Card3_162 extends AbstractAutomatedWeapon {
    public Card3_162() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Vehicle Mine");
        setLore("Launches and detonates when activated by a passing metallic mass or the repulsorlift field of an approaching speeder. The shrapnel can hit even the quickest craft.");
        setGameText("Deploy at same exterior site as your mining droid. 'Explodes' if starfighter (use 5 as defense value) or non-creature vehicle deploys or moves to or across same site. Draw destiny. Target lost if destiny +2 > defense value. Vehicle Mine is also lost.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.MINE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.exterior_site, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.mining_droid)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        Filter sameSiteFilter = Filters.sameSite(self);
        Filter starfighterOrVehicleFilter = Filters.and(Filters.or(Filters.starfighter, Filters.non_creature_vehicle), Filters.present(self));

        // Check condition(s)
        Collection<PhysicalCard> starfightersOrVehicles = null;
        if (TriggerConditions.justDeployedToLocation(game, effectResult, starfighterOrVehicleFilter, sameSiteFilter)) {
            starfightersOrVehicles = Collections.singletonList(((PlayCardResult) effectResult).getPlayedCard());
        }
        else if (TriggerConditions.movedToLocation(game, effectResult, starfighterOrVehicleFilter, sameSiteFilter)) {
            starfightersOrVehicles = Filters.filter(((MovedResult) effectResult).getMovedCards(), game, starfighterOrVehicleFilter);
        }

        if (starfightersOrVehicles != null && !starfightersOrVehicles.isEmpty()) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("'Explode'");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose vehicle or starfighter", Filters.in(starfightersOrVehicles)) {
                        @Override
                        protected void cardSelected(final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " 'explode' due to " + GameUtils.getCardLink(cardTargeted));
                            action.appendCost(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            game.getGameState().beginWeaponFiring(self, null);
                                            game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(cardTargeted);
                                                        }

                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, self, true));
                                                                return;
                                                            }

                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            float defenseValue = Filters.starfighter.accepts(game, cardTargeted) ? 5 : modifiersQuerying.getDefenseValue(game.getGameState(), cardTargeted);
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));
                                                            if ((totalDestiny + 2) > defenseValue) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardTargeted, true));
                                                            } else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                            action.appendEffect(
                                                                    new LoseCardFromTableEffect(action, self, true));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            // Clean up
            action.appendAfterEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            game.getGameState().finishWeaponFiring();
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
