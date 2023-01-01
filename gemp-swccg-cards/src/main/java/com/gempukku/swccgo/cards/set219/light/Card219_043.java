package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Effect
 * Title: Phoenix Squadron Operations
 */
public class Card219_043 extends AbstractNormalEffect {
    public Card219_043() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Phoenix Squadron Operations", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setGameText("If Lothal on table, deploy on table. Chopper, Sabine, and Zeb are deploy -1. " +
                    "Once per turn, may [download] Malachor, Mandalore, or Seelos (or Chopper, Wedge, Zeb, or an A-wing to a Lothal location). [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_19);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.Lothal_system);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostModifier(self, Filters.or(Filters.Chopper, Filters.Sabine, Filters.Zeb), -1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.PHOENIX_SQUADRON__DOWNLOAD_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canSpotLocation(game, Filters.Lothal_location)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a location or a card to Lothal");
            action.setActionMsg("Deploy Malachor, Mandalor, or Seelos (or Wedge, Zeb, Chopper, or an A-wing to a Lothal location) from Reserve Deck");

            Filter systems = Filters.or(Filters.Malachor_system, Filters.Mandalore_system, Filters.Seelos_system);
            Filter other = Filters.or(Filters.Wedge, Filters.Zeb, Filters.Chopper, Filters.A_wing);
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.or(systems, other),
                            Filters.locationAndCardsAtLocation(Filters.Lothal_location),
                            systems, Filters.none, false, true));

            return Collections.singletonList(action);
        }
        return null;
    }
}