package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
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
        super(Side.DARK, 1, 4, 4, 3, 6, "Dryden Vos", Uniqueness.UNIQUE, ExpansionSet.SET_13, Rarity.V);
        setLore("Crimson Dawn leader. Gangster.");
        setGameText("When deployed, may [download] a weapon on Vos. While armed and present at a site, draws one battle destiny if unable to otherwise and opponent may not 'react' to or from here. Immune to attrition < 5 (< 3 if with Qi'ra).");
        addIcons(Icon.WARRIOR, Icon.PILOT, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.CRIMSON_DAWN, Keyword.LEADER, Keyword.GANGSTER);
        setArmor(5);
        addPersona(Persona.VOS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition armedAndPresentAtSiteCondition = new AndCondition(new ArmedWithCondition(self, Filters.any), new PresentAtCondition(self, Filters.site));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(5, 3, new WithCondition(self, Filters.persona(Persona.QIRA)))));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, armedAndPresentAtSiteCondition, 1));
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.sameSite(self), armedAndPresentAtSiteCondition, game.getOpponent(self.getOwner())));
        modifiers.add(new MayNotReactFromLocationModifier(self, Filters.sameSite(self), armedAndPresentAtSiteCondition, game.getOpponent(self.getOwner())));
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
