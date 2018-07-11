package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
•A Brave Resistance [Surprise Assault (V)] 5
        [Premiere - C1]
        Lore: Blank
        EFFECT
        Text: If your [Episode VII] location on table, deploy on table. Your Force generation is +1 at Jakku battlegrounds you occupy.
        During your deploy phase, may place a Resistance character from hand on top of Used Pile to /\ a Resistance character. [Immune to Alter.]
        [Set 9]
*/

public class Card209_016 extends AbstractEffect {
    public Card209_016() {
        super(Side.LIGHT, 5.0f, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Brave Resistance", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("");
        setGameText("If your [Episode VII] location on table, deploy on table. Your Force generation is +1 at Jakku battlegrounds you occupy.\n" +
                "        During your deploy phase, may place a Resistance character from hand on top of Used Pile to /\\ a Resistance character. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_9);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpotLocation(game, Filters.and(Filters.location, Icon.EPISODE_VII));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.Jakku_battleground, Filters.occupies(playerId)), 1, playerId));

        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Card action 1
        GameTextActionId gameTextActionId = GameTextActionId.A_BRAVE_RESISTANCE__UPLOAD_RESISTANCE_CHARACTER;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place a Resistance character from hand on to of Used Pile");
            action.setActionMsg("/\\ a Resistance character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PutCardFromHandOnUsedPileEffect(action, playerId, Filters.Resistance_character, false));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Resistance_character, true));
            actions.add(action);
        }
        return actions;
    }

}