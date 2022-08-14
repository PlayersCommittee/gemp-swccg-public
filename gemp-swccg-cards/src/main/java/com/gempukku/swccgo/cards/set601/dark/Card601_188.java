package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 8
 * Type: Character
 * Subtype: Alien
 * Title: Vigo (V)
 */
public class Card601_188 extends AbstractAlien {
    public Card601_188() {
        super(Side.DARK, 3, 3, 3, 2, 4, Title.Vigo, Uniqueness.RESTRICTED_3);
        setVirtualSuffix(true);
        setLore("One of Xizor's hand-picked lieutenants. Ascended as Black Sun agent from gangster to manager. Earned title of Vigo from old Tionese for 'nephew'.");
        setGameText("Deploys -1 to Coruscant. While present with Xizor, forfeit +2 and opponents spies may not deploy to same non-battleground site. Once per game, may use 1 Force to [download] a non-war room battleground planet site (or system) not already on table.");
        addIcons(Icon.REFLECTIONS_II, Icon.LEGACY_BLOCK_8);
        addKeywords(Keyword.GANGSTER, Keyword.BLACK_SUN_AGENT);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Deploys_at_Coruscant));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition presentWithXizor = new PresentWithCondition(self, Filters.Xizor);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, presentWithXizor, 2));
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.spy),
                presentWithXizor, Filters.and(Filters.sameSite(self), Filters.non_battleground_location)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.VIGO__DOWNLOAD_LOCATION;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy location from Reserve Deck");
            action.setActionMsg("Deploy a non-war room battleground planet site (or system) not already on table from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.not(Filters.war_room), Filters.or(Filters.planet_site, Filters.planet_system), Filters.not(Filters.sameTitleAs(self, Filters.onTable))), Filters.battleground, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
