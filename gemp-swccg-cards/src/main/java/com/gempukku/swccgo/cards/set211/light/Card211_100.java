package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

public class Card211_100 extends AbstractRebel {
        public Card211_100() {
            super(Side.LIGHT, 1, 5, 5, 6, 7, "Ahsoka Tano", Uniqueness.UNIQUE);
            setArmor(5);
            setLore("Human-replica droid. Programmed to function as Xizor's personal bodyguard and assassin. Black Sun agent. Cost 9 million credits. Worth every decicred.");
            setGameText("Adds 2 to power of anything she pilots. When present with Xizor, he may not be targeted by weapons. While Vader not here, opponent may draw no more than one battle destiny here. Immune to purchase, Restraining Bolt, and attrition < 5.");
            addIcons(Icon.PILOT, Icon.WARRIOR,Icon.WARRIOR);
            addKeywords(Keyword.FEMALE);
            setSpecies(Species.TOGRUTA);
            addPersona(Persona.AHSOKA);
        }

        @Override
        protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
            List<Modifier> modifiers = new LinkedList<Modifier>();
            modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), -1, game.getDarkPlayer()));
            //it might be self, Filter.self
            modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, self, new TargetedByWeaponCondition(self, Filters.and(Filters.weapon, Filters.icon(Icon.PERMANENT_WEAPON))));
            modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
            return modifiers;
        }

        //new action: move
    }
}
