package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Subtype: Immediate
 * Title: All Too Easy
 */
public class Card5_112 extends AbstractImmediateEffect {
    public Card5_112() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.All_Too_Easy, Uniqueness.UNIQUE);
        setLore("'Perhaps you are not as strong as the Emperor thought.'");
        setGameText("If an opponent's character at the Carbonite Chamber was just 'hit' by a lightsaber, deploy on that character. Character is instead immediately captured and 'frozen.' If captive released, lose Immediate Effect. (Immune to Control.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.and(Filters.opponents(self), Filters.character, Filters.at(Filters.Carbonite_Chamber)), Filters.lightsaber)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameCardId(cardHit), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return getRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    private List<RequiredGameTextTriggerAction> getRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, Filters.and(self, Filters.attachedTo(Filters.character)))) {
            PhysicalCard character = self.getAttachedTo();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Capture and 'freeze' " + GameUtils.getFullName(character));
            action.setActionMsg("Capture and 'freeze' " + GameUtils.getCardLink(character));
            // Perform result(s)
            action.appendEffect(
                    new RestoreCardToNormalEffect(action, character));
            action.appendEffect(
                    new CaptureCharacterOnTableEffect(action, character, true));
            return Collections.singletonList(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.released(game, effectResult, Filters.hasAttached(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}