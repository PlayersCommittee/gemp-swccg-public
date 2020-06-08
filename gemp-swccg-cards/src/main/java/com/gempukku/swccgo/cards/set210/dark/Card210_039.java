package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 10
 * Type: Location
 * Subtype: System
 * Title: Mustafar
 */
public class Card210_039 extends AbstractSystem {
    public Card210_039() {
        super(Side.DARK, Title.Mustafar, 7);
        setLocationDarkSideGameText("Once per turn, may use 1 Force to deploy a Mustafar site from Reserve Deck; reshuffle.");
        setLocationLightSideGameText("While Vader on table, unless Padme at a Mustafar location, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.VIRTUAL_SET_10, Icon.PLANET, Icon.EPISODE_I);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MUSTAFAR__DOWNLOAD_MUSTAFAR_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerOnDarkSideOfLocation, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Mustafar Site");
            action.setActionMsg("Deploy a Mustafar site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(new UseForceEffect(action, playerOnDarkSideOfLocation, 1));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.and(Filters.Mustafar_site, Filters.unique), Title.Mustafar, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        OnTableCondition VaderOnTable = new OnTableCondition(self, Filters.Vader);
        NotCondition PadmeNotAtMustafarLocation = new NotCondition(new AtCondition(self, Filters.Amidala, Filters.Mustafar_Location));

        modifiers.add(new ForceDrainModifier(self, new AndCondition(new ControlsCondition(playerOnLightSideOfLocation, self),
                VaderOnTable, PadmeNotAtMustafarLocation), -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}