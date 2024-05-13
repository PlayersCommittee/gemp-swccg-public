package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotFireWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * Subtype: Alien
 * Title: Cliegg Lars
 */
public class Card217_032 extends AbstractAlien {
    public Card217_032() {
        super(Side.LIGHT, 3, 2, 3, 2, 4, "Cliegg Lars", Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setLore("Moisture farmer.");
        setGameText("While present with Shmi, Vader may not fire weapons here. Once per game, may [download] Owen (or a device that deploys on a site) here. If with Shmi or a Vaporator, Force drain +1 here.");
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotFireWeaponsModifier(self, Filters.and(Filters.Vader, Filters.here(self)), new PresentWithCondition(self, Filters.Shmi)));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new WithCondition(self, Filters.or(Filters.Shmi, Filters.Vaporator)), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CLIEGG_LARS__DEPLOY_OWEN_OR_DEVICE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Owen or a device here");
            action.setActionMsg("Deploy Owen (or a device that deploys on a site) here from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));

            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.Owen, Filters.and(Filters.device, Filters.deploys_on_site)), Filters.here(self), true));
            return Collections.singletonList(action);
        }

        return null;
    }
}
