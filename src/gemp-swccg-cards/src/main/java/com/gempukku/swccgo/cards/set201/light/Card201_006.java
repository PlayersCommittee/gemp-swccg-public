package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtScompLink;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Character
 * Subtype: Droid
 * Title: R2-D2 (Artoo-Detoo) (V)
 */
public class Card201_006 extends AbstractDroid {
    public Card201_006() {
        super(Side.LIGHT, 2, 2, 1, 4, "R2-D2 (Artoo-Detoo)", Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setAlternateDestiny(5);
        setVirtualSuffix(true);
        setLore("Fiesty. Loyal. Heroic. Insecure. Rebel spy. Excels at trouble. Incorrigible counterpart of a mindless philosopher. Has picked up a slight flutter. A bit eccentric.");
        setGameText("While aboard a starfighter, adds 2 to power, maneuver, and hyperspeed. " +
                "While present with a Scomp link, adds one [Light Side] icon here and, during battles at same and related interior sites, once per turn may use 1 Force to subtract 1 from a just drawn destiny.");
        addPersona(Persona.R2D2);
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.A_NEW_HOPE, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starfighterAboard = Filters.and(Filters.starfighter, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, Filters.sameLocation(self), new PresentAtScompLink(self), Icon.LIGHT_FORCE, 1));
        modifiers.add(new PowerModifier(self, starfighterAboard, 2));
        modifiers.add(new ManeuverModifier(self, starfighterAboard, 2));
        modifiers.add(new HyperspeedModifier(self, starfighterAboard, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        Filter sameInteriorSite = Filters.and(Filters.sameSite(self), Filters.interior_site);
        Filter relatedInteriorSite = Filters.and(Filters.relatedSite(self), Filters.interior_site);
        Filter sameOrRelatedInteriorSite = Filters.or(sameInteriorSite, relatedInteriorSite);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isAtScompLink(game, self)
                && GameConditions.isDuringBattleAt(game, sameOrRelatedInteriorSite) //battle at same or related interior site to R2-D2
                && GameConditions.isAtLocation(game, self, Filters.site) //R2-D2 at any site (not at a system or sector)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && TriggerConditions.isDestinyJustDrawn(game, effectResult)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Subtract 1 from destiny draw");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            // Perform result(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            actions.add(action);

        }

        return actions;
    }
}
