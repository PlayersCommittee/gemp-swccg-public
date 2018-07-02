package com.gempukku.swccgo.cards.set3.light;

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
 * Title: Infantry Mine
 */
public class Card3_076 extends AbstractAutomatedWeapon {
    public Card3_076() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Infantry_Mine);
        setLore("Typically stolen by Rebel spies and saboteurs from the perimeter of high-security Imperial installations.");
        setGameText("Deploy at same exterior site as your mining droid. 'Explodes' if a character deploys or moves (without using a vehicle or starfighter) to or across same site. Draw destiny. Character lost if destiny +2 > defense value. Infantry Mine is also lost.");
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
        Filter characterFilter = Filters.and(Filters.character, Filters.present(self), Filters.not(Filters.aboardOrAboardCargoOf(Filters.or(Filters.vehicle, Filters.starfighter))));

        // Check condition(s)
        Collection<PhysicalCard> characters = null;
        if (TriggerConditions.justDeployedToLocation(game, effectResult, characterFilter, sameSiteFilter)) {
            characters = Collections.singletonList(((PlayCardResult) effectResult).getPlayedCard());
        }
        else if (TriggerConditions.movedToLocation(game, effectResult, characterFilter, sameSiteFilter)) {
            characters = Filters.filter(((MovedResult) effectResult).getMovedCards(), game, characterFilter);
        }
        if (characters != null && !characters.isEmpty()) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("'Explode'");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose character", Filters.in(characters)) {
                        @Override
                        protected void cardSelected(final PhysicalCard characterTargeted) {
                            action.addAnimationGroup(characterTargeted);
                            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " 'explode' due to " + GameUtils.getCardLink(characterTargeted));
                            action.appendTargeting(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            game.getGameState().beginWeaponFiring(self, null);
                                            game.getGameState().getWeaponFiringState().setTarget(characterTargeted);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(characterTargeted);
                                                        }

                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, self, true));
                                                                return;
                                                            }

                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), characterTargeted);
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));
                                                            if ((totalDestiny + 2) > defenseValue) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, characterTargeted, true));
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
