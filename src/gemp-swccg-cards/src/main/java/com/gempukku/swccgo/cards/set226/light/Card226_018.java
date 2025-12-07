package com.gempukku.swccgo.cards.set226.light;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractJediMasterRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

/**
 * Set: Set 26
 * Type: Character
 * Subtype: Jedi Master/Republic
 * Title: Kelleran Beq
 */
public class Card226_018 extends AbstractJediMasterRepublic {
    public Card226_018() {
        super(Side.LIGHT, 1, 8, 6, 7, 8, "Kelleran Beq", Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("Jedi survivor.");
        setGameText("[Pilot] 2. Adds one battle destiny with Grogu or a Padawan. Any lightsaber may deploy on Beq. Once per game, may deploy a lightsaber on Beq from Lost Pile. Your other characters here are defense value +1 for each lightsaber on Beq. Immune to attrition < 6.");
        addKeyword(Keyword.JEDI_SURVIVOR);
        addPersona(Persona.BEQ);
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.VIRTUAL_SET_26);
        addIcon(Icon.WARRIOR, 2);
    }
    
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayDeployToTargetModifier(self, Filters.lightsaber, self));
        modifiers.add(new MayUseWeaponModifier(self, Filters.lightsaber));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.character, Filters.here(self)),
                new OnTableEvaluator(self, Filters.and(Filters.lightsaber, Filters.attachedTo(self)))));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.or(Filters.Grogu, Filters.padawan)), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.KELLERAN_BEQ__DEPLOY_LIGHTSABER;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy lightsaber from Lost Pile");
            action.setActionMsg("Deploy a lightsaber on Beq from Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromLostPileEffect(action, Filters.lightsaber, Filters.Beq, false));
            actions.add(action);
        }
        return actions;
    }
}
