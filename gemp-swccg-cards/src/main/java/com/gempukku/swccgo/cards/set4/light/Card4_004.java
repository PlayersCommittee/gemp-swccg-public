package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.DetachParasiteEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Creature
 * Title: Mynock
 */
public class Card4_004 extends AbstractCreature {
    public Card4_004() {
        super(Side.LIGHT, 3, 3, 2, 3, 0, Title.Mynock);
        setLore("Starship parasite. Flourishes in vacuum of space. Consumes radiation and other forms of energy. Chews on power cables. Adults have one-meter wingspan. Reproduces by macromiosis.");
        setGameText("Habitat: unlimited. Parasite: Starfighter. Host's power and hyperspeed are cumulatively -2; while both < 1, Mynocks randomly detach one at a time (cannot attach for remainder of turn). Moves like a starfighter.");
        addModelType(ModelType.SPACE);
        addIcons(Icon.DAGOBAH, Icon.SELECTIVE_CREATURE);
        addKeyword(Keyword.PARASITE);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.any;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter host = Filters.and(Filters.hasAttached(self), Filters.starfighter);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ParasiteTargetModifier(self, Filters.starfighter));
        modifiers.add(new PowerModifier(self, host, -2, true));
        modifiers.add(new HyperspeedModifier(self, host, -2, true));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            final PhysicalCard host = self.getAttachedTo();
            if (host != null && Filters.starfighter.accepts(game, host)) {
                GameState gameState = game.getGameState();
                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                float power = modifiersQuerying.getPower(gameState, host);
                float hyperspeed = modifiersQuerying.getHyperspeed(gameState, host);
                if (power < 1 && hyperspeed < 1) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setSingletonTrigger(true);
                    action.setText("Detach a random mynock");
                    action.setActionMsg("Detach a random mynock from " + GameUtils.getCardLink(host));
                    // Perform result(s)
                    action.appendEffect(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    Collection<PhysicalCard> mynocks = Filters.filterAllOnTable(game, Filters.and(Filters.mynock, Filters.attachedTo(host)));
                                    if (!mynocks.isEmpty()) {
                                        PhysicalCard mynockToDetach = GameUtils.getRandomCards(mynocks, 1).get(0);
                                        action.appendEffect(
                                                new AddUntilEndOfTurnModifierEffect(action, new MayNotAttackModifier(self, mynockToDetach), null));
                                        action.appendEffect(
                                                new DetachParasiteEffect(action, mynockToDetach));
                                    }
                                }
                            });
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    public boolean isDeploysAndMovesLikeStarfighter() {
        return true;
    }
}
