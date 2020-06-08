package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.EatenResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Location
 * Subtype: Site
 * Title: Jabba's Palace: Rancor Pit
 */
public class Card6_166 extends AbstractSite {
    public Card6_166() {
        super(Side.DARK, Title.Rancor_Pit, Title.Tatooine);
        setLocationDarkSideGameText("Dark Waters may deploy here. If a creature present, cards cannot move from here.");
        setLocationLightSideGameText("If your character here is 'eaten,' opponent retrieves Force equal to character's forfeit.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.JABBAS_PALACE, Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.JABBAS_PALACE_SITE, Keyword.PIT);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.Dark_Waters, self));
        modifiers.add(new MayNotMoveFromLocationModifier(self, Filters.any, new PresentCondition(self, Filters.creature), self));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(final String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerOnLightSideOfLocation);

        // Check condition(s)
        if (TriggerConditions.justEatenAt(game, effectResult, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.character), self)) {
            int forceToRetrieve = (int) Math.floor(((EatenResult) effectResult).getForfeitValue());
            if (forceToRetrieve > 0) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Retrieve " + forceToRetrieve + " Force");
                // Perform result(s)
                action.appendEffect(
                        new RetrieveForceEffect(action, opponent, forceToRetrieve));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}