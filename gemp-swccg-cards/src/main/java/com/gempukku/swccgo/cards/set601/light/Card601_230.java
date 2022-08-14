package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 6
 * Type: Character
 * Subtype: Republic
 * Title: Obi-Wan Kenobi, Padawan Learner (V)
 */
public class Card601_230 extends AbstractRepublic {
    public Card601_230() {
        super(Side.LIGHT, 1, 6, 6, 5, 8, "Obi-Wan Kenobi, Padawan Learner", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Qui-Gon Jinn's Padawan. Stayed behind to protect Queen Amidala when Qui-Gon left to explore Mos Espa, but was in constant communication should he be needed.");
        setGameText("Deploys -2 (and landspeed = 2) on Tatooine. If a card was just stacked on Credits Will Do Fine, may [upload] a non-character card. While at opponent's Watto's Junkyard, its Light Side game text is canceled. Immune to attrition < 4.");
        addPersona(Persona.OBIWAN);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.WARRIOR, Icon.LEGACY_BLOCK_6);
        addKeywords(Keyword.PADAWAN);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_on_Tatooine));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter opponentWattosJunkyard = Filters.and(Filters.opponents(self), Filters.Wattos_Junkyard);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, new OnCondition(self, Title.Tatooine), 2));
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, opponentWattosJunkyard, new AtCondition(self, opponentWattosJunkyard), game.getLightPlayer()));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OBIWAN_KENOBI_PADAWAN_LEARNER__UPLOAD_NON_CHARACTER_CARD;

        // Check condition(s)
        if (TriggerConditions.justStackedCardOn(game, effectResult, Filters.any, Filters.Credits_Will_Do_Fine)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a non-character card into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.not(Filters.character), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
