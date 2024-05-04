package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataNotSetCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 8
 * Type: Character
 * Subtype: Alien
 * Title: Boba Fett, Prepared Hunter
 */
public class Card601_002 extends AbstractAlien {
    public Card601_002() {
        super(Side.DARK, 2, 4, 4, 3, 7, "Boba Fett, Prepared Hunter", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setArmor(5);
        setLore("Boba Fett's Mandalorian armor was so versatile that his opponents never knew what to expect. Bounty hunter. Assassin.");
        setGameText("Adds 3 to power of anything he pilots. May be revealed from hand to take Slave I from Reserve Deck; reshuffle;  and deploy both simultaneously. While piloting Slave 1, it is maneuver +3 and hyperspeed + 1. Draws one battle destiny if unable to otherwise (if present at a site may add a destiny to total power instead).");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_8, Icon.JABBAS_PALACE);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.ASSASSIN);
        addPersona(Persona.BOBA_FETT);
        setMatchingStarshipFilter(Filters.Slave_I);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition condition = new InPlayDataNotSetCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, condition, 1));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), new PilotingCondition(self, Filters.Slave_I), 3));
        modifiers.add(new HyperspeedModifier(self, Filters.hasPiloting(self), new PilotingCondition(self, Filters.Slave_I), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BOBA_FETT__UPLOAD_AND_DEPLOY_SLAVE_I;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.SLAVE_I)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal to deploy with Slave I");
            action.setActionMsg("Reveal to deploy with Slave I");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self));
            action.appendEffect(
                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, self, Filters.Slave_I, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId2)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isPresentAt(game, self, Filters.site)
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId2);
            action.setText("Add destiny to total power");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new SetWhileInPlayDataEffect(action, self, new WhileInPlayData()));
            action.appendEffect(
                    new AddDestinyToTotalPowerEffect(action, 1, playerId));
            Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (self.getWhileInPlayData() != null && !GameConditions.isDuringBattle(game)) {
            self.setWhileInPlayData(null);
        }
        return null;
    }

}