package com.gempukku.swccgo.cards.set226.dark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractAlienImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddBattleDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Set: Set 26
 * Type: Character
 * Subtype: Alien/Imperial
 * Title: Mara Jade, The Emperor's Hand (V)
 */
public class Card226_008 extends AbstractAlienImperial {
    public Card226_008() {
        super(Side.DARK, 1, 5, 4, 5, 7, Title.Mara_Jade_The_Emperors_Hand, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("Spy. Ordered to kill Luke Skywalker. Assumed the identity of a dancer named 'Arica' in order to sneak into Jabba's palace.");
        setGameText("Functions as your apprentice. Battle destiny draws may not be added here. May [download] Mara Jade's Lightsaber here. If you just won a battle here, may retrieve a card with 'Mara' in game text. Immune to Rebel Barrier and attrition < 4 (< 5 if Luke on table).");
        addPersona(Persona.MARA_JADE);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_26);
        addImmuneToCardTitle(Title.Rebel_Barrier);
        addKeywords(Keyword.SPY, Keyword.FEMALE, Keyword.SITH_APPRENTICE);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        
        Condition lukeOnTable = new OnTableCondition(self, Filters.Luke);
        Condition selfInBattle = new InBattleCondition(self);

        modifiers.add(new MayNotAddBattleDestinyDrawsModifier(self, Filters.any, selfInBattle));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(4, 5, lukeOnTable)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MARA_JADE_THE_EMPERORS_HAND_V__DOWNLOAD_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.MARA_JADES_LIGHTSABER)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Mara Jade's Lightsaber");
            action.setActionMsg("Deploy Mara Jade's Lightsaber to Mara's location from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.persona(Persona.MARA_JADES_LIGHTSABER), Filters.atSameLocation(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.wonBattleAt(game, effectResult, playerId, Filters.here(self))) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve card with 'Mara' in game text");
            action.setActionMsg("Retrieve a card with 'Mara' in game text");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.gameTextContains("Mara")));
            return Collections.singletonList(action);
        }
        return null;
    }
}
