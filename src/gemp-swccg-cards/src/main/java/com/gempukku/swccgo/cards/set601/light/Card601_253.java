package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 6
 * Type: Starship
 * Subtype: Starfighter
 * Title: Wedge In Red Squadron 1
 */
public class Card601_253 extends AbstractStarfighter {
    public Card601_253() {
        super(Side.LIGHT, 2, 4, 6, null, 5, 5, 6, "Wedge In Red Squadron 1", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("Flown by Wedge Antilles as Red 2 at the Battle of Yavin. Redesignated at Endor. Rugged Incom fighter. Victory markers show it role in the attack on the first Death Star.");
        setGameText("Permanent pilot is •Wedge, who provides ability of 3. During a battle with opponent's [Independent] starship, may add one destiny to total power or attrition. Immune to Come With Me, Tallon Roll, and attrition < 4.");
        addPersona(Persona.RED_2);
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_6);
        addKeywords(Keyword.RED_SQUADRON);
        addModelType(ModelType.X_WING);
        setAsLegacy(true);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.WEDGE, 3) {});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattleWith(game, self, Filters.and(Filters.opponents(self), Icon.INDEPENDENT, Filters.starship))) {
            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Add one destiny to total power");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AddDestinyToTotalPowerEffect(action, 1));
                actions.add(action);
            }
            if (GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Add one destiny to attrition");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AddDestinyToAttritionEffect(action, 1));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Come_With_Me));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Tallon_Roll));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
