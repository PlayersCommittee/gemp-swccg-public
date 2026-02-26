package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Spaceport Street
 */
public class Card7_291 extends AbstractSite {
    public Card7_291() {
        super(Side.DARK, "Spaceport Street", Uniqueness.DIAMOND_1, ExpansionSet.SPECIAL_EDITION, Rarity.F);
        setLocationDarkSideGameText("May not deploy to Bespin, Dagobah, Endor, Hoth, Kashyyyk or Yavin 4. During your move phase, may move free between here and any related ◇ spaceport site.");
        setLocationLightSideGameText("May not deploy to Bespin, Dagobah, Endor, Hoth, Kashyyyk or Yavin 4. Rebels are power -1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.SPACEPORT_SITE);
        addMayNotBePartOfSystem(Title.Bespin, Title.Dagobah, Title.Endor, Title.Hoth, Title.Kashyyyk, Title.Yavin_4, Title.Ahch_To);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        Filter relatedGenericSpaceportSite = Filters.and(Filters.generic, Filters.spaceport_site, Filters.relatedLocation(self));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.MOVE)
                && GameConditions.canSpotLocation(game, relatedGenericSpaceportSite)) {
            if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, SpotOverride.INCLUDE_UNDERCOVER, Filters.any, self, relatedGenericSpaceportSite, true)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, SpotOverride.INCLUDE_UNDERCOVER, Filters.any, self, relatedGenericSpaceportSite, true);
                action.setText("Move from here to related spaceport site");
                actions.add(action);
            }
            if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, SpotOverride.INCLUDE_UNDERCOVER, Filters.any, relatedGenericSpaceportSite, self, true)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, SpotOverride.INCLUDE_UNDERCOVER, Filters.any, relatedGenericSpaceportSite, self, true);
                action.setText("Move from related spaceport site to here");
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Rebel, Filters.here(self)), -1));
        return modifiers;
    }
}