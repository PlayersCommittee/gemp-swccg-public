package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Strike Planning (V)
 */
public class Card221_006 extends AbstractNormalEffect {
    public Card221_006() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Strike_Planning, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("'General Solo, is your strike team assembled?'");
        setGameText("If Rebel Strike Team on table, deploy on table. Unless opponent's [Endor] objective on table, opponent generates no Force at your Endor system. Twice per game, may [upload] an [Endor] or [Death Star II] leader. May [download] [Death Star II] Falcon to Endor system. [Immune to Alter.]");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.title(Title.Rebel_Strike_Team));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new GenerateNoForceModifier(self, Filters.and(Filters.your(self), Filters.Endor_system), new UnlessCondition(new OnTableCondition(self, Filters.and(Filters.owner(opponent), Icon.ENDOR, Filters.Objective))), opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.STRIKE_PLANNING_V__UPLOAD_ENDOR_OR_DS2_LEADER;

        // Check condition(s)
        if (GameConditions.isTwicePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take leader into hand from Reserve Deck");
            action.setActionMsg("Take an Endor or Death Star II leader into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new TwicePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.leader, Filters.or(Icon.ENDOR, Icon.DEATH_STAR_II)), true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.STRIKE_PLANNING_V__DOWNLOAD_FALCON;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.FALCON)){
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Falcon from Reserve Deck");
            action.setActionMsg("Deploy Falcon from Reserve Deck");

            // Perform result(s)
            action.appendEffect(
                new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Icon.DEATH_STAR_II, Filters.Falcon), Filters.Endor_system, true));
            actions.add(action);
        }

        return actions;
    }
}