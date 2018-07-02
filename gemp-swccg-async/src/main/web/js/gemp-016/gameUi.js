var GempSwccgGameUI = Class.extend({
    padding:1,

    gameUiInitialized:false,

    bottomPlayerId:null,
    bottomPlayerIndex:null,
    replayMode:null,
    spectatorMode:null,

    currentPlayerId:null,
    allPlayerIds:null,

    cardActionDialog:null,
    smallDialog:null,
    gameStateElem:null,
    alertBox:null,
    alertText:null,
    alertTextMsg:null,
    alertButtons:null,
    mouseInAlertBox:false,
    infoDialog:null,

    sideOfTableOpponent:null,
    sideOfTablePlayer:null,

    hand:null,
    sabaccHand:null,
    showSabaccHand:false,
    revealedSabaccHandPlayer:null,
    revealedSabaccHandOpponent:null,
    showRevealedSabaccHands:false,
    darkSabaccHandTotal:null,
    lightSabaccHandTotal:null,
    darkRevealedSabaccHandTotal:null,
    lightRevealedSabaccHandTotal:null,

    extraActionsGroup:null,
    specialGroup:null,
    topOfReserveDeckPlayer:null,
    topOfForcePilePlayer:null,
    topOfUsedPilePlayer:null,
    topOfLostPilePlayer:null,
    topOfDrawnDestinyPlayer:null,
    topOfReserveDeckOpponent:null,
    topOfForcePileOpponent:null,
    topOfUsedPileOpponent:null,
    topOfLostPileOpponent:null,
    topOfDrawnDestinyOpponent:null,
    extraActionsGroupOpponent:null,

    locationDivs:null,
    locationCardGroups:null,
    playerSideOfLocationGroups:null,
    opponentSideOfLocationGroups:null,
    darkPowerAtLocationDivs:null,
    lightPowerAtLocationDivs:null,
    outOfPlayPileDialogs:null,
    outOfPlayPileGroups:null,

    zoomedInLocationIndex:null,
    clickToZoomLocationDisabled:false,

    battleLocationIndex:null,
    battleGroupDiv:null,
    playerInBattleGroups:null,
    opponentInBattleGroups:null,
    darkBattlePowerDiv:null,
    lightBattlePowerDiv:null,
    darkBattleNumDestinyToPowerDiv:null,
    lightBattleNumDestinyToPowerDiv:null,
    darkBattleNumBattleDestinyDiv:null,
    lightBattleNumBattleDestinyDiv:null,
    darkBattleNumDestinyToAttritionDiv:null,
    lightBattleNumDestinyToAttritionDiv:null,
    darkBattleDamageRemainingDiv:null,
    lightBattleDamageRemainingDiv:null,
    darkBattleAttritionRemainingDiv:null,
    lightBattleAttritionRemainingDiv:null,

    attackLocationIndex:null,
    playerIdAttacking:null,
    playerIdDefending:null,
    attackGroupDiv:null,
    attackingInAttackGroups:null,
    defendingInAttackGroups:null,
    attackingPowerOrFerocityInAttackDiv:null,
    defendingPowerOrFerocityInAttackDiv:null,
    attackingNumDestinyInAttackDiv:null,
    defendingNumDestinyInAttackDiv:null,

    duelOrLightsaberCombatLocationIndex:null,
    duelOrLightsaberCombatGroupDiv:null,
    playerInDuelOrLightsaberCombatGroups:null,
    opponentInDuelOrLightsaberCombatGroups:null,
    darkDuelOrLightsaberCombatTotalDiv:null,
    lightDuelOrLightsaberCombatTotalDiv:null,
    darkDuelOrLightsaberCombatNumDestinyDiv:null,
    lightDuelOrLightsaberCombatNumDestinyDiv:null,

    selectionFunction:null,
    decisionCountdownId: 0,
    decisionTimeoutFunction: null,
    isDecisionDuringYourTurn: false,
    decisionCountdownInProgress: false,
    decisionCountdownReadyToRestart: false,

    chatBoxDiv:null,
    chatBox:null,
    communication:null,
    channelNumber:null,

    settingsAutoAccept:false,
    settingsAlwaysDropDown:false,
    settingsAutoPassYourTurnEnabled:false,
    settingsAutoPassYourTurnCountdown:5,
    settingsAutoPassOpponentsTurnEnabled:true,
    settingsAutoPassOpponentsTurnCountdown:2,
    settingsMimicDecisionDelayEnabled:false,
    settingsMimicDecisionDelayTime:1,
    settingsCardActionsSilent: false,

    windowWidth:null,
    windowHeight:null,

    tabPane:null,

    animations:null,
    replayPlay: false,

    init:function (url, replayMode) {
        this.replayMode = replayMode;

        log("ui initialized");
        var that = this;

        this.animations = new GameAnimations(this);

        this.communication = new GempSwccgCommunication(url,
                function (xhr, ajaxOptions, thrownError) {
                    if (!that.replayMode && thrownError != "abort") {
                        if (xhr != null) {
                            if (xhr.status == 401) {
                                that.chatBox.appendMessage("Game problem - You're not logged in, go to the <a href='index.html'>main page</a> to log in", "warningMessage");
                                return;
                            } else {
                                that.chatBox.appendMessage("There was a problem communicating with the server (" + xhr.status + "), if the game is finished, it has been removed, otherwise you have lost connection to the server.", "warningMessage");
                                that.chatBox.appendMessage("Refresh the page (press F5) to resume the game, or press back on your browser to get back to the Game Hall.", "warningMessage");
                                return;
                            }
                        }
                        that.chatBox.appendMessage("There was a problem communicating with the server, if the game is finished, it has been removed, otherwise you have lost connection to the server.", "warningMessage");
                        that.chatBox.appendMessage("Refresh the page (press F5) to resume the game, or press back on your browser to get back to the Game Hall.", "warningMessage");
                    }
                });

        $.expr[':'].cardId = function (obj, index, meta, stack) {
            var cardIds = meta[3].split(",");
            var cardData = $(obj).data("card");
            return (cardData != null && ($.inArray(cardData.cardId, cardIds) > -1));
        };

         if (this.replayMode) {
             var replayDiv = $("<div class='replay' style='position:absolute'></div>");
             var slowerBut = $("<button id='slowerButton'>Slower</button>").button({ icons: {primary:'ui-icon-triangle-1-w'}, text: false});
             var fasterBut = $("<button id='fasterButton'>Faster</button>").button({ icons: {primary:'ui-icon-triangle-1-e'}, text: false});
             slowerBut.click(
                     function() {
                         that.animations.replaySpeed = Math.min(2, that.animations.replaySpeed + 0.2);
                     });
             fasterBut.click(
                     function() {
                         that.animations.replaySpeed = Math.max(0.2, that.animations.replaySpeed - 0.2);
                     });
             replayDiv.append(slowerBut);
             replayDiv.append(fasterBut);
             replayDiv.append("<br/>");

             var replayBut = $("<img id='replayButton' src='images/play.png' width='64' height='64'>").button();
             replayDiv.append(replayBut);

             $("#main").append(replayDiv);
             replayDiv.css({"z-index":1000});
         }

        this.locationDivs = new Array();
        this.locationCardGroups = new Array();
        this.playerSideOfLocationGroups = new Array();
        this.opponentSideOfLocationGroups = new Array();
        this.darkPowerAtLocationDivs = new Array();
        this.lightPowerAtLocationDivs = new Array();
        this.playerInBattleGroups = new Array();
        this.opponentInBattleGroups = new Array();
        this.attackingInAttackGroups = new Array();
        this.defendingInAttackGroups = new Array();
        this.playerInDuelOrLightsaberCombatGroups = new Array();
        this.opponentInDuelOrLightsaberCombatGroups = new Array();
        this.outOfPlayPileDialogs = {};
        this.outOfPlayPileGroups = {};

        this.initializeDialogs();

        this.addBottomLeftTabPane();
   },

    initializeGameUI:function () {

        var that = this;
        $('div').not('#main,.replay').remove();

        this.initializeDialogs();

        this.addBottomLeftTabPane();

        var playerSide = null;
        var opponentSide = null;
        if (that.bottomPlayerIndex == 0) {
            playerSide = "Dark";
            opponentSide = "Light";
        }
        else {
            playerSide = "Light";
            opponentSide = "Dark";
        }

        this.locationDivs = new Array();
        this.locationCardGroups = new Array();
        this.playerSideOfLocationGroups = new Array();
        this.opponentSideOfLocationGroups = new Array();
        this.darkPowerAtLocationDivs = new Array();
        this.lightPowerAtLocationDivs = new Array();
        this.playerInBattleGroups = new Array();
        this.opponentInBattleGroups = new Array();
        this.attackingInAttackGroups = new Array();
        this.defendingInAttackGroups = new Array();
        this.playerInDuelOrLightsaberCombatGroups = new Array();
        this.opponentInDuelOrLightsaberCombatGroups = new Array();
        this.outOfPlayPileDialogs = {};
        this.outOfPlayPileGroups = {};

        for (var i = 0; i < this.allPlayerIds.length; i++) {
            var outOfPlayPileDialog = $("<div></div>").dialog({
                autoOpen:false,
                closeOnEscape:true,
                resizable:true,
                title:"Out of play - " + this.allPlayerIds[i],
                minHeight:80,
                minWidth:200,
                width:600,
                height:300
            });
            this.outOfPlayPileDialogs[this.allPlayerIds[i]] = outOfPlayPileDialog;
            this.outOfPlayPileGroups[this.allPlayerIds[i]] = new NormalCardGroup(outOfPlayPileDialog, function (card) {
                return true;
            }, false);

            this.outOfPlayPileGroups[this.allPlayerIds[i]].setBounds(this.padding, this.padding, 580 - 2 * (this.padding), 250 - 2 * (this.padding));

            outOfPlayPileDialog.bind("dialogresize", (function (dialog, index) {
                return function () {
                    that.dialogResize(dialog, that.outOfPlayPileGroups[that.allPlayerIds[index]]);
                }
            })(outOfPlayPileDialog, i));
        }

        this.sideOfTableOpponent = new NormalCardGroup($("#main"), function (card) {
            return ((card.zone == "SIDE_OF_TABLE" || card.zone == "SIDE_OF_TABLE_NOT_IN_PLAY" || card.zone == "SIDE_OF_TABLE_FACE_DOWN_NOT_IN_PLAY") && card.owner != that.bottomPlayerId);
        });

        this.sideOfTablePlayer = new NormalCardGroup($("#main"), function (card) {
            return ((card.zone == "SIDE_OF_TABLE" || card.zone == "SIDE_OF_TABLE_FACE_DOWN" || card.zone == "SIDE_OF_TABLE_FACE_DOWN_NOT_IN_PLAY") && card.owner == that.bottomPlayerId);
        });

        this.revealedSabaccHandOpponent = new NormalCardGroup($("#main"), function (card) {
            return (card.zone == "REVEALED_SABACC_HAND" && card.owner != that.bottomPlayerId);
        });

        this.revealedSabaccHandPlayer = new NormalCardGroup($("#main"), function (card) {
            return (card.zone == "REVEALED_SABACC_HAND" && card.owner == that.bottomPlayerId);
        });
        this.darkRevealedSabaccHandTotal = $("<div class='sabaccTotalDiv'></div>");
        $("#main").append(this.darkRevealedSabaccHandTotal);
        this.lightRevealedSabaccHandTotal = $("<div class='sabaccTotalDiv'></div>");
        $("#main").append(this.lightRevealedSabaccHandTotal);

        this.topOfReserveDeckPlayer = new CardPileGroup($("#main"), function (card) {
            if (card.zone == "TOP_OF_RESERVE_DECK" && card.owner == that.bottomPlayerId)
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").addClass("top" + playerSide + "ReserveDeck");
                 return true;
            }
            else
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").removeClass("top" + playerSide + "ReserveDeck");
                 return false;
            }
        });

        this.topOfForcePilePlayer = new CardPileGroup($("#main"), function (card) {
            if (card.zone == "TOP_OF_FORCE_PILE" && card.owner == that.bottomPlayerId)
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").addClass("top" + playerSide + "ForcePile");
                 return true;
            }
            else
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").removeClass("top" + playerSide + "ForcePile");
                 return false;
            }
        });

        this.topOfUsedPilePlayer = new CardPileGroup($("#main"), function (card) {
            if (card.zone == "TOP_OF_USED_PILE" && card.owner == that.bottomPlayerId)
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").addClass("top" + playerSide + "UsedPile");
                 return true;
            }
            else
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").removeClass("top" + playerSide + "UsedPile");
                 return false;
            }
        });

        this.topOfLostPilePlayer = new CardPileGroup($("#main"), function (card) {
            if (card.zone == "TOP_OF_LOST_PILE" && card.owner == that.bottomPlayerId)
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").addClass("top" + playerSide + "LostPile");
                 return true;
            }
            else
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").removeClass("top" + playerSide + "LostPile");
                 return false;
            }
        });

        this.topOfDrawnDestinyPlayer = new CardPileGroup($("#main"), function (card) {
            if (card.zone == "TOP_OF_UNRESOLVED_DESTINY_DRAW" && card.owner == that.bottomPlayerId)
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").addClass("top" + playerSide + "DrawnDestiny");
                 return true;
            }
            else
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").removeClass("top" + playerSide + "DrawnDestiny");
                 return false;
            }
        });

        this.topOfReserveDeckOpponent = new CardPileGroup($("#main"), function (card) {
            if (card.zone == "TOP_OF_RESERVE_DECK" && card.owner != that.bottomPlayerId)
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").addClass("top" + opponentSide + "ReserveDeck");
                 return true;
            }
            else
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").removeClass("top" + opponentSide + "ReserveDeck");
                 return false;
            }
        });

        this.topOfForcePileOpponent = new CardPileGroup($("#main"), function (card) {
            if (card.zone == "TOP_OF_FORCE_PILE" && card.owner != that.bottomPlayerId)
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").addClass("top" + opponentSide + "ForcePile");
                 return true;
            }
            else
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").removeClass("top" + opponentSide + "ForcePile");
                 return false;
            }
        });

        this.topOfUsedPileOpponent = new CardPileGroup($("#main"), function (card) {
            if (card.zone == "TOP_OF_USED_PILE" && card.owner != that.bottomPlayerId)
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").addClass("top" + opponentSide + "UsedPile");
                 return true;
            }
            else
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").removeClass("top" + opponentSide + "UsedPile");
                 return false;
            }
        });

        this.topOfLostPileOpponent = new CardPileGroup($("#main"), function (card) {
            if (card.zone == "TOP_OF_LOST_PILE" && card.owner != that.bottomPlayerId)
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").addClass("top" + opponentSide + "LostPile");
                 return true;
            }
            else
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").removeClass("top" + opponentSide + "LostPile");
                 return false;
            }
        });

        this.topOfDrawnDestinyOpponent = new CardPileGroup($("#main"), function (card) {
            if (card.zone == "TOP_OF_UNRESOLVED_DESTINY_DRAW" && card.owner != that.bottomPlayerId)
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").addClass("top" + opponentSide + "DrawnDestiny");
                 return true;
            }
            else
            {
                 $(".card:cardId(" + card.cardId + ") > div.cardPileCount").removeClass("top" + opponentSide + "DrawnDestiny");
                 return false;
            }
        });

        this.extraActionsGroup = new CardPileGroup($("#main"), function (card) {
            return (card.zone == "EXTRA");
        }, true);

        this.darkSabaccHandTotal = $("<div class='sabaccTotalDiv'></div>");
        this.lightSabaccHandTotal = $("<div class='sabaccTotalDiv'></div>");

        if (!this.spectatorMode) {
            this.hand = new NormalCardGroup($("#main"), function (card) {
                return (card.zone == "HAND");
            });
            this.sabaccHand = new NormalCardGroup($("#main"), function (card) {
                return (card.zone == "SABACC_HAND");
            });
            $("#main").append(this.darkSabaccHandTotal);
            $("#main").append(this.lightSabaccHandTotal);
        }

        this.extraActionsGroupOpponent = new CardPileGroup($("#main"), function (card) {
            return (card.zone == "EXTRA_OPPONENT");
        }, true);

        this.specialGroup = new NormalCardGroup(this.cardActionDialog, function (card) {
            return (card.zone == "SPECIAL");
        }, false);
        this.specialGroup.setBounds(this.padding, this.padding, 580 - 2 * (this.padding), 250 - 2 * (this.padding));

        this.gameStateElem = $("<div class='ui-widget-content'></div>");
        this.gameStateElem.css({"border-radius":"0px"});

        for (var i = 0; i < this.allPlayerIds.length; i++) {
            var handClass = null;
            var sabaccHandClass = null;
            var outOfPlayClass = null;
            var forceGenerationClass = null;
            var raceTotalClass = null;
            if (i == 0) {
                handClass = "darkHandsize";
                sabaccHandClass = "darkSabaccHandsize";
                outOfPlayClass = "darkOutOfPlayPileSize";
                forceGenerationClass = "darkForceGeneration";
                raceTotalClass = "darkRaceTotal";
            }
            else {
                handClass = "lightHandsize";
                sabaccHandClass = "lightSabaccHandsize";
                outOfPlayClass = "lightOutOfPlayPileSize";
                forceGenerationClass = "lightForceGeneration";
                raceTotalClass = "lightRaceTotal";
            }

            this.gameStateElem.append("<div class='player'>" + (i + 1) + ". " + this.allPlayerIds[i] + "<div id='clock" + i + "' class='clock'></div><div class='phase'></div>"
                    + "<div class='playerStats'><div id='hand" + i + "' class='" + handClass + "'></div><div id='sabaccHand" + i + "' class='" + sabaccHandClass + "'></div><div id='showStats" + i + "' class='showStats'></div><div id='outOfPlay" + i + "' class='" + outOfPlayClass + "'></div><div id='forceGeneration" + i + "' class='" + forceGenerationClass + "'></div><div id='raceTotal" + i + "' class='" + raceTotalClass + "'></div></div></div>");
        }

        $("#main").append(this.gameStateElem);

//        for (var i = 0; i < this.allPlayerIds.length; i++) {
//            var showBut = $("<div class='slimButton'>+</div>").button().click(
//                    (function (playerIndex) {
//                        return function () {
//                            $(".player").each(
//                                    function (index) {
//                                        if (index == playerIndex) {
//                                            if ($(this).hasClass("opened")) {
//                                                $(this).removeClass("opened");
//                                                $("#outOfPlay" + playerIndex).css({display:"none"});
//                                                $("#forceGeneration" + playerIndex).css({display:"none"});
//                                            } else {
//                                                $(this).addClass("opened");
//                                                $("#outOfPlay" + playerIndex).css({display:"table-cell"});
//                                                $("#forceGeneration" + playerIndex).css({display:"table-cell"});
//                                            }
//                                        }
//                                    });
//                        };
//                    })(i));
//
//            $("#showStats" + i).append(showBut);
//        }

        for (var i = 0; i < this.allPlayerIds.length; i++) {
            $("#outOfPlay" + i).addClass("clickable").click(
                    (function (index) {
                        return function () {
                            openSizeDialog(that.outOfPlayPileDialogs[that.allPlayerIds[index]]);
                        };
                    })(i));
        }

        if (!this.spectatorMode) {
            this.alertBox = $("<div class='ui-widget-content'></div>");
            this.alertBox.css({"border-radius":"0px"});
            this.alertBox.mouseenter(
                       function (event) {
                            that.mouseInAlertBox = true;
                            if (!that.decisionCountdownInProgress && that.decisionCountdownReadyToRestart) {
                                that.startDecisionCountdown(that.isDecisionDuringYourTurn, that.decisionTimeoutFunction);
                            }
                       }).mouseleave(
                       function () {
                            that.mouseInAlertBox = false;
                            if (that.decisionCountdownInProgress) {
                                that.suspendDecisionCountdown();
                            }
                       });

            this.alertText = $("<div></div>");
            this.alertText.css({position:"absolute", left:"0px", top:"0px", width:"100%", height:"50px", scroll:"auto"});

            this.alertButtons = $("<div class='alertButtons'></div>");
            this.alertButtons.css({position:"absolute", left:"0px", bottom:"20px", width:"100%", height:"30px", scroll:"auto"});

            this.alertBox.append(this.alertText);
            this.alertBox.append(this.alertButtons);

            $("#main").append(this.alertBox);
        }

        var dragFunc = function (event) {
            return that.dragContinuesCardFunction(event);
        };

        $('body').unbind('click');
        $("body").click(
                function (event) {
                    return that.clickCardFunction(event);
                });
        $('body').unbind('mousedown');
        $("body").mousedown(
                function (event) {
                    $("body").bind("mousemove", dragFunc);
                    return that.dragStartCardFunction(event);
                });
        $('body').unbind('mouseup');
        $("body").mouseup(
                function (event) {
                    $("body").unbind("mousemove", dragFunc);
                    return that.dragStopCardFunction(event);
                });

        if (!this.gameUiInitialized && !this.spectatorMode && !this.replayMode) {
            var soundPlay = $("<embed src='/gemp-swccg/coolsaber.wav' hidden='true' autostart='true' loop='false' height='0' width='0'>");
            this.gameStateElem.append(soundPlay);
            setTimeout(
                function() {
                    soundPlay.remove();
                }, 5000);
        }

        this.gameUiInitialized = true;
    },

    getReorganizableCardGroupForCardData:function (cardData) {

        for (var i=0; i<this.locationCardGroups.length; i++) {
            if (this.locationCardGroups[i].cardBelongs(cardData)) {
                return this.locationCardGroups[i];
            }
        }
        for (var i=0; i<this.playerSideOfLocationGroups.length; i++) {
            if (this.playerSideOfLocationGroups[i].cardBelongs(cardData)) {
                return this.playerSideOfLocationGroups[i];
            }
        }
        for (var i=0; i<this.opponentSideOfLocationGroups.length; i++) {
            if (this.opponentSideOfLocationGroups[i].cardBelongs(cardData)) {
                return this.opponentSideOfLocationGroups[i];
            }
        }
        for (var i=0; i<this.playerInBattleGroups.length; i++) {
            if (this.playerInBattleGroups[i].cardBelongs(cardData)) {
                return this.playerInBattleGroups[i];
            }
        }
        for (var i=0; i<this.opponentInBattleGroups.length; i++) {
            if (this.opponentInBattleGroups[i].cardBelongs(cardData)) {
                return this.opponentInBattleGroups[i];
            }
        }
        for (var i=0; i<this.attackingInAttackGroups.length; i++) {
            if (this.attackingInAttackGroups[i].cardBelongs(cardData)) {
                return this.attackingInAttackGroups[i];
            }
        }
        for (var i=0; i<this.defendingInAttackGroups.length; i++) {
            if (this.defendingInAttackGroups[i].cardBelongs(cardData)) {
                return this.defendingInAttackGroups[i];
            }
        }
        for (var i=0; i<this.playerInDuelOrLightsaberCombatGroups.length; i++) {
            if (this.playerInDuelOrLightsaberCombatGroups[i].cardBelongs(cardData)) {
                return this.playerInDuelOrLightsaberCombatGroups[i];
            }
        }
        for (var i=0; i<this.opponentInDuelOrLightsaberCombatGroups.length; i++) {
            if (this.opponentInDuelOrLightsaberCombatGroups[i].cardBelongs(cardData)) {
                return this.opponentInDuelOrLightsaberCombatGroups[i];
            }
        }
        if (this.sideOfTablePlayer.cardBelongs(cardData)) {
            return this.sideOfTablePlayer;
        }
        if (this.sideOfTableOpponent.cardBelongs(cardData)) {
            return this.sideOfTableOpponent;
        }
        if (this.topOfReserveDeckPlayer.cardBelongs(cardData)) {
            return this.topOfReserveDeckPlayer;
        }
        if (this.topOfForcePilePlayer.cardBelongs(cardData)) {
            return this.topOfForcePilePlayer;
        }
        if (this.topOfUsedPilePlayer.cardBelongs(cardData)) {
            return this.topOfUsedPilePlayer;
        }
        if (this.topOfLostPilePlayer.cardBelongs(cardData)) {
            return this.topOfLostPilePlayer;
        }
        if (this.topOfDrawnDestinyPlayer.cardBelongs(cardData)) {
            return this.topOfDrawnDestinyPlayer;
        }
        if (this.topOfReserveDeckOpponent.cardBelongs(cardData)) {
            return this.topOfReserveDeckOpponent;
        }
        if (this.topOfForcePileOpponent.cardBelongs(cardData)) {
            return this.topOfForcePileOpponent;
        }
        if (this.topOfUsedPileOpponent.cardBelongs(cardData)) {
            return this.topOfUsedPileOpponent;
        }
        if (this.topOfLostPileOpponent.cardBelongs(cardData)) {
            return this.topOfLostPileOpponent;
        }
        if (this.topOfDrawnDestinyOpponent.cardBelongs(cardData)) {
            return this.topOfDrawnDestinyOpponent;
        }
        if (this.hand != null) {
            if (this.hand.cardBelongs(cardData)) {
                return this.hand;
            }
        }
        if (this.sabaccHand != null) {
            if (this.sabaccHand.cardBelongs(cardData)) {
                return this.hand;
            }
        }
        if (this.revealedSabaccHandPlayer.cardBelongs(cardData)) {
            return this.revealedSabaccHandPlayer;
        }
        if (this.revealedSabaccHandOpponent.cardBelongs(cardData)) {
            return this.revealedSabaccHandOpponent;
        }

        return null;
    },

    layoutGroupWithCard:function (cardId) {
        var cardData = $(".card:cardId(" + cardId + ")").data("card");

        for (var i=0; i<this.locationCardGroups.length; i++) {
            if (this.locationCardGroups[i].cardBelongs(cardData)) {
                this.locationCardGroups[i].layoutCards();
                return;
            }
        }
        for (var i=0; i<this.playerSideOfLocationGroups.length; i++) {
            if (this.playerSideOfLocationGroups[i].cardBelongs(cardData)) {
                this.playerSideOfLocationGroups[i].layoutCards();
                return;
            }
        }
        for (var i=0; i<this.opponentSideOfLocationGroups.length; i++) {
            if (this.opponentSideOfLocationGroups[i].cardBelongs(cardData)) {
                this.opponentSideOfLocationGroups[i].layoutCards();
                return;
            }
        }
        for (var i=0; i<this.playerInBattleGroups.length; i++) {
            if (this.playerInBattleGroups[i].cardBelongs(cardData)) {
                this.playerInBattleGroups[i].layoutCards();
                return;
            }
        }
        for (var i=0; i<this.opponentInBattleGroups.length; i++) {
            if (this.opponentInBattleGroups[i].cardBelongs(cardData)) {
                this.opponentInBattleGroups[i].layoutCards();
                return;
            }
        }
        for (var i=0; i<this.attackingInAttackGroups.length; i++) {
            if (this.attackingInAttackGroups[i].cardBelongs(cardData)) {
                this.attackingInAttackGroups[i].layoutCards();
                return;
            }
        }
        for (var i=0; i<this.defendingInAttackGroups.length; i++) {
            if (this.defendingInAttackGroups[i].cardBelongs(cardData)) {
                this.defendingInAttackGroups[i].layoutCards();
                return;
            }
        }
        for (var i=0; i<this.playerInDuelOrLightsaberCombatGroups.length; i++) {
            if (this.playerInDuelOrLightsaberCombatGroups[i].cardBelongs(cardData)) {
                this.playerInDuelOrLightsaberCombatGroups[i].layoutCards();
                return;
            }
        }
        for (var i=0; i<this.opponentInDuelOrLightsaberCombatGroups.length; i++) {
            if (this.opponentInDuelOrLightsaberCombatGroups[i].cardBelongs(cardData)) {
                this.opponentInDuelOrLightsaberCombatGroups[i].layoutCards();
                return;
            }
        }
        if (this.sideOfTablePlayer.cardBelongs(cardData)) {
            this.sideOfTablePlayer.layoutCards();
            return;
        }
        if (this.sideOfTableOpponent.cardBelongs(cardData)) {
            this.sideOfTableOpponent.layoutCards();
            return;
        }
        if (this.topOfReserveDeckPlayer.cardBelongs(cardData)) {
            this.topOfReserveDeckPlayer.layoutCards();
            return;
        }
        if (this.topOfForcePilePlayer.cardBelongs(cardData)) {
            this.topOfForcePilePlayer.layoutCards();
            return;
        }
        if (this.topOfUsedPilePlayer.cardBelongs(cardData)) {
            this.topOfUsedPilePlayer.layoutCards();
            return;
        }
        if (this.topOfLostPilePlayer.cardBelongs(cardData)) {
            this.topOfLostPilePlayer.layoutCards();
            return;
        }
        if (this.topOfDrawnDestinyPlayer.cardBelongs(cardData)) {
            this.topOfDrawnDestinyPlayer.layoutCards();
            return;
        }
        if (this.topOfReserveDeckOpponent.cardBelongs(cardData)) {
            this.topOfReserveDeckOpponent.layoutCards();
            return;
        }
        if (this.topOfForcePileOpponent.cardBelongs(cardData)) {
            this.topOfForcePileOpponent.layoutCards();
            return;
        }
        if (this.topOfUsedPileOpponent.cardBelongs(cardData)) {
            this.topOfUsedPileOpponent.layoutCards();
            return;
        }
        if (this.topOfLostPileOpponent.cardBelongs(cardData)) {
            this.topOfLostPileOpponent.layoutCards();
            return;
        }
        if (this.topOfDrawnDestinyOpponent.cardBelongs(cardData)) {
            this.topOfDrawnDestinyOpponent.layoutCards();
            return;
        }
        if (this.hand != null) {
            if (this.hand.cardBelongs(cardData)) {
                this.hand.layoutCards();
                return;
            }
        }
        if (this.sabaccHand != null) {
            if (this.sabaccHand.cardBelongs(cardData)) {
                this.sabaccHand.layoutCards();
                return;
            }
        }
        if (this.revealedSabaccHandPlayer.cardBelongs(cardData)) {
            this.revealedSabaccHandPlayer.layoutCards();
            return;
        }
        if (this.revealedSabaccHandOpponent.cardBelongs(cardData)) {
            this.revealedSabaccHandOpponent.layoutCards();
            return;
        }

        this.layoutUI(false);
    },

    addBottomLeftTabPane:function () {
        var that = this;
        var tabsLabels = "<li><a href='#chatBox' class='slimTab'>Chat</a></li>";
        var tabsBodies = "<div id='chatBox' class='slimPanel'></div>";
        if (!this.spectatorMode && !this.replayMode) {
            tabsLabels += "<li><a href='#settingsBox' class='slimTab'>Settings</a></li><li><a href='#gameOptionsBox' class='slimTab'>Options</a></li>";
            tabsBodies += "<div id='settingsBox' class='slimPanel'></div><div id='gameOptionsBox' class='slimPanel'></div>";
        }
        if (!this.replayMode) {
            tabsLabels += "<li><a href='#playersInRoomBox' class='slimTab'>Players</a></li>";
            tabsBodies += "<div id='playersInRoomBox' class='slimPanel'></div>";
        }
        var tabsStr = "<div id='bottomLeftTabs' style='border-radius: 0px'><ul>" + tabsLabels + "</ul>" + tabsBodies + "</div>";

        this.tabPane = $(tabsStr).tabs();

        $("#main").append(this.tabPane);

        this.chatBoxDiv = $("#chatBox");

        if (!this.spectatorMode && !this.replayMode) {

            //
            // Auto-pass (during your turn)
            //
            $("#settingsBox").append("<input id='autoPassYourTurnEnabled' type='checkbox' value='' /><label for='autoPassYourTurnEnabled'>Enable auto-pass during your turn</label><br /><div class='indentedSetting'><input id='autoPassYourTurnCountdown' type='number' min = '1' max='10' value='5'/><label id='autoPassYourTurnCountdownLabel' for='autoPassYourTurnCountdown'>&nbsp;seconds</label></div><br />");

            var autoPassYourTurnEnabledCookie = $.cookie("autoPassYourTurnEnabled");
            if (autoPassYourTurnEnabledCookie != null && autoPassYourTurnEnabledCookie == "true") {
                $("#autoPassYourTurnEnabled").prop("checked", true);
                this.settingsAutoPassYourTurnEnabled = true;
            }
            else {
                $("#autoPassYourTurnEnabled").prop("checked", false);
                this.settingsAutoPassYourTurnEnabled = false;
            }
            $.cookie("autoPassYourTurnEnabled", "" + this.settingsAutoPassYourTurnEnabled, { expires:365 });

            $("#autoPassYourTurnEnabled").bind("change", function () {
                var selected = $("#autoPassYourTurnEnabled").prop("checked");
                that.settingsAutoPassYourTurnEnabled = selected;
                $("#autoPassYourTurnCountdown").prop("hidden", !that.settingsAutoPassYourTurnEnabled);
                $("#autoPassYourTurnCountdownLabel").prop("hidden", !that.settingsAutoPassYourTurnEnabled);
                $.cookie("autoPassYourTurnEnabled", "" + selected, { expires:365 });
            });

            $("#autoPassYourTurnCountdown").prop("hidden", !this.settingsAutoPassYourTurnEnabled);
            $("#autoPassYourTurnCountdownLabel").prop("hidden", !this.settingsAutoPassYourTurnEnabled);

            var autoPassYourTurnCountdownCookie = $.cookie("autoPassYourTurnCountdown");
            if (autoPassYourTurnCountdownCookie == null || !(autoPassYourTurnCountdownCookie >= 1 && autoPassYourTurnCountdownCookie <= 10)) {
                $("#autoPassYourTurnCountdown").prop("value", 5);
            }
            else {
                $("#autoPassYourTurnCountdown").prop("value", autoPassYourTurnCountdownCookie);
            }
            var autoPassYourTurnCountdown = $("#autoPassYourTurnCountdown").prop("value");
            if (!(autoPassYourTurnCountdown >= 1 && autoPassYourTurnCountdown <= 10)) {
                this.settingsAutoPassYourTurnCountdown = 5;
            }
            else {
                this.settingsAutoPassYourTurnCountdown = autoPassYourTurnCountdown;
            }
            $.cookie("autoPassYourTurnCountdown", "" + this.settingsAutoPassYourTurnCountdown, { expires:365 });

            $("#autoPassYourTurnCountdown").bind("change", function () {
                var autoPassYourTurnCountdown = $("#autoPassYourTurnCountdown").prop("value");
                if (!(autoPassYourTurnCountdown >= 1 && autoPassYourTurnCountdown <= 10)) {
                    that.settingsAutoPassYourTurnCountdown = 5;
                }
                else {
                    that.settingsAutoPassYourTurnCountdown = autoPassYourTurnCountdown;
                }
                $.cookie("autoPassYourTurnCountdown", "" + that.settingsAutoPassYourTurnCountdown, { expires:365 });
            });

            //
            // Auto-pass (during opponents turn)
            //
            $("#settingsBox").append("<input id='autoPassOpponentsTurnEnabled' type='checkbox' value='selected' /><label for='autoPassOpponentsTurnEnabled'>Enable auto-pass during opponent's turn</label><br /><div class='indentedSetting'><input id='autoPassOpponentsTurnCountdown' type='number' min = '1' max='10' value='5'/><label id='autoPassOpponentsTurnCountdownLabel' for='autoPassOpponentsTurnCountdown'>&nbsp;seconds</label></div><br />");

            var autoPassOpponentsTurnEnabledCookie = $.cookie("autoPassOpponentsTurnEnabled");
            if (autoPassOpponentsTurnEnabledCookie == null || autoPassOpponentsTurnEnabledCookie == "true") {
                $("#autoPassOpponentsTurnEnabled").prop("checked", true);
                this.settingsAutoPassOpponentsTurnEnabled = true;
            }
            else {
                $("#autoPassOpponentsTurnEnabled").prop("checked", false);
                this.settingsAutoPassOpponentsTurnEnabled = false;
            }
            $.cookie("autoPassOpponentsTurnEnabled", "" + this.settingsAutoPassOpponentsTurnEnabled, { expires:365 });

            $("#autoPassOpponentsTurnEnabled").bind("change", function () {
                var selected = $("#autoPassOpponentsTurnEnabled").prop("checked");
                that.settingsAutoPassOpponentsTurnEnabled = selected;
                $("#autoPassOpponentsTurnCountdown").prop("hidden", !that.settingsAutoPassOpponentsTurnEnabled);
                $("#autoPassOpponentsTurnCountdownLabel").prop("hidden", !that.settingsAutoPassOpponentsTurnEnabled);
                $.cookie("autoPassOpponentsTurnEnabled", "" + selected, { expires:365 });
            });

            $("#autoPassOpponentsTurnCountdown").prop("hidden", !this.settingsAutoPassOpponentsTurnEnabled);
            $("#autoPassOpponentsTurnCountdownLabel").prop("hidden", !this.settingsAutoPassOpponentsTurnEnabled);

            var autoPassOpponentsTurnCountdownCookie = $.cookie("autoPassOpponentsTurnCountdown");
            if (autoPassOpponentsTurnCountdownCookie == null || !(autoPassOpponentsTurnCountdownCookie >= 1 && autoPassOpponentsTurnCountdownCookie <= 10)) {
                $("#autoPassOpponentsTurnCountdown").prop("value", 5);
            }
            else {
                $("#autoPassOpponentsTurnCountdown").prop("value", autoPassOpponentsTurnCountdownCookie);
            }
            var autoPassOpponentsTurnCountdown = $("#autoPassOpponentsTurnCountdown").prop("value");
            if (!(autoPassOpponentsTurnCountdown >= 1 && autoPassOpponentsTurnCountdown <= 10)) {
                this.settingsAutoPassOpponentsTurnCountdown = 2;
            }
            else {
                this.settingsAutoPassOpponentsTurnCountdown = autoPassOpponentsTurnCountdown;
            }
            $.cookie("autoPassOpponentsTurnCountdown", "" + this.settingsAutoPassOpponentsTurnCountdown, { expires:365 });

            $("#autoPassOpponentsTurnCountdown").bind("change", function () {
                var autoPassOpponentsTurnCountdown = $("#autoPassOpponentsTurnCountdown").prop("value");
                if (!(autoPassOpponentsTurnCountdown >= 1 && autoPassOpponentsTurnCountdown <= 10)) {
                    that.settingsAutoPassOpponentsTurnCountdown = 5;
                }
                else {
                    that.settingsAutoPassOpponentsTurnCountdown = autoPassOpponentsTurnCountdown;
                }
                $.cookie("autoPassOpponentsTurnCountdown", "" + that.settingsAutoPassOpponentsTurnCountdown, { expires:365 });
            });

            //
            // Mimic decision delay (when no actions)
            //
            $("#settingsBox").append("<input id='mimicDecisionDelayEnabled' type='checkbox' value='selected' /><label for='mimicDecisionDelayEnabled'>Enable mimic decision delay</label><br /><div class='indentedSetting'><input id='mimicDecisionDelayTime' type='number' min = '1' max='10' value='2'/><label id='mimicDecisionDelayTimeLabel' for='mimicDecisionDelayTime'>&nbsp;seconds</label></div><br />");

            var mimicDecisionDelayEnabledCookie = $.cookie("mimicDecisionDelayEnabled");
            if (mimicDecisionDelayEnabledCookie == null || mimicDecisionDelayEnabledCookie == "false") {
                $("#mimicDecisionDelayEnabled").prop("checked", false);
                this.settingsMimicDecisionDelayEnabled = false;
            }
            else {
                $("#mimicDecisionDelayEnabled").prop("checked", true);
                this.settingsMimicDecisionDelayEnabled = true;
            }
            $.cookie("mimicDecisionDelayEnabled", "" + this.settingsMimicDecisionDelayEnabled, { expires:365 });

            $("#mimicDecisionDelayEnabled").bind("change", function () {
                var selected = $("#mimicDecisionDelayEnabled").prop("checked");
                that.settingsMimicDecisionDelayEnabled = selected;
                $("#mimicDecisionDelayTime").prop("hidden", !that.settingsMimicDecisionDelayEnabled);
                $("#mimicDecisionDelayTimeLabel").prop("hidden", !that.settingsMimicDecisionDelayEnabled);
                $.cookie("mimicDecisionDelayEnabled", "" + selected, { expires:365 });
            });

            $("#mimicDecisionDelayTime").prop("hidden", !this.settingsMimicDecisionDelayEnabled);
            $("#mimicDecisionDelayTimeLabel").prop("hidden", !this.settingsMimicDecisionDelayEnabled);

            var mimicDecisionDelayTimeCookie = $.cookie("mimicDecisionDelayTime");
            if (mimicDecisionDelayTimeCookie == null || !(mimicDecisionDelayTimeCookie >= 1 && mimicDecisionDelayTimeCookie <= 5)) {
                $("#mimicDecisionDelayTime").prop("value", 1);
            }
            else {
                $("#mimicDecisionDelayTime").prop("value", mimicDecisionDelayTimeCookie);
            }
            var mimicDecisionDelayTime = $("#mimicDecisionDelayTime").prop("value");
            if (!(mimicDecisionDelayTime >= 1 && mimicDecisionDelayTime <= 2)) {
                this.settingsMimicDecisionDelayTime = 1;
            }
            else {
                this.settingsMimicDecisionDelayTime = mimicDecisionDelayTime;
            }
            $.cookie("mimicDecisionDelayTime", "" + this.settingsMimicDecisionDelayTime, { expires:365 });

            $("#mimicDecisionDelayTime").bind("change", function () {
                var mimicDecisionDelayTime = $("#mimicDecisionDelayTime").prop("value");
                if (!(mimicDecisionDelayTime >= 1 && mimicDecisionDelayTime <= 2)) {
                    that.mimicDecisionDelayTimeCookie = 1;
                }
                else {
                    that.mimicDecisionDelayTimeCookie = mimicDecisionDelayTime;
                }
                $.cookie("mimicDecisionDelayTime", "" + that.mimicDecisionDelayTimeCookie, { expires:365 });
            });

            //
            // Auto-accept
            //
            $("#settingsBox").append("<input id='autoAccept' type='checkbox' value='selected' /><label for='autoAccept'>Auto-accept after selecting action or card</label><br />");

            var autoAccept = $.cookie("autoAccept");
            if (autoAccept == "true" || autoAccept == null) {
                $("#autoAccept").prop("checked", true);
                this.settingsAutoAccept = true;
            }

            $("#autoAccept").bind("change", function () {
                var selected = $("#autoAccept").prop("checked");
                that.settingsAutoAccept = selected;
                $.cookie("autoAccept", "" + selected, { expires:365 });
            });

            //
            // Always drop-down
            //
            $("#settingsBox").append("<input id='alwaysDropDown' type='checkbox' value='selected' /><label for='alwaysDropDown'>Always display drop-down in answer selection</label><br />");

            var alwaysDropDown = $.cookie("alwaysDropDown");
            if (alwaysDropDown == "true") {
                $("#alwaysDropDown").prop("checked", true);
                this.settingsAlwaysDropDown = true;
            }


            $("#alwaysDropDown").bind("change", function () {
                var selected = $("#alwaysDropDown").prop("checked");
                that.settingsAlwaysDropDown = selected;
                $.cookie("alwaysDropDown", "" + selected, { expires:365 });
            });


            //
            // Silent card actions
            //
            $("#settingsBox").append("<input id='cardActionsSilent' type='checkbox' value='selected' /><label for='cardActionsSilent'>Do not highlight cards with eligible actions</label><br />");

            var cardActionsSilent = $.cookie("cardActionsSilent");
            if (cardActionsSilent == "true") {
                $("#cardActionsSilent").prop("checked", true);
                this.settingsCardActionsSilent = true;
            }

            $("#cardActionsSilent").bind("change", function () {
                var selected = $("#cardActionsSilent").prop("checked");
                that.settingsCardActionsSilent = selected;
                $.cookie("cardActionsSilent", "" + selected, { expires:365 });
            });

            //$("#settingsBox").append("<br />Phases to auto-pass if no actions to perform<br />");
            //$("#settingsBox").append("<input id='autoPassACTIVATE' type='checkbox' value='selected' /><label for='autoPassACTIVATE'>Activate</label> ");
            //$("#settingsBox").append("<input id='autoPassCONTROL' type='checkbox' value='selected' /><label for='autoPassCONTROL'>Control</label> ");
            //$("#settingsBox").append("<input id='autoPassDEPLOY' type='checkbox' value='selected' /><label for='autoPassDEPLOY'>Deploy</label> ");
            //$("#settingsBox").append("<input id='autoPassBATTLE' type='checkbox' value='selected' /><label for='autoPassBATTLE'>Battle</label> ");
            //$("#settingsBox").append("<input id='autoPassMOVE' type='checkbox' value='selected' /><label for='autoPassMOVE'>Move</label> ");
            //$("#settingsBox").append("<input id='autoPassDRAW' type='checkbox' value='selected' /><label for='autoPassDRAW'>Draw</label> ");

            //var autoPassPhases = $.cookie("autoPassPhases");
            //if (autoPassPhases == null)
            //    autoPassPhases = "ACTIVATE0CONTROL0DEPLOY0BATTLE0MOVE0DRAW";

            //var passPhasesArr = autoPassPhases.split("0");
            //for (var i = 0; i < passPhasesArr.length; i++) {
            //    $("#autoPass" + passPhasesArr[i]).prop("checked", true);
            //}

            //$("#autoPassACTIVATE,#autoPassCONTROL,#autoPassDEPLOY,#autoPassBATTLE,#autoPassMOVE,#autoPassDRAW").bind("change", function () {
            //    var newAutoPassPhases = "";
            //    if ($("#autoPassACTIVATE").prop("checked"))
            //        newAutoPassPhases += "0ACTIVATE";
            //    if ($("#autoPassCONTROL").prop("checked"))
            //        newAutoPassPhases += "0CONTROL";
            //    if ($("#autoPassDEPLOY").prop("checked"))
            //        newAutoPassPhases += "0DEPLOY";
            //    if ($("#autoPassBATTLE").prop("checked"))
            //        newAutoPassPhases += "0BATTLE";
            //    if ($("#autoPassMOVE").prop("checked"))
            //        newAutoPassPhases += "0MOVE";
            //    if ($("#autoPassDRAW").prop("checked"))
            //        newAutoPassPhases += "0DRAW";
            //
            //    if (newAutoPassPhases.length > 0)
            //        newAutoPassPhases = newAutoPassPhases.substr(1);
            //    $.cookie("autoPassPhases", newAutoPassPhases, { expires:365 });
            //});
        }

        var playerListener = function (players) {
            var val = "";
            if (!this.replayMode) {
                for (var i = 0; i < players.length; i++)
                    val += players[i] + "<br/>";
                $("a[href='#playersInRoomBox']").html("Players(" + players.length + ")");
                $("#playersInRoomBox").html(val);
            }
        };

        var chatRoomName = (this.replayMode ? null : ("Game" + getUrlParam("gameId")));
        this.chatBox = new ChatBoxUI(chatRoomName, $("#chatBox"), this.communication.url, false, playerListener, false, true);
        this.chatBox.chatUpdateInterval = 3000;

        if (!this.spectatorMode && !this.replayMode) {
            $("#gameOptionsBox").append("<button id='concedeGame'>Concede game</button><br/>");
            $("#concedeGame").button().click(
                    function () {
                        that.communication.concede();
                    });
            $("#gameOptionsBox").append("<button id='cancelGame'>Request game cancel</button><br/>");
            $("#gameOptionsBox").append("<br/>");
            $("#cancelGame").button().click(
                    function () {
                        that.communication.cancel();
                    });
            $("#gameOptionsBox").append("<button id='gameTimerExtend30Min'>Request game timer +30min</button><br/>");
            $("#gameTimerExtend30Min").button().click(
                    function () {
                        that.communication.extendGameTimer(30);
                    });
            $("#gameOptionsBox").append("<button id='disableActionTimer'>Request action timer disabled</button>");
            $("#disableActionTimer").button().click(
                    function () {
                        that.communication.disableActionTimer();
                    });
        }
    },

    clickCardFunction:function (event) {
        this.suspendDecisionCountdown();

        var tar = $(event.target);
        if (tar.hasClass("cardHint")) {
            var blueprintId = tar.attr("value");
            var testingText = tar.attr("data-testingText");
            var backSideTestingText = tar.attr("data-backSideTestingText");
            var card = new Card(blueprintId, testingText, backSideTestingText, "SPECIAL", "hint", "");
            this.displayCard(card, false);
            event.stopPropagation();
            return false;
        }

        if (!this.successfulDrag && this.infoDialog.dialog("isOpen")) {
            this.infoDialog.dialog("close");
            event.stopPropagation();
            return false;
        }

        if (tar.hasClass("actionArea")) {
            var selectedCardElem = tar.closest(".card");
            if (!this.successfulDrag) {
                if (event.shiftKey || event.which > 1) {
                    this.displayCardInfo(selectedCardElem.data("card"));
                } else if ((selectedCardElem.hasClass("selectableCard") || selectedCardElem.hasClass("actionableCard") || selectedCardElem.hasClass("actionableCardSilent")) && !this.replayMode)
                    this.selectionFunction(selectedCardElem.data("card").cardId, event);
                event.stopPropagation();
            }
            return false;
        }

        if (!this.clickToZoomLocationDisabled && tar.hasClass("locationDiv")) {
            var locationIndex = tar.data("locationIndex");
            if (this.zoomedInLocationIndex == locationIndex) {
                this.zoomedInLocationIndex = null;
            }
            else {
                this.zoomedInLocationIndex = locationIndex;
            }
            event.stopPropagation();
            this.layoutUI(false);
            return false;
        }

        return true;
    },

    dragCardId:null,
    dragCardIndex:null,
    draggedCardIndex:null,
    dragStartX:null,
    dragStartY:null,
    successfulDrag:null,
    draggingHorizontaly:false,

    dragStartCardFunction:function (event) {
        this.successfulDrag = false;
        var tar = $(event.target);
        if (tar.hasClass("actionArea")) {
            var selectedCardElem = tar.closest(".card");
            if (event.which == 1) {
                var cardData = selectedCardElem.data("card");
                if (cardData) {
                    this.dragCardId = cardData.cardId;
                    this.dragStartX = event.clientX;
                    this.dragStartY = event.clientY;
                    return false;
                }
            }
        }
        return true;
    },

    dragContinuesCardFunction:function (event) {
        if (this.dragCardId != null) {
            if (!this.draggingHorizontaly && Math.abs(this.dragStartX - event.clientX) >= 20) {
                var cardElems = $(".card:cardId(" + this.dragCardId + ")");
                if (cardElems.length > 0) {
                    var cardElem = cardElems[0];
                    var cardData = $(cardElem).data("card");
                    this.draggingHorizontaly = true;
                    var cardGroup = this.getReorganizableCardGroupForCardData(cardData);
                    if (cardGroup != null) {
                        var cardsInGroup = cardGroup.getCardElems();
                        for (var i = 0; i < cardsInGroup.length; i++)
                            if (cardsInGroup[i].data("card").cardId == this.dragCardId) {
                                this.dragCardIndex = i;
                                this.draggedCardIndex = i;
                                break;
                            }
                    }
                }
            }
            if (this.draggingHorizontaly && this.dragCardId != null && this.dragCardIndex != null) {
                var cardElems = $(".card:cardId(" + this.dragCardId + ")");
                if (cardElems.length > 0) {
                    var cardElem = $(cardElems[0]);
                    var cardData = cardElem.data("card");
                    var cardGroup = this.getReorganizableCardGroupForCardData(cardData);
                    if (cardGroup != null) {
                        var cardsInGroup = cardGroup.getCardElems();
                        var width = cardElem.width();
                        var currentIndex;
                        if (event.clientX < this.dragStartX)
                            currentIndex = this.dragCardIndex - Math.floor((this.dragStartX - event.clientX) / width);
                        else
                            currentIndex = this.dragCardIndex + Math.floor((event.clientX - this.dragStartX) / width);

                        if (currentIndex < 0)
                            currentIndex = 0;
                        if (currentIndex >= cardsInGroup.length)
                            currentIndex = cardsInGroup.length - 1;

                        var cardIdAtIndex = $(cardsInGroup[currentIndex]).data("card").cardId;
                        if (cardIdAtIndex != this.dragCardId) {
                            //                            var sizeListeners = $(cardElem).data("sizeListeners");
                            if (currentIndex < this.draggedCardIndex)
                                $(".card:cardId(" + cardIdAtIndex + ")").before($(".card:cardId(" + this.dragCardId + ")"));
                            else
                                $(".card:cardId(" + cardIdAtIndex + ")").after($(".card:cardId(" + this.dragCardId + ")"));
                            //                            $(cardElem).data("card", cardData);
                            //                            $(cardElem).data("sizeListeners", sizeListeners);
                            cardGroup.layoutCards();
                            this.draggedCardIndex = currentIndex;
                        }
                    }
                }
            }
        }
    },

    dragStopCardFunction:function (event) {
        if (this.dragCardId != null) {
            if (this.dragStartY - event.clientY >= 20 && !this.draggingHorizontaly) {
                var cardElems = $(".card:cardId(" + this.dragCardId + ")");
                if (cardElems.length > 0) {
                    this.displayCardInfo($(cardElems[0]).data("card"));
                    this.successfulDrag = true;
                }
            }
            this.dragCardId = null;
            this.dragCardIndex = null;
            this.draggedCardIndex = null;
            this.dragStartX = null;
            this.dragStartY = null;
            this.draggingHorizontaly = false;
            return false;
        }
        return true;
    },

    displayCard:function (card, extraSpace) {
        var that = this;
        this.infoDialog.html("");
        this.infoDialog.html("<div style='scroll: auto'></div>");
        var floatCardDiv = $("<div style='float: left;'></div>");
        var showAsHorizontal = card.isHorizontal(card.bareBlueprint, card.zone);
        var cardDiv = createFullCardDiv(card.imageUrl, card.testingText, card.foil, showAsHorizontal);
        // Check if card div needs to be inverted
        this.infoDialog.cardImageRotation = 0;
        this.infoDialog.cardImageFlipped = false;
        if (card.inverted==true)
        {
            that.infoDialog.cardImageRotation = (that.infoDialog.cardImageRotation + 180) % 360;
            $(cardDiv).rotate(this.infoDialog.cardImageRotation);
        }
        $(cardDiv).click(
                function(event) {
                    // Check if need to show other card image if the image has two sides
                    if (card.backSideImageUrl != null && !card.backSideImageUrl.includes("CardBack")) {
                        that.infoDialog.cardImageFlipped = !that.infoDialog.cardImageFlipped;
                        if (that.infoDialog.cardImageFlipped) {
                            $(cardDiv).find("div.fullcard img").attr('src', card.backSideImageUrl);
                        }
                        else {
                            $(cardDiv).find("div.fullcard img").attr('src', card.imageUrl);
                        }
                    }
                    // Otherwise rotate the image
                    else {
                        that.infoDialog.cardImageRotation = (that.infoDialog.cardImageRotation + 180) % 360;
                        $(cardDiv).rotate(that.infoDialog.cardImageRotation);
                    }
                    event.stopPropagation();
                });
        floatCardDiv.append(cardDiv);

        this.infoDialog.append(floatCardDiv);
        if (extraSpace)
            this.infoDialog.append("<div id='cardEffects'></div>");

        var windowWidth = $(window).width();
        var windowHeight = $(window).height();

        var horSpace = (extraSpace ? 400 : 0) + 30;
        var vertSpace = 45;

        if (showAsHorizontal) {
            // 500x360
            this.infoDialog.dialog({width:Math.min(500 + horSpace, windowWidth), height:Math.min(360 + vertSpace, windowHeight)});
        } else {
            // 360x500
            this.infoDialog.dialog({width:Math.min(360 + horSpace, windowWidth), height:Math.min(500 + vertSpace, windowHeight)});
        }
        this.infoDialog.dialog("open");
    },

    displayCardInfo:function (card) {
        var showModifiers = false;
        var cardId = card.cardId;
        if (!this.replayMode && (cardId.length < 4 || cardId.substring(0, 4) != "temp"))
            showModifiers = true;

        this.displayCard(card, showModifiers);

        if (showModifiers)
            this.getCardModifiersFunction(cardId, this.setCardModifiers);
    },

    setCardModifiers:function (html) {
        $("#cardEffects").replaceWith(html);
    },

    initializeDialogs:function () {
        this.smallDialog = $("<div></div>")
                .dialog({
            autoOpen:false,
            closeOnEscape:false,
            resizable:true,
            width:425,
            height:275
        });

        this.cardActionDialog = $("<div></div>")
                .dialog({
            autoOpen:false,
            closeOnEscape:false,
            resizable:true,
            width:600,
            height:400
        });

        var that = this;

        this.cardActionDialog.bind("dialogresize", function () {
            that.arbitraryDialogResize();
        });

        $(".ui-dialog-titlebar-close").hide();

        var width = $(window).width();
        var height = $(window).height();

        this.infoDialog = $("<div></div>")
                .dialog({
            autoOpen:false,
            closeOnEscape:true,
            resizable:false,
            title:"Card information"
        });

        var swipeOptions = {
            threshold:20,
            swipeUp:function (event) {
                that.infoDialog.prop({ scrollTop:that.infoDialog.prop("scrollHeight") });
                return false;
            },
            swipeDown:function (event) {
                that.infoDialog.prop({ scrollTop:0 });
                return false;
            }
        };
        this.infoDialog.swipe(swipeOptions);
    },

    windowResized:function () {
        this.animations.windowResized();
    },

    // Performs the layout of the UI for the game.
    // sizeChanged is set to true if the size of the window was changed, otherwise false
    layoutUI:function (sizeChanged) {
        var width = $(window).width();
        var height = $(window).height();

        if (sizeChanged) {
            this.windowWidth = width;
            this.windowHeight = height;
        } else {
            width = this.windowWidth;
            height = this.windowHeight;
        }

        var BORDER_PADDING = 2;
        var LOCATION_BORDER_PADDING = 4;

        // Defines the relative height of the opponent/player/table areas of the UI.
        var OPPONENT_AREA_HEIGHT_SCALE = 0.15;
        var PLAYER_AREA_HEIGHT_SCALE = 0.3;

        // Defines the minimum/maximum height of the opponent/player/table areas of the UI. No max for table area.
        var MIN_OPPONENT_AREA_HEIGHT = 114;
        var MAX_OPPONENT_AREA_HEIGHT = 140;
        var MIN_PLAYER_AREA_HEIGHT = MIN_OPPONENT_AREA_HEIGHT * Math.floor(PLAYER_AREA_HEIGHT_SCALE / OPPONENT_AREA_HEIGHT_SCALE);
        var MAX_PLAYER_AREA_HEIGHT = MAX_OPPONENT_AREA_HEIGHT * Math.floor(PLAYER_AREA_HEIGHT_SCALE / OPPONENT_AREA_HEIGHT_SCALE);

        // Sets the top and height of the opponent/player/table areas of the UI.
        var OPPONENT_AREA_TOP = 0;
        var OPPONENT_AREA_HEIGHT = Math.min(MAX_OPPONENT_AREA_HEIGHT, Math.max(MIN_OPPONENT_AREA_HEIGHT, Math.floor(height * OPPONENT_AREA_HEIGHT_SCALE)));
        var OPPONENT_CARD_PILE_TOP_1 = OPPONENT_AREA_TOP;
        var OPPONENT_CARD_PILE_HEIGHT_1 = Math.floor(OPPONENT_AREA_HEIGHT / 2);
        var OPPONENT_CARD_PILE_TOP_2 = OPPONENT_AREA_TOP + OPPONENT_CARD_PILE_HEIGHT_1 + BORDER_PADDING - 1;
        var OPPONENT_CARD_PILE_HEIGHT_2 = OPPONENT_AREA_HEIGHT - OPPONENT_CARD_PILE_HEIGHT_1 - BORDER_PADDING + 1;
        var PLAYER_AREA_HEIGHT = Math.min(MAX_PLAYER_AREA_HEIGHT, Math.max(MIN_PLAYER_AREA_HEIGHT, Math.floor(height * PLAYER_AREA_HEIGHT_SCALE)));
        var PLAYER_AREA_TOP = height - BORDER_PADDING - PLAYER_AREA_HEIGHT;
        var PLAYER_CARD_PILES_AND_SIDE_OF_TABLE_HEIGHT = Math.floor(PLAYER_AREA_HEIGHT / 2);
        var PLAYER_CARD_PILE_TOP_1 = PLAYER_AREA_TOP;
        var PLAYER_CARD_PILE_HEIGHT_1 = Math.floor(PLAYER_CARD_PILES_AND_SIDE_OF_TABLE_HEIGHT / 2);
        var PLAYER_CARD_PILE_TOP_2 = PLAYER_CARD_PILE_TOP_1 + PLAYER_CARD_PILE_HEIGHT_1 + BORDER_PADDING - 1;
        var PLAYER_CARD_PILE_HEIGHT_2 = PLAYER_CARD_PILES_AND_SIDE_OF_TABLE_HEIGHT - PLAYER_CARD_PILE_HEIGHT_1 - BORDER_PADDING + 1;
        var PLAYER_ACTION_AREA_AND_HAND_TOP = PLAYER_AREA_TOP + PLAYER_CARD_PILES_AND_SIDE_OF_TABLE_HEIGHT + BORDER_PADDING - 1;
        var PLAYER_ACTION_AREA_AND_HAND_HEIGHT = PLAYER_AREA_HEIGHT - PLAYER_CARD_PILES_AND_SIDE_OF_TABLE_HEIGHT - BORDER_PADDING;
        var TABLE_AREA_TOP = OPPONENT_AREA_HEIGHT + BORDER_PADDING;
        var TABLE_AREA_HEIGHT = Math.max(0, PLAYER_AREA_TOP - LOCATION_BORDER_PADDING - TABLE_AREA_TOP);

        // Defines the sizes of other items in the UI.
        var LEFT_SIDE = 0;
        var GAME_STATE_AND_CHAT_WIDTH = 425;
        var CARD_PILE_AND_ACTION_AREA_LEFT = GAME_STATE_AND_CHAT_WIDTH + BORDER_PADDING - 1;
        var CARD_PILE_AND_ACTION_AREA_WIDTH = 141;
        var CARD_PILE_LEFT_1 = CARD_PILE_AND_ACTION_AREA_LEFT;
        var CARD_PILE_WIDTH_1 = Math.floor(CARD_PILE_AND_ACTION_AREA_WIDTH / 3);
        var CARD_PILE_LEFT_2 = CARD_PILE_AND_ACTION_AREA_LEFT + CARD_PILE_WIDTH_1 + BORDER_PADDING - 1;
        var CARD_PILE_WIDTH_2 = CARD_PILE_WIDTH_1;
        var CARD_PILE_LEFT_3 = CARD_PILE_LEFT_2 + CARD_PILE_WIDTH_2 + BORDER_PADDING - 1;
        var CARD_PILE_WIDTH_3 = CARD_PILE_AND_ACTION_AREA_WIDTH - CARD_PILE_WIDTH_1 - CARD_PILE_WIDTH_2;
        var LARGE_STAT_BOX_SIZE = 25;
        var SMALL_STAT_BOX_SIZE = 20;
        var STAT_BOX_PADDING = 2;
        var TAB_PANE_HEIGHT = 25;
        var TAB_PANE_WIDTH_PADDING = 4;
        var CHAT_HEIGHT = PLAYER_AREA_HEIGHT - BORDER_PADDING + 1;
        var CHAT_WIDTH = GAME_STATE_AND_CHAT_WIDTH;

        // Sets the hand left and width (for with and without Sabacc)
        var HAND_LEFT = CARD_PILE_LEFT_3 + CARD_PILE_WIDTH_3 + BORDER_PADDING - 1;
        var HAND_WIDTH = (width - HAND_LEFT) - BORDER_PADDING;
        var SABACC_HAND_LEFT = CARD_PILE_LEFT_3 + CARD_PILE_WIDTH_3 + BORDER_PADDING - 1;
        var SABACC_HAND_WIDTH = Math.floor((width - SABACC_HAND_LEFT) / 3) - BORDER_PADDING;
        var HAND_WITH_SABACC_LEFT = SABACC_HAND_LEFT + SABACC_HAND_WIDTH + BORDER_PADDING - 1;
        var HAND_WITH_SABACC_WIDTH = (width - HAND_WITH_SABACC_LEFT) - BORDER_PADDING;

        // Sets the side of table left and width (for with and without Sabacc)
        var SIDE_OF_TABLE_LEFT = CARD_PILE_LEFT_3 + CARD_PILE_WIDTH_3 + BORDER_PADDING - 1;
        var SIDE_OF_TABLE_WIDTH = (width - SIDE_OF_TABLE_LEFT) - BORDER_PADDING;
        var SIDE_OF_TABLE_WITH_SABACC_LEFT = SABACC_HAND_LEFT + SABACC_HAND_WIDTH + BORDER_PADDING - 1;
        var SIDE_OF_TABLE_WITH_SABACC_WIDTH = (width - SIDE_OF_TABLE_WITH_SABACC_LEFT) - BORDER_PADDING;

        if (!this.spectatorMode) {
            $("#bottomLeftTabs").css({ position:"absolute", left:LEFT_SIDE, top: PLAYER_AREA_TOP, width: CHAT_WIDTH, height: CHAT_HEIGHT, padding: 0});
            this.tabPane.css({ position:"absolute", left:LEFT_SIDE, top: PLAYER_AREA_TOP, width: CHAT_WIDTH, height: CHAT_HEIGHT});
            this.chatBox.setBounds(BORDER_PADDING + TAB_PANE_WIDTH_PADDING, TAB_PANE_HEIGHT, CHAT_WIDTH - (2 * TAB_PANE_WIDTH_PADDING), CHAT_HEIGHT - TAB_PANE_HEIGHT);
        }
        else {
            $("#bottomLeftTabs").css({ position:"absolute", left:LEFT_SIDE, top: PLAYER_AREA_TOP, width: CHAT_WIDTH, height: CHAT_HEIGHT, padding: 0});
            this.tabPane.css({ position:"absolute", left:LEFT_SIDE, top: PLAYER_AREA_TOP, width: CHAT_WIDTH, height: CHAT_HEIGHT});
            this.chatBox.setBounds(BORDER_PADDING + TAB_PANE_WIDTH_PADDING, TAB_PANE_HEIGHT, CHAT_WIDTH - (2 * TAB_PANE_WIDTH_PADDING), CHAT_HEIGHT - TAB_PANE_HEIGHT);
        }

        if (this.replayMode) {
            $(".replay").css({position:"absolute", left:width - 70 - 4 - BORDER_PADDING, top:height - 97 - 2 - BORDER_PADDING, width:70, height:97, "z-index":1000});
        }

        if (!this.gameUiInitialized) {
            return;
        }

        // Determine if it is the turn of the bottom player.
        var currentPlayerTurn = (this.currentPlayerId == this.bottomPlayerId);

        // Layout the UI elements
        if (this.gameStateElem != null) {

            // Layout the game state element
            this.gameStateElem.css({ position:"absolute", left:LEFT_SIDE, top:OPPONENT_AREA_TOP, width: GAME_STATE_AND_CHAT_WIDTH, height: OPPONENT_AREA_HEIGHT});
            this.topOfLostPileOpponent.setBounds(CARD_PILE_LEFT_1, OPPONENT_CARD_PILE_TOP_1, CARD_PILE_WIDTH_1, OPPONENT_CARD_PILE_HEIGHT_1);
            this.topOfReserveDeckOpponent.setBounds(CARD_PILE_LEFT_2, OPPONENT_CARD_PILE_TOP_1, CARD_PILE_WIDTH_2, OPPONENT_CARD_PILE_HEIGHT_1);
            this.topOfForcePileOpponent.setBounds(CARD_PILE_LEFT_3, OPPONENT_CARD_PILE_TOP_1, CARD_PILE_WIDTH_3, OPPONENT_CARD_PILE_HEIGHT_1);
            this.extraActionsGroupOpponent.setBounds(CARD_PILE_LEFT_1, OPPONENT_CARD_PILE_TOP_2, CARD_PILE_WIDTH_1, OPPONENT_CARD_PILE_HEIGHT_2);
            this.topOfUsedPileOpponent.setBounds(CARD_PILE_LEFT_2, OPPONENT_CARD_PILE_TOP_2, CARD_PILE_WIDTH_2, OPPONENT_CARD_PILE_HEIGHT_2);
            this.topOfDrawnDestinyOpponent.setBounds(CARD_PILE_LEFT_3, OPPONENT_CARD_PILE_TOP_2, CARD_PILE_WIDTH_3, OPPONENT_CARD_PILE_HEIGHT_2);

            if (this.showRevealedSabaccHands) {
                this.revealedSabaccHandOpponent.setBounds(SABACC_HAND_LEFT, OPPONENT_AREA_TOP, SABACC_HAND_WIDTH, OPPONENT_AREA_HEIGHT);
                this.revealedSabaccHandOpponent.layoutCards();

                if (this.bottomPlayerIndex==0) {
                    this.lightRevealedSabaccHandTotal.css({position:"absolute", left:SABACC_HAND_LEFT + SABACC_HAND_WIDTH - SMALL_STAT_BOX_SIZE - STAT_BOX_PADDING + "px", top:OPPONENT_AREA_TOP + OPPONENT_AREA_HEIGHT - SMALL_STAT_BOX_SIZE - STAT_BOX_PADDING + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                    this.lightRevealedSabaccHandTotal.show();
                }
                else {
                    this.darkRevealedSabaccHandTotal.css({position:"absolute", left:SABACC_HAND_LEFT + SABACC_HAND_WIDTH - SMALL_STAT_BOX_SIZE - STAT_BOX_PADDING + "px", top:OPPONENT_AREA_TOP + OPPONENT_AREA_HEIGHT - SMALL_STAT_BOX_SIZE - STAT_BOX_PADDING + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                    this.darkRevealedSabaccHandTotal.show();
                }

                this.sideOfTableOpponent.setBounds(SIDE_OF_TABLE_WITH_SABACC_LEFT, OPPONENT_AREA_TOP, SIDE_OF_TABLE_WITH_SABACC_WIDTH, OPPONENT_AREA_HEIGHT);
                this.sideOfTableOpponent.layoutCards();
            }
            else {
                this.revealedSabaccHandOpponent.hide();
                this.darkRevealedSabaccHandTotal.hide();
                this.lightRevealedSabaccHandTotal.hide();
                this.sideOfTableOpponent.setBounds(SIDE_OF_TABLE_LEFT, OPPONENT_AREA_TOP, SIDE_OF_TABLE_WIDTH, OPPONENT_AREA_HEIGHT);
                this.sideOfTableOpponent.layoutCards();
            }

            // Location groups
            var locationsCount = this.locationDivs.length;

            var zoomedInLocationDivWidth = (width / Math.min(3.25, locationsCount)) - (LOCATION_BORDER_PADDING / 2);
            var otherLocationDivWidth = zoomedInLocationDivWidth;
            if (locationsCount > 1) {
                var numZoomedInLocations = 0;
                if (this.zoomedInLocationIndex != null) {
                    numZoomedInLocations++;
                }
                if (this.battleLocationIndex != null && this.battleLocationIndex != this.zoomedInLocationIndex) {
                    numZoomedInLocations++;
                }
                if (this.attackLocationIndex != null && this.attackLocationIndex != this.zoomedInLocationIndex) {
                    numZoomedInLocations++;
                }
                if (this.duelOrLightsaberCombatLocationIndex != null && this.duelOrLightsaberCombatLocationIndex != this.zoomedInLocationIndex && this.duelOrLightsaberCombatLocationIndex != this.battleLocationIndex) {
                    numZoomedInLocations++;
                }
                otherLocationDivWidth = ((width - (zoomedInLocationDivWidth * numZoomedInLocations)) / (locationsCount - numZoomedInLocations)) - (LOCATION_BORDER_PADDING / 2);
            }

            var x = 0;
            var y = TABLE_AREA_TOP;
            var locationDivWidth = otherLocationDivWidth;
            var locationDivHeight = TABLE_AREA_HEIGHT;

            for (var locationIndex = 0; locationIndex < locationsCount; locationIndex++) {
                if (locationIndex == (locationsCount - 1)) {
                    locationDivWidth = (width - x) - LOCATION_BORDER_PADDING;
                }
                else if (this.zoomedInLocationIndex == locationIndex || this.battleLocationIndex == locationIndex || this.attackLocationIndex == locationIndex || this.duelOrLightsaberCombatLocationIndex == locationIndex) {
                    locationDivWidth = zoomedInLocationDivWidth;
                }
                else {
                    locationDivWidth = otherLocationDivWidth;
                }
                this.locationDivs[locationIndex].css({left:x, top:y, width:locationDivWidth, height:locationDivHeight, position:"absolute"});

                var curSystemName = this.locationDivs[locationIndex].data("systemName");
                if (locationIndex > 0) {
                    var prevSystemName = this.locationDivs[locationIndex - 1].data("systemName");
                    if (curSystemName == prevSystemName) {
                        this.locationDivs[locationIndex].css({"border-left-color":"#111111"});
                    }
                    else {
                        this.locationDivs[locationIndex].css({"border-left-color":"#666666"});
                    }
                }
                else {
                    this.locationDivs[locationIndex].css({"border-left-color":"#666666"});
                }

                if (locationIndex < (locationsCount - 1)) {
                    var nextSystemName = this.locationDivs[locationIndex + 1].data("systemName");
                    if (curSystemName == nextSystemName) {
                        this.locationDivs[locationIndex].css({"border-right-color":"#111111"});
                    }
                    else {
                        this.locationDivs[locationIndex].css({"border-right-color":"#666666"});
                    }
                }
                else {
                    this.locationDivs[locationIndex].css({"border-right-color":"#666666"});
                }

                // Battle and duel at this location
                if (this.battleLocationIndex == locationIndex && this.duelOrLightsaberCombatLocationIndex == locationIndex) {
                    this.opponentSideOfLocationGroups[locationIndex].setBounds(x, y, locationDivWidth, locationDivHeight/10);
                    this.opponentSideOfLocationGroups[locationIndex].layoutCards();
                    this.opponentInBattleGroups[locationIndex].setBounds(x, y + locationDivHeight/10, locationDivWidth, locationDivHeight/10);
                    this.opponentInBattleGroups[locationIndex].layoutCards();
                    this.opponentInDuelOrLightsaberCombatGroups[locationIndex].setBounds(x, y + locationDivHeight/5 + LARGE_STAT_BOX_SIZE, locationDivWidth, locationDivHeight/5 - LARGE_STAT_BOX_SIZE);
                    this.opponentInDuelOrLightsaberCombatGroups[locationIndex].layoutCards();
                    this.locationCardGroups[locationIndex].setBounds(x, y + 2*locationDivHeight/5, locationDivWidth, locationDivHeight/5);
                    this.locationCardGroups[locationIndex].layoutCards();
                    this.playerInDuelOrLightsaberCombatGroups[locationIndex].setBounds(x, y + 3*locationDivHeight/5, locationDivWidth, locationDivHeight/5 - LARGE_STAT_BOX_SIZE);
                    this.playerInDuelOrLightsaberCombatGroups[locationIndex].layoutCards();
                    this.playerInBattleGroups[locationIndex].setBounds(x, y + 4*locationDivHeight/5, locationDivWidth, locationDivHeight/10);
                    this.playerInBattleGroups[locationIndex].layoutCards();
                    this.playerSideOfLocationGroups[locationIndex].setBounds(x, y + 9*locationDivHeight/10, locationDivWidth, locationDivHeight/10);
                    this.playerSideOfLocationGroups[locationIndex].layoutCards();

                    if (this.battleGroupDiv != null && this.duelOrLightsaberCombatGroupDiv != null) {
                        this.battleGroupDiv.css({left:x, top:y + locationDivHeight/10, width:locationDivWidth, height:4*locationDivHeight/5, position:"absolute"});

                        this.duelOrLightsaberCombatGroupDiv.css({left:x, top:y + locationDivHeight/5, width:locationDivWidth - 4, height:3*locationDivHeight/5, position:"absolute"});
                        if (this.bottomPlayerIndex==0) {
                            this.lightDuelOrLightsaberCombatTotalDiv.css({position:"absolute", left:2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.lightDuelOrLightsaberCombatNumDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.darkDuelOrLightsaberCombatTotalDiv.css({position:"absolute", left:2 + "px", top:3*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.darkDuelOrLightsaberCombatNumDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:3*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                        }
                        else {
                            this.darkDuelOrLightsaberCombatTotalDiv.css({position:"absolute", left:2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.darkDuelOrLightsaberCombatNumDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.lightDuelOrLightsaberCombatTotalDiv.css({position:"absolute", left:2 + "px", top:3*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.lightDuelOrLightsaberCombatNumDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:3*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                        }
                    }
                }
                // Battle at this location
                else if (this.battleLocationIndex==locationIndex) {
                    this.opponentSideOfLocationGroups[locationIndex].setBounds(x, y, locationDivWidth, locationDivHeight/10);
                    this.opponentSideOfLocationGroups[locationIndex].layoutCards();
                    this.opponentInBattleGroups[locationIndex].setBounds(x, y + locationDivHeight/10 + LARGE_STAT_BOX_SIZE, locationDivWidth, 3*locationDivHeight/10 - LARGE_STAT_BOX_SIZE);
                    this.opponentInBattleGroups[locationIndex].layoutCards();
                    this.locationCardGroups[locationIndex].setBounds(x, y + 2*locationDivHeight/5, locationDivWidth, locationDivHeight/5);
                    this.locationCardGroups[locationIndex].layoutCards();
                    this.playerInBattleGroups[locationIndex].setBounds(x, y + 3*locationDivHeight/5, locationDivWidth, 3*locationDivHeight/10 - LARGE_STAT_BOX_SIZE);
                    this.playerInBattleGroups[locationIndex].layoutCards();
                    this.playerSideOfLocationGroups[locationIndex].setBounds(x, y + 9*locationDivHeight/10, locationDivWidth, locationDivHeight/10);
                    this.playerSideOfLocationGroups[locationIndex].layoutCards();

                    if (this.battleGroupDiv!=null) {
                        this.battleGroupDiv.css({left:x, top:y + locationDivHeight/10, width:locationDivWidth, height:4*locationDivHeight/5, position:"absolute"});
                        if (this.bottomPlayerIndex==0) {
                            this.lightBattlePowerDiv.css({position:"absolute", left:2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.lightBattleNumDestinyToPowerDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.lightBattleNumBattleDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + SMALL_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.lightBattleNumDestinyToAttritionDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + (2 * (SMALL_STAT_BOX_SIZE + 1)) + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.lightBattleDamageRemainingDiv.css({position:"absolute", left:locationDivWidth - LARGE_STAT_BOX_SIZE - 2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.lightBattleAttritionRemainingDiv.css({position:"absolute", left:locationDivWidth - LARGE_STAT_BOX_SIZE - 2 + "px", top:2 + LARGE_STAT_BOX_SIZE + 1 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.darkBattlePowerDiv.css({position:"absolute", left:2 + "px", top:4*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.darkBattleNumDestinyToPowerDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:4*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.darkBattleNumBattleDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + SMALL_STAT_BOX_SIZE + 1 + "px", top:4*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.darkBattleNumDestinyToAttritionDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + (2 * (SMALL_STAT_BOX_SIZE + 1)) + "px", top:4*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.darkBattleDamageRemainingDiv.css({position:"absolute", left:locationDivWidth - LARGE_STAT_BOX_SIZE - 2 + "px", top:4*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.darkBattleAttritionRemainingDiv.css({position:"absolute", left:locationDivWidth - LARGE_STAT_BOX_SIZE - 2 + "px", top:4*locationDivHeight/5 - 2*LARGE_STAT_BOX_SIZE - 3 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                        }
                        else {
                            this.darkBattlePowerDiv.css({position:"absolute", left:2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.darkBattleNumDestinyToPowerDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.darkBattleNumBattleDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + SMALL_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.darkBattleNumDestinyToAttritionDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + (2 * (SMALL_STAT_BOX_SIZE + 1)) + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.darkBattleDamageRemainingDiv.css({position:"absolute", left:locationDivWidth - LARGE_STAT_BOX_SIZE - 2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.darkBattleAttritionRemainingDiv.css({position:"absolute", left:locationDivWidth - LARGE_STAT_BOX_SIZE - 2 + "px", top:2 + LARGE_STAT_BOX_SIZE + 1 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.lightBattlePowerDiv.css({position:"absolute", left:2 + "px", top:4*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.lightBattleNumDestinyToPowerDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:4*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.lightBattleNumBattleDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + SMALL_STAT_BOX_SIZE + 1 + "px", top:4*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.lightBattleNumDestinyToAttritionDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + (2 * (SMALL_STAT_BOX_SIZE + 1)) + "px", top:4*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.lightBattleDamageRemainingDiv.css({position:"absolute", left:locationDivWidth - LARGE_STAT_BOX_SIZE - 2 + "px", top:4*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.lightBattleAttritionRemainingDiv.css({position:"absolute", left:locationDivWidth - LARGE_STAT_BOX_SIZE - 2 + "px", top:4*locationDivHeight/5 - 2*LARGE_STAT_BOX_SIZE - 3 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                        }
                    }
                }
                // Duel or lightsaber combat at this location
                else if (this.duelOrLightsaberCombatLocationIndex==locationIndex) {

                    this.opponentSideOfLocationGroups[locationIndex].setBounds(x, y, locationDivWidth, locationDivHeight/5);
                    this.opponentSideOfLocationGroups[locationIndex].layoutCards();
                    this.opponentInDuelOrLightsaberCombatGroups[locationIndex].setBounds(x, y + locationDivHeight/5 + LARGE_STAT_BOX_SIZE, locationDivWidth, locationDivHeight/5 - LARGE_STAT_BOX_SIZE);
                    this.opponentInDuelOrLightsaberCombatGroups[locationIndex].layoutCards();
                    this.locationCardGroups[locationIndex].setBounds(x, y + 2*locationDivHeight/5, locationDivWidth, locationDivHeight/5);
                    this.locationCardGroups[locationIndex].layoutCards();
                    this.playerInDuelOrLightsaberCombatGroups[locationIndex].setBounds(x, y + 3*locationDivHeight/5, locationDivWidth, locationDivHeight/5 - LARGE_STAT_BOX_SIZE);
                    this.playerInDuelOrLightsaberCombatGroups[locationIndex].layoutCards();
                    this.playerSideOfLocationGroups[locationIndex].setBounds(x, y + 4*locationDivHeight/5, locationDivWidth, locationDivHeight/5);
                    this.playerSideOfLocationGroups[locationIndex].layoutCards();

                    if (this.duelOrLightsaberCombatGroupDiv != null) {
                        this.duelOrLightsaberCombatGroupDiv.css({left:x, top:y + locationDivHeight/5, width:locationDivWidth - 4, height:3*locationDivHeight/5, position:"absolute"});

                        if (this.bottomPlayerIndex==0) {
                            this.lightDuelOrLightsaberCombatTotalDiv.css({position:"absolute", left:2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.lightDuelOrLightsaberCombatNumDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.darkDuelOrLightsaberCombatTotalDiv.css({position:"absolute", left:2 + "px", top:3*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.darkDuelOrLightsaberCombatNumDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:3*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                        }
                        else {
                            this.darkDuelOrLightsaberCombatTotalDiv.css({position:"absolute", left:2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.darkDuelOrLightsaberCombatNumDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            this.lightDuelOrLightsaberCombatTotalDiv.css({position:"absolute", left:2 + "px", top:3*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                            this.lightDuelOrLightsaberCombatNumDestinyDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:3*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                        }
                    }
                }
                // Attack at this location
                else if (this.attackLocationIndex==locationIndex) {

                    this.opponentSideOfLocationGroups[locationIndex].setBounds(x, y, locationDivWidth, locationDivHeight/10);
                    this.opponentSideOfLocationGroups[locationIndex].layoutCards();
                    if (this.playerIdAttacking == this.playerIdDefending) {
                        if (this.bottomPlayerId == this.playerIdAttacking) {
                            this.locationCardGroups[locationIndex].setBounds(x, y + locationDivHeight/10, locationDivWidth, locationDivHeight/5);
                            this.locationCardGroups[locationIndex].layoutCards();
                            this.defendingInAttackGroups[locationIndex].setBounds(x, y + 3*locationDivHeight/10 + LARGE_STAT_BOX_SIZE, locationDivWidth, 3*locationDivHeight/10 - LARGE_STAT_BOX_SIZE);
                            this.defendingInAttackGroups[locationIndex].layoutCards();
                            this.attackingInAttackGroups[locationIndex].setBounds(x, y + 3*locationDivHeight/5, locationDivWidth, 3*locationDivHeight/10 - LARGE_STAT_BOX_SIZE);
                            this.attackingInAttackGroups[locationIndex].layoutCards();

                            if (this.attackGroupDiv != null) {
                                this.attackGroupDiv.css({left:x, top:y + 3*locationDivHeight/10, width:locationDivWidth - 4, height:3*locationDivHeight/5, position:"absolute"});
                                this.defendingPowerOrFerocityInAttackDiv.css({position:"absolute", left:2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                                this.defendingNumDestinyInAttackDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                                this.attackingPowerOrFerocityInAttackDiv.css({position:"absolute", left:2 + "px", top:3*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                                this.attackingNumDestinyInAttackDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:3*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            }
                        }
                        else {
                            this.attackingInAttackGroups[locationIndex].setBounds(x, y + locationDivHeight/10 + LARGE_STAT_BOX_SIZE, locationDivWidth, 3*locationDivHeight/10 - LARGE_STAT_BOX_SIZE);
                            this.attackingInAttackGroups[locationIndex].layoutCards();
                            this.defendingInAttackGroups[locationIndex].setBounds(x, y + 3*locationDivHeight/5, locationDivWidth, 3*locationDivHeight/10 - LARGE_STAT_BOX_SIZE);
                            this.defendingInAttackGroups[locationIndex].layoutCards();
                            this.locationCardGroups[locationIndex].setBounds(x, y + 7*locationDivHeight/10, locationDivWidth, locationDivHeight/5);
                            this.locationCardGroups[locationIndex].layoutCards();

                            if (this.attackGroupDiv != null) {
                                this.attackGroupDiv.css({left:x, top:y + locationDivHeight/10, width:locationDivWidth - 4, height:3*locationDivHeight/5, position:"absolute"});
                                this.attackingPowerOrFerocityInAttackDiv.css({position:"absolute", left:2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                                this.attackingNumDestinyInAttackDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                                this.defendingPowerOrFerocityInAttackDiv.css({position:"absolute", left:2 + "px", top:3*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                                this.defendingNumDestinyInAttackDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:3*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            }
                        }
                    }
                    else {
                        this.locationCardGroups[locationIndex].setBounds(x, y + 2*locationDivHeight/5, locationDivWidth, locationDivHeight/5);
                        this.locationCardGroups[locationIndex].layoutCards();
                        if (this.bottomPlayerId == this.playerIdAttacking) {
                            this.defendingInAttackGroups[locationIndex].setBounds(x, y + locationDivHeight/10 + LARGE_STAT_BOX_SIZE, locationDivWidth, 3*locationDivHeight/10 - LARGE_STAT_BOX_SIZE);
                            this.defendingInAttackGroups[locationIndex].layoutCards();
                            this.attackingInAttackGroups[locationIndex].setBounds(x, y + 3*locationDivHeight/5, locationDivWidth, 3*locationDivHeight/10 - LARGE_STAT_BOX_SIZE);
                            this.attackingInAttackGroups[locationIndex].layoutCards();

                            if (this.attackGroupDiv != null) {
                                this.attackGroupDiv.css({left:x, top:y + locationDivHeight/10, width:locationDivWidth - 4, height:4*locationDivHeight/5, position:"absolute"});
                                this.defendingPowerOrFerocityInAttackDiv.css({position:"absolute", left:2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                                this.defendingNumDestinyInAttackDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                                this.attackingPowerOrFerocityInAttackDiv.css({position:"absolute", left:2 + "px", top:4*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                                this.attackingNumDestinyInAttackDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:4*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            }
                        }
                        else {
                            this.attackingInAttackGroups[locationIndex].setBounds(x, y + locationDivHeight/10 + LARGE_STAT_BOX_SIZE, locationDivWidth, 3*locationDivHeight/10 - LARGE_STAT_BOX_SIZE);
                            this.attackingInAttackGroups[locationIndex].layoutCards();
                            this.defendingInAttackGroups[locationIndex].setBounds(x, y + 3*locationDivHeight/5, locationDivWidth, 3*locationDivHeight/10 - LARGE_STAT_BOX_SIZE);
                            this.defendingInAttackGroups[locationIndex].layoutCards();

                            if (this.attackGroupDiv != null) {
                                this.attackGroupDiv.css({left:x, top:y + locationDivHeight/10, width:locationDivWidth - 4, height:4*locationDivHeight/5, position:"absolute"});
                                this.attackingPowerOrFerocityInAttackDiv.css({position:"absolute", left:2 + "px", top:2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                                this.attackingNumDestinyInAttackDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                                this.defendingPowerOrFerocityInAttackDiv.css({position:"absolute", left:2 + "px", top:4*locationDivHeight/5 - LARGE_STAT_BOX_SIZE - 2 + "px", width:LARGE_STAT_BOX_SIZE, height:LARGE_STAT_BOX_SIZE, "z-index":50});
                                this.defendingNumDestinyInAttackDiv.css({position:"absolute", left:2 + LARGE_STAT_BOX_SIZE + 1 + "px", top:4*locationDivHeight/5 - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                            }
                        }
                    }
                    this.playerSideOfLocationGroups[locationIndex].setBounds(x, y + 9*locationDivHeight/10, locationDivWidth, locationDivHeight/10);
                    this.playerSideOfLocationGroups[locationIndex].layoutCards();
                }
                // Neither duel, lightsaber combat, battle, nor attack at this location
                else {
                    this.opponentSideOfLocationGroups[locationIndex].setBounds(x, y, locationDivWidth, 2*locationDivHeight/5);
                    this.opponentSideOfLocationGroups[locationIndex].layoutCards();
                    this.opponentInBattleGroups[locationIndex].hide();
                    this.locationCardGroups[locationIndex].setBounds(x, y + 2*locationDivHeight/5, locationDivWidth, locationDivHeight/5);
                    this.locationCardGroups[locationIndex].layoutCards();
                    this.playerSideOfLocationGroups[locationIndex].setBounds(x, y + 3*locationDivHeight/5, locationDivWidth, 2*locationDivHeight/5);
                    this.playerSideOfLocationGroups[locationIndex].layoutCards();
                    this.playerInBattleGroups[locationIndex].hide();
                    this.attackingInAttackGroups[locationIndex].hide();
                    this.defendingInAttackGroups[locationIndex].hide();
                    if (this.bottomPlayerIndex==0) {
                        this.lightPowerAtLocationDivs[locationIndex].css({position:"absolute", left:2 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                        this.darkPowerAtLocationDivs[locationIndex].css({position:"absolute", left:2 + "px", top:locationDivHeight - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                    }
                    else {
                        this.darkPowerAtLocationDivs[locationIndex].css({position:"absolute", left:2 + "px", top:2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                        this.lightPowerAtLocationDivs[locationIndex].css({position:"absolute", left:2 + "px", top:locationDivHeight - SMALL_STAT_BOX_SIZE - 2 + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                    }
                }

                // Update x-coordinate for the next location div
                x = (x + locationDivWidth + (LOCATION_BORDER_PADDING / 2));
            }

            this.extraActionsGroup.setBounds(CARD_PILE_LEFT_1, PLAYER_CARD_PILE_TOP_1, CARD_PILE_WIDTH_1, PLAYER_CARD_PILE_HEIGHT_1);
            this.topOfUsedPilePlayer.setBounds(CARD_PILE_LEFT_2, PLAYER_CARD_PILE_TOP_1, CARD_PILE_WIDTH_2, PLAYER_CARD_PILE_HEIGHT_1);
            this.topOfDrawnDestinyPlayer.setBounds(CARD_PILE_LEFT_3, PLAYER_CARD_PILE_TOP_1, CARD_PILE_WIDTH_3, PLAYER_CARD_PILE_HEIGHT_1);
            this.topOfLostPilePlayer.setBounds(CARD_PILE_LEFT_1, PLAYER_CARD_PILE_TOP_2, CARD_PILE_WIDTH_1, PLAYER_CARD_PILE_HEIGHT_2);
            this.topOfReserveDeckPlayer.setBounds(CARD_PILE_LEFT_2, PLAYER_CARD_PILE_TOP_2, CARD_PILE_WIDTH_2, PLAYER_CARD_PILE_HEIGHT_2);
            this.topOfForcePilePlayer.setBounds(CARD_PILE_LEFT_3, PLAYER_CARD_PILE_TOP_2, CARD_PILE_WIDTH_3, PLAYER_CARD_PILE_HEIGHT_2);

            if (this.showRevealedSabaccHands) {
                this.revealedSabaccHandPlayer.setBounds(SABACC_HAND_LEFT, PLAYER_AREA_TOP, SABACC_HAND_WIDTH, PLAYER_CARD_PILES_AND_SIDE_OF_TABLE_HEIGHT);
                this.revealedSabaccHandPlayer.layoutCards();

                if (this.bottomPlayerIndex==0) {
                    this.darkRevealedSabaccHandTotal.css({position:"absolute", left:SABACC_HAND_LEFT + SABACC_HAND_WIDTH - SMALL_STAT_BOX_SIZE - STAT_BOX_PADDING + "px", top:PLAYER_AREA_TOP + STAT_BOX_PADDING + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                    this.darkRevealedSabaccHandTotal.show();
                }
                else {
                    this.lightRevealedSabaccHandTotal.css({position:"absolute", left:SABACC_HAND_LEFT + SABACC_HAND_WIDTH - SMALL_STAT_BOX_SIZE - STAT_BOX_PADDING + "px", top:PLAYER_AREA_TOP + STAT_BOX_PADDING + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                    this.lightRevealedSabaccHandTotal.show();
                }

                this.sideOfTablePlayer.setBounds(SIDE_OF_TABLE_WITH_SABACC_LEFT, PLAYER_AREA_TOP, HAND_WITH_SABACC_WIDTH, PLAYER_CARD_PILES_AND_SIDE_OF_TABLE_HEIGHT);
                this.sideOfTablePlayer.layoutCards();
            }
            else {
                this.revealedSabaccHandPlayer.hide();
                this.sideOfTablePlayer.setBounds(SIDE_OF_TABLE_LEFT, PLAYER_AREA_TOP, SIDE_OF_TABLE_WIDTH, PLAYER_CARD_PILES_AND_SIDE_OF_TABLE_HEIGHT);
                this.sideOfTablePlayer.layoutCards();
            }

            if (!this.spectatorMode) {

                this.alertBox.css({ position:"absolute", left:CARD_PILE_AND_ACTION_AREA_LEFT, top:PLAYER_ACTION_AREA_AND_HAND_TOP, width: CARD_PILE_AND_ACTION_AREA_WIDTH + 2, height: PLAYER_ACTION_AREA_AND_HAND_HEIGHT });

                if (this.showSabaccHand) {
                    this.sabaccHand.setBounds(SABACC_HAND_LEFT, PLAYER_ACTION_AREA_AND_HAND_TOP, SABACC_HAND_WIDTH, PLAYER_ACTION_AREA_AND_HAND_HEIGHT);
                    this.sabaccHand.layoutCards();

                    if (this.bottomPlayerIndex==0) {
                        this.lightSabaccHandTotal.hide();
                        this.darkSabaccHandTotal.css({position:"absolute", left:SABACC_HAND_LEFT + SABACC_HAND_WIDTH - SMALL_STAT_BOX_SIZE - STAT_BOX_PADDING + "px", top:PLAYER_ACTION_AREA_AND_HAND_TOP + STAT_BOX_PADDING + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                        this.darkSabaccHandTotal.show();
                    }
                    else {
                        this.darkSabaccHandTotal.hide();
                        this.lightSabaccHandTotal.css({position:"absolute", left:SABACC_HAND_LEFT + SABACC_HAND_WIDTH - SMALL_STAT_BOX_SIZE - STAT_BOX_PADDING + "px", top:PLAYER_ACTION_AREA_AND_HAND_TOP + STAT_BOX_PADDING + "px", width:SMALL_STAT_BOX_SIZE, height:SMALL_STAT_BOX_SIZE, "z-index":50});
                        this.lightSabaccHandTotal.show();
                    }
                    this.hand.setBounds(HAND_WITH_SABACC_LEFT, PLAYER_ACTION_AREA_AND_HAND_TOP, HAND_WITH_SABACC_LEFT, PLAYER_ACTION_AREA_AND_HAND_HEIGHT);
                    this.hand.layoutCards();
                }
                else {
                    this.sabaccHand.hide();
                    this.darkSabaccHandTotal.hide();
                    this.lightSabaccHandTotal.hide();
                    this.hand.setBounds(HAND_LEFT, PLAYER_ACTION_AREA_AND_HAND_TOP, HAND_WIDTH, PLAYER_ACTION_AREA_AND_HAND_HEIGHT);
                    this.hand.layoutCards();
                }
            }
        }

        for (var playerId in this.outOfPlayPileGroups)
            if (this.outOfPlayPileGroups.hasOwnProperty(playerId))
                this.outOfPlayPileGroups[playerId].layoutCards();
    },

    startReplaySession:function (replayId) {
        var that = this;
        this.communication.getReplay(replayId,
                function (xml) {
                    that.processXmlReplay(xml, true);
                });
    },

    startGameSession:function () {
        var that = this;
        this.communication.startGameSession(
                function (xml) {
                    that.processXml(xml, false);
                }, this.gameErrorMap());
    },

    updateGameState:function () {
        var that = this;
        this.communication.updateGameState(
                this.channelNumber,
                function (xml) {
                    that.processXml(xml, true);
                }, this.gameErrorMap());
    },

    decisionFunction:function (decisionId, result) {
        var that = this;
        this.stopAnimatingTitle();
        this.communication.gameDecisionMade(decisionId, result,
                this.channelNumber,
                function (xml) {
                    that.processXml(xml, true);
                }, this.gameErrorMap());
    },

    gameErrorMap:function() {
        var that = this;
        return {
            "0": function() {
                that.showErrorDialog("Server connection error", "Unable to connect to server. Either server is down or there is a problem with your internet connection.", true, false, false);
            },
            "401":function() {
                that.showErrorDialog("Authentication error", "You are not logged in", false, true, false);
            },
            "403":function() {
                that.showErrorDialog("Game access forbidden", "This game is private and does not allow spectators.", false, false, true);
            },
            "409":function() {
                that.showErrorDialog("Concurrent access error", "You are observing this Game Hall from another browser or window. Close this window or if you wish to observe it here, click \"Refresh page\".", true, false, false);
            },
            "410":function() {
                that.showErrorDialog("Inactivity error", "You were inactive for too long and have been removed from observing this game. If you wish to start again, click \"Refresh page\".", true, false, false);
            }
        };
    },

    showErrorDialog:function(title, text, reloadButton, mainPageButton, gameHallButton) {
        var buttons = {};
        if (reloadButton) {
            buttons["Refresh page"] =
            function () {
                location.reload(true);
            };
        }
        if (mainPageButton) {
            buttons["Go to main page"] =
            function() {
                location.href = "/gemp-swccg/";
            };
        }
        if (gameHallButton) {
            buttons["Go to Game Hall"] =
            function() {
                location.href = "/gemp-swccg/hall.html";
            };
        }

        var dialog = $("<div></div>").dialog({
            title: title,
            resizable: false,
            height: 160,
            modal: true,
            buttons: buttons
        }).text(text);
    },

    getCardModifiersFunction:function (cardId, func) {
        var that = this;
        this.communication.getGameCardModifiers(cardId,
                function (html) {
                    that.setCardModifiers(html);
                });
    },

    processXml:function (xml, animate) {
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'gameState' || root.tagName == 'update')
            this.processGameEventsXml(root, animate);
    },

    replayGameEventNextIndex:0,
    replayGameEvents:null,

    processXmlReplay:function (xml, animate) {
        var that = this;
        log(xml);
        var root = xml.documentElement;
        if (root.tagName == 'gameReplay') {
            this.replayGameEvents = root.getElementsByTagName("ge");
            this.replayGameEventNextIndex = 0;

            $("#replayButton").click(
                    function() {
                        if (that.replayPlay) {
                            that.replayPlay = false;
                            $("#replayButton").attr("src", "images/play.png");
                        } else {
                            that.replayPlay = true;
                            $("#replayButton").attr("src", "images/pause.png");
                            that.playNextReplayEvent();
                        }
                    });

            this.playNextReplayEvent();
        }
    },

    shouldPlay:function () {
        return this.replayPlay;
    },

    playNextReplayEvent:function () {
        if (this.shouldPlay()) {
            var that = this;
            if (this.replayGameEventNextIndex < this.replayGameEvents.length) {
                $("#main").queue(
                        function (next) {
                            that.cleanupDecision();
                            next();
                        });
                var gameEvent = this.replayGameEvents[this.replayGameEventNextIndex];
                this.processGameEvent(gameEvent, true);

                this.replayGameEventNextIndex++;

                $("#main").queue(
                        function (next) {
                            that.playNextReplayEvent();
                            next();
                        });
            }
        }
    },

    processGameEvent:function (gameEvent, animate) {
        var eventType = gameEvent.getAttribute("type");
        if (eventType == "PCIP" || eventType == "RCIP") {
            this.animations.putCardInPlay(gameEvent, animate);  
        } else if (eventType == "PCIPAR") {
            this.animations.putCardInPlay(gameEvent, false);
        } else if (eventType == "FCIP") {
            this.animations.flipCardInPlay(gameEvent, animate);
        } else if (eventType == "MCIP") {
            this.animations.moveCardInPlay(gameEvent, animate);
        } else if (eventType == "ROCIP") {
            this.animations.rotateCardInPlay(gameEvent, animate);
        } else if (eventType == "P") {
            this.participant(gameEvent);
        } else if (eventType == "RCFP") {
            this.animations.removeCardFromPlay(gameEvent, animate);
        } else if (eventType == "RLFP") {
            this.animations.removeLocationFromPlay(gameEvent, animate);
        } else if (eventType == "GPC") {
            this.animations.gamePhaseChange(gameEvent, animate);
        } else if (eventType == "TC") {
            this.animations.turnChange(gameEvent, animate);
        } else if (eventType == "SB") {
            this.animations.startBattle(gameEvent, animate);
        } else if (eventType == "ATB") {
            this.animations.addToBattle(gameEvent, animate);
        } else if (eventType == "RFB") {
            this.animations.removeFromBattle(gameEvent, animate);
        } else if (eventType == "EB") {
            this.animations.endBattle(animate);
        } else if (eventType == "SA") {
            this.animations.startAttack(gameEvent, animate);
        } else if (eventType == "EA") {
            this.animations.endAttack(animate);
        } else if (eventType == "SD" || eventType == "SLC") {
            this.animations.startDuelOrLightsaberCombat(gameEvent, animate);
        } else if (eventType == "ED" || eventType == "ELC") {
            this.animations.endDuelOrLightsaberCombat(animate);
        } else if (eventType == "SS") {
            this.animations.startSabacc(animate);
        } else if (eventType == "RSH") {
            this.animations.revealSabaccHands(animate);
        } else if (eventType == "ES") {
            this.animations.endSabacc(animate);
        } else if (eventType == "GS") {
            this.animations.gameStats(gameEvent, animate);
        } else if (eventType == "M") {
            this.animations.message(gameEvent, animate);
        } else if (eventType == "W") {
            this.animations.warning(gameEvent, animate);
        } else if (eventType == "CAC") {
            this.animations.cardAffectsCard(gameEvent, animate);
        } else if (eventType == "IP") {
            this.animations.interruptPlayed(gameEvent, animate);
        } else if (eventType == "DD") {
            this.animations.destinyDrawn(gameEvent, animate);
        } else if (eventType == "CA") {
            this.animations.cardActivated(gameEvent, animate);
        } else if (eventType == "D") {
            this.animations.processDecision(gameEvent, animate);
        }
    },

    processGameEventsXml:function (element, animate) {
        try {
            this.channelNumber = element.getAttribute("cn");

            var gameEvents = element.getElementsByTagName("ge");

            var hasDecision = false;

            // Go through all the events
            for (var i = 0; i < gameEvents.length; i++) {
                var gameEvent = gameEvents[i];
                this.processGameEvent(gameEvent, animate);
                var eventType = gameEvent.getAttribute("type");
                if (eventType == "D")
                    hasDecision = true;
            }

            if (this.allPlayerIds != null) {
                var clocksXml = element.getElementsByTagName("clocks");
                if (clocksXml.length > 0) {
                    var clocks = clocksXml[0].getElementsByTagName("clock");
                    for (var i = 0; i < clocks.length; i++) {
                        var clock = clocks[i];
                        var participantId = clock.getAttribute("participantId");
                        var index = this.getPlayerIndex(participantId);

                        var value = parseInt(clock.childNodes[0].nodeValue);

                        var sign = (value < 0) ? "-" : "";
                        value = Math.abs(value);
                        var minutes = Math.floor(value / 60);
                        var seconds = value % 60;

                        $("#clock" + index).text(sign + minutes + ":" + ((seconds < 10) ? ("0" + seconds) : seconds));
                    }
                }
            }

            if (!hasDecision) {
                this.animations.updateGameState(animate);
            } else {
                this.startAnimatingTitle();
            }
        } catch (e) {
            this.showErrorDialog("Game error", "There was an error while processing game events in your browser. Reload the game to continue", true, false, false);
        }
    },

    keepAnimating: false,

    startAnimatingTitle: function() {
        var that = this;
        this.keepAnimating = true;
        setTimeout(function() {
            that.setDecisionTitle();
        }, 500);
    },

    stopAnimatingTitle: function() {
        this.keepAnimating = false;
        window.document.title = "Game of Gemp-Swccg";
    },

    setDecisionTitle: function() {
        if (this.keepAnimating) {
            window.document.title = "Waiting for your decision";
            var that = this;
            setTimeout(function() {
                that.setNormalTitle();
            }, 500);
        }
    },

    setNormalTitle: function() {
        if (this.keepAnimating) {
            window.document.title = "Game of Gemp-Swccg";
            var that = this;
            setTimeout(function() {
                that.setDecisionTitle();
            }, 500);
        }
    },

    getPlayerIndex:function (playerId) {
        for (var plId = 0; plId < this.allPlayerIds.length; plId++)
            if (this.allPlayerIds[plId] == playerId)
                return plId;
        return -1;
    },

    layoutZones:function () {
        this.sideOfTablePlayer.layoutCards();
        this.sideOfTableOpponent.layoutCards();
        this.topOfReserveDeckPlayer.layoutCards();
        this.topOfReserveDeckOpponent.layoutCards();
        this.topOfForcePilePlayer.layoutCards();
        this.topOfForcePileOpponent.layoutCards();
        this.topOfUsedPilePlayer.layoutCards();
        this.topOfUsedPileOpponent.layoutCards();
        this.topOfLostPilePlayer.layoutCards();
        this.topOfLostPileOpponent.layoutCards();
        this.topOfDrawnDestinyPlayer.layoutCards();
        this.topOfDrawnDestinyOpponent.layoutCards();
        if (!this.spectatorMode) {
            this.hand.layoutCards();
            this.sabaccHand.layoutCards();
        }
        this.revealedSabaccHandPlayer.layoutCards();
        this.revealedSabaccHandOpponent.layoutCards();
        this.topOfUsedPilePlayer.layoutCards();
    },

    participant:function (element) {
        var participantId = element.getAttribute("participantId");
        this.allPlayerIds = element.getAttribute("allParticipantIds").split(",");
        this.bottomPlayerId = participantId;

        var that = this;

        this.bottomPlayerIndex = this.getPlayerIndex(this.bottomPlayerId);
        if (this.bottomPlayerIndex == -1) {
            this.bottomPlayerId = this.allPlayerIds[1];
            this.bottomPlayerIndex = 1;
            this.spectatorMode = true;
        } else {
            this.spectatorMode = false;
        }

        this.initializeGameUI();
        this.layoutUI(true);
    },

    getDecisionParameter:function (decision, name) {
        var parameters = decision.getElementsByTagName("parameter");
        for (var i = 0; i < parameters.length; i++)
            if (parameters[i].getAttribute("name") == name)
                return parameters[i].getAttribute("value");

        return null;
    },

    getDecisionParameters:function (decision, name) {
        var result = new Array();
        var parameters = decision.getElementsByTagName("parameter");
        for (var i = 0; i < parameters.length; i++)
            if (parameters[i].getAttribute("name") == name)
                result.push(parameters[i].getAttribute("value"));

        return result;
    },

    cleanupDecision:function () {
        this.smallDialog.dialog("close");
        this.cardActionDialog.dialog("close");
        this.clearSelection();
        if (this.alertText != null) {
            this.alertTextMsg = "";
            this.alertText.html(this.alertTextMsg);
        }
        if (this.alertButtons != null)
            this.alertButtons.html("");
        if (this.alertBox != null)
            this.alertBox.css({"border-radius":"0px", "border-color":""});

        $(".card").each(
                function () {
                    var card = $(this).data("card");
                    if (card.zone == "EXTRA")
                        $(this).remove();
                });
        if (this.extraActionsGroup != null)
            this.extraActionsGroup.layoutCards();
    },

    emptyDecision:function (decision) {
        var id = decision.getAttribute("id");
        var timeout = 1000;
        var timeoutValue = this.getDecisionParameter(decision, "timeoutValue");
        if (timeoutValue != null)
            timeout = parseInt(timeoutValue);

        var that = this;
        setTimeout(function(){that.decisionFunction(id, 0)}, timeout);
    },

    integerDecision:function (decision) {
        var id = decision.getAttribute("id");
        var text = decision.getAttribute("text");
        var val = 0;

        var min = this.getDecisionParameter(decision, "min");
        if (min == null)
            min = 0;
        var max = this.getDecisionParameter(decision, "max");
        if (max == null)
            max = 1000;

        var defaultValue = this.getDecisionParameter(decision, "defaultValue");
        if (defaultValue != null)
            val = parseInt(defaultValue);

        var that = this;
        this.smallDialog
                .html(text + "<br /><input id='integerDecision' type='text' value='0'>");

        if (!this.replayMode) {
            this.smallDialog.dialog("option", "buttons",
            {
                "OK":function () {
                    $(this).dialog("close");
                    that.decisionFunction(id, $("#integerDecision").val());
                }
            });
        }

        $("#integerDecision").SpinnerControl({ type:'range',
            typedata:{
                min:parseInt(min),
                max:parseInt(max),
                interval:1,
                decimalplaces:0
            },
            defaultVal:val,
            width:'50px',
            backColor:"#000000"
        });

        this.smallDialog.dialog("open");
        $('.ui-dialog :button').blur();
    },

    multipleChoiceDecision:function (decision) {
        var id = decision.getAttribute("id");
        var text = decision.getAttribute("text");
        var results = this.getDecisionParameters(decision, "results");
        var defaultIndex = this.getDecisionParameter(decision, "defaultIndex");

        var that = this;
        this.smallDialog
                .html(text);

        if (results.length > 2 || this.settingsAlwaysDropDown || defaultIndex >= 0) {
            var html = "<br /><select id='multipleChoiceDecision'>";
            for (var i = 0; i < results.length; i++) {
                html += "<option " + (i == defaultIndex ? "selected='selected' " : "") + "value='" + i + "'>" + results[i] + "</option>";
            }
            html += "</select>";
            this.smallDialog.append(html);

            if (!this.replayMode) {
                this.smallDialog.dialog("option", "buttons",
                {
                    "OK":function () {
                        that.smallDialog.dialog("close");
                        that.decisionFunction(id, $("#multipleChoiceDecision").val());
                    }
                });
            }
        } else {
            this.smallDialog.append("<br />");
            for (var i = 0; i < results.length; i++) {
                if (i > 0)
                    this.smallDialog.append(" ");

                var but = $("<button></button>").html(results[i]).button();
                if (!this.replayMode) {
                    but.click(
                            (function (ind) {
                                return function () {
                                    that.smallDialog.dialog("close");
                                    that.decisionFunction(id, "" + ind);
                                }
                            })(i));
                }
                this.smallDialog.append(but);
            }
            if (!this.replayMode)
                this.smallDialog.dialog("option", "buttons", {});
        }

        this.smallDialog.dialog("open");
        $('.ui-dialog :button').blur();
    },

    createCardDiv:function (card, text) {
        var cardDiv = createCardDiv(card.imageUrl, card.testingText, text, card.isFoil(), false, false, card.incomplete);

        cardDiv.data("card", card);

        var that = this;
        var swipeOptions = {
            threshold:20,
            fallbackToMouseEvents:false,
            swipeUp:function (event) {
                var tar = $(event.target);
                if (tar.hasClass("actionArea")) {
                    var selectedCardElem = tar.closest(".card");
                    that.displayCardInfo(selectedCardElem.data("card"));
                }
                return false;
            },
            click:function (event) {
                return that.clickCardFunction(event);
            }
        };
        cardDiv.swipe(swipeOptions);

        return cardDiv;
    },

    attachSelectionFunctions:function (cardIds, selection, selected) {
        if (selected) {
            if (cardIds.length > 0) {
                $(".card:cardId(" + cardIds + ")").addClass("selectedCard");
            }
        }
        else if (selection) {
            if (cardIds.length > 0) {
                $(".card:cardId(" + cardIds + ")").addClass("selectableCard");
            }
        }
        else {
            if (cardIds.length > 0) {
                if (this.settingsCardActionsSilent) {
                    $(".card:cardId(" + cardIds + ")").addClass("actionableCardSilent");
                }
                else {
                    $(".card:cardId(" + cardIds + ")").addClass("actionableCard");
                }
            }
        }
    },

    // Choosing cards from a predefined selection (for example starting cards)
    arbitraryCardsDecision:function (decision) {
        var id = decision.getAttribute("id");
        var text = decision.getAttribute("text");

        var min = this.getDecisionParameter(decision, "min");
        var max = this.getDecisionParameter(decision, "max");
        var returnAnyChange = this.getDecisionParameter(decision, "returnAnyChange");
        var cardIds = this.getDecisionParameters(decision, "cardId");
        var blueprintIds = this.getDecisionParameters(decision, "blueprintId");
        var preselected = this.getDecisionParameters(decision, "preselected");
        var selectable = this.getDecisionParameters(decision, "selectable");
        var cardTexts = this.getDecisionParameters(decision, "cardText");
        var testingTexts = this.getDecisionParameters(decision, "testingText");
        var backSideTestingTexts = this.getDecisionParameters(decision, "backSideTestingText");

        var that = this;

        var selectedCardIds = new Array();

        var selectableCardIds = new Array();

        this.cardActionDialog
                .html("<div id='arbitraryChoice'></div>")
                .dialog("option", "title", text);

        if (blueprintIds.length < 6) {
            this.cardActionDialog
                    .dialog( "option", "height", 300 )
                    .dialog( "option", "width", 600 );
        }
        else if (blueprintIds.length < 12) {
            this.cardActionDialog
                    .dialog( "option", "height", 500 )
                    .dialog( "option", "width", 600 );
        }
        else {
            this.cardActionDialog
                    .dialog( "option", "height", 600 )
                    .dialog( "option", "width", 800 );
        }

        // Create the action cards and fill the dialog with them
        for (var i = 0; i < blueprintIds.length; i++) {
            var cardId = cardIds[i];
            var blueprintId = blueprintIds[i];
            var cardText = cardTexts[i];
            var testingText = testingTexts[i];
            if (testingText == "null") {
                testingText = null;
            }
            var backSideTestingText = backSideTestingTexts[i];
            if (backSideTestingText == "null") {
                backSideTestingText = null;
            }

            if (selectable[i] == "true")
                selectableCardIds.push(cardId);

            if (preselected[i] == "true") {
                selectedCardIds.push(cardId);
            }

            var card = new Card(blueprintId, testingText, backSideTestingText, "SPECIAL", cardId, null);

            var cardDiv = this.createCardDiv(card, cardText);

            $("#arbitraryChoice").append(cardDiv);
        }

        var finishChoice = function () {
            that.cardActionDialog.dialog("close");
            $("#arbitraryChoice").html("");
            that.clearSelection();
            that.decisionFunction(id, "" + selectedCardIds);
        };

        var resetChoice = function () {
            selectedCardIds = new Array();
            that.clearSelection();
            allowSelection();
            processButtons();
        };

        var processButtons = function () {
            var buttons = {};
            if (selectedCardIds.length > 0)
                buttons["Clear selection"] = function () {
                    // If return any change, then finish after any selection change.
                    if (returnAnyChange == "true") {
                        selectedCardIds = new Array();
                        that.clearSelection();
                        finishChoice();
                        return;
                    }
                    resetChoice();
                    processButtons();
                };
            if (selectedCardIds.length >= min)
                buttons["Done"] = function () {
                    finishChoice();
                };
            that.cardActionDialog.dialog("option", "buttons", buttons);
        };

        var allowSelection = function () {
            that.selectionFunction = function (cardId) {
                selectedCardIds.push(cardId);

                // If return any change, then finish after any selection change.
                if (returnAnyChange == "true") {
                    finishChoice();
                    return;
                }
                else if (selectedCardIds.length == max) {
                    if (that.settingsAutoAccept) {
                        finishChoice();
                        return;
                    } else {
                        that.clearSelection();
                        if (selectedCardIds.length > 0)
                            $(".card:cardId(" + selectedCardIds + ")").addClass("selectedCard");
                    }
                } else {
                    $(".card:cardId(" + cardId + ")").removeClass("selectableCard").addClass("selectedCard");
                }

                processButtons();
            };

            that.attachSelectionFunctions(selectableCardIds, true, false);
            that.attachSelectionFunctions(selectedCardIds, true, true);
        };

        allowSelection();
        if (!this.replayMode)
            processButtons();

        openSizeDialog(this.cardActionDialog);
        this.arbitraryDialogResize(false);
        $('.ui-dialog :button').blur();
    },

    // Starts a decision countdown.  If the countdown expires, then the decisionTimeout function is called.
    startDecisionCountdown: function(isDecisionDuringYourTurn, decisionTimeout) {
        var that = this;
        this.isDecisionDuringYourTurn = isDecisionDuringYourTurn;
        this.decisionTimeoutFunction = decisionTimeout;

        if (this.mouseInAlertBox && ((isDecisionDuringYourTurn && this.settingsAutoPassYourTurnEnabled) || (!isDecisionDuringYourTurn && this.settingsAutoPassOpponentsTurnEnabled))) {
            this.decisionCountdownId++;
            var newDecisionCountdownId = this.decisionCountdownId;
            this.decisionCountdownReadyToRestart = false;
            this.decisionCountdownInProgress = true;
            var timeLeftInMs = (isDecisionDuringYourTurn ? that.settingsAutoPassYourTurnCountdown : that.settingsAutoPassOpponentsTurnCountdown) * 1000;

            if (this.alertText != null) {
                this.alertText.html(this.alertTextMsg + "<br/><br/>&nbsp;&nbsp;<strong>(Seconds remaining:&nbsp;" + (timeLeftInMs / 1000) + ")</strong>&nbsp;");
            }
            setTimeout(function() {
                that.decrementDecisionCountdown(newDecisionCountdownId, decisionTimeout, timeLeftInMs);
            }, 1000);
        }
        else {
            this.decisionCountdownReadyToRestart = true;
            this.decisionCountdownInProgress = false;
        }
    },

    // Suspend the decision countdown (and can be restarted).
    suspendDecisionCountdown: function() {
        if (this.decisionCountdownInProgress) {
            this.decisionCountdownReadyToRestart = true;
            this.decisionCountdownInProgress = false;
            if (this.alertText != null) {
                this.alertText.html(this.alertTextMsg);
            }
        }
    },

    // Stops the decision countdown.
    stopDecisionCountdown: function() {
        if (this.decisionCountdownInProgress || this.decisionCountdownReadyToRestart) {
            this.decisionCountdownReadyToRestart = false;
            this.decisionCountdownInProgress = false;
            if (this.alertText != null) {
                this.alertText.html(this.alertTextMsg);
            }
        }
    },

    // Decrements the decision countdown (by 1000ms), and if reached 0, then calls the decisionTimeout function.
    decrementDecisionCountdown: function(decisionCountdownId, decisionTimeout, timeLeftInMs) {
        var that = this;
        if (this.decisionCountdownInProgress && decisionCountdownId == this.decisionCountdownId) {
            var newTimeLeftInMs = timeLeftInMs - 1000;
            if (newTimeLeftInMs <= 0) {
                that.stopDecisionCountdown();
                decisionTimeout();
            }
            else {
                if (this.alertText != null) {
                    this.alertText.html(this.alertTextMsg + "<br/><br/>&nbsp;&nbsp;<strong>(Seconds remaining:&nbsp;" + (newTimeLeftInMs / 1000) + ")</strong>&nbsp;");
                }
                setTimeout(function() {
                    that.decrementDecisionCountdown(decisionCountdownId, decisionTimeout, newTimeLeftInMs);
                }, 1000);
            }
        }
    },

    // Choosing one action to resolve, for example phase actions
    cardActionChoiceDecision:function (decision) {
        var id = decision.getAttribute("id");
        var text = decision.getAttribute("text");

        var cardIds = this.getDecisionParameters(decision, "cardId");
        var blueprintIds = this.getDecisionParameters(decision, "blueprintId");
        var actionIds = this.getDecisionParameters(decision, "actionId");
        var actionTexts = this.getDecisionParameters(decision, "actionText");
        var isYourTurn = this.getDecisionParameters(decision, "yourTurn");
        var isAutoPassEligible = this.getDecisionParameters(decision, "autoPassEligible");
        var noPass = this.getDecisionParameters(decision, "noPass");
        var noLongDelay = this.getDecisionParameters(decision, "noLongDelay");
        var isRevertEligible = this.getDecisionParameters(decision, "revertEligible");
        var testingTexts = this.getDecisionParameters(decision, "testingText");
        var backSideTestingTexts = this.getDecisionParameters(decision, "backSideTestingText");

        var that = this;

        if (cardIds.length == 0) {
            if (!this.replayMode) {
                // If there are no valid actions, then wait a brief period of time before invoking decision.
                // This is to simulate a user choosing to 'Pass' as if there were actions to choose from.
                // Otherwise, the opponent will see the decision made so quickly that the opponent could infer that
                // there were no valid actions to choose from.
                setTimeout(function() {
                    that.decisionFunction(id, "");
                }, this.settingsMimicDecisionDelayEnabled ? ((noLongDelay ? 1 : this.settingsMimicDecisionDelayTime) * 1000) : 250);
            }
            return;
        }

        var selectedCardIds = new Array();

        if (this.alertText != null) {
            this.alertTextMsg = text;
            this.alertText.html(this.alertTextMsg);
        }
        if (this.alertBox != null)
            this.alertBox.css({"border-radius":"0px", "border-color":"#7f7fff", "border-width":"1px"});

        var processButtons = function () {
            if (that.alertButtons != null)
                that.alertButtons.html("");
            if (noPass != "true" && selectedCardIds.length == 0) {
                if (that.alertButtons != null) {
                    that.alertButtons.append("<button id='Pass' style='font-size: 2em'>Pass</button>");
                    if (isRevertEligible == "true") {
                        that.alertButtons.append("<button id='Revert' style='float: right'>Revert</button>");
                    }
                }
                $("#Pass").button().click(function () {
                    finishChoice(false);
                });
                if (isRevertEligible == "true") {
                    $("#Revert").button().click(function () {
                        finishChoice(true);
                    });
                }
            }
            if (selectedCardIds.length > 0) {
                if (that.alertButtons != null) {
                    that.alertButtons.append("<button id='ClearSelection'>Reset choice</button>");
                    that.alertButtons.append("<button id='Done' style='float: right'>Done</button>");
                }
                $("#Done").button().click(function () {
                    finishChoice(false);
                });
                $("#ClearSelection").button().click(function () {
                    resetChoice();
                });
            }
        };

        var finishChoice = function (isRevert) {
            that.stopDecisionCountdown();
            if (that.alertText != null) {
                that.alertTextMsg = "";
                that.alertText.html(that.alertTextMsg);
            }
            if (that.alertBox != null)
                that.alertBox.css({"border-radius":"0px", "border-color":"", "border-width":"1px"});
            if (that.alertButtons != null)
                that.alertButtons.html("");
            that.clearSelection();
            $(".card").each(
                    function () {
                        var card = $(this).data("card");
                        if (card.zone == "EXTRA")
                            $(this).remove();
                    });
            that.extraActionsGroup.layoutCards();
            if (isRevert) {
                that.decisionFunction(id, "revert");
            }
            else {
                that.decisionFunction(id, "" + selectedCardIds);
           }
        };

        var resetChoice = function () {
            selectedCardIds = new Array();
            that.clearSelection();
            allowSelection();
            processButtons();
        };

        var allowSelection = function () {
            var hasVirtual = false;

            for (var i = 0; i < cardIds.length; i++) {
                var cardId = cardIds[i];
                var actionId = actionIds[i];
                var actionText = actionTexts[i];
                var blueprintId = blueprintIds[i];
                var testingText = testingTexts[i];
                if (testingText == "null") {
                    testingText = null;
                }
                var backSideTestingText = backSideTestingTexts[i];
                if (backSideTestingText == "null") {
                    backSideTestingText = null;
                }

                if (blueprintId == "inPlay") {
                    var cardIdElem = $(".card:cardId(" + cardId + ")");
                    if (cardIdElem.data("action") == null) {
                        cardIdElem.data("action", new Array());
                    }

                    var actions = cardIdElem.data("action");
                    actions.push({ actionId:actionId, actionText:actionText });
                } else {
                    hasVirtual = true;
                    cardIds[i] = "extra" + cardId;
                    var card = new Card(blueprintId, testingText, backSideTestingText, "EXTRA", "extra" + cardId, null);

                    var cardDiv = that.createCardDiv(card);
                    $(cardDiv).css({opacity:"0.8"});

                    $("#main").append(cardDiv);

                    var cardIdElem = $(".card:cardId(extra" + cardId + ")");
                    if (cardIdElem.data("action") == null) {
                        cardIdElem.data("action", new Array());
                    }

                    var actions = cardIdElem.data("action");
                    actions.push({ actionId:actionId, actionText:actionText });
                }
            }

            if (hasVirtual) {
                that.extraActionsGroup.layoutCards();
            }

            that.selectionFunction = function (cardId, event) {
                var cardIdElem = $(".card:cardId(" + cardId + ")");
                var actions = cardIdElem.data("action");

                var selectActionFunction = function (actionId) {
                    selectedCardIds.push(actionId);
                    if (that.settingsAutoAccept) {
                        finishChoice();
                    } else {
                        that.clearSelection();
                        $(".card:cardId(" + cardId + ")").addClass("selectedCard");
                        processButtons();
                    }
                };

                var card = cardIdElem.data("card");

                if (actions.length == 1 && (card.zone == "TOP_OF_RESERVE_DECK" || card.zone == "TOP_OF_FORCE_PILE")) {
                    var action = actions[0];
                    selectActionFunction(action.actionId);
                } else {
                    that.createActionChoiceContextMenu(actions, event, selectActionFunction, card);
                }
            };

            that.attachSelectionFunctions(cardIds, false, false);
            if (isAutoPassEligible == "true") {
                that.startDecisionCountdown(isYourTurn == "true", finishChoice);
            }
        };

        allowSelection();
        if (!this.replayMode)
            processButtons();

        $(':button').blur();
    },

    createActionChoiceContextMenu:function (actions, event, selectActionFunction, card) {
        var that = this;

        // Remove context menus that may be showing
        $(".contextMenu").remove();

        var div = $("<ul class='contextMenu'></ul>");
        var prevActionText = null;
        for (var i = 0; i < actions.length; i++) {
            var action = actions[i];
            var text = action.actionText;
            if (text != prevActionText || (!card.zone == "TOP_OF_RESERVE_DECK" && !card.zone == "TOP_OF_FORCE_PILE")) {
                div.append("<li><a href='#" + action.actionId + "'>" + text + "</a></li>");
            }
            prevActionText = text;
        }

        $("#main").append(div);
        // While an action choice menu is being shown, disable the click to zoom location
        that.clickToZoomLocationDisabled = true;

        var contextMenuWidth = 250;
        var x = event.pageX;
        var y = event.pageY;
        if ((x + contextMenuWidth) > this.windowWidth) {
            x = event.pageX - contextMenuWidth;
        }
        $(div).css({left:x, top:y}).fadeIn(150);

        $(div).find('A').mouseover(
                function () {
                    $(div).find('LI.hover').removeClass('hover');
                    $(this).parent().addClass('hover');
                }).mouseout(function () {
            $(div).find('LI.hover').removeClass('hover');
        });

        var getRidOfContextMenu = function () {
            $(div).remove();
            $(document).unbind("click", getRidOfContextMenu);

            // Now that an action choice menu is no longer being shown, enable the click to zoom location
            that.clickToZoomLocationDisabled = false;

            return false;
        };

        // When items are selected
        $(div).find('A').unbind('click');
        $(div).find('LI:not(.disabled) A').click(function () {
            $(document).unbind('click', getRidOfContextMenu);
            $(".contextMenu").remove();

            // Now that an action choice menu is no longer being shown, enable the click to zoom location
            that.clickToZoomLocationDisabled = false;

            var actionId = $(this).attr('href').substr(1);
            selectActionFunction(actionId);
            return false;
        });

        // Hide bindings
        setTimeout(function () { // Delay for Mozilla
            $(document).click(getRidOfContextMenu);
        }, 0);
    },

    // Choosing one action to resolve, for example required triggered actions
    actionChoiceDecision:function (decision) {
        var id = decision.getAttribute("id");
        var text = decision.getAttribute("text");

        var blueprintIds = this.getDecisionParameters(decision, "blueprintId");
        var actionIds = this.getDecisionParameters(decision, "actionId");
        var actionTexts = this.getDecisionParameters(decision, "actionText");
        var testingTexts = this.getDecisionParameters(decision, "testingText");
        var backSideTestingTexts = this.getDecisionParameters(decision, "backSideTestingText");

        var that = this;

        var selectedActionIds = new Array();

        this.cardActionDialog
                .html("<div id='arbitraryChoice'></div>")
                .dialog("option", "title", text);

        var cardIds = new Array();

        for (var i = 0; i < blueprintIds.length; i++) {
            var blueprintId = blueprintIds[i];
            var testingText = testingTexts[i];
            if (testingText == "null") {
                testingText = null;
            }
            var backSideTestingText = backSideTestingTexts[i];
            if (backSideTestingText == "null") {
                backSideTestingText = null;
            }

            cardIds.push("temp" + i);
            var card = new Card(blueprintId, testingText, backSideTestingText, "SPECIAL", "temp" + i, null);

            var cardDiv = this.createCardDiv(card, actionTexts[i]);

            $("#arbitraryChoice").append(cardDiv);
        }

        var finishChoice = function () {
            that.cardActionDialog.dialog("close");
            $("#arbitraryChoice").html("");
            that.clearSelection();
            that.decisionFunction(id, "" + selectedActionIds);
        };

        var resetChoice = function () {
            selectedActionIds = new Array();
            that.clearSelection();
            allowSelection();
            processButtons();
        };

        var processButtons = function () {
            var buttons = {};
            if (selectedActionIds.length > 0) {
                buttons["Clear selection"] = function () {
                    resetChoice();
                    processButtons();
                };
                buttons["Done"] = function () {
                    finishChoice();
                };
            }
            that.cardActionDialog.dialog("option", "buttons", buttons);
        };

        var allowSelection = function () {
            that.selectionFunction = function (cardId) {
                var actionId = actionIds[parseInt(cardId.substring(4))];
                selectedActionIds.push(actionId);

                that.clearSelection();

                if (this.settingsAutoAccept) {
                    finishChoice();
                } else {
                    processButtons();
                    $(".card:cardId(" + cardId + ")").addClass("selectedCard");
                }
            };

            that.attachSelectionFunctions(cardIds, true, false);
        };

        allowSelection();
        if (!this.replayMode)
            processButtons();

        openSizeDialog(this.cardActionDialog);
        this.arbitraryDialogResize(false);
        $('.ui-dialog :button').blur();
    },

    // Choosing some number of cards, for example to target
    cardSelectionDecision:function (decision) {
        var id = decision.getAttribute("id");
        var text = decision.getAttribute("text");

        var min = this.getDecisionParameter(decision, "min");
        var max = this.getDecisionParameter(decision, "max");
        var cardIds = this.getDecisionParameters(decision, "cardId");

        var that = this;

        if (this.alertText != null) {
            this.alertTextMsg = text;
            this.alertText.html(this.alertTextMsg);
        }
        if (this.alertBox != null)
            this.alertBox.css({"border-radius":"0px", "border-color":"#7faf7f", "border-width":"2px"});

        var selectedCardIds = new Array();

        var finishChoice = function () {
            if (that.alertText != null) {
                that.alertTextMsg = "";
                that.alertText.html(that.alertTextMsg);
            }
            if (that.alertBox != null)
                that.alertBox.css({"border-radius":"0px", "border-color":"", "border-width":"1px"});
            if (that.alertButtons != null)
                that.alertButtons.html("");
            that.clearSelection();
            that.decisionFunction(id, "" + selectedCardIds);
        };

        var resetChoice = function () {
            selectedCardIds = new Array();
            that.clearSelection();
            allowSelection();
            processButtons();
        };

        var processButtons = function () {
            if (that.alertButtons != null)
                that.alertButtons.html("");
            if (selectedCardIds.length > 0) {
                if (that.alertButtons != null)
                    that.alertButtons.append("<button id='ClearSelection'>Reset choice</button>");
                $("#ClearSelection").button().click(function () {
                    resetChoice();
                });
            }
            if (selectedCardIds.length >= min) {
                if (that.alertButtons != null)
                    that.alertButtons.append("<button id='Done' style='float: right'>Done</button>");
                $("#Done").button().click(function () {
                    finishChoice();
                });
            }
        };

        var allowSelection = function () {
            that.selectionFunction = function (cardId) {
                selectedCardIds.push(cardId);
                if (selectedCardIds.length == max) {
                    if (this.settingsAutoAccept) {
                        finishChoice();
                        return;
                    } else {
                        that.clearSelection();
                        if (selectedCardIds.length > 0)
                            $(".card:cardId(" + selectedCardIds + ")").addClass("selectedCard");
                    }
                } else {
                    $(".card:cardId(" + cardId + ")").removeClass("selectableCard").addClass("selectedCard");
                }

                processButtons();
            };

            that.attachSelectionFunctions(cardIds, true, false);
        };

        allowSelection();
        if (!this.replayMode)
            processButtons();
    },

    addLocationDiv:function (index, systemName) {
        var that = this;

        // Increment locationIndex for existing cards on the table to the right of the added location
        var locationBeforeCount = this.locationDivs.length;
        for (var i=locationBeforeCount-1; i>=index; i--) {
            this.locationDivs[i].data( "locationIndex", i+1)

            var otherCards1 = this.opponentSideOfLocationGroups[i].getCardElems();
            for (var j=0; j<otherCards1.length; j++) {
                var cardData = $(otherCards1[j]).data("card");
                cardData.locationIndex = i+1;
            }
            this.opponentSideOfLocationGroups[i].locationIndex = i+1;

            var otherCards2 = this.opponentInBattleGroups[i].getCardElems();
            for (var j=0; j<otherCards2.length; j++) {
                var cardData = $(otherCards2[j]).data("card");
                cardData.locationIndex = i+1;
            }
            this.opponentInBattleGroups[i].locationIndex = i+1;

            var otherCards3 = this.opponentInDuelOrLightsaberCombatGroups[i].getCardElems();
            for (var j=0; j<otherCards3.length; j++) {
                var cardData = $(otherCards3[j]).data("card");
                cardData.locationIndex = i+1;
            }
            this.opponentInDuelOrLightsaberCombatGroups[i].locationIndex = i+1;

            var otherCards4 = this.locationCardGroups[i].getCardElems();
            for (var j=0; j<otherCards4.length; j++) {
                var cardData = $(otherCards4[j]).data("card");
                cardData.locationIndex = i+1;
            }
            this.locationCardGroups[i].locationIndex = i+1;

            var otherCards5 = this.playerInDuelOrLightsaberCombatGroups[i].getCardElems();
            for (var j=0; j<otherCards5.length; j++) {
                var cardData = $(otherCards5[j]).data("card");
                cardData.locationIndex = i+1;
            }
            this.playerInDuelOrLightsaberCombatGroups[i].locationIndex = i+1;

            var otherCards6 = this.playerInBattleGroups[i].getCardElems();
            for (var j=0; j<otherCards6.length; j++) {
                var cardData = $(otherCards6[j]).data("card");
                cardData.locationIndex = i+1;
            }
            this.playerInBattleGroups[i].locationIndex = i+1;

            var otherCards7 = this.playerSideOfLocationGroups[i].getCardElems();
            for (var j=0; j<otherCards7.length; j++) {
                var cardData = $(otherCards7[j]).data("card");
                cardData.locationIndex = i+1;
            }
            this.playerSideOfLocationGroups[i].locationIndex = i+1;

            var otherCards8 = this.attackingInAttackGroups[i].getCardElems();
            for (var j=0; j<otherCards8.length; j++) {
                var cardData = $(otherCards8[j]).data("card");
                cardData.locationIndex = i+1;
            }
            this.attackingInAttackGroups[i].locationIndex = i+1;

            var otherCards9 = this.defendingInAttackGroups[i].getCardElems();
            for (var j=0; j<otherCards9.length; j++) {
                var cardData = $(otherCards9[j]).data("card");
                cardData.locationIndex = i+1;
            }
            this.defendingInAttackGroups[i].locationIndex = i+1;
        }

        if (this.zoomedInLocationIndex != null && this.zoomedInLocationIndex >= index) {
            this.zoomedInLocationIndex++;
        }

        if (this.battleLocationIndex != null && this.battleLocationIndex >= index) {
            this.battleLocationIndex++;
        }

        if (this.attackLocationIndex != null && this.attackLocationIndex >= index) {
            this.attackLocationIndex++;
        }

        if (this.duelOrLightsaberCombatLocationIndex != null && this.duelOrLightsaberCombatLocationIndex >= index) {
            this.duelOrLightsaberCombatLocationIndex++;
        }

        var newDiv = $("<div class='ui-widget-content locationDiv'></div>");
        newDiv.css({"border-radius":"0px", "border-color":"#111111", "border-width":"2px"});
        newDiv.data( "locationIndex", index);
        newDiv.data( "systemName", systemName);
        $("#main").append(newDiv);

        this.locationDivs.splice(index, 0, newDiv);

        var newLightPowerAtLocationDiv = $("<div class='powerAtLocationDiv'></div>");
        newLightPowerAtLocationDiv.css({visibility:"hidden"});
        newDiv.append(newLightPowerAtLocationDiv);
        this.lightPowerAtLocationDivs.splice(index, 0, newLightPowerAtLocationDiv);

        var newDarkPowerAtLocationDiv = $("<div class='powerAtLocationDiv'></div>");
        newDarkPowerAtLocationDiv.css({visibility:"hidden"});
        newDiv.append(newDarkPowerAtLocationDiv);
        this.darkPowerAtLocationDivs.splice(index, 0, newDarkPowerAtLocationDiv);

        var newGrp1 = new TableCardGroup($("#main"), function (card) {
            return (card.zone == "AT_LOCATION" && card.locationIndex == this.locationIndex && card.owner != that.bottomPlayerId && card.inBattle != true && card.inDuelOrLightsaberCombat != true && card.attackingInAttack != true && card.defendingInAttack != true);
        }, false, index, null);
        this.opponentSideOfLocationGroups.splice(index, 0, newGrp1);

        var newGrp2 = new TableCardGroup($("#main"), function (card) {
            return (card.zone == "AT_LOCATION" && card.locationIndex == this.locationIndex && card.owner != that.bottomPlayerId && card.inBattle == true && card.inDuelOrLightsaberCombat != true);
        }, false, index, null);
        this.opponentInBattleGroups.splice(index, 0, newGrp2);

        var newGrp3 = new TableCardGroup($("#main"), function (card) {
            return (card.zone == "AT_LOCATION" && card.locationIndex == this.locationIndex && card.owner != that.bottomPlayerId && card.inDuelOrLightsaberCombat == true);
        }, false, index, null);
        this.opponentInDuelOrLightsaberCombatGroups.splice(index, 0, newGrp3);

        var newGrp4 = new TableCardGroup($("#main"), function (card) {
            return (card.zone == "LOCATIONS" && card.locationIndex == this.locationIndex );
        }, false, index, this.bottomPlayerId);
        this.locationCardGroups.splice(index, 0, newGrp4);

        var newGrp5 = new TableCardGroup($("#main"), function (card) {
            return (card.zone == "AT_LOCATION" && card.locationIndex == this.locationIndex && card.owner == that.bottomPlayerId && card.inDuelOrLightsaberCombat == true);
        }, false, index, null);
        this.playerInDuelOrLightsaberCombatGroups.splice(index, 0, newGrp5);

        var newGrp6 = new TableCardGroup($("#main"), function (card) {
            return (card.zone == "AT_LOCATION" && card.locationIndex == this.locationIndex && card.owner == that.bottomPlayerId && card.inBattle == true && card.inDuelOrLightsaberCombat != true);
        }, false, index, null);
        this.playerInBattleGroups.splice(index, 0, newGrp6);

        var newGrp7 = new TableCardGroup($("#main"), function (card) {
            return (card.zone == "AT_LOCATION" && card.locationIndex == this.locationIndex && card.owner == that.bottomPlayerId && card.inBattle != true && card.inDuelOrLightsaberCombat != true && card.attackingInAttack != true && card.defendingInAttack != true);
        }, false, index, null);
        this.playerSideOfLocationGroups.splice(index, 0, newGrp7);

        var newGrp8 = new TableCardGroup($("#main"), function (card) {
            return (card.zone == "AT_LOCATION" && card.locationIndex == this.locationIndex && card.attackingInAttack == true);
        }, false, index, null);
        this.attackingInAttackGroups.splice(index, 0, newGrp8);

        var newGrp9 = new TableCardGroup($("#main"), function (card) {
            return (card.zone == "AT_LOCATION" && card.locationIndex == this.locationIndex && card.defendingInAttack == true);
        }, false, index, null);
        this.defendingInAttackGroups.splice(index, 0, newGrp9);

        this.layoutUI(false);
    },

    removeLocationDiv:function (index) {
        var that = this;

        // Remove the groups for this location index from the array (and the location div from the DOM)
        this.locationDivs.splice(index, 1)[0].remove();
        this.opponentSideOfLocationGroups.splice(index, 1);
        this.opponentInBattleGroups.splice(index, 1);
        this.opponentInDuelOrLightsaberCombatGroups.splice(index, 1);
        this.locationCardGroups.splice(index, 1);
        this.playerInDuelOrLightsaberCombatGroups.splice(index, 1);
        this.playerInBattleGroups.splice(index, 1);
        this.playerSideOfLocationGroups.splice(index, 1);
        this.darkPowerAtLocationDivs.splice(index, 1)[0].remove();
        this.lightPowerAtLocationDivs.splice(index, 1)[0].remove();
        this.attackingInAttackGroups.splice(index, 1);
        this.defendingInAttackGroups.splice(index, 1);

        // Decrement locationIndex for existing cards on the table to the right of the added location
        var locationAfterCount = this.locationDivs.length;
        for (var i=index; i < locationAfterCount; i++) {
            this.locationDivs[i].data( "locationIndex", i)

            var otherCards1 = this.opponentSideOfLocationGroups[i].getCardElems();
            for (var j=0; j<otherCards1.length; j++) {
                var cardData = $(otherCards1[j]).data("card");
                cardData.locationIndex = i;
            }
            this.opponentSideOfLocationGroups[i].locationIndex = i;

            var otherCards2 = this.opponentInBattleGroups[i].getCardElems();
            for (var j=0; j<otherCards2.length; j++) {
                var cardData = $(otherCards2[j]).data("card");
                cardData.locationIndex = i;
            }
            this.opponentInBattleGroups[i].locationIndex = i;

            var otherCards3 = this.opponentInDuelOrLightsaberCombatGroups[i].getCardElems();
            for (var j=0; j<otherCards3.length; j++) {
                var cardData = $(otherCards3[j]).data("card");
                cardData.locationIndex = i;
            }
            this.opponentInDuelOrLightsaberCombatGroups[i].locationIndex = i;

            var otherCards4 = this.locationCardGroups[i].getCardElems();
            for (var j=0; j<otherCards4.length; j++) {
                var cardData = $(otherCards4[j]).data("card");
                cardData.locationIndex = i;
            }
            this.locationCardGroups[i].locationIndex = i;

            var otherCards5 = this.playerInDuelOrLightsaberCombatGroups[i].getCardElems();
            for (var j=0; j<otherCards5.length; j++) {
                var cardData = $(otherCards5[j]).data("card");
                cardData.locationIndex = i;
            }
            this.playerInDuelOrLightsaberCombatGroups[i].locationIndex = i;

            var otherCards6 = this.playerInBattleGroups[i].getCardElems();
            for (var j=0; j<otherCards6.length; j++) {
                var cardData = $(otherCards6[j]).data("card");
                cardData.locationIndex = i;
            }
            this.playerInBattleGroups[i].locationIndex = i;

            var otherCards7 = this.playerSideOfLocationGroups[i].getCardElems();
            for (var j=0; j<otherCards7.length; j++) {
                var cardData = $(otherCards7[j]).data("card");
                cardData.locationIndex = i;
            }
            this.playerSideOfLocationGroups[i].locationIndex = i;

            var otherCards8 = this.attackingInAttackGroups[i].getCardElems();
            for (var j=0; j<otherCards8.length; j++) {
                var cardData = $(otherCards8[j]).data("card");
                cardData.locationIndex = i;
            }
            this.attackingInAttackGroups[i].locationIndex = i;

            var otherCards9 = this.defendingInAttackGroups[i].getCardElems();
            for (var j=0; j<otherCards9.length; j++) {
                var cardData = $(otherCards9[j]).data("card");
                cardData.locationIndex = i;
            }
            this.defendingInAttackGroups[i].locationIndex = i;
        }

        if (this.zoomedInLocationIndex != null && this.zoomedInLocationIndex > index) {
            this.zoomedInLocationIndex--;
        }

        if (this.battleLocationIndex != null && this.battleLocationIndex > index) {
            this.battleLocationIndex--;
        }

        if (this.attackLocationIndex != null && this.attackLocationIndex > index) {
            this.attackLocationIndex--;
        }

        if (this.duelOrLightsaberCombatLocationIndex != null && this.duelOrLightsaberCombatLocationIndex > index) {
            this.duelOrLightsaberCombatLocationIndex--;
        }
    },

    clearSelection:function () {
        $(".selectableCard").removeClass("selectableCard").data("action", null);
        $(".actionableCard").removeClass("actionableCard").removeClass("actionableCardSilent").data("action", null);
        $(".selectedCard").removeClass("selectedCard");
        this.selectionFunction = null;
    },

    dialogResize:function (dialog, group) {
        var width = dialog.width() + 10;
        var height = dialog.height() + 10;
        group.setBounds(this.padding, this.padding, width - 2 * this.padding, height - 2 * this.padding);
    },

    arbitraryDialogResize:function (texts) {
        if (texts) {
            var width = this.cardActionDialog.width() + 10;
            var height = this.cardActionDialog.height() - 10;
            this.specialGroup.setBounds(this.padding, this.padding, width - 2 * this.padding, height - 2 * this.padding);
        } else
            this.dialogResize(this.cardActionDialog, this.specialGroup);
    }
});
