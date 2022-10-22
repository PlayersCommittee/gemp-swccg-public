package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.TrueCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.RotateLocationModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
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
        setLocationLightSideGameText("During your move phase, may move free between here and any related site.");
        setLocationDarkSideGameText("If you initiate a Force drain here, may rotate this site. Immune to Expand The Empire.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_9);
    }

    // Always on modifier, immune to Expand The Empire
    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Expand_The_Empire));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId)
    {
        GameTextActionId gameTextActionId = GameTextActionId.TURBOLIFT_COMPLEX__ROTATE_LOCATION;

        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerOnDarkSideOfLocation, Filters.Scarif_Turbolift_Complex)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);

            action.setText("Rotate this site");
            action.appendUsage(new OncePerPhaseEffect(action));
            action.appendEffect(new AddUntilEndOfGameModifierEffect(action, new RotateLocationModifier(self, self, new TrueCondition()), "Rotate location"));

            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        Filter relatedSite = Filters.relatedSite(self);

        if (GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.MOVE)
                && GameConditions.canSpotLocation(game, relatedSite)) {
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.any, self, relatedSite, true)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, Filters.any, self, relatedSite, true);
                action.setText("Move from here to related site");
                actions.add(action);
            }
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.any, relatedSite, self, true)) {

                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, Filters.any, relatedSite, self, true);
                action.setText("Move from related site to here");
                actions.add(action);
            }
        }
        return actions;
    }

}