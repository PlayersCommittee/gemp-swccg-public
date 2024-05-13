package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Resistance
 * Title: Paige Tico
 */
public class Card216_041 extends AbstractResistance {
    public Card216_041() {
        super(Side.LIGHT, 3, 2, 2, 2, 5, Title.Paige, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLore("Female gunner.");
        setGameText("Adds 1 to weapon destiny draws and defense value of anything she is aboard as a passenger. While out of play, adds 1 to your total power where you have a Resistance character of ability = 2. If just lost, may place her out of play.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_16);
        addKeywords(Keyword.FEMALE, Keyword.GUNNER);
    }

    @Override
    public List<Modifier> getGameTextWhileOutOfPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalPowerModifier(self, Filters.sameLocationAs(self, Filters.and(Filters.your(self.getOwner()), Filters.Resistance_character, Filters.abilityEqualTo(2))), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.hasPassenger(self), 1));
        modifiers.add(new DefenseValueModifier(self, Filters.hasPassenger(self), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, playerId, self, false));
            return Collections.singletonList(action);
        }
        return null;
    }
}