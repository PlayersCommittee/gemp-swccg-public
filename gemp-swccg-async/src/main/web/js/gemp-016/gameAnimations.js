var GameAnimations = Class.extend({
    game:null,
    replaySpeed:1,
    playEventDuration:1000,
    putCardIntoPlayDurationFast:550,
    putCardIntoPlayDurationSlow:900,
    cardAffectsCardDuration:1000,
    destinyDrawnDuration:1000,
    cardActivatedDuration:1100,
    decisionDuration:1000,
    removeCardFromPlayDuration:750,
    drawDestinyLeft:null,
    drawDestinyTop:null,

    init:function (gameUI) {
        this.game = gameUI;
    },

    getAnimationLength:function (origValue) {
        if (this.game.replayMode)
            return origValue * this.replaySpeed;
        return origValue;
    },

    cardActivated:function (element, animate) {
        if (animate) {
            var that = this;

            var participantId = element.getAttribute("participantId");
            var cardId = element.getAttribute("cardId");

            // Play-out game event animation to both players
            $("#main").queue(
                function (next) {
                    var cardDiv = $(".card:cardId(" + cardId + ")");
                    if (cardDiv.length > 0) {
                        $(".borderOverlay", cardDiv)
                            .switchClass("borderOverlay", "highlightBorderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6))
                            .switchClass("highlightBorderOverlay", "borderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6))
                            .switchClass("borderOverlay", "highlightBorderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6))
                            .switchClass("highlightBorderOverlay", "borderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6))
                            .switchClass("borderOverlay", "highlightBorderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6))
                            .switchClass("highlightBorderOverlay", "borderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6), next);
                    }
                    else {
                        next();
                    }
                });
        }
    },

    destinyDrawn:function (element, animate) {
        if (animate) {
            var that = this;

            var participantId = element.getAttribute("participantId");
            var blueprintId = element.getAttribute("blueprintId");
            var destinyText = element.getAttribute("destinyText");
            var testingText = element.getAttribute("testingText");
            var backSideTestingText = element.getAttribute("backSideTestingText");

            // Play-out game event animation to both players
            var card = new Card(blueprintId, testingText, backSideTestingText, "ANIMATION", "anim", participantId);
            var cardDiv = createSimpleCardDiv(card.imageUrl, card.testingText, card.foil, card.incomplete, 16);
            // Overlay destiny card with text explaining destiny type
            var destinyTypeOverlayDiv = $("<div class='destinyTypeOverlay'></div>");
            destinyTypeOverlayDiv.text(destinyText);
            cardDiv.append(destinyTypeOverlayDiv);

            $("#main").queue(
                function (next) {
                    cardDiv.data("card", card);
                    $("#main").append(cardDiv);

                    var gameWidth = $("#main").width();
                    var gameHeight = $("#main").height();

                    var cardHeight = (gameHeight / 2);
                    var cardWidth = card.getWidthForHeight(cardHeight);

                    $(cardDiv).css(
                        {
                            position:"absolute",
                            left:(gameWidth / 2 - cardWidth / 4),
                            top:gameHeight * (3 / 8),
                            width:cardWidth / 2,
                            height:cardHeight / 2,
                            "z-index":100,
                            opacity:0});

                    $(cardDiv).animate(
                        {
                            left:"-=" + cardWidth / 4,
                            top:"-=" + (gameHeight / 8),
                            width:"+=" + (cardWidth / 2),
                            height:"+=" + (cardHeight / 2),
                            opacity:1},
                        {
                            duration:that.getAnimationLength(that.destinyDrawnDuration / 5),
                            easing:"linear",
                            queue:false,
                            complete:next});
                }).queue(
                function (next) {
                    setTimeout(next, that.getAnimationLength(4 * that.destinyDrawnDuration / 5));
                }).queue(
                function (next) {
                    $(cardDiv).remove();
                    next();
                });
        }
    },

    interruptPlayed:function (element, animate) {
        if (animate) {
            var that = this;

            var participantId = element.getAttribute("participantId");
            var blueprintId = element.getAttribute("blueprintId");
            var testingText = element.getAttribute("testingText");
            var backSideTestingText = element.getAttribute("backSideTestingText");

            // Play-out game event animation to both players
            var card = new Card(blueprintId, testingText, backSideTestingText, "ANIMATION", "anim", participantId);
            var cardDiv = createSimpleCardDiv(card.imageUrl, card.testingText, card.foil, card.incomplete, 16);

            $("#main").queue(
                function (next) {
                    cardDiv.data("card", card);
                    $("#main").append(cardDiv);

                    var gameWidth = $("#main").width();
                    var gameHeight = $("#main").height();

                    var cardHeight = (gameHeight / 2);
                    var cardWidth = card.getWidthForHeight(cardHeight);

                    $(cardDiv).css(
                        {
                            position:"absolute",
                            left:(gameWidth / 2 - cardWidth / 4),
                            top:gameHeight * (3 / 8),
                            width:cardWidth / 2,
                            height:cardHeight / 2,
                            "z-index":100,
                            opacity:0});

                    $(cardDiv).animate(
                        {
                            left:"-=" + cardWidth / 4,
                            top:"-=" + (gameHeight / 8),
                            width:"+=" + (cardWidth / 2),
                            height:"+=" + (cardHeight / 2),
                            opacity:1},
                        {
                            duration:that.getAnimationLength(that.playEventDuration / 8),
                            easing:"linear",
                            queue:false,
                            complete:next});
                }).queue(
                function (next) {
                    setTimeout(next, that.getAnimationLength(that.playEventDuration * (5 / 8)));
                }).queue(
                function (next) {
                    $(cardDiv).animate(
                        {
                            opacity:0},
                        {
                            duration:that.getAnimationLength(that.playEventDuration / 4),
                            easing:"easeOutQuart",
                            queue:false,
                            complete:next
                        });
                }).queue(
                function (next) {
                    $(cardDiv).remove();
                    next();
                });
        }
    },

    cardAffectsCard:function (element, animate) {
        if (animate) {
            var that = this;

            var participantId = element.getAttribute("participantId");
            var blueprintId = element.getAttribute("blueprintId");
            var targetCardIds = element.getAttribute("otherCardIds").split(",");
            var testingText = element.getAttribute("testingText");
            var backSideTestingText = element.getAttribute("backSideTestingText");

            // Play-out game event animation to both players
            $("#main").queue(
                function (next) {
                    for (var i = 0; i < targetCardIds.length; i++) {
                        var targetCardId = targetCardIds[i];

                        var card = new Card(blueprintId, testingText, backSideTestingText, "ANIMATION", "anim" + i, participantId);
                        var cardDiv = createSimpleCardDiv(card.imageUrl, card.testingText, card.foil, card.incomplete, 6);

                        var targetCard = $(".card:cardId(" + targetCardId + ")");
                        if (targetCard.length > 0) {
                            cardDiv.data("card", card);
                            $("#main").append(cardDiv);

                            targetCard = targetCard[0];
                            var targetCardWidth = $(targetCard).width();
                            var targetCardHeight = $(targetCard).height();

                            var shadowStartPosX;
                            var shadowStartPosY;
                            var shadowWidth;
                            var shadowHeight;
                            if (card.horizontal != $(targetCard).data("card").horizontal) {
                                shadowWidth = targetCardHeight;
                                shadowHeight = targetCardWidth;
                                shadowStartPosX = $(targetCard).position().left - (shadowWidth - targetCardWidth) / 2;
                                shadowStartPosY = $(targetCard).position().top - (shadowHeight - targetCardHeight) / 2;
                            } else {
                                shadowWidth = targetCardWidth;
                                shadowHeight = targetCardHeight;
                                shadowStartPosX = $(targetCard).position().left;
                                shadowStartPosY = $(targetCard).position().top;
                            }

                            $(cardDiv).css(
                                {
                                    position:"absolute",
                                    left:shadowStartPosX,
                                    top:shadowStartPosY,
                                    width:shadowWidth,
                                    height:shadowHeight,
                                    "z-index":100,
                                    opacity:1});
                            $(cardDiv).animate(
                                {
                                    opacity:0,
                                    left:"-=" + (shadowWidth / 2),
                                    top:"-=" + (shadowHeight / 2),
                                    width:"+=" + shadowWidth,
                                    height:"+=" + shadowHeight},
                                {
                                    duration:that.getAnimationLength(that.cardAffectsCardDuration),
                                    easing:"easeInQuart",
                                    queue:false,
                                    complete:null});
                        }
                    }

                    setTimeout(next, that.getAnimationLength(that.cardAffectsCardDuration));
                }).queue(
                function (next) {
                    $(".card").each(
                        function () {
                            var cardData = $(this).data("card");
                            if (cardData.zone == "ANIMATION") {
                                $(this).remove();
                            }
                        }
                    );
                    next();
                });
        }
    },

    putCardInPlay:function (element, animate) {
        var participantId = element.getAttribute("participantId");
        var cardId = element.getAttribute("cardId");
        var zone = element.getAttribute("zone");
        var zoneOwnerId = element.getAttribute("zoneOwnerId");
        var locationIndex = element.getAttribute("locationIndex");
        var systemName = element.getAttribute("systemName");
        var eventType = element.getAttribute("type");
        var inverted = element.getAttribute("inverted");
        var sideways = element.getAttribute("sideways");
        var isFrozen = element.getAttribute("frozen");
        var isSuspended = element.getAttribute("suspended");
        var isCollapsed = element.getAttribute("collapsed");
        var phase = element.getAttribute("phase");

        var that = this;
        $("#main").queue(
            function (next) {
                var blueprintId = element.getAttribute("blueprintId");
                var targetCardId = element.getAttribute("targetCardId");
                var testingText = element.getAttribute("testingText");
                var backSideTestingText = element.getAttribute("backSideTestingText");

                // Only add location div if this is not a replacement (conversion) of a location
                if (zone == "LOCATIONS" && eventType != "RCIP") {
                    that.game.addLocationDiv(locationIndex, systemName);
                }

                // Check if card is supposed to be upside down
                var upsideDown = false;
                if (inverted == "true") {
                    upsideDown = true;
                }

                // Check if card is supposed to be sideways
                var onSide = false;
                if (sideways == "true") {
                    onSide = true;
                }

                // Check if card is supposed to be frozen
                var frozen = false;
                if (isFrozen == "true") {
                    frozen = true;
                }

                // Check if card is supposed to be suspended (or turned off)
                var suspended = false;
                if (isSuspended == "true") {
                    suspended = true;
                }

                // Check if card is supposed to be collapsed
                var collapsed = false;
                if (isCollapsed == "true") {
                    collapsed = true;
                }

                var card = new Card(blueprintId, testingText, backSideTestingText, zone, cardId, zoneOwnerId, locationIndex, upsideDown, onSide, frozen, suspended, collapsed);

                var cardDiv = that.game.createCardDiv(card, null);

                if (zone == "OUT_OF_PLAY") {
                    that.game.outOfPlayPileDialogs[zoneOwnerId].append(cardDiv);
                }
                else {
                    $("#main").append(cardDiv);
                }

                if (targetCardId != null) {
                    var targetCardData = $(".card:cardId(" + targetCardId + ")").data("card");
                    targetCardData.attachedCards.push(cardDiv);
                }

                next();
            });

        $("#main").queue(
            function (next) {
                that.game.layoutGroupWithCard(cardId);
                next();
            });

        if (animate
            && zone != "OUT_OF_PLAY" && zone != "LOST_PILE" && zone != "TOP_OF_LOST_PILE" && zone != "USED_PILE" && zone != "TOP_OF_USED_PILE"
            && zone != "FORCE_PILE" && zone != "TOP_OF_FORCE_PILE" && zone != "RESERVE_DECK" && zone != "TOP_OF_RESERVE_DECK" && zone != "HAND"
            && zone != "SABACC_HAND" && zone != "REVEALED_SABACC_HAND") {
            var oldValues = {};
            var putCardIntoPlayDuration = (phase == "PLAY_STARTING_CARDS") ? that.putCardIntoPlayDurationFast : that.putCardIntoPlayDurationSlow;

            $("#main").queue(
                function (next) {
                    var cardDiv = $(".card:cardId(" + cardId + ")");
                    var card = cardDiv.data("card");
                    var pos = cardDiv.position();

                    oldValues["zIndex"] = cardDiv.css("zIndex");
                    oldValues["left"] = pos.left;
                    oldValues["top"] = pos.top;
                    oldValues["width"] = cardDiv.width();
                    oldValues["height"] = cardDiv.height();

                    // Now we begin the animation
                    var gameWidth = $("#main").width();
                    var gameHeight = $("#main").height();

                    var cardHeight = (gameHeight / 2);
                    var cardWidth = card.getWidthForHeight(cardHeight);

                    $(cardDiv).css(
                        {
                            position:"absolute",
                            left:(gameWidth / 2 - cardWidth / 4),
                            top:gameHeight * (3 / 8),
                            width:cardWidth / 2,
                            height:cardHeight / 2,
                            "z-index":100,
                            opacity:0});

                    $(cardDiv).animate(
                        {
                            opacity:1},
                        {
                            duration:that.getAnimationLength(putCardIntoPlayDuration / 8),
                            easing:"linear",
                            step:function (now, fx) {
                                layoutCardElem(cardDiv,
                                    (gameWidth / 2 - cardWidth / 4) - now * (cardWidth / 4),
                                    gameHeight * (3 / 8) - now * (gameHeight / 8),
                                    cardWidth / 2 + now * (cardWidth / 2),
                                    cardHeight / 2 + now * (cardHeight / 2), 100, card);
                            },
                            complete:next});
                }).queue(
                function (next) {
                    if (zone == "TOP_OF_UNRESOLVED_DESTINY_DRAW")
                        setTimeout(next, that.getAnimationLength(putCardIntoPlayDuration));
                    else
                        setTimeout(next, that.getAnimationLength(putCardIntoPlayDuration * (5 / 8)));
                }).queue(
                function (next) {
                    var cardDiv = $(".card:cardId(" + cardId + ")");
                    var pos = cardDiv.position();

                    var startLeft = pos.left;
                    var startTop = pos.top;
                    var startWidth = cardDiv.width();
                    var startHeight = cardDiv.height();

                    $(cardDiv).animate(
                        {
                            left:oldValues["left"]},
                        {
                            duration:that.getAnimationLength(putCardIntoPlayDuration / 4),
                            easing:"linear",
                            step:function (now, fx) {
                                var state = fx.state;
                                layoutCardElem(cardDiv,
                                    startLeft + (oldValues["left"] - startLeft) * state,
                                    startTop + (oldValues["top"] - startTop) * state,
                                    startWidth + (oldValues["width"] - startWidth) * state,
                                    startHeight + (oldValues["height"] - startHeight) * state, 100);
                            },
                            complete:next});
                }).queue(
                function (next) {
                    var cardDiv = $(".card:cardId(" + cardId + ")");
                    $(cardDiv).css({zIndex:oldValues["zIndex"]});
                    next();
                });
        }
    },

    cardActivated:function (element, animate) {
        if (animate) {
            var that = this;

            var participantId = element.getAttribute("participantId");
            var cardId = element.getAttribute("cardId");

            // Play-out game event animation to both players
            $("#main").queue(
                function (next) {
                    var cardDiv = $(".card:cardId(" + cardId + ")");
                    if (cardDiv.length > 0) {
                        $(".borderOverlay", cardDiv)
                            .switchClass("borderOverlay", "highlightBorderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6))
                            .switchClass("highlightBorderOverlay", "borderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6))
                            .switchClass("borderOverlay", "highlightBorderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6))
                            .switchClass("highlightBorderOverlay", "borderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6))
                            .switchClass("borderOverlay", "highlightBorderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6))
                            .switchClass("highlightBorderOverlay", "borderOverlay", that.getAnimationLength(that.cardActivatedDuration / 6), next);
                    }
                    else {
                        next();
                    }
                });
        }
    },

    rotateCardInPlay:function (element, animate) {
        var that = this;
        var cardId = element.getAttribute("cardId");

        $("#main").queue(
            function (next) {
                var upsideDown = element.getAttribute("inverted");
                var sideways = element.getAttribute("sideways");
                var isFrozen = element.getAttribute("frozen");
                var isSuspended = element.getAttribute("suspended");
                var isCollapsed = element.getAttribute("collapsed");

                var card = $(".card:cardId(" + cardId + ")");
                var cardData = card.data("card");

                // Check if card is supposed to be upside down
                if (upsideDown=="true")
                    cardData.upsideDown = true;
                else
                    cardData.upsideDown = false;

                // Check if card is supposed to be sideways
                if (sideways=="true")
                    cardData.onSide = true;
                else
                    cardData.onSide = false;

                // Check if card is supposed to be frozen
                if (isFrozen=="true")
                    cardData.frozen = true;
                else
                    cardData.frozen = false;

                // Check if card is supposed to be suspended (or turned off)
                if (isSuspended=="true")
                    cardData.suspended = true;
                else
                    cardData.suspended = false;

                // Check if card is supposed to be collapsed
                if (isCollapsed=="true")
                    cardData.collapsed = true;
                else
                    cardData.collapsed = false;

                next();
            });

        $("#main").queue(
            function (next) {
                that.game.layoutGroupWithCard(cardId);
                next();
            });
    },

    flipCardInPlay:function (element, animate) {
        var that = this;
        var cardId = element.getAttribute("cardId");
        var sideways = element.getAttribute("sideways");
        var isCollapsed = element.getAttribute("collapsed");

        $("#main").queue(
            function (next) {
                var blueprintId = element.getAttribute("blueprintId");
                var card = $(".card:cardId(" + cardId + ")");
                var cardData = card.data("card");

                // Update blueprintId and imageUrl of card
                cardData.flipOverCard();

                // Check if card is supposed to be sideways
                if (sideways=="true")
                    cardData.onSide = true;
                else
                    cardData.onSide = false;

                // Check if card is supposed to be collapsed
                if (isCollapsed=="true")
                    cardData.collapsed = true;
                else
                    cardData.collapsed = false;

                next();
            });

        $("#main").queue(
            function (next) {
                that.game.layoutGroupWithCard(cardId);
                next();
            });
    },

    moveCardInPlay:function (element, animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                var cardId = element.getAttribute("cardId");
                var zone = element.getAttribute("zone");
                var targetCardId = element.getAttribute("targetCardId");
                var participantId = element.getAttribute("participantId");
                var zoneOwnerId = element.getAttribute("zoneOwnerId");
                var locationIndex = element.getAttribute("locationIndex");
                var isFrozen = element.getAttribute("frozen");

                if (zoneOwnerId != null)
                    participantId = zoneOwnerId;

                // Remove from where it was already attached
                $(".card").each(
                    function () {
                        var cardData = $(this).data("card");
                        var index = -1;
                        for (var i = 0; i < cardData.attachedCards.length; i++)
                            if (cardData.attachedCards[i].data("card").cardId == cardId) {
                                index = i;
                                break;
                            }
                        if (index != -1)
                            cardData.attachedCards.splice(index, 1);
                    }
                );

                var card = $(".card:cardId(" + cardId + ")");
                var cardData = card.data("card");
                // move to new zone and location index
                cardData.zone = zone;
                cardData.owner = participantId;
                cardData.locationIndex = locationIndex;

                // Check if card is supposed to be frozen
                if (isFrozen=="true")
                    cardData.frozen = true;
                else
                    cardData.frozen = false;

                if (targetCardId != null) {
                    // attach to new card if it's attached
                    var targetCardData = $(".card:cardId(" + targetCardId + ")").data("card");
                    targetCardData.attachedCards.push(card);
                }

                next();
            });

        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    removeCardFromPlay:function (element, animate) {
        var that = this;
        var cardRemovedIds = element.getAttribute("otherCardIds").split(",");
        var participantId = element.getAttribute("participantId");
        var eventType = element.getAttribute("type");

        if (animate) {
            $("#main").queue(
                function (next) {
                    var doAnimation = false;
                    if (cardRemovedIds.length > 0) {
                        var cardId = cardRemovedIds[0];
                        var card = $(".card:cardId(" + cardId + ")");
                        if (card.length > 0) {
                            var cardData = card.data("card");
                            if (cardData.zone != "LOST_PILE" && cardData.zone != "TOP_OF_LOST_PILE" && cardData.zone != "USED_PILE"
                                && cardData.zone != "TOP_OF_USED_PILE"  && cardData.zone != "FORCE_PILE" && cardData.zone != "TOP_OF_FORCE_PILE"
                                && cardData.zone != "RESERVE_DECK" && cardData.zone != "TOP_OF_RESERVE_DECK" && cardData.zone != "VOID_DESTINY_DRAWN")
                            {
                                doAnimation = true;
                            }
                        }
                    }

                    if (doAnimation) {
                        $(".card:cardId(" + cardRemovedIds + ")")
                            .animate(
                            {
                                opacity:0},
                            {
                                duration:that.getAnimationLength(that.removeCardFromPlayDuration),
                                easing:"easeOutQuart",
                                queue:false});
                        setTimeout(next, that.getAnimationLength(that.removeCardFromPlayDuration));
                    }
                    else {
                        next();
                    }
                });
        }
        $("#main").queue(
            function (next) {
                for (var i = 0; i < cardRemovedIds.length; i++) {
                    var cardId = cardRemovedIds[i];
                    var card = $(".card:cardId(" + cardId + ")");

                    if (card.length > 0) {
                        var cardData = card.data("card");
                        if (cardData.zone == "ATTACHED" || cardData.zone == "STACKED" || cardData.zone == "STACKED_FACE_DOWN") {
                            $(".card").each(
                                function () {
                                    var cardData = $(this).data("card");
                                    var index = -1;
                                    for (var i = 0; i < cardData.attachedCards.length; i++)
                                        if (cardData.attachedCards[i].data("card").cardId == cardId) {
                                            index = i;
                                            break;
                                        }
                                    if (index != -1)
                                        cardData.attachedCards.splice(index, 1);
                                }
                            );
                        }

                        card.remove();
                    }
                }

                next();
            });

        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    // Removes the location groups from the table layout. This is done after a location and all the cards at that location
    // have been removed from the table. Example: When a planet is "blown away", each of the related sites are removed
    // from the table. This removes the location div where the site existed, since there is no location there anymore.
    removeLocationFromPlay:function (element, animate) {
        var that = this;
        var locationIndexes = element.getAttribute("locationIndexes").split(",");

        $("#main").queue(
            function (next) {

                // Remove in descending order
                locationIndexes.sort(function(a, b){return b-a});
                $.each(locationIndexes, function(arrayIndex, locationIndex) {
                    that.game.removeLocationDiv(locationIndex);
                });

                that.game.layoutUI(false);
                next();
            });
    },

    gamePhaseChange:function (element, animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                var phase = element.getAttribute("phase");

                $(".phase").text(phase);

                next();
            });
    },

    turnChange:function (element, animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                var playerId = element.getAttribute("participantId");
                var playerIndex = that.game.getPlayerIndex(playerId);

                that.game.currentPlayerId = playerId;

                $(".player").each(function (index) {
                    if (index == playerIndex)
                        $(this).addClass("current");
                    else
                        $(this).removeClass("current");
                });

                next();
            });
    },

    startBattle:function (element, animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                var cardIds = element.getAttribute("otherCardIds");
                var locationIndex = element.getAttribute("locationIndex");

                that.game.battleLocationIndex = locationIndex;

                if (cardIds != null && cardIds != "")
                    $(".card:cardId(" + cardIds + ")").each(function () {
                        $(this).data("card").inBattle = true;
                    });

                that.game.darkBattlePowerDiv = $("<div class='battlePowerDiv'></div>");
                that.game.darkBattlePowerDiv.css({visibility:"hidden"});
                that.game.darkBattleNumDestinyToPowerDiv = $("<div class='battleNumDestinyDiv'></div>");
                that.game.darkBattleNumDestinyToPowerDiv.css({visibility:"hidden"});
                that.game.darkBattleNumBattleDestinyDiv = $("<div class='battleNumDestinyDiv'></div>");
                that.game.darkBattleNumBattleDestinyDiv.css({visibility:"hidden"});
                that.game.darkBattleNumDestinyToAttritionDiv = $("<div class='battleNumDestinyDiv'></div>");
                that.game.darkBattleNumDestinyToAttritionDiv.css({visibility:"hidden"});

                that.game.darkBattleDamageRemainingDiv = $("<div class='battleDamageRemainingDiv'></div>");
                that.game.darkBattleDamageRemainingDiv.css({visibility:"hidden"});
                that.game.darkBattleAttritionRemainingDiv = $("<div class='battleAttritionRemainingDiv'></div>");
                that.game.darkBattleAttritionRemainingDiv.css({visibility:"hidden"});

                that.game.lightBattlePowerDiv = $("<div class='battlePowerDiv'></div>");
                that.game.lightBattlePowerDiv.css({visibility:"hidden"});
                that.game.lightBattleNumDestinyToPowerDiv = $("<div class='battleNumDestinyDiv'></div>");
                that.game.lightBattleNumDestinyToPowerDiv.css({visibility:"hidden"});
                that.game.lightBattleNumBattleDestinyDiv = $("<div class='battleNumDestinyDiv'></div>");
                that.game.lightBattleNumBattleDestinyDiv.css({visibility:"hidden"});
                that.game.lightBattleNumDestinyToAttritionDiv = $("<div class='battleNumDestinyDiv'></div>");
                that.game.lightBattleNumDestinyToAttritionDiv.css({visibility:"hidden"});

                that.game.lightBattleDamageRemainingDiv = $("<div class='battleDamageRemainingDiv'></div>");
                that.game.lightBattleDamageRemainingDiv.css({visibility:"hidden"});
                that.game.lightBattleAttritionRemainingDiv = $("<div class='battleAttritionRemainingDiv'></div>");
                that.game.lightBattleAttritionRemainingDiv.css({visibility:"hidden"});

                that.game.battleGroupDiv = $("<div class='ui-widget-content'></div>");
                that.game.battleGroupDiv.css({"border-radius":"7px", "border-color":"#ff0000"});
                that.game.battleGroupDiv.append(that.game.darkBattlePowerDiv);
                that.game.battleGroupDiv.append(that.game.darkBattleNumDestinyToPowerDiv);
                that.game.battleGroupDiv.append(that.game.darkBattleNumBattleDestinyDiv);
                that.game.battleGroupDiv.append(that.game.darkBattleNumDestinyToAttritionDiv);
                that.game.battleGroupDiv.append(that.game.darkBattleDamageRemainingDiv);
                that.game.battleGroupDiv.append(that.game.darkBattleAttritionRemainingDiv);
                that.game.battleGroupDiv.append(that.game.lightBattlePowerDiv);
                that.game.battleGroupDiv.append(that.game.lightBattleNumDestinyToPowerDiv);
                that.game.battleGroupDiv.append(that.game.lightBattleNumBattleDestinyDiv);
                that.game.battleGroupDiv.append(that.game.lightBattleNumDestinyToAttritionDiv);
                that.game.battleGroupDiv.append(that.game.lightBattleDamageRemainingDiv);
                that.game.battleGroupDiv.append(that.game.lightBattleAttritionRemainingDiv);

                $("#main").append(that.game.battleGroupDiv);

                next();
            });
        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    addToBattle:function (element, animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                var cardId = element.getAttribute("cardId");

                $(".card:cardId(" + cardId + ")").each(function () {
                    $(this).data("card").inBattle = true;
                });

                next();
            });
        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    removeFromBattle:function (element, animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                var cardId = element.getAttribute("cardId");

                $(".card:cardId(" + cardId + ")").each(function () {
                    $(this).data("card").inBattle = false;
                });

                next();
            });
        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    endBattle:function (animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                that.game.battleGroupDiv.remove();
                that.game.battleGroupDiv = null;
                that.game.battleLocationIndex = null;

                $(".card").each(function () {
                    $(this).data("card").inBattle = false;
                });

                next();
            });
        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    startAttack:function (element, animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                var attackingCardIds = element.getAttribute("otherCardIds");
                var defendingCardIds = element.getAttribute("otherCardIds2");
                var locationIndex = element.getAttribute("locationIndex");
                var playerAttacking = element.getAttribute("playerAttacking");
                var playerDefending = element.getAttribute("playerDefending");

                that.game.attackLocationIndex = locationIndex;
                that.game.playerIdAttacking = playerAttacking;
                that.game.playerIdDefending = playerDefending;

                if (attackingCardIds != null && attackingCardIds != "")
                    $(".card:cardId(" + attackingCardIds + ")").each(function () {
                        $(this).data("card").attackingInAttack = true;
                    });
                if (defendingCardIds != null && defendingCardIds != "")
                    $(".card:cardId(" + defendingCardIds + ")").each(function () {
                        $(this).data("card").defendingInAttack = true;
                    });

                that.game.attackingPowerOrFerocityInAttackDiv = $("<div class='attackPowerOrFerocityDiv'></div>");
                that.game.attackingPowerOrFerocityInAttackDiv.css({visibility:"hidden"});
                that.game.attackingNumDestinyInAttackDiv = $("<div class='attackNumDestinyDiv'></div>");
                that.game.attackingNumDestinyInAttackDiv.css({visibility:"hidden"});
                that.game.defendingPowerOrFerocityInAttackDiv = $("<div class='attackPowerOrFerocityDiv'></div>");
                that.game.defendingPowerOrFerocityInAttackDiv.css({visibility:"hidden"});
                that.game.defendingNumDestinyInAttackDiv = $("<div class='attackNumDestinyDiv'></div>");
                that.game.defendingNumDestinyInAttackDiv.css({visibility:"hidden"});

                that.game.attackGroupDiv = $("<div class='ui-widget-content'></div>");
                that.game.attackGroupDiv.css({"border-radius":"7px", "border-color":"#ff0000"});
                that.game.attackGroupDiv.append(that.game.attackingPowerOrFerocityInAttackDiv);
                that.game.attackGroupDiv.append(that.game.attackingNumDestinyInAttackDiv);
                that.game.attackGroupDiv.append(that.game.defendingPowerOrFerocityInAttackDiv);
                that.game.attackGroupDiv.append(that.game.defendingNumDestinyInAttackDiv);

                $("#main").append(that.game.attackGroupDiv);

                next();
            });
        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    endAttack:function (animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                that.game.attackGroupDiv.remove();
                that.game.attackGroupDiv = null;
                that.game.attackLocationIndex = null;
                that.game.playerIdAttacking = null;
                that.game.playerIdDefending = null;
                that.game.attackingPowerOrFerocityInAttackDiv = null;
                that.game.attackingNumDestinyInAttackDiv = null;
                that.game.defendingPowerOrFerocityInAttackDiv = null;
                that.game.defendingNumDestinyInAttackDiv = null;

                $(".card").each(function () {
                    $(this).data("card").attackingInAttack = false;
                    $(this).data("card").defendingInAttack = false;
                });

                next();
            });
        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    startDuelOrLightsaberCombat:function (element, animate) {
        var that = this;
        var eventType = element.getAttribute("type");

        $("#main").queue(
            function (next) {
                var cardIds = element.getAttribute("otherCardIds");
                var locationIndex = element.getAttribute("locationIndex");

                that.game.duelOrLightsaberCombatLocationIndex = locationIndex;

                if (cardIds != null && cardIds != "")
                    $(".card:cardId(" + cardIds + ")").each(function () {
                        $(this).data("card").inDuelOrLightsaberCombat = true;
                    });

                that.game.darkDuelOrLightsaberCombatTotalDiv = $("<div class='duelOrLightsaberCombatTotalDiv'></div>");
                that.game.darkDuelOrLightsaberCombatTotalDiv.css({visibility:"hidden"});
                that.game.darkDuelOrLightsaberCombatNumDestinyDiv = $("<div class='duelOrLightsaberCombatNumDestinyDiv'></div>");
                that.game.darkDuelOrLightsaberCombatNumDestinyDiv.css({visibility:"hidden"});

                that.game.lightDuelOrLightsaberCombatTotalDiv = $("<div class='duelOrLightsaberCombatTotalDiv'></div>");
                that.game.lightDuelOrLightsaberCombatTotalDiv.css({visibility:"hidden"});
                that.game.lightDuelOrLightsaberCombatNumDestinyDiv = $("<div class='duelOrLightsaberCombatNumDestinyDiv'></div>");
                that.game.lightDuelOrLightsaberCombatNumDestinyDiv.css({visibility:"hidden"});

                that.game.duelOrLightsaberCombatGroupDiv = $("<div class='ui-widget-content'></div>");
                if (eventType != "SLC")
                    that.game.duelOrLightsaberCombatGroupDiv.css({"border-radius":"7px", "border-color":"#ff0000", "border-width":"4px"});
                else
                    that.game.duelOrLightsaberCombatGroupDiv.css({"border-radius":"7px", "border-color":"#7200ff", "border-width":"4px"});
                that.game.duelOrLightsaberCombatGroupDiv.append(that.game.darkDuelOrLightsaberCombatTotalDiv);
                that.game.duelOrLightsaberCombatGroupDiv.append(that.game.darkDuelOrLightsaberCombatNumDestinyDiv);
                that.game.duelOrLightsaberCombatGroupDiv.append(that.game.lightDuelOrLightsaberCombatTotalDiv);
                that.game.duelOrLightsaberCombatGroupDiv.append(that.game.lightDuelOrLightsaberCombatNumDestinyDiv);

                $("#main").append(that.game.duelOrLightsaberCombatGroupDiv);

                next();
            });
        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    endDuelOrLightsaberCombat:function (animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                that.game.duelOrLightsaberCombatGroupDiv.remove();
                that.game.duelOrLightsaberCombatGroupDiv = null;
                that.game.darkDuelOrLightsaberCombatTotalDiv = null;
                that.game.darkDuelOrLightsaberCombatNumDestinyDiv = null;
                that.game.lightDuelOrLightsaberCombatTotalDiv = null;
                that.game.lightDuelOrLightsaberCombatNumDestinyDiv = null;

                that.game.duelOrLightsaberCombatLocationIndex = null;

                $(".card").each(function () {
                    $(this).data("card").inDuelOrLightsaberCombat = false;
                });

                next();
            });
        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    startSabacc:function (animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                that.game.showSabaccHand = true;
                that.game.showRevealedSabaccHands = false;
                next();
            });
        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    revealSabaccHands:function (animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                that.game.showSabaccHand = false;
                that.game.showRevealedSabaccHands = true;
                next();
            });
        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    endSabacc:function (animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                that.game.showSabaccHand = false;
                that.game.showRevealedSabaccHands = false;
                next();
            });
        if (animate) {
            $("#main").queue(
                function (next) {
                    that.game.layoutUI(false);
                    next();
                });
        }
    },

    gameStats:function (element, animate) {
        var that = this;
        $("#main").queue(
            function (next) {

                var darkForceGeneration = element.getAttribute("darkForceGeneration");
                var lightForceGeneration = element.getAttribute("lightForceGeneration");
                var darkRaceTotal = element.getAttribute("darkRaceTotal");
                var lightRaceTotal = element.getAttribute("lightRaceTotal");

                var playerZones = element.getElementsByTagName("playerZones");
                for (var i = 0; i < playerZones.length; i++) {
                    var playerZone = playerZones[i];

                    var playerId = playerZone.getAttribute("name");
                    var hand = playerZone.getAttribute("HAND");
                    var sabaccHand = playerZone.getAttribute("SABACC_HAND");
                    var reserve = playerZone.getAttribute("RESERVE_DECK");
                    var force = playerZone.getAttribute("FORCE_PILE");
                    var used = playerZone.getAttribute("USED_PILE");
                    var lost = playerZone.getAttribute("LOST_PILE");
                    var outOfPlay = playerZone.getAttribute("OUT_OF_PLAY");

                    $("#hand" + that.game.getPlayerIndex(playerId)).text(hand);
                    if (that.game.showSabaccHand || that.game.showRevealedSabaccHands) {
                        $("#sabaccHand" + that.game.getPlayerIndex(playerId)).css({display:"table-cell"});
                    }
                    else {
                        $("#sabaccHand" + that.game.getPlayerIndex(playerId)).css({display:"none"});
                    }
                    $("#sabaccHand" + that.game.getPlayerIndex(playerId)).text(sabaccHand);
                    $("#outOfPlay" + that.game.getPlayerIndex(playerId)).text(outOfPlay);

                    if (that.game.getPlayerIndex(playerId)==0) {
                        $("#forceGeneration" + that.game.getPlayerIndex(playerId)).text(darkForceGeneration);
                        if (darkRaceTotal == null || darkRaceTotal == -1) {
                            $("#raceTotal" + that.game.getPlayerIndex(playerId)).css({display:"none"});
                        }
                        else {
                            $("#raceTotal" + that.game.getPlayerIndex(playerId)).text(darkRaceTotal);
                            $("#raceTotal" + that.game.getPlayerIndex(playerId)).css({display:"table-cell"});
                        }
                        $(".topDarkReserveDeck").text(reserve);
                        $(".topDarkForcePile").text(force);
                        $(".topDarkUsedPile").text(used);
                        $(".topDarkLostPile").text(lost);
                   }
                    else {
                        $("#forceGeneration" + that.game.getPlayerIndex(playerId)).text(lightForceGeneration);
                        if (lightRaceTotal == null || lightRaceTotal == -1) {
                            $("#raceTotal" + that.game.getPlayerIndex(playerId)).css({display:"none"});
                        }
                        else {
                            $("#raceTotal" + that.game.getPlayerIndex(playerId)).text(lightRaceTotal);
                            $("#raceTotal" + that.game.getPlayerIndex(playerId)).css({display:"table-cell"});
                        }
                        $(".topLightReserveDeck").text(reserve);
                        $(".topLightForcePile").text(force);
                        $(".topLightUsedPile").text(used);
                        $(".topLightLostPile").text(lost);
                    }
                }

                var darkPowerAtLocationsElem = element.getElementsByTagName("darkPowerAtLocations");
                if (darkPowerAtLocationsElem != null) {
                    darkPowerAtLocations = darkPowerAtLocationsElem[0];
                    for (var i = 0; i < that.game.darkPowerAtLocationDivs.length; i++) {
                        var darkPowerAtLocation = darkPowerAtLocations.getAttribute("locationIndex" + i);
                        if (darkPowerAtLocation == null || darkPowerAtLocation == -1 || that.game.battleLocationIndex == i || that.game.attackLocationIndex == i) {
                            that.game.darkPowerAtLocationDivs[i].css({visibility:"hidden"});
                        }
                        else {
                            that.game.darkPowerAtLocationDivs[i].text(darkPowerAtLocation);
                            that.game.darkPowerAtLocationDivs[i].css({visibility:"visible"});
                        }
                    }
                }

                var lightPowerAtLocationsElem = element.getElementsByTagName("lightPowerAtLocations");
                if (lightPowerAtLocationsElem != null) {
                    lightPowerAtLocations = lightPowerAtLocationsElem[0];
                    for (var i = 0; i < that.game.lightPowerAtLocationDivs.length; i++) {
                        var lightPowerAtLocation = lightPowerAtLocations.getAttribute("locationIndex" + i);
                        if (lightPowerAtLocation == null || lightPowerAtLocation == -1 || that.game.battleLocationIndex == i || that.game.attackLocationIndex == i) {
                            that.game.lightPowerAtLocationDivs[i].css({visibility:"hidden"});
                        }
                        else {
                            that.game.lightPowerAtLocationDivs[i].text(lightPowerAtLocation);
                            that.game.lightPowerAtLocationDivs[i].css({visibility:"visible"});
                        }
                    }
                }

                if (that.game.darkBattlePowerDiv != null) {
                    var totalPower = element.getAttribute("darkBattlePower");
                    if (totalPower != null) {
                        that.game.darkBattlePowerDiv.text(totalPower);
                        if (totalPower == -1 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex))
                            that.game.darkBattlePowerDiv.css({visibility:"hidden"});
                        else
                            that.game.darkBattlePowerDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.darkBattleNumDestinyToPowerDiv != null) {
                    var numDestinyToPower = element.getAttribute("darkBattleNumDestinyToPower");
                    if (numDestinyToPower != null) {
                        that.game.darkBattleNumDestinyToPowerDiv.text("+" + numDestinyToPower);
                        if (numDestinyToPower == 0 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex))
                            that.game.darkBattleNumDestinyToPowerDiv.css({visibility:"hidden"});
                        else
                            that.game.darkBattleNumDestinyToPowerDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.darkBattleNumBattleDestinyDiv != null) {
                    var numBattleDestiny = element.getAttribute("darkBattleNumBattleDestiny");
                    if (numBattleDestiny != null) {
                        that.game.darkBattleNumBattleDestinyDiv.text("+" + numBattleDestiny);
                        if (numBattleDestiny == 0 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex))
                            that.game.darkBattleNumBattleDestinyDiv.css({visibility:"hidden"});
                        else
                            that.game.darkBattleNumBattleDestinyDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.darkBattleNumDestinyToAttritionDiv != null) {
                    var numDestinyToAttrition = element.getAttribute("darkBattleNumDestinyToAttrition");
                    if (numDestinyToAttrition != null) {
                        that.game.darkBattleNumDestinyToAttritionDiv.text("+" + numDestinyToAttrition);
                        if (numDestinyToAttrition == 0 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex))
                            that.game.darkBattleNumDestinyToAttritionDiv.css({visibility:"hidden"});
                        else
                            that.game.darkBattleNumDestinyToAttritionDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.darkBattleDamageRemainingDiv != null) {
                    var damageRemaining = element.getAttribute("darkBattleDamageRemaining");
                    if (damageRemaining != null) {
                        that.game.darkBattleDamageRemainingDiv.text(damageRemaining);
                        if (damageRemaining == 0 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex))
                            that.game.darkBattleDamageRemainingDiv.css({visibility:"hidden"});
                        else
                            that.game.darkBattleDamageRemainingDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.darkBattleAttritionRemainingDiv != null) {
                    var attritionRemaining = element.getAttribute("darkBattleAttritionRemaining");
                    if (attritionRemaining != null) {

                        that.game.darkBattleAttritionRemainingDiv.text(attritionRemaining);

                        if (attritionRemaining == 0 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex)) {
                            that.game.darkBattleAttritionRemainingDiv.css({visibility:"hidden"});
                        }
                        else {
                            var immuneToAttrition = element.getAttribute("darkImmuneToRemainingAttrition");
                            if (immuneToAttrition == "true")
                                that.game.darkBattleAttritionRemainingDiv.css({"background-color": "#ffcc00"});
                            else
                                that.game.darkBattleAttritionRemainingDiv.css({"background-color": "#ff3333"});

                            that.game.darkBattleAttritionRemainingDiv.css({visibility:"visible"});
                        }
                    }
                }

                if (that.game.lightBattlePowerDiv != null) {
                    var totalPower = element.getAttribute("lightBattlePower");
                    if (totalPower != null) {
                        that.game.lightBattlePowerDiv.text(totalPower);
                        if (totalPower == -1 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex))
                            that.game.lightBattlePowerDiv.css({visibility:"hidden"});
                        else
                            that.game.lightBattlePowerDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.lightBattleNumDestinyToPowerDiv != null) {
                    var numDestinyToPower = element.getAttribute("lightBattleNumDestinyToPower");
                    if (numDestinyToPower != null) {
                        that.game.lightBattleNumDestinyToPowerDiv.text("+" + numDestinyToPower);
                        if (numDestinyToPower == 0 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex))
                            that.game.lightBattleNumDestinyToPowerDiv.css({visibility:"hidden"});
                        else
                            that.game.lightBattleNumDestinyToPowerDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.lightBattleNumBattleDestinyDiv != null) {
                    var numBattleDestiny = element.getAttribute("lightBattleNumBattleDestiny");
                    if (numBattleDestiny != null) {
                        that.game.lightBattleNumBattleDestinyDiv.text("+" + numBattleDestiny);
                        if (numBattleDestiny == 0 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex))
                            that.game.lightBattleNumBattleDestinyDiv.css({visibility:"hidden"});
                        else
                            that.game.lightBattleNumBattleDestinyDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.lightBattleNumDestinyToAttritionDiv != null) {
                    var numDestinyToAttrition = element.getAttribute("lightBattleNumDestinyToAttrition");
                    if (numDestinyToAttrition != null) {
                        that.game.lightBattleNumDestinyToAttritionDiv.text("+" + numDestinyToAttrition);
                        if (numDestinyToAttrition == 0 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex))
                            that.game.lightBattleNumDestinyToAttritionDiv.css({visibility:"hidden"});
                        else
                            that.game.lightBattleNumDestinyToAttritionDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.lightBattleDamageRemainingDiv != null) {
                    var damageRemaining = element.getAttribute("lightBattleDamageRemaining");
                    if (damageRemaining != null) {
                        that.game.lightBattleDamageRemainingDiv.text(damageRemaining);
                        if (damageRemaining == 0 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex))
                            that.game.lightBattleDamageRemainingDiv.css({visibility:"hidden"});
                        else
                            that.game.lightBattleDamageRemainingDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.lightBattleAttritionRemainingDiv != null) {
                    var attritionRemaining = element.getAttribute("lightBattleAttritionRemaining");
                    if (attritionRemaining != null) {

                        that.game.lightBattleAttritionRemainingDiv.text(attritionRemaining);

                        if (attritionRemaining == 0 || (that.game.battleLocationIndex == that.game.duelOrLightsaberCombatLocationIndex)) {
                            that.game.lightBattleAttritionRemainingDiv.css({visibility:"hidden"});
                        }
                        else {
                            var immuneToAttrition = element.getAttribute("lightImmuneToRemainingAttrition");
                            if (immuneToAttrition == "true")
                                that.game.lightBattleAttritionRemainingDiv.css({"background-color": "#ffcc00"});
                            else
                                that.game.lightBattleAttritionRemainingDiv.css({"background-color": "#ff3333"});

                            that.game.lightBattleAttritionRemainingDiv.css({visibility:"visible"});
                        }
                    }
                }

                if (that.game.darkDuelOrLightsaberCombatTotalDiv != null) {
                    var duelOrLightsaberCombatTotal = element.getAttribute("darkDuelOrLightsaberCombatTotal");
                    if (duelOrLightsaberCombatTotal != null) {
                        that.game.darkDuelOrLightsaberCombatTotalDiv.text(duelOrLightsaberCombatTotal);
                        if (duelOrLightsaberCombatTotal == -1)
                            that.game.darkDuelOrLightsaberCombatTotalDiv.css({visibility:"hidden"});
                        else
                            that.game.darkDuelOrLightsaberCombatTotalDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.darkDuelOrLightsaberCombatNumDestinyDiv != null) {
                    var numDuelOrLightsaberCombatDestiny = element.getAttribute("darkDuelOrLightsaberCombatNumDestiny");
                    if (numDuelOrLightsaberCombatDestiny != null) {
                        that.game.darkDuelOrLightsaberCombatNumDestinyDiv.text("+" + numDuelOrLightsaberCombatDestiny);
                        if (numDuelOrLightsaberCombatDestiny == 0)
                            that.game.darkDuelOrLightsaberCombatNumDestinyDiv.css({visibility:"hidden"});
                        else
                            that.game.darkDuelOrLightsaberCombatNumDestinyDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.lightDuelOrLightsaberCombatTotalDiv != null) {
                    var duelOrLightsaberCombatTotal = element.getAttribute("lightDuelOrLightsaberCombatTotal");
                    if (duelOrLightsaberCombatTotal != null) {
                        that.game.lightDuelOrLightsaberCombatTotalDiv.text(duelOrLightsaberCombatTotal);
                        if (duelOrLightsaberCombatTotal == -1)
                            that.game.lightDuelOrLightsaberCombatTotalDiv.css({visibility:"hidden"});
                        else
                            that.game.lightDuelOrLightsaberCombatTotalDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.lightDuelOrLightsaberCombatNumDestinyDiv != null) {
                    var numDuelOrLightsaberCombatDestiny = element.getAttribute("lightDuelOrLightsaberCombatNumDestiny");
                    if (numDuelOrLightsaberCombatDestiny != null) {
                        that.game.lightDuelOrLightsaberCombatNumDestinyDiv.text("+" + numDuelOrLightsaberCombatDestiny);
                        if (numDuelOrLightsaberCombatDestiny == 0)
                            that.game.lightDuelOrLightsaberCombatNumDestinyDiv.css({visibility:"hidden"});
                        else
                            that.game.lightDuelOrLightsaberCombatNumDestinyDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.attackingPowerOrFerocityInAttackDiv != null) {
                    var totalPowerOrFerocity = element.getAttribute("attackingPowerOrFerocityInAttack");
                    if (totalPowerOrFerocity != null) {
                        that.game.attackingPowerOrFerocityInAttackDiv.text(totalPowerOrFerocity);
                        if (totalPowerOrFerocity == -1)
                            that.game.attackingPowerOrFerocityInAttackDiv.css({visibility:"hidden"});
                        else
                            that.game.attackingPowerOrFerocityInAttackDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.attackingNumDestinyInAttackDiv != null) {
                    var attackingNumDestiny = element.getAttribute("attackingNumDestinyInAttack");
                    if (attackingNumDestiny != null) {
                        that.game.attackingNumDestinyInAttackDiv.text("+" + attackingNumDestiny);
                        if (attackingNumDestiny == 0)
                            that.game.attackingNumDestinyInAttackDiv.css({visibility:"hidden"});
                        else
                            that.game.attackingNumDestinyInAttackDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.defendingPowerOrFerocityInAttackDiv != null) {
                    var totalPowerOrFerocity = element.getAttribute("defendingPowerOrFerocityInAttack");
                    if (totalPowerOrFerocity != null) {
                        that.game.defendingPowerOrFerocityInAttackDiv.text(totalPowerOrFerocity);
                        if (totalPowerOrFerocity == -1)
                            that.game.defendingPowerOrFerocityInAttackDiv.css({visibility:"hidden"});
                        else
                            that.game.defendingPowerOrFerocityInAttackDiv.css({visibility:"visible"});
                    }
                }

                if (that.game.defendingNumDestinyInAttackDiv != null) {
                    var defendingNumDestiny = element.getAttribute("defendingNumDestinyInAttack");
                    if (defendingNumDestiny != null) {
                        that.game.defendingNumDestinyInAttackDiv.text("+" + defendingNumDestiny);
                        if (defendingNumDestiny == 0)
                            that.game.defendingNumDestinyInAttackDiv.css({visibility:"hidden"});
                        else
                            that.game.defendingNumDestinyInAttackDiv.css({visibility:"visible"});
                    }
                }

                var darkSabaccTotal = element.getAttribute("darkSabaccTotal");
                if (darkSabaccTotal != null) {
                    if (darkSabaccTotal == -2) {
                        that.game.darkSabaccHandTotal.text("?");
                        that.game.darkRevealedSabaccHandTotal.text("?");
                    }
                    else {
                        that.game.darkSabaccHandTotal.text(darkSabaccTotal);
                        that.game.darkRevealedSabaccHandTotal.text(darkSabaccTotal);
                    }

                    if (darkSabaccTotal != -1) {
                        that.game.darkSabaccHandTotal.css({visibility:"visible"});
                        that.game.darkRevealedSabaccHandTotal.css({visibility:"visible"});
                    }
                    else {
                        that.game.darkSabaccHandTotal.css({visibility:"hidden"});
                        that.game.darkRevealedSabaccHandTotal.css({visibility:"hidden"});
                    }
                }

                var lightSabaccTotal = element.getAttribute("lightSabaccTotal");
                if (lightSabaccTotal != null) {
                    if (lightSabaccTotal == -2) {
                        that.game.lightSabaccHandTotal.text("?");
                        that.game.lightRevealedSabaccHandTotal.text("?");
                    }
                    else {
                        that.game.lightSabaccHandTotal.text(lightSabaccTotal);
                        that.game.lightRevealedSabaccHandTotal.text(lightSabaccTotal);
                    }

                    if (lightSabaccTotal != -1) {
                        that.game.lightSabaccHandTotal.css({visibility:"visible"});
                        that.game.lightRevealedSabaccHandTotal.css({visibility:"visible"});
                    }
                    else {
                        that.game.lightSabaccHandTotal.css({visibility:"hidden"});
                        that.game.lightRevealedSabaccHandTotal.css({visibility:"hidden"});
                    }
                }

                next();
            });
    },

    message:function (element, animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                var message = element.getAttribute("message");
                if (that.game.chatBox != null)
                    that.game.chatBox.appendMessage(message, "gameMessage");

                next();
            });
    },

    warning:function (element, animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                var message = element.getAttribute("message");
                if (that.game.chatBox != null)
                    that.game.chatBox.appendMessage(message, "warningMessage");

                next();
            });
    },

    processDecision:function (decision, animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                var decisionType = decision.getAttribute("decisionType");
                if (decisionType == "EMPTY") {
                    that.game.emptyDecision(decision);
                } else if (decisionType == "INTEGER") {
                    that.game.integerDecision(decision);
                } else if (decisionType == "MULTIPLE_CHOICE") {
                    that.game.multipleChoiceDecision(decision);
                } else if (decisionType == "ARBITRARY_CARDS") {
                    that.game.arbitraryCardsDecision(decision);
                } else if (decisionType == "ACTION_CHOICE") {
                    that.game.actionChoiceDecision(decision);
                } else if (decisionType == "CARD_ACTION_CHOICE") {
                    that.game.cardActionChoiceDecision(decision);
                } else if (decisionType == "CARD_SELECTION") {
                    that.game.cardSelectionDecision(decision);
                }

                if (!animate)
                    that.game.layoutUI(false);

                next();
            });
        if (that.game.replayMode) {
            $("#main").queue(
                function (next) {
                    setTimeout(next, that.getAnimationLength(that.decisionDuration));
                });
        }
    },

    updateGameState:function (animate) {
        var that = this;
        $("#main").queue(
            function (next) {
                setTimeout(
                    function () {
                        that.game.updateGameState();
                    }, 100);

                if (!animate)
                    that.game.layoutUI(false);

                next();
            });
    },

    windowResized:function () {
        var that = this;
        $("#main").queue(
            function (next) {
                that.game.layoutUI(true);
                next();
            });
    }
});
