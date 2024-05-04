package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
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
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Effect
 * Title: I Will Find Them Quickly, Master
 */
public class Card11_071 extends AbstractNormalEffect {
    public Card11_071() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.I_Will_Find_Them_Quickly_Master, Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.R);
        setLore("Utilizing his 'dark eye' probe droids and sensory equipment, Maul knew he would eventually find his target.");
        setGameText("Deploy on table. Opponent may deploy Amidala from Reserve Deck; reshuffle. If Maul present with Amidala and opponent has no Jedi there, she is captured. Once per game may take If The Trace Was Correct into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.I_WILL_FIND_THEM_QUICKLY_MASTER__DOWNLOAD_AMIDALA;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.AMIDALA)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Amidala from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Amidala, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            PhysicalCard amidala = Filters.findFirstActive(game, self, Filters.Amidala);
            if (amidala != null) {
                if (GameConditions.isPresentWith(game, self, amidala, Filters.Maul)
                    && !GameConditions.isWith(game, self, amidala, Filters.and(Filters.opponents(self), Filters.Jedi))) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setSingletonTrigger(true);
                    action.setText("Capture Amidala");
                    // Perform result(s)
                    action.appendEffect(
                            new CaptureCharacterOnTableEffect(action, amidala));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.I_WILL_FIND_THEM_QUICKLY_MASTER__UPLOAD_IF_THE_TRACE_WAS_CORRECT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take If The Trace Was Correct into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.If_The_Trace_Was_Correct, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}