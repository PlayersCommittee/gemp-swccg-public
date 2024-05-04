package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 7
 * Type: Character
 * Subtype: Imperial
 * Title: General Nevar
 */
public class Card601_084 extends AbstractImperial {
    public Card601_084() {
        super(Side.DARK, 2, 3, 3, 2, 4, "General Nevar", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("Leader.");
        setGameText("[Pilot] 2, 3: Blizzard 2. Deploys -1 on Hoth. Once per game, may deploy a marker site with < 2 [Dark Side] or a battleground (except Endor system or a war room). Once per game, if piloting at a site where no characters present, may peek at opponent's hand.");
        addIcons(Icon.PILOT, Icon.DAGOBAH, Icon.LEGACY_BLOCK_7);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
        setMatchingVehicleFilter(Filters.Blizzard_2);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Blizzard_2)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Deploys_at_Hoth));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__GENERAL_NEVAR__DEPLOY_LOCATION;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a location from Reserve Deck");
            action.setActionMsg("Deploy a marker site with < 2 [DS] or a battleground except Endor or a war room");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action,
                    Filters.or(Filters.and(Filters.marker_site, Filters.iconCountLessThan(Icon.DARK_FORCE, 2)), Filters.and(Filters.battleground, Filters.except(Filters.Endor_system), Filters.except(Filters.war_room))),
                    true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__GENERAL_NEVAR__PEEK_AT_HAND;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isPiloting(game, self, Filters.at(Filters.and(Filters.site, Filters.not(Filters.wherePresent(self, Filters.character)))))) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at opponent's hand");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new PeekAtOpponentsHandEffect(action, playerId));
            actions.add(action);
        }

        return actions;
    }
}
