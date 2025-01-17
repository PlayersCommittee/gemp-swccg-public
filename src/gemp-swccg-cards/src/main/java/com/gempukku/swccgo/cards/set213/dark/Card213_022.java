package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Effect
 * Subtype: Effect
 * Title: Working Much More Closely
 */
public class Card213_022 extends AbstractNormalEffect {
    public Card213_022() {
        super(Side.DARK, 7, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Working Much More Closely", Uniqueness.UNIQUE, ExpansionSet.SET_13, Rarity.V);
        setLore("Hologram.");
        setGameText("Deploy on table. Your non-[Dagobah] holograms are canceled. While present with an opponent's Jedi, Maul is defense value +2 and immune to attrition. Once per game, if [Set 13] Maul on table, may [download] Qi'ra. If Maul just lost, place Effect in Used Pile. [Immune to Alter.]");
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_13);
        addKeyword(Keyword.HOLOGRAM);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter maulPresentWithOpponentsJedi = Filters.and(Filters.Maul, Filters.presentWith(self, Filters.and(Filters.opponents(self), Filters.Jedi)));
        
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DefenseValueModifier(self, maulPresentWithOpponentsJedi, 2));
        modifiers.add(new ImmuneToAttritionModifier(self, maulPresentWithOpponentsJedi));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.WORKING_MUCH_MORE_CLOSELY__DOWNLOAD_QIRA;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.QIRA)
                && GameConditions.canSpot(game, self, Filters.and(Icon.VIRTUAL_SET_13, Filters.Maul))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Qi'ra from Reserve Deck");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Qira, true));
            return Collections.singletonList(action);
        }
        return null;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.your(self), Filters.not(Icon.DAGOBAH), Filters.hologram))
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
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.and(Filters.your(self), Filters.not(Icon.DAGOBAH), Filters.hologram))) {

            Collection<PhysicalCard> cardsToCancel = Filters.filterActive(game, self, TargetingReason.TO_BE_CANCELED, Filters.and(Filters.your(self), Filters.not(Icon.DAGOBAH), Filters.hologram));

            if (!cardsToCancel.isEmpty()) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Cancel holograms");

                action.appendEffect(
                        new CancelCardsOnTableEffect(action, cardsToCancel));
                actions.add(action);
            }
        }

        if (TriggerConditions.justLost(game, effectResult, Filters.Maul)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            actions.add(action);
        }
        
        return actions;
    }
}