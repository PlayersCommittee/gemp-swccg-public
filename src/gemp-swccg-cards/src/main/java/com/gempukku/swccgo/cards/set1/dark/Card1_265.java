package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.FireWeaponsCombinedAction;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Precise Attack
 */
public class Card1_265 extends AbstractLostInterrupt {
    public Card1_265() {
        super(Side.DARK, 4, Title.Precise_Attack, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("'Only Imperial stormtroopers are so precise.'");
        setGameText("During a battle, target opponent's character or vehicle present with two (or more) of your weapons. Add all weapon destiny draws together. Apply that total separately for each weapon in an order of your choosing.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Weapons segment / default during-battle (AR ~line 3717), not only at battle initiation.
        if (!GameConditions.isDuringBattle(game)) {
            return null;
        }

        final Filter targetFilter = Filters.and(
                Filters.opponents(self),
                Filters.or(Filters.character, Filters.vehicle),
                Filters.presentInBattle,
                new Filter() {
                    @Override
                    public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState,
                                          com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying,
                                          PhysicalCard physicalCard) {
                        return Filters.filterActive(game, self, getCharacterOrVehicleWeaponFilter(self, physicalCard)).size() >= 2;
                    }
                });

        if (!GameConditions.canTarget(game, self, targetFilter)) {
            return null;
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Combine character and vehicle weapon firings");
        // Initiation: target 2+ eligible weapons + one character or vehicle. Interrupt itself has no Force cost;
        // each weapon pays its own fire cost as that shot initiates (Gergall / same structure as Combined Attack).
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose opponent's character or vehicle", targetFilter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                        final Filter weaponFilter = getCharacterOrVehicleWeaponFilter(self, targetedCard);
                        action.appendTargeting(
                                new ChooseCardsOnTableEffect(action, playerId,
                                        "Choose two or more weapons",
                                        2, Integer.MAX_VALUE, weaponFilter) {
                                    @Override
                                    protected void cardsSelected(final Collection<PhysicalCard> selectedWeapons) {
                                        action.addAnimationGroup(targetedCard);
                                        action.addAnimationGroup(selectedWeapons);
                                        // Responses (Sense, etc.). If canceled, weapons have not fired and may still fire normally.
                                        action.allowResponses("Combine weapon firings at " + GameUtils.getCardLink(targetedCard),
                                                new RespondablePlayCardEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                                        if (finalTarget == null) {
                                                            finalTarget = targetedCard;
                                                        }
                                                        List<PhysicalCard> weaponsInOrder = new ArrayList<PhysicalCard>(selectedWeapons);
                                                        action.appendEffect(
                                                                new FireWeaponsCombinedAction(action, self, weaponsInOrder, finalTarget));
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
        return Collections.singletonList(action);
    }

    /**
     * Your character and vehicle weapons (including permanent weapons on your characters) that can currently fire at the target.
     * Starship weapons are not included; Precise Attack is the ground analog of Combined Attack.
     */
    static Filter getCharacterOrVehicleWeaponFilter(final PhysicalCard source, final PhysicalCard target) {
        final Filter fireAtFilter = Filters.sameCardId(target);
        return Filters.and(
                Filters.your(source),
                Filters.or(
                        Filters.and(
                                Filters.or(Filters.character_weapon, Filters.vehicle_weapon),
                                Filters.canBeFiredAt(source, fireAtFilter, 0)),
                        new Filter() {
                            @Override
                            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState,
                                                  com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying,
                                                  PhysicalCard physicalCard) {
                                // Permanent character weapons live on the character card (not CardCategory.WEAPON).
                                if (!Filters.character.accepts(gameState, modifiersQuerying, physicalCard)) {
                                    return false;
                                }
                                if (physicalCard.getBlueprint().getPermanentWeapon(physicalCard) == null) {
                                    return false;
                                }
                                return physicalCard.getBlueprint().getFireWeaponAction(
                                        physicalCard.getOwner(), gameState.getGame(), physicalCard, false, 0, source,
                                        false, Filters.none, null, fireAtFilter, false) != null;
                            }
                        }
                )
        );
    }
}
