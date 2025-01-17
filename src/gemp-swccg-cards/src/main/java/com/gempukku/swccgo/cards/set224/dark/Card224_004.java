package com.gempukku.swccgo.cards.set224.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceDestinyCardOutOfPlayEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 24
 * Type: Character
 * Subtype: Dark Jedi Master
 * Title: Darth Plagueis
 */
public class Card224_004 extends AbstractDarkJediMaster {
    public Card224_004() {
        super(Side.DARK, 1, 4, 3, 7, 9, "Darth Plagueis", Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("Muun leader. Trade Federation.");
        setGameText("Once per game, if drawn for destiny, may place this card out of play to make that destiny draw +6. Other Dark Jedi (except Sidious) are lost. While alone, your characters with 'Trade Federation' in lore deploy -1 to [Episode I] locations. Immune to attrition.");
        setSpecies(Species.MUUN);
        addKeywords(Keyword.LEADER);
        addIcons(Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_24);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.loreContains("Trade Federation")), new AloneCondition(self), -1, Filters.and(Icon.EPISODE_I, Filters.location)));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Blaster_Deflection)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.isDuringWeaponFiringAtTarget(game, Filters.any, Filters.here(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalDrawnAsDestinyTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DARTH_PLAGUEIS__PLACE_OUT_OF_PLAY;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDestinyCardMatchTo(game, self)
                && GameConditions.canPlaceDestinyCardOutOfPlay(game)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place out of play to make destiny +6");
            action.setActionMsg("Place out of play to make destiny draw +6");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PlaceDestinyCardOutOfPlayEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, 6));
            return Collections.singletonList(action);
        }
        return null;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {

        // Other Dark Jedi (except Sidious) are lost.
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> toLose = Filters.filterActive(game, self, Filters.and(Filters.Dark_Jedi, Filters.other(self), Filters.except(Filters.Sidious)));
            if (!toLose.isEmpty()) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make other Dark Jedi lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(toLose) + " lost");

                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, toLose));
                actions.add(action);
            }
        }
        return actions;
    }
}
