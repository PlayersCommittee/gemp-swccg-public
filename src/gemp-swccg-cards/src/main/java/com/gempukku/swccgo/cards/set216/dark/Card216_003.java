package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 16
 * Type: Starship
 * Subtype: Capital
 * Title: Avenger (V)
 */
public class Card216_003 extends AbstractCapitalStarship {
    public Card216_003() {
        super(Side.DARK, 1, 8, 6, 7, null, 3, 9, Title.Avenger, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLore("Key starship used to subjugate Outer Rim worlds. Reassigned to Death Squadron under the command of Captain Needa. Communications ship at the Battle of Endor.");
        setGameText("May add 4 pilots, 4 passengers, 2 vehicles, and 4 TIEs. Permanent pilot provides ability of 2. Once per game, may [download] a captain (or Imperial with armor) aboard (deploy -2).");
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_16);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        addKeywords(Keyword.DEATH_SQUADRON);
        setPilotCapacity(4);
        setPassengerCapacity(4);
        setVehicleCapacity(2);
        setTIECapacity(4);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.AVENGER_V__DOWNLOAD_CHARACTER;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy character aboard from Reserve Deck");
            action.setActionMsg("Deploy a captain (or Imperial with armor) aboard from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new DeployCardAboardFromReserveDeckEffect(action, Filters.or(Filters.captain, Filters.and(Filters.Imperial, Filters.hasArmor)), Filters.sameCardId(self), -2, true));

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {
        });
    }
}
