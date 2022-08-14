package com.gempukku.swccgo.cards.set214.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Effect
 * Title: The Dead Speak!
 */
public class Card214_011 extends AbstractNormalEffect {
    public Card214_011() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Dead Speak!", Uniqueness.UNIQUE);
        setGameText("If I Will Finish What You Started on table, deploy on table. While [Episode VII] Emperor on Exegol, attrition against opponent is +2 at same location as Kylo, Rey, or Snoke and, once per game, may [download] an [Episode VII] battleground system. (Immune to Alter.)");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_14);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.I_Will_Finish_What_You_Started);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AttritionModifier(self, Filters.and(Filters.sameLocationAs(self, Filters.or(Filters.Rey, Filters.Kylo, Filters.Snoke))), new OnCondition(self, Filters.and(Filters.icon(Icon.EPISODE_VII), Filters.Emperor), Title.Exegol), 2, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_DEAD_SPEAK__DOWNLOAD_EP7_BATTLEGROUND_SYSTEM;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Emperor, Filters.on(Title.Exegol)))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a system from Reserve Deck");
            action.setActionMsg("Deploy an [Episode VII] battleground system from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.icon(Icon.EPISODE_VII), Filters.battleground_system), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}