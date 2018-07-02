package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ResetForfeitUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Rebel
 * Title: Lieutenant Tarn Mison
 */
public class Card7_030 extends AbstractRebel {
    public Card7_030() {
        super(Side.LIGHT, 3, 2, 1, 2, 4, "Lieutenant Tarn Mison", Uniqueness.UNIQUE);
        setLore("Former Imperial pilot. Joined the Alliance shortly after the Battle of Yavin. Flew cover for Bright Hope during the evacuation of Hoth. Expert marksman.");
        setGameText("Deploys -1 aboard your unique (•) Rebel starfighter. Adds 2 to power of anything he pilots. When starfighter he pilots fires a starship weapon, characters aboard target are forfeit = 0 for remainder of turn.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, -1, Filters.and(Filters.your(self), Filters.unique, Filters.Rebel_starfighter)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isFiringWeapon(game, effect, Filters.starship_weapon, Filters.and(Filters.starfighter, Filters.hasPiloting(self)))) {
            WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
            final Collection<PhysicalCard> characters = Filters.filterActive(game, self, Filters.and(Filters.aboardOrAboardCargoOf(Filters.in(weaponFiringState.getTargets())), Filters.canBeTargetedBy(self)));
            if (!characters.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Reset forfeit of characters to 0");
                action.setActionMsg("Reset forfeit of characters aboard " + GameUtils.getAppendedNames(weaponFiringState.getTargets()) + " to 0");
                // Perform result(s)
                action.appendEffect(
                        new ResetForfeitUntilEndOfTurnEffect(action, characters, 0));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
