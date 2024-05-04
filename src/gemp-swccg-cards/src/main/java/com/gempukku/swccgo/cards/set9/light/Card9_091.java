package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractAutomatedWeapon;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Weapon
 * Subtype: Automated
 * Title: Orbital Mine
 */
public class Card9_091 extends AbstractAutomatedWeapon {
    public Card9_091() {
        super(Side.LIGHT, 2, PlayCardZoneOption.ATTACHED, Title.Orbital_Mine, Uniqueness.DIAMOND_1, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLore("Often Stolen from the Empire. Used by the Rebellion to protect key spaceports. Can be released to cover the retreat of a fleeing strike force.");
        setGameText("Deploy at the same system as a bomber. 'Explodes' if an opponents starship deploys or moves here. Draw destiny. Add 2 when targeting a capital starship. Starship lost if total destiny > defense value. Orbital mine is also lost.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.MINE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.sameSystemAs(self, Filters.and(Filters.your(self), Filters.bomber));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        Filter hereFilter = Filters.here(self);
        Filter starshipFilter = Filters.and(Filters.opponents(self), Filters.starship, Filters.present(self));

        // Check condition(s)
        Collection<PhysicalCard> starships = null;
        if (TriggerConditions.justDeployedToLocation(game, effectResult, starshipFilter, hereFilter)) {
            starships = Collections.singletonList(((PlayCardResult) effectResult).getPlayedCard());
        }
        else if (TriggerConditions.movedToLocation(game, effectResult, starshipFilter, hereFilter)) {
            starships = Filters.filter(((MovedResult) effectResult).getMovedCards(), game, starshipFilter);
        }
        if (starships != null && !starships.isEmpty()) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("'Explode'");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose starship", Filters.in(starships)) {
                        @Override
                        protected void cardSelected(final PhysicalCard starshipTargeted) {
                            action.addAnimationGroup(starshipTargeted);
                            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " 'explode' due to " + GameUtils.getCardLink(starshipTargeted));
                            action.appendCost(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            game.getGameState().beginWeaponFiring(self, null);
                                            game.getGameState().getWeaponFiringState().setTarget(starshipTargeted);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(starshipTargeted);
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

                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), starshipTargeted);
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));
                                                            if (totalDestiny > defenseValue) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, starshipTargeted, true));
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

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyModifier(self, 2, Filters.capital_starship));
        return modifiers;
    }
}
