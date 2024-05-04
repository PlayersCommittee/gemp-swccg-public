package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Mos Espa
 */
public class Card11_043 extends AbstractSite {
    public Card11_043() {
        super(Side.LIGHT, Title.Mos_Espa, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.C);
        setLocationLightSideGameText("During your control phase, may move your characters from Slave Quarters to here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.CONTROL)
                && GameConditions.canSpotLocation(game, Filters.Slave_Quarters)) {
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.character, Filters.Slave_Quarters, self, false)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, Filters.character, Filters.Slave_Quarters, self, false);
                action.setText("Move from Slave Quarters to here");
                actions.add(action);
            }
        }
        return actions;
    }
}