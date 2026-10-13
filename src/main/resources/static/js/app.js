/*
 * Catalogix: UI behaviour.
 * Loaded with `defer`, so the DOM is ready when this runs.
 *
 * This file only enhances the server-rendered pages. There is NO business logic
 * here: validation, persistence, search, sorting and paging all stay in Spring.
 * Everything is wired through data-* attributes (no inline event handlers).
 */
(function () {
    "use strict";

    var root = document.documentElement;

    function $(selector, scope) {
        return (scope || document).querySelector(selector);
    }

    function $$(selector, scope) {
        return Array.prototype.slice.call((scope || document).querySelectorAll(selector));
    }

    /* ---------------------------------------------------------------------
     * Theme (same key and values as before: "catalogix-theme" = dark | light)
     * ------------------------------------------------------------------- */
    var THEME_KEY = "catalogix-theme";

    function currentTheme() {
        return root.getAttribute("data-theme") === "light" ? "light" : "dark";
    }

    function syncThemeUi() {
        var label = currentTheme() === "dark" ? "Switch to light mode" : "Switch to dark mode";

        $$("[data-theme-toggle]").forEach(function (button) {
            button.setAttribute("aria-label", label);
            button.setAttribute("title", label);
        });

        // Keep the mobile browser chrome in step with the page background.
        var meta = $('meta[name="theme-color"]');
        if (meta) {
            meta.setAttribute("content", getComputedStyle(root).getPropertyValue("--bg").trim());
        }
    }

    $$("[data-theme-toggle]").forEach(function (button) {
        button.addEventListener("click", function () {
            var next = currentTheme() === "dark" ? "light" : "dark";
            root.setAttribute("data-theme", next);

            try {
                localStorage.setItem(THEME_KEY, next);
            } catch (error) {
                // The choice lasts until the page is reloaded.
            }
            syncThemeUi();
        });
    });

    syncThemeUi();

    /* ---------------------------------------------------------------------
     * Navigation drawer (tablet / mobile)
     * ------------------------------------------------------------------- */
    var sidebar = $("#sidebar");
    var scrim = $("[data-scrim]");
    var menuButton = $("[data-menu-toggle]");
    var pageShell = $("#page");
    var narrowScreen = window.matchMedia("(max-width: 1099.98px)");

    function setDrawer(open, restoreFocus) {
        if (!sidebar) {
            return;
        }

        sidebar.classList.toggle("is-open", open);
        if (scrim) {
            scrim.classList.toggle("is-visible", open);
        }
        document.body.classList.toggle("drawer-open", open);
        if (menuButton) {
            menuButton.setAttribute("aria-expanded", String(open));
        }
        if (pageShell) {
            pageShell.inert = open; // keep keyboard focus inside the open drawer
        }

        if (open) {
            var first = $(".sidebar-close", sidebar) || $("a", sidebar);
            if (first) {
                first.focus();
            }
        } else if (restoreFocus && menuButton) {
            menuButton.focus();
        }
    }

    if (sidebar && menuButton) {
        menuButton.addEventListener("click", function () {
            setDrawer(!sidebar.classList.contains("is-open"), true);
        });

        $$("[data-menu-close]").forEach(function (element) {
            element.addEventListener("click", function () {
                setDrawer(false, true);
            });
        });

        document.addEventListener("keydown", function (event) {
            if (event.key === "Escape" && sidebar.classList.contains("is-open")) {
                setDrawer(false, true);
            }
        });

        var onBreakpointChange = function (event) {
            if (!event.matches) {
                setDrawer(false, false);
            }
        };
        if (narrowScreen.addEventListener) {
            narrowScreen.addEventListener("change", onBreakpointChange);
        } else if (narrowScreen.addListener) {
            narrowScreen.addListener(onBreakpointChange); // older Safari
        }
    }

    /* ---------------------------------------------------------------------
     * Dialogs (native <dialog>)
     * ------------------------------------------------------------------- */
    function isOutside(event, dialog) {
        var rect = dialog.getBoundingClientRect();
        return event.clientX < rect.left || event.clientX > rect.right ||
               event.clientY < rect.top || event.clientY > rect.bottom;
    }

    function wireDialog(dialog) {
        var pressStartedOnBackdrop = false;

        // Close on backdrop click, but not when a drag started inside the dialog.
        dialog.addEventListener("mousedown", function (event) {
            pressStartedOnBackdrop = event.target === dialog && isOutside(event, dialog);
        });
        dialog.addEventListener("click", function (event) {
            if (pressStartedOnBackdrop && event.target === dialog && isOutside(event, dialog)) {
                dialog.close();
            }
            pressStartedOnBackdrop = false;
        });

        // Cancel / X controls. They are real links, so they still work without JS.
        $$("[data-dialog-close]", dialog).forEach(function (control) {
            control.addEventListener("click", function (event) {
                event.preventDefault();
                dialog.close();
            });
        });

        // Closing a dialog that the SERVER opened (edit mode or a failed validation)
        // returns to the clean list URL, keeping search / sort / page.
        dialog.addEventListener("close", function () {
            var url = dialog.getAttribute("data-close-url");
            if (url) {
                window.location.assign(url);
            }
        });
    }

    $$("dialog").forEach(wireDialog);

    /* Add / edit product dialog */
    var productDialog = $("#product-dialog");

    // Compare the VALUE, so an empty attribute can never open the dialog by accident.
    var opensOnLoad = !!productDialog && productDialog.getAttribute("data-open-on-load") === "true";

    function openProductDialog() {
        if (!productDialog || productDialog.open) {
            return;
        }
        productDialog.showModal();

        // After a failed validation, announce and focus the error summary.
        var errors = $(".form-errors", productDialog);
        if (errors && opensOnLoad) {
            errors.focus();
        }
    }

    $$("[data-open-product-dialog]").forEach(function (button) {
        button.addEventListener("click", openProductDialog);
    });

    if (opensOnLoad) {
        openProductDialog();
    }

    /* Delete confirmation: the row's own <form> still posts to /products/delete/{id} */
    var deleteDialog = $("#delete-dialog");

    if (deleteDialog) {
        var deleteName = $("[data-delete-name]", deleteDialog);
        var deleteConfirm = $("[data-delete-confirm]", deleteDialog);
        var pendingForm = null;

        document.addEventListener("submit", function (event) {
            var form = event.target;
            if (!(form instanceof HTMLFormElement) || !form.matches("[data-confirm-delete]")) {
                return;
            }

            event.preventDefault();
            pendingForm = form;
            deleteName.textContent = form.getAttribute("data-product-name") || "this product";
            deleteDialog.showModal();
        });

        deleteConfirm.addEventListener("click", function () {
            if (!pendingForm) {
                return;
            }
            var form = pendingForm;
            pendingForm = null;
            deleteConfirm.setAttribute("aria-busy", "true");
            form.submit(); // native submit: no extra request, CSRF fields (if added later) are included
        });

        deleteDialog.addEventListener("close", function () {
            pendingForm = null;
        });
    }

    /* ---------------------------------------------------------------------
     * Forms
     * ------------------------------------------------------------------- */

    // Selects with data-autosubmit re-run the search immediately (page size, sort).
    $$("select[data-autosubmit]").forEach(function (select) {
        select.addEventListener("change", function () {
            var form = select.form;
            if (!form) {
                return;
            }
            if (typeof form.requestSubmit === "function") {
                form.requestSubmit();
            } else {
                form.submit();
            }
        });
    });

    // Live character counter for inputs with data-counter + a [data-counter-for] output.
    $$("input[data-counter]").forEach(function (input) {
        var output = $('[data-counter-for="' + input.id + '"]');
        if (!output || input.maxLength < 0) {
            return;
        }
        var update = function () {
            output.textContent = input.value.length + " / " + input.maxLength;
        };
        output.hidden = false;
        update();
        input.addEventListener("input", update);
    });

    // Prevent double submits (e.g. a double-click creating two products).
    document.addEventListener("submit", function (event) {
        var form = event.target;
        if (event.defaultPrevented || !(form instanceof HTMLFormElement) ||
            !form.matches("[data-busy-submit]")) {
            return;
        }

        if (form.getAttribute("data-submitting") === "true") {
            event.preventDefault();
            return;
        }
        form.setAttribute("data-submitting", "true");

        var button = $('button[type="submit"]', form);
        if (button) {
            setTimeout(function () {
                button.setAttribute("aria-busy", "true");
            }, 0);
        }
    });

    // Back/forward cache can restore the page in its "busy" state; reset it.
    window.addEventListener("pageshow", function (event) {
        if (!event.persisted) {
            return;
        }
        $$('[aria-busy="true"]').forEach(function (element) {
            element.removeAttribute("aria-busy");
        });
        $$('[data-submitting="true"]').forEach(function (form) {
            form.removeAttribute("data-submitting");
        });
    });

    /* ---------------------------------------------------------------------
     * Notifications (server renders them from ?success= / ?error=)
     * ------------------------------------------------------------------- */
    function dismissToast(toast) {
        if (!toast || toast.classList.contains("is-leaving")) {
            return;
        }
        toast.classList.add("is-leaving");
        setTimeout(function () {
            toast.remove();
        }, 220);
    }

    $$("[data-toast]").forEach(function (toast) {
        var delay = Number(toast.getAttribute("data-timeout")) || 0; // 0 = stay until dismissed
        var timer = null;

        function start() {
            if (delay > 0) {
                timer = setTimeout(function () { dismissToast(toast); }, delay);
            }
        }
        function stop() {
            clearTimeout(timer);
        }

        toast.addEventListener("mouseenter", stop);
        toast.addEventListener("focusin", stop);
        toast.addEventListener("mouseleave", start);
        start();
    });

    $$("[data-toast-close]").forEach(function (button) {
        button.addEventListener("click", function () {
            dismissToast(button.closest("[data-toast]"));
        });
    });

    // Remove ?success= / ?error= from the address bar so a refresh doesn't repeat the message.
    try {
        var url = new URL(window.location.href);
        var changed = false;
        ["success", "error"].forEach(function (key) {
            if (url.searchParams.has(key)) {
                url.searchParams.delete(key);
                changed = true;
            }
        });
        if (changed) {
            window.history.replaceState(window.history.state, "", url.pathname + url.search + url.hash);
        }
    } catch (error) {
        // Non-critical.
    }
})();
