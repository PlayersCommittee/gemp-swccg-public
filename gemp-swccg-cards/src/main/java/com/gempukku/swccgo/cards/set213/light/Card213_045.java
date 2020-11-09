package com.gempukku.swccgo.cards.set213.light;

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
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Defensive Shield
 * Title: Clumsy And Stupid
 */
public class Card213_045 extends AbstractDefensiveShield {
    public Card213_045() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Clumsy And Stupid");
        setLore("'Lord Vader, the fleet has moved out of lightspeed and we're preparing to aah...ukh...uh...uuuuukkk!'");
        setGameText("Plays on table. Opponent's Undercover spies are immune to Nevar Yalnal. Field Promotion is canceled if on Ozzel or opponent's [Maintenance] card. If Monnok just revealed your hand, you may place up to two cards in your Used Pile.");
        addIcons(Icon.VIRTUAL_SET_13);
    }

    @Override
    public List<Modifier> getWhileInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.opponents(self.getOwner()), Filters.undercover_spy), Title.Nevar_Yalnal));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Monnok, ModifyGameTextType.MONNOK__PUT_TWO_CARDS_IN_USED));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.title(Title.Field_Promotion), Filters.or(Filters.Ozzel, Filters.icon(Icon.MAINTENANCE)))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
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
