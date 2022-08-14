package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Effect
 * Title: Another Pathetic Lifeform & Security Control
 */
public class Card217_028 extends AbstractNormalEffect {
    public Card217_028() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Another Pathetic Lifeform & Security Control", Uniqueness.UNIQUE);
        addComboCardTitles("Another Pathetic Lifeform", "Security Control");
        setGameText("Deploy on table. Your unique (•) Republic characters (and your unique (•) Gungans) of ability < 4 are forfeit +1. Nabrun Leids and Elis Helrot are limited to owner's move phase and exterior sites only. Once per turn, may [download] a docking bay. Once during opponent's turn, if Jar Jar or your [Episode I] leader occupies an [Episode I] battleground, may activate 1 Force. [Immune to Alter.]");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_17);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.unique, Filters.or(Filters.Gungan, Filters.Republic_character), Filters.abilityLessThan(4)), 1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.or(Filters.Nabrun_Leids, Filters.Elis_Helrot), ModifyGameTextType.NABRUN_LEIDS_ELIS_HELROT__LIMIT_USAGE));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.ANOTHER_PATHETIC_LIFEFORM_SECURITY_CONTROL__DOWNLOAD_DOCKING_BAY;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy docking bay from Reserve Deck");
            action.setActionMsg("Deploy a docking bay from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.docking_bay, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOnceDuringOpponentsTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canActivateForce(game, playerId)
                && GameConditions.occupiesWith(game, self, playerId, Filters.and(Icon.EPISODE_I, Filters.battleground), Filters.or(Filters.and(Filters.your(self), Icon.EPISODE_I, Filters.leader), Filters.Jar_Jar))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            actions.add(action);
        }

        return actions;
    }
}