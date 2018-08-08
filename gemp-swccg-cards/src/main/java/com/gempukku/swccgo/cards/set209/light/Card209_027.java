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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.TrueCondition;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.RotateCardEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.modifiers.RotateLocationModifier;

import javax.swing.text.html.Option;
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
        addKeyword(Keyword.SCARIF_LOCATION);
    }


    // God I hope this works - Jim
    @Override
    //protected List<OptionalGameTextTriggerAction> getGameTextLigthSideOptionalAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId)
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId)
    {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, self)) {

            //final OptionalGameTextTriggerAction action1 = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            final OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            //final OptionalGameTextTriggerAction action3 = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);

            /*
            action1.setText("Rotate this site. (RotateCardEffect)");
            action1.appendEffect(new RotateCardEffect(action1, self, true));
            actions.add(action1);
*/

            action2.setText("Rotate this site. (RotateLocationModifier)");


            //if (self.setBeheaded();)
            //action2.appendEffect(new AddUntilEndOfGameModifierEffect(action2, new RotateLocationModifier(self, Filters.Scarif_Turbolift_Complex, new TrueCondition()), null));
            //if (self.isRotated())
            //    action2.appendAfterEffect(new AddUntilEndOfGameModifierEffect(action2, new CancelEffectsOfRevolutionModifier())
            //action2.appendAfterEffect(new AddUntilEndOfGameModifierEffect(action2, new RotateLocationModifier(self, Filters.Scarif_Turbolift_Complex, new TrueCondition()), null));

            action2.appendAfterEffect(new AddUntilEndOfGameModifierEffect(action2, new RotateLocationModifier(self, self, new TrueCondition()), null));
            actions.add(action2);

            /*
            action3.setText("Rotate this site.  (Do both methods)");
            action3.appendEffect(new RotateCardEffect(action1, self, true));
            action3.appendEffect(new AddUntilEndOfGameModifierEffect(action1, new RotateLocationModifier(self, Filters.Scarif_Turbolift_Complex, new TrueCondition()), null));
            actions.add(action3);
            */

//            return Collections.singletonList(action1);
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
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.any, self, relatedSite, true))
            {
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