package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 10
 * Type: Starship
 * Subtype: Starfighter
 * Title: Kylo Ren's TIE Silencer
 */
public class Card210_035 extends AbstractStarfighter {
    public Card210_035() {
        super(Side.DARK, 2, 2, 3, null, 4, 3, 4, "Kylo Ren's TIE Silencer", Uniqueness.UNIQUE, ExpansionSet.SET_10, Rarity.V);
        setLore("");
        setGameText("May add 1 pilot. Kylo deploys -2 aboard. May reveal from hand to [upload] Kylo and deploy both simultaneously. While Kylo piloting, Kylo and this TIE are immune to It Can Wait, Rebel Barrier, and attrition < 5.");
        addIcons(Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_10, Icon.EPISODE_VII, Icon.FIRST_ORDER);
        addModelType(ModelType.TIE_VN);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Kylo);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.Kylo, -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.Kylo, -2, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new HasPilotingCondition(self, Filters.Kylo), Title.It_Can_Wait));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.Kylo, Filters.piloting(self)), new HasPilotingCondition(self, Filters.Kylo), Title.It_Can_Wait));
        modifiers.add(new ImmuneToTitleModifier(self, new HasPilotingCondition(self, Filters.Kylo), Title.Rebel_Barrier));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.Kylo, Filters.piloting(self)), new HasPilotingCondition(self, Filters.Kylo), Title.Rebel_Barrier));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Kylo), 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KYLO_RENS_TIE_SILENCER__UPLOAD_KYLO;

        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.KYLO)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal to take Kylo into hand");
            action.setActionMsg("Take Kylo into hand from Reserve Deck and deploy both simultaneously");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self));
            action.appendEffect(
                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, self, Filters.Kylo, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
