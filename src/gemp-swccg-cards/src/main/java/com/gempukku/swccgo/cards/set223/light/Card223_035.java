package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Character
 * Subtype: Rebel
 * Title: Corran Horn, Jedi
 */

public class Card223_035 extends AbstractRebel {
    public Card223_035() {
        super(Side.LIGHT, 1, 5, 4, 6, 6, "Corran Horn, Jedi", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Corellian.  Former counter-intelligence agent for CorSec (Corellian Security). Gifted tactician. One of Wedge Antilles' best pilots. Member of Rogue Squadron.");
        setGameText("Adds 1 to maneuver of anything he pilots. Once per game, during battle, may deploy a blaster on Corran from hand (or Reserve Deck; reshuffle). During battle, may subtract 1 from a just drawn blaster weapon destiny; Corran is power +2. Immune to attrition < 5.");
        addPersona(Persona.CORRAN_HORN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_23);
        setSpecies(Species.CORELLIAN);
        addKeyword(Keyword.ROGUE_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId2 = GameTextActionId.CORRAN_HORN_JEDI__DOWNLOAD_BLASTER;

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId2)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId2, true, false)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId2);
            action.setText("Deploy blaster from Reserve Deck");
            // Allow response(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.blaster, false, false, true));
            actions.add(action);
        }

        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId2)
                && GameConditions.hasInHand(game, playerId, Filters.blaster)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId2);
            action.setText("Deploy blaster from hand");
            // Allow response(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromHandEffect(action, playerId, Filters.blaster, 0));
            actions.add(action); 
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult, Filters.blaster)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            // Subtract 1
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Subtract 1 from destiny draw");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            action.appendEffect(
                    new ModifyPowerUntilEndOfBattleEffect(action, self, 2));
            actions.add(action);
        }

        return actions;
    }
}
