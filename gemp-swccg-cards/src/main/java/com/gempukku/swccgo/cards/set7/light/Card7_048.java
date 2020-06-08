package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.UndercoverCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.BreakCoverEffect;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBreakOwnCoverDuringDeployPhaseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Rebel
 * Title: TK-422
 */
public class Card7_048 extends AbstractRebel {
    public Card7_048() {
        super(Side.LIGHT, 1, 3, 3, 3, 6, Title.TK422, Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Corellian smuggler. Spy. Han stole the armor and identity of an enemy soldier that boarded the Millennium Falcon. Bluffed his way into the detention area.");
        setGameText("Deploys only as an Undercover spy at same site as an Imperial. While Undercover, Imperials are deploy +1 at related sites. May voluntarily 'break cover' only during your move phase by using 3 Force. Captured if 'cover broken' by opponent.");
        addPersona(Persona.HAN);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SPY, Keyword.SMUGGLER);
        setSpecies(Species.CORELLIAN);
        setDeploysAsUndercoverSpy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.sameSiteAs(self, Filters.Imperial);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Imperial, new UndercoverCondition(self), 1, Filters.relatedSite(self)));
        modifiers.add(new MayNotBreakOwnCoverDuringDeployPhaseModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.isUndercover(game, self)
                && GameConditions.canUseForce(game, playerId, 3)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("'Break cover'");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Perform result(s)
            action.appendEffect(
                    new BreakCoverEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.coverBrokenBy(game, effectResult, game.getOpponent(self.getOwner()), self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make captured");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " captured");
            // Perform result(s)
            action.appendEffect(
                    new CaptureCharacterOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
