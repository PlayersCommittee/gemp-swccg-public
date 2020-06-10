package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: set 13
 * Type: Immediate Effect
 * Title: Jabba's Influence (v)
 */
public class Card501_010 extends AbstractImmediateEffect {
    public Card501_010() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Jabbas_Influence, Uniqueness.UNIQUE);
        setLore("Jabba makes offers one cannot refuse. Smugglers, thieves and competitors who do not acquiesce have been rumored to wake up with a bantha's head in their bed.");
        setGameText("If you just moved a captive to Audience Chamber, deploy on that location; no battles or Force Drains may take place here until end of your next turn. Once per game, may deploy a character with \"captive\" in Gametext here from Reserve deck; reshuffle. Immune to Control.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_13);
        isImmuneToCardTitle(Title.Control);
        setTestingText("Jabba's Influence (v)");
        setVirtualSuffix(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Audience_Chamber;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.movedToLocation(game, effectResult, Filters.escorting(Filters.any), Filters.Audience_Chamber)) {
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.Audience_Chamber, null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
           RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
           action.appendEffect(
                    new AddUntilEndOfPlayersNextTurnModifierEffect(action, self.getOwner(), new MayNotInitiateBattleAtLocationModifier(self, Filters.hasAttached(self)),"No battles at " + GameUtils.getCardLink(self.getAttachedTo())
            ));
            action.appendEffect(
                    new AddUntilEndOfPlayersNextTurnModifierEffect(action, self.getOwner(), new MayNotForceDrainAtLocationModifier(self, Filters.hasAttached(self)),"No force drains at " + GameUtils.getCardLink(self.getAttachedTo())
            ));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
       GameTextActionId gameTextActionId = GameTextActionId.JABBAS_INFLUENCE__DOWNLOAD_CHARACTER;
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Choose target(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.character, Filters.gameTextContains("captive")), Filters.here(self), true)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}