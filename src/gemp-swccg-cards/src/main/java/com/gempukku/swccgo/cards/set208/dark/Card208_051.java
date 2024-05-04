package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationImmuneToLimitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Location
 * Subtype: System
 * Title: Starkiller Base
 */
public class Card208_051 extends AbstractSystem {
    public Card208_051() {
        super(Side.DARK, Title.Starkiller_Base, 1, ExpansionSet.SET_8, Rarity.V);
        setLocationDarkSideGameText("Opponent may not limit Force generation at Starkiller Base locations. Once per turn, may [download] a Starkiller Base battleground.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.EPISODE_VII, Icon.PLANET, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationImmuneToLimitModifier(self, Filters.Starkiller_Base_location, Filters.opponents(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.STARKILLER_BASE__DOWNLOAD_STARKILLER_BASE_BATTLEGROUND;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy battleground from Reserve Deck");
            action.setActionMsg("Deploy a Starkiller Base battleground from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Starkiller_Base_location, Filters.battleground, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}