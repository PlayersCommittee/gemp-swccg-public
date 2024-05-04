package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractResistance;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Character
 * Subtype: Resistance
 * Title: Major Harter Kalonia
 */
public class Card208_009 extends AbstractResistance {
    public Card208_009() {
        super(Side.LIGHT, 3, 3, 1, 2, 4, "Major Harter Kalonia", Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setLore("Female.");
        setGameText("Resistance characters here may not have their forfeit reduced. If you just forfeited a 'hit' non-droid character from same or related site, may lose 1 Force to place that character in Used Pile.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_8);
        addKeyword(Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self, Filters.and(Filters.Resistance_character, Filters.here(self))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justForfeitedHitToLostPileFromLocation(game, effectResult, Filters.and(Filters.your(self), Filters.non_droid_character), Filters.sameOrRelatedSite(self))) {
            PhysicalCard justForfeitedCard = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(justForfeitedCard) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(justForfeitedCard) + " in Used Pile");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, justForfeitedCard, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
