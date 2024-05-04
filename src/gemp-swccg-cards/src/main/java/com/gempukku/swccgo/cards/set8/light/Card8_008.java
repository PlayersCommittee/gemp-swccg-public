package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToDagobahLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Daughter Of Skywalker
 */
public class Card8_008 extends AbstractRebel {
    public Card8_008() {
        super(Side.LIGHT, 1, 7, 4, 5, 8, "Daughter Of Skywalker", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Scout. Leader. Made friends with Wicket. Negotiated an alliance with the Ewoks. Leia found out the truth about her father from Luke in the Ewok village.");
        setGameText("Deploys -3 on Tydirium, Endor or Dagobah. May be deployed instead of Luke by Mind What You Have Learned (that card then targets Leia instead of Luke for remainder of game). While at any exterior site, adds one Light side icon. Immune to attrition < 4.");
        addPersona(Persona.LEIA);
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT, Keyword.LEADER, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
        setMatchingStarshipFilter(Filters.Tydirium);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, -3, Filters.or(Filters.Tydirium, Filters.Deploys_on_Endor, Filters.Deploys_on_Dagobah)));
        modifiers.add(new MayDeployToDagobahLocationModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Mind_What_You_Have_Learned, Filters.not(Filters.hasGameTextModification(ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE))))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make Mind What You Have Learned target Leia");
            action.setActionMsg("Make Mind What You Have Learned / Save You It Can target Leia instead of Luke for remainder of game");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action, new ModifyGameTextModifier(self,
                            Filters.or(Filters.Mind_What_You_Have_Learned, Filters.Save_You_It_Can), ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE),
                            "Makes Mind What You Have Learned / Save You It Can target Leia instead of Luke for remainder of game"));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, Filters.sameSite(self), new AtCondition(self, Filters.exterior_site), Icon.LIGHT_FORCE, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
