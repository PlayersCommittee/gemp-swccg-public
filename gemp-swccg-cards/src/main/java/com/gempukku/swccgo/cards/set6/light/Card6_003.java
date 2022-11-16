package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Droid
 * Title: Artoo
 */
public class Card6_003 extends AbstractDroid {
    public Card6_003() {
        super(Side.LIGHT, 1, 4, 1, 5, "Artoo", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setAlternateDestiny(6);
        setLore("Counterpart to C-3PO. Spy. Obstinate, headstrong and always full of surprises. R2-D2 was an integral part of Luke Skywalker's rescue plans.");
        setGameText("During each of your control phases, may take one Hero Of A Thousand Devices or A Gift into hand from Reserve Deck; reshuffle. If at a battleground site with C-3P0, may subtract 1 from each opponent's battle destiny at same and related sites.");
        addPersona(Persona.R2D2);
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ARTOO__UPLOAD_HERO_OF_A_THOUSAND_DEVICES_OR_A_GIFT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Hero Of A Thousand Devices or A Gift into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Hero_Of_A_Thousand_Devices, Filters.A_Gift), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && GameConditions.isAtLocation(game, self, Filters.battleground_site)
                && GameConditions.isWith(game, self, Filters.C3PO)
                && GameConditions.isDuringBattleAt(game, Filters.sameOrRelatedSite(self))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Subtract 1 from battle destiny");
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
