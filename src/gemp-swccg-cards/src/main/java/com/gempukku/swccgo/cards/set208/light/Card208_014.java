package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForceDrainCompletedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Effect
 * Title: Prophecy Of The Force
 */
public class Card208_014 extends AbstractNormalEffect {
    public Card208_014() {
        super(Side.LIGHT, 2, PlayCardZoneOption.ATTACHED, Title.Prophecy_Of_The_Force, Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setLore("The turning of Vader and the loss of Palpatine was the beginning of a new era for the entire galaxy.");
        setGameText("Deploy on a site. While at a battleground, adds one [Dark Force] icon and one [Light Force] icon here. Once per turn, if a player just Force drained at a site with total ability > 5, relocate this Effect to that site. Once per game, may retrieve a card with 'Anakin' in title into hand. [Immune to Alter]");
        addIcons(Icon.VIRTUAL_SET_8);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter attachedTo = Filters.and(Filters.hasAttached(self), Filters.battlegroundIgnoringForceIconsAddedFromCard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, attachedTo, Icon.DARK_FORCE, 1));
        modifiers.add(new IconModifier(self, attachedTo, Icon.LIGHT_FORCE, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.forceDrainCompleted(game, effectResult, Filters.and(Filters.site, Filters.not(Filters.sameSite(self))))
                && !GameConditions.hasGameTextModification(game, self, ModifyGameTextType.PROPHECY_OF_THE_FORCE__MAY_NOT_BE_RELOCATED)
                && GameConditions.isOncePerTurn(game, self, gameTextSourceCardId, gameTextActionId)) {
            ForceDrainCompletedResult forceDrainCompleted = (ForceDrainCompletedResult) effectResult;
            PhysicalCard forceDrainSite = forceDrainCompleted.getLocation();
            if (game.getModifiersQuerying().getTotalAbilityAtLocation(game.getGameState(), forceDrainCompleted.getPerformingPlayerId(), forceDrainSite) > 5) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate to " + GameUtils.getFullName(forceDrainSite));
                action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(forceDrainSite));
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AttachCardFromTableEffect(action, self, forceDrainSite));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.PROPHECY_OF_THE_FORCE__RETRIEVE_CARD_WITH_ANAKIN_INTO_HAND;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a card into hand");
            action.setActionMsg("Retrieve a card with 'Anakin' in title into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerId, Filters.titleContains("Anakin")));
            return Collections.singletonList(action);
        }
        return null;
    }
}