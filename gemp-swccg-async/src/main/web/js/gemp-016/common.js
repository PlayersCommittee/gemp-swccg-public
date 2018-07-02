var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

function formatToTwoDigits(no) {
    if (no < 10)
        return "0" + no;
    else
        return no;
}

function formatDate(date) {
    return monthNames[date.getMonth()] + " " + date.getDate() + " " + formatToTwoDigits(date.getHours()) + ":" + formatToTwoDigits(date.getMinutes()) + ":" + formatToTwoDigits(date.getSeconds());
}

function formatPrice(price) {
    var silver = (price % 100);
    return Math.floor(price / 100) + "<img src='images/gold.png'/> " + ((silver < 10) ? ("0" + silver) : silver) + "<img src='images/silver.png'/>";
}

function getDateString(date) {
    return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
}

function iterObj(obj, func) {
    for (var key in obj) {
        if (obj.hasOwnProperty(key)) {
            func(key, obj[key]);
        }
    }
}

function getUrlParam(param) {
    var search = window.location.search.substring(1);
    if (search.indexOf('&') > -1) {
        var params = search.split('&');
        for (var i = 0; i < params.length; i++) {
            var key_value = params[i].split('=');
            if (key_value[0] == param) return key_value[1];
        }
    } else {
        var params = search.split('=');
        if (params[0] == param) return params[1];
    }
    return null;
}

function getMapSize(map) {
    var size = 0, key;
    for (key in map)
        if (map.hasOwnProperty(key)) size++;
    return size;
}
