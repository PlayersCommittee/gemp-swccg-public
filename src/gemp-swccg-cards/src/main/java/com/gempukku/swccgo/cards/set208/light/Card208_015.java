package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 8
 * Type: Effect
 * Title: Why Does Everyone Want To Go Back To Jakku?!
 */
public class Card208_015 extends AbstractNormalEffect {
    public Card208_015() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Why Does Everyone Want To Go Back To Jakku?!", Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setGameText("Deploy on table. May [download] Lor San Tekka. Once per turn, if you just lost a Resistance character from a Jakku site, may [upload] a Resistance character of lesser ability. [Immune to Alter]");
        addIcons(Icon.VIRTUAL_SET_8);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.WHY_DOES_EVERYONE_WANT_TO_GO_BACK_TO_JAKKU__DOWNLOAD_LOR_SAN_TEKKA;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Lor_San_Tekka)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Lor San Tekka from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Lor_San_Tekka, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.WHY_DOES_EVERYONE_WANT_TO_GO_BACK_TO_JAKKU__UPLOAD_RESISTANCE_CHARACTER;

        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.your(self), Filters.Resistance_character, Filters.hasAbility), Filters.Jakku_site)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), ((LostFromTableResult) effectResult).getCard());

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take character of ability < " + GuiUtils.formatAsString(ability) + " into hand from Reserve Deck");
            action.setActionMsg("Take a Resistance character of ability < " + GuiUtils.formatAsString(ability) + " into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Resistance_character, Filters.abilityLessThan(ability)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}