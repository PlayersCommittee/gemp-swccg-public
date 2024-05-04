package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Location
 * Subtype: Site
 * Title: Starkiller Base: Interrogation Room (Prison)
 */
public class Card208_054 extends AbstractSite {
    public Card208_054() {
        super(Side.DARK, "Starkiller Base: Interrogation Room (Prison)", Title.Starkiller_Base, Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setLocationDarkSideGameText("During your move phase, may use 2 Force to move Kylo between here and a battleground.");
        setLocationLightSideGameText("If Kylo here and And Now You'll Give It To Me on table, opponent's Force drains are +2 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EPISODE_VII, Icon.INTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_8);
        addKeywords(Keyword.PRISON);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        Filter otherBattleground = Filters.and(Filters.otherLocation(self), Filters.battleground);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.MOVE)
                && GameConditions.canSpotLocation(game, otherBattleground)) {

            if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.Kylo, self, otherBattleground, false, 2)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.Kylo, self, otherBattleground, false, 2);
                action.setText("Move Kylo from here to a battleground");
                actions.add(action);
            }
            if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.Kylo, otherBattleground, self, false, 2)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.Kylo, otherBattleground, self, false, 2);
                action.setText("Move Kylo from a battleground to here");
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnLightSideOfLocation);
        
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new AndCondition(new HereCondition(self, Filters.Kylo),
                new OnTableCondition(self, Filters.And_Now_Youll_Give_It_To_Me)), 2, opponent));
        return modifiers;
    }
}