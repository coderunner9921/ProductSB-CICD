/*
 * Catalogix: theme bootstrap.
 * Loaded synchronously in <head> so the saved theme is applied BEFORE the first
 * paint (the old inline script ran at the end of <body> and caused a flash).
 *
 * Behaviour is unchanged from the previous version:
 *   - same localStorage key ("catalogix-theme"), values "dark" | "light"
 *   - defaults to dark when nothing valid is stored
 */
(function () {
    var root = document.documentElement;
    var theme = "dark";

    try {
        var stored = localStorage.getItem("catalogix-theme");
        if (stored === "light" || stored === "dark") {
            theme = stored;
        }
    } catch (error) {
        // Storage can be blocked (private mode); the default theme is used.
    }

    root.setAttribute("data-theme", theme);
    root.classList.add("js"); // lets CSS provide a no-JavaScript fallback
})();
