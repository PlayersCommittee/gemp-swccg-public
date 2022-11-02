package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractAutomatedWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Weapon
 * Subtype: Automated
 * Title: Explosive Charge
 */
public class Card8_090 extends AbstractAutomatedWeapon {
    public Card8_090() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Explosive_Charge);
        setLore("Standard explosive charge carried by Rebel commandos. When used in multiple, these charges have the explosive capacity to level a heavily armored structure.");
        setGameText("Deploy on an interior planet site you occupy. Immune to Overload. Place in Used Pile if opponent controls this site. If you just lost a battle opponent initiated here, may draw destiny. All cards (except Effects) here are lost if destiny > 4 (otherwise, Charge lost).");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.interior_planet_site, Filters.occupies(self.getOwner()));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Overload));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.controls(game, opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.sameSite(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Place in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.lostBattleAt(game, effectResult, playerId, Filters.here(self))
                && GameConditions.isDuringBattleInitiatedBy(game, opponent)
                && GameConditions.canDrawDestiny(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw destiny");
            // Choose target(s)
            action.appendCost(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            game.getGameState().beginWeaponFiring(self, null);

                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId, 1, DestinyType.WEAPON_DESTINY) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            GameState gameState = game.getGameState();
                                            if (totalDestiny == null) {
                                                gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                return;
                                            }

                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                            if (totalDestiny > 4) {
                                                gameState.sendMessage("Result: Succeeded");
                                                Collection<PhysicalCard> cardsHere = Filters.filterAllOnTable(game,
                                                        Filters.and(Filters.or(Filters.character, Filters.vehicle, Filters.starship, Filters.weapon, Filters.device), Filters.here(self), Filters.except(Filters.Effect)));
                                                if (!cardsHere.isEmpty()) {
                                                    action.appendEffect(
                                                            new LoseCardsFromTableEffect(action, cardsHere, true));
                                                }
                                            } else {
                                                gameState.sendMessage("Result: Failed");
                                                action.appendEffect(
                                                        new LoseCardFromTableEffect(action, self, true));
                                            }
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
