package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Device
 * Title: Observation Holocam
 */
public class Card1_204 extends AbstractDevice {
    public Card1_204() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Observation Holocam");
        setLore("Remote surveillance viewers with droid controllers supplement security. Can activate alarms and automated weapons when needed, bringing help to endangered locations.");
        setGameText("Deploy on a site. Adds 1 to the total weapon destiny of each of your automated weapons at same and adjacent sites. During battle, may add the power of one of your characters at an adjacent site you control to your total.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveModifier(self));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.and(Filters.your(self), Filters.automated_weapon, Filters.atSameOrAdjacentSite(self)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, gameTextSourceCardId)
                && GameConditions.isInBattle(game, self)) {
            final Filter filter = Filters.and(Filters.your(playerId), Filters.character, Filters.canUseDevice(self), Filters.at(Filters.and(Filters.adjacentSite(self), Filters.controls(playerId))));
            if (GameConditions.canSpot(game, self, filter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Add power from adjacent site");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character", filter) {
                            @Override
                            protected void cardSelected(final PhysicalCard character) {
                                action.addAnimationGroup(character);
                                float power = game.getModifiersQuerying().getPower(game.getGameState(), character);
                                action.setActionMsg("Add " + GameUtils.getCardLink(character) + "'s power of " + GuiUtils.formatAsString(power) + " to total power");
                                // Update usage limit(s)
                                action.appendUsage(
                                        new UseDeviceEffect(action, character, self));
                                // Perform result(s)
                                action.appendEffect(
                                        new ModifyTotalPowerUntilEndOfBattleEffect(action, power, playerId,
                                                "Adds " + GameUtils.getCardLink(character) + "'s power of " + GuiUtils.formatAsString(power) + " to total power"));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}