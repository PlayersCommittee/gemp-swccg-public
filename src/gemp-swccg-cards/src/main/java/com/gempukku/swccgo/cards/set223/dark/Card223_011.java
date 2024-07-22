package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Device
 * Title: Electro-Rangefinder (V)
 */
public class Card223_011 extends AbstractDevice {
    public Card223_011() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "Electro-Rangefinder", Uniqueness.UNRESTRICTED, ExpansionSet.SET_23, Rarity.V);
        setVirtualSuffix(true);
        setLore("Long-range stereoscopic sighting device connected to the cannons of an Imperial walker. Calibrated to allow the AT-AT commander to accurately fire at distant targets.");
        setGameText("Deploy on an AT-AT; may immediately search your Used Pile and take an AT-AT Cannon or [Hoth] Epic Event into hand; reshuffle. Adds 2 to this AT-AT's weapon destiny draws (unless targeting a character).");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_23);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.AT_AT;
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.AT_AT;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 2, Filters.hasAttached(self), Filters.not(Filters.character)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ELECTRO_RANGEFINDER__UPLOAD_CARD_FROM_PILE;
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Used Pile");
            action.setActionMsg("Take an AT-AT Cannon or [Hoth] Epic Event into hand from Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromUsedPileEffect(action, playerId, Filters.or(Filters.AT_AT_Cannon, Filters.and(Filters.icon(Icon.HOTH), Filters.Epic_Event)), true));
            actions.add(action);
        }
        return actions;
    }
}
