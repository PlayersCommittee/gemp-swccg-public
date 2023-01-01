package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetTotalBattleDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Character
 * Subtype: Rebel
 * Title: Master Luke (V)
 */
public class Card218_025 extends AbstractRebel {
    public Card218_025() {
        super(Side.LIGHT, 1, 5, 5, 5, 8, "Master Luke", Uniqueness.UNIQUE, ExpansionSet.SET_18, Rarity.V);
        setVirtualSuffix(true);
        setLore("Until being reunited with Yoda, Luke suspected that he had completed his training. Has a strong influence on the weak minded.");
        setGameText("While at a site, unless opponent's character of ability > 3 here, reset opponent's total battle destiny here to 0. Once per game, may deploy a lightsaber on Luke from Lost Pile. Opponent's aliens deploy +1 to same and related Tatooine sites. Immune to attrition < 4.");
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_18);
        addPersona(Persona.LUKE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ResetTotalBattleDestinyModifier(self, Filters.sameSite(self), new AndCondition(new InBattleCondition(self),
                new UnlessCondition(new HereCondition(self, Filters.and(Filters.opponents(self), Filters.character,
                        Filters.abilityMoreThan(3))))), 0, opponent));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.alien), 1, Filters.and(Filters.Tatooine_site, Filters.sameOrRelatedSite(self))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MASTER_LUKE__DEPLOY_LIGHTSABER_FROM_LOST_PILE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy lightsaber from Lost Pile");
            action.setActionMsg("Deploy a lightsaber on Luke from Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToTargetFromLostPileEffect(action, Filters.lightsaber, Filters.Luke, false));

            return Collections.singletonList(action);
        }
        return null;
    }
}
