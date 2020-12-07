package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.*;

/**
 * Set: Set 1
 * Type: Effect
 * Title: Seeking An Audience (V)
 */
public class Card201_009 extends AbstractNormalEffect {
    public Card201_009() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Seeking_An_Audience, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'With your wisdom, I'm sure that we can work out an arrangement which will be mutually beneficial and enable us to avoid any unpleasant confrontation.'");
        setGameText("If a Jabba's Palace site on table, deploy on table. His Name Is Anakin is canceled. Once per turn, if Han frozen, may use 2 Force to [download] non-[Reflections III]: C-3PO, Chewie, Lando, Leia, or R2-D2. Once per game, may place this Effect out of play to retrieve an alien or [Independent] starship into hand. [Immune to Alter]");
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_1);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpotLocation(game, Filters.Jabbas_Palace_site);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if ((TriggerConditions.isPlayingCard(game, effect, Filters.His_Name_Is_Anakin))
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
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.His_Name_Is_Anakin)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.His_Name_Is_Anakin, Title.His_Name_Is_Anakin);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.SEEKING_AN_AUDIENCE__DOWNLOAD_NON_REFLECTIONS_III_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Han, Filters.frozenCaptive))
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, new HashSet<Persona>(Arrays.asList(Persona.C3PO, Persona.CHEWIE, Persona.LANDO, Persona.LEIA, Persona.R2D2)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy non-[Reflections III] C-3PO, Chewie, Lando, Leia, or R2-D2 from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.not(Icon.REFLECTIONS_III), Filters.or(Filters.C3PO, Filters.Chewie, Filters.Lando, Filters.Leia, Filters.R2D2)), true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
        action.setText("Place out of play to retrieve card into hand");
        action.setActionMsg("Retrieve an alien or [Independent] starship into hand");
        // Pay cost(s)
        action.appendCost(
                new PlaceCardOutOfPlayFromTableEffect(action, self));
        // Perform result(s)
        action.appendEffect(
                new RetrieveCardIntoHandEffect(action, playerId, Filters.or(Filters.alien, Filters.and(Icon.INDEPENDENT, Filters.starship))));
        actions.add(action);

        return actions;
    }
}