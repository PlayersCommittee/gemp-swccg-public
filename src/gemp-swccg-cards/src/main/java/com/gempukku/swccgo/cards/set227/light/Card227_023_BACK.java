package com.gempukku.swccgo.cards.set227.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Effect
 * Title: Sacrifice For Something Bigger (back)
 */
public class Card227_023_BACK extends AbstractNormalEffect {
    public Card227_023_BACK() {
        super(Side.LIGHT, 7, PlayCardZoneOption.YOUR_SIDE_OF_LOCATION, Title.Sacrifice_For_Something_Bigger, Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setGameText("Moves like a character at normal use of the Force. Where present, your Rebels gain Phoenix Squadron, your total power is +3, and opponent's immunity to attrition is canceled. If a Dark Jedi (or two Imperials) control this location, place this Effect out of play. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_27);
        addImmuneToCardTitle(Title.Alter);
        setMayNotBePlacedInReserveDeck(true);
    }

    @Override
    public boolean isMovesLikeCharacter() {
        return true;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        // wherePresent(self) accepts LOCATIONS where this Effect is present (correct for the total-power
        // location filter); presentWith(self) accepts CARDS present with this Effect (for the keyword/immunity clauses).
        Filter locationsWherePresent = Filters.wherePresent(self);
        Filter cardsPresentWith = Filters.presentWith(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.your(self), Filters.Rebel, cardsPresentWith), Keyword.PHOENIX_SQUADRON));
        modifiers.add(new TotalPowerModifier(self, locationsWherePresent, 3, playerId));
        modifiers.add(new CancelImmunityToAttritionModifier(self, Filters.and(Filters.opponents(self), cardsPresentWith)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        Filter thisLocation = Filters.sameLocation(self);

        // Check condition(s): "If a Dark Jedi (or two Imperials) control this location..."
        if (TriggerConditions.isTableChanged(game, effectResult)
            && GameConditions.canBePlacedOutOfPlay(game, self)
            && (GameConditions.controlsWith(game, self, opponent, thisLocation, Filters.Dark_Jedi)
            || (GameConditions.controls(game, opponent, thisLocation)
            && GameConditions.canSpot(game, self, 2, Filters.and(Filters.opponents(self), Filters.Imperial, Filters.here(self)))))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
            action.setSingletonTrigger(true);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
