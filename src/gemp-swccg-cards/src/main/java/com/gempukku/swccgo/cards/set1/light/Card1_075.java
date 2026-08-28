package com.gempukku.swccgo.cards.set1.light;

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
 * Title: Combined Attack
 */
public class Card1_075 extends AbstractLostInterrupt {
    public Card1_075() {
        super(Side.LIGHT, 4, Title.Combined_Attack, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Efficient cooperation allowed the Rebels to coordinate the attack of their small starfighters effectively at the Battle of Yavin.");
        setGameText("During a battle, target opponent's starship present with two (or more) of your starship weapons. Add all weapon destiny draws together. Apply that total separately for each weapon in an order of your choosing.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Weapons segment / default during-battle (AR ~line 3717), not only at battle initiation.
        if (!GameConditions.isDuringBattle(game)) {
            return null;
        }

        final Filter starshipFilter = Filters.and(
                Filters.opponents(self),
                Filters.starship,
                Filters.presentInBattle,
                new Filter() {
                    @Override
                    public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState,
                                          com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying,
                                          PhysicalCard physicalCard) {
                        return Filters.filterActive(game, self, getStarshipWeaponFilter(self, physicalCard)).size() >= 2;
                    }
                });

        if (!GameConditions.canTarget(game, self, starshipFilter)) {
            return null;
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Combine starship weapon firings");
        // Initiation: target 2+ eligible weapons + one starship. Interrupt itself has no Force cost;
        // each weapon pays its own fire cost as that shot initiates (Gergall / pending option A).
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose opponent's starship", starshipFilter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedStarship) {
                        final Filter weaponFilter = getStarshipWeaponFilter(self, targetedStarship);
                        action.appendTargeting(
                                new ChooseCardsOnTableEffect(action, playerId,
                                        "Choose two or more starship weapons (selection order is fire/apply order)",
                                        2, Integer.MAX_VALUE, weaponFilter) {
                                    @Override
                                    protected void cardsSelected(final Collection<PhysicalCard> selectedWeapons) {
                                        action.addAnimationGroup(targetedStarship);
                                        action.addAnimationGroup(selectedWeapons);
                                        // Responses (Sense, Boring Conversation Anyway, etc.). If canceled,
                                        // weapons have not fired and may still fire normally.
                                        action.allowResponses("Combine weapon firings at " + GameUtils.getCardLink(targetedStarship),
                                                new RespondablePlayCardEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                                        if (finalTarget == null) {
                                                            finalTarget = targetedStarship;
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
     * Your starship weapons (including permanent weapons on your starships) that can currently fire at the target.
     */
    static Filter getStarshipWeaponFilter(final PhysicalCard source, final PhysicalCard target) {
        final Filter targetFilter = Filters.sameCardId(target);
        return Filters.and(
                Filters.your(source),
                Filters.or(
                        Filters.and(Filters.starship_weapon, Filters.canBeFiredAt(source, targetFilter, 0)),
                        new Filter() {
                            @Override
                            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState,
                                                  com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying,
                                                  PhysicalCard physicalCard) {
                                // Permanent starship weapons live on the starship card (not CardCategory.WEAPON).
                                if (!Filters.starship.accepts(gameState, modifiersQuerying, physicalCard)) {
                                    return false;
                                }
                                if (physicalCard.getBlueprint().getPermanentWeapon(physicalCard) == null) {
                                    return false;
                                }
                                return physicalCard.getBlueprint().getFireWeaponAction(
                                        physicalCard.getOwner(), gameState.getGame(), physicalCard, false, 0, source,
                                        false, Filters.none, null, targetFilter, false) != null;
                            }
                        }
                )
        );
    }
}
