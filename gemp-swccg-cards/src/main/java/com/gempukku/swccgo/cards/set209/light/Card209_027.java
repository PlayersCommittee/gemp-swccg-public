package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.TrueCondition;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Location
 * Subtype: Site
 * Title: Scarif: Turbolift Complex
 */

public class Card209_027 extends AbstractSite {
    public Card209_027() {
        super(Side.LIGHT, Title.Scarif_Turbolift_Complex, Title.Scarif);
        setLocationDarkSideGameText("During your move phase, may move free between here and any related site.");
        setLocationLightSideGameText("If you initiate a Force drain here, may rotate this site. Immune to Expand The Empire.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_9);
    }


    // God I hope this works - Jim
    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId)
    {
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult,Filters.canBeTargetedBy(self))) {
            List<Modifier> modifiers = new LinkedList<>();
            // rotate location modifier came from revolution
            modifiers.add(new RotateLocationModifier(self, Filters.Scarif_Turbolift_Complex, new TrueCondition()));
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        Filter relatedSite = Filters.relatedSite(self);
        Filter cardsAtSelf = Filters.at(self);

        if (GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.MOVE)
                && GameConditions.canSpotLocation(game, relatedSite)) {

            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.at(self), self, Filters.relatedSite(self), true))
            {
                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, cardsAtSelf, self, relatedSite, true);
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Expand_The_Empire));
        return modifiers;
    }
}