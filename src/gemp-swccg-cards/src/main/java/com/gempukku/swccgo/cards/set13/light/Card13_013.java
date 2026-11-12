package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Lost
 * Title: Desperate Times
 */
public class Card13_013 extends AbstractLostInterrupt {
    public Card13_013() {
        super(Side.LIGHT, 4, "Desperate Times", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Han's best tactic when outnumbered? Shoot first and don't worry about asking questions later.");
        setGameText("During a battle where your opponent has more characters participating than you, fire (for free) one blaster that cannot fire repeatedly, even if that blaster was already fired this battle. Add 1 to your total weapon destiny.");
        // AR errata: no longer includes Episode I icon
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // During a battle where opponent has more characters participating than you
        if (GameConditions.isDuringBattle(game)) {
            int yourParticipating = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.character, Filters.participatingInBattle));
            int opponentParticipating = Filters.countActive(game, self, Filters.and(Filters.opponents(self), Filters.character, Filters.participatingInBattle));
            if (opponentParticipating > yourParticipating) {
                // One blaster that cannot fire repeatedly (weapon-sourced repeatedly OR same-target repeatedly from e.g. Greeve)
                Filter cannotFireRepeatedly = Filters.not(Filters.or(Filters.mayFireRepeatedly, Filters.mayFireRepeatedlyAtSameTarget));
                // Even if already fired: ignore per-battle fire limit only for this interrupt's firing
                Filter weaponFilter = Filters.and(Filters.your(self), Filters.blaster, Filters.presentInBattle, cannotFireRepeatedly,
                        Filters.canBeFiredForFree(self, 0, true));
                if (GameConditions.canSpot(game, self, weaponFilter)) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Fire a blaster");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose blaster to fire", weaponFilter) {
                                @Override
                                protected void cardSelected(final PhysicalCard weapon) {
                                    action.addAnimationGroup(weapon);
                                    // Allow response(s)
                                    action.allowResponses("Fire " + GameUtils.getCardLink(weapon),
                                            new RespondablePlayCardEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new FireWeaponEffect(action, weapon, true, Filters.canBeTargetedBy(self)) {
                                                                @Override
                                                                protected boolean isignorePerAttackOrBattleLimit() {
                                                                    // Fire even if already fired; do not grant ongoing may-fire-again
                                                                    // (cancel must not leave a leftover second fire).
                                                                    return true;
                                                                }

                                                                @Override
                                                                protected List<Modifier> getWeaponFiringModifiers(String playerId, SwccgGame game, PhysicalCard weapon) {
                                                                    return Collections.singletonList(
                                                                            new TotalWeaponDestinyModifier(self, weapon, 1));
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
            }
        }
        return null;
    }
}
