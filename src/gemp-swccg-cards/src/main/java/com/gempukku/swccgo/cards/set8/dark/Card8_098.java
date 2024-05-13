package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Corporal Misik
 */
public class Card8_098 extends AbstractImperial {
    public Card8_098() {
        super(Side.DARK, 3, 2, 2, 1, 3, "Corporal Misik", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Son of a Tatooine diplomat. Stormtrooper. Biker scout. Deadly shot. Develops speeder bike tactics with Sergeant Barich. Hopes for promotion to Emperor's Demonstration Team.");
        setGameText("Adds 3 to power of any speeder bike he pilots. When firing a Scout Blaster, adds 1 to his total weapon destiny. When with Barich in a battle and piloting or driving a vehicle, may add that vehicle's maneuver to your total power.");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.BIKER_SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.speeder_bike));
        modifiers.add(new TotalWeaponDestinyForWeaponFiredByModifier(self, 1, Filters.scout_blaster));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattleWith(game, self, Filters.Barich)) {
            final PhysicalCard vehicle = Filters.findFirstActive(game, self,
                    Filters.and(Filters.vehicle, Filters.or(Filters.hasPiloting(self), Filters.hasDriving(self)), Filters.hasManeuver));
            if (vehicle != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Add vehicle's maneuver to total power");
                action.setActionMsg("Add " + GameUtils.getCardLink(vehicle) + " maneuver to total power");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                float maneuver = game.getModifiersQuerying().getManeuver(game.getGameState(), vehicle);
                                action.appendEffect(
                                        new ModifyTotalPowerUntilEndOfBattleEffect(action, maneuver, playerId,
                                                "Adds " + GuiUtils.formatAsString(maneuver) + " to total power"));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
