package com.gempukku.swccgo.cards.set218.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Effect
 * Title: Crush The Rebellion (V)
 */
public class Card218_009 extends AbstractNormalEffect {
    public Card218_009() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Crush The Rebellion", Uniqueness.UNIQUE, ExpansionSet.SET_18, Rarity.V);
        setVirtualSuffix(true);
        setLore("After dueling his son and seizing control of a city in the clouds, Vader resumed his quest to destroy the Alliance.");
        setGameText("If Shield Gate on table, deploy on table. Once per game, may [upload] Devastator. May [download] Comm Chief or Praji to Scarif system. Vader is power +2 and his game text may not be canceled. If opponent just lost a battle, they lose 1 Force. Death Star moves for free. [Immune to Alter.]");
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_18);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.Shield_Gate);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MovesForFreeModifier(self, Filters.Death_Star_system));
        modifiers.add(new PowerModifier(self, Filters.Vader, 2));
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, Filters.Vader));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.CRUSH_THE_REBELLION_V__UPLOAD_DEVASTATOR;
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Devastator into hand");
            action.setActionMsg("Take Devastator into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Devastator, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.CRUSH_THE_REBELLION_V__DOWNLOAD_CARD;
        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.Scarif_system)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.PRAJI, Title.Comm_Chief)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Comm Chief or Praji");
            action.setActionMsg("Deploy Comm Chief or Praji to Scarif system from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.Comm_Chief, Filters.Praji), Filters.Scarif_system, true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.lostBattle(game, effectResult, opponent)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
