package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Defensive Shield
 * Title: You Have Failed Me For The Last Time (V)
 */
public class Card501_027 extends AbstractDefensiveShield {
    public Card501_027() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "You Have Failed Me For The Last Time");
        setLore("'Lord Vader, the fleet has moved out of lightspeed and we're preparing to aah...ukh...uh...uuuuukkk!'");
        setGameText("Plays to table. Opponent's Undercover spies are immune to Nevar Yalnal. Field Promotion is canceled if on opponent's [M] card or Ozzel. If Monnok just revealed your hand, you may place up to 2 cards in your Used Pile.");
        addIcons(Icon.VIRTUAL_SET_13);
        setVirtualSuffix(true);
    }

    @Override
    public List<Modifier> getWhileInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.opponents(self.getOwner()), Filters.undercover_spy), Title.Nevar_Yalnal));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Monnok, ModifyGameTextType.MONNOK__PUT_TWO_CARDS_IN_USED));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        Filter fieldPromotionAttachedToOzzelOrMainChar = Filters.and(Filters.title(Title.Field_Promotion), Filters.attachedTo(Filters.or(Filters.Ozzel, Filters.icon(Icon.MAINTENANCE))));

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (GameConditions.canTargetToCancel(game, self, fieldPromotionAttachedToOzzelOrMainChar)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, fieldPromotionAttachedToOzzelOrMainChar, Title.Field_Promotion);
                actions.add(action);
            }
        }

        return actions;
    }
}
