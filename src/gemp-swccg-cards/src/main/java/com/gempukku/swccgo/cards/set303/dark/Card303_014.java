package com.gempukku.swccgo.cards.set303.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PlaceInUsedPileWhenCanceledModifier;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Shadow Academy
 * Type: Effect
 * Title: Impatient Apprentice
 */
public class Card303_014 extends AbstractNormalEffect {
    public Card303_014() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Impatient Apprentice", Uniqueness.UNIQUE, ExpansionSet.SA, Rarity.U);
        setLore("By nature of the Rule of Two, an Apprentice will attempt to overthrow their Master. All to often the Apprentice rushes to claim power and learns a bitter lesson.");
        setGameText("Deploy on your side of table. Your training destiny draws are each +1. Your Immediate Effects may deploy for free. During your deploy phase, you may deploy one Shadow Academy site directly from Reserve Deck. Shuffle, cut and replace. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachTrainingDestinyModifier(self, Filters.your(self), 1));
        modifiers.add(new DeploysFreeModifier(self, Filters.and(Filters.your(self), Filters.Immediate_Effect)));
        return modifiers;
    }
	
	@Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SHADOW_ACADEMY__DOWNLOAD_SHADOW_ACADEMY_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Shadow Academy site from Reserve Deck");
            action.setActionMsg("Deploy a Shadow Academy site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Shadow_Academy_location, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}