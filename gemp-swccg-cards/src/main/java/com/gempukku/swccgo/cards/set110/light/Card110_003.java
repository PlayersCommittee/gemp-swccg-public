package com.gempukku.swccgo.cards.set110.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Character
 * Subtype: Droid
 * Title: See-Threepio
 */
public class Card110_003 extends AbstractDroid {
    public Card110_003() {
        super(Side.LIGHT, 2, 3, 1, 4, "See-Threepio", Uniqueness.UNIQUE);
        setLore("C-3PO was Jabba's 'khan chita,' or translator. Survived more battles than most members of the Alliance. Wasn't informed of R2-D2's role in the rescue of Han.");
        setGameText("Deploys only to a Jabba's Palace site. Once per game, when replacing another C-3PO, retrieve 3 Force. When in battle with your other droid and a Rebel, adds one battle destiny. R2-D2 deploys and moves for free to same location.");
        addIcons(Icon.PREMIUM);
        addModelType(ModelType.PROTOCOL);
        addPersona(Persona.C3PO);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_to_Jabbas_Palace_site;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.SEE_THREEPIO__RETRIEVE_FORCE;

        // Check condition(s)
        if (TriggerConditions.justPersonaReplacedCharacter(game, effectResult, self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Have " + playerId + " retrieve 3 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 3));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter sameLocation = Filters.sameLocation(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new InBattleWithCondition(self,
                Filters.and(Filters.your(self), Filters.droid)), new InBattleWithCondition(self, Filters.Rebel)), 1));
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.R2D2, sameLocation));
        modifiers.add(new MovesFreeToLocationModifier(self, Filters.R2D2, sameLocation));
        return modifiers;
    }
}
