package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCloakModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Rebel
 * Title: Theron Nett (V)
 */
public class Card204_012 extends AbstractRebel {
    public Card204_012() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Theron Nett", Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Experienced smuggler from Ord Mantell. Piloted Red 10 at the Battle of Yavin. Long-time wingman of Garven Dreis.");
        setGameText("[Pilot] 2. While at a battleground system, opponent's starships may not 'cloak'. May lose 1 Force (free if piloting Red 10) to cancel a just-drawn weapon destiny targeting a starship he is piloting.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.RED_SQUADRON, Keyword.SMUGGLER);
        setMatchingStarshipFilter(Filters.Red_10);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotCloakModifier(self, Filters.and(Filters.opponents(self), Filters.starship), new AtCondition(self, Filters.battleground_system)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, Filters.and(Filters.starship, Filters.hasPiloting(self)))
                && GameConditions.canCancelDestiny(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel weapon destiny");
            if (!GameConditions.isPiloting(game, self, Filters.Red_10)) {
                // Pay cost(s)
                action.appendCost(
                        new LoseForceEffect(action, playerId, 1, true));
            }
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
