package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 6
 * Type: Character
 * Subtype: Droid
 * Title: IG-88, Renegade Droid
 */
public class Card601_008 extends AbstractDroid {
    public Card601_008() {
        super(Side.DARK, 1, 4, 4, 5, "IG-88", Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Bounty hunter droid equipped with proprietary stealth technology. Archived several of its enhancement subroutines in favor of sophisticated tracking and capture programming.");
        setGameText("Adds 2 to power of anything he pilots (3 if IG-2000).  Deploys -3 aboard IG-2000.  Adds one battle destiny if alone or with a smuggler.  During battle, if piloting a starship, may draw destiny.  Starship is immune to attrition < X, or its immunity is +X (limit +2), where X = destiny.");
        addPersona(Persona.IG88);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.PRESENCE, Icon.BLOCK_6);
        addKeywords(Keyword.BOUNTY_HUNTER);
        addModelType(ModelType.ASSASSIN);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, -3, Persona.IG2000));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Persona.IG2000)));
        modifiers.add(new AddsBattleDestinyModifier(self, new OrCondition(new AloneCondition(self), new WithCondition(self, Filters.smuggler)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isPiloting(game, self, Filters.starship)) {
            final PhysicalCard starship = Filters.findFirstActive(game, self, Filters.hasPiloting(self));
            if (starship != null) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Draw destiny to modify immunity to attrition");
                action.setActionMsg("Draw destiny to modify immunity to attrition");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId, 1) {
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                if (totalDestiny != null) {
                                    //TODO this part probably needs to be changed
                                    action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                            new ImmuneToAttritionLessThanModifier(self, Filters.and(starship, Filters.not(Filters.alreadyHasImmunityToAttrition(self))), totalDestiny),
                                            "Adds immunity to attrition"));
                                    float toAdd = Math.min(2, totalDestiny);
                                    action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                            new ImmunityToAttritionChangeModifier(self, Filters.and(starship, Filters.alreadyHasImmunityToAttrition(self)), toAdd),
                                            "Adds to immunity to attrition"));
                                }
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
