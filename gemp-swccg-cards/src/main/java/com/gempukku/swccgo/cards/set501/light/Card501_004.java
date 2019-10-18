package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 12
 * Type: Character
 * Subtype: Alien
 * Title: Tamtel Skreej (V)
 */
public class Card501_004 extends AbstractAlien {
    public Card501_004() {
        super(Side.LIGHT, 1, 3, 3, 3, 6, "Tamtel Skreej", Uniqueness.UNIQUE);
        setLore("Gambler. Lando Calrissian posed as a guard for Jabba in order to spy on the Hutt. Feared that he would be recognized by some of Jabba's companions.");
        setGameText("Deploys -1 to Falcon. While at a Jabba’s Palace site, Lando’s game text may not be canceled. Any Force you retrieve with a [Premium] objective may be taken into hand. While with Han or your Rep, opponent may not target that character with interrupts or weapons.");
        addIcons(Icon.VIRTUAL_SET_12, Icon.JABBAS_PALACE, Icon.PILOT, Icon.WARRIOR);
        addPersona(Persona.LANDO);
        addKeywords(Keyword.GAMBLER, Keyword.SPY);
        setVirtualSuffix(true);
        setTestingText("Tamtel Skreej (V)");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToTargetModifier(self, -1, Filters.Falcon));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, new AtCondition(self, Filters.Jabbas_Palace_site)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final PhysicalCard rep = game.getGameState().getRep(self.getOwner());
        final Filter repFilter;
        if (rep != null) {
            repFilter = Filters.sameTitle(rep);
        } else {
            repFilter = null;
        }
        Filter hanOrYourRep = Filters.and(Filters.your(self), Filters.or(Filters.Han, repFilter));
        Condition withHanOrYourRep = new WithCondition(self, hanOrYourRep);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, hanOrYourRep, withHanOrYourRep));
        modifiers.add(new MayNotBeTargetedByModifier(self, hanOrYourRep, withHanOrYourRep, Filters.Interrupt));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Icon.PREMIUM, CardType.OBJECTIVE), ModifyGameTextType.PREM_OBJECTIVE__RETRIEVE_FORCE_INTO_HAND));
        return modifiers;
    }
}