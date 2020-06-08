function log(text) {
    if (getUrlParam("log") == "true")
        console.log(text);
}