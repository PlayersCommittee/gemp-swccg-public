var CardFilter = Class.extend({
    clearCollectionFunc:null,
    addCardFunc:null,
    finishCollectionFunc:null,
    getCollectionFunc:null,

    collectionType:null,

    filter:null,
    start:0,
    count:12,

    pageDiv:null,
    fullFilterDiv:null,
    filterDiv:null,

    previousPageBut:null,
    nextPageBut:null,
    countSlider:null,

    sideLabel:null,
    sideSelect:null,
    formatLabel:null,
    formatSelect:null,
    setLabel:null,
    setSelect:null,
    cardTypeLabel:null,
    cardTypeSelect:null,
    rarityLabel:null,
    raritySelect:null,

    resetFiltersButton:null,

    nameLabel:null,
    nameInput:null,
    loreLabel:null,
    loreInput:null,
    gametextLabel:null,
    gametextInput:null,

    iconLabel:null,
    iconSelect:null,
    personaLabel:null,
    personaSelect:null,

    destinyLabel:null,
    destinyCompareSelect:null,
    destinyValueInput:null,
    powerLabel:null,
    powerCompareSelect:null,
    powerValueInput:null,
    abilityLabel:null,
    abilityCompareSelect:null,
    abilityValueInput:null,
    deployLabel:null,
    deployCompareSelect:null,
    deployValueInput:null,
    forfeitLabel:null,
    forfeitCompareSelect:null,
    forfeitValueInput:null,
    armorLabel:null,
    armorCompareSelect:null,
    armorValueInput:null,
    defenseValueLabel:null,
    defenseValueCompareSelect:null,
    defenseValueValueInput:null,
    maneuverValueLabel:null,
    maneuverValueCompareSelect:null,
    maneuverValueValueInput:null,
    landspeedValueLabel:null,
    landspeedValueCompareSelect:null,
    landspeedValueValueInput:null,

    productLabel:null,
    productSelect:null,
    sortLabel:null,
    sortSelect:null,

    init:function (elem, pageElem, getCollectionFunc, clearCollectionFunc, addCardFunc, finishCollectionFunc) {
        this.getCollectionFunc = getCollectionFunc;
        this.clearCollectionFunc = clearCollectionFunc;
        this.addCardFunc = addCardFunc;
        this.finishCollectionFunc = finishCollectionFunc;

        this.buildUi(elem, pageElem);
        this.filter = this.calculateDeckFilter();
        this.getCollection();
    },

    buildUi:function (elem, pageElem) {
        var that = this;

        this.pageDiv = $("<div></div>");

        // Previous page navigation button
        this.previousPageBut = $("<button id='previousPage' style='float: left;'>Previous page</button>").button({
            text:false,
            icons:{
                primary:"ui-icon-circle-triangle-w"
            },
            disabled:true
        }).click(
            function () {
                that.disableNavigation();
                that.start -= that.count;
                that.getCollection();
            });

        // Next page navigation button
        this.nextPageBut = $("<button id='nextPage' style='float: right;'>Next page</button>").button({
            text:false,
            icons:{
                primary:"ui-icon-circle-triangle-e"
            },
            disabled:true
        }).click(
            function () {
                that.disableNavigation();
                that.start += that.count;
                that.getCollection();
            });

        // Slider for number of cards per page
        this.countSlider = $("<div id='countSlider' style='left: 50px; top: 10px; width: 200px;'></div>").slider({
            value:12,
            min:4,
            max:40,
            step:1,
            disabled:true,
            slide:function (event, ui) {
                that.start = 0;
                that.count = ui.value;
                that.getCollection();
            }
        });

        this.pageDiv.append(this.previousPageBut);
        this.pageDiv.append(this.nextPageBut);
        this.pageDiv.append(this.countSlider);
        pageElem.append(this.pageDiv);

        this.fullFilterDiv = $("<div></div>");

        this.sideLabel = $("<label for='sideSelect' class='filterLabel'>Side:</label>");
        this.sideSelect = $("<select id='sideSelect' class='filterInput'>"
            + "<option value='' selected='selected'>All</option>"
            + "<option value='DARK'>Dark</option>"
            + "<option value='LIGHT'>Light</option>"
            + "</select>");

        this.formatLabel = $("<label for='formatSelect' class='filterLabel'>Format:</label>");
        this.formatSelect = $("<select id='formatSelect' class='filterInput'>"
            + "<option value='' selected='selected'>All</option>"
            + "<option value='open'>Open</option>"
            + "<option value='jawa'>Jawa Format</option>"
            + "<option value='open_no_shields'>Open (no shields)</option>"
            + "<option value='open_no_virtual'>Open (no v-cards)</option>"
            + "<option value='open_no_shields_no_virtual'>Open (no shields / no v-cards)</option>"
            + "<option value='classic'>Classic</option>"
            + "<option value='classic_no_virtual'>Classic (no virtual cards)</option>"
            + "<option value='premiere_ref2'>Premiere - Reflections II</option>"
            + "<option value='premiere_ds2'>Premiere - Death Star II</option>"
            + "<option value='premiere_endor'>Premiere - Endor</option>"
            + "<option value='premiere_se'>Premiere - Special Edition</option>"
            + "<option value='premiere_jp'>Premiere - Jabba's Palace</option>"
            + "<option value='premiere_cc'>Premiere - Cloud City</option>"
            + "<option value='premiere_dagobah'>Premiere - Dagobah</option>"
            + "<option value='premiere_hoth'>Premiere - Hoth</option>"
            + "<option value='premiere_anh'>Premiere - A New Hope</option>"
            + "<option value='premiere'>Premiere</option>"
            + "</select>");

        this.setLabel = $("<label for='setSelect' class='filterLabel'>Set:</label>");
        this.setSelect = $("<select id='setSelect' class='filterInput'>"
            + "<option value='' selected='selected'>All</option>"
            + "<option value='1'>Premiere</option>"
            + "<option value='101'>Premiere 2-Player</option>"
            + "<option value='102'>Jedi Pack</option>"
            + "<option value='103'>Rebel Leader</option>"
            + "<option value='2'>A New Hope</option>"
            + "<option value='3'>Hoth</option>"
            + "<option value='104'>ESB 2-Player</option>"
            + "<option value='4'>Dagobah</option>"
            + "<option value='105'>1st Anthology</option>"
            + "<option value='5'>Cloud City</option>"
            + "<option value='6'>Jabba's Palace</option>"
            + "<option value='106'>Sealed Deck</option>"
            + "<option value='107'>2nd Anthology</option>"
            + "<option value='7'>Special Edition</option>"
            + "<option value='108'>Enhanced Premiere Pack</option>"
            + "<option value='8'>Endor</option>"
            + "<option value='109'>Enhanced Cloud City</option>"
            + "<option value='110'>Enhanced Jabba's Palace</option>"
            + "<option value='111'>3rd Anthology</option>"
            + "<option value='9'>Death Star II</option>"
            + "<option value='112'>Jabba's Palace Sealed Deck</option>"
            + "<option value='10'>Reflections II</option>"
            + "<option value='11'>Tatooine</option>"
            + "<option value='12'>Coruscant</option>"
            + "<option value='13'>Reflections III</option>"
            + "<option value='14'>Theed Palace</option>"
            + "<option value='200'>Set 0</option>"
            + "<option value='201'>Set 1</option>"
            + "<option value='202'>Set 2</option>"
            + "<option value='203'>Set 3</option>"
            + "<option value='204'>Set 4</option>"
            + "<option value='205'>Set 5</option>"
            + "<option value='206'>Set 6</option>"
            + "<option value='207'>Set 7</option>"
            + "<option value='208'>Set 8</option>"
            + "<option value='209'>Set 9</option>"
            + "<option value='210'>Set 10</option>"
            + "<option value='211'>Set 11</option>"
            + "<option value='301'>Virtual Premium Set</option>"
            + "<option value='401'>Dream Cards</option>"
            + "<option value='501'>Playtesting</option>"
            + "</select>");

        this.cardTypeLabel = $("<label for='cardTypeSelect' class='filterLabel'>Card&nbsp;type:</label>");
        this.cardTypeSelect = $("<select id='cardTypeSelect' class='filterInput'>"
            + "<option value='' selected='selected'>All</option>"
            + "<option value='ADMIRALS_ORDER'>Admiral's Order</option>"
            + "<option value='CHARACTER'>Character</option>"
            + "<option value='CHARACTER_ALIEN'>Character - Alien</option>"
            + "<option value='CHARACTER_DARK_JEDI_MASTER'>Character - Dark Jedi Master</option>"
            + "<option value='CHARACTER_DROID'>Character - Droid</option>"
            + "<option value='CHARACTER_FIRST_ORDER'>Character - First Order</option>"
            + "<option value='CHARACTER_IMPERIAL'>Character - Imperial</option>"
            + "<option value='CHARACTER_JEDI_MASTER'>Character - Jedi Master</option>"
            + "<option value='CHARACTER_REBEL'>Character - Rebel</option>"
            + "<option value='CHARACTER_REPUBLIC'>Character - Republic</option>"
            + "<option value='CHARACTER_RESISTANCE'>Character - Resistance</option>"
            + "<option value='CHARACTER_SITH'>Character - Sith</option>"
            + "<option value='CREATURE'>Creature</option>"
            + "<option value='DEFENSIVE_SHIELD'>Defensive Shield</option>"
            + "<option value='DEVICE'>Device</option>"
            + "<option value='EFFECT'>Effect (of any kind)</option>"
            + "<option value='EFFECT_NO_SUBTYPE'>Effect</option>"
            + "<option value='EFFECT_IMMEDIATE'>Effect - Immediate</option>"
            + "<option value='EFFECT_MOBILE'>Effect - Mobile</option>"
            + "<option value='EFFECT_POLITICAL'>Effect - Political</option>"
            + "<option value='EFFECT_STARTING'>Effect - Starting</option>"
            + "<option value='EFFECT_UTINNI'>Effect - Utinni</option>"
            + "<option value='EPIC_EVENT'>Epic Event</option>"
            + "<option value='GAME_AID'>Game Aid</option>"
            + "<option value='INTERRUPT'>Interrupt</option>"
            + "<option value='INTERRUPT_LOST'>Interrupt - Lost</option>"
            + "<option value='INTERRUPT_LOST_OR_STARTING'>Interrupt - Lost Or Starting</option>"
            + "<option value='INTERRUPT_USED'>Interrupt - Used</option>"
            + "<option value='INTERRUPT_USED_OR_LOST'>Interrupt - Used Or Lost</option>"
            + "<option value='INTERRUPT_USED_OR_STARTING'>Interrupt - Used Or Starting</option>"
            + "<option value='JEDI_TEST'>Jedi Test</option>"
            + "<option value='LOCATION'>Location</option>"
            + "<option value='LOCATION_SECTOR'>Location - Sector</option>"
            + "<option value='LOCATION_SITE'>Location - Site</option>"
            + "<option value='LOCATION_SYSTEM'>Location - System</option>"
            + "<option value='OBJECTIVE'>Objective</option>"
            + "<option value='PODRACER'>Podracer</option>"
            + "<option value='STARSHIP'>Starship</option>"
            + "<option value='STARSHIP_CAPITAL'>Starship - Capital</option>"
            + "<option value='STARSHIP_SQUADRON'>Starship - Squadron</option>"
            + "<option value='STARSHIP_STARFIGHTER'>Starship - Starfighter</option>"
            + "<option value='VEHICLE'>Vehicle</option>"
            + "<option value='VEHICLE_COMBAT'>Vehicle - Combat</option>"
            + "<option value='VEHICLE_CREATURE'>Vehicle - Creature</option>"
            + "<option value='VEHICLE_SHUTTLE'>Vehicle - Shuttle</option>"
            + "<option value='VEHICLE_TRANSPORT'>Vehicle - Transport</option>"
            + "<option value='WEAPON'>Weapon</option>"
            + "<option value='WEAPON_ARTILLERY'>Weapon - Artillery</option>"
            + "<option value='WEAPON_AUTOMATED'>Weapon - Automated</option>"
            + "<option value='WEAPON_CHARACTER'>Weapon - Character</option>"
            + "<option value='WEAPON_DEATH_STAR'>Weapon - Death Star</option>"
            + "<option value='WEAPON_DEATH_STAR_II'>Weapon - Death Star II</option>"
            + "<option value='WEAPON_STARSHIP'>Weapon - Starship</option>"
            + "<option value='WEAPON_VEHICLE'>Weapon - Vehicle</option>"
            + "</select>");

        this.rarityLabel = $("<label for='raritySelect' class='filterLabel'>Rarity:</label>");
        this.raritySelect = $("<select id='raritySelect' class='filterInput'>"
            + "<option value=''>All</option>"
            + "<option value='C'>Common (C)</option>"
            + "<option value='C1'>Common (C1)</option>"
            + "<option value='C2'>Common (C2)</option>"
            + "<option value='C3'>Common (C3)</option>"
            + "<option value='XR'>Extra Rare (XR)</option>"
            + "<option value='F'>Fixed (F)</option>"
            + "<option value='PM'>Premium (PM)</option>"
            + "<option value='PV'>Preview (PV)</option>"
            + "<option value='R'>Rare (R)</option>"
            + "<option value='R1'>Rare (R1)</option>"
            + "<option value='R2'>Rare (R2)</option>"
            + "<option value='U'>Uncommon (U)</option>"
            + "<option value='U1'>Uncommon (U1)</option>"
            + "<option value='U2'>Uncommon (U2)</option>"
            + "<option value='UR'>Ultra Rare (UR)</option>"
            + "<option value='V'>Virtual</option>"
            + "</select>");

        this.productLabel = $("<label for='productSelect' class='filterLabel'>Product:</label>");
        this.productSelect = $("<select id='productSelect' class='filterInput'>"
            + "<option value='card'>Cards</option>"
            + "<option value='foil'>Foil Cards</option>"
            + "<option value='nonFoil'>Non-foil Cards</option>"
            + "<option value='pack'>Packs/Boxes</option>"
            + "</select>");

        this.sortLabel = $("<label for='sortSelect' class='filterLabel'>Sort&nbsp;by:</label>");
        this.sortSelect = $("<select id='sortSelect' class='filterInput'>"
            + "<option value='name,set,cardType' selected='selected'>Title</option>"
            + "<option value='cardType,name,set'>Card type</option>"
            + "</select>");

        this.nameLabel = $("<label for='nameInput' class='filterLabel'>Title&nbsp;contains:</label>");
        this.nameInput = $("<input id='nameInput' type='text' maxlength='100' class='filterInput' style='width: 250px;'>");

        this.loreLabel = $("<label for='loreInput' class='filterLabel'>Lore&nbsp;contains:</label>");
        this.loreInput = $("<input id='loreInput' type='text' maxlength='100' class='filterInput' style='width: 250px;'>");

        this.gametextLabel = $("<label for='gametextInput' class='filterLabel'>Gametext&nbsp;contains:</label>");
        this.gametextInput = $("<input id='gametextInput' type='text' maxlength='100' class='filterInput' style='width: 250px;'>");

        this.iconLabel = $("<label for='iconSelect' class='filterLabel'>Icon:</label>");
        this.iconSelect = $("<select id='iconSelect' class='filterInput'>"
            + "<option value='' selected='selected'>(Not specified)</option>"
            + "<option value='A_NEW_HOPE'>A New Hope</option>"
            + "<option value='CLONE_ARMY'>Clone Army</option>"
            + "<option value='CORUSCANT'>Coruscant</option>"
            + "<option value='CREATURE_SITE'>Creature Site</option>"
            + "<option value='DAGOBAH'>Dagobah</option>"
            + "<option value='DEATH_STAR_II'>Death Star II</option>"
            + "<option value='ENDOR'>Endor</option>"
            + "<option value='EPISODE_I'>Episode I</option>"
            + "<option value='EPISODE_VII'>Episode VII</option>"
            + "<option value='EXTERIOR_SITE'>Exterior Site</option>"
            + "<option value='FIRST_ORDER'>First Order</option>"
            + "<option value='GRABBER'>Grabber</option>"
            + "<option value='HOTH'>Hoth</option>"
            + "<option value='INDEPENDENT'>Independent</option>"
            + "<option value='INTERIOR_SITE'>Interior Site</option>"
            + "<option value='JABBAS_PALACE'>Jabba's Palace</option>"
            + "<option value='MAINTENANCE'>Maintenance</option>"
            + "<option value='MOBILE'>Mobile</option>"
            + "<option value='NAV_COMPUTER'>Nav Computer</option>"
            + "<option value='PERMANENT_WEAPON'>Permanent Weapon</option>"
            + "<option value='PILOT'>Pilot</option>"
            + "<option value='PLANET'>Planet</option>"
            + "<option value='PREMIUM'>Premium</option>"
            + "<option value='PRESENCE'>Presence</option>"
            + "<option value='REFLECTIONS_II'>Reflections II</option>"
            + "<option value='REFLECTIONS_III'>Reflections III</option>"
            + "<option value='REPUBLIC'>Republic</option>"
            + "<option value='SCOMP_LINK'>Scomp Link</option>"
            + "<option value='SELECTIVE_CREATURE'>Selective Creature</option>"
            + "<option value='SEPARATIST'>Separatist</option>"
            + "<option value='VIRTUAL_SET_0'>Set 0</option>"
            + "<option value='VIRTUAL_SET_1'>Set 1</option>"
            + "<option value='VIRTUAL_SET_2'>Set 2</option>"
            + "<option value='VIRTUAL_SET_3'>Set 3</option>"
            + "<option value='VIRTUAL_SET_4'>Set 4</option>"
            + "<option value='VIRTUAL_SET_5'>Set 5</option>"
            + "<option value='VIRTUAL_SET_6'>Set 6</option>"
            + "<option value='VIRTUAL_SET_7'>Set 7</option>"
            + "<option value='VIRTUAL_SET_8'>Set 8</option>"
            + "<option value='VIRTUAL_SET_9'>Set 9</option>"
            + "<option value='VIRTUAL_SET_10'>Set 10</option>"
            + "<option value='VIRTUAL_SET_11'>Set 11</option>"
            + "<option value='VIRTUAL_SET_P'>Set P</option>"
            + "<option value='SPACE'>Space</option>"
            + "<option value='SPECIAL_EDITION'>Special Edition</option>"
            + "<option value='STARSHIP_SITE'>Starship Site</option>"
            + "<option value='TATOOINE'>Tatooine</option>"
            + "<option value='THEED_PALACE'>Theed Palace</option>"
            + "<option value='TRADE_FEDERATION'>Trade Federation</option>"
            + "<option value='UNDERGROUND_SITE'>Underground Site</option>"
            + "<option value='UNDERWATER_SITE'>Underwater Site</option>"
            + "<option value='VEHICLE_SITE'>Vehicle Site</option>"
            + "<option value='VIRTUAL_DEFENSIVE_SHIELD'>Virtual Defensive Shield</option>"
            + "<option value='WARRIOR'>Warrior</option>"
            + "</select>");

        this.personaLabel = $("<label for='personaSelect' class='filterLabel'>Persona:</label>");
        this.personaSelect = $("<select id='personaSelect' class='filterInput'>"
            + "<option value='' selected='selected'>(Not specified)</option>"
            + "<option value='_4_LOM'>4-LOM</option>"
            + "<option value='AMIDALA'>Amidala</option>"
            + "<option value='BB8'>BB-8</option>"
            + "<option value='BLACK_2'>Black 2</option>"
            + "<option value='BLACK_3'>Black 3</option>"
            + "<option value='BOBA_FETT'>Boba Fett</option>"
            + "<option value='BOOSTER'>Booster</option>"
            + "<option value='BOSSK'>Bossk</option>"
            + "<option value='C3PO'>C-3PO</option>"
            + "<option value='CHEWIE'>Chewie</option>"
            + "<option value='CORRAN_HORN'>Corran Horn</option>"
            + "<option value='CRACKEN'>Cracken</option>"
            + "<option value='DASH'>Dash</option>"
            + "<option value='DENGAR'>Dengar</option>"
            + "<option value='DOFINE'>Dofine</option>"
            + "<option value='DS_61_2'>DS-61-2</option>"
            + "<option value='DS_61_3'>DS-61-3</option>"
            + "<option value='DUTCH'>Dutch</option>"
            + "<option value='EMPEROR'>Emperor</option>"
            + "<option value='EXECUTOR'>Executor</option>"
            + "<option value='FALCON'>Falcon</option>"
            + "<option value='GOLD_1'>Gold 1</option>"
            + "<option value='GREEN_SQUADRON_1'>Green Squadron 1</option>"
            + "<option value='GREEN_SQUADRON_3'>Green Squadron 3</option>"
            + "<option value='GUNRAY'>Gunray</option>"
            + "<option value='HAAKO'>Haako</option>"
            + "<option value='HAN'>Han</option>"
            + "<option value='HOUNDS_TOOTH'>Hound's Tooth</option>"
            + "<option value='IG2000'>IG-2000</option>"
            + "<option value='IG88'>IG-88</option>"
            + "<option value='JABBA'>Jabba</option>"
            + "<option value='JAR_JAR'>Jar Jar</option>"
            + "<option value='JENDON'>Jendon</option>"
            + "<option value='JONUS'>Jonus</option>"
            + "<option value='KYLO'>Kylo</option>"
            + "<option value='KYLOS'>Kylo's Lightsaber</option>"
            + "<option value='LANDO'>Lando</option>"
            + "<option value='LEIA'>Leia</option>"
            + "<option value='LOBOT'>Lobot</option>"
            + "<option value='LUKE'>Luke</option>"
            + "<option value='MARA_JADE'>Mara Jade</option>"
            + "<option value='MACE'>Mace</option>"
            + "<option value='MAUL'>Maul</option>"
            + "<option value='MAULS_DOUBLE_BLADED_LIGHTSABER'>Maul's Lightsaber</option>"
            + "<option value='MIST_HUNTER'>Mist Hunter</option>"
            + "<option value='MOTTI'>Motti</option>"
            + "<option value='OBIWAN'>Obi-Wan</option>"
            + "<option value='ONYX_1'>Onyx 1</option>"
            + "<option value='OS_72_1'>OS-72-1</option>"
            + "<option value='OS_72_2'>OS-72-2</option>"
            + "<option value='PANAKA'>Panaka</option>"
            + "<option value='PULSAR_SKATE'>Pulsar Skate</option>"
            + "<option value='PUNISHING_ONE'>Punishing One</option>"
            + "<option value='QUEENS_ROYAL_STARSHIP'>Queen's Royal Starship</option>"
            + "<option value='QUIGON'>Qui-Gon</option>"
            + "<option value='QUIGON_JINNS_LIGHTSABER'>Qui-Gon's Lightsaber</option>"
            + "<option value='R2D2'>R2-D2</option>"
            + "<option value='RED_1'>Red 1</option>"
            + "<option value='RED_2'>Red 2</option>"
            + "<option value='RED_5'>Red 5</option>"
            + "<option value='RED_LEADER'>Red Leader</option>"
            + "<option value='RIC'>Ric</option>"
            + "<option value='SCIMITAR_2'>Scimitar 2</option>"
            + "<option value='SIDIOUS'>Sidious</option>"
            + "<option value='SLAVE_I'>Slave I</option>"
            + "<option value='TARKIN'>Tarkin</option>"
            + "<option value='TYCHO'>Tycho</option>"
            + "<option value='VADER'>Vader</option>"
            + "<option value='VADERS_CUSTOM_TIE'>Vader's Custom TIE</option>"
            + "<option value='VADERS_LIGHTSABER'>Vader's Lightsaber</option>"
            + "<option value='VEERS'>Veers</option>"
            + "<option value='WEDGE'>Wedge</option>"
            + "<option value='YODA'>Yoda</option>"
            + "<option value='ZUCKUSS'>Zuckuss</option>"
            + "</select>");

        this.destinyLabel = $("<label for='destinyCompareSelect' class='filterLabel'>Destiny:</label>");
        this.destinyCompareSelect = $("<select id='destinyCompareSelect' class='filterInput'>"
            + "<option value='' selected='selected'>Any</option>"
            + "<option value='EQUALS'>=</option>"
            + "<option value='GREATER_THAN'>></option>"
            + "<option value='GREATER_THAN_OR_EQUAL_TO'>>=</option>"
            + "<option value='LESS_THAN'><</option>"
            + "<option value='LESS_THAN_OR_EQUAL_TO'><=</option>"
            + "</select>");
        this.destinyValueInput = $("<input id='destinyValueInput' type='number' min='0' max='7' class='filterInput' style='width: 30px;'>");

        this.powerLabel = $("<label for='powerCompareSelect' class='filterLabel'>Power:</label>");
        this.powerCompareSelect = $("<select id='powerCompareSelect' class='filterInput'>"
            + "<option value='' selected='selected'>Any</option>"
            + "<option value='EQUALS'>=</option>"
            + "<option value='GREATER_THAN'>></option>"
            + "<option value='GREATER_THAN_OR_EQUAL_TO'>>=</option>"
            + "<option value='LESS_THAN'><</option>"
            + "<option value='LESS_THAN_OR_EQUAL_TO'><=</option>"
            + "</select>");
        this.powerValueInput = $("<input id='powerValueInput' type='number' min = '0' max='12' class='filterInput' style='width: 30px;'>");

        this.abilityLabel = $("<label for='abilityCompareSelect' class='filterLabel'>Ability:</label>");
        this.abilityCompareSelect = $("<select id='abilityCompareSelect' class='filterInput'>"
            + "<option value='' selected='selected'>Any</option>"
            + "<option value='EQUALS'>=</option>"
            + "<option value='GREATER_THAN'>></option>"
            + "<option value='GREATER_THAN_OR_EQUAL_TO'>>=</option>"
            + "<option value='LESS_THAN'><</option>"
            + "<option value='LESS_THAN_OR_EQUAL_TO'><=</option>"
            + "</select>");
        this.abilityValueInput = $("<input id='abilityValueInput' type='number' min='0' max='7' class='filterInput' style='width: 30px;'>");

        this.deployLabel = $("<label for='deployCompareSelect' class='filterLabel'>Deploy:</label>");
        this.deployCompareSelect = $("<select id='deployCompareSelect' class='filterInput'>"
            + "<option value='' selected='selected'>Any</option>"
            + "<option value='EQUALS'>=</option>"
            + "<option value='GREATER_THAN'>></option>"
            + "<option value='GREATER_THAN_OR_EQUAL_TO'>>=</option>"
            + "<option value='LESS_THAN'><</option>"
            + "<option value='LESS_THAN_OR_EQUAL_TO'><=</option>"
            + "</select>");
        this.deployValueInput = $("<input id='deployValueInput' type='number' min='0' max='15' class='filterInput' style='width: 30px;'>");

        this.forfeitLabel = $("<label for='forfeitCompareSelect' class='filterLabel'>Forfeit:</label>");
        this.forfeitCompareSelect = $("<select id='forfeitCompareSelect' class='filterInput'>"
            + "<option value='' selected='selected'>Any</option>"
            + "<option value='EQUALS'>=</option>"
            + "<option value='GREATER_THAN'>></option>"
            + "<option value='GREATER_THAN_OR_EQUAL_TO'>>=</option>"
            + "<option value='LESS_THAN'><</option>"
            + "<option value='LESS_THAN_OR_EQUAL_TO'><=</option>"
            + "</select>");
        this.forfeitValueInput = $("<input id='forfeitValueInput' type='number' min='0' max='15' class='filterInput' style='width: 30px;'>");

        this.armorLabel = $("<label for='armorCompareSelect' class='filterLabel'>Armor:</label>");
        this.armorCompareSelect = $("<select id='armorCompareSelect' class='filterInput'>"
            + "<option value='' selected='selected'>Any</option>"
            + "<option value='EQUALS'>=</option>"
            + "<option value='GREATER_THAN'>></option>"
            + "<option value='GREATER_THAN_OR_EQUAL_TO'>>=</option>"
            + "<option value='LESS_THAN'><</option>"
            + "<option value='LESS_THAN_OR_EQUAL_TO'><=</option>"
            + "</select>");
        this.armorValueInput = $("<input id='armorValueInput' type='number' min='0' max='12' class='filterInput' style='width: 30px;'>");

        this.defenseValueLabel = $("<label for='defenseValueCompareSelect' class='filterLabel'>Defense&nbsp;value:</label>");
        this.defenseValueCompareSelect = $("<select id='defenseValueCompareSelect' class='filterInput'>"
            + "<option value='' selected='selected'>Any</option>"
            + "<option value='EQUALS'>=</option>"
            + "<option value='GREATER_THAN'>></option>"
            + "<option value='GREATER_THAN_OR_EQUAL_TO'>>=</option>"
            + "<option value='LESS_THAN'><</option>"
            + "<option value='LESS_THAN_OR_EQUAL_TO'><=</option>"
            + "</select>");
        this.defenseValueValueInput = $("<input id='defenseValueValueInput' type='number' min='0' max='12' class='filterInput' style='width: 30px;'>");

        this.maneuverLabel = $("<label for='maneuverCompareSelect' class='filterLabel'>Maneuver:</label>");
        this.maneuverCompareSelect = $("<select id='maneuverCompareSelect' class='filterInput'>"
            + "<option value='' selected='selected'>Any</option>"
            + "<option value='EQUALS'>=</option>"
            + "<option value='GREATER_THAN'>></option>"
            + "<option value='GREATER_THAN_OR_EQUAL_TO'>>=</option>"
            + "<option value='LESS_THAN'><</option>"
            + "<option value='LESS_THAN_OR_EQUAL_TO'><=</option>"
            + "</select>");
        this.maneuverValueInput = $("<input id='maneuverValueInput' type='number' min ='0' max='6' class='filterInput' style='width: 30px;'>");

        this.hyperspeedLabel = $("<label for='hyperspeedCompareSelect' class='filterLabel'>Hyperspeed:</label>");
        this.hyperspeedCompareSelect = $("<select id='hyperspeedCompareSelect' class='filterInput'>"
            + "<option value='' selected='selected'>Any</option>"
            + "<option value='EQUALS'>=</option>"
            + "<option value='GREATER_THAN'>></option>"
            + "<option value='GREATER_THAN_OR_EQUAL_TO'>>=</option>"
            + "<option value='LESS_THAN'><</option>"
            + "<option value='LESS_THAN_OR_EQUAL_TO'><=</option>"
            + "</select>");
        this.hyperspeedValueInput = $("<input id='hyperspeedValueInput' type='number' min='0' max='7' class='filterInput' style='width: 30px;'>");

        this.landspeedLabel = $("<label for='landspeedCompareSelect' class='filterLabel'>Landspeed:</label>");
        this.landspeedCompareSelect = $("<select id='landspeedCompareSelect' class='filterInput'>"
            + "<option value='' selected='selected'>Any</option>"
            + "<option value='EQUALS'>=</option>"
            + "<option value='GREATER_THAN'>></option>"
            + "<option value='GREATER_THAN_OR_EQUAL_TO'>>=</option>"
            + "<option value='LESS_THAN'><</option>"
            + "<option value='LESS_THAN_OR_EQUAL_TO'><=</option>"
            + "</select>");
        this.landspeedValueInput = $("<input id='landspeedValueInput' type='number' min='0' max='6' class='filterInput' style='width: 30px;'>");

        this.resetAdvancedFiltersButton = $("<button id='resetAdvancedFiltersButton' class='ui-button-icon-primary'>Reset advanced filters only</button>")
        this.resetAllFiltersButton = $("<button id='resetAllFiltersButton' class='ui-button-icon-primary'>Reset all filters</button>")

        var filterTable = $("<table id='filterTable' />");
        filterTable.append('<tr><td>' + this.productLabel[0].outerHTML + '</td><td>' + this.productSelect[0].outerHTML + '</td><td>'
                                      + this.rarityLabel[0].outerHTML + '</td><td>' + this.raritySelect[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.sideLabel[0].outerHTML + '</td><td>' + this.sideSelect[0].outerHTML + '</td><td>'
                                      + this.sortLabel[0].outerHTML + '</td><td>' + this.sortSelect[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.formatLabel[0].outerHTML + '</td><td colspan="3">' + this.formatSelect[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.setLabel[0].outerHTML + '</td><td colspan="3">' + this.setSelect[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.cardTypeLabel[0].outerHTML + '</td><td colspan="3">' + this.cardTypeSelect[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td colspan="4"><div class="cardFilterText">Advanced filters</div></td></tr>');
        filterTable.append('<tr><td>' + this.nameLabel[0].outerHTML + '</td><td colspan="3">' + this.nameInput[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.loreLabel[0].outerHTML + '</td><td colspan="3">' + this.loreInput[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.gametextLabel[0].outerHTML + '</td><td colspan="3">' + this.gametextInput[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.iconLabel[0].outerHTML + '</td><td>' + this.iconSelect[0].outerHTML + '</td><td>'
                            + this.destinyLabel[0].outerHTML + '</td><td>' + this.destinyCompareSelect[0].outerHTML + this.destinyValueInput[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.personaLabel[0].outerHTML + '</td><td>' + this.personaSelect[0].outerHTML + '</td><td>'
                            + this.powerLabel[0].outerHTML + '</td><td>' + this.powerCompareSelect[0].outerHTML + this.powerValueInput[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.abilityLabel[0].outerHTML + '</td><td>' + this.abilityCompareSelect[0].outerHTML + this.abilityValueInput[0].outerHTML
                            + '</td><td>' + this.deployLabel[0].outerHTML + '</td><td>' + this.deployCompareSelect[0].outerHTML + this.deployValueInput[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.forfeitLabel[0].outerHTML + '</td><td>' + this.forfeitCompareSelect[0].outerHTML + this.forfeitValueInput[0].outerHTML
                            + '</td><td>' + this.armorLabel[0].outerHTML + '</td><td>' + this.armorCompareSelect[0].outerHTML + this.armorValueInput[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.defenseValueLabel[0].outerHTML + '</td><td>' + this.defenseValueCompareSelect[0].outerHTML + this.defenseValueValueInput[0].outerHTML
                            + '</td><td>' + this.maneuverLabel[0].outerHTML + '</td><td>' + this.maneuverCompareSelect[0].outerHTML + this.maneuverValueInput[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td>' + this.hyperspeedLabel[0].outerHTML + '</td><td>' + this.hyperspeedCompareSelect[0].outerHTML + this.hyperspeedValueInput[0].outerHTML
                            + '</td><td>' + this.landspeedLabel[0].outerHTML + '</td><td>' + this.landspeedCompareSelect[0].outerHTML + this.landspeedValueInput[0].outerHTML + '</td></tr>');
        filterTable.append('<tr><td colspan="4"><div class="buttonHolder">' + this.resetAdvancedFiltersButton[0].outerHTML + this.resetAllFiltersButton[0].outerHTML + '</div></td></tr>');

        this.fullFilterDiv.append(filterTable);
        elem.append(this.fullFilterDiv);

        // Callback function for when the product filter is changed
        var productChanged = function () {
            var isPackOrBox = ($("#productSelect option:selected").prop("value") == "pack");

            $("#sideSelect").prop("disabled", isPackOrBox);
            $("#formatSelect").prop("disabled", isPackOrBox);
            $("#setSelect").prop("disabled", isPackOrBox);
            $("#cardTypeSelect").prop("disabled", isPackOrBox);
            $("#raritySelect").prop("disabled", isPackOrBox);
            $("#sortSelect").prop("disabled", isPackOrBox);
            $("#nameInput").prop("hidden", isPackOrBox);
            $("#loreInput").prop("hidden", isPackOrBox);
            $("#gametextInput").prop("hidden", isPackOrBox);
            $("#iconSelect").prop("disabled", isPackOrBox);
            $("#personaSelect").prop("disabled", isPackOrBox);
            $("#destinyCompareSelect").prop("disabled", isPackOrBox);
            $("#destinyValueInput").prop("hidden", isPackOrBox);
            $("#powerCompareSelect").prop("disabled", isPackOrBox);
            $("#powerValueInput").prop("hidden", isPackOrBox);
            $("#abilityCompareSelect").prop("disabled", isPackOrBox);
            $("#abilityValueInput").prop("hidden", isPackOrBox);
            $("#deployCompareSelect").prop("disabled", isPackOrBox);
            $("#deployValueInput").prop("hidden", isPackOrBox);
            $("#forfeitCompareSelect").prop("disabled", isPackOrBox);
            $("#forfeitValueInput").prop("hidden", isPackOrBox);
            $("#armorCompareSelect").prop("disabled", isPackOrBox);
            $("#armorValueInput").prop("hidden", isPackOrBox);
            $("#defenseValueCompareSelect").prop("disabled", isPackOrBox);
            $("#defenseValueValueInput").prop("hidden", isPackOrBox);
            $("#maneuverCompareSelect").prop("disabled", isPackOrBox);
            $("#maneuverValueInput").prop("hidden", isPackOrBox);
            $("#hyperspeedCompareSelect").prop("disabled", isPackOrBox);
            $("#hyperspeedValueInput").prop("hidden", isPackOrBox);
            $("#landspeedCompareSelect").prop("disabled", isPackOrBox);
            $("#landspeedValueInput").prop("hidden", isPackOrBox);
            $("#resetAdvancedFiltersButton").prop("disabled", isPackOrBox);
            $("#resetAllFiltersButton").prop("disabled", isPackOrBox);

            return compareTypeChanged();
        };

        // Callback function for when a filter compare is changed
        var compareTypeChanged = function () {
            if ($("#destinyCompareSelect option:selected").prop("value") == "") {
                $("#destinyValueInput").prop("hidden", true);
                $("#destinyValueInput").prop("value", '');
            }
            else {
                $("#destinyValueInput").prop("hidden", false);
            }

            if ($("#powerCompareSelect option:selected").prop("value") == "") {
                $("#powerValueInput").prop("hidden", true);
                $("#powerValueInput").prop("value", '');
            }
            else {
                $("#powerValueInput").prop("hidden", false);
            }

            if ($("#abilityCompareSelect option:selected").prop("value") == "") {
                $("#abilityValueInput").prop("hidden", true);
                $("#abilityValueInput").prop("value", '');
            }
            else {
                $("#abilityValueInput").prop("hidden", false);
            }

            if ($("#deployCompareSelect option:selected").prop("value") == "") {
                $("#deployValueInput").prop("hidden", true);
                $("#deployValueInput").prop("value", '');
            }
            else {
                $("#deployValueInput").prop("hidden", false);
            }

            if ($("#forfeitCompareSelect option:selected").prop("value") == "") {
                $("#forfeitValueInput").prop("hidden", true);
                $("#forfeitValueInput").prop("value", '');
            }
            else {
                $("#forfeitValueInput").prop("hidden", false);
            }

            if ($("#armorCompareSelect option:selected").prop("value") == "") {
                $("#armorValueInput").prop("hidden", true);
                $("#armorValueInput").prop("value", '');
            }
            else {
                $("#armorValueInput").prop("hidden", false);
            }

            if ($("#defenseValueCompareSelect option:selected").prop("value") == "") {
                $("#defenseValueValueInput").prop("hidden", true);
                $("#defenseValueValueInput").prop("value", '');
            }
            else {
                $("#defenseValueValueInput").prop("hidden", false);
            }

            if ($("#maneuverCompareSelect option:selected").prop("value") == "") {
                $("#maneuverValueInput").prop("hidden", true);
                $("#maneuverValueInput").prop("value", '');
            }
            else {
                $("#maneuverValueInput").prop("hidden", false);
            }

            if ($("#hyperspeedCompareSelect option:selected").prop("value") == "") {
                $("#hyperspeedValueInput").prop("hidden", true);
                $("#hyperspeedValueInput").prop("value", '');
            }
            else {
                $("#hyperspeedValueInput").prop("hidden", false);
            }

            if ($("#landspeedCompareSelect option:selected").prop("value") == "") {
                $("#landspeedValueInput").prop("hidden", true);
                $("#landspeedValueInput").prop("value", '');
            }
            else {
                $("#landspeedValueInput").prop("hidden", false);
            }

            return filterChanged();
        };

        // Callback function for when a filter is changed
        var filterChanged = function () {
            var newFilterString = that.calculateDeckFilter();
            // Only re-gather collection if filter actually changed from previous filter
            if (that.filter != newFilterString) {
                that.filter = newFilterString
                that.start = 0;
                that.getCollection();
            }
            return true;
        };

        // Hide initially hidden fields
        $("#destinyValueInput").prop("hidden", true);
        $("#powerValueInput").prop("hidden", true);
        $("#abilityValueInput").prop("hidden", true);
        $("#deployValueInput").prop("hidden", true);
        $("#forfeitValueInput").prop("hidden", true);
        $("#armorValueInput").prop("hidden", true);
        $("#defenseValueValueInput").prop("hidden", true);
        $("#maneuverValueInput").prop("hidden", true);
        $("#hyperspeedValueInput").prop("hidden", true);
        $("#landspeedValueInput").prop("hidden", true);

        // Reset buttons
        $("#resetAdvancedFiltersButton").click(
                function (event) {
                    $('#nameInput, #loreInput, #gametextInput, #iconSelect, #personaSelect, #destinyCompareSelect, #destinyValueInput' +
                      ', #powerCompareSelect, #powerValueInput, #abilityCompareSelect, #abilityValueInput, #deployCompareSelect' +
                      ', #deployValueInput, #forfeitCompareSelect, #forfeitValueInput, #armorCompareSelect, #armorValueInput' +
                      ', #defenseValueCompareSelect, #defenseValueValueInput, #maneuverCompareSelect, #maneuverValueInput' +
                      ', #hyperspeedCompareSelect, #hyperspeedValueInput, #landspeedCompareSelect, #landspeedValueInput').val('').trigger('change');
                });
        $("#resetAllFiltersButton").click(
                function (event) {
                    $('#sideSelect, #formatSelect, #setSelect, #cardTypeSelect, #raritySelect, #nameInput, #loreInput, #gametextInput' +
                      ', #iconSelect, #personaSelect, #destinyCompareSelect, #destinyValueInput' +
                      ', #powerCompareSelect, #powerValueInput, #abilityCompareSelect, #abilityValueInput, #deployCompareSelect' +
                      ', #deployValueInput, #forfeitCompareSelect, #forfeitValueInput, #armorCompareSelect, #armorValueInput' +
                      ', #defenseValueCompareSelect, #defenseValueValueInput, #maneuverCompareSelect, #maneuverValueInput' +
                      ', #hyperspeedCompareSelect, #hyperspeedValueInput, #landspeedCompareSelect, #landspeedValueInput').val('').trigger('change');
                });

        // Triggers for filter fields changed
        $("#productSelect").change(productChanged);

        $("#sideSelect, #formatSelect, #setSelect, #cardTypeSelect, #raritySelect, #sortSelect, #nameInput, #loreInput, #gametextInput" +
          ", #iconSelect, #personaSelect, #destinyValueInput, #powerValueInput, #abilityValueInput, #deployValueInput" +
          ", #forfeitValueInput, #armorValueInput, #defenseValueValueInput, #maneuverValueInput" +
          ", #hyperspeedValueInput, #landspeedValueInput").change(filterChanged);

        $("#destinyCompareSelect, #powerCompareSelect, #abilityCompareSelect, #deployCompareSelect" +
          ", #forfeitCompareSelect, #armorCompareSelect, #defenseValueCompareSelect, #maneuverCompareSelect" +
          ", #hyperspeedCompareSelect, #landspeedCompareSelect").change(compareTypeChanged);
    },

    layoutUi:function (x, y, width, height) {
        this.pageDiv.css({ position:"absolute", left:x, top:y, width:width, height:40 });
        this.countSlider.css({width:width - 100});
        this.fullFilterDiv.css({position:"absolute", left:x, top:y + 40, width:width, height:335});
    },

    layoutPageUi: function(x, y, width) {
        this.pageDiv.css({ left:x, top:y, width:width, height:40 });
        this.countSlider.css({width:width - 100});
    },

    disableNavigation:function () {
        this.previousPageBut.button("option", "disabled", true);
        this.nextPageBut.button("option", "disabled", true);
        this.countSlider.button("option", "disabled", true);
    },

    calculateDeckFilter:function () {

        // Generates the filter string based on the current filter selections

        var side = $("#sideSelect option:selected").prop("value");
        if (side != "")
            side = " side:" + side;

        var format = $("#formatSelect option:selected").prop("value");
        if (format != "")
            format = " format:" + format;

        var set = $("#setSelect option:selected").prop("value");
        if (set != "")
            set = " set:" + set;

        var cardType = $("#cardTypeSelect option:selected").prop("value");
        if (cardType != "")
            cardType = " cardType:" + cardType;

        var rarity = $("#raritySelect option:selected").prop("value");
        if (rarity != "")
            rarity = " rarity:" + rarity;

        var sort = $("#sortSelect option:selected").prop("value");
        if (sort != "")
            sort = " sort:" + sort;

        var product = $("#productSelect option:selected").prop("value");
        if (product != "")
            product = " product:" + product;

        var name = $("#nameInput").prop("value");
        if (name != "") {
            var nameElems = name.split(" ");
            name = "";
            for (var i = 0; i < nameElems.length; i++)
                name += " name:" + nameElems[i];
        }

        var lore = $("#loreInput").prop("value");
        if (lore != "") {
            var loreElems = lore.split(" ");
            lore = "";
            for (var i = 0; i < loreElems.length; i++)
                lore += " lore:" + loreElems[i];
        }

        var gametext = $("#gametextInput").prop("value");
        if (gametext != "") {
            var gametextElems = gametext.split(" ");
            gametext = "";
            for (var i = 0; i < gametextElems.length; i++)
                gametext += " gametext:" + gametextElems[i];
        }

        var icon = $("#iconSelect option:selected").prop("value");
        if (icon != "")
            icon = " icon:" + icon;

        var persona = $("#personaSelect option:selected").prop("value");
        if (persona != "")
            persona = " persona:" + persona;

        var destinyCompare = $("#destinyCompareSelect option:selected").prop("value");
        var destiny = $("#destinyValueInput").prop("value");
        if (destinyCompare != "" && destiny != "") {
            destinyCompare = " destinyCompare:" + destinyCompare;
            destiny = " destiny:" + destiny;
        }
        else {
            destinyCompare = "";
            destiny = "";
        }

        var powerCompare = $("#powerCompareSelect option:selected").prop("value");
        var power = $("#powerValueInput").prop("value");
        if (powerCompare != "" && power != "") {
            powerCompare = " powerCompare:" + powerCompare;
            power = " power:" + power;
        }
        else {
            powerCompare = "";
            power = "";
        }

        var abilityCompare = $("#abilityCompareSelect option:selected").prop("value");
        var ability = $("#abilityValueInput").prop("value");
        if (abilityCompare != "" && ability != "") {
            abilityCompare = " abilityCompare:" + abilityCompare;
            ability = " ability:" + ability;
        }
        else {
            abilityCompare = "";
            ability = "";
        }

        var deployCompare = $("#deployCompareSelect option:selected").prop("value");
        var deploy = $("#deployValueInput").prop("value");
        if (deployCompare != "" && deploy != "") {
            deployCompare = " deployCompare:" + deployCompare;
            deploy = " deploy:" + deploy;
        }
        else {
            deployCompare = "";
            deploy = "";
        }

        var forfeitCompare = $("#forfeitCompareSelect option:selected").prop("value");
        var forfeit = $("#forfeitValueInput").prop("value");
        if (forfeitCompare != "" && forfeit != "") {
            forfeitCompare = " forfeitCompare:" + forfeitCompare;
            forfeit = " forfeit:" + forfeit;
        }
        else {
            forfeitCompare = "";
            forfeit = "";
        }

        var armorCompare = $("#armorCompareSelect option:selected").prop("value");
        var armor = $("#armorValueInput").prop("value");
        if (armorCompare != "" && armor != "") {
            armorCompare = " armorCompare:" + armorCompare;
            armor = " armor:" + armor;
        }
        else {
            armorCompare = "";
            armor = "";
        }

        var defenseValueCompare = $("#defenseValueCompareSelect option:selected").prop("value");
        var defenseValue = $("#defenseValueValueInput").prop("value");
        if (defenseValueCompare != "" && defenseValue != "") {
            defenseValueCompare = " defenseValueCompare:" + defenseValueCompare;
            defenseValue = " defenseValue:" + defenseValue;
        }
        else {
            defenseValueCompare = "";
            defenseValue = "";
        }

        var maneuverCompare = $("#maneuverCompareSelect option:selected").prop("value");
        var maneuver = $("#maneuverValueInput").prop("value");
        if (maneuverCompare != "" && maneuver != "") {
            maneuverCompare = " maneuverCompare:" + maneuverCompare;
            maneuver = " maneuver:" + maneuver;
        }
        else {
            maneuverCompare = "";
            maneuver = "";
        }

        var hyperspeedCompare = $("#hyperspeedCompareSelect option:selected").prop("value");
        var hyperspeed = $("#hyperspeedValueInput").prop("value");
        if (hyperspeedCompare != "" && hyperspeed != "") {
            hyperspeedCompare = " hyperspeedCompare:" + hyperspeedCompare;
            hyperspeed = " hyperspeed:" + hyperspeed;
        }
        else {
            hyperspeedCompare = "";
            hyperspeed = "";
        }

        var landspeedCompare = $("#landspeedCompareSelect option:selected").prop("value");
        var landspeed = $("#landspeedValueInput").prop("value");
        if (landspeedCompare != "" && landspeed != "") {
            landspeedCompare = " landspeedCompare:" + landspeedCompare;
            landspeed = " landspeed:" + landspeed;
        }
        else {
            landspeedCompare = "";
            landspeed = "";
        }

        var filterString = side + format + set + cardType + rarity + sort + product + name + lore + gametext + icon + persona
                + destinyCompare + destiny + powerCompare + power + abilityCompare + ability + deployCompare
                + deploy + forfeitCompare + forfeit + armorCompare + armor + defenseValueCompare + defenseValue
                + maneuverCompare + maneuver + hyperspeedCompare + hyperspeed + landspeedCompare + landspeed;
        return filterString.trim();
    },

    getCollection:function () {
        var that = this;
        this.getCollectionFunc(this.filter, this.start, this.count, function (xml) {
            that.displayCollection(xml);
        });
    },

    displayCollection:function (xml) {
        log(xml);
        var root = xml.documentElement;

        this.clearCollectionFunc(root);

        var packs = root.getElementsByTagName("pack");
        for (var i = 0; i < packs.length; i++) {
            var packElem = packs[i];
            var blueprintId = packElem.getAttribute("blueprintId");
            var count = packElem.getAttribute("count");
            this.addCardFunc(packElem, "pack", blueprintId, null, null, count);
        }

        var cards = root.getElementsByTagName("card");
        for (var i = 0; i < cards.length; i++) {
            var cardElem = cards[i];
            var blueprintId = cardElem.getAttribute("blueprintId");
            var testingText = cardElem.getAttribute("testingText");
            var backSideTestingText = cardElem.getAttribute("backSideTestingText");
            var count = cardElem.getAttribute("count");
            this.addCardFunc(cardElem, "card", blueprintId, testingText, backSideTestingText, count);
        }

        this.finishCollectionFunc();

        $("#previousPage").button("option", "disabled", this.start == 0);
        var cnt = parseInt(root.getAttribute("count"));
        $("#nextPage").button("option", "disabled", (this.start + this.count) >= cnt);
        $("#countSlider").slider("option", "disabled", false);
    }
});
