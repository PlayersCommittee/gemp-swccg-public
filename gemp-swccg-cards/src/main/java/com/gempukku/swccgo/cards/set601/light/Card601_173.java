package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Character
 * Subtype: Republic
 * Title: Maris Brood, Fallen Jedi
 */

public class Card601_173 extends AbstractRepublic {
    public Card601_173() {
        super(Side.LIGHT, 1, 6, 6, 6, 7, "Maris Brood, Fallen Jedi", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Matching weapon is Elegant Lightsaber and, if Sai'torr Kal Fas on table, may lose 2 Force to deploy it on her from Reserve Deck; reshuffle.  While with Galen or a [Sidious] Effect, adds one battle destiny.  Immune to [Permanent Weapon] weapons and attrition < 5.");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_4);
        addIcon(Icon.WARRIOR, 2);
        addKeywords(Keyword.FEMALE);
        setMatchingWeaponFilter(Filters.title("Elegant Lightsaber"));
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.or(Filters.Galen, Filters.and(Icon.SIDIOUS, Filters.Effect))), 1));
        modifiers.add(new MayNotBeTargetedByPermanentWeaponsModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    //new action: move
    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MARIS_BROOD_FALLEN_JEDI__DEPLOY_ELEGANT_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Saitorr_Kal_Fas)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Elegant Lightsaber on her");

            action.appendCost(
                    new LoseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.title("Elegant Lightsaber"), Filters.and(self), true));
            actions.add(action);
        }

        return actions;
    }
}