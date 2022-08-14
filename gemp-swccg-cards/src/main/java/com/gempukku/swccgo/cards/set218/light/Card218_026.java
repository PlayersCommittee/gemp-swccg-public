package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Effect
 * Title: Ounee Ta (V)
 */
public class Card218_026 extends AbstractNormalEffect {
    public Card218_026() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Ounee_Ta, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Jabba's decadent behavior makes him susceptible to deception. Leia and Lando exploited this weakness, posing as Jabba's kind of scum.");
        setGameText("Deploy on a battleground site. When deployed, may take top card of Lost Pile into hand. Opponent may not modify the deploy cost of your characters deploying here. Once per game, may place an Interrupt (except Ghhhk) from opponent's Lost Pile out of play. [Immune to Alter.]");
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_18);
        addKeyword(Keyword.DEPLOYS_ON_SITE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.battleground_site;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.hasLostPile(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take top card of Lost Pile into hand");
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromLostPileEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OUNEE_TA_V__PLACE_INTERRUPT_OUT_OF_PLAY;
        String opponent = game.getOpponent(playerId);

        if (GameConditions.hasLostPile(game, opponent)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Interrupt out of play");
            action.setActionMsg("Place any Interrupt (except Ghhhk) from opponent's Lost Pile out of play");

            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, opponent, Filters.and(Filters.Interrupt, Filters.not(Filters.Ghhhk)), false));

            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToDeployCostModifiersToLocationModifier(self, Filters.and(Filters.your(self), Filters.character), Filters.opponents(self), Filters.here(self)));
        return modifiers;
    }
}