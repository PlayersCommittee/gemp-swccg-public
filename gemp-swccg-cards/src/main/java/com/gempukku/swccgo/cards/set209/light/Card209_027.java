package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.TrueCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.modifiers.RotateLocationModifier;
import com.gempukku.swccgo.logic.timing.results.PutUndercoverResult;

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
        addKeyword(Keyword.SCARIF_LOCATION);
    }


    // COMMENTS - JIM: The initial rotation by DS after a drain works.
    //              - There's some weird quirk in that it asks you to choose which site to rotate, and this turbolift complex appears twice.
    //              - But the ultimate behavior is correct.  The movement text is then usable by the DS, the next chance to rotate this site
    //                after a drain goes to LS.
    //
    //              - But then future rotations by the LS player after a drain does not work.
    //              - The player gets the option of applying the rotation effect, but nothing happens...
    //              - The site does not rotate back.
    //              - Multiple copies of this "RotateLocation" effect appears if you shift-click to examine the card.

    //              Previous implementation attempt was to just apply new RotateLocationModifiers on top of each other.  That didn't work.
    //              This current attempt was to add the rotation modifier, and then try to suspend it when LS then drains. Also not working.
    //              I don't know if it makes sense to change the "new TrueCondition()" part of that RotateLocationModifier to something else, or what that something else might be.

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId)
    {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();



        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, self)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);

            if (self.isRotatedByTurboliftComplex()==true) {
                action.setText("Un-rotate this site.");
                self.setRotatedByTurboliftComplex(false);
                action.appendEffect(new AddUntilEndOfGameModifierEffect(action, new SuspendModifierEffectsModifier(self, Filters.Scarif_Turbolift_Complex, Filters.Scarif_Turbolift_Complex), "AddUntilEndOfGameModifierEffect"));
            }
            else {
                action.setText("Rotate this site.");
                action.appendEffect(new AddUntilEndOfGameModifierEffect(action, new RotateLocationModifier(self, self, new TrueCondition()), "AddUntilEndOfGameModifierEffect"));
                self.setRotatedByTurboliftComplex(true);
                actions.add(action);
            }
            return actions;
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        Filter relatedSite = Filters.relatedSite(self);
        Filter otherScarifSite = Filters.and(Filters.other(self), Filters.Scarif_site);

        if (GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.MOVE)
                && GameConditions.canSpotLocation(game, relatedSite)) {
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.any, self, relatedSite, true)) {
                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, Filters.any, self, otherScarifSite, true);
                actions.add(action);
            }
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.any, relatedSite, self, true)) {
                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, Filters.any, otherScarifSite, self, true);
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