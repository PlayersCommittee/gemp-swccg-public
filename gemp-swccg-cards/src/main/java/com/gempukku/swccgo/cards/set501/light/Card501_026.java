package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda, Master Of The Force (v)
 */
public class Card501_026 extends AbstractJediMaster {
    public Card501_026() {
        super(Side.LIGHT, 4, 4, 3, 7, 7, "Yoda, Master Of The Force", Uniqueness.UNIQUE);
        setLore("Jedi Council Member. 'More to say have you?'");
        setGameText("Power +3 in a defensive battle (or while you have exactly 2 Jedi on table). Once per game, during your move phase, may relocate to a battleground as a regular move. Immune to attrition.");
        addPersona(Persona.YODA);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
        setVirtualSuffix(true);
        setTestingText("Yoda, Master Of The Force (v)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition defendingBattleCondition = new DefendingBattleCondition(self);
        Condition exactlyTwoJediOnTable = new OnTableCondition(self, 2, true, Filters.and(Filters.your(self.getOwner()), Filters.Jedi));
        modifiers.add(new PowerModifier(self, new OrCondition(defendingBattleCondition, exactlyTwoJediOnTable), 3));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.YODA_MASTER_OF_THE_FORCE_V__RELOCATE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.MOVE)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Relocate to a battleground");
            action.setActionMsg("Relocate to a battleground");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate to", Filters.battleground_site) {
                        @Override
                        protected void cardSelected(final PhysicalCard siteSelected) {
                            action.addAnimationGroup(self);
                            action.addAnimationGroup(siteSelected);
                            // Pay cost(s)
                            action.appendCost(
                                    new PayRelocateBetweenLocationsCostEffect(action, playerId, self, siteSelected, 1));
                            // Allow response(s)
                            action.allowResponses("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(siteSelected),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RelocateBetweenLocationsEffect(action, self, siteSelected, true));
                                        }
                                    });
                        }
                    });

            return Collections.singletonList(action);
        }
        return null;
    }
}
