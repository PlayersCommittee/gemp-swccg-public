package com.gempukku.swccgo.cards.set110.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EatenByIsPlacedOutOfPlayModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.EatenResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Objective
 * Title: Court Of The Vile Gangster / I Shall Enjoy Watching You Die
 */
public class Card110_006_BACK extends AbstractObjective {
    public Card110_006_BACK() {
        super(Side.DARK, 7, Title.I_Shall_Enjoy_Watching_You_Die, ExpansionSet.ENHANCED_JABBAS_PALACE, Rarity.PM);
        setGameText("While this side up, once during each of your deploy phases, may deploy Sarlacc, Rancor or Rancor Pit from Reserve Deck; reshuffle. Captives targeted by trap door are immediately relocated to Rancor Pit (do not draw destiny) and Trap Door may not be canceled. Opponent loses Force equal to forfeit value of each opponent's character eaten by a Rancor or Sarlacc (place that character out of play). Flip this card if you have no captives at Tatooine sites and opponent has no character at same site as Rancor.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.I_SHALL_ENJOY_WATCHING_YOU_DIE__DOWNLOAD_SARLACC_RANCOR_OR_RANCOR_PIT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Sarlacc, Rancor, or Rancor Pit from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Sarlacc, Filters.Rancor, Filters.Rancor_Pit), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Trap_Door, ModifyGameTextType.TRAP_DOOR__DO_NOT_DRAW_DESTINY));
        modifiers.add(new MayNotBeCanceledModifier(self, Filters.Trap_Door));
        modifiers.add(new EatenByIsPlacedOutOfPlayModifier(self, Filters.or(Filters.Rancor, Filters.Sarlacc), Filters.and(Filters.opponents(self), Filters.character)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (TriggerConditions.justEatenBy(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.or(Filters.Sarlacc, Filters.Rancor))) {
            EatenResult eatenResult = (EatenResult) effectResult;

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make " + opponent + " lose " + GuiUtils.formatAsString(eatenResult.getForfeitValue()) + " Force");
            action.setActionMsg("Make " + opponent + " lose " + GuiUtils.formatAsString(eatenResult.getForfeitValue()) + " Force and place " + GameUtils.getCardLink(eatenResult.getCardEaten()) + " out of play");
            action.appendEffect(
                    new LoseForceEffect(action, opponent, eatenResult.getForfeitValue()));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_4;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.and(Filters.captive, Filters.at(Filters.Tatooine_site)))
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.character, Filters.at(Filters.sameSiteAs(self, Filters.Rancor))))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}