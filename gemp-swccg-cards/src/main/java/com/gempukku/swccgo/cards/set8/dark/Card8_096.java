package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameTextActionModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.SpecialDeliveryCompletedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Corporal Drazin
 */
public class Card8_096 extends AbstractImperial {
    public Card8_096() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Corporal Drazin", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.U);
        setLore("Stormtrooper assigned to Commander Igar's honor guard. Accompanied Vader on Drazin's homeworld of Bespin. Shot C-3PO there.");
        setGameText("Once during each of your control phases, may take one Special Delivery into hand from Reserve Deck; reshuffle. Special Delivery is immune to Alter. When Drazin completes Special Delivery, you may take two additional cards into hand.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.STORMTROOPER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CORPORAL_DRAZIN__UPLOAD_SPECIAL_DELIVERY;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Special Delivery into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Special_Delivery, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Special_Delivery, Title.Alter));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.specialDeliveryCompletedBy(game, effectResult, self)) {
            PhysicalCard specialDelivery = ((SpecialDeliveryCompletedResult) effectResult).getSpecialDelivery();
            GameTextAction gameTextAction = ((SpecialDeliveryCompletedResult) effectResult).getGameTextAction();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            action.setText("Allow to take two additional cards into hand");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfGameTextActionModifierEffect(action, gameTextAction,
                            new ModifyGameTextModifier(self, specialDelivery, ModifyGameTextType.SPECIAL_DELIVERY__TAKE_TWO_ADDITIONAL_CARDS_INTO_HAND), null));
            return Collections.singletonList(action);
        }
        return null;
    }
}
