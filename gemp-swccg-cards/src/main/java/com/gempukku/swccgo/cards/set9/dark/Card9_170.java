package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Scythe 3
 */
public class Card9_170 extends AbstractStarfighter {
    public Card9_170() {
        super(Side.DARK, 4, 2, 1, null, 4, null, 3, Title.Scythe_3, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Heavily modified TIE/ln. Cannons upgraded to destroy small asteroids while serving in Anoat system. P-w702 maneuvering jets added by Death Star technicians.");
        setGameText("May add 1 pilot. Hebsly deploys -2 aboard. Once during each of your move phases, may fire one starship weapon aboard ('hit' targets are lost). Immune to attrition < 4 when Hebsly piloting.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.SCYTHE_SQUADRON);
        addModelType(ModelType.TIE_LN);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Hebsly);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.Hebsly, -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.Hebsly, -2, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Hebsly), 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)) {
            Filter weaponFilter = Filters.and(Filters.your(self), Filters.starship_weapon, Filters.attachedTo(self), Filters.canBeFiredAt(self, Filters.canBeTargetedBy(self), 0));
            if (GameConditions.canSpot(game, self, weaponFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Fire a starship weapon");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose starship weapon to fire", weaponFilter) {
                            @Override
                            protected void cardSelected(PhysicalCard weapon) {
                                action.addAnimationGroup(weapon);
                                action.setActionMsg("Fire " + GameUtils.getCardLink(weapon));
                                // Perform result(s)
                                action.appendEffect(
                                        new FireWeaponEffect(action, weapon, false, Filters.canBeTargetedBy(self)));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
