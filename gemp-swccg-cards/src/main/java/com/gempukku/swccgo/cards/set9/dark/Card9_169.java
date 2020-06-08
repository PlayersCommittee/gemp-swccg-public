package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Scythe 1
 */
public class Card9_169 extends AbstractStarfighter {
    public Card9_169() {
        super(Side.DARK, 4, 2, 1, null, 4, null, 3, "Scythe 1", Uniqueness.UNIQUE);
        setLore("TIE fighter garrisoned aboard second Death Star. Part of Scythe Squadron, a TIE group with upgraded SFS Pw702 maneuvering jets to increase performance in tight quarters.");
        setGameText("May add 1 pilot. Once during each of your move phases, may fire one starship weapon aboard ('hit' targets are lost) and/or make an additional move. Immune to attrition < 4 when Mianda piloting.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.SCYTHE_SQUADRON);
        addModelType(ModelType.TIE_LN);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Mianda);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Mianda), 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)) {
            Filter weaponFilter = Filters.and(Filters.your(self), Filters.starship_weapon, Filters.attachedTo(self), Filters.canBeFiredAt(self, Filters.canBeTargetedBy(self), 0));
            if (GameConditions.canSpot(game, self, weaponFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
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
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && Filters.movableAsAdditionalMove(playerId).accepts(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make an additional move");
            action.setActionMsg("Have " + GameUtils.getCardLink(self) + " make an additional move");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new MoveCardAsRegularMoveEffect(action, playerId, self, false, true, Filters.any));
            actions.add(action);
        }
        return actions;
    }
}
