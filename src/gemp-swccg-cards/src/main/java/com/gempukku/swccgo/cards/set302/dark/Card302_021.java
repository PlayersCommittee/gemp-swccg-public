package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringAttackWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerAttackEffect;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfAttackModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NumDestinyDrawsDuringAttackModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Imperial
 * Title: Darth Nehalem
 */
public class Card302_021 extends AbstractDarkJediMasterImperial {
    public Card302_021() {
        super(Side.DARK, 6, 6, 6, 7, 9, "Darth Nehalem", Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("Currently the Grand Master and leader of the Brotherhood. Darth Nehalem was once Evant Taelyan before becoming a Sith Lord. He is the first Grand Master from Clan Scholae Palatinae.");
        setGameText("Adds +3 to anything he pilots. When attacking or being attacked by a creature, power +3 and may add one destiny. Once per turn, you may deploy a creature here; reshuffle. Immune to attrition.");
        addPersona(Persona.EVANT);
		addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.DARK_COUNCILOR, Keyword.GRAND_MASTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionModifier(self));
		modifiers.add(new PowerModifier(self, new DuringAttackWithParticipantCondition(self), 3));
        return modifiers;
    }
	
@Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
    List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isDuringAttackWithParticipant(game, self)
                && GameConditions.isOncePerAttack(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one destiny");
            action.appendUsage(
                    new OncePerAttackEffect(action));
            action.appendEffect(
                    new AddUntilEndOfAttackModifierEffect(action, new NumDestinyDrawsDuringAttackModifier(self, 1, playerId), null));

            actions.add(action);
        }

		gameTextActionId = GameTextActionId.DARTH_NEHALEM__SUMMON_CREATURE;
		
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isAtLocation(game, self, Filters.site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a creature from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.creature, Filters.sameSite(self), true));
            actions.add(action);
        }

    return actions;
    }
}
