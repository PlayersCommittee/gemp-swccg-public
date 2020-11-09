package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Dryden Vos
 */
public class Card213_004 extends AbstractAlien {
    public Card213_004() {
        super(Side.DARK, 1, 6, 4, 3, 6, "Dryden Vos", Uniqueness.UNIQUE);
        setLore("Crimson Dawn leader. Gangster.");
        setGameText("Deploys -2 if Maul on table. When deployed, may deploy a weapon on Vos from Reserve Deck; reshuffle. While armed and present at a site, opponent may not 'react' to or from here. Immune to attrition < 5 (< 3 if with Qi'ra).");
        addIcons(Icon.WARRIOR, Icon.PILOT, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.CRIMSON_DAWN, Keyword.LEADER, Keyword.GANGSTER);
        setArmor(5);
        addPersona(Persona.VOS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, new OnTableCondition(self, Filters.or(Filters.Maul)), -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(5, 3, new WithCondition(self, Filters.persona(Persona.QIRA)))));
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.sameSite(self), new AndCondition(new ArmedWithCondition(self, Filters.any), new PresentAtCondition(self, Filters.site)), game.getOpponent(self.getOwner())));
        modifiers.add(new MayNotReactFromLocationModifier(self, Filters.sameSite(self), new AndCondition(new ArmedWithCondition(self, Filters.any), new PresentAtCondition(self, Filters.site)), game.getOpponent(self.getOwner())));
        return modifiers;
    }


    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DRYDEN__DOWNLOAD_WEAPON;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy weapon from Reserve Deck");
            action.setActionMsg("Deploy a weapon on Vos from Reserve Deck");

            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.weapon, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }


}
