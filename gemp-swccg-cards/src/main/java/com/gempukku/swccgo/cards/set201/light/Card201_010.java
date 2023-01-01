package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.choose.TakeCardAndOrCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Effect
 * Title: We'll Take The Long Way
 */
public class Card201_010 extends AbstractNormalEffect {
    public Card201_010() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "We'll Take The Long Way", Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setLore("Actions borne of the love for one's planet can heavily outweigh those generated from simple battle orders.");
        setGameText("If We Have A Plan on table, deploy on table. Imperial deploy +1 to Theed Palace Throne Room and, while you occupy that site, you may ignore your Objective's location deployment restrictions. Once per game, may [upload] an [Episode I] system and/or a [Theed Palace] leader. [Immune to Alter]");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_1);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.We_Have_A_Plan);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Imperial, 1, Filters.Theed_Palace_Throne_Room));
        modifiers.add(new IgnoresLocationDeploymentRestrictionsFromCardModifier(self, Filters.and(Filters.your(self), Filters.Objective),
                new OccupiesCondition(playerId, Filters.Theed_Palace_Throne_Room), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.WELL_TAKE_THE_LONG_WAY__UPLOAD_SYSTEM_AND_OR_LEADER;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take cards into hand from Reserve Deck");
            action.setActionMsg("Take an [Episode I] system and/or a [Theed Palace] leader into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardAndOrCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.system, Icon.EPISODE_I), Filters.and(Filters.leader, Icon.THEED_PALACE), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}