package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromOutsideOfGameSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Character
 * Subtype: Imperial
 * Title: TD-4445
 */
public class Card221_038 extends AbstractImperial {
    public Card221_038() {
        super(Side.DARK, 3, 2, 2, 2, 3, "TD-4445", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setArmor(4);
        setLore("Sandtrooper.");
        setGameText("Once per game, may reveal from hand to take a dewback into hand from outside your deck and deploy both simultaneously. Deploys -2 aboard a dewback. Your characters aboard dewbacks here may not be targeted by axes or lightsabers.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.SANDTROOPER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.TK_4445__DEPLOY_WITH_DEWBACK;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal to deploy a dewback");
            action.setActionMsg("Reveal to deploy simultaneously with a dewback from outside the game");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self));
            action.appendEffect(
                    new DeployCardFromOutsideOfGameSimultaneouslyWithCardEffect(action, self, playerId, Filters.Dewback));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToTargetModifier(self, -2, Filters.Dewback));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.aboard(Filters.Dewback), Filters.here(self)), Filters.or(Filters.lightsaber, Filters.ax)));
        return modifiers;
    }
}