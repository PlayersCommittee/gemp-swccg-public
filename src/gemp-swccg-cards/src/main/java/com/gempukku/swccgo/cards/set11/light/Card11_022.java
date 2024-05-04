package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceHandInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Effect
 * Title: The Shield Is Down!
 */
public class Card11_022 extends AbstractNormalEffect {
    public Card11_022() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Shield Is Down!", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.R);
        setLore("Lando's confidence in the Rebel strike team on the forest moon was rewarded, and proved to be a decisive factor at the Battle of Endor.");
        setGameText("Deploy on table. If Rebel Strike Team on table, your Force generation is +1 at Endor sites and you may deploy Back Door from Reserve Deck; reshuffle. When Bunker 'blown away,' retrieve 8 Force and opponent must place their hand in Used Pile. (Immune to Alter.)");
        addIcons(Icon.TATOOINE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.Endor_site, new OnTableCondition(self, Filters.Rebel_Strike_Team), 1, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_SHIELD_IS_DOWN__DOWNLOAD_BACK_DOOR;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Back_Door)
                && GameConditions.canSpot(game, self, Filters.Rebel_Strike_Team)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Back Door from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Back_Door, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.title(Title.Bunker, true))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 8 Force");
            action.setActionMsg("Make " + playerId + " retrieve 8 Force and make " + opponent + " place hand in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 8));
            action.appendEffect(
                    new PlaceHandInUsedPileEffect(action, opponent));
            return Collections.singletonList(action);
        }
        return null;
    }
}