package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Creature
 * Title: Rancor
 */
public class Card6_139 extends AbstractCreature {
    public Card6_139() {
        super(Side.DARK, 1, 6, null, 5, 0, Title.Rancor);
        setLore("Indigenous to Dathomir, but found on several dozen worlds throughout the galaxy. Vicious predator. Sometimes kept as pets by eccentrics and crime lords.");
        setGameText("* Ferocity = 8 + destiny. Habitat: Rancor Pit and exterior planet sites. Deploys only to the rancor pit or a site where Malakili is the only character. Moves towards another Rancor whenever possible.");
        addModelType(ModelType.GIGANTIC_PREDATOR);
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Rancor_Pit, Filters.exterior_planet_site);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Rancor_Pit, Filters.and(Filters.sameSiteAs(self, Filters.Malakili),
                Filters.not(Filters.sameSiteAs(self, Filters.and(Filters.character, Filters.not(Filters.Malakili))))));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter towardAnotherRancor = Filters.toward(self, Filters.and(Filters.other(self), Filters.Rancor));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextFerocityModifier(self, 8, 1));
        modifiers.add(new MayNotMoveToLocationUsingLandspeedModifier(self, new OnTableCondition(self, towardAnotherRancor), Filters.not(towardAnotherRancor)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.MOVE, playerId)) {
            Filter towardAnotherRancor = Filters.toward(self, Filters.and(Filters.other(self), Filters.Rancor));
            Action moveUsingLandspeedAction = getMoveUsingLandspeedAction(playerId, game, self, false, 0, false, false, false, false, null, towardAnotherRancor);
            if (moveUsingLandspeedAction != null) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setPerformingPlayer(playerId);
                action.setText("Move towards another Rancor");
                action.setActionMsg("Move " + GameUtils.getCardLink(self) + " towards another Rancor");
                // Perform result(s)
                action.appendEffect(
                        new StackActionEffect(action, action));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
