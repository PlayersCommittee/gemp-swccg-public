package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilStartOfPlayersNextTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotParticipateInBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 23
 * Type: Character
 * Subtype: Rebel
 * Title: Boushh (V)
 */
public class Card223_032 extends AbstractRebel {
    public Card223_032() {
        super(Side.LIGHT, 1, 4, 4, 4, 6, Title.Boushh, Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Leia obtained the armor of a notorious mercenary to sneak onto Coruscant. She later assumed the same role to spy on Jabba. Fearless and inventive. Jabba's kind of scum.");
        setGameText("If deployed to same site as frozen Han, Leia may not battle until your next turn. Your characters here are immune to Imperial Barrier. During battle with Chewie, may add one destiny to attrition. Once per game, may [upload] Someone Who Loves You.");
        setArmor(5);
        addIcons(Icon.PREMIUM, Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_23, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SPY, Keyword.FEMALE);
        addPersona(Persona.LEIA);
        setSpecies(Species.ALDERAANIAN);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.here(self), Filters.character), Title.Imperial_Barrier));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        if (TriggerConditions.justDeployedToLocation(game, effectResult, self, Filters.sameSiteAs(self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.frozenCaptive, Filters.Han)))) {
            String playerId = self.getOwner();

			final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
			action.setText("Leia may not battle");
            action.setActionMsg("Prevent Leia from battling until the start of " + playerId + "'s next turn.");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilStartOfPlayersNextTurnModifierEffect(action, playerId,
                            new MayNotParticipateInBattleModifier(self, Filters.Leia), null));
            actions.add(action);
        }
        return actions;
    }
	
    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.BOUSHH__UPLOAD_SOMEONE_WHO_LOVES_YOU;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Someone Who Loves You into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Someone_Who_Loves_You, true));
            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattleWith(game, self, Filters.Chewie)
                && GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId2);
            action.setText("Add one destiny to total attrition");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddDestinyToAttritionEffect(action, 1));
            actions.add(action);
        }

        return actions;
    }
}
