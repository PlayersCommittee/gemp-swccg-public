package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 25
 * Type: Character
 * Subtype: Rebel
 * Title: Captain Han Solo (V)
 */
public class Card225_038 extends AbstractRebel {
    public Card225_038() {
        super(Side.LIGHT, 1, 4, 4, 3, 7, "Captain Han Solo", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Smuggler and scoundrel. His piloting abilities have become legend in the Rebellion. Intends to leave the Alliance to pay off Jabba the Hutt.");
        setGameText("[Pilot] 3. Draws one battle destiny if unable to otherwise (two if with Chewie or Leia). Vader's gametext may not target Han. May [download] Han's Heavy Blaster Pistol (or lose 1 Force to deploy from Lost Pile) on Han. If just lost or captured, may [upload] [Cloud City] Lando.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_25);
        addKeywords(Keyword.SMUGGLER, Keyword.CAPTAIN);
        addPersona(Persona.HAN);
        setSpecies(Species.CORELLIAN);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition inBattleWithLeiaOrChewie = new InBattleWithCondition(self, Filters.or(Filters.Leia, Filters.Chewie));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new ConditionEvaluator(1, 2, inBattleWithLeiaOrChewie)));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.Vader));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.CAPTAIN_HAN_SOLO_V__DEPLOY_BLASTER;
        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Hans_Heavy_Blaster_Pistol)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Han's Heavy Blaster Pistol from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Hans_Heavy_Blaster_Pistol, Filters.sameCardId(self), true));
            actions.add(action);
        }
        if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Title.Hans_Heavy_Blaster_Pistol)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Han's Heavy Blaster Pistol from Lost Pile");

            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromLostPileEffect(action, Filters.Hans_Heavy_Blaster_Pistol, Filters.sameCardId(self), true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenInactiveInPlay(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CAPTAIN_HAN_SOLO_V__UPLOAD_LANDO;
        // Check condition(s)
        if (GameConditions.isOnlyCaptured(game, self)
                && TriggerConditions.captured(game, effectResult, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            Filter ccLando = Filters.and(Filters.icon(Icon.CLOUD_CITY), Filters.Lando);
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take [Cloud City] Lando into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, ccLando, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CAPTAIN_HAN_SOLO_V__UPLOAD_LANDO;

        // Check condition(s)
        if ((TriggerConditions.justLost(game, effectResult, self) || TriggerConditions.captured(game, effectResult, self))
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            Filter ccLando = Filters.and(Filters.icon(Icon.CLOUD_CITY), Filters.Lando);
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take [Cloud City] Lando into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, ccLando, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
