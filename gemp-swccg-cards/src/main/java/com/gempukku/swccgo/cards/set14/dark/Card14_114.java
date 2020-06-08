package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Capital
 * Title: Blockade Flagship
 */
public class Card14_114 extends AbstractCapitalStarship {
    public Card14_114() {
        super(Side.DARK, 1, 7, 6, 7, null, 3, 7, "Blockade Flagship", Uniqueness.UNIQUE);
        setLore("Aware that the Senate was sending a commission to Naboo, the Trade Federation evacuated many of its battleships, leaving its flagship to operate as the Droid Control Ship.");
        setGameText("May add 4 pilots, 4 passengers, 2 vehicles and 4 droid starfighters. Permanent pilot provides ability of 2. Once per game, may deploy a Neimoidian pilot aboard from Reserve Deck; reshuffle.");
        addPersona(Persona.BLOCKADE_FLAGSHIP);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.TRADE_FEDERATION, Icon.PILOT, Icon.NAV_COMPUTER);
        addKeywords(Keyword.DROID_CONTROL_SHIP);
        addModelType(ModelType.TRADE_FEDERATION_BATTLESHIP);
        setPilotCapacity(4);
        setPassengerCapacity(4);
        setVehicleCapacity(2);
        setStarfighterCapacity(4, Filters.droid_starfighter);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BLOCKADE_FLAGSHIP__DOWNLOAD_NEIMOIDIAN;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a Neimoidian pilot aboard " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.and(Filters.Neimoidian, Filters.pilot), Filters.Deploys_aboard_Blockade_Flagship, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
