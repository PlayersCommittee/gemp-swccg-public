package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByPermanentWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeCapturedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 1
 * Type: Device
 * Title: Tatooine Utility Belt (V)
 */
public class Card601_103 extends AbstractCharacterDevice {
    public Card601_103() {
        super(Side.LIGHT, 4, "Tatooine Utility Belt", Uniqueness.UNRESTRICTED, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Survival gear, food and tools are stored in small compartments. Made from bantha hide. Used by Luke and other Tatooine inhabitants.");
        setGameText("Deploy on Han or a Lars. Character cannot be targeted by [Permanent Weapon] weapons or be captured. If just lost, place this device in Used Pile. Force loss from ...Or Be Destroyed must come from Reserve Deck (if possible) and may not be reduced below 2.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.LEGACY_BLOCK_1);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Han, Filters.Owen, Filters.Beru));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Han, Filters.Owen, Filters.Beru));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByPermanentWeaponsModifier(self, Filters.hasAttached(self)));
        modifiers.add(new MayNotTargetToBeCapturedModifier(self, Filters.hasAttached(self)));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Or_Be_Destroyed, ModifyGameTextType.LEGACY__OR_BE_DESTROYED__FORCE_LOSS));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLost(game, effectResult, self)
            || TriggerConditions.justForfeitedToLostPileFromLocation(game, effectResult, self, Filters.any)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.appendEffect(
                    new PlaceCardsInUsedPileFromOffTableEffect(action, Collections.singletonList(self)));

            return Collections.singletonList(action);
        }

        return null;
    }
}