package com.gempukku.swccgo.cards.set214.dark;

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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Effect
 * Title: The First Order Was Just The Beginning
 */
public class Card214_012 extends AbstractNormalEffect {
    public Card214_012() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The First Order Was Just The Beginning", Uniqueness.UNIQUE, ExpansionSet.SET_14, Rarity.V);
        setGameText("If I Want That Map on table, deploy on table. Locations where there is a Resistance Agent or Rey gain [Episode VII]. Once per turn, may [download] a Jakku or Kijimi battleground. Your non-unique [Episode VII] troopers are forfeit +1. [Immune to Alter.]");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_14);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.I_Want_That_Map);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new IconModifier(self, Filters.sameLocationAs(self, Filters.or(Filters.Resistance_Agent, Filters.Rey)), Icon.EPISODE_VII));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.non_unique, Icon.EPISODE_VII, Filters.trooper), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_FIRST_ORDER_WAS_JUST_THE_BEGINNING__DOWNLOAD_JAKKU_OR_KIJIMI_BATTLEGROUND;

        List<TopLevelGameTextAction> actions = new LinkedList<>();

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy location from Reserve Deck");
            action.setActionMsg("Deploy a Jakku or Kijimi battleground from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.or(Filters.Jakku_location, Filters.Kijimi_location), Filters.battleground), true));

            actions.add(action);
        }
        return actions;
    }
}
